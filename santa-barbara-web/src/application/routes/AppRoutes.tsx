import { lazy, Suspense } from "react";
import { BrowserRouter, Route, Routes } from "react-router";
import { PrivateRoute } from "./PrivateRoute";

const AppLayout = lazy(() => import('@/application/layout/AppLayout'));

const Components = lazy(() => import('@/application/pages/Components'));
const ProfilePage = lazy(() => import('@/application/pages/ProfilePage'));
const Login = lazy(() => import('@/application/pages/Login'));
const MembersListPage = lazy(() => import('@/application/pages/MembersListPage'));


function AppRoutes() {
    return(
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
    )
}

export default AppRoutes;