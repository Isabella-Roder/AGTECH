import { useState } from "react"
import "../layout/AppLayout.css"
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { removerToken } from "../../api/cliente";

export function AppLayout() {
    const [menuAberto, setMenuAberto] = useState(false);
    const navigate = useNavigate();

    function fecharMenu() {
        setMenuAberto(false);
    }

    function logout() {
        removerToken();
        navigate("/", {replace: true});
    }

    return (
        <div className="app-layout">
            <button className="app-menu-button" type="button" aria-label="Abrir menu" aria-expanded={menuAberto} onClick={() => setMenuAberto((aberto) => !aberto)}>
                <span/>
                <span/>
                <span/>
            </button>

            {menuAberto && (
                <button className="app-menu-overlay" type="button" aria-label="Fechar menu" onClick={fecharMenu}/>
            )}

            <aside className={
                menuAberto 
                    ? "app-sidebar app-sidebar--open"
                    : "app-sidebar"
            }>
                <header className="app-brand">
                    <div className="app-brand-mark" aria-hidden="true">
                        A
                    </div>

                    <div>
                        <strong>AGTECH</strong>
                        <span>Gestão rural</span>
                    </div>
                </header>

                <nav className="app-navigation" aria-label="Navegação principal">
                    <span className="app-navigation-label">
                        Gestão
                    </span>

                    <NavLink className={({isActive}) => 
                            isActive
                                ? "app-navigation-link app-navigation-link--active"
                                : "app-navigation-link"    
                        }
                        to="/propriedades"
                        onClick={fecharMenu}
                    >
                        <svg aria-hidden="true" viewBox="0 0 24 24" fill="none">
                            <path
                                d="M3 21V10l9-7 9 7v11M8 21v-7h8v7M3 21h18"
                                stroke="currentColor"
                                strokeWidth="1.8"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />
                        </svg>

                        Propriedades
                    </NavLink>
                </nav>

                <footer className="app-sidebar-footer">
                    <div className="app-user">
                        <div className="app-user-avatar" aria-hidden="true">
                            U
                        </div>

                        <div>
                            <strong>Minha conta</strong>
                            <span>Usuário autenticado</span>
                        </div>
                    </div>

                    <button className="app-logout" type="button" onClick={logout}>
                        <svg
                            aria-hidden="true"
                            viewBox="0 0 24 24"
                            fill="none"
                        >
                            <path
                                d="M10 17l5-5-5-5M15 12H3M14 3h5a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-5"
                                stroke="currentColor"
                                strokeWidth="1.8"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />
                        </svg>

                        Encerrar sessão
                    </button>
                </footer>
            </aside>

            <div className="app-content">
                <header className="app-topbar">
                    <div>
                        <span className="app-topbar-eyebrow">
                            Plataforma AGTECH
                        </span>

                        <strong>Gestão da propriedade rural</strong>
                    </div>
                </header>

                <div className="app-page-content">
                    <Outlet />
                </div>
            </div>
        </div>
    );
}
