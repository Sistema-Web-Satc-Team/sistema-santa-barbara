import { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router";
// import { AuthService } from "../services/auth.service";

const PrivateRoute = () => {
    const [isAutenticado, setIsAutenticado] = useState<boolean | null>(null);

    useEffect(() => {
        (async function () {
            // const autenticado = await AuthService.isAutenticado();
            const autenticado = true; // só pra testar
            setIsAutenticado(autenticado);
        })()
    }, [])

    if (isAutenticado === null) {
        return <div>Carregando...</div>; 
    }
    
    return isAutenticado ? <Outlet /> : <Navigate to="/login" replace />;
};

export { PrivateRoute };
