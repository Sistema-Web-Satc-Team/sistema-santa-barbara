import { UserPlus, Users } from "lucide-react";
import { Outlet, useLocation, useNavigate } from "react-router";

function DashBoardLayout() {
    const navigate = useNavigate();
    const location = useLocation();


    const isActive = (path: string) => location.pathname === path;

    return (
        <div className="flex flex-1 w-full bg-(--surface-color) overflow-hidden">
            <aside className="w-80 bg-(--surface-color) border-r border-(--light-neutral-color) hidden md:flex flex-col">
                <nav className="flex flex-col">
                    <button 
                        onClick={() => navigate("/dashboard/membros")}
                        className={`flex items-center gap-4 px-6 py-5 border-b border-(--light-neutral-color) transition-colors cursor-pointer ${
                            isActive("/dashboard/membros") 
                                ? "bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)" 
                                : "text-(--foreground-color) hover:bg-(--strong-surface-color)"
                        }`}
                    >
                        <Users className="w-7 h-7" /> Membros
                    </button>

                    <button 
                        onClick={() => navigate("/dashboard/membros/convidar")}
                        className={`flex items-center gap-4 px-6 py-5 border-b border-(--light-neutral-color) transition-colors cursor-pointer ${
                            isActive("/dashboard/membros/convidar") 
                                ? "bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)" 
                                : "text-(--foreground-color) hover:bg-(--strong-surface-color)"
                        }`}
                    >
                        <UserPlus className="w-7 h-7" /> Convidar Membros
                    </button>


                    {/*<button className="flex items-center gap-4 px-6 py-5 text-(--foreground-color) border-b border-(--light-neutral-color) hover:bg-(--strong-surface-color) transition-colors">
                        <Shield className="w-7 h-7" /> Papeis
                    </button>*/}
                </nav>
            </aside>

            <Outlet />
        </div>
    );
}

export default DashBoardLayout;