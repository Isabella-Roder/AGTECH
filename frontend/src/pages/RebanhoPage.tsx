import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom"
import { ativarRebanho, desativarRebanho, listarRebanhos, rotulosEspecie, type EspecieAnimal, type Rebanho } from "../api/rebanhos";
import "../styles/rebanhos.css";

type FiltroEspecie = "TODAS" | EspecieAnimal;

export function RebanhoPage() {

    const { propriedadeId } = useParams<{ propriedadeId: string }>();

    const [rebanhos, setRebanhos] = useState<Rebanho[]>([]);
    const [busca, setBusca] = useState("");
    const [especie, setEspecie] = useState<FiltroEspecie>("TODAS");
    const [carregando, setCarregando] = useState(true);
    const [alterandoId, setAlterandoId] = useState<string | null>(null);
    const [erro, setErro] = useState("");

    useEffect(() => {
        if (!propriedadeId) {
            return;
        }

        let ativo = true;

        listarRebanhos(propriedadeId)
            .then((dados) => {
                if (ativo) {
                    setRebanhos(dados);
                }
            })
            .catch((error) => {
                if (ativo) {
                    setErro(
                        error instanceof Error
                            ? error.message
                            : "Não foi possivel carregar os rebanhos."
                    );
                }
            })
            .finally(() => {
                if (ativo) {
                    setCarregando(false);
                }
            });

        return () => {
            ativo = false;
        };
    }, [propriedadeId]);

    async function alterarStatus(rebanho: Rebanho) {
        if (!propriedadeId) {
            return;
        }

        const acao = rebanho.ativo ? "desativar" : "ativar";

        if (!window.confirm(`Deseja ${acao} o rebanho "${rebanho.nome}"?`)) {
            return;
        }

        try {
            setAlterandoId(rebanho.id);
            setErro("");

            const atualizado = rebanho.ativo
                ? await desativarRebanho(propriedadeId, rebanho.id)
                : await ativarRebanho(propriedadeId, rebanho.id);

            setRebanhos((atuais) =>
                atuais.map((item) =>
                    item.id === atualizado.id ? atualizado : item
                )
            );
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : `Não foi possivel ${acao} o rebanho.`
            );
        } finally {
            setAlterandoId(null);
        }
    }

    const rebanhosFiltrados = useMemo(() => {
        const termo = busca.trim().toLocaleLowerCase("pt-BR");

        return rebanhos.filter((rebanho) => {
            const correspondeBusca =
                rebanho.nome.toLocaleLowerCase("pt-BR").includes(termo) ||
                rotulosEspecie[rebanho.especie]
                    .toLocaleLowerCase("pt-BR")
                    .includes(termo);

            const corresponsdeEspecie =
                especie === "TODAS" || rebanho.especie === especie;

            return correspondeBusca && corresponsdeEspecie;
        });
    }, [rebanhos, busca, especie]);

    const ativos = rebanhos.filter((rebanho) => rebanho.ativo).length;

    if (!propriedadeId) {
        return (
            <main className="rebanhos-page">
                <p className="rebanhos-alert">
                    Propriedade não identificada.
                </p>
            </main>
        );
    }

    return (
        <main className="rebanhos-page">
            <header className="rebanhos-hero">
                <div>
                    <span className="rebanhos-eyebrow">Pecuária</span>
                    <h1>Rebanhos</h1>
                    <p>
                        Organize os rebanhos vinculados a esta propriedade.
                    </p>
                </div>

                <Link className="rebanhos-button rebanhos-button--primary" to={`/propriedades/${propriedadeId}/rebanhos/novo`}>
                    + Novo rebanho
                </Link>
            </header>

            <section className="rebanhos-summary">
                <article>
                    <span>Total cadastrado</span>
                    <strong>{rebanhos.length}</strong>
                </article>

                <article>
                    <span>Ativos</span>
                    <strong>{ativos}</strong>
                </article>

                <article>
                    <span>Inativos</span>
                    <strong>{rebanhos.length - ativos}</strong>
                </article>
            </section>

            <section className="rebanhos-content">
                <div className="rebanhos-toolbar">
                    <label>
                        <span>Buscar</span>
                        <input type="text" value={busca} placeholder="Nome ou espécie..." onChange={(e) => setBusca(e.target.value)} />
                    </label>

                    <label>
                        <span>Espécie</span>
                        <select value={especie} onChange={(e) => setEspecie(e.target.value as FiltroEspecie)}>
                            <option value="TODAS">Todas</option>

                            {Object.entries(rotulosEspecie).map(
                                ([valor, rotulo]) => (
                                    <option key={valor} value={valor}>
                                        {rotulo}
                                    </option>
                                )
                            )}
                        </select>
                    </label>
                </div>

                {erro && (
                    <p className="rebanhos-alert" role="alert">
                        {erro}
                    </p>
                )}

                {carregando ? (
                    <div className="rebanhos-state">
                        Carregando rebanhos...
                    </div>
                ) : erro && rebanhos.length === 0 ? null :(
                    rebanhosFiltrados.length === 0 ? (
                        <div className="rebanhos-state">
                            {rebanhos.length  === 0
                                ? "Nenhum rebanho cadastrado nesta propriedade."
                                : "Nenhum rebanho corresponde aos filtros."
                            }
                        </div>
                    ) : (
                        <div className="rebanhos-grid">
                            {rebanhosFiltrados.map((rebanho) => (
                                <article className="rebanho-card" key={rebanho.id}>
                                    <div className="rebanho-card__top">
                                        <span className="rebanho-card__icon">
                                            {rebanho.especie.charAt(0)}
                                        </span>
                                        <span className={rebanho.ativo ? "rebanho-status rebanho-status--ativo" : "rebanho-status rebanho-status--inativo"}>
                                            {rebanho.ativo ? "Ativo" : "Inativo"}
                                        </span>
                                    </div>

                                    <span className="rebanho-card__species">
                                        {rotulosEspecie[rebanho.especie]}
                                    </span>
                                    <h2>{rebanho.nome}</h2>

                                    <div className="rebanho-card__actions">
                                        <Link
                                            className="rebanhos-button rebanhos-button--animals"
                                            to={`/propriedades/${propriedadeId}/rebanhos/${rebanho.id}/animais`}
                                        >
                                            Ver animais
                                        </Link>

                                        <Link className="rebanhos-button rebanhos-button--secondary" to={`/propriedades/${propriedadeId}/rebanhos/${rebanho.id}/editar`}>
                                            Editar
                                        </Link>

                                        <button type="button" className="rebanhos-button rebanhos-button--outline" disabled={alterandoId === rebanho.id} onClick={() => alterarStatus(rebanho)}>
                                            {alterandoId === rebanho.id ? "Aguarde..." : rebanho.ativo ? "Desativar" : "Ativar"}
                                        </button>
                                    </div>
                                </article>
                            ))}
                        </div>
                    )
                )}
            </section>
        </main>
    );
}
