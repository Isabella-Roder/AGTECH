import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { atualizarAnimal, buscarAnimalPorId, cadastrarAnimal, rotulosSexoAnimal, type DadosAnimal, type SexoAnimal } from "../api/animais";

function obterDataAtual(): string {
    const agora = new Date();
    const ano = agora.getFullYear();
    const mes = String(agora.getMonth() + 1).padStart(2, "0");
    const dia = String(agora.getDate()).padStart(2, "0");

    return `${ano}-${mes}-${dia}`;
}

export function FormularioAnimalPage() {

    const { propriedadeId, rebanhoId, animalId } = useParams<{
        propriedadeId: string;
        rebanhoId: string;
        animalId?: string;
    }>();

    const navigate = useNavigate();
    const editando = Boolean(animalId);

    const [identificacao, setIdentificacao] = useState("");
    const [sexo, setSexo] = useState<SexoAnimal>("FEMEA");
    const [dataNascimento, setDataNascimento] = useState("");
    const [carregando, setCarregando] = useState(editando);
    const [salvando, setSalvando] = useState(false);
    const [erro, setErro] = useState("");

    const caminhoVoltar =
        propriedadeId && rebanhoId
            ? `/propriedades/${propriedadeId}/rebanhos/${rebanhoId}/animais`
            : "/propriedades";

    useEffect(() => {
        if (!propriedadeId || !rebanhoId || !animalId) {
            return;
        }

        let ativo = true;

        buscarAnimalPorId(propriedadeId, rebanhoId, animalId)
            .then((animal) => {
                if (!ativo) {
                    return;
                }

                setIdentificacao(animal.identificacao);
                setSexo(animal.sexo);
                setDataNascimento(animal.dataNascimento ?? "");
            })
            .catch((error) => {
                if (ativo) {
                    setErro(
                        error instanceof Error
                            ? error.message
                            : "Não foi possivel carregar o animal."
                    );
                }
            })
            .finally(() => {
                if (ativo) {
                    setCarregando(false);
                };
            });

        return () => {
            ativo = false;
        };
    }, [propriedadeId, rebanhoId, animalId]);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();

        if (!propriedadeId || !rebanhoId) {
            setErro("Rebanho não identificado.");
            return;
        }

        const identificacaoTratada = identificacao.trim();

        if (!identificacaoTratada) {
            setErro("Informe a identificação do animal.");
            return;
        }

        if (identificacaoTratada.length > 50) {
            setErro("A identificação deve ter no máximo 50 caracteres.");
            return;
        }

        if (dataNascimento && dataNascimento >= obterDataAtual()) {
            setErro("A data de nascimento deve ser anterior à data atual.");
            return;
        }

        const dados: DadosAnimal = {
            identificacao: identificacaoTratada,
            sexo,
            dataNascimento: dataNascimento || null,
        };

        try {
            setSalvando(true);
            setErro("");

            if (animalId) {
                await atualizarAnimal(
                    propriedadeId,
                    rebanhoId,
                    animalId,
                    dados
                );
            } else {
                await cadastrarAnimal(
                    propriedadeId,
                    rebanhoId,
                    dados
                );
            }

            navigate(caminhoVoltar);
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : "Não foi possivel salvar o animal."
            );
        } finally {
            setSalvando(false);
        }
    }

    if (!propriedadeId || !rebanhoId) {
        return (
            <main className="animais-page">
                <p className="animais-alert">
                    Rebanho não identificado.
                </p>
            </main>
        );
    }

    if (carregando) {
        return (
            <main className="animais-page">
                <div className="animais-state">
                    Carregando animal...
                </div>
            </main>
        );
    }

    return (
        <main className="animais-page">
            <header className="animais-hero">
                <div>
                    <span className="animais-eyebrow">Pecuária</span>

                    <h1>
                        {editando ? "Editar animal" : "Novo animal"}
                    </h1>

                    <p>
                        Informe a identificação e os básicos do animal.
                    </p>
                </div>

                <Link className="animais-button animais-button--ghost" to={caminhoVoltar}>
                    Voltar
                </Link>
            </header>

            <section className="animal-form-container">
                <div className="animal-form-heading">
                    <span>Informações do animal</span>
                    <h2>Dados básicos</h2>
                    <p>
                        A data de nascimento é opcional e deve ser anterior à data atual.
                    </p>
                </div>

                {erro && (
                    <p className="animais-alert" role="alert">
                        {erro}
                    </p>
                )}

                <form className="animal-form" onSubmit={handleSubmit}>
                    <label className="animal-field animal-field--full">
                        <span>Identificação</span>
                        <input type="text" value={identificacao} maxLength={50} placeholder="Ex.: BRINCO-001" disabled={salvando} onChange={(e) => setIdentificacao(e.target.value)} required />
                        <small>
                            Código do brinco, nome ou identificação utilizada na propriedade.
                        </small>
                    </label>

                    <label className="animal-field">
                        <span>Sexo</span>
                        <select value={sexo} disabled={salvando} onChange={(e) => setSexo(e.target.value as SexoAnimal)}>
                            {Object.entries(rotulosSexoAnimal).map(
                                ([valor, rotulo]) => (
                                    <option value={valor} key={valor}>{rotulo}</option>
                                )
                            )}
                        </select>
                    </label>

                    <label className="animal-field">
                        <span>Data de nascimento</span>
                        <input type="date" value={dataNascimento} max={obterDataAtual()} disabled={salvando} onChange={(e) => setDataNascimento(e.target.value)} />
                        <small>Campo opcional.</small>
                    </label>

                    <div className="animal-form-actions">
                        <Link className="animais-button animais-button--secondary" to={caminhoVoltar}>
                            Cancelar
                        </Link>

                        <button type="submit" className="animais-button animais-button--primary" disabled={salvando}>
                            {salvando ? "Salvando..." : editando ? "Salvar alterações" : "Cadastrar animal"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    );
}
