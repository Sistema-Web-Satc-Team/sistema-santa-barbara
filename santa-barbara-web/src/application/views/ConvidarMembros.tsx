import { Badge } from '@/ui/components/badge';
import { InputSearch } from '@/ui/components/input-search';
import { Table } from '@/ui/components/table';
import { Filter, MoreVertical } from 'lucide-react';
import React, { useRef, useState } from 'react';
import { Button } from '../../ui/components/button';
import '../../ui/styles/ConvidarMembros.css';
import { useInvites } from '../hook/useInvites';



export const ConvidarMembros: React.FC = () => {
  const {
    formData,
    invites,
    errors,
    loading,
    availableRoles,
    handleInputChange,
    sendInvite,
    deleteInvite,
    resendInvite,
  } = useInvites();

  const [searchTerm, setSearchTerm] = useState('');
  const [sortAsc, setSortAsc] = useState(true);

  // Estados dos Filtros
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState<string>('todos');
  const [selectedRole, setSelectedRole] = useState<string>('todos');
  const filterRef = useRef<HTMLDivElement>(null);

  // Estado do Menu de Ações (...)
  const [activeMenuId, setActiveMenuId] = useState<string | number | null>(null);
  const menuRef = useRef<HTMLDivElement>(null);

  // Estado da Notificação Toast Moderna
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (message: string) => {
    setToastMessage(message);
    setTimeout(() => {
      setToastMessage(null);
    }, 3500);
  };


  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const success = await sendInvite();
    if (success) {
      showToast('Convite enviado com sucesso!');
    }
  };

  const handleResend = (id: string, email: string) => {
    resendInvite(id);
    setActiveMenuId(null);
    showToast(`Convite reenviado com sucesso para ${email}!`);
  };

  const handleDelete = (id: string) => {
    deleteInvite(id);
    setActiveMenuId(null);
    showToast('Convite removido.');
  };

  const hasActiveFilters = selectedStatus !== 'todos' || selectedRole !== 'todos';

  const resetFilters = () => {
    setSelectedStatus('todos');
    setSelectedRole('todos');
  };

  // Filtragem e ordenação
  const filteredInvites = invites
    .filter((invite) => {
      const matchesSearch = invite.email.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesStatus =
        selectedStatus === 'todos' ||
        invite.status.toLowerCase() === selectedStatus.toLowerCase();
      const matchesRole =
        selectedRole === 'todos' ||
        invite.roles.some((r) => r.toLowerCase() === selectedRole.toLowerCase());

      return matchesSearch && matchesStatus && matchesRole;
    })
    .sort((a, b) => {
      if (sortAsc) {
        return a.email.localeCompare(b.email);
      }
      return b.email.localeCompare(a.email);
    });

  return (
    <>
 

      <section className='flex-1 p-8 overflow-y-auto mb-10 min-h-[900px]'>


        {/* Notificação Toast Moderna */}
        {toastMessage && (
          <div className="modern-toast">
            <div className="toast-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M20 6L9 17l-5-5" />
              </svg>
            </div>
            <span className="toast-text">{toastMessage}</span>
            <button
              type="button"
              className="toast-close"
              onClick={() => setToastMessage(null)}
            >
              ×
            </button>
          </div>
        )}

        <h1 className="page-section-title">Convidar Novos Membros</h1>

        {/* Cartão com o formulário */}
        <div className="py-8">
          <form onSubmit={handleSubmit} className="invite-form" noValidate>
            <div className="form-group">
              <label>Email do Convidado</label>

              <InputSearch
                name="email"
                type="email"
                error={errors.email}
                placeholder="exemplo@gmail.com"
                value={formData.email}
                onChange={handleInputChange}
              />

            </div>

            <div className="form-actions">
              <Button type="submit" variant="normal" disabled={loading}>
                {loading ? 'A enviar...' : 'Enviar Convite'}
              </Button>
            </div>
          </form>
        </div>

        {/* Secção de Convites Recentes */}
        <h2 className="page-section-subtitle">Convites Recentes</h2>

        <div className="table-controls-bar">
          <div className="search-bar-wrapper">
            
            <InputSearch
              placeholder="Pesquisar por e-mail..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

        {/* Menu Dropdown de Filtros */}
        <div className="filter-dropdown-container" ref={filterRef}>
          <button
            type="button"
            className={`btn-filter ${hasActiveFilters ? 'btn-filter-active' : ''}`}
            onClick={() => setIsFilterOpen(!isFilterOpen)}
          >
            <Filter className='w-6 h-6'></Filter>
            <span>Filtros</span> {hasActiveFilters && <span className="filter-active-dot">•</span>}
          </button>

          {isFilterOpen && (
            <div className="filter-popup-card">
              <div className="filter-popup-header">
                <span>Filtrar Resultados</span>
                {hasActiveFilters && (
                  <button type="button" className="btn-filter-reset" onClick={resetFilters}>
                    Limpar
                  </button>
                )}
              </div>

              <div className="filter-popup-group">
                <label className="filter-popup-label">Status</label>
                <select
                  value={selectedStatus}
                  onChange={(e) => setSelectedStatus(e.target.value)}
                  className="filter-select"
                >
                  <option value="todos">Todos os estados</option>
                  <option value="enviado">Enviado</option>
                  <option value="reenviado">Reenviado</option>
                  <option value="rejeitado">Rejeitado</option>
                  <option value="falha">Falha</option>
                  <option value="aceito">Aceito</option>
                  <option value="expirado">Expirado</option>
                </select>
              </div>

              <div className="filter-popup-group">
                <label className="filter-popup-label">Papel / Função</label>
                <select
                  value={selectedRole}
                  onChange={(e) => setSelectedRole(e.target.value)}
                  className="filter-select"
                >
                  <option value="todos">Todos os papéis</option>
                  {availableRoles.map((role) => (
                    <option key={role} value={role}>
                      {role}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          )}
        </div>

          {/* Botão de Ordenação */}
          <button
            type="button"
            className="btn-sort"
            onClick={() => setSortAsc(!sortAsc)}
            title={sortAsc ? "A-Z (clique para Z-A)" : "Z-A (clique para A-Z)"}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M3 8L7 4M7 4L11 8M7 4V20" />
              <path d="M21 16L17 20M17 20L13 16M17 20V4" />
            </svg>
          </button>
        </div>

        {/* Tabela de Convites */}
        <Table
          data={filteredInvites}
          keyExtractor={(invite) => invite.id}
          columns={[
            { header: "E-mail convidado", accessor: "email" },
            { 
              header: "Papéis", 
              render: (invite) => (
                <div className="role-chips-list">
                  {invite.roles.map((r, i) => (
                    <span key={i} className="table-role-chip">{r}</span>
                  ))}
                </div>
              ) 
            },
            { 
              header: "Data do convite", 
              render: (invite) => (
                <div className="date-input-display">
                  <span>{invite.date}</span>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                    <line x1="16" y1="2" x2="16" y2="6" />
                    <line x1="8" y1="2" x2="8" y2="6" />
                    <line x1="3" y1="10" x2="21" y2="10" />
                  </svg>
                </div>
              ) 
            },
            { 
              header: "Status", 
              render: (invite) => <Badge>{invite.status}</Badge> 
            },
            {
              header: "",
              align: "right",
              render: (invite) => (
                <div
                  className="row-action-wrapper"
                  ref={activeMenuId === invite.id ? menuRef : null}
                >
                  <button
                    type="button"
                    className={`btn-dots ${activeMenuId === invite.id ? 'active' : ''}`}
                    onClick={() =>
                      setActiveMenuId(activeMenuId === invite.id ? null : invite.id)
                    }
                  >
                    <MoreVertical size={20} />
                  </button>

                   {activeMenuId === invite.id && (
                    <div className="action-popover-menu">
                      <button
                        type="button"
                        className="action-menu-item"
                        onClick={() => handleResend(invite.id, invite.email)}
                      >
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67" />
                        </svg>
                        Reenviar convite
                      </button>

                      <button
                        type="button"
                        className="action-menu-item item-danger"
                        onClick={() => handleDelete(invite.id)}
                      >
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <polyline points="3 6 5 6 21 6" />
                          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                        </svg>
                        Excluir convite
                      </button>
                    </div>
                  )}

                 
                </div>
              )
            }
          ]}
        />

        

      </section>
    </>
  );
};

export default ConvidarMembros;