import { useEffect, useState } from "react";
import { ativarProduto, desativarProduto, listarProdutos, type CategoriaProduto, type Produto, type UnidadeMedida } from "../api/produtos"
import { useLocation, useNavigate } from "react-router-dom";
import "../styles/produtos.css";


const nomesCategorias: Record<CategoriaProduto, string> = {
    SEMENTE: "Semente",
    FERTILIZANTE: "Fertilizante",
    DEFENSIVO: "Defensivo",
    COMBUSTIVEL: "Combustivel",
    OUTRO: "Outro"
};

const nomesUnidades: Record<UnidadeMedida, string> = {
    KG: "Quilograma",
    LITRO: "Litro",
    SACA: "Saca",
    UNIDADE: "Unidade"
};

export function ProdutosPage() {

    const [produtos, setProdutos] = useState<Produto[]>([]);

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState<string | null>(null);
    const [tentativa, setTentativa] = useState(0);
    const [produtoEmAlteracao, setProdutoEmAlteracao] = useState<string | null>(null);
    const [mensagemStatus, setMensagemStatus] = useState<string | null>(null);
    const [erroStatus, setErroStatus] = useState<string | null>(null);

    const navigate = useNavigate();
    const location = useLocation();

    const mensagem = (
        location.state as { mensagem?: string } | null
    )?.mensagem;

    useEffect(() => {
        let paginaAtiva = true;

        listarProdutos()
            .then((produtoRecebidos) => {
                if (paginaAtiva) {
                    setProdutos(produtoRecebidos);
                    setErro(null);
                }
            })
            .catch ((erroRecebido) => {
                if (paginaAtiva) {
                    setErro(
                        erroRecebido instanceof Error
                            ? erroRecebido.message
                            : "Não foi possivel carregar os produtos."
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
    }, [tentativa]);

    function tentarNovamente() {
        setCarregando(true);
        setErro(null);
        setTentativa((valor) => valor + 1);
    }

    async function alterarStatusProduto(produto: Produto) {
        const acao = produto.ativo ? "desativar" : "ativar";
        if (!window.confirm(`Deseja realmente ${acao} o produto “${produto.nome}”?`)) return;

        setProdutoEmAlteracao(produto.id);
        setMensagemStatus(null);
        setErroStatus(null);

        try {
            const produtoAtualizado = produto.ativo
                ? await desativarProduto(produto.id)
                : await ativarProduto(produto.id);

            setProdutos((atuais) => atuais.map((item) =>
                item.id === produtoAtualizado.id ? produtoAtualizado : item,
            ));
            setMensagemStatus(`Produto ${produtoAtualizado.ativo ? "ativado" : "desativado"} com sucesso.`);
        } catch (erroRecebido) {
            setErroStatus(erroRecebido instanceof Error
                ? erroRecebido.message
                : `Não foi possível ${acao} o produto.`);
        } finally {
            setProdutoEmAlteracao(null);
        }
    }

    const produtosAtivos = produtos.filter(
        (produto) => produto.ativo,
    ).length;

    return (
        <main className="produtos-page">
            <header className="page-header">
                <div>
                    <span className="page-eyebrow">
                        Insumos e estoque
                    </span>

                    <h1>Produtos</h1>

                    <p>
                        Gerencie os produtos utilizados nas operações rurais.
                    </p>
                </div>

                <button type="button" className="primary-button" onClick={() => navigate("/produtos/novo")}>
                    <span aria-hidden="true">+</span>
                    Novo produto
                </button>
            </header>

            {mensagem && (
                <p className="page-message page-message--success" role="status">
                    {mensagem}
                </p>
            )}

            {mensagemStatus && <p className="page-message page-message--success" role="status">{mensagemStatus}</p>}
            {erroStatus && <p className="page-message page-message--error" role="alert">{erroStatus}</p>}

            {!carregando && !erro && (
                <section className="product-summary" aria-label="Resumo dos produtos">
                    <article>
                        <span>Total de produtos</span>
                        <strong>{produtos.length}</strong>
                    </article>

                    <article>
                        <span>Produtos ativos</span>
                        <strong>{produtosAtivos}</strong>
                    </article>

                    <article>
                        <span>Categorias utilizadas</span>
                        <strong>
                            {
                                new Set(
                                    produtos.map(
                                        (produto) =>
                                            produto.categoria
                                    ),
                                ).size
                            }
                        </strong>
                    </article>
                </section>
            )}

            <section className="produtos-section">
                <header className="produtos-section-header">
                    <div>
                        <h2>Catálogo de produtos</h2>

                        <p>
                            Produtos disponíveis para movimentações de estoque.
                        </p>
                    </div>

                    {!carregando && !erro && (
                        <span className="property-count">
                            {produtos.length}{" "}
                            {produtos.length === 1
                                ? "Produto"
                                : "Produtos"
                            }
                        </span>
                    )}
                </header>

                {carregando && <div className="page-feedback" role="status">Carregando produtos...</div>}

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
                        produtos.length === 0 && (
                            <div className="empty-state">
                                <div className="empty-state-icon" aria-hidden="true">
                                    +
                                </div>

                                <h2>Nenhum produto cadastrado.</h2>

                                <p>
                                    Cadastre o primeiro produto do catálogo.
                                </p>

                                <button type="button" className="primary-button" onClick={() => navigate("/produtos/novo")}>
                                    Cadastrar produto
                                </button>
                            </div>
                        )
                    }

                    {!carregando &&
                        !erro &&
                        produtos.length > 0 && (
                            <ul className="produtos-grid">
                                {produtos.map((produto) => (
                                    <li className="produto-card" key={produto.id}>
                                        <header>
                                            <div className="produto-card-icon" aria-hidden="true">
                                                {produto.nome.charAt(0).toUpperCase()}
                                            </div>

                                            <span className={
                                                produto.ativo
                                                    ? "property-status property-status--active"
                                                    : "property-status property-status--inactive"
                                            }>
                                                {produto.ativo 
                                                    ? "Ativo"
                                                    : "Inativo"
                                                }
                                            </span>
                                        </header>

                                        <h3>{produto.nome}</h3>

                                        <dl>
                                            <div>
                                                <dt>Categoria</dt>
                                                <dd>
                                                    {
                                                        nomesCategorias[
                                                            produto.categoria
                                                        ]
                                                    }
                                                </dd>
                                            </div>

                                            <div>
                                                <dt>Unidade</dt>
                                                <dd>
                                                    {
                                                        nomesUnidades[
                                                            produto.unidadeMedida
                                                        ]
                                                    }
                                                </dd>
                                            </div>
                                        </dl>

                                        <div className="produto-card-actions">
                                            <button
                                                className={produto.ativo
                                                    ? "produto-status-action produto-status-action--deactivate"
                                                    : "produto-status-action produto-status-action--activate"}
                                                type="button"
                                                disabled={produtoEmAlteracao === produto.id}
                                                onClick={() => alterarStatusProduto(produto)}
                                            >
                                                {produtoEmAlteracao === produto.id
                                                    ? "Alterando..."
                                                    : produto.ativo ? "Desativar" : "Ativar"}
                                            </button>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}
            </section>
        </main>
    );
}
