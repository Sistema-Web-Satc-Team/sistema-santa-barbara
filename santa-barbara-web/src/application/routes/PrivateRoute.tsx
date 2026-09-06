import { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router";
import { AuthService } from "../services/auth.service";

const PrivateRoute = () => {
    const [isAutenticado, setIsAutenticado] = useState(null);

    useEffect(() => {
        (async function () {
            const isAutenticado = await AuthService.isAutenticado();
            setIsAutenticado(isAutenticado);
        })()
    }, [])

    if (isAutenticado === null) {
        return <div>Carregando...</div>; 
    }
    
    return isAutenticado ? <Outlet /> : <Navigate to="/login" replace />;
};

export { PrivateRoute };
