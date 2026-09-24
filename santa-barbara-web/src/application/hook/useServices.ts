import { useContext } from "react";
import { ServiceContext } from "../context/service.context";

export function useServices() {
  const context = useContext(ServiceContext);
  if (!context) {
    throw new Error('useServices deve ser usado dentro de um ServiceProvider');
  }
  return context;
}