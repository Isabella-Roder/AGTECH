import React, { useState } from "react";
import { login } from "../api/auth";
import { Link, useLocation, useNavigate } from "react-router-dom";

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
        <main>
            <h1>Entrar</h1>

            {mensagem && <p role="status">{mensagem}</p>}

            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="email">E-mail</label>
                    <input id="email" name="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} autoComplete="email" required />
                </div>

                <div>
                    <label htmlFor="senha">Senha</label>
                    <input id="senha" name="senha" type="password" value={senha} onChange={(e) => setSenha(e.target.value)} autoComplete="current-password" required />
                </div>

                {erro && <p role="alert">{erro}</p>}

                <button type="submit" disabled={enviando}>
                    {enviando ? "Entrando...": "Entrar"}
                </button>
            </form>

            <p>
                Ainda não possui uma conta?{" "}
                <Link to="/cadastro">Criar conta</Link>
            </p>
        </main>
    );
}