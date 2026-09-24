import { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router";
import { useServices } from "../hook/useServices";

const PrivateRoute = () => {
    const { authService } = useServices();

    const [isAutenticado, setIsAutenticado] = useState<boolean | null>(null);

    useEffect(() => {
        (async function () {
         const autenticado = await authService.isAutenticado();
            setIsAutenticado(autenticado);
        })()
    }, [])

    if (isAutenticado === null) {
        return <div>Carregando...</div>; 
    }
    
    return isAutenticado ? <Outlet /> : <Navigate to="/login" replace />;
};

export { PrivateRoute };
