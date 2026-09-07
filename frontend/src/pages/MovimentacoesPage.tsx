import { useEffect, useState } from "react";
import { listarMovimentacoes, type MovimentacaoEstoque } from "../api/movimentacoes";
import { listarProdutos, type Produto } from "../api/produtos";
import { buscarDepositoPorId, type Deposito } from "../api/depositos";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import "../styles/movimentacoes.css";


export function MovimentacoesPage() {
    
    const [movimentacoes, setMovimentacoes] = useState<MovimentacaoEstoque[]>([]);
    const [produtos, setProdutos] = useState<Produto[]>([]);
    const [deposito, setDeposito] = useState<Deposito | null>(null);

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState<string | null>(null);
    const [tentativa, setTentativa] = useState(0);

    const {
        propriedadeId = "",
        depositoId = "",
    } = useParams();

    const navigate = useNavigate();
    const location = useLocation();

    const idsValidos = Boolean(
        propriedadeId && depositoId
    );

    const mensagem = (
        location.state as { mensagem?: string } | null
    )?.mensagem;

    const caminhoDepositos = `/propriedades/${propriedadeId}/depositos`;
    const caminhoNovaMovimentacao = `${caminhoDepositos}/${depositoId}/movimentacoes/nova`;

    useEffect(() => {
        let paginaAtiva = true;

        if (!idsValidos) {
            return;
        }

        Promise.all([
            listarMovimentacoes(
                propriedadeId,
                depositoId
            ),
            listarProdutos(),
            buscarDepositoPorId(
                propriedadeId,
                depositoId
            ),
        ])
            .then(([
                movimentacoesRecebidas,
                produtosRecebidos,
                depositoRecebido
            ]) => {
                if (paginaAtiva) {
                    setMovimentacoes(movimentacoesRecebidas);
                    setProdutos(produtosRecebidos);
                    setDeposito(depositoRecebido);
                    setErro(null);
                }
            })
            .catch((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(
                        erroRecebido instanceof Error
                            ? erroRecebido.message
                            : "Não foi possível carregar as movimentações."
                    );
                }
            })
            .finally (() => {
                if (paginaAtiva) {
                    setCarregando(false);
                }
            });

        return () => {
            paginaAtiva = false;
        };
    }, [depositoId, idsValidos, propriedadeId, tentativa]);

    function tentarNovamente() {
        setCarregando(true);
        setErro(null);
        setTentativa((valor) => valor + 1);
    }

    function nomeDoProduto(produtoId: string) {
        return (
            produtos.find(
                (produto) => produto.id === produtoId,
            )?.nome ?? "Produto não encontrado"
        );
    }

    function unidadeDoProduto(produtoId: string) {
        return (
            produtos.find(
                (produto) => produto.id === produtoId
            )?.unidadeMedida ?? ""
        );
    }

    function formatarData(data: string) {
        return new Intl.DateTimeFormat("pt-BR", {
            timeZone: "UTC",
        }).format(new Date(`${data}T00:00:00Z`));
    }

    const entradas = movimentacoes.filter(
        (movimentacao) => movimentacao.tipo === "ENTRADA"
    ).length;

    const saidas = movimentacoes.filter(
        (movimentacao) => movimentacao.tipo === "SAIDA"
    ).length;

    if (!idsValidos) {
        return (
            <main className="movimentacoes-page">
                <div className="page-feedback page-feedback--error" role="alert">
                    Não foi possível identificar o depósito.
                </div>
            </main>
        );
    }

    return (
        <main className="movimentacoes-page">
            <button type="button" className="property-details-back" onClick={() => navigate(caminhoDepositos)}>
                <span aria-hidden="true">←</span>
                Voltar para os depósitos
            </button>

            <header className="page-header">
                <div>
                    <span className="page-eyebrow">
                        Controle de estoque
                    </span>

                    <h1>
                        {deposito?.nome
                            ? `Movimentações — ${deposito.nome}`
                            : "Movimentações"
                        }
                    </h1>

                    <p>
                        Consulte as entradas e saídas registradas neste depósito.
                    </p>
                </div>

                {deposito?.ativo && (
                    <button type="button" className="primary-button" onClick={() => navigate(caminhoNovaMovimentacao)}>
                        <span aria-hidden="true">+</span>
                        Nova movimentação
                    </button>
                )}
            </header>

            {mensagem && (
                <p className="page-message page-message--success" role="status">
                    {mensagem}
                </p>
            )}

            {!carregando && !erro && (
                <section className="movimentacoes-summary" aria-label="Resumo das movimentações">
                    <article>
                        <span>Total</span>
                        <strong>{movimentacoes.length}</strong>
                    </article>
                    <article>
                        <span>Entradas</span>
                        <strong>{entradas}</strong>
                    </article>
                    <article>
                        <span>Saídas</span>
                        <strong>{saidas}</strong>
                    </article>
                </section>
            )}

            <section className="movimentacoes-section">
                {carregando && (
                    <div className="page-feedback" role="status">
                        Carregando movimentações...
                    </div>
                )}

                {erro && (
                    <div className="page-feedback page-feedback--error" role="alert">
                        <p>{erro}</p>

                        <button type="button" onClick={tentarNovamente}>
                            Tentar novamente
                        </button>
                    </div>
                )}

                {!carregando &&
                    !erro &&
                    movimentacoes.length === 0 && (
                        <div className="empty-state">
                            <div className="empty-state-icon" aria-hidden="true">
                                +
                            </div>

                            <h2>Nenhuma movimentação registrada</h2>

                            <p>
                                As entradas e saídas deste depósito aparecerão aqui.
                            </p>

                            {deposito?.ativo && (
                                <button type="button" className="primary-button" onClick={() => navigate(caminhoNovaMovimentacao)}>
                                    Registrar movimentação
                                </button>
                            )}
                        </div>
                    )}

                    {!carregando && 
                        !erro &&
                        movimentacoes.length > 0 && (
                            <ul className="movimentacoes-grid">
                                {movimentacoes.map(
                                    (movimentacao) => (
                                        <li className="movimentacao-card" key={movimentacao.id}>
                                            <header>
                                                <span className={
                                                    movimentacao.tipo === "ENTRADA"
                                                        ? "movimentacao-type movimentacao-type--entry"
                                                        : "movimentacao-type movimentacao-type--exit"
                                                }>
                                                    {movimentacao.tipo === "ENTRADA"
                                                        ? "Entrada"
                                                        : "Saída"
                                                    }
                                                </span>

                                                <time dateTime={movimentacao.data}>
                                                    {formatarData(movimentacao.data)}
                                                </time>
                                            </header>

                                            <h2>{nomeDoProduto(movimentacao.produtoId)}</h2>

                                            <div className="movimentacao-quantity">
                                                <span>Quantidade</span>
                                                <strong>
                                                    {new Intl.NumberFormat("pt-BR", {
                                                        maximumFractionDigits: 2,
                                                    }).format(movimentacao.quantidade)}{" "}
                                                    <small>{unidadeDoProduto(movimentacao.produtoId)}</small>
                                                </strong>
                                            </div>

                                            <p className="movimentacao-notes">
                                                {movimentacao.observacoes || "Nenhuma observação informada."}
                                            </p>
                                        </li>
                                    )
                                )}
                            </ul>
                        )}
            </section>
        </main>
    )
}
