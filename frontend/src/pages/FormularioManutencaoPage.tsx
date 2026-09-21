import { Link, useNavigate, useParams } from "react-router-dom";
import "../styles/manutencoes.css";
import React, { useEffect, useState } from "react";
import { atualizarManutencao, buscarManutencaoPorId, cadastroManutencao, type CadastroManutencao, type TipoManutencao } from "../api/manutencoes";

export function FormularioManutencaoPage() {

    const { propriedadeId, maquinaId, manutencaoId } = useParams<{
        propriedadeId: string;
        maquinaId: string;
        manutencaoId: string;
    }>();

    const navigate = useNavigate();
    const editando = Boolean(manutencaoId);

    const [tipo, setTipo] = useState<TipoManutencao>("PREVENTIVA");
    const [dataRealizacao, setDataRealizacao] = useState("");
    const [horimetroNoMomento, setHorimetroNoMomento] = useState("");
    const [descricao, setDescricao] = useState("");
    const [carregando, setCarregando] = useState(editando);
    const [salvando, setSalvando] = useState(false);
    const [erro, setErro] = useState("");

    useEffect(() => {
        if (
            !editando ||
            !propriedadeId ||
            !maquinaId ||
            !manutencaoId
        ) {
            setCarregando(false);
            return;
        }

        async function carregarManutencao() {
            try {
                setErro("");

                const manutencao = await buscarManutencaoPorId(
                    propriedadeId!,
                    maquinaId!,
                    manutencaoId!
                );

                setTipo(manutencao.tipo);
                setDataRealizacao(manutencao.dataRealizacao);
                setHorimetroNoMomento(String(manutencao.horimetroNoMomento));
                setDescricao(manutencao.descricao ?? "");
            } catch (error) {
                setErro(
                    error instanceof Error
                        ? error.message
                        : "Não foi possivel carregar a manutenção."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregarManutencao();
    }, [editando, propriedadeId, maquinaId, manutencaoId]);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();

        if (!propriedadeId || !maquinaId) {
            setErro("Máquina não identificada.");
            return;
        }

        const horimetro = Number(horimetroNoMomento.replace(",", "."));

        if (!dataRealizacao) {
            setErro("Informe a data de realização.")
            return;
        }

        if (!Number.isFinite(horimetro) || horimetro <= 0) {
            setErro("O horímetro deve ser maior que zero.");
            return;
        }

        const descricaoTratada = descricao.trim();

        if (descricaoTratada.length > 500) {
            setErro("A descrição deve ter no máximo 500 caracteres.");
            return;
        }

        const dados: CadastroManutencao = {
            tipo,
            dataRealizacao,
            horimetroNoMomento: horimetro,
            descricao: descricaoTratada || null
        };

        try {
            setSalvando(true);
            setErro("");

            if (editando && manutencaoId) {
                await atualizarManutencao(
                    propriedadeId,
                    maquinaId,
                    manutencaoId,
                    dados
                );
            } else {
                await cadastroManutencao(
                    propriedadeId,
                    maquinaId,
                    dados
                );
            }

            navigate(`/propriedades/${propriedadeId}/maquinas/${maquinaId}/manutencoes`);
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : "Não foi possivel salvar a manutenção."
            );
        } finally {
            setSalvando(false);
        }
    }

    const caminhoVoltar = propriedadeId && maquinaId
        ? `/propriedades/${propriedadeId}/maquinas/${maquinaId}/manutencoes`
        : "/propriedades";

    if (!propriedadeId || !maquinaId) {
        return (
            <main className="manutencoes-page">
                <div className="manutencoes-alert manutencoes-alert--erro">
                    Máquina não identificada.
                </div>
            </main>
        );
    }

    if (carregando) {
        return (
            <main className="manutencoes-page">
                <div className="manutencoes-state">
                    <div className="manutencoes-spinner" />
                    <p>Carregando manutenção...</p>
                </div>
            </main>
        );
    }

    return (
        <main className="manutencoes-page">
            <section className="manutencoes-hero">
                <div>
                    <span className="manutencoes-eyebrow">
                        Histórico da máquina
                    </span>

                    <h1>
                        {editando ? "Editar manutenção" : "Nova manutenção"}
                    </h1>

                    <p>
                        Registre o serviço e o horímetro apresentado pelo equipamento.
                    </p>
                </div>

                <Link className="manutencoes-button manutencoes-button--ghost" to={caminhoVoltar}>
                    Voltar
                </Link>
            </section>

            <section className="manutencao-form-container">
                <div className="manutencao-form-heading">
                    <span>Informações do serviço</span>
                    <h2>Dados da manutenção</h2>
                    <p>
                        Preencha os dados conforme o serviço realizado.
                    </p>
                </div>

                {erro && (
                    <div className="manutencoes-alert manutencoes-alert--erro">
                        {erro}
                    </div>
                )}

                <form className="manutencao-form" onSubmit={handleSubmit}>
                    <label className="manutencao-field">
                        <span>Tipo de manutenção</span>

                        <select value={tipo} disabled={salvando} onChange={(e) => setTipo(e.target.value as TipoManutencao)}>
                            <option value="PREVENTIVA">Preventiva</option>
                            <option value="CORRETIVA">Corretiva</option>
                        </select>
                    </label>

                    <label className="manutencao-field">
                        <span>Data de realização</span>

                        <input type="date" value={dataRealizacao} disabled={salvando} onChange={(e) => setDataRealizacao(e.target.value)} required />
                    </label>

                    <label className="manutencao-field manutencao-field--full">
                        <span>Horímetro no momento</span>

                        <div className="manutencao-input-suffix">
                            <input type="number" value={horimetroNoMomento} min="0.1" step="0.1" inputMode="decimal" placeholder="Ex.: 1280,5" disabled={salvando} onChange={(e) => setHorimetroNoMomento(e.target.value)} required />
                            <span>h</span>
                        </div>

                        <small>
                            Informe o valor exibido no horímetro quando a manutenção foi realizada.
                        </small>
                    </label>

                    <label className="manutencao-field manutencao-field--full">
                        <span>Descrição</span>

                        <textarea value={descricao} maxLength={500} rows={6} placeholder="Descreva o serviço realizado, peças substituídas e observações..." disabled={salvando} onChange={(e) => setDescricao(e.target.value)}/>

                            <small>
                                {descricao.length}/500 caracteres
                            </small>
                    </label>

                    <div className="manutencao-form-actions">
                        <Link className="manutencoes-button manutencoes-button--secondary" to={caminhoVoltar}>
                            Cancelar
                        </Link>

                        <button type="submit" className="manutencoes-button manutencoes-button--primary" disabled={salvando}>
                            {salvando ? "Salvando..." : editando ? "Salvar alterações" : "Cadastrar manutenção"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    )
}