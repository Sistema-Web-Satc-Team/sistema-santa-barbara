import { lazy, Suspense } from "react";
import { BrowserRouter, Route, Routes } from "react-router";

const AppLayout = lazy(() => import('@/application/layout/AppLayout'));

const Components = lazy(() => import('@/application/pages/Components'));
const Login = lazy(() => import('@/application/pages/Login'));


function AppRoutes() {
    return(
        <BrowserRouter>
            <Suspense fallback={<div>Carregando...</div>}>
                <Routes>
                    <Route path="/dev/components" element={<Components/>} />
                    <Route element={<AppLayout />}>
                        <Route path="/login" element={<Login />} />
                    </Route>
                </Routes>
            </Suspense>
        </BrowserRouter>
    )
}

export default AppRoutes;