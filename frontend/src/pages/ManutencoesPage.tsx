import { Link, useParams } from "react-router-dom";
import { listarManutencoes, rotulosTipoManutencao, type Manutencao, type TipoManutencao } from "../api/manutencoes"
import { useEffect, useMemo, useState } from "react";
import { buscarMaquinaPorId, type Maquina } from "../api/maquinas";
import "../styles/manutencoes.css";

type FiltroTipo = "TODAS" | TipoManutencao;

function formatarData(data: string): string {
    const [ano, mes, dia] = data.split("-");

    if (!ano || !mes || !dia) {
        return data;
    }

    return `${dia}/${mes}/${ano}`;
}

export function ManutencoesPage() {

    const { propriedadeId, maquinaId } = useParams<{propriedadeId: string; maquinaId: string;}>();

    const [maquina, setMaquina] = useState<Maquina | null>(null);
    const [manutencoes, setManutencoes] = useState<Manutencao[]>([]);
    const [filtroTipo, setFiltroTipo] = useState<FiltroTipo>("TODAS");
    const [busca, setBusca] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    useEffect(() => {
        if (!propriedadeId || !maquinaId) {
            setErro("Máquina não identificada.");
            setCarregando(false);
            return;
        }

        async function carregarDados() {
            try {
                setCarregando(true);
                setErro("");

                const [maquinaResposta, manutencoesResposta] = await Promise.all([
                    buscarMaquinaPorId(propriedadeId!, maquinaId!),
                    listarManutencoes(propriedadeId!, maquinaId!)
                ]);

                const ordenadas = [...manutencoesResposta].sort((a, b) =>
                    b.dataRealizacao.localeCompare(a.dataRealizacao)
                );

                setMaquina(maquinaResposta);
                setManutencoes(ordenadas);
            } catch (error) {
                setErro(
                    error instanceof Error
                        ? error.message
                        : "Não foi possivel carregar as manutenções."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregarDados();
    }, [propriedadeId, maquinaId]);

    const manutencoesFiltradas = useMemo(() => {
        const termo = busca.trim().toLowerCase();

        return manutencoes.filter((manutencao) => {
            const correspondeTipo =
                filtroTipo === "TODAS" ||
                manutencao.tipo === filtroTipo;


            const correspondeBusca =
                termo.length === 0 ||
                manutencao.descricao?.toLowerCase().includes(termo) ||
                rotulosTipoManutencao[manutencao.tipo].toLowerCase().includes(termo);

            return correspondeTipo && correspondeBusca;
        });
    }, [manutencoes, filtroTipo, busca]);

    const totalPreventivas = manutencoes.filter(
        (manutencao) => manutencao.tipo === "PREVENTIVA"
    ).length;

    const totalCorretivas = manutencoes.filter(
        (manutencao) => manutencao.tipo === "CORRETIVA"
    ).length;

    if (!propriedadeId || !maquinaId) {
        return (
            <main className="manutencoes-page">
                <div className="manutencoes-alert manutencoes-alert--erro">
                    Máquina não identificada.
                </div>
            </main>
        );
    }

    return (
        <main className="manutencoes-page">
            <section className="manutencoes-hero">
                <div>
                    <span className="manutencoes-eyebrow">
                        Histórico de manutenção
                    </span>

                    <h1>Manutenção</h1>

                    <p>
                        {maquina
                            ? `Acompanhe os serviços realizado em ${maquina.identificador}.`
                            : "Acompanhe os serviços realizado no equipamento."
                        }
                    </p>
                </div>

                <div className="manutencoes-hero__actions">
                    <Link className="manutencoes-button manutencoes-button--ghost" to={`/propriedades/${propriedadeId}/maquinas/${maquinaId}`}>
                        Voltar
                    </Link>

                    <Link className="manutencoes-button manutencoes-button--primary" to={`/propriedades/${propriedadeId}/maquinas/${maquinaId}/manutencoes/nova`}>
                        + Nova manutenção
                    </Link>
                </div>
            </section>

            <section className="manutencoes-summary">
                <article className="manutencoes-summary-card">
                    <span>Total registrado</span>
                    <strong>{manutencoes.length}</strong>
                    <small>serviços do histórico</small>
                </article>

                <article className="manutencoes-summary-card manutencoes-summary-card--preventiva">
                    <span>Preventivas</span>
                    <strong>{totalPreventivas}</strong>
                    <small>ações de prevenção</small>
                </article>

                <article className="manutencoes-summary-card manutencoes-summary-card--corretiva">
                    <span>Corretivas</span>
                    <strong>{totalCorretivas}</strong>
                    <small>correções realizadas</small>
                </article>
            </section>

            <section className="manutencoes-content">
                <div className="manutencoes-toolbar">
                    <label>
                        <span>Buscar</span>

                        <input type="search" value={busca} placeholder="Descrição ou tipo..." onChange={(e) => setBusca(e.target.value)} />
                    </label>

                    <label>
                        <span>Tipo</span>

                        <select value={filtroTipo} onChange={(e) => setFiltroTipo(e.target.value as FiltroTipo)}>
                            <option value="TODAS">Todas</option>
                            <option value="PREVENTIVA">Preventiva</option>
                            <option value="CORRETIVA">Corretiva</option>
                        </select>
                    </label>
                </div>

                {erro && (
                    <div className="manutencoes-alert manutencoes-alert--erro">
                        {erro}
                    </div>
                )}

                {carregando ? (
                    <div className="manutencoes-state">
                        <div className="manutencoes-spinner"/>
                        <p>Carregando manutenções...</p>
                    </div>
                ) : manutencoesFiltradas.length === 0 ? (
                    <div className="manutencoes-state">
                        <div className="manutencoes-empty-icon">

                            <h2>Nenhuma manutenção encontrada</h2>

                            <p>
                                {manutencoes.length === 0
                                    ? "Registre a primeira manutenção desta máquina."
                                    : "Tente alterar os filtros da pesquisa."
                                }
                            </p>

                        </div>
                    </div>
                ) : (
                    <div className="manutencoes-list">
                        {manutencoesFiltradas.map((manutencao) => (
                            <article key={manutencao.id} className="manutencao-card">
                                <div className={`manutencao-card__marker--${manutencao.tipo.toLowerCase()}`}/>

                                <div className="manutencao-card__main">
                                    <header>
                                        <span className={`manutencao-badge manutencao-badge--${manutencao.tipo.toLowerCase()}`}>
                                            {rotulosTipoManutencao[manutencao.tipo]}
                                        </span>

                                        <time>
                                            {formatarData(manutencao.dataRealizacao)}
                                        </time>
                                    </header>

                                    <h2>
                                        {manutencao.descricao || "Manutenção sem descrição"}
                                    </h2>

                                    <div className="manutencao-card__metric">
                                        <span>Horímetro no momento</span>

                                        <strong>{manutencao.horimetroNoMomento.toLocaleString("pt-BR", {
                                            minimumFractionDigits: 1,
                                            maximumFractionDigits: 1,
                                        })}
                                            <small> h</small>
                                        </strong>
                                    </div>
                                </div>

                                <Link className="manutencoes-button manutencoes-button--secondary" to={`/propriedades/${propriedadeId}/maquinas/${maquinaId}/manutencoes/${manutencao.id}/editar`}>
                                    Editar
                                </Link>
                            </article>
                        ))}
                    </div>
                )}
            </section>
        </main>
    );
}