import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { buscarMaquinaPorId, type Maquina } from "../api/maquinas";
import { listarAbastecimento, type Abastecimento } from "../api/abastecimentos";
import "../styles/abastecimentos.css";

function formatarDataHora(data: string): string {
    const [parteData, parteHora = ""] = data.split("T");
    const [ano, mes, dia] = parteData.split("-");
    const hora = parteHora.slice(0, 5);

    if (!ano || !mes || !dia) {
        return data;
    }

    return hora
        ? `${dia}/${mes}/${ano} às ${hora}`
        : `${dia}/${mes}/${ano}`;
}

function formatarLitros(valor: number): string {
    return valor.toLocaleString("pt-BR", {
        minimumFractionDigits: 1,
        maximumFractionDigits: 2
    });
}

export function AbastecimentosPage() {

    const { propriedadeId, maquinaId } = useParams<{
        propriedadeId: string;
        maquinaId: string;
    }>();

    const [maquina, setMaquina] = useState<Maquina | null>(null);
    const [abastecimentos, setAbastecimentos] = useState<Abastecimento[]>([]);
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

                const [maquinaResposta, abastecimentosResposta] = await Promise.all([
                    buscarMaquinaPorId(propriedadeId!, maquinaId!),
                    listarAbastecimento(propriedadeId!, maquinaId!)
                ]);

                const ordenados = [...abastecimentosResposta].sort(
                    (a, b) => b.data.localeCompare(a.data)
                );

                setMaquina(maquinaResposta);
                setAbastecimentos(ordenados);
            } catch (error) {
                setErro(
                    error instanceof Error
                        ? error.message
                        : "Não foi possivel carregas os abastecimentos."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregarDados();
    }, [propriedadeId, maquinaId]);

    const abastecimentosFiltrados = useMemo(() => {
        const termo = busca.trim().toLowerCase();

        if (!termo) {
            return abastecimentos;
        }

        return abastecimentos.filter((abastecimento) => {
            const observacoes = abastecimento.observacoes?.toLowerCase() ?? "";

            const dataFormatada = formatarDataHora(abastecimento.data).toLowerCase();

            return(
                observacoes.includes(termo) ||
                dataFormatada.includes(termo)
            );
        });
    }, [abastecimentos, busca]);

    const totalLitros = abastecimentos.reduce(
        (total, abastecimento) => total + abastecimento.litros, 0
    );

    const mediaLitros =
        abastecimentos.length > 0
            ? totalLitros / abastecimentos.length
            : 0;

    const ultimoAbastecimento = abastecimentos[0];

    if (!propriedadeId || !maquinaId) {
        return (
            <main className="abastecimentos-page">
                <div className="abastecimentos-alert abastecimentos-alert--erro">
                    Máquina não identificada.
                </div>
            </main>
        )
    }


    return (
        <main className="abastecimentos-page">
            <section className="abastecimentos-hero">
                <div>
                    <span className="abastecimentos-eyebrow">
                        Controle de combustível
                    </span>

                    <h1>Abastecimentos</h1>

                    <p>
                        {maquina
                            ? `Acompanhe o consumo registrado para ${maquina.identificador}`
                            : "Acompanhe o histórico de abastecimento do equipamento"
                        }
                    </p>
                </div>

                <div className="abastecimentos-hero__actions">
                    <Link className="abastecimentos-button abastecimentos-button--ghost" to={`/propriedades/${propriedadeId}/maquinas/${maquinaId}`}>
                        Voltar
                    </Link>

                    <Link className="abastecimentos-button abastecimentos-button--primary" to={`/propriedades/${propriedadeId}/maquinas/${maquinaId}/abastecimentos/novo`}>
                        + Novo abastecimento
                    </Link>
                </div>
            </section>

            <section className="abastecimentos-summary">
                <article className="abastecimentos-summary-card">
                    <span>Total abastecimento</span>
                    <strong>{formatarLitros(totalLitros)}</strong>
                    <small>litros registrados</small>
                </article>

                <article className="abastecimentos-summary-card abastecimentos-summary-card--media">
                    <span>Média por registro</span>
                    <strong>{formatarLitros(mediaLitros)}</strong>
                    <small>litros por abastecimento</small>
                </article>

                <article className="abastecimentos-summary-card abastecimentos-summary-card--ultimo">
                        <span>Último abastecimento</span>
                        <strong className="abastecimentos-summary-card__date">
                            {ultimoAbastecimento
                                ? formatarDataHora(ultimoAbastecimento.data)
                                : "Nenhum"
                            }
                        </strong>
                        <small>
                            {abastecimentos.length} registro
                            {abastecimentos.length === 1 ? "" : "s"}
                        </small>
                </article>
            </section>

            <section className="abastecimentos-content">
                <div className="abastecimentos-toolbar">
                    <label>
                        <span>Buscar no histórico</span>

                        <input type="search" value={busca} placeholder="Data ou observações..." onChange={(e) => setBusca(e.target.value)} />
                    </label>
                </div>

                {erro && (
                    <div className="abastecimentos-alert abastecimentos-alert--erro">
                        {erro}
                    </div>
                )}

                {carregando ? (
                    <div className="abastecimentos-state">
                        <div className="abastecimentos-spinner"/>
                        <p>Carregando abastecimentos...</p>
                    </div>
                ) : abastecimentosFiltrados.length === 0 ? (
                    <div className="abastecimentos-state">
                        <div className="abastecimentos-empty-icon">
                            A
                        </div>

                        <h2>Nenhum abastecimento encontrado</h2>

                        <p>
                            {abastecimentos.length === 0
                                ? "Registre o primeiro abastecimento desta máquina."
                                : "Tente utilizar outro termo na pesquisa."
                            }
                        </p>
                    </div>
                ) : (
                    <div className="abastecimentos-list">
                        {abastecimentosFiltrados.map(
                            (abastecimento) => (
                                <article key={abastecimento.id} className="abastecimento-card">
                                    <div className="abastecimento-card__icon">
                                        L
                                    </div>

                                    <div className="abastecimento-card__main">
                                        <header>
                                            <span>Abastecimento</span>

                                            <time>
                                                {formatarDataHora(abastecimento.data)}
                                            </time>
                                        </header>

                                        <div className="abastecimento-card__metrics">
                                            <div>
                                                <span>Quantidade</span>
                                                <strong>
                                                    {formatarLitros(
                                                        abastecimento.litros
                                                    )}
                                                    <small> L</small>
                                                </strong>
                                            </div>

                                            <div>
                                                <span>Horímetro</span>
                                                <strong>
                                                    {abastecimento.horimetroNoMomento.toLocaleString(
                                                        "pt-BR",
                                                        {
                                                            minimumFractionDigits: 1,
                                                            maximumFractionDigits: 1
                                                        }
                                                    )}
                                                    <small> h</small>
                                                </strong>
                                            </div>
                                        </div>

                                        {abastecimento.observacoes && (
                                            <p>
                                                {abastecimento.observacoes}
                                            </p>
                                        )}
                                    </div>

                                    <Link className="abastecimentos-button abastecimentos-button--secondary" to={`/propriedades/${propriedadeId}/maquinas/${maquinaId}/abastecimentos/${abastecimento.id}/editar`}>
                                        Editar
                                    </Link>
                                </article>
                            )
                        )}
                    </div>
                )}
            </section>
        </main>
    );
}
