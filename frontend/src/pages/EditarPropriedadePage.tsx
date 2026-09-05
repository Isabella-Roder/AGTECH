import { useEffect, useState, type FormEvent } from "react"
import { useNavigate, useParams } from "react-router-dom";
import { atualizarPropriedade, buscarPropriedadePorId } from "../api/propriedades";
import "../styles/propriedade-form.css"

export function EditarPropriedadePage() {
    const [nome, setNome] = useState("");
    const [municipio, setMunicipio] = useState("");
    const [estado, setEstado] = useState("");
    const [areaTotalHectares, setAreaTotalHectares] = useState("");
    
    const [carregando, setCarregando] = useState(true);
    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    const { id } = useParams();
    const navigate = useNavigate();

    const propriedadeId = id ?? "";
    const idValido = propriedadeId.length > 0;

    useEffect(() => {
        let componenteAtivo = true;

        if (!idValido) {
            return;
        }

        buscarPropriedadePorId(propriedadeId).then((propriedade) => {
            if (!componenteAtivo) {
                return;
            }

            setNome(propriedade.nome);
            setMunicipio(propriedade.municipio);
            setEstado(propriedade.estado);
            setAreaTotalHectares(String(propriedade.areaTotalHectares));
        })
        .catch((erroRecebido) => {
            if (componenteAtivo) {
                setErro(
                    erroRecebido instanceof Error
                        ? erroRecebido.message
                        : "Não foi possivel carregar a propriedade."
                );
            }
        })
        .finally(() => {
            if (componenteAtivo) {
                setCarregando(false)
            }
        });

        return () => {
            componenteAtivo = false;
        };
    }, [idValido, propriedadeId]);

    async function handleSubmit(e:FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setErro(null);

        const areaConvertida = Number(areaTotalHectares);

        if (!idValido) {
            setErro("Identificador da propriedade inválido.");
            return;
        }

        if (!Number.isFinite(areaConvertida) || areaConvertida <= 0) {
            setErro("Informe uma área total maior que zero.");
            return;
        }

        try {
            setEnviando(true);

            await atualizarPropriedade(propriedadeId, {
                nome: nome.trim(),
                municipio: municipio.trim(),
                estado: estado.trim(),
                areaTotalHectares: areaConvertida
            });

            navigate(`/propriedades/${propriedadeId}`, {
                replace: true,
                state: {
                    mensagem: "Propriedade atualizada com sucesso.",
                },
            });
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possivel atualizar propriedade.",
            );
        } finally {
            setEnviando(false);
        }
    }

    function cancelar() {
        if (idValido) {
            navigate(`/propriedades/${propriedadeId}`);
            return;
        }

        navigate("/propriedades");
    }

    if (!idValido) {
        return (
            <main className="propriedade-form-page">
                <div role="alert" className="property-details-feedback property-details-feedback--error">
                    <h1>Identificador inválido</h1>
                    <p>Não foi possivel identificar a propriedade.</p>

                    <button type="button" onClick={() => navigate("/propriedades")}>
                        Voltar
                    </button>
                </div>
            </main>
        )
    }

    if (carregando) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback" role="status">
                    Carregando propriedade...
                </div>
            </main>
        );
    }

    if (erro && !nome) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback property-details-feedback--error " role="alert">
                    <h1>Não foi possivel editar a propriedade</h1>
                    <p>{erro}</p>

                    <button type="button" onClick={cancelar}>
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
                        Configurações
                    </span>

                    <h1>Editar propriedade</h1>

                    <p>
                        Atualize os dados gerais da propriedade rural.
                    </p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome da propriedade</label>
                        <input id="nome" name="nome" type="text" value={nome} onChange={(e) => setNome(e.target.value)} minLength={3} maxLength={80} autoComplete="organization" required />
                    </div>

                    <div className="propriedade-form-row">
                        <div className="propriedade-form-field">
                            <label htmlFor="municipio">
                                Município
                            </label>
                            <input id="municipio" name="municipio" type="text" value={municipio} onChange={(e) => setMunicipio(e.target.value)} maxLength={80} autoComplete="address-level2" required />
                        </div>

                        <div className="propriedade-form-field">
                            <label htmlFor="estado">
                                Estado
                            </label>
                            <input id="estado" name="estado" type="text" value={estado} onChange={(e) => setEstado(e.target.value)} maxLength={80} autoComplete="address-level1" required />
                        </div>
                    </div>

                    <div className="propriedade-form-field">
                        <label htmlFor="areaTotalHectares">
                            Área total
                        </label>

                        <div className="propriedade-form-area">
                            <input id="areaTotalHectares" name="areaTotalHectares" type="number" value={areaTotalHectares} onChange={(e) => setAreaTotalHectares(e.target.value)} min="0.01" step="0.01" inputMode="decimal" required />
                            <span>hectares</span>
                        </div>
                    </div>

                    {erro && (
                        <p className="propriedade-form-error" role="alert">{erro}</p>
                    )}

                    <div className="propriedade-form-actions">
                        <button type="button" className="propriedade-form-cancel" onClick={cancelar} disabled={enviando}>
                            Cancelar
                        </button>

                        <button type="submit" className="propriedade-form-submit" disabled={enviando}>
                            {enviando ? "Salvando..." : "Salvar alterações"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    )
}
