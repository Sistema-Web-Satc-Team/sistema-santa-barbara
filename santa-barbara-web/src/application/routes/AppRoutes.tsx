import { lazy, Suspense } from "react";
import { BrowserRouter, Route, Routes } from "react-router";
import { ServiceProvider } from "../context/service.provider";
import { PrivateRoute } from "./PrivateRoute";

const AppLayout = lazy(() => import('@/application/layout/AppLayout'));

const Components = lazy(() => import('@/application/views/Components'));
const ProfilePage = lazy(() => import('@/application/views/ProfilePage'));
const Login = lazy(() => import('@/application/views/Login'));
const MembersListPage = lazy(() => import('@/application/views/MembersList'));


function AppRoutes() {
    return(
        <ServiceProvider>
            <BrowserRouter>
                <Suspense fallback={<div>Carregando...</div>}>
                    <Routes>
                        
                        <Route element={<AppLayout />}>
                            <Route path="/login" element={<Login />} />

                            <Route element={<PrivateRoute />}>
                                <Route path="/dev/components" element={<Components/>} />
                                <Route path="/profile" element={<ProfilePage/>} />
                                <Route path="/dashboard/members" element={<MembersListPage/>} />
                            </Route>
                        </Route>

                    </Routes>
                </Suspense>
            </BrowserRouter>
        </ServiceProvider>
    )
}

export default AppRoutes;