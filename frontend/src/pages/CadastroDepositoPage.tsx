import React, { useState } from "react"
import { useNavigate, useParams } from "react-router-dom";
import { cadastrarDeposito } from "../api/depositos";
import "../styles/propriedade-form.css";

export function CadastroDepositoPage() {
    
    const [nome, setNome] = useState("");
    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    const {propriedadeId = ""} = useParams();
    const navigate = useNavigate();

    const idValido = propriedadeId.length > 0;
    const caminhoDepositos = `/propriedades/${propriedadeId}/depositos`;
    
    function cancelar() {
        navigate(
            idValido
                ? caminhoDepositos
                : "/propriedades"
        );
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setErro(null);

        if (!idValido) {
            setErro("Não foi possivel identificar a propriedade.");
            return;
        }

        const nomeTratado = nome.trim();

        if (nomeTratado.length < 2) {
            setErro("O nome deve conter pelo menos 2 caracteres.");
            return;
        }

        try {
            setEnviando(true);

            await cadastrarDeposito(propriedadeId, {
                nome: nomeTratado
            });

            navigate(caminhoDepositos, {
                replace: true,
                state: {
                    mensagem: "Depósito cadastrado com sucesso."
                }
            });
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possível cadastrar depósito."
            );
        } finally {
            setEnviando(false);
        }
    }

    if (!idValido) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback property-details-feedback-error" role="alert">
                    <h1>Propriedade inválida</h1>

                    <p>
                        Não foi possível identificar a propriedade.
                    </p>

                    <button type="button" onClick={() => navigate("/propriedades")}>
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
                        Insumos e estoque
                    </span>

                    <h1>Novo depósito</h1>

                    <p>
                        Cadastre um local de armazenamento vinculado à propriedade.
                    </p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome de depósito</label>
                        <input type="text" id="nome" name="nome" value={nome} onChange={(e) => setNome(e.target.value)} minLength={2} maxLength={100} placeholder="Ex.: Armazém principal" autoFocus required />
                    </div>

                    {erro && (
                        <p className="propriedade-form-error" role="alert">
                            {erro}
                        </p>
                    )}

                    <div className="propriedade-form-actions">
                        <button type="button" className="propriedade-form-cancel" onClick={cancelar} disabled={enviando}>
                            Cancelar
                        </button>

                        <button type="submit" className="propriedade-form-submit" disabled={enviando || nome.trim().length < 2}>
                            {enviando
                                ? "Cadastrando..."
                                : "Cadastrar depósito"
                            }
                        </button>
                    </div>
                </form>
            </section>
        </main>
    )
}