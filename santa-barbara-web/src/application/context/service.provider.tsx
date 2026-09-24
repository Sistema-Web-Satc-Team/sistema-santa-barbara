import { globalServices } from "./service.container";
import { ServiceContext } from "./service.context";

export function ServiceProvider({ children }: { children: React.ReactNode }) {
  return (
    <ServiceContext.Provider value={globalServices}>
      {children}
    </ServiceContext.Provider>
  );
}
