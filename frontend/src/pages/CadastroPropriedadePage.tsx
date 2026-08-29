import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { cadastrarPropriedade } from "../api/propriedades";
import "../styles/propriedade-form.css"

export function CadastroPropriedadePage() {
    const [nome, setNome] = useState("");
    const [municipio, setMunicipio] = useState("");
    const [estado, setEstado] = useState("");
    const [areaTotalHectares, setAreaTotalHectares] = useState("");
    const [erro, setErro] = useState<string | null>(null);
    const [enviado, setEnviado] = useState(false);

    const navigate = useNavigate();

    async function handleSubmit(e: FormEvent<HTMLFormElement>,) {
        e.preventDefault();
        setErro(null);

        const areaConvertida = Number(areaTotalHectares);

        if (!Number.isFinite(areaConvertida) || areaConvertida <= 0) {
            setErro("Informe uma área total maior que zero.");
            return;
        }

        try {
            setEnviado(true);

            await cadastrarPropriedade({
                nome: nome.trim(),
                municipio: municipio.trim(),
                estado: estado.trim(),
                areaTotalHectares: areaConvertida,
            });

            navigate("/propriedades", {
                replace: true,
                state: {
                    mensagem: "Propriedade cadastrada com sucesso.",
                },
            });
        } catch (erroRecebido) {
            setErro(erroRecebido instanceof Error ? erroRecebido.message : "Não foi possivel cadastrar a propriedade.");
        } finally {
            setEnviado(false);
        }
    }

    return (
        <main className="propriedade-form-page">
            <section className="propriedade-form-card">
                <header className="propriedade-form-header">
                    <span className="propriedade-form-eyebrow">
                        Nova propriedade
                    </span>

                    <h1>Cadastrar propriedade rural</h1>

                    <p>Informe os dados gerais da propriedade rural.</p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome da propriedade</label>
                        <input id="nome" name="nome" type="text" value={nome} onChange={(e) => setNome(e.target.value)} minLength={3} maxLength={80} autoComplete="organization" required />
                    </div>

                    <div className="propriedade-form-row">
                        <div className="propriedade-form-field">
                            <label htmlFor="municipal">Município</label>
                            <input id="municipio" name="municipio" type="text" value={municipio} onChange={(e) => setMunicipio(e.target.value)} maxLength={80} autoComplete="address-level2" required />
                        </div>

                        <div className="propriedade-form-field">
                            <label htmlFor="estado">Estado</label>
                            <input id="estado" name="estado" type="text" value={estado} onChange={(e) => setEstado(e.target.value)} maxLength={80} autoComplete="address-level1" required />
                        </div>

                        <div className="propriedade-form-field">
                            <label htmlFor="areaTotalHectares">Área total</label>

                            <div className="propriedade-form-area">
                                <input id="areaTotalHectares" name="areaTotalHectares" type="number" value={areaTotalHectares} onChange={(e) => setAreaTotalHectares(e.target.value)} min="0.01" step="0.01" inputMode="decimal" required />
                                <span>hectares</span>
                            </div>
                        </div>

                        {erro && (
                            <p className="propriedade-form-error" role="alert">{erro}</p>
                        )}

                        <div className="propriedade-form-actions">
                            <button className="propriedade-form-cancel" type="button" onClick={() => navigate("/propriedades")} disabled={enviado}>
                                Cancelar
                            </button>

                            <button className="propriedade-form-submit" type="submit" disabled={enviado}>
                                {enviado ? "Cadastrado..." : "Cadastrar propriedade"}
                            </button>
                        </div>
                    </div>
                </form>
            </section>
        </main>
    )
}