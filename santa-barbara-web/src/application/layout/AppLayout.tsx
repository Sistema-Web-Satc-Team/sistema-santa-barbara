import { LogoIcon } from "@/ui/icons/LogoIcon";
import { Outlet } from "react-router";


function AppLayout() {
    return (
        <>
            <header>
                <nav className={`
                        flex flex-row 
                        bg-[var(--strong-surface-color)]
                        px-8 py-4
                `}>
                    <LogoIcon className={`
                        w-[64px] h-[62px]
                        lg:w-[72px] lg:h-[72px]
                        xl:w-[92px] xl:h-[92px]
                        2xl:w-[126px] 2xl:h-[126px]
                    `}/>
                </nav>
            </header>
            <main className={`
                w-sreen 
                flex flex-col
                min-h-[calc(100vh-64px)] lg:min-h-[calc(100vh-72px)] xl:min-h-[calc(100vh-92px)] 2xl:min-h-[calc(100vh-126px)]
            `}>
                <Outlet />
            </main>
        </>
    )
}

export default AppLayout;
