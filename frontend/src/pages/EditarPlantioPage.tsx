import { useEffect, useState, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { atualizarPlantio, buscarPlantioPorId } from "../api/plantios";
import "../styles/propriedade-form.css";

export function EditarPlantioPage() {
    const [dataPlantio, setDataPlantio] = useState("");
    const [areaPlantadaHectares, setAreaPlantadaHectares] = useState("");
    const [observacoes, setObservacoes] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);
    const { propriedadeId = "", talhaoId = "", safraId = "", plantioId = "" } = useParams();
    const navigate = useNavigate();
    const idsValidos = Boolean(propriedadeId && talhaoId && safraId && plantioId);
    const caminhoPlantios = `/propriedades/${propriedadeId}/talhoes/${talhaoId}/safras/${safraId}/plantios`;

    useEffect(() => {
        let paginaAtiva = true;
        if (!idsValidos) return;

        buscarPlantioPorId(propriedadeId, talhaoId, safraId, plantioId)
            .then((plantio) => {
                if (paginaAtiva) {
                    setDataPlantio(plantio.dataPlantio);
                    setAreaPlantadaHectares(String(plantio.areaPlantadaHectares));
                    setObservacoes(plantio.observacoes ?? "");
                }
            })
            .catch((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(erroRecebido instanceof Error
                        ? erroRecebido.message
                        : "Não foi possível carregar o plantio.");
                }
            })
            .finally(() => {
                if (paginaAtiva) setCarregando(false);
            });

        return () => { paginaAtiva = false; };
    }, [idsValidos, plantioId, propriedadeId, safraId, talhaoId]);

    async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
        evento.preventDefault();
        setErro(null);
        const areaConvertida = Number(areaPlantadaHectares);

        if (!Number.isFinite(areaConvertida) || areaConvertida <= 0) {
            setErro("Informe uma área plantada maior que zero.");
            return;
        }

        try {
            setEnviando(true);
            await atualizarPlantio(propriedadeId, talhaoId, safraId, plantioId, {
                dataPlantio,
                areaPlantadaHectares: areaConvertida,
                observacoes: observacoes.trim() || null,
            });
            navigate(caminhoPlantios, {
                replace: true,
                state: { mensagem: "Plantio atualizado com sucesso." },
            });
        } catch (erroRecebido) {
            setErro(erroRecebido instanceof Error
                ? erroRecebido.message
                : "Não foi possível atualizar o plantio.");
        } finally {
            setEnviando(false);
        }
    }

    if (!idsValidos) {
        return <main className="propriedade-form-page"><div className="property-details-feedback property-details-feedback--error" role="alert">Não foi possível identificar o plantio.</div></main>;
    }

    if (carregando) {
        return <main className="propriedade-form-page"><div className="property-details-feedback" role="status">Carregando plantio...</div></main>;
    }

    return (
        <main className="propriedade-form-page">
            <section className="propriedade-form-card">
                <header className="propriedade-form-header">
                    <span className="propriedade-form-eyebrow">Operação agrícola</span>
                    <h1>Editar plantio</h1>
                    <p>Atualize a data, a área plantada e as observações.</p>
                </header>
                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-row">
                        <div className="propriedade-form-field">
                            <label htmlFor="dataPlantio">Data do plantio</label>
                            <input id="dataPlantio" type="date" value={dataPlantio} onChange={(evento) => setDataPlantio(evento.target.value)} required />
                        </div>
                        <div className="propriedade-form-field">
                            <label htmlFor="areaPlantadaHectares">Área plantada</label>
                            <div className="propriedade-form-area">
                                <input id="areaPlantadaHectares" type="number" min="0.01" step="0.01" inputMode="decimal" value={areaPlantadaHectares} onChange={(evento) => setAreaPlantadaHectares(evento.target.value)} required />
                                <span>hectares</span>
                            </div>
                        </div>
                    </div>
                    <div className="propriedade-form-field">
                        <label htmlFor="observacoes">Observações</label>
                        <textarea id="observacoes" rows={5} maxLength={500} value={observacoes} onChange={(evento) => setObservacoes(evento.target.value)} />
                        <small>{observacoes.length}/500 caracteres</small>
                    </div>
                    {erro && <p className="propriedade-form-error" role="alert">{erro}</p>}
                    <div className="propriedade-form-actions">
                        <button className="propriedade-form-cancel" type="button" onClick={() => navigate(caminhoPlantios)} disabled={enviando}>Cancelar</button>
                        <button className="propriedade-form-submit" type="submit" disabled={enviando}>{enviando ? "Salvando..." : "Salvar alterações"}</button>
                    </div>
                </form>
            </section>
        </main>
    );
}
