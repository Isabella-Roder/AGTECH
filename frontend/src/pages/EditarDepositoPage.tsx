import { useEffect, useState, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { atualizarDeposito, buscarDepositoPorId } from "../api/depositos";
import "../styles/propriedade-form.css";

export function EditarDepositoPage() {
    const [nome, setNome] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);
    const { propriedadeId = "", depositoId = "" } = useParams();
    const navigate = useNavigate();
    const idsValidos = Boolean(propriedadeId && depositoId);
    const caminhoDepositos = `/propriedades/${propriedadeId}/depositos`;

    useEffect(() => {
        let paginaAtiva = true;
        if (!idsValidos) return;

        buscarDepositoPorId(propriedadeId, depositoId)
            .then((deposito) => {
                if (paginaAtiva) setNome(deposito.nome);
            })
            .catch((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(erroRecebido instanceof Error
                        ? erroRecebido.message
                        : "Não foi possível carregar o depósito.");
                }
            })
            .finally(() => {
                if (paginaAtiva) setCarregando(false);
            });

        return () => { paginaAtiva = false; };
    }, [depositoId, idsValidos, propriedadeId]);

    async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
        evento.preventDefault();
        setErro(null);
        const nomeTratado = nome.trim();

        if (nomeTratado.length < 2) {
            setErro("O nome deve conter pelo menos 2 caracteres.");
            return;
        }

        try {
            setEnviando(true);
            await atualizarDeposito(propriedadeId, depositoId, { nome: nomeTratado });
            navigate(caminhoDepositos, {
                replace: true,
                state: { mensagem: "Depósito atualizado com sucesso." },
            });
        } catch (erroRecebido) {
            setErro(erroRecebido instanceof Error
                ? erroRecebido.message
                : "Não foi possível atualizar o depósito.");
        } finally {
            setEnviando(false);
        }
    }

    if (!idsValidos) {
        return <main className="propriedade-form-page"><div className="property-details-feedback property-details-feedback--error" role="alert">Não foi possível identificar o depósito.</div></main>;
    }

    if (carregando) {
        return <main className="propriedade-form-page"><div className="property-details-feedback" role="status">Carregando depósito...</div></main>;
    }

    return (
        <main className="propriedade-form-page">
            <section className="propriedade-form-card">
                <header className="propriedade-form-header">
                    <span className="propriedade-form-eyebrow">Insumos e estoque</span>
                    <h1>Editar depósito</h1>
                    <p>Atualize a identificação do local de armazenamento.</p>
                </header>
                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome do depósito</label>
                        <input id="nome" type="text" minLength={2} maxLength={100} value={nome} onChange={(evento) => setNome(evento.target.value)} required />
                    </div>
                    {erro && <p className="propriedade-form-error" role="alert">{erro}</p>}
                    <div className="propriedade-form-actions">
                        <button className="propriedade-form-cancel" type="button" onClick={() => navigate(caminhoDepositos)} disabled={enviando}>Cancelar</button>
                        <button className="propriedade-form-submit" type="submit" disabled={enviando || nome.trim().length < 2}>{enviando ? "Salvando..." : "Salvar alterações"}</button>
                    </div>
                </form>
            </section>
        </main>
    );
}
