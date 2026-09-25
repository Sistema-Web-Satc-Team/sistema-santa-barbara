import React from 'react';
import '../styles/card-invitation.css';

// 1. Componente Card básico
export interface CardProps {
  title?: string;
  children: React.ReactNode;
  className?: string;
}

export const Card: React.FC<CardProps> = ({ title, children, className = '' }) => {
  return (
    <div className={`app-card ${className}`}>
      {title && <h2 className="app-card-title">{title}</h2>}
      <div className="app-card-body">{children}</div>
    </div>
  );
};

// 2. Componente StatusBadge (Pendente, Aceito, Expirado)
export interface StatusBadgeProps {
  status: 'Pendente' | 'Aceito' | 'Expirado' | string;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status }) => {
  const normalized = status.toLowerCase();
  return <span className={`status-badge status-${normalized}`}>{status}</span>;
};

// 3. Componente TableCard com cabeçalhos e linhas
export interface TableHeader {
  key: string;
  label: string;
}

export interface TableAction {
  id: string;
  label: string;
  icon?: React.ReactNode;
}

export interface TableCardProps {
  title?: string;
  headers: TableHeader[];
  rows: Record<string, any>[];
  actions?: TableAction[];
  onRowAction?: (actionId: string, row: any) => void;
  className?: string;
}

export const TableCard: React.FC<TableCardProps> = ({
  title,
  headers,
  rows,
  actions = [],
  onRowAction,
  className = '',
}) => {
  return (
    <div className={`app-card app-table-card ${className}`}>
      {title && <h2 className="app-card-title">{title}</h2>}
      <div className="table-responsive">
        <table className="custom-table">
          <thead>
            <tr>
              {headers.map((h) => (
                <th key={h.key}>{h.label}</th>
              ))}
              {actions.length > 0 && <th className="th-actions">Ações</th>}
            </tr>
          </thead>
          <tbody>
            {rows.length === 0 ? (
              <tr>
                <td colSpan={headers.length + (actions.length > 0 ? 1 : 0)} className="table-empty">
                  Nenhum registo encontrado.
                </td>
              </tr>
            ) : (
              rows.map((row, idx) => (
                <tr key={row.id || idx}>
                  {headers.map((h) => (
                    <td key={h.key}>
                      {h.key === 'status' ? (
                        <StatusBadge status={row[h.key]} />
                      ) : (
                        row[h.key]
                      )}
                    </td>
                  ))}
                  {actions.length > 0 && (
                    <td className="td-actions">
                      {actions.map((act) => (
                        <button
                          key={act.id}
                          type="button"
                          className="table-action-btn"
                          title={act.label}
                          onClick={() => onRowAction && onRowAction(act.id, row)}
                        >
                          {act.icon || act.label}
                        </button>
                      ))}
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Card;