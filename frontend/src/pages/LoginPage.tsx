import React, { useState } from "react";
import { login } from "../api/auth";
import { Link, useNavigate } from "react-router-dom";

export function LoginPage() {
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [erro, setErro] = useState<string | null>(null);

    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setErro(null);

        try {
            await login(email, senha);
            navigate("/propriedades");
        } catch (err) {
            setErro(err instanceof Error ? err.message : "Erro ao fazer login");
        }
    }

    return (
        <>
            <form onSubmit={handleSubmit}>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="E-mail"/>
                <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} placeholder="Senha" />
                {erro && <p>{erro}</p>}
                <button type="submit">Entrar</button>
            </form>

            <p>
                Ainda não possui uma conta?{" "}
                <Link to="/cadastro">Criar Conta</Link>
            </p>
        </>
    );
}