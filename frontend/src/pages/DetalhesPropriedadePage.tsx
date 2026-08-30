import { useEffect, useState } from "react"
import { buscarPropriedadePorId, type Propriedade } from "../api/propriedades"
import { useNavigate, useParams } from "react-router-dom";
import "../styles/detalhes-propriedade.css";

export function DetalhesPropriedadePage() {
    const [propriedade, setPropriedade] = useState<Propriedade | null>(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState<string | null>(null);
    const [tentativa, setTentativa] = useState(0);
    
    const { id } = useParams();
    const navigate = useNavigate();
    const propriedadeId = Number(id);
    const idValido = Number.isInteger(propriedadeId) && propriedadeId > 0;

    useEffect(() => {
        let componenteAtivo = true;

        if (!idValido) {
            return;
        }

        buscarPropriedadePorId(propriedadeId).then((propriedadeRecebida) => {
            if (componenteAtivo) {
                setErro(null);
                setPropriedade(propriedadeRecebida);
            }
        })
        .catch((erroRecebido) => {
            if (componenteAtivo) {
                setErro(
                    erroRecebido instanceof Error
                        ? erroRecebido.message
                        : "Não foi possivel carregar a propriedade."
                );
            }
        })
        .finally(() => {
            if (componenteAtivo) {
                setCarregando(false);
            }
        });

        return () => {
            componenteAtivo = false;
        };
    }, [idValido, propriedadeId, tentativa]);

    function tentarNovamente() {
        setCarregando(true);
        setErro(null);
        setTentativa((valor) => valor + 1);
    }

    if (!idValido) {
        return (
            <main className="property-details-page">
                <div
                    className="property-details-feedback property-details-feedback--error"
                    role="alert"
                >
                    <h1>Identificador inválido</h1>
                    <p>Não foi possível identificar a propriedade.</p>

                    <button type="button" onClick={() => navigate("/propriedades")}>
                        Voltar
                    </button>
                </div>
            </main>
        );
    }

    if (carregando) {
        return (
            <main className="property-details-page">
                <div className="property-details-feedback" role="status">
                    Carregando propriedade...
                </div>
            </main>
        );
    }

    if (erro || !propriedade) {
        return (
            <main className="property-details-page">
                <div className="property-details-feedback property-details-feedback--error" role="alert">
                    <h1>Não foi possível abrir propriedade</h1>

                    <p>{erro ?? "Propriedade não encontrada."}</p>

                    <div className="property-details-feedback-actions">
                        <button type="button" onClick={() => navigate("/propriedades")}>
                            Voltar
                        </button>

                        <button type="button" onClick={tentarNovamente}>
                            Tentar novamente
                        </button>
                    </div>
                </div>
            </main>
        );
    }

    const areaFormatada = new Intl.NumberFormat("pt-BR", {
        maximumFractionDigits: 2,
    }).format(propriedade.areaTotalHectares);

    return (
        <main className="property-details-page">
            <button className="property-details-back" type="button" onClick={() => navigate("/propriedades")}>
                <span aria-hidden="true">←</span>
                Voltar para propriedades
            </button>

            <header className="property-details-hero">
                <div className="property-details-identity">
                    <div className="property-details-avatar" aria-hidden="true">
                        {propriedade.nome.charAt(0).toLocaleUpperCase()}
                    </div>

                    <div>
                        <span className="property-details-eyebrow">
                            Propriedade rural
                        </span>

                        <h1>{propriedade.nome}</h1>

                        <p>
                            {propriedade.municipio}/{propriedade.estado}
                        </p>
                    </div>
                </div>

                <div className="property-details-actions">
                    <span
                        className={
                            propriedade.ativo
                                ? "property-details-status property-details-status--active"
                                : "property-details-status property-details-status--inactive"
                        }
                    >
                        {propriedade.ativo ? "Ativa" : "Inativa"}
                    </span>

                    <button
                        className="property-details-edit"
                        type="button"
                        onClick={() =>
                            navigate(`/propriedades/${propriedade.id}/editar`)
                        }
                    >
                        Editar propriedade
                    </button>
                </div>
            </header>

            <section className="property-details-grid" aria-label="Informações da propriedade">
                <article className="property-details-card">
                    <span className="property-details-card-label">
                        Área total
                    </span>

                    <strong>
                        {areaFormatada}
                        <small>Hectares</small>
                    </strong>

                    <p>Área cadastrada para esta propriedade.</p>
                </article>

                <article className="property-details-card">
                    <span className="property-details-card-label">
                        Município
                    </span>

                    <strong>{propriedade.municipio}</strong>

                    <p>Localização municipal da propriedade.</p>
                </article>

                <article className="property-details-card">
                    <span className="property-details-card-label">
                        Estado
                    </span>

                    <strong>{propriedade.estado}</strong>

                    <p>Unidade federativa cadastrada.</p>
                </article>
            </section>

            <section className="property-details-content">
                <header>
                    <div>
                        <h2>Visão operacional</h2>

                        <p>
                            Os módulos à propriedade serão apresentados nesta área.
                        </p>
                    </div>
                </header>

                <div className="property-details-empty">
                    <div aria-hidden="true">🌱</div>

                    <h3>Fundação da propriedade concluída</h3>

                    <p>Talhões, safras, e atividades agrícolas serão adicionados conforma a evolução da V0.1.</p>
                </div>
            </section>
        </main>
    );
}
