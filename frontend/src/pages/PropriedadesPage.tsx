import { useEffect, useState } from "react";
import { listarMinhasPropriedades, type Propriedade } from "../api/propriedades";

export function PropriedadesPage() {
    const [propriedades, setPropriedades] = useState<Propriedade[]>([]);
    const [erro, setErro] = useState<string | null>(null);

    useEffect(() => {
        listarMinhasPropriedades()
            .then(setPropriedades)
            .catch((err) => setErro(err instanceof Error ? err.message : "Erro ao carregar"));
    }, []);

    if (erro) return <p>{erro}</p>;

    return(
        <ul>
            {propriedades.map((p) => (
                <li key={p.id}>{p.nome} - {p.municipio}/{p.estado} - {p.areaTotalHectares} ha</li>
            ))}
        </ul>
    );
}