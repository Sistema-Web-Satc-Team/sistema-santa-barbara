import { lazy, Suspense } from "react";
import { BrowserRouter, Route, Routes, Navigate } from "react-router";

const Components = lazy(() => import('@/application/pages/Components'));
const Login = lazy(() => import('@/application/pages/Login'));
const Invite = lazy(() => import('@/application/pages/Invite'));

function AppRoutes() {
    return(
        <BrowserRouter>
            <Suspense fallback={<div className="flex items-center justify-center min-h-screen">Carregando...</div>}>
                <Routes>
                    <Route path="/" element={<Navigate to="/invite" replace />} />
                    <Route path="/invite" element={<Invite/>} />
                    <Route path="/login" element={<Login/>} />
                    <Route path="/dev/components" element={<Components/>} />
                </Routes>
            </Suspense>
        </BrowserRouter>
    )
}

export default AppRoutes;