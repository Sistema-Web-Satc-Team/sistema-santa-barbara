import { createContext } from "react";
import type { ServiceContainer } from "./service.container";

export const ServiceContext = createContext<ServiceContainer | null>(null);

