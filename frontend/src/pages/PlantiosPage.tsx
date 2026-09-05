import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { listarPlantio, type Plantio } from "../api/plantios";
import { buscarSafraPorId, type Safra } from "../api/safra";
import "../styles/plantios.css";

export function PlantiosPage() {
    const [plantios, setPlantios] = useState<Plantio[]>([]);
    const [safra, setSafra] = useState<Safra | null>(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState<string | null>(null);
    const [tentativa, setTentativa] = useState(0);
    const { propriedadeId = "", talhaoId = "", safraId = "" } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const idsValidos = Boolean(propriedadeId && talhaoId && safraId);
    const mensagem = (location.state as { mensagem?: string } | null)?.mensagem;
    const caminhoSafras = `/propriedades/${propriedadeId}/talhoes/${talhaoId}/safras`;
    const caminhoNovoPlantio = `${caminhoSafras}/${safraId}/plantios/novo`;

    useEffect(() => {
        let paginaAtiva = true;
        if (!idsValidos) return;

        Promise.all([
            listarPlantio(propriedadeId, talhaoId, safraId),
            buscarSafraPorId(propriedadeId, talhaoId, safraId),
        ])
            .then(([plantiosRecebidos, safraRecebida]) => {
                if (paginaAtiva) {
                    setPlantios(plantiosRecebidos);
                    setSafra(safraRecebida);
                    setErro(null);
                }
            })
            .catch((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(erroRecebido instanceof Error
                        ? erroRecebido.message
                        : "Não foi possível carregar os plantios.");
                }
            })
            .finally(() => {
                if (paginaAtiva) setCarregando(false);
            });

        return () => { paginaAtiva = false; };
    }, [idsValidos, propriedadeId, talhaoId, safraId, tentativa]);

    function formatarData(data: string) {
        return new Intl.DateTimeFormat("pt-BR", { timeZone: "UTC" })
            .format(new Date(`${data}T00:00:00Z`));
    }

    if (!idsValidos) {
        return <main className="plantios-page"><div className="page-feedback page-feedback--error" role="alert">Não foi possível identificar a safra.</div></main>;
    }

    return (
        <main className="plantios-page">
            <button className="property-details-back" type="button" onClick={() => navigate(caminhoSafras)}>
                <span aria-hidden="true">←</span> Voltar para as safras
            </button>

            <header className="page-header">
                <div>
                    <span className="page-eyebrow">Operação agrícola</span>
                    <h1>{safra?.nome ?? "Plantios da safra"}</h1>
                    <p>Acompanhe as áreas e datas registradas neste ciclo produtivo.</p>
                </div>
                {safra?.status === "EM_ANDAMENTO" && (
                    <button className="primary-button" type="button" onClick={() => navigate(caminhoNovoPlantio)}>
                        <span aria-hidden="true">+</span> Novo plantio
                    </button>
                )}
            </header>

            {mensagem && <p className="page-message page-message--success" role="status">{mensagem}</p>}

            <section className="plantios-section">
                {carregando && <div className="page-feedback" role="status">Carregando plantios...</div>}
                {erro && (
                    <div className="page-feedback page-feedback--error" role="alert">
                        <p>{erro}</p>
                        <button type="button" onClick={() => {
                            setCarregando(true);
                            setErro(null);
                            setTentativa((valor) => valor + 1);
                        }}>Tentar novamente</button>
                    </div>
                )}
                {!carregando && !erro && plantios.length === 0 && (
                    <div className="empty-state">
                        <div className="empty-state-icon" aria-hidden="true">+</div>
                        <h2>Nenhum plantio registrado</h2>
                        <p>Os registros de plantio desta safra aparecerão aqui.</p>
                        {safra?.status === "EM_ANDAMENTO" && (
                            <button className="primary-button" type="button" onClick={() => navigate(caminhoNovoPlantio)}>Registrar plantio</button>
                        )}
                    </div>
                )}
                {!carregando && !erro && plantios.length > 0 && (
                    <ul className="plantios-grid">
                        {plantios.map((plantio) => (
                            <li className="plantio-card" key={plantio.id}>
                                <header>
                                    <span>Data do plantio</span>
                                    <strong>{formatarData(plantio.dataPlantio)}</strong>
                                </header>
                                <div className="plantio-card-area">
                                    <span>Área plantada</span>
                                    <strong>{new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 2 }).format(plantio.areaPlantadaHectares)} <small>ha</small></strong>
                                </div>
                                <div className="plantio-card-notes">
                                    <span>Observações</span>
                                    <p>{plantio.observacoes || "Nenhuma observação informada."}</p>
                                </div>
                                {safra?.status === "EM_ANDAMENTO" && (
                                    <button
                                        className="plantio-card-edit"
                                        type="button"
                                        onClick={() => navigate(`${caminhoSafras}/${safraId}/plantios/${plantio.id}/editar`)}
                                    >
                                        Editar plantio <span aria-hidden="true">→</span>
                                    </button>
                                )}
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </main>
    );
}
