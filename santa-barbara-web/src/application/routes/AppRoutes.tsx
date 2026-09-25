import { lazy, Suspense } from "react";
import { BrowserRouter, Route, Routes } from "react-router";
import { ServiceProvider } from "../context/service.provider";
import DashBoardLayout from "../layout/DashBoardLayout";
import { PrivateRoute } from "./PrivateRoute";

const AppLayout = lazy(() => import('@/application/layout/AppLayout'));

const Components = lazy(() => import('@/application/views/Components'));
const ProfilePage = lazy(() => import('@/application/views/ProfilePage'));
const Login = lazy(() => import('@/application/views/Login'));
const MembersListPage = lazy(() => import('@/application/views/MembersList'));
const ConvidarMembros = lazy(() => import('@/application/views/ConvidarMembros'));
const Invite = lazy(() => import('@/application/views/Invite'));


function AppRoutes() {
    return(
        <ServiceProvider>
            <BrowserRouter>
                <Suspense fallback={<div>Carregando...</div>}>
                    <Routes>
                        
                        <Route element={<AppLayout />}>
                            <Route path="/login" element={<Login />} />

                            <Route path="/convidar-membro/:id" element={<Invite />} />

                            <Route element={<PrivateRoute />}>
                                <Route element={<DashBoardLayout />}>
                                    <Route path="/dashboard/membros" element={<MembersListPage/>} />
                                    <Route path="/dashboard/membros/convidar" element={<ConvidarMembros />} />
                                </Route>

                                <Route path="/dev/components" element={<Components/>} />
                                <Route path="/profile" element={<ProfilePage/>} />
                            </Route>
                        </Route>

                    </Routes>
                </Suspense>
            </BrowserRouter>
        </ServiceProvider>
    )
}

export default AppRoutes;