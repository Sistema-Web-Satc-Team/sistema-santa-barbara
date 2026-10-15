import { createFileRoute, Outlet, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/_auth')({
  beforeLoad: ({ context, location }) => {
    if (!context.profile) {
      throw redirect({ to: '/login', search: { redirect: location.href } })
    }
    return { profile: context.profile }
  },
  component: Outlet,
})