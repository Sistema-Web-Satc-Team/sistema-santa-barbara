import { useState } from "react";
import { Search } from "lucide-react";
import { Convite } from "@/application/model/ConviteModel";
import "@/ui/styles/ConviteTable.css";

export interface ConviteTableProps {
  convites: Convite[];
  carregando?: boolean;
}

export function ConviteTable({ convites, carregando = false }: ConviteTableProps) {
  const [busca, setBusca] = useState("");

  const convitesFiltrados = convites.filter((convite) =>
    convite.emailConvidado.toLowerCase().includes(busca.toLowerCase())
  );

  function obterClasseStatus(status: string): string {
    const classMap: Record<string, string> = {
      PENDENTE: "convite-table__status--pendente",
      ACEITO: "convite-table__status--aceito",
      EXPIRADO: "convite-table__status--expirado",
      RECUSADO: "convite-table__status--recusado",
    };
    return classMap[status] || "convite-table__status--pendente";
  }

  function formatarData(data: Date): string {
    return new Date(data).toLocaleDateString("pt-BR");
  }

  function traduzirPapel(papel: string): string {
    const papelMap: Record<string, string> = {
      ALUNO: "Aluno",
      PROFESSOR: "Professor",
      ADMINISTRADOR: "Administrador",
    };
    return papelMap[papel] || papel;
  }

  function traduzirStatus(status: string): string {
    const statusMap: Record<string, string> = {
      PENDENTE: "Pendente",
      ACEITO: "Aceito",
      EXPIRADO: "Expirado",
      RECUSADO: "Recusado",
    };
    return statusMap[status] || status;
  }

  if (carregando) {
    return <div className="convite-table__loading">Carregando convites...</div>;
  }

  return (
    <div className="convite-table">
      <div className="convite-table__search">
        <Search className="convite-table__search-icon" size={20} />
        <input
          type="text"
          placeholder="Buscar convite..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
          className="convite-table__search-input"
        />
      </div>

      {convitesFiltrados.length === 0 ? (
        <div className="convite-table__empty">
          {convites.length === 0
            ? "Nenhum convite enviado ainda"
            : "Nenhum convite encontrado"}
        </div>
      ) : (
        <div className="convite-table__wrapper">
          <table className="convite-table__content">
            <thead>
              <tr>
                <th>E-mail convidado</th>
                <th>Papel Designado</th>
                <th>Data do Convite</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {convitesFiltrados.map((convite) => (
                <tr key={convite.id}>
                  <td>{convite.emailConvidado}</td>
                  <td>{traduzirPapel(convite.papelDesignado)}</td>
                  <td>{formatarData(convite.dataConvite)}</td>
                  <td>
                    <span
                      className={`convite-table__status ${obterClasseStatus(
                        convite.status
                      )}`}
                    >
                      {traduzirStatus(convite.status)}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
