import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ativarMaquina, desativarMaquina, listarMaquinas, rotuloTipoMaquina, type Maquina, type TipoMaquina } from "../api/maquinas";
import "../styles/maquinas.css";

export function MaquinaPage() {

    const { propriedadeId } = useParams<{ propriedadeId: string }>();

    const [maquinas, setMaquinas] = useState<Maquina[]>([]);
    const [busca, setBusca] = useState("");
    const [tipo, setTipo] = useState<"TODOS" | TipoMaquina>("TODOS");
    const [carregando, setCarregando] = useState(true);
    const [alterandoId, setAlterandoId] = useState<string | null>(null);
    const [erro, setErro] = useState("");

    useEffect(() => {
        if (!propriedadeId) {
            setErro("Propriedade não identificada.");
            setCarregando(false);
            return;
        }

        carregarMaquinas(propriedadeId);
    }, [propriedadeId]);

    async function carregarMaquinas(id: string) {
        try {
            setCarregando(true);
            setErro("");

            const resposta = await listarMaquinas(id);
            setMaquinas(resposta);
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : "Não foi possivel carregar as máquinas."
            );
        } finally {
            setCarregando(false);
        }
    }

    async function alterarStatus(maquina: Maquina) {
        if (!propriedadeId) {
            return;
        }

        try {
            setAlterandoId(maquina.id);
            setErro("");

            const maquinaAtualizada = maquina.ativo
                ? await desativarMaquina(propriedadeId, maquina.id)
                : await ativarMaquina(propriedadeId, maquina.id);

            setMaquinas((estadoAtual) =>
                estadoAtual.map((item) =>
                    item.id === maquina.id ? maquinaAtualizada : item
                )
            );
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : "Não foi possivel alterar o status da máquina."
            );
        } finally {
            setAlterandoId(null);
        }
    }

    const maquinasFiltradas = useMemo(() => {
        const termo = busca.trim().toLowerCase();

        return maquinas.filter((maquina) => {
            const correspondeBusca =
                maquina.identificador.toLowerCase().includes(termo) ||
                rotuloTipoMaquina[maquina.tipo]
                    .toLowerCase()
                    .includes(termo);

            const correspondeTipo =
                tipo === "TODOS" || maquina.tipo === tipo;

            return correspondeBusca && correspondeTipo;
        });
    }, [maquinas, busca, tipo]);

    const totalAtivas = maquinas.filter((maquina) => maquina.ativo).length;
    const totalInativas = maquinas.length - totalAtivas;

    if (!propriedadeId) {
        return (
            <main className="maquinas-page">
                <div className="maquinas-alert maquinas-alet--error">
                    Propriedade não identificada.
                </div>
            </main>
        );
    }

    return (
        <main className="maquinas-page">
            <section className="maquinas-hero">
                <div>
                    <span className="maquinas-eyebrow">
                        Gestão de equipamentos
                    </span>

                    <h1>Máquinas</h1>

                    <p>
                        Acompanhe os equipamentos, horímetros, manutenções
                        e abastecimentos da propriedade.
                    </p>
                </div>

                <Link
                    className="maquinas-button maquinas-button--primary"
                    to={`/propriedades/${propriedadeId}/maquinas/nova`}
                >
                    + Nova máquina
                </Link>
            </section>

            <section className="maquinas-summary">
                <article className="maquinas-summary-card">
                    <span>Total cadastrado</span>
                    <strong>{maquinas.length}</strong>
                    <small>Equipamentos registrados</small>
                </article>

                <article className="maquinas-summary-card maquinas-summary-card--success">
                    <span>Em operação</span>
                    <strong>{totalAtivas}</strong>
                    <small>Máquinas ativas</small>
                </article>

                <article className="maquinas-summary-card maquinas-summary-card--muted">
                    <span>Fora de operação</span>
                    <strong>{totalInativas}</strong>
                    <small>Máquinas inativas</small>
                </article>
            </section>

            <section className="maquinas-content">
                <div className="maquinas-toolbar">
                    <label className="maquinas-search">
                        <span>Buscar máquina</span>

                        <input type="search" value={busca} placeholder="Identificador ou tipo..." onChange={(e) => setBusca(e.target.value)}/>
                    </label>

                    <label className="maquinas-filter">
                        <span>Tipo</span>

                        <select value={tipo} onChange={(e) => setTipo(e.target.value as | "TODOS" | TipoMaquina)}>
                            <option value="TODOS">Todos os tipos</option>
                            <option value="TRATOR">Trator</option>
                            <option value="COLHEITADEIRA">Colheitadeira</option>
                            <option value="PULVERIZADOR">Pulverizador</option>
                            <option value="IMPLEMENTO">Implemento</option>
                        </select>
                    </label>
                </div>

                {erro && (
                    <div className="maquinas-alert maquinas-alert--error">
                        {erro}
                    </div>
                )}

                {carregando ? (
                    <div className="maquinas-state">
                        <div className="maquinas-spinner"/>
                        <p>Carregando máquinas...</p>
                    </div>
                ) : maquinasFiltradas.length === 0 ? (
                    <div className="maquinas-state">
                        <div className="maquinas-empty-icon">M</div>
                        <h2>Nenhuma máquina encontrada</h2>
                        <p>
                            {maquinas.length === 0
                                ? "Cadastre o primeiro equipamento da propriedade."
                                : "Tente alterar os filtros da pesquisa."
                            }
                        </p>
                    </div>
                ) : (
                    <div className="maquinas-grid">
                        {maquinasFiltradas.map((maquina) => (
                            <article className={`maquina-card ${!maquina.ativo ? "maquina-card--inativa" : ""}`} key={maquina.id}>
                                <header className="maquina-card__header">
                                    <div className="maquina-card__icon">
                                        {maquina.tipo.charAt(0)}
                                    </div>

                                    <div className="maquina-card__title">
                                        <span>
                                            {rotuloTipoMaquina[maquina.tipo]}
                                        </span>
                                        <h2>{maquina.identificador}</h2>
                                    </div>

                                    <span className={`maquina-status ${maquina.ativo ? "maquina-status--ativo" : "maquina-status--inativo"}`}>
                                        {maquina.ativo ? "Ativa" : "Inativa"}
                                    </span>
                                </header>

                                <div className="maquina-card__metric">
                                    <span>Horímetro atual</span>
                                    <strong>{maquina.horimetroAtual.toLocaleString("pt-BR", { minimumFractionDigits: 1, maximumFractionDigits: 1 })}</strong>
                                    <small>Horas trabalhadas</small>
                                </div>

                                <footer className="maquina-card__actions">
                                    <Link
                                        className="maquinas-button maquinas-button--secondary"
                                        to={`/propriedades/${propriedadeId}/maquinas/${maquina.id}`}
                                    >
                                        Ver detalhes
                                    </Link>

                                    <button type="button" className={`maquinas-button ${
                                        maquina.ativo
                                            ? "maquinas-button--danger"
                                            : "maquinas-button--success"
                                    }`} disabled={alterandoId === maquina.id} onClick={() => alterarStatus(maquina)}>
                                        {alterandoId === maquina.id
                                            ? "Aguarde..."
                                            : maquina.ativo
                                                ? "Desativar"
                                                : "Ativar"
                                        }
                                    </button>
                                </footer>
                            </article>
                        ))}
                    </div>
                )}
            </section>
        </main>
    )
}