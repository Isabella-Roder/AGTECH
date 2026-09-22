import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { atualizarRebanho, buscarRebanhoPorId, cadastrarRebanho, rotulosEspecie, type DadosRebanho, type EspecieAnimal } from "../api/rebanhos";
import "../styles/rebanhos.css";

export function FormularioRebanhoPage() {

    const { propriedadeId, rebanhoId } = useParams<{
        propriedadeId: string;
        rebanhoId?: string;
    }>();

    const navigate = useNavigate();
    const editando = Boolean(rebanhoId);

    const [nome, setNome] = useState("");
    const [especie, setEspecie] = useState<EspecieAnimal>("BOVINO");
    const [carregando, setCarregando] = useState(editando);
    const [salvando, setSalvando] = useState(false);
    const [erro, setErro] = useState("");

    const caminhoVoltar = propriedadeId
        ? `/propriedades/${propriedadeId}/rebanhos`
        : "/propriedades";

    useEffect(() => {
        if (!rebanhoId || !propriedadeId) {
            return;
        }

        let ativo = true;

        buscarRebanhoPorId(propriedadeId, rebanhoId)
            .then((rebanho) => {
                if (!ativo) {
                    return;
                }
                setNome(rebanho.nome);
                setEspecie(rebanho.especie);
            })
            .catch((error) => {
                if (ativo) {
                    setErro(
                        error instanceof Error
                            ? error.message
                            : "Não foi possivel carregar o rebanho."
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
    }, [propriedadeId, rebanhoId]);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();

        if (!propriedadeId) {
            setErro("propriedade não identificada.");
            return;
        }

        const nomeTratado = nome.trim();

        if (!nomeTratado) {
            setErro("Informe o nome do rebanho.");
            return;
        }

        if (nomeTratado.length > 100) {
            setErro("O nome deve ter o máximo 100 caracteres.");
            return;
        }

        const dados: DadosRebanho = {
            nome: nomeTratado,
            especie
        };

        try {
            setSalvando(true);
            setErro("");

            if (rebanhoId) {
                await atualizarRebanho(propriedadeId, rebanhoId, dados);
            } else {
                await cadastrarRebanho(propriedadeId, dados);
            }

            navigate(caminhoVoltar);
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : "Não foi possivel salvar o rebanho."
            );
        } finally {
            setSalvando(false);
        }
    }

    if (!propriedadeId) {
        return (
            <main className="rebanhos-page">
                <p className="rebanhos-alert">
                    Propriedade não identificada.
                </p>
            </main>
        );
    }

    if (carregando) {
        return (
            <main className="rebanhos-page">
                <div className="rebanhos-state">
                    Carregando rebanho...
                </div>
            </main>
        );
    }

    return (
        <main className="rebanhos-page">
            <header className="rebanhos-hero">
                <div>
                    <span className="rebanhos-eyebrow">Pecuária</span>
                    <h1>
                        {editando ? "Editar rebanho" : "Novo rebanho"}
                    </h1>

                    <p>
                        Identifique o grupo e selecione sua espécie.
                    </p>
                </div>

                <Link className="rebanhos-button rebanhos-button--hero" to={caminhoVoltar}>
                    Voltar
                </Link>
            </header>

            <section className="rebanho-form-container">
                <div className="rebanho-form-heading">
                    <span>Informações do rebanho</span>
                    <h2>Dados básicos</h2>
                    <p>
                        O cadastro do rebanho organiza os animais da propriedade.
                    </p>
                </div>

                {erro && (
                    <p className="rebanhos-alert" role="alert">
                        {erro}
                    </p>
                )}

                <form className="rebanho-form" onSubmit={handleSubmit}>
                    <label className="rebanho-field">
                        <span>Nome do rebanho</span>
                        <input type="text" value={nome} maxLength={100} placeholder="Ex.: Rebanho leiteiro" disabled={salvando} onChange={(e) => setNome(e.target.value)} required />
                    </label>

                    <label className="rebanho-field">
                        <span>Espécie</span>

                        <select value={especie} disabled={salvando} onChange={(e) => setEspecie(e.target.value as EspecieAnimal)}>
                            {Object.entries(rotulosEspecie).map(
                                ([valor, rotulo]) => (
                                    <option value={valor} key={valor}>{rotulo}</option>
                                )
                            )}
                        </select>
                    </label>

                    <div className="rebanho-form-actions">
                        <Link className="rebanhos-button rebanhos-button--secondary" to={caminhoVoltar}>
                            Cancelar
                        </Link>

                        <button type="submit" className="rebanhos-button rebanhos-button--primary" disabled={salvando}>
                            {salvando ? "Salvando..." : editando ? "Salvar alterações" : "Cadastrar rebanho"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    );
}
