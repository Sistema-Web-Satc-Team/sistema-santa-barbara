import { UserProfile } from '#/components/business/user-profile'
import { DropdownMenu, DropdownMenuContent, DropdownMenuGroup, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from '#/components/ui/dropdown-menu'
import { Sidebar, SidebarContent, SidebarFooter, SidebarGroup, SidebarGroupContent, SidebarHeader, SidebarInset, SidebarMenu, SidebarMenuButton, SidebarMenuItem, SidebarProvider } from '#/components/ui/sidebar'
import { Spinner } from '#/components/ui/spinner'
import { useAuth } from '#/hooks/use-auth'
import { createFileRoute, Link, Navigate, Outlet, useNavigate } from '@tanstack/react-router'
import { Briefcase, GraduationCap, LogOut, Mail, Music2, Shield, Users, UserSquare2 } from 'lucide-react'
import { toast } from 'sonner'

export const Route = createFileRoute('/dashboard/_layout')({
  component: DashboardLayout,
})

function DashboardLayout() {
  const { data, state, actions } = useAuth();
  const navigate = useNavigate();
  
    if (state.isLoading) {
      return (
        <div className="w-full flex items-center justify-center min-h-screen">
          <Spinner />
        </div>
      )
    }
  
    if (!data?.id || data?.id == "") {
      return <Navigate to="/login" replace />
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
                  <SidebarMenuItem>
                    <SidebarMenuButton asChild isActive={location.pathname === "/dashboard/contatos"}>
                      <Link to="/dashboard/contatos">
                        <Users />
                        <span>Contatos</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>


                  <SidebarMenuItem>
                    <SidebarMenuButton asChild isActive={location.pathname === "/dashboard/alunos"}>
                      <Link to="/dashboard/alunos">
                        <UserSquare2 />
                        <span>Alunos</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>

                  <SidebarMenuItem>
                    <SidebarMenuButton asChild isActive={location.pathname === "/dashboard/professores"}>
                      <Link to="/dashboard/professores">
                        <GraduationCap />
                        <span>Professores</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>

                  <SidebarMenuItem>
                    <SidebarMenuButton asChild isActive={location.pathname === "/dashboard/funcionarios"}>
                      <Link to="/dashboard/funcionarios">
                        <Briefcase />
                        <span>Funcionários</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>


                  <SidebarMenuItem>
                    <SidebarMenuButton asChild isActive={location.pathname === "/dashboard/musicos"}>
                      <Link to="/dashboard/musicos">
                        <Music2 />
                        <span>Músicos</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>

                  <SidebarMenuItem>
                    <SidebarMenuButton asChild isActive={location.pathname === "/dashboard/membros/convites"}>
                      <Link to="/dashboard/convites">
                        <Mail />
                        <span>Convidar Membros</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>


                  <SidebarMenuItem>
                    <SidebarMenuButton asChild isActive={location.pathname === "/dashboard/papeis"}>
                      <Link to="/dashboard/papeis">
                        <Shield />
                        <span>Papeis</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
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
                    onClick={()=> {
                      navigate({ to: "/profile", replace: true });
                    }}
                  >Perfil</DropdownMenuLabel>
                </DropdownMenuGroup>
                <DropdownMenuSeparator className='p-0 m-0' />
                <DropdownMenuGroup>
                  <DropdownMenuLabel
                    onClick={async () => { 
                      try{
                        await actions.logout();
                        toast.success("Logout efetuado com sucesso.");
                        navigate({ to: "/login", replace: true });
                      } catch {
                        toast.error("Ocorreu um erro inesperado ao efetuar o logout.");
                      }
                    }} 
                    className='flex gap-2 items-center text-destructive font-normal px-2 py-2 transition-colors hover:bg-destructive/10'
                    >
                      <LogOut className='w-4 h-4'/> Logout
                    </DropdownMenuLabel>
                </DropdownMenuGroup>
              </DropdownMenuContent>
            </DropdownMenu>
          </SidebarFooter>
        </Sidebar>



        <SidebarInset className="flex flex-col flex-1 min-w-0">
          <main className="flex-1 min-w-0 p-4 md:p-8">
            <Outlet />
          </main>
        </SidebarInset>
      </SidebarProvider>
  )
}
