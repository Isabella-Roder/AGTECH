import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { listarCulturas, type Cultura } from "../api/cultura";
import "../styles/culturas.css";

export function CulturasPage() {
    const [culturas, setCulturas] = useState<Cultura[]>([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState<string | null>(null);
    const [tentativa, setTentativa] = useState(0);
    const navigate = useNavigate();
    const location = useLocation();
    const mensagem = (location.state as { mensagem?: string } | null)?.mensagem;

    useEffect(() => {
        let paginaAtiva = true;

        listarCulturas()
            .then((dados) => {
                if (paginaAtiva) {
                    setCulturas(dados);
                    setErro(null);
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
                    setCarregando(false);
                }
            });

        return () => {
            paginaAtiva = false;
        };
    }, [tentativa]);

    function tentarNovamente() {
        setCarregando(true);
        setErro(null);
        setTentativa((valor) => valor + 1);
    }

    return (
        <main className="culturas-page">
            <header className="page-header">
                <div>
                    <span className="page-eyebrow">Catálogo agrícola</span>
                    <h1>Culturas</h1>
                    <p>
                        Organize as culturas que serão utilizadas em safras e
                        plantios.
                    </p>
                </div>
                <button
                    className="primary-button"
                    type="button"
                    onClick={() => navigate("/culturas/nova")}
                >
                    <span aria-hidden="true">+</span>
                    Nova cultura
                </button>
            </header>

            {mensagem && (
                <p className="page-message page-message--success" role="status">
                    {mensagem}
                </p>
            )}

            <section className="culturas-section">
                <header className="culturas-section-header">
                    <div>
                        <h2>Catálogo de culturas</h2>
                        <p>Culturas disponíveis para o planejamento rural.</p>
                    </div>
                    {!carregando && !erro && (
                        <span className="property-count">
                            {culturas.length} {culturas.length === 1 ? "cultura" : "culturas"}
                        </span>
                    )}
                </header>

                {carregando && (
                    <div className="page-feedback" role="status">
                        Carregando culturas...
                    </div>
                )}

                {erro && (
                    <div className="page-feedback page-feedback--error" role="alert">
                        <p>{erro}</p>
                        <button type="button" onClick={tentarNovamente}>
                            Tentar novamente
                        </button>
                    </div>
                )}

                {!carregando && !erro && culturas.length === 0 && (
                    <div className="empty-state">
                        <div className="empty-state-icon" aria-hidden="true">+</div>
                        <h3>Nenhuma cultura cadastrada</h3>
                        <p>Cadastre a primeira cultura do catálogo agrícola.</p>
                        <button
                            className="primary-button"
                            type="button"
                            onClick={() => navigate("/culturas/nova")}
                        >
                            Cadastrar cultura
                        </button>
                    </div>
                )}

                {!carregando && !erro && culturas.length > 0 && (
                    <ul className="culturas-grid">
                        {culturas.map((cultura) => (
                            <li className="cultura-card" key={cultura.id}>
                                <div className="cultura-card-icon" aria-hidden="true">
                                    {cultura.nome.charAt(0).toUpperCase()}
                                </div>
                                <div>
                                    <span>Cultura agrícola</span>
                                    <h3>{cultura.nome}</h3>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </main>
    );
}
