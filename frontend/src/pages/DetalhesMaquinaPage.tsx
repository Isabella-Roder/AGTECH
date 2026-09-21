import { Link, useParams } from "react-router-dom";
import "../styles/maquinas.css";
import { useEffect, useState } from "react";
import { buscarMaquinaPorId, rotuloTipoMaquina, type Maquina } from "../api/maquinas";

export function DetalhesMaquinaPage() {

    const { propriedadeId, maquinaId } = useParams<{propriedadeId: string; maquinaId: string;}>();

    const [maquina, setMaquina] = useState<Maquina | null>(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    useEffect(() => {
        if (!propriedadeId || !maquinaId) {
            setErro("Máquina não identificada.");
            setCarregando(false);
            return;
        }

        async function carregarMaquina() {
            try {
                setErro("");

                const resposta = await buscarMaquinaPorId(propriedadeId!, maquinaId!);

                setMaquina(resposta);
            } catch (error) {
                setErro(
                    error instanceof Error
                        ? error.message
                        : "Não foi possível carregar a máquina."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregarMaquina();
    }, [propriedadeId, maquinaId]);

    if (!propriedadeId || !maquinaId) {
        return (
            <main className="maquinas-page">
                <div className="maquinas-alert maquinas-alert--erro">
                    Máquina não identificada.
                </div>
            </main>
        );
    }

    if (carregando) {
        return (
            <main className="maquinas-page">
                <div className="maquinas-state">
                    <div className="maquinas-spinner" />
                    <p>Carregando máquina...</p>
                </div>
            </main>
        );
    }

    if (erro || !maquina) {
        return (
            <main className="maquinas-page">
                <div className="maquinas-alert maquinas-alert--erro">
                    {erro || "Máquina não encontrada."}
                </div>

                <Link className="maquinas-button maquinas-button--secondary" to={`/propriedades/${propriedadeId}/maquinas`}>
                    Voltar para máquinas
                </Link>
            </main>
        );
    }

    return (
        <main className="maquinas-page">
            <section className="maquinas-hero">
                <div>
                    <span className="maquinas-eyebrow">
                        {rotuloTipoMaquina[maquina.tipo]}
                    </span>

                    <h1>{maquina.identificador}</h1>

                    <p>
                        Consulte o histórioco operacional, as manutenções e os abastecimentos deste equipamento.
                    </p>
                </div>

                <Link className="maquinas-button maquinas-button--hero-secondary" to={`/propriedades/${propriedadeId}/maquinas`}>
                    Voltar
                </Link>
            </section>

            <section className="maquina-details-grid">
                <article className="maquina-details-card">
                    <span className="maquina-details-card__label">
                        Horímetro atual
                    </span>

                    <strong>
                        {maquina.horimetroAtual.toLocaleString("pt-BR", {
                            minimumFractionDigits: 1,
                            maximumFractionDigits: 1,
                        })}
                    </strong>

                    <small>horas registradas</small>
                </article>

                <article className="maquina-details-card">
                    <span className="maquina-details-card__label">
                        Tipo do equipamento
                    </span>

                    <strong className="maquina-details-card__text">
                        {rotuloTipoMaquina[maquina.tipo]}
                    </strong>

                    <small>classificação da máquina</small>
                </article>

                <article className="maquina-details-card">
                    <span className="maquina-details-card__label">
                        Situação
                    </span>

                    <strong className="maquina-details-card__text">
                        {maquina.ativo ? "Em operação" : "Inativa"}
                    </strong>

                    <span className={`maquina-status ${maquina.ativo ? "maquina-status--ativo" : "maquina-status--inativo"}`}>
                        {maquina.ativo ? "Ativa" : "Inativa"}
                    </span>
                </article>
            </section>

            <section className="maquina-modules">
                <div className="maquina-modules__heading">
                    <span>Histórico operacional</span>
                    <h2>Gerenciar máquina</h2>
                    <p>
                        Escolha qual conjunto de registros deseja consultar.
                    </p>
                </div>

                <div className="maquina-modules__grid">
                    <Link className="maquina-module-card" to={`/propriedades/${propriedadeId}/maquinas/${maquinaId}/manutencoes`}>
                        <div className="maquina-module-card__icon">M</div>

                        <div>
                            <h3>Manutenções</h3>
                            <p>
                                Registre serviços preventivos e corretivos, custo e horímetro.
                            </p>
                        </div>

                        <span className="maquina-module-card__arrow">→</span>
                    </Link>

                    <Link className="maquina-module-card" to={`/propriedades/${propriedadeId}/maquinas/${maquinaId}/abastecimentos`}>
                        <div className="maquina-module-card__icon">A</div>

                        <div>
                            <h3>Abastecimentos</h3>
                            <p>
                                Controle combústivel, quantidade, valor e horímetro.
                            </p>
                        </div>

                        <span className="maquina-module-card__arrow">→</span>
                    </Link>
                </div>
            </section>
        </main>
    );
}
