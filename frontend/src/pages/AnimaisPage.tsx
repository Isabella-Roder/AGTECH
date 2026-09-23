import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
    ativarAnimal,
    desativarAnimal,
    listarAnimais,
    rotulosSexoAnimal,
    type Animal,
    type SexoAnimal,
} from "../api/animais";
import { buscarRebanhoPorId, type Rebanho } from "../api/rebanhos";

import "../styles/animais.css";

type FiltroSexo = "TODOS" | SexoAnimal;

function formatarData(data: string | null): string {
    if (!data) return "Não informada";

    const [ano, mes, dia] = data.split("-");

    if (!ano || !mes || !dia) return data;

    return `${dia}/${mes}/${ano}`;
}

export function AnimaisPage() {
    const { propriedadeId, rebanhoId } = useParams<{
        propriedadeId: string;
        rebanhoId: string;
    }>();

    const [rebanho, setRebanho] = useState<Rebanho | null>(null);
    const [animais, setAnimais] = useState<Animal[]>([]);
    const [busca, setBusca] = useState("");
    const [sexo, setSexo] = useState<FiltroSexo>("TODOS");
    const [carregando, setCarregando] = useState(true);
    const [alterandoId, setAlterandoId] = useState<string | null>(null);
    const [erro, setErro] = useState("");

    useEffect(() => {
        if (!propriedadeId || !rebanhoId) return;

        let ativo = true;

        Promise.all([
            buscarRebanhoPorId(propriedadeId, rebanhoId),
            listarAnimais(propriedadeId, rebanhoId),
        ])
            .then(([rebanhoRecebido, animaisRecebidos]) => {
                if (!ativo) return;

                setRebanho(rebanhoRecebido);
                setAnimais(animaisRecebidos);
            })
            .catch((error) => {
                if (ativo) {
                    setErro(
                        error instanceof Error
                            ? error.message
                            : "Não foi possível carregar os animais."
                    );
                }
            })
            .finally(() => {
                if (ativo) setCarregando(false);
            });

        return () => {
            ativo = false;
        };
    }, [propriedadeId, rebanhoId]);

    async function alterarStatus(animal: Animal) {
        if (!propriedadeId || !rebanhoId) return;

        const acao = animal.ativo ? "desativar" : "ativar";

        if (
            !window.confirm(
                `Deseja ${acao} o animal “${animal.identificacao}”?`
            )
        ) {
            return;
        }

        try {
            setAlterandoId(animal.id);
            setErro("");

            const atualizado = animal.ativo
                ? await desativarAnimal(
                      propriedadeId,
                      rebanhoId,
                      animal.id
                  )
                : await ativarAnimal(
                      propriedadeId,
                      rebanhoId,
                      animal.id
                  );

            setAnimais((atuais) =>
                atuais.map((item) =>
                    item.id === atualizado.id ? atualizado : item
                )
            );
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : `Não foi possível ${acao} o animal.`
            );
        } finally {
            setAlterandoId(null);
        }
    }

    const animaisFiltrados = useMemo(() => {
        const termo = busca.trim().toLocaleLowerCase("pt-BR");

        return animais.filter((animal) => {
            const correspondeBusca =
                animal.identificacao
                    .toLocaleLowerCase("pt-BR")
                    .includes(termo);

            const correspondeSexo =
                sexo === "TODOS" || animal.sexo === sexo;

            return correspondeBusca && correspondeSexo;
        });
    }, [animais, busca, sexo]);

    const ativos = animais.filter((animal) => animal.ativo).length;
    const machos = animais.filter(
        (animal) => animal.sexo === "MACHO"
    ).length;
    const femeas = animais.filter(
        (animal) => animal.sexo === "FEMEA"
    ).length;

    if (!propriedadeId || !rebanhoId) {
        return (
            <main className="animais-page">
                <p className="animais-alert">
                    Rebanho não identificado.
                </p>
            </main>
        );
    }

    return (
        <main className="animais-page">
            <header className="animais-hero">
                <div>
                    <span className="animais-eyebrow">
                        {rebanho?.nome ?? "Rebanho"}
                    </span>

                    <h1>Animais</h1>

                    <p>
                        Gerencie os animais vinculados a este rebanho.
                    </p>
                </div>

                <div className="animais-hero__actions">
                    <Link
                        className="animais-button animais-button--ghost"
                        to={`/propriedades/${propriedadeId}/rebanhos`}
                    >
                        Voltar
                    </Link>

                    <Link
                        className="animais-button animais-button--primary"
                        to={`/propriedades/${propriedadeId}/rebanhos/${rebanhoId}/animais/novo`}
                    >
                        + Novo animal
                    </Link>
                </div>
            </header>

            <section className="animais-summary">
                <article>
                    <span>Total</span>
                    <strong>{animais.length}</strong>
                    <small>{ativos} ativos</small>
                </article>

                <article>
                    <span>Machos</span>
                    <strong>{machos}</strong>
                    <small>animais cadastrados</small>
                </article>

                <article>
                    <span>Fêmeas</span>
                    <strong>{femeas}</strong>
                    <small>animais cadastrados</small>
                </article>
            </section>

            <section className="animais-content">
                <div className="animais-toolbar">
                    <label>
                        <span>Buscar</span>
                        <input
                            type="search"
                            value={busca}
                            placeholder="Identificação do animal..."
                            onChange={(event) =>
                                setBusca(event.target.value)
                            }
                        />
                    </label>

                    <label>
                        <span>Sexo</span>
                        <select
                            value={sexo}
                            onChange={(event) =>
                                setSexo(
                                    event.target.value as FiltroSexo
                                )
                            }
                        >
                            <option value="TODOS">Todos</option>
                            <option value="MACHO">Machos</option>
                            <option value="FEMEA">Fêmeas</option>
                        </select>
                    </label>
                </div>

                {erro && (
                    <p className="animais-alert" role="alert">
                        {erro}
                    </p>
                )}

                {carregando ? (
                    <div className="animais-state">
                        Carregando animais...
                    </div>
                ) : animaisFiltrados.length === 0 ? (
                    <div className="animais-state">
                        {animais.length === 0
                            ? "Nenhum animal cadastrado neste rebanho."
                            : "Nenhum animal corresponde aos filtros."}
                    </div>
                ) : (
                    <div className="animais-grid">
                        {animaisFiltrados.map((animal) => (
                            <article
                                className={
                                    animal.ativo
                                        ? "animal-card"
                                        : "animal-card animal-card--inativo"
                                }
                                key={animal.id}
                            >
                                <header className="animal-card__header">
                                    <span className="animal-card__icon">
                                        {animal.sexo === "MACHO"
                                            ? "M"
                                            : "F"}
                                    </span>

                                    <span
                                        className={
                                            animal.ativo
                                                ? "animal-status animal-status--ativo"
                                                : "animal-status animal-status--inativo"
                                        }
                                    >
                                        {animal.ativo
                                            ? "Ativo"
                                            : "Inativo"}
                                    </span>
                                </header>

                                <span className="animal-card__sex">
                                    {rotulosSexoAnimal[animal.sexo]}
                                </span>

                                <h2>{animal.identificacao}</h2>

                                <div className="animal-card__data">
                                    <span>Data de nascimento</span>
                                    <strong>
                                        {formatarData(
                                            animal.dataNascimento
                                        )}
                                    </strong>
                                </div>

                                <footer className="animal-card__actions">
                                    <Link
                                        className="animais-button animais-button--secondary"
                                        to={`/propriedades/${propriedadeId}/rebanhos/${rebanhoId}/animais/${animal.id}/editar`}
                                    >
                                        Editar
                                    </Link>

                                    <button
                                        className="animais-button animais-button--outline"
                                        type="button"
                                        disabled={
                                            alterandoId === animal.id
                                        }
                                        onClick={() =>
                                            alterarStatus(animal)
                                        }
                                    >
                                        {alterandoId === animal.id
                                            ? "Aguarde..."
                                            : animal.ativo
                                              ? "Desativar"
                                              : "Ativar"}
                                    </button>
                                </footer>
                            </article>
                        ))}
                    </div>
                )}
            </section>
        </main>
    );
}