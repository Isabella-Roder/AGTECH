import { useEffect, useState } from "react"
import { buscarPropriedadePorId, type Propriedade } from "../api/propriedades"
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {
    ativarTalhao,
    desativarTalhao,
    listarTalhoes,
    type Talhao,
} from "../api/talhoes";
import "../styles/detalhes-propriedade.css";

export function DetalhesPropriedadePage() {
    const [propriedade, setPropriedade] = useState<Propriedade | null>(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState<string | null>(null);
    const [tentativa, setTentativa] = useState(0);

    const [talhoes, setTalhoes] = useState<Talhao[]>([]);
    const [talhaoEmAlteracao, setTalhaoEmAlteracao] = useState<string | null>(null);
    const [mensagemStatus, setMensagemStatus] = useState<string | null>(null);
    const [erroStatus, setErroStatus] = useState<string | null>(null);
    
    const { id } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const propriedadeId = id ?? "";
    const idValido = propriedadeId.length > 0;
    const mensagem = (
        location.state as { mensagem?: string } | null
    )?.mensagem;

    useEffect(() => {
        let componenteAtivo = true;

        if (!idValido) {
            return;
        }

        Promise.all([
            buscarPropriedadePorId(propriedadeId),
            listarTalhoes(propriedadeId),
        ])
            .then(([propriedadeRecebida, talhoesRecebidos]) => {
                if (componenteAtivo) {
                    setErro(null);
                    setPropriedade(propriedadeRecebida);
                    setTalhoes(talhoesRecebidos);
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

    async function alterarStatusTalhao(talhao: Talhao) {
        const acao = talhao.ativo ? "desativar" : "ativar";
        const confirmado = window.confirm(
            `Deseja realmente ${acao} o talhão “${talhao.nome}”?`,
        );

        if (!confirmado) {
            return;
        }

        setTalhaoEmAlteracao(talhao.id);
        setMensagemStatus(null);
        setErroStatus(null);

        try {
            const talhaoAtualizado = talhao.ativo
                ? await desativarTalhao(propriedadeId, talhao.id)
                : await ativarTalhao(propriedadeId, talhao.id);

            setTalhoes((atuais) =>
                atuais.map((item) =>
                    item.id === talhaoAtualizado.id ? talhaoAtualizado : item,
                ),
            );
            setMensagemStatus(
                `Talhão ${talhaoAtualizado.ativo ? "ativado" : "desativado"} com sucesso.`,
            );
        } catch (erroRecebido) {
            setErroStatus(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : `Não foi possível ${acao} o talhão.`,
            );
        } finally {
            setTalhaoEmAlteracao(null);
        }
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

    const talhoesAtivos = talhoes.filter(
        (talhao) => talhao.ativo,
    ).length;

    const areaDosTalhoes = talhoes.reduce(
        (total, talhao) => total + talhao.areaHectares,
        0,
    );

    const areaDosTalhoesFormatada = new Intl.NumberFormat("pt-BR", {
        maximumFractionDigits: 2,
    }).format(areaDosTalhoes)

    return (
        <main className="property-details-page">
            <button className="property-details-back" type="button" onClick={() => navigate("/propriedades")}>
                <span aria-hidden="true">←</span>
                Voltar para propriedades
            </button>

            {mensagem && (
                <p className="property-details-message" role="status">
                    {mensagem}
                </p>
            )}

            {mensagemStatus && (
                <p className="property-details-message" role="status">
                    {mensagemStatus}
                </p>
            )}

            {erroStatus && (
                <p className="property-details-message property-details-message--error" role="alert">
                    {erroStatus}
                </p>
            )}

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
                <header className="talhoes-section-header">
                    <div>
                        <span className="property-details-eyebrow">
                            Organização produtiva
                        </span>
                        <h2>Talhões</h2>

                        <p>
                            Áreas produtivas vinculadas a esta propriedade.
                        </p>
                    </div>

                    <button type="button" className="primary-button" onClick={() => 
                        navigate(`/propriedades/${propriedade.id}/talhoes/novo`)
                    }>
                        <span aria-hidden="true">+</span>
                        Novo talhão
                    </button>

                    <div className="talhoes-summary">
                        <span>
                            <strong>{talhoes.length}</strong>
                            total
                        </span>

                        <span>
                            <strong>{talhoesAtivos}</strong>
                            ativos
                        </span>

                        <span>
                            <strong>{areaDosTalhoesFormatada} ha</strong>
                            cadastrados
                        </span>
                    </div>
                </header>

                {talhoes.length === 0 ? (
                    <div className="property-details-empty">
                        <div aria-hidden="true">⌗</div>

                        <h3>Nenhum talhão cadastrado</h3>

                        <p>
                            Divida a propriedade em área produtivas para 
                            organizar safras e atividades agrícolas
                        </p>
                    </div>
                ): (
                    <ul className="talhoes-grid">
                        {talhoes.map((talhao) => (
                            <li className="talhao-card" key={talhao.id}>
                                <header>
                                    <div className="talhao-card-mark" aria-hidden="true">
                                        {talhao.nome.charAt(0).toUpperCase()}
                                    </div>

                                    <span className={talhao.ativo ? "property-status property-status--active" : "property-status property-status--inactive"}>
                                        {talhao.ativo ? "Ativo" : "Inativo"}
                                    </span>
                                </header>

                                <h3>{talhao.nome}</h3>

                                <div className="talhao-card-area">
                                    <span>Área produtiva</span>

                                    <strong>
                                        {new Intl.NumberFormat("pt-BR", {
                                            maximumFractionDigits: 2,
                                        }).format(talhao.areaHectares)}
                                        <small> ha</small>
                                    </strong>
                                </div>

                                <div className="talhao-card-actions">
                                    <button
                                        className="talhao-card-edit"
                                        type="button"
                                        onClick={() => navigate(`/propriedades/${propriedade.id}/talhoes/${talhao.id}/safras`)}
                                    >
                                        Ver safras
                                    </button>
                                    <button
                                        className="talhao-card-edit"
                                        type="button"
                                        onClick={() =>
                                            navigate(
                                                `/propriedades/${propriedade.id}/talhoes/${talhao.id}/editar`,
                                            )
                                        }
                                    >
                                        Editar
                                    </button>

                                    <button
                                        className={
                                            talhao.ativo
                                                ? "talhao-card-status talhao-card-status--deactivate"
                                                : "talhao-card-status talhao-card-status--activate"
                                        }
                                        type="button"
                                        disabled={talhaoEmAlteracao === talhao.id}
                                        onClick={() => alterarStatusTalhao(talhao)}
                                    >
                                        {talhaoEmAlteracao === talhao.id
                                            ? "Alterando..."
                                            : talhao.ativo
                                              ? "Desativar"
                                              : "Ativar"}
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </main>
    );
}
