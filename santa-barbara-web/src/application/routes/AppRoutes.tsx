import { lazy, Suspense } from "react";
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import { MainLayout } from "../../ui/layouts/MainLayout";

const Components = lazy(() => import("@/application/pages/Components"));
const ConvidarMembros = lazy(() => import("@/application/pages/ConvidarMembros"));
const Membros = lazy(() => import("@/application/pages/Membros"));
const Papeis = lazy(() => import("@/application/pages/Papeis"));

export function AppRoutes() {
  return (
    <BrowserRouter>
      <Suspense
        fallback={
          <div className="flex items-center justify-center min-h-screen bg-[#242424]">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
          </div>
        }
      >
        <Routes>
          {/* Rotas com o Layout padrão (Sidebar + Header) */}
          <Route element={<MainLayout />}>
            <Route path="/" element={<Navigate to="/convidar" replace />} />
            <Route path="/membros" element={<Membros />} />
            <Route path="/convidar" element={<ConvidarMembros />} />
            <Route path="/membros/convidar" element={<ConvidarMembros />} />
            <Route path="/papeis" element={<Papeis />} />
          </Route>

          {/* Rota isolada para visualização de componentes */}
          <Route path="/dev/components" element={<Components />} />

          {/* Rota coringa */}
          <Route path="*" element={<Navigate to="/convidar" replace />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}

export default AppRoutes;