import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { listarCulturas, type Cultura } from "../api/cultura";
import { listarSafras, type Safra } from "../api/safra";
import "../styles/safras.css";

const rotulosStatus: Record<Safra["status"], string> = {
    PLANEJADA: "Planejada",
    EM_ANDAMENTO: "Em andamento",
    FINALIZADA: "Finalizada",
    CANCELADA: "Cancelada",
};

export function SafraPage() {
    const [safras, setSafras] = useState<Safra[]>([]);
    const [culturas, setCulturas] = useState<Cultura[]>([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState<string | null>(null);
    const [tentativa, setTentativa] = useState(0);
    const { propriedadeId = "", talhaoId = "" } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const idsValidos = Boolean(propriedadeId && talhaoId);
    const mensagem = (location.state as { mensagem?: string } | null)?.mensagem;

    useEffect(() => {
        let paginaAtiva = true;

        if (!idsValidos) {
            return;
        }

        Promise.all([listarSafras(propriedadeId, talhaoId), listarCulturas()])
            .then(([safrasRecebidas, culturasRecebidas]) => {
                if (paginaAtiva) {
                    setSafras(safrasRecebidas);
                    setCulturas(culturasRecebidas);
                    setErro(null);
                }
            })
            .catch((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(erroRecebido instanceof Error
                        ? erroRecebido.message
                        : "Não foi possível carregar as safras.");
                }
            })
            .finally(() => {
                if (paginaAtiva) setCarregando(false);
            });

        return () => { paginaAtiva = false; };
    }, [idsValidos, propriedadeId, talhaoId, tentativa]);

    function nomeDaCultura(culturaId: string) {
        return culturas.find((cultura) => cultura.id === culturaId)?.nome
            ?? "Cultura não encontrada";
    }

    function formatarData(data: string | null) {
        if (!data) return "Não informada";
        return new Intl.DateTimeFormat("pt-BR", { timeZone: "UTC" })
            .format(new Date(`${data}T00:00:00Z`));
    }

    const caminhoNovaSafra = `/propriedades/${propriedadeId}/talhoes/${talhaoId}/safras/nova`;

    if (!idsValidos) {
        return (
            <main className="safras-page">
                <div className="page-feedback page-feedback--error" role="alert">
                    Não foi possível identificar o talhão.
                </div>
            </main>
        );
    }

    return (
        <main className="safras-page">
            <button className="property-details-back" type="button" onClick={() => navigate(`/propriedades/${propriedadeId}`)}>
                <span aria-hidden="true">←</span> Voltar para a propriedade
            </button>

            <header className="page-header">
                <div>
                    <span className="page-eyebrow">Planejamento agrícola</span>
                    <h1>Safras do talhão</h1>
                    <p>Acompanhe os ciclos produtivos vinculados a este talhão.</p>
                </div>
                <button className="primary-button" type="button" onClick={() => navigate(caminhoNovaSafra)} disabled={!idsValidos}>
                    <span aria-hidden="true">+</span> Nova safra
                </button>
            </header>

            {mensagem && <p className="page-message page-message--success" role="status">{mensagem}</p>}

            <section className="safras-section">
                {carregando && <div className="page-feedback" role="status">Carregando safras...</div>}

                {erro && (
                    <div className="page-feedback page-feedback--error" role="alert">
                        <p>{erro}</p>
                        {idsValidos && <button type="button" onClick={() => {
                            setCarregando(true);
                            setErro(null);
                            setTentativa((valor) => valor + 1);
                        }}>Tentar novamente</button>}
                    </div>
                )}

                {!carregando && !erro && safras.length === 0 && (
                    <div className="empty-state">
                        <div className="empty-state-icon" aria-hidden="true">+</div>
                        <h2>Nenhuma safra cadastrada</h2>
                        <p>Cadastre a primeira safra deste talhão para iniciar o planejamento.</p>
                        <button className="primary-button" type="button" onClick={() => navigate(caminhoNovaSafra)}>Cadastrar safra</button>
                    </div>
                )}

                {!carregando && !erro && safras.length > 0 && (
                    <ul className="safras-grid">
                        {safras.map((safra) => (
                            <li className="safra-card" key={safra.id}>
                                <header>
                                    <span className={`safra-status safra-status--${safra.status.toLowerCase()}`}>
                                        {rotulosStatus[safra.status]}
                                    </span>
                                </header>
                                <h2>{safra.nome}</h2>
                                <dl>
                                    <div><dt>Cultura</dt><dd>{nomeDaCultura(safra.culturaId)}</dd></div>
                                    <div><dt>Início</dt><dd>{formatarData(safra.dataInicio)}</dd></div>
                                    <div><dt>Fim previsto</dt><dd>{formatarData(safra.dataFimPrevisto)}</dd></div>
                                </dl>
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </main>
    );
}
