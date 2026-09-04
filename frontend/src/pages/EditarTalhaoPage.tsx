import React, { useEffect, useState } from "react";
import "../styles/propriedade-form.css";
import { useNavigate, useParams } from "react-router-dom";
import { atualizarTalhao, buscarTalhaoPorId } from "../api/talhoes";

export function EditarTalhao() {
    
    const [nome, setNome] = useState("");
    const [areaHectares, setAreaHectares] = useState("");

    const [carregando, setCarregando] = useState(true);
    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    const { propriedadeId, talhaoId } = useParams();
    const navigate = useNavigate();
    
    const idPropriedade = Number(propriedadeId);
    const idTalhao = Number(talhaoId);

    const idsValidos =
        Number.isInteger(idPropriedade) &&
        idPropriedade > 0 &&
        Number.isInteger(idTalhao) &&
        idTalhao > 0;

    useEffect(() => {
        let componenteAtivo = true;

        if (!idsValidos) {
            return;
        }

        buscarTalhaoPorId(idPropriedade, idTalhao).then((talhao) => {
            if (!componenteAtivo) {
                return;
            }

            setNome(talhao.nome);
            setAreaHectares(String(talhao.areaHectares));
        })
        .catch((erroRecebido) => {
            if (componenteAtivo) {
                setErro(
                    erroRecebido instanceof Error
                        ? erroRecebido.message
                        : "Não foi possivel carregar o talhão."
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
    }, [idPropriedade, idTalhao, idsValidos]);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setErro(null);

        const areaConvertida = Number(areaHectares);

        if (!idsValidos) {
            setErro("Identificador do talhão inválido.");
            return;
        }

        if (!Number.isFinite(areaConvertida) || areaConvertida <= 0) {
            setErro("Informe uma área maior que zero.");
            return;
        }

        try {
            setEnviando(true);

            await atualizarTalhao(idPropriedade, idTalhao, {
                nome: nome.trim(),
                areaHectares: areaConvertida
            });

            navigate(`/propriedades/${idPropriedade}`, {
                replace: true,
                state: {
                    mensagem: "Talhão atualizado com sucesso."
                }
            });
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possível atualizar o talhão."
            );
        } finally {
            setEnviando(false);
        }
    }

    function voltarParaPropriedade() {
        if (Number.isInteger(idPropriedade) && idPropriedade > 0) {
            navigate(`/propriedades/${idPropriedade}`);
            return;
        }

        navigate("/propriedades");
    }

    if (!idsValidos) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback property-details-feedback--error" role="alert">
                    <h1>Identificador inválido</h1>
                    <p>Não foi possivel identificar o talhão da propriedade.</p>

                    <button type="button" onClick={voltarParaPropriedade}>Voltar</button>
                </div>
            </main>
        );
    }

    if (carregando) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback" role="status">
                    Carregando talhão...
                </div>
            </main>
        );
    }

    if (erro && !nome) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback property-details-feedback--error" role="alert">
                    <h1>Não foi possível editar o talhão</h1>
                    <p>{erro}</p>


                    <button type="button" onClick={voltarParaPropriedade}>
                        Voltar
                    </button>
                </div>
            </main>
        );
    }

    return (
        <main className="propriedade-form-page">
            <section className="propriedade-form-card">
                <header className="propriedade-form-header">
                    <span className="propriedade-form-eyebrow">
                        Organização produtiva
                    </span>

                    <h1>Editar talhão</h1>

                    <p>
                        Atualize o nome e a área produtiva do talhão.
                    </p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome do talhão</label>

                        <input type="text" name="nome" id="nome" value={nome} onChange={(e) => setNome(e.target.value)} minLength={3} maxLength={80} required />
                    </div>

                    <div className="propriedade-form-field">
                        <label htmlFor="areaHectares">Área produtiva</label>

                        <div className="propriedade-form-area">
                            <input type="number" name="areaHectares" id="areaHectares" value={areaHectares} onChange={(e) => setAreaHectares(e.target.value)} min="0.01" step="0.01" inputMode="decimal" required />
                            <span>hectares</span>
                        </div>
                    </div>

                    {erro && (
                        <p className="propriedade-form-error" role="alert">
                            {erro}
                        </p>
                    )}

                    <div className="propriedade-form-actions">
                        <button type="button" className="propriedade-form-cancel" onClick={voltarParaPropriedade} disabled={enviando}>
                            Cancelar
                        </button>

                        <button type="submit" className="propriedade-form-submit" disabled={enviando}>
                            {enviando ? "Salvando..." : "Salvar alteração"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    )
}