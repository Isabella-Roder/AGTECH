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