import { HomeLayout } from '#/components/layout/HomeLayout'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_auth/musico/_layout')({
  component: Layout,
})

function Layout() {
  return <HomeLayout />
}
