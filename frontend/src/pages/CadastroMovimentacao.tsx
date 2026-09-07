import React, { useEffect, useState } from "react";
import { cadastrarMovimentacao, type TipoMovimentacao } from "../api/movimentacoes";
import { listarProdutos, type Produto } from "../api/produtos";
import { useNavigate, useParams } from "react-router-dom";


export function CadastroMovimentacaoPage() {
    
    const [produtoId, setProdutoId] = useState("");
    const [tipo, setTipo] = useState<TipoMovimentacao>("ENTRADA");
    const [quantidade, setQuantidade] = useState("");
    const [data, setData] = useState(new Date().toLocaleDateString("pt-BR"));
    const [observacoes, setObservacoes] = useState("");
    const [produtos, setProdutos] = useState<Produto[]>([]);

    const [carregandoProdutos, setCarregandoProdutos] = useState(true);
    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    const {
        propriedadeId = "",
        depositoId = ""
    } = useParams();

    const navigate = useNavigate();

    const idsValidos = Boolean(
        propriedadeId && depositoId
    );

    const caminhoMovimentacoes = `/propriedades/${propriedadeId}/depositos/${depositoId}/movimentacoes`;

    useEffect(() => {
        let paginaAtiva = true;

        listarProdutos()
            .then((produtosRecebidos) => {
                if (paginaAtiva) {
                    setProdutos(produtosRecebidos.filter(
                        (produto) => produto.ativo
                    ));
                }
            })
            .catch((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(
                        erroRecebido instanceof Error
                            ? erroRecebido.message
                            : "Não foi possivel carregas os produtos."
                    );
                }
            })
            .finally (() => {
                if (paginaAtiva) {
                    setCarregandoProdutos(false);
                }
            });

        return () => {
            paginaAtiva = false;
        };
    }, []);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setErro(null);

        if (!idsValidos) {
            setErro("Não foi possivel identificar o depósito.");
            return;
        }

        if (!produtoId) {
            setErro("Selecione um produto.");
            return;
        }

        const quantidadeConvertida = Number(quantidade);

        if (!Number.isFinite(quantidadeConvertida) || quantidadeConvertida <= 0) {
            setErro("Informe uma quantidade maior que zero.");
            return;
        }

        try {
            setEnviando(true);

            await cadastrarMovimentacao(propriedadeId, depositoId, {
                produtoId,
                tipo, 
                quantidade: quantidadeConvertida,
                data, 
                safraId: null,
                observacoes: observacoes.trim() || null,
            });

            navigate(caminhoMovimentacoes, {
                replace: true,
                state: {
                    mensagem: tipo === "ENTRADA"
                        ? "Entrada registrada com sucesso."
                        : "Saida registrado com sucesso.",
                }
            });
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possivel registrar a movimentação."
            );
        } finally {
            setEnviando(false);
        }
    }

    if (!idsValidos) {
        return (
            <main className="propriedade-form-page">
                <div className="property-details-feedback property-details-feedback--error" role="alert">
                    <h1>Depósito inválido</h1>

                    <p>
                        Não foi possivel identificar o depósito.
                    </p>

                    <button type="button" onClick={() => navigate("/propriedades")}>
                        Voltar
                    </button>
                </div>
            </main>
        );
    }

    return (
        <main className="propriedade-form-page">
            <section className="propriedade-form-card">
                <header className="propriedade-form-header">
                    <span className="propriedade-form-eyebrow">
                        Controle de estoque
                    </span>

                    <h1>Nova movimentação</h1>

                    <p>
                        Registre uma entrada ou saída de produto neste depósito.
                    </p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="produtoId">Produto</label>
                        <select id="produtoId" value={produtoId} onChange={(e) => setProdutoId(e.target.value)} disabled={carregandoProdutos} required>
                            <option value="">
                                {carregandoProdutos ? "Carregando..." : "Selecione um produto"}
                            </option>

                            {produtos.map((produto) => (
                                <option value={produto.id} key={produto.id}>
                                    {produto.nome} -{" "}
                                    {produto.unidadeMedida}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="propriedade-form-row">
                        <div className="propriedade-form-field">
                            <label htmlFor="tipo">Tipo de movimentação</label>

                            <select id="tipo" value={tipo} onChange={(e) => setTipo(e.target.value as TipoMovimentacao)}>
                                <option value="ENTRADA">Entrada</option>
                                <option value="SAIDA">Saída</option>
                            </select>
                        </div>

                        <div className="propriedade-form-field">
                            <label htmlFor="data">Data</label>
                            <input type="date" id="data" value={data} onChange={(e) => setData(e.target.value)} required />
                        </div>
                    </div>

                    <div className="propriedade-form-field">
                        <label htmlFor="quantidade">Quantidade</label>
                        <input type="number" id="quantidade" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} min="0.01" step="0.01" inputMode="decimal" placeholder="0,00" required />
                    </div>

                    <div className="propriedade-form-field">
                        <label htmlFor="observacoes">Observações</label>
                        <textarea id="observacoes" value={observacoes} onChange={(e) => setObservacoes(e.target.value)} rows={5} maxLength={500} placeholder="Informações adicionais..."></textarea>

                        <small>
                            {observacoes.length}/500 caracteres
                        </small>
                    </div>

                    {!carregandoProdutos &&
                        produtos.length === 0 &&
                        !erro && (
                            <p className="propriedade-form-error" role="alert">
                                Não existem produtos ativos para movimentação.
                            </p>
                        )}

                    {erro && (
                        <p className="propriedade-form-error" role="alert">
                            {erro}
                        </p>
                    )}

                    <div className="propriedade-form-actions">
                        <button type="button" className="propriedade-form-cancel" onChange={() => navigate(caminhoMovimentacoes)} disabled={enviando}>
                            Cancelar
                        </button>

                        <button type="submit" className="propriedade-form-submit" disabled={enviando || carregandoProdutos || produtos.length === 0}>
                            {enviando ? "Registrando..." : "Registrar movimentação"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    )
}