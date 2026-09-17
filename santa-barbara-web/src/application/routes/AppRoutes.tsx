import { lazy, Suspense } from "react";
import { BrowserRouter, Route, Routes } from "react-router";

const Components = lazy(() => import('@/application/pages/Components'));
const ConvidarMembros = lazy(() => import('@/application/pages/ConvidarMembros'));
const AceitarConvite = lazy(() => import('@/application/pages/AceitarConvite'));

function AppRoutes() {
    return(
        <BrowserRouter>
            <Suspense fallback={<div>Carregando...</div>}>
                <Routes>
                    <Route path="/dev/components" element={<Components/>} />
                    <Route path="/convidar-membros" element={<ConvidarMembros />} />
                    <Route path="/convite/:idConvite" element={<AceitarConvite />} />
                </Routes>
            </Suspense>
        </BrowserRouter>
    )
}

export default AppRoutes;
