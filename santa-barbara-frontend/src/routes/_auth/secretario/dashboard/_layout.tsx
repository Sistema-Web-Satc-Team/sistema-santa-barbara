import { DashboardLayout } from '#/components/layout/DashboardLayout'
import { SidebarMenuButton, SidebarMenuItem } from '#/components/ui/sidebar'
import { createFileRoute, Link, useLocation } from '@tanstack/react-router'
import { GraduationCap, Mail, Music2, Users, UserSquare2 } from 'lucide-react'

export const Route = createFileRoute('/_auth/secretario/dashboard/_layout')({
  component: Layout,
})

function Layout() {
  const { pathname } = useLocation();


  return (
    <DashboardLayout>
      <SidebarMenuItem>
        <SidebarMenuButton asChild isActive={pathname === "/dashboard/membros"}>
          <Link to="/secretario/dashboard/membros">
            <Users />
            <span>Membros</span>
          </Link>
        </SidebarMenuButton>
      </SidebarMenuItem>


      <SidebarMenuItem>
        <SidebarMenuButton asChild isActive={pathname === "/dashboard/alunos"}>
          <Link to="/secretario/dashboard/alunos">
            <UserSquare2 />
            <span>Alunos</span>
          </Link>
        </SidebarMenuButton>
      </SidebarMenuItem>

      <SidebarMenuItem>
        <SidebarMenuButton asChild isActive={pathname === "/dashboard/professores"}>
          <Link to="/secretario/dashboard/professores">
            <GraduationCap />
            <span>Professores</span>
          </Link>
        </SidebarMenuButton>
      </SidebarMenuItem>

      <SidebarMenuItem>
        <SidebarMenuButton asChild isActive={pathname === "/dashboard/musicos"}>
          <Link to="/secretario/dashboard/musicos">
            <Music2 />
            <span>Músicos</span>
          </Link>
        </SidebarMenuButton>
      </SidebarMenuItem>

      <SidebarMenuItem>
        <SidebarMenuButton asChild isActive={pathname === "/dashboard/membros/convites"}>
          <Link to="/secretario/dashboard/convites" search={{ page: 0 }}>
            <Mail />
            <span>Convidar Membros</span>
          </Link>
        </SidebarMenuButton>
      </SidebarMenuItem>
    </DashboardLayout>
  )
}

