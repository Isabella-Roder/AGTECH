
import React, { useState } from "react";
import "../styles/propriedade-form.css";
import { cadastrarProduto, type CategoriaProduto, type UnidadeMedida } from "../api/produtos";
import { useNavigate } from "react-router-dom";

export function CadastroProdutoPage() {
    
    const [nome, setNome] = useState("");
    const [categoria, setCategoria] = useState<CategoriaProduto | "">("");
    const [unidadeMedida, setUnidadeMedida] = useState<UnidadeMedida | "">("");

    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setErro(null);

        if (!categoria || !unidadeMedida) {
            setErro("Selecione a categoria e a unidade de medida.");
            return;
        }

        try {
            setEnviando(true);

            await cadastrarProduto({
                nome: nome.trim(),
                categoria, 
                unidadeMedida
            });

            navigate("/produtos", {
                replace: true,
                state: {
                    mensagem: "Produto cadastrado com sucesso."
                }
            });
        } catch (erroRecebido) {
            setErro(
                erroRecebido instanceof Error
                    ? erroRecebido.message
                    : "Não foi possivel cadastrar o produto."
            );
        } finally {
            setEnviando(false);
        }
    }

    return (
        <main className="propriedade-form-page">
            <section className="propriedade-form-card">
                <header className="propriedade-form-header">
                    <span className="propriedade-form-eyebrow">
                        Insumos e estoque
                    </span>

                    <h1>Novo produto</h1>

                    <p>
                        Cadastre um produto para utilizá-lo nas movimentações de estoque.
                    </p>
                </header>

                <form className="propriedade-form" onSubmit={handleSubmit}>
                    <div className="propriedade-form-field">
                        <label htmlFor="nome">Nome do produto</label>
                        <input type="text" id="nome" name="nome" value={nome} onChange={(e) => setNome(e.target.value)} minLength={2} maxLength={100} placeholder="Ex.: Semente de soja" autoFocus required />
                    </div>

                    <div className="propriedade-form-row">
                        <div className="propriedade-form-field">
                            <label htmlFor="categoria">Categoria</label>

                            <select name="categoria" id="categoria" value={categoria} onChange={(e) => setCategoria(e.target.value as | CategoriaProduto | "")} required>
                                <option value="">Selecione</option>
                                <option value="SEMENTE">Semente</option>
                                <option value="FERTILIZANTE">Fertilizante</option>
                                <option value="DEFENSIVO">Defensivo</option>
                                <option value="COMBUSTIVEL">Combustivel</option>
                                <option value="OUTRO">Outro</option>
                            </select>
                        </div>

                        <div className="propriedade-form-field">
                            <label htmlFor="unidadeMedida">Unidade de medida</label>

                            <select name="unidadeMedida" id="unidadeMedida" value={unidadeMedida} onChange={(e) => setUnidadeMedida(e.target.value as | UnidadeMedida | "")} required>
                                <option value="">Selecione</option>
                                <option value="KG">Quilograma</option>
                                <option value="LITRO">Litro</option>
                                <option value="SACA">Saca</option>
                                <option value="UNIDADE">Unidade</option>
                            </select>
                        </div>
                    </div>

                    {erro && (
                        <p role="alert" className="propriedade-form-error">
                            {erro}
                        </p>
                    )}

                    <div className="propriedade-form-actions">
                        <button type="button" className="propriedade-form-cancel" onClick={() => navigate("/produtos")} disabled={enviando}>
                            Cancelar
                        </button>

                        <button type="submit" className="propriedade-form-submit" disabled={enviando}>
                            {enviando
                                ? "Cadastrando..."
                                : "Cadastrar produto"
                            }
                        </button>
                    </div>
                </form>
            </section>
        </main>
    );
}