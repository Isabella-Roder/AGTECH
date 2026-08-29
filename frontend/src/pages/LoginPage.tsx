import React, { useState } from "react";
import { login } from "../api/auth";
import { Link, useLocation, useNavigate } from "react-router-dom";
import "../styles/auth.css"

interface LoginLocationState{
    mensagem?: string;
}

export function LoginPage() {
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [erro, setErro] = useState<string | null>(null);
    const [enviando, setEnviando] = useState(false);

    const navigate = useNavigate();
    const location = useLocation();

    const state = location.state as LoginLocationState | null;
    const mensagem = state?.mensagem;

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setErro(null);
        setEnviando(true);

        try {
            await login(email, senha);
            navigate("/propriedades", {replace: true});
        } catch (err) {
            setErro(err instanceof Error ? err.message : "Erro ao fazer login");
        } finally {
            setEnviando(false)
        }
    }

    return (
        <main className="auth-page">
            <section className="auth-card">
                <h1>Entrar</h1>
                <p className="auth-subtitle">
                    Acesse sua gestão rural
                </p>

                {mensagem && (
                    <p
                        className="auth-message auth-message--success"
                        role="status"
                    >
                        {mensagem}
                    </p>
                )}

                <form className="auth-form" onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="email">E-mail</label>
                        <input
                            id="email"
                            type="email"
                            value={email}
                            onChange={(evento) =>
                                setEmail(evento.target.value)
                            }
                            autoComplete="email"
                            required
                        />
                    </div>

                    <div className="auth-field">
                        <label htmlFor="senha">Senha</label>
                        <input
                            id="senha"
                            type="password"
                            value={senha}
                            onChange={(evento) =>
                                setSenha(evento.target.value)
                            }
                            autoComplete="current-password"
                            required
                        />
                    </div>

                    {erro && (
                        <p
                            className="auth-message auth-message--error"
                            role="alert"
                        >
                            {erro}
                        </p>
                    )}

                    <button
                        className="auth-button"
                        type="submit"
                        disabled={enviando}
                    >
                        {enviando ? "Entrando..." : "Entrar"}
                    </button>
                </form>

                <p className="auth-footer">
                    Ainda não possui uma conta?{" "}
                    <Link to="/cadastro">Criar conta</Link>
                </p>
            </section>
        </main>
    );
}