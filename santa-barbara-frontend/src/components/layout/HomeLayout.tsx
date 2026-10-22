import { Outlet } from "@tanstack/react-router";
import type React from "react";


export function HomeLayout({ children } : { children?: React.ReactNode}) {

    return <>
        <header className='px-4 py-4'>
          <img
            src="/assets/images/Brand.jpg"
            alt="Brasão Santa Bárbara"
            className="size-24 object-contain contrast-[0.94]"
          />
          {children}
        </header>
        <main className='px-16'>
          <Outlet />
        </main>
    </>
}