import React, { useState } from "react"
import { useNavigate, useParams } from "react-router-dom";
import { cadastrarTalhao } from "../api/talhoes";
import "../styles/propriedade-form.css"

export function CadastroTalhaoPage() {
    
    const [nome, setNome] = useState("");
    const [areaHectares, setAreaHectares] = useState("");
    const [erro, setErro] = useState<string | null>(null);
    const [enviando, setEnviando] = useState(false);

    const {propriedadeId} = useParams();
    const navigate = useNavigate();

    const id = Number(propriedadeId);
    const idValido = Number.isInteger(id) && id > 0;

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();

        setErro(null);

        const areaConvertida = Number(areaHectares);

        if (!idValido) {
            setErro("Identificador da propriedade inválido.");
            return;
        }

        if (!Number.isFinite(areaConvertida) || areaConvertida <= 0) {
            setErro("Informe uma área maior que zero.");
            return;
        }

        try {
            setEnviando(true);

            await cadastrarTalhao(id, {
                nome: nome.trim(),
                areaHectares: areaConvertida
            });

            navigate(`/propriedades/${id}`, {
                replace: true,
                state: {
                    mensagem: "Talhao cadastrado com sucesso."
                }
            });
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possivel cadastrar o talhão."
            );
        } finally {
            setEnviando(false);
        }
    }

    function cancelar() {
        navigate(idValido ? `/propriedades/${id}` : "/propriedades")
    }

    if (!idValido) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback property-details-feedback--error" role="alert">
                    <h1>Propriedade inválida</h1>
                    <p>Não foi possivel identificar a propriedade.</p>

                    <button type="button" onClick={cancelar}>Voltar</button>
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

                    <h1>Novo talhão</h1>

                    <p>
                        Cadastre uma área produtiva dentro da propriedade.
                    </p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome do talhão</label>
                        <input type="text" id="nome" name="nome" value={nome} onChange={(e) => setNome(e.target.value)} minLength={3} maxLength={80} placeholder="Ex.: Talhão Norte" required/>
                    </div>

                    <div className="propriedade-form-field">
                        <label htmlFor="areaHectares">Área do talhão</label>
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
                        <button type="button" className="propriedade-form-cancel" onClick={cancelar} disabled={enviando}>
                            Cancelar
                        </button>

                        <button type="submit" className="propriedade-form-submit" disabled={enviando}>
                            {enviando ? "Cadastrando..." : "Cadastrar talhão"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    )
}