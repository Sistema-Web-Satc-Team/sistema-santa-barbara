import { UserProfile } from '#/components/business/user-profile'
import { DropdownMenu, DropdownMenuContent, DropdownMenuGroup, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from '#/components/ui/dropdown-menu'
import { Sidebar, SidebarContent, SidebarFooter, SidebarGroup, SidebarGroupContent, SidebarHeader, SidebarInset, SidebarMenu, SidebarProvider, SidebarTrigger } from '#/components/ui/sidebar'
import { Spinner } from '#/components/ui/spinner'
import { useAuth } from '#/hooks/use-auth'
import { Outlet, useNavigate } from '@tanstack/react-router'
import { LogOut } from 'lucide-react'
import type React from 'react'
import { toast } from 'sonner'

export function DashboardLayout({ children }: { children: React.ReactNode }) {

    const { state, actions } = useAuth();
    const navigate = useNavigate();

    if (state.isLoading) {
        return (
        <div className="w-full flex items-center justify-center min-h-screen">
            <Spinner />
        </div>
        )
    }


    return (
    <SidebarProvider>


        <Sidebar>
            <SidebarHeader />
            <SidebarContent>
            <SidebarGroup>
                <SidebarHeader>
                <div className="flex gap-2 mb-4">
                    <img
                    src="/assets/images/Brand.jpg"
                    alt="Brasão Santa Bárbara"
                    className="size-24 object-contain contrast-[0.94]"
                    />


                    <div className="flex flex-col mt-4 gap-0.5">
                    <h1 className="text-base font-bold text-foreground leading-tight tracking-tight">
                        Santa Bárbara
                    </h1>
                    <span className="text-xs text-muted-foreground">Sistema de Gestão</span>
                    </div>
                </div>
                </SidebarHeader>
                <SidebarGroupContent>
                <SidebarMenu>
                    { children }
                </SidebarMenu>
                </SidebarGroupContent>
            </SidebarGroup>
            </SidebarContent>
            <SidebarFooter>
            <DropdownMenu>
                <DropdownMenuTrigger>
                <UserProfile />
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
            </SidebarFooter>
        </Sidebar>



        <SidebarInset className="flex flex-col flex-1 min-w-0">
            <main className="flex-1 min-w-0 p-4 md:p-8">
                <div className="mb-4 flex items-center gap-2 md:hidden">
                    <SidebarTrigger aria-label="Abrir menu de navegação" className="size-9" />
                    <span className="text-sm font-medium">Menu</span>
                </div>
                <Outlet />
            </main>
        </SidebarInset>
        </SidebarProvider>
    )
}