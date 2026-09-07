import { useEffect, useState } from "react";
import { listarDepositos, type Deposito } from "../api/depositos";
import { buscarPropriedadePorId, type Propriedade } from "../api/propriedades";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import "../styles/depositos.css";

export function DepositosPage() {
    
    const [depositos, setDepositos] = useState<Deposito[]>([]);
    const [propriedade, setPropriedade] = useState<Propriedade | null>(null);

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState<string | null>(null);
    const [tentativa, setTentativa] = useState(0);

    const { propriedadeId = "" } = useParams();
    const navigate = useNavigate();
    const location = useLocation();

    const idValido = propriedadeId.length > 0;

    const mensagem = (
        location.state as { mensagem?: string } | null
    )?.mensagem;

    useEffect(() => {
        let paginaAtiva = true;

        if (!idValido) {
            return;
        }

        Promise.all([
            buscarPropriedadePorId(propriedadeId),
            listarDepositos(propriedadeId)
        ])
            .then(([
                propriedadeRecebida,
                depositosRecebidos
            ]) => {
                if (paginaAtiva) {
                    setPropriedade(
                        propriedadeRecebida
                    );
                    setDepositos(depositosRecebidos);
                    setErro(null);
                }
            },)
            .catch ((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(
                        erroRecebido instanceof Error
                            ? erroRecebido.message
                            : "Não foi possivel carregar os depósitos."
                    );
                }
            })
            .finally(() => {
                if (paginaAtiva) {
                    setCarregando(false);
                }
            });
        
        return () => {
            paginaAtiva = false;
        };
    }, [idValido, propriedadeId, tentativa]);

    function tentarNovamente() {
        setCarregando(true);
        setErro(null);
        setTentativa((valor) => valor + 1);
    }

    const depositosAtivos = depositos.filter(
        (deposito) => deposito.ativo
    ).length;

    if (!idValido) {
        return (
            <main className="depositos-page">
                <div className="page-feedback page-feedback--error" role="alert">
                    Não foi possível identificar a propriedade.
                </div>
            </main>
        );
    }

    return (
        <main className="depositos-page">
            <button type="button" className="property-details-back" onClick={() => navigate(`/propriedades/${propriedadeId}`)}>
                <span aria-hidden="true">←</span>
                Voltar para a propriedade
            </button>

            <header className="page-header">
                <div>
                    <span className="page-eyebrow">
                        Insumos e estoque
                    </span>

                    <h1>
                        {propriedade?.nome
                            ? `Depósitos — ${propriedade.nome}`
                            : "Depósitos"
                        }
                    </h1>

                    <p>
                        Gerencie os locais de armazenamento da propriedade.
                    </p>
                </div>

                <button type="button" className="primary-button" onClick={() => navigate(`/propriedades/${propriedadeId}/depositos/novo`)}>
                        <span aria-hidden="true">+</span>
                        Novo depósito
                </button>
            </header>

            {mensagem && (
                <p className="page-message page-message--success" role="status">
                    {mensagem}
                </p>
            )}

            {!carregando && !erro && (
                <section className="depositos-summary" aria-label="Resumo dos depósitos">
                    <article>
                        <span>Total de depósitos</span>
                        <strong>{depositos.length}</strong>
                    </article>
                    <article>
                        <span>Depósitos ativos</span>
                        <strong>{depositosAtivos}</strong>
                    </article>
                    <article>
                        <span>Depósitos inativos</span>
                        <strong>{depositos.length - depositosAtivos}</strong>
                    </article>
                </section>
            )}

            <section className="depositos-section">
                <header className="depositos-section-header">
                    <div>
                        <h2>Locais de armazenamento</h2>

                        <p>Selecione um depósito para consultar suas movimentações.</p>
                    </div>

                    {!carregando && !erro && (
                        <span className="property-count">
                            {depositos.length}{" "}
                            {depositos.length === 1
                                ? "depósito"
                                : "depósitos"
                            }
                        </span>
                    )}
                </header>

                {carregando && (
                    <div className="page-feedback" role="status">
                        Carregando depósitos...
                    </div>
                )}

                {erro && (
                    <div className="page-feedback page-feedback--error" role="alert">
                        <p>{erro}</p>
                        <button type="button" onClick={tentarNovamente}>Tentar novamente</button>
                    </div>
                )}

                {!carregando &&
                    !erro && 
                    depositos.length === 0 && (
                        <div className="empty-state">
                            <div className="empty-state-icon" aria-hidden="true">
                                +
                            </div>

                            <h2>Nenhum depósito cadastrado</h2>

                            <p>
                                Cadastre o primeiro local de armazenamento da propriedade.
                            </p>

                            <button type="button" className="primary-button" onClick={() => navigate(`/propriedades/${propriedadeId}/depositos/novo`)}>
                                Cadastrar depósito
                            </button>
                        </div>
                    )}

                    {!carregando &&
                        !erro &&
                        depositos.length > 0 && (
                            <ul className="depositos-grid">
                                {depositos.map((deposito) => (
                                    <li className="deposito-card" key={deposito.id}>
                                        <header>
                                            <div className="deposito-card-icon" aria-hidden="true">
                                                {deposito.nome.charAt(0).toUpperCase()}
                                            </div>

                                            <span className={deposito.ativo
                                                ? "property-status property-status--active"
                                                : "property-status property-status--inactive"
                                            }>
                                                {deposito.ativo ? "Ativo" : "Inativo"}
                                            </span>
                                        </header>

                                        <h3>{deposito.nome}</h3>

                                        <button type="button" className="deposito-card-action" onClick={() => navigate(`/propriedades/${propriedadeId}/depositos/${deposito.id}/movimentacoes`)}>
                                            Ver movimentações
                                            <span aria-hidden="true">→</span>
                                        </button>
                                        <button type="button" className="deposito-card-action deposito-card-action--edit" onClick={() => navigate(`/propriedades/${propriedadeId}/depositos/${deposito.id}/editar`)}>
                                            Editar depósito
                                            <span aria-hidden="true">→</span>
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        )}
            </section>
        </main>
    );
}
