import { useEffect, useState } from "react";
import { listarMinhasPropriedades, type Propriedade } from "../api/propriedades";
import { useLocation, useNavigate } from "react-router-dom";
import "../styles/propriedades.css"

export function PropriedadesPage() {
    const [propriedades, setPropriedades] = useState<Propriedade[]>([]);
    const [erro, setErro] = useState<string | null>(null);
    const [carregando, setCarregando] = useState(true);

    const navigate = useNavigate();

    const location = useLocation();

    const mensagem = (
        location.state as {mensagem?: string} | null
    ) ?.mensagem;

    useEffect(() => {
        listarMinhasPropriedades()
            .then(setPropriedades)
            .catch((err) => setErro(err instanceof Error ? err.message : "Erro ao carregar"))
            .finally(() => setCarregando(false));
    }, []);

    const propriedadesAtivas = propriedades.filter((propriedade) => propriedade.ativo,).length;

    const areaTotal = propriedades.reduce(
        (total, propriedade) =>
            total + propriedade.areaTotalHectares, 0
    );

    const areaFormatada = new Intl.NumberFormat("pt-BR", {
        maximumFractionDigits: 2,
    }).format(areaTotal);

    return (
        <main className="propriedades-page">
            <header className="page-header">
                <div>
                    <span className="page-eyebrow">
                        Visão geral
                    </span>

                    <h1>Propriedades rurais</h1>

                    <p>
                        Acompanhe e administre as propriedades vinculadas
                        à sua conta.
                    </p>
                </div>

                <button
                    className="primary-button"
                    type="button"
                    onClick={() => navigate("/propriedades/nova")}
                >
                    <span aria-hidden="true">+</span>
                    Nova propriedade
                </button>
            </header>

            {mensagem && (
                <p
                    className="page-message page-message--success"
                    role="status"
                >
                    {mensagem}
                </p>
            )}

            {!carregando && !erro && (
                <section
                    className="property-summary"
                    aria-label="Resumo das propriedades"
                >
                    <article className="summary-card">
                        <span className="summary-card-label">
                            Total de propriedades
                        </span>

                        <strong>{propriedades.length}</strong>

                        <span className="summary-card-detail">
                            Cadastradas na plataforma
                        </span>
                    </article>

                    <article className="summary-card">
                        <span className="summary-card-label">
                            Propriedades ativas
                        </span>

                        <strong>{propriedadesAtivas}</strong>

                        <span className="summary-card-detail">
                            Em operação atualmente
                        </span>
                    </article>

                    <article className="summary-card">
                        <span className="summary-card-label">
                            Área administrada
                        </span>

                        <strong>
                            {areaFormatada}
                            <small> ha</small>
                        </strong>

                        <span className="summary-card-detail">
                            Soma das áreas cadastradas
                        </span>
                    </article>
                </section>
            )}

            <section className="property-section">
                <header className="property-section-header">
                    <div>
                        <h2>Suas propriedades</h2>

                        <p>
                            Selecione uma propriedade para consultar
                            seus dados.
                        </p>
                    </div>

                    {!carregando && !erro && (
                        <span className="property-count">
                            {propriedades.length}{" "}
                            {propriedades.length === 1
                                ? "propriedade"
                                : "propriedades"}
                        </span>
                    )}
                </header>

                {carregando && (
                    <div
                        className="page-feedback"
                        role="status"
                    >
                        Carregando propriedades...
                    </div>
                )}

                {erro && (
                    <div
                        className="page-feedback page-feedback--error"
                        role="alert"
                    >
                        {erro}
                    </div>
                )}

                {!carregando && !erro && propriedades.length === 0 && (
                    <div className="empty-state">
                        <div
                            className="empty-state-icon"
                            aria-hidden="true"
                        >
                            +
                        </div>

                        <h3>Nenhuma propriedade cadastrada</h3>

                        <p>
                            Cadastre sua primeira propriedade para começar
                            a gestão rural.
                        </p>

                        <button
                            className="primary-button"
                            type="button"
                            onClick={() =>
                                navigate("/propriedades/nova")
                            }
                        >
                            Cadastrar propriedade
                        </button>
                    </div>
                )}

                {!carregando && !erro && propriedades.length > 0 && (
                    <ul className="property-grid">
                        {propriedades.map((propriedade) => (
                            <li
                                className="property-card"
                                key={propriedade.id}
                            >
                                <header className="property-card-header">
                                    <div className="property-card-icon">
                                        {propriedade.nome
                                            .charAt(0)
                                            .toUpperCase()}
                                    </div>

                                    <span
                                        className={
                                            propriedade.ativo
                                                ? "property-status property-status--active"
                                                : "property-status property-status--inactive"
                                        }
                                    >
                                        {propriedade.ativo
                                            ? "Ativa"
                                            : "Inativa"}
                                    </span>
                                </header>

                                <h3>{propriedade.nome}</h3>

                                <div className="property-card-information">
                                    <p>
                                        <span>Localização</span>
                                        <strong>
                                            {propriedade.municipio}/
                                            {propriedade.estado}
                                        </strong>
                                    </p>

                                    <p>
                                        <span>Área total</span>
                                        <strong>
                                            {new Intl.NumberFormat(
                                                "pt-BR",
                                                {
                                                    maximumFractionDigits: 2,
                                                },
                                            ).format(
                                                propriedade.areaTotalHectares,
                                            )}{" "}
                                            ha
                                        </strong>
                                    </p>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </main>
    );
}