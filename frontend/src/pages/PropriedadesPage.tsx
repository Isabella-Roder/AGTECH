import { useEffect, useState } from "react";
import { listarMinhasPropriedades, type Propriedade } from "../api/propriedades";
import { removerToken } from "../api/cliente";
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

    function logout() {
        removerToken();
        navigate("/", {replace: true});
    }

    return (
        <main className="propriedades-page">
            <div className="propriedades-container">
                <header className="propriedades-header">
                    <div>
                        <h1>Minhas propriedades</h1>

                        <p className="propriedades-subtitle">
                            Consulte as propriedades rurais vinculadas à sua conta.
                        </p>
                    </div>

                    <div className="propriedade-actions">
                        <button className="nova-propriedade-button" type="button" onClick={() => navigate("/propriedades/nova")}>
                            + Nova propriedade
                        </button>

                        <button className="logout-button" type="button" onClick={logout}>
                            Sair
                        </button>
                    </div>
                </header>

                {mensagem && (
                    <p className="propriedades-sucesso" role="status">
                        {mensagem}
                    </p>
                )}

                {carregando && (
                    <p
                        className="propriedades-feedback"
                        role="status"
                    >
                        Carregando propriedades...
                    </p>
                )}

                {erro && (
                    <p
                        className="
                            propriedades-feedback
                            propriedades-feedback--erro
                        "
                        role="alert"
                    >
                        {erro}
                    </p>
                )}

                {!carregando && !erro && propriedades.length === 0 && (
                    <p className="propriedades-feedback">
                        Nenhuma propriedade encontrada.
                    </p>
                )}

                {!carregando && !erro && propriedades.length > 0 && (
                    <ul className="propriedades-grid">
                        {propriedades.map((propriedade) => (
                            <li
                                className="propriedade-card"
                                key={propriedade.id}
                            >
                                <h2>{propriedade.nome}</h2>

                                <p>
                                    <strong>Localização:</strong>{" "}
                                    {propriedade.municipio}/
                                    {propriedade.estado}
                                </p>

                                <p>
                                    <strong>Área total:</strong>{" "}
                                    {propriedade.areaTotalHectares} ha
                                </p>

                                <span
                                    className={
                                        propriedade.ativo
                                            ? "propriedade-status propriedade-status--ativa"
                                            : "propriedade-status propriedade-status--inativa"
                                    }
                                >
                                    {propriedade.ativo ? "Ativa" : "Inativa"}
                                </span>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </main>
    );
}