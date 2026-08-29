import { useEffect, useState } from "react";
import { listarMinhasPropriedades, type Propriedade } from "../api/propriedades";
import { removerToken } from "../api/cliente";
import { useNavigate } from "react-router-dom";

export function PropriedadesPage() {
    const [propriedades, setPropriedades] = useState<Propriedade[]>([]);
    const [erro, setErro] = useState<string | null>(null);
    const [carregando, setCarregando] = useState(true);

    const navigate = useNavigate();

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

    if (erro) return <p>{erro}</p>;

    return(
        <main>
            <header>
                <h1>Minhas propriedades</h1>
                <button type="button" onClick={logout}>
                    Sair
                </button>
            </header>

            {carregando && <p>Carregando propriedades...</p>}

            {erro && <p role="alert">{erro}</p>}

            {!carregando && !erro && propriedades.length === 0 && (
                <p>Nenhuma propriedade encontrada.</p>
            )}

            {!carregando && !erro && propriedades.length > 0 &&(
                <ul>
                    {propriedades.map((propriedade) => (
                        <li key={propriedade.id}>
                            {propriedade.nome} -{" "}
                            {propriedade.municipio}/
                            {propriedade.estado} -{" "}
                            {propriedade.areaTotalHectares} ha
                        </li>
                    ))}
                </ul>
            )}
        </main>
    );
}