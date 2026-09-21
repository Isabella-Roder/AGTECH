import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import {
    BrowserRouter,
    Navigate,
    Route,
    Routes,
} from "react-router-dom";

import { AppLayout } from "./components/layout/AppLayout";
import { RotaProtegida } from "./RotaProtegida";
import { CadastroPage } from "./pages/CadastroPage";
import { CadastroPropriedadePage } from "./pages/CadastroPropriedadePage";
import { LoginPage } from "./pages/LoginPage";
import { PropriedadesPage } from "./pages/PropriedadesPage";


import "./index.css";
import { DetalhesPropriedadePage } from "./pages/DetalhesPropriedadePage";
import { EditarPropriedadePage } from "./pages/EditarPropriedadePage";
import { CadastroTalhaoPage } from "./pages/CadastroTalhaoPage";
import { EditarTalhao } from "./pages/EditarTalhaoPage";
import { CadastroCultura } from "./pages/CadastroCulturaPage";
import { CulturasPage } from "./pages/CulturasPage";
import { CadastroSafra } from "./pages/CadastroSafraPage";
import { SafraPage } from "./pages/SafraPage";
import { CadastroPlantioPage } from "./pages/CadastroPlantioPage";
import { PlantiosPage } from "./pages/PlantiosPage";
import { EditarPlantioPage } from "./pages/EditarPlantioPage";
import { ProdutosPage } from "./pages/ProdutosPage";
import { CadastroProdutoPage } from "./pages/CadastroProdutoPage";
import { DepositosPage } from "./pages/DepositoPage";
import { CadastroDepositoPage } from "./pages/CadastroDepositoPage";
import { EditarDepositoPage } from "./pages/EditarDepositoPage";
import { MovimentacoesPage } from "./pages/MovimentacoesPage";
import { CadastroMovimentacaoPage } from "./pages/CadastroMovimentacao";
import { MaquinaPage } from "./pages/MaquinaPage";
import { CadastroMaquinaPage } from "./pages/CadastroMaquinaPage";
import { DetalhesMaquinaPage } from "./pages/DetalhesMaquinaPage";
import { ManutencoesPage } from "./pages/ManutencoesPage";
import { FormularioManutencaoPage } from "./pages/FormularioManutencaoPage";
import { AbastecimentosPage } from "./pages/AbastecimentosPage";
import { FormularioAbastecimentoPage } from "./pages/FormularioAbastecimentoPage";

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <BrowserRouter>
            <Routes>
                {/* Rotas públicas */}
                <Route path="/" element={<LoginPage />} />
                <Route
                    path="/cadastro"
                    element={<CadastroPage />}
                />

                {/* Rotas autenticadas com o layout profissional */}
                <Route
                    element={
                        <RotaProtegida>
                            <AppLayout />
                        </RotaProtegida>
                    }
                >
                    <Route
                        path="/propriedades"
                        element={<PropriedadesPage />}
                    />

                    <Route
                        path="/propriedades/nova"
                        element={<CadastroPropriedadePage />}
                    />

                    <Route
                        path="/propriedades/:id"
                        element={<DetalhesPropriedadePage />}
                    />

                    <Route
                        path="/propriedades/:id/editar"
                        element={<EditarPropriedadePage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/talhoes/novo"
                        element={<CadastroTalhaoPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/talhoes/:talhaoId/editar"
                        element={<EditarTalhao/>}
                    />

                    {/** Culturas  */}
                    <Route
                        path="/culturas"
                        element={<CulturasPage />}
                    />

                    <Route
                        path="/culturas/nova"
                        element={<CadastroCultura />}
                    />

                    {/** Safras */}

                    <Route
                        path="/propriedades/:propriedadeId/talhoes/:talhaoId/safras/nova"
                        element={<CadastroSafra />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/talhoes/:talhaoId/safras"
                        element={<SafraPage />}
                    />

                    {/** Plantio */}

                    <Route
                        path="/propriedades/:propriedadeId/talhoes/:talhaoId/safras/:safraId/plantios"
                        element={<PlantiosPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/talhoes/:talhaoId/safras/:safraId/plantios/novo"
                        element={<CadastroPlantioPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/talhoes/:talhaoId/safras/:safraId/plantios/:plantioId/editar"
                        element={<EditarPlantioPage />}
                    />

                    {/** produtos */}

                    <Route
                        path="/produtos"
                        element={<ProdutosPage />}
                    />

                    <Route
                        path="/produtos/novo"
                        element={<CadastroProdutoPage />}
                    />

                    {/** Depositos */}

                    <Route
                        path="/propriedades/:propriedadeId/depositos"
                        element={<DepositosPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/depositos/novo"
                        element={<CadastroDepositoPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/depositos/:depositoId/editar"
                        element={<EditarDepositoPage />}
                    />

                    {/** Movimentações */}
                    
                    <Route
                        path="/propriedades/:propriedadeId/depositos/:depositoId/movimentacoes"
                        element={<MovimentacoesPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/depositos/:depositoId/movimentacoes/nova"
                        element={<CadastroMovimentacaoPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas"
                        element={<MaquinaPage/>}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas/nova"
                        element={<CadastroMaquinaPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas/:maquinaId"
                        element={<DetalhesMaquinaPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas/:maquinaId/manutencoes"
                        element={<ManutencoesPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas/:maquinaId/manutencoes/nova"
                        element={<FormularioManutencaoPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas/:maquinaId/manutencoes/:manutencaoId/editar"
                        element={<FormularioManutencaoPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas/:maquinaId/abastecimentos"
                        element={<AbastecimentosPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas/:maquinaId/abastecimentos/novo"
                        element={<FormularioAbastecimentoPage />}
                    />

                    <Route
                        path="/propriedades/:propriedadeId/maquinas/:maquinaId/abastecimentos/:abastecimentoId/editar"
                        element={<FormularioAbastecimentoPage />}
                    />

                </Route>

                {/* Rota desconhecida */}
                <Route
                    path="*"
                    element={<Navigate to="/" replace />}
                />
            </Routes>
        </BrowserRouter>
    </StrictMode>,
);
