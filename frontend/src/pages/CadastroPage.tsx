import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { cadastrarUsuario } from "../api/usuarios";

export function CadastroPage() {
    const [nome, setNome] = useState("");
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [confirmacaoSenha, setConfirmacaoSenha] = useState("");
    const [erro, setErro] = useState<string | null>(null);
    const [enviado, setEnviado] = useState(false);

    const navigate = useNavigate();

    async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
        evento.preventDefault();
        setErro(null);

        if (senha !== confirmacaoSenha) {
            setErro("As senhas não coincidem.");
            return;
        }

        try {
            setEnviado(true);

            await cadastrarUsuario({
                nome: nome.trim(),
                email: email.trim(),
                senha
            });

            navigate("/", {
                replace: true,
                state: {
                    mensagem: "Conta criada com sucesso. Faça o login.",
                },
            });
        } catch (erroRecebido) {
            setErro(erroRecebido instanceof Error ? erroRecebido.message : "Não foi possovel criar a conta.");
        } finally {
            setEnviado(false);
        }
    }

    return (
        <main>
            <h1>Criar conta</h1>

            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="nome">Nome</label>
                    <input id="nome" name="nome" type="text" value={nome} onChange={(evento) => setNome(evento.target.value)} minLength={3} maxLength={80} autoComplete="name" required/>
                </div>

                <div>
                    <label htmlFor="email">E-mail</label>
                    <input id="email" name="email" type="text" value={email} onChange={(evento) => setEmail(evento.target.value)} autoComplete="email" required />
                </div>

                <div>
                    <label htmlFor="senha">Senha</label>
                    <input id="senha" name="senha" type="password" value={senha} onChange={(evento) => setSenha(evento.target.value)} minLength={8} maxLength={60} autoComplete="new-password" required />
                </div>

                <div>
                    <label htmlFor="confirmacaoSenha">Confirme a senha</label>
                    <input id="confirmacaoSenha" name="confirmacaoSenha" type="password" value={confirmacaoSenha} onChange={(evento) => setConfirmacaoSenha(evento.target.value)} minLength={8} maxLength={60} autoComplete="new-password" required />
                </div>

                {erro && (
                    <p role="alert">{erro}</p>
                )}

                <button type="submit" disabled={enviado}>
                    {enviado ? "Criando conta...": "Criar conta"}
                </button>
            </form>

            <p>
                Já possui uma conta? <Link to="/">Entrar</Link>
            </p>
        </main>
    )
}