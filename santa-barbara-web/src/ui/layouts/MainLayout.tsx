import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import '../styles/MainLayout.css';

export interface MainLayoutProps {
  children?: React.ReactNode;
}

export const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  return (
    <div className="layout-root">
      {/* Cabeçalho principal com a sua logo oficial da pasta public */}
      <header className="header-bar">
        <div className="brand-container">
          <img 
            src="/logo Santa Barbara.svg" 
            alt="Logo Santa Bárbara" 
            className="brand-custom-logo" 
          />
        </div>

        <div className="user-profile">
          <div className="user-avatar">A</div>
          <div className="user-details">
            <span className="user-name">Administrador</span>
            <span className="user-role">Admin</span>
          </div>
        </div>
      </header>

      <div className="layout-body">
        {/* Barra lateral de navegação com os ícones do Figma */}
        <aside className="layout-sidebar">
          <nav className="sidebar-nav">
            <NavLink
              to="/membros"
              className={({ isActive }: { isActive: boolean }) =>
                `nav-link ${isActive ? 'active' : ''}`
              }
            >
              <img 
                src="/icon Membros.svg" 
                alt="Membros" 
                className="nav-icon-img" 
              />
              <span className="nav-label">Membros</span>
            </NavLink>

            <NavLink
              to="/convidar"
              className={({ isActive }: { isActive: boolean }) =>
                `nav-link ${isActive ? 'active' : ''}`
              }
            >
              <img 
                src="/icon convidar membros.svg" 
                alt="Convidar Membros" 
                className="nav-icon-img" 
              />
              <span className="nav-label">Convidar Membros</span>
            </NavLink>

            <NavLink
              to="/papeis"
              className={({ isActive }: { isActive: boolean }) =>
                `nav-link ${isActive ? 'active' : ''}`
              }
            >
              <img 
                src="/Icon papeis.svg" 
                alt="Papéis" 
                className="nav-icon-img" 
              />
              <span className="nav-label">Papéis</span>
            </NavLink>
          </nav>
        </aside>

        {/* Área de conteúdo das páginas */}
        <main className="layout-content">
          {children || <Outlet />}
        </main>
      </div>
    </div>
  );
};

export default MainLayout;