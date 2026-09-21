import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { atualizarAbastecimento, buscarAbastecimentoPorId, cadastrarAbastecimento, type CadastroAbastecimento } from "../api/abastecimentos";
import "../styles/abastecimentos.css";

export function FormularioAbastecimentoPage() {

    const { propriedadeId, maquinaId, abastecimentoId } = useParams<{
        propriedadeId: string;
        maquinaId: string;
        abastecimentoId?: string;
    }>();

    const navigate = useNavigate();
    const editando = Boolean(abastecimentoId);

    const [data, setData] = useState("");
    const [litros, setLitros] = useState("");
    const [horimetroNoMomento, setHorimetroNoMomento] = useState("");
    const [observacoes, setObservacoes] = useState("");
    const [carregando, setCarregando] = useState(editando);
    const [salvando, setSalvando] = useState(false);
    const [erro, setErro] = useState("");

    const caminhoVoltar =
        propriedadeId && maquinaId
            ? `/propriedades/${propriedadeId}/maquinas/${maquinaId}/abastecimentos`
            : "/propriedades";

    useEffect(() => {
        if (!abastecimentoId || !propriedadeId || !maquinaId) {
            setCarregando(false);
            return;
        }

        async function carregarAbastecimento() {
            try {
                setCarregando(true);
                setErro("");

                const abastecimento =  await buscarAbastecimentoPorId(
                    propriedadeId!,
                    maquinaId!,
                    abastecimentoId!
                );

                //datetime sem segundos.
                setData(abastecimento.data.slice(0, 16));
                setLitros(String(abastecimento.litros));
                setHorimetroNoMomento(String(abastecimento.horimetroNoMomento));
                setObservacoes(abastecimento.observacoes ?? "");
            } catch (error) {
                setErro(
                    error instanceof Error
                        ? error.message
                        : "Não foi possivel carregar o abastecimento."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregarAbastecimento();
    }, [propriedadeId, maquinaId, abastecimentoId]);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();

        if (!propriedadeId || !maquinaId) {
            setErro("Máquina não identificada.");
            return;
        }

        const quantidade = Number(litros.replace(",", "."));
        const horimetro = Number(horimetroNoMomento.replace(",", "."));
        const observacoesTratadas = observacoes.trim();

        if (!data) {
            setErro("Informe a data e hora do abastecimento.");
            return;
        }

        if (!Number.isFinite(quantidade) || quantidade <= 0) {
            setErro("A quantidade de litros deve ser maior que zero.");
            return;
        }

        if (!Number.isFinite(horimetro) || horimetro <= 0) {
            setErro("O horímetro deve ser maior que zero.");
            return;
        }

        if (observacoesTratadas.length > 500) {
            setErro("As observações devem ter no máximo 500 caracteres.");
            return;
        }

        const dados: CadastroAbastecimento = {
            data,
            litros: quantidade,
            horimetroNoMomento: horimetro,
            observacoes: observacoesTratadas || null
        };

        try {
            setSalvando(true);
            setErro("");

            if (abastecimentoId) {
                await atualizarAbastecimento(
                    propriedadeId,
                    maquinaId,
                    abastecimentoId,
                    dados
                );
            } else {
                await cadastrarAbastecimento(
                    propriedadeId,
                    maquinaId,
                    dados
                );
            }

            navigate(caminhoVoltar);
        } catch (error) {
            setErro(
                error instanceof Error
                    ? error.message
                    : "Não foi possivel salvar o abastecimento."
            );
        } finally {
            setSalvando(false);
        }
    }

    if (!propriedadeId || !maquinaId) {
        return (
            <main className="abastecimentos-page">
                <div className="abastecimentos-alert abastecimentos-alert--erro">
                    Máquina não identificada.
                </div>
            </main>
        );
    }

    if (carregando) {
        return (
            <main className="abastecimentos-page">
                <div className="abastecimentos-state">
                    <div className="abastecimentos-spinner"/>
                    <p>Carregando abastecimento...</p>
                </div>
            </main>
        );
    }

    return (
        <main className="abastecimentos-page">
            <section className="abastecimentos-hero">
                <div>
                    <span className="abastecimentos-eyebrow">
                        Histórico da máquina
                    </span>

                    <h1>
                        {editando
                            ? "Editar abastecimento"
                            : "Novo abastecimento"
                        }
                    </h1>

                    <p>
                        Registre a quantidade abastecida e o horímetro.
                    </p>
                </div>

                <Link className="abastecimentos-button abastecimentos-button--ghost" to={caminhoVoltar}>
                        Voltar
                </Link>
            </section>

            <section className="abastecimento-form-container">
                <div className="abastecimento-form-heading">
                    <span>Controle de combustível</span>
                    <h2>Dados do abastecimento</h2>
                    <p>Preencha os dados conforme o registro realizado.</p>
                </div>

                {erro && (
                    <div className="abastecimentos-alert abastecimentos-alert--erro">
                        {erro}
                    </div>
                )}

                <form className="abastecimento-form" onSubmit={handleSubmit}>
                    <label className="abastecimento-field abastecimento-field--full">
                        <span>Data e hora</span>
                        <input type="datetime-local" value={data} disabled={salvando} onChange={(e) => setData(e.target.value)} required />
                    </label>

                    <label className="abastecimento-field">
                        <span>Quantidade abastecida</span>
                        <div className="abastecimento-input-suffix">
                            <input type="number" min={"0.01"} step={"0.01"} inputMode="decimal" value={litros} placeholder="Ex.: 75,50" disabled={salvando} onChange={(e) => setLitros(e.target.value)} required />
                            <span>L</span>
                        </div>
                    </label>

                    <label className="abastecimento-field">
                        <span>Horímetro no momento</span>
                        <div className="abastecimento-input-suffix">
                            <input type="number" min={"0.1"} step={"0.1"} inputMode="decimal" value={horimetroNoMomento} placeholder="Ex.: 1280,5" disabled={salvando} onChange={(e) => setHorimetroNoMomento(e.target.value)} required />
                            <span>h</span>
                        </div>
                    </label>

                    <label className="abastecimento-field abastecimento-field--full">
                        <span>Observações</span>
                        <textarea value={observacoes} maxLength={500} rows={5} placeholder="Informações adicionais sobre o abastecimento..." disabled={salvando} onChange={(e) => setObservacoes(e.target.value)}/>
                        <small>{observacoes.length}/500 caracteres</small>
                    </label>

                    <div className="abastecimento-form-actions">
                        <Link className="abastecimentos-button abastecimentos-button--secondary" to={caminhoVoltar}>
                            Cancelar
                        </Link>

                        <button type="submit" className="abastecimentos-button abastecimentos-button--primary" disabled={salvando}>
                            {salvando ? "Salvando..." : editando ? "Salvar alterações" : "Cadastrar abastecimento"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    );

}
