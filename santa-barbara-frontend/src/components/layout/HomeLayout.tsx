import { Outlet, useNavigate, useLocation, redirect } from "@tanstack/react-router";
import type React from "react";
import { UserProfile } from "../business/user-profile";
import { DropdownMenuSeparator, DropdownMenuContent, DropdownMenuTrigger, DropdownMenuGroup, DropdownMenuLabel, DropdownMenu } from '#/components/ui/dropdown-menu'
import { useAuth } from "#/hooks/use-auth";
import { toast } from "sonner";
import { LogOut } from "lucide-react";


export function HomeLayout({ children } : { children?: React.ReactNode}) {
    const { state, actions } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    return <>
        <header className='px-4 py-4 border-b mb-8'>
          <nav className="flex">
            <img
              src="/assets/images/Brand.jpg"
              alt="Brasão Santa Bárbara"
              className="size-24 object-contain contrast-[0.94]"
            />
            {children}

            
            <DropdownMenu>
              <DropdownMenuTrigger className="ml-auto">
                <UserProfile withChevronsUpDown={false} avatarSide="Right" size="Large"/>
              </DropdownMenuTrigger>
              <DropdownMenuContent className='cursor-pointer drop-shadow-card-foreground min-w-40 p-0'>
                <DropdownMenuGroup className='p-0'>
                    <DropdownMenuLabel className='font-normal text-foreground transition-colors hover:bg-primary/10 px-2 py-2'
                    onClick={() => {
                        navigate({ 
                          to: "/profile", 
                          replace: true,
                          search: {
                            redirect: location.href
                          } 
                        });
                    }}
                    >Perfil</DropdownMenuLabel>
                </DropdownMenuGroup>
              <DropdownMenuSeparator className='p-0 m-0' />

                {state.profile && state.profile.funcoes.length > 1 && (
                    <DropdownMenuGroup className="p-0">
                        <DropdownMenuLabel
                            className="px-2 py-2 font-normal text-foreground transition-colors hover:bg-primary/10 cursor-pointer"
                            onClick={() => navigate({ to: "/", replace: true })}
                        >
                            Mudar de função
                        </DropdownMenuLabel>
                    </DropdownMenuGroup>
                )}

                <DropdownMenuGroup>
                    <DropdownMenuLabel
                    onClick={async () => {
                        try {
                          await actions.logout();
                          toast.success("Logout efetuado com sucesso.");
                          navigate({ to: "/login", replace: true });
                        } catch {
                          toast.error("Ocorreu um erro inesperado ao efetuar o logout.");
                        }
                    }}
                    className='flex gap-2 items-center text-destructive font-normal px-2 py-2 transition-colors hover:bg-destructive/10'
                    >
                    <LogOut className='w-4 h-4' /> Logout
                    </DropdownMenuLabel>
                </DropdownMenuGroup>
              </DropdownMenuContent>
            </DropdownMenu>
          </nav>
        </header>
        <main className='px-4 sm:px-6 lg:px-16'>
          <Outlet />
        </main>
    </>
}
