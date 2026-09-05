import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { cadastrarCultura } from "../api/cultura";
import "../styles/propriedade-form.css";

export function CadastroCultura() {
    
    const [nome, setNome] = useState("");

    const [erro, setErro] = useState<string | null>(null);
    const [enviando, setEnviando] = useState(false);

    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setErro(null);

        try {
            setEnviando(true);

            await cadastrarCultura({
                nome: nome.trim()
            });

            navigate("/culturas", {
                replace: true,
                state: {
                    mensagem: "Cultura cadastrada com sucesso."
                }
            });
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possivel cadastrar cultura."
            );
        } finally {
            setEnviando(false);
        }
    }

    return (
        <main className="propriedade-form-page">
            <section className="propriedade-form-card">
                <header className="propriedade-form-header">
                    <span className="propriedade-form-eyebrow">
                        Catálogo agrícola
                    </span>
                    <h1>Nova cultura</h1>
                    <p>
                        Cadastre uma cultura para utilizá-la nas próximas
                        etapas do planejamento agrícola.
                    </p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome da cultura</label>
                        <input
                            id="nome"
                            name="nome"
                            type="text"
                            value={nome}
                            onChange={(evento) => setNome(evento.target.value)}
                            minLength={2}
                            maxLength={60}
                            placeholder="Ex.: Soja"
                            autoFocus
                            required
                        />
                    </div>

                    {erro && (
                        <p className="propriedade-form-error" role="alert">
                            {erro}
                        </p>
                    )}

                    <div className="propriedade-form-actions">
                        <button
                            className="propriedade-form-cancel"
                            type="button"
                            onClick={() => navigate("/culturas")}
                            disabled={enviando}
                        >
                            Cancelar
                        </button>
                        <button
                            className="propriedade-form-submit"
                            type="submit"
                            disabled={enviando || nome.trim().length < 2}
                        >
                            {enviando ? "Cadastrando..." : "Cadastrar cultura"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    );

}
