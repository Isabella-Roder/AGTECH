import { useEffect, useState, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { listarCulturas, type Cultura } from "../api/cultura";
import { cadastrarSafra } from "../api/safra";
import "../styles/propriedade-form.css";

export function CadastroSafra() {
    const [nome, setNome] = useState("");
    const [culturaId, setCulturaId] = useState("");
    const [dataFimPrevista, setDataFimPrevista] = useState("");
    const [culturas, setCulturas] = useState<Cultura[]>([]);
    const [carregandoCulturas, setCarregandoCulturas] = useState(true);
    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    const { propriedadeId = "", talhaoId = "" } = useParams();
    const navigate = useNavigate();
    const idsValidos = propriedadeId.length > 0 && talhaoId.length > 0;

    useEffect(() => {
        let paginaAtiva = true;

        listarCulturas()
            .then((dados) => {
                if (paginaAtiva) {
                    setCulturas(dados);
                }
            })
            .catch((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(
                        erroRecebido instanceof Error
                            ? erroRecebido.message
                            : "Não foi possível carregar as culturas.",
                    );
                }
            })
            .finally(() => {
                if (paginaAtiva) {
                    setCarregandoCulturas(false);
                }
            });

        return () => {
            paginaAtiva = false;
        };
    }, []);

    function cancelar() {
        navigate(idsValidos ? `/propriedades/${propriedadeId}` : "/propriedades");
    }

    async function handleSubmit(evento: FormEvent<HTMLFormElement>) {
        evento.preventDefault();
        setErro(null);

        if (!idsValidos) {
            setErro("Não foi possível identificar a propriedade ou o talhão.");
            return;
        }

        if (!culturaId) {
            setErro("Selecione uma cultura.");
            return;
        }

        try {
            setEnviando(true);
            await cadastrarSafra(propriedadeId, talhaoId, {
                nome: nome.trim(),
                culturaId,
                dataFimPrevista,
            });

            navigate(`/propriedades/${propriedadeId}`, {
                replace: true,
                state: { mensagem: "Safra cadastrada com sucesso." },
            });
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possível cadastrar a safra.",
            );
        } finally {
            setEnviando(false);
        }
    }

    if (!idsValidos) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback property-details-feedback--error" role="alert">
                    <h1>Endereço inválido</h1>
                    <p>Não foi possível identificar a propriedade ou o talhão.</p>
                    <button type="button" onClick={cancelar}>Voltar</button>
                </div>
            </main>
        );
    }

    return (
        <main className="propriedade-form-page">
            <section className="propriedade-form-card">
                <header className="propriedade-form-header">
                    <span className="propriedade-form-eyebrow">Planejamento agrícola</span>
                    <h1>Nova safra</h1>
                    <p>Planeje um novo ciclo produtivo para este talhão.</p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome da safra</label>
                        <input
                            id="nome"
                            value={nome}
                            onChange={(evento) => setNome(evento.target.value)}
                            minLength={3}
                            maxLength={70}
                            placeholder="Ex.: Safra de soja 2026/2027"
                            required
                        />
                    </div>

                    <div className="propriedade-form-row">
                        <div className="propriedade-form-field">
                            <label htmlFor="culturaId">Cultura</label>
                            <select
                                id="culturaId"
                                value={culturaId}
                                onChange={(evento) => setCulturaId(evento.target.value)}
                                disabled={carregandoCulturas}
                                required
                            >
                                <option value="">
                                    {carregandoCulturas ? "Carregando..." : "Selecione"}
                                </option>
                                {culturas.map((cultura) => (
                                    <option key={cultura.id} value={cultura.id}>
                                        {cultura.nome}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="propriedade-form-field">
                            <label htmlFor="dataFimPrevista">Fim previsto</label>
                            <input
                                id="dataFimPrevista"
                                type="date"
                                value={dataFimPrevista}
                                onChange={(evento) => setDataFimPrevista(evento.target.value)}
                                required
                            />
                        </div>
                    </div>

                    {!carregandoCulturas && culturas.length === 0 && !erro && (
                        <p className="propriedade-form-error" role="alert">
                            Cadastre uma cultura antes de criar uma safra.
                        </p>
                    )}

                    {erro && <p className="propriedade-form-error" role="alert">{erro}</p>}

                    <div className="propriedade-form-actions">
                        <button className="propriedade-form-cancel" type="button" onClick={cancelar} disabled={enviando}>
                            Cancelar
                        </button>
                        <button
                            className="propriedade-form-submit"
                            type="submit"
                            disabled={enviando || carregandoCulturas || culturas.length === 0}
                        >
                            {enviando ? "Cadastrando..." : "Cadastrar safra"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    );
}
