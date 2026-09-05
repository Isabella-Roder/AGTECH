import React, { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { cadastrarPlantio } from "../api/plantios";
import "../styles/propriedade-form.css"


export function CadastroPlantioPage() {
    
    const [dataPlantio, setDataPlantio] = useState("");
    const [areaPlantadaHectares, setAreaPlantadaHectares] = useState("");
    const [observacoes, setObservacoes] = useState("");

    const [erro, setErro] = useState<string | null>(null);
    const [enviando, setEnviando] = useState(false);

    const {
        propriedadeId = "",
        talhaoId = "",
        safraId = "",
    } = useParams();

    const navigate = useNavigate();

    const idsValidos = Boolean(
        propriedadeId && talhaoId && safraId
    );

    function voltarParaSafras() {
        navigate(
            `/propriedades/${propriedadeId}/talhoes/${talhaoId}/safras`
        );
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setErro(null);

        if (!idsValidos) {
            setErro("Não foi possivel identificar a safra.");
            return;
        }

        const areaConvertida = Number(areaPlantadaHectares);

        if (!Number.isFinite(areaConvertida) || areaConvertida <= 0) {
            setErro("Informe uma área plantada maior que zero.");
            return;
        }

        try {
            setEnviando(true);

            await cadastrarPlantio(
                propriedadeId,
                talhaoId,
                safraId,
                {
                    dataPlantio,
                    areaPlantadaHectares: areaConvertida,
                    observacoes: observacoes.trim() || null
                }
            );

            navigate(
                `/propriedades/${propriedadeId}/talhoes/${talhaoId}/safras`, {
                    replace: true,
                    state: {
                        mensagem: "Plantio cadastrado com sucesso."
                    }
                }
            );
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possível cadastrar o plantio."
            );
        } finally {
            setEnviando(false);
        }
    }

    if (!idsValidos) {
        return (
            <main className="propriedade-form-page">
                <div
                    className="property-details-feedback property-details-feedback--error"
                    role="alert"
                >
                    <h1>Endereço inválido</h1>

                    <p>
                        Não foi possível identificar a safra.
                    </p>

                    <button
                        type="button"
                        onClick={() =>
                            navigate("/propriedades")
                        }
                    >
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
                        Operação agrícola
                    </span>

                    <h1>Novo plantio</h1>

                    <p>
                        Registre a data, a área utilizada e as
                        observações do plantio.
                    </p>
                </header>

                <form
                    className="propriedade-form"
                    onSubmit={handleSubmit}
                >
                    <div className="propriedade-form-row">
                        <div className="propriedade-form-field">
                            <label htmlFor="dataPlantio">
                                Data do plantio
                            </label>

                            <input
                                id="dataPlantio"
                                name="dataPlantio"
                                type="date"
                                value={dataPlantio}
                                onChange={(e) =>
                                    setDataPlantio(
                                        e.target.value,
                                    )
                                }
                                required
                            />
                        </div>

                        <div className="propriedade-form-field">
                            <label htmlFor="areaPlantadaHectares">
                                Área plantada
                            </label>

                            <div className="propriedade-form-area">
                                <input
                                    id="areaPlantadaHectares"
                                    name="areaPlantadaHectares"
                                    type="number"
                                    value={areaPlantadaHectares}
                                    onChange={(evento) =>
                                        setAreaPlantadaHectares(
                                            evento.target.value,
                                        )
                                    }
                                    min="0.01"
                                    step="0.01"
                                    inputMode="decimal"
                                    required
                                />

                                <span>hectares</span>
                            </div>
                        </div>
                    </div>

                    <div className="propriedade-form-field">
                        <label htmlFor="observacoes">
                            Observações
                        </label>

                        <textarea
                            id="observacoes"
                            name="observacoes"
                            value={observacoes}
                            onChange={(evento) =>
                                setObservacoes(
                                    evento.target.value,
                                )
                            }
                            maxLength={500}
                            rows={5}
                            placeholder="Informações adicionais sobre o plantio..."
                        />

                        <small>
                            {observacoes.length}/500 caracteres
                        </small>
                    </div>

                    {erro && (
                        <p
                            className="propriedade-form-error"
                            role="alert"
                        >
                            {erro}
                        </p>
                    )}

                    <div className="propriedade-form-actions">
                        <button
                            className="propriedade-form-cancel"
                            type="button"
                            onClick={voltarParaSafras}
                            disabled={enviando}
                        >
                            Cancelar
                        </button>

                        <button
                            className="propriedade-form-submit"
                            type="submit"
                            disabled={enviando}
                        >
                            {enviando
                                ? "Cadastrando..."
                                : "Cadastrar plantio"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    );
}
