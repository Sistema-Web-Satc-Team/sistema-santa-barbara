import { SidebarMenuButton, SidebarMenuItem } from '#/components/ui/sidebar'
import { createFileRoute, Link, redirect, useLocation } from '@tanstack/react-router'
import { GraduationCap, Guitar, Mail, Music2, Shield, Users, UserSquare2 } from 'lucide-react'

import { DashboardLayout } from '#/components/layout/DashboardLayout'

export const Route = createFileRoute('/_auth/_admin/dashboard/_layout')({
  component: Layout,
  beforeLoad: ({ context }) => {
    if (!context.profile.funcoes.some((funcao) => funcao === 'SUPER_ADMIN' || funcao === 'ADMIN')) {
      throw redirect({ to: '/' })
    }
  },
})

function Layout() {
  const { pathname } = useLocation();

  return (
      <DashboardLayout>
          <SidebarMenuItem>
            <SidebarMenuButton asChild isActive={pathname === "/dashboard/membros"}>
              <Link to="/dashboard/membros">
                <Users />
                <span>Membros</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>

          <SidebarMenuItem>
            <SidebarMenuButton asChild isActive={pathname === "/dashboard/alunos"}>
              <Link to="/dashboard/alunos">
                <UserSquare2 />
                <span>Alunos</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>

          <SidebarMenuItem>
            <SidebarMenuButton asChild isActive={pathname === "/dashboard/professores"}>
              <Link to="/dashboard/professores">
                <GraduationCap />
                <span>Professores</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>

          <SidebarMenuItem>
            <SidebarMenuButton asChild isActive={pathname === "/dashboard/musicos"}>
              <Link to="/dashboard/musicos">
                <Music2 />
                <span>Músicos</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>

          <SidebarMenuItem>
            <SidebarMenuButton asChild isActive={pathname === "/dashboard/membros/convites"}>
              <Link to="/dashboard/convites" search={{ page: 0 }}>
                <Mail />
                <span>Convidar Membros</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>

          <SidebarMenuItem>
            <SidebarMenuButton asChild isActive={pathname === "/dashboard/funcoes"}>
              <Link to="/dashboard/funcoes">
                <Shield />
                <span>Funções</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>

          <SidebarMenuItem>
            <SidebarMenuButton asChild isActive={pathname === "/dashboard/musicas"}>
              <Link to="/dashboard/musicas">
                <Music2 />
                <span>Músicas</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>

          <SidebarMenuItem>
            <SidebarMenuButton asChild isActive={location.pathname === "/dashboard/instrumentos"}>
              <Link to="/dashboard/instrumentos">
                <Guitar />
                <span>Instrumentos</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>
      </DashboardLayout>
  )
}
