import React, { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom"
import { cadastrarMaquina, type CadastroMaquina, type TipoMaquina } from "../api/maquinas";
import "../styles/maquinas.css";

export function CadastroMaquinaPage() {

    const { propriedadeId } = useParams<{ propriedadeId: string }>();
    const navigate = useNavigate();

    const [identificador, setIdentificador] = useState("");
    const [tipo, setTipo] = useState<TipoMaquina>("TRATOR");
    const [horimetroAtual, setHorimetroAtual] = useState("");
    const [erro, setErro] = useState("");
    const [salvando, setSalvando] = useState(false);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();

        if (!propriedadeId) {
            setErro("Propriedade não identificada.");
            return;
        }

        const identificadorTratado = identificador.trim();
        const horimetro = Number(horimetroAtual.replace(",", "."));

        if (!identificadorTratado) {
            setErro("Informe o identificador da máquina.");
            return;
        }

        if (identificadorTratado.length > 100) {
            setErro("O identificador deve ter no máximo 100 caracteres.");
            return;
        }

        if (!Number.isFinite(horimetro) || horimetro <= 0) {
            setErro("O horimetro deve ser um número maior que zero.");
            return;
        }

        const dados: CadastroMaquina = {
            identificador: identificadorTratado,
            tipo,
            horimetroAtual: horimetro
        };

        try {
            setSalvando(true);
            setErro("");

            await cadastrarMaquina(propriedadeId, dados);

            navigate(`/propriedades/${propriedadeId}/maquinas`);
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : "Não foi possível cadastrar a máquina."
            );
        } finally {
            setSalvando(false);
        }
    }

    if (!propriedadeId) {
        return (
            <main className="maquinas-page">
                <div className="maquinas-alert maquinas-alert--erro">
                    Propriedade não encontrada.
                </div>
            </main>
        )
    }

    return (
        <main className="maquinas-page">
            <section className="maquinas-hero maquinas-hero--form">
                <div>
                    <span className="maquinas-eyebrow">
                        Gestão de equipamentos
                    </span>

                    <h1>Nova máquina</h1>

                    <p>Cadastre um equipamento e informe seu horímetro inicial.</p>
                </div>

                <Link className="maquinas-button maquinas-button--hero-secondary" to={`/propriedades/${propriedadeId}/maquinas`}>
                    Voltar
                </Link>
            </section>

            <section className="maquina-form-container">
                <div className="maquina-form-heading">
                    <span>Informações do equipamento</span>
                    <h2>Dados da máquina</h2>
                    <p>
                        Os dados poderão ser utilizados nos registros de manutenção e abastecimento.
                    </p>
                </div>

                {erro && (
                    <div className="maquinas-alert maquinas-alert--erro">
                        {erro}
                    </div>
                )}

                <form className="maquina-form" onSubmit={handleSubmit}>
                    <label className="maquina-field maquina-field--full">
                        <span>Identificador</span>
                        <input type="text" value={identificador} maxLength={100} placeholder="Ex.: Trator John Deere 5075E" disabled={salvando} onChange={(e) => setIdentificador(e.target.value)} required />
                        <small>
                            Nome, código interno ou placa usada para identificador o equipamento.
                        </small>
                    </label>

                    <label className="maquina-field">
                        <span>Tipo da máquina</span>

                        <select value={tipo} disabled={salvando} onChange={(e) => setTipo(e.target.value as TipoMaquina)} required>
                            <option value="TRATOR">Trator</option>
                            <option value="COLHEITADEIRA">Colheitadeira</option>
                            <option value="PULVERIZADOR">Pulverizador</option>
                            <option value="IMPLEMENTO">Implemento</option>
                        </select>
                    </label>

                    <label className="maquina-field">
                        <span>Horímetro atual</span>

                        <div className="maquina-input-suffix">
                            <input type="number" value={horimetroAtual} min="0.1" step="0.1" inputMode="decimal" placeholder="Ex.: 1250,5" disabled={salvando} onChange={(e) => setHorimetroAtual(e.target.value)} required />

                            <span>h</span>
                        </div>

                        <small>
                            Informe as horas registradas atualmente no equipamento.
                        </small>
                    </label>

                    <div className="maquina-form-actions">
                        <Link className="maquinas-button maquinas-button--secondary" to={`/propriedades/${propriedadeId}/maquinas`}>
                            Cancelar
                        </Link>

                        <button type="submit" className="maquinas-button maquinas-button--primary" disabled={salvando}>
                            {salvando ? "Cadastrando..." : "Cadastrar máquina"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    )
}