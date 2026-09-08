import { useState, useEffect, useRef } from "react";
import { memberService } from "@/application/services/member.service";
import type { MemberData } from "@/application/model/MemberData";
import { Input } from "@/ui/components/input";
import { Button } from "@/ui/components/button";
import { 
    Search, MoreVertical, Users, UserPlus, Shield, 
    AlertCircle, Filter, Settings2, Plus, Edit2, Mail, Ban, ArrowDownAZ, ArrowUpAZ
} from "lucide-react";

export function MembersListPage() {
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(5);
    const [searchTerm, setSearchTerm] = useState("");
    const [members, setMembers] = useState<MemberData[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [hasError, setHasError] = useState(false);
    const [openMenuId, setOpenMenuId] = useState<string | null>(null);
    const [statusFilter, setStatusFilter] = useState(""); 
    const [isFilterOpen, setIsFilterOpen] = useState(false);
   const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc"); // "asc" = A-Z, "desc" = Z-A

    
    const filterRef = useRef<HTMLDivElement>(null);
    const menuRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setOpenMenuId(null);
            }
           
            if (filterRef.current && !filterRef.current.contains(event.target as Node)) {
                setIsFilterOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);
    useEffect(() => {
        const fetchMembers = async () => {
            try {
                setIsLoading(true);
                const data = await memberService.getAllMembers();
                setMembers(data);
                setHasError(false);
            } catch (error) {
                console.error("Erro ao buscar a lista de membros:", error);
                setHasError(true);
            } finally {
                setIsLoading(false);
            }
        };

        fetchMembers();
    }, []);

    const handleActionClick = (action: string, memberId: string) => {
        setOpenMenuId(null);
        alert(`Ação "${action}" acionada para o membro ID: ${memberId}`);
    };

    const filteredMembers = members.filter(member => {
        const matchesSearch = member.fullName.toLowerCase().includes(searchTerm.toLowerCase()) || 
                              member.email.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesStatus = statusFilter === "" || member.status === statusFilter;
        
        return matchesSearch && matchesStatus;
    });


    const sortedMembers = [...filteredMembers].sort((a, b) => {
        if (sortOrder === "asc") {
            return a.fullName.localeCompare(b.fullName);
        }
        return b.fullName.localeCompare(a.fullName);
    });


    const totalPages = Math.ceil(sortedMembers.length / itemsPerPage) || 1;
    const startIndex = (currentPage - 1) * itemsPerPage;
    const currentMembers = sortedMembers.slice(startIndex, startIndex + itemsPerPage);

    const goToPage = (page: number) => {
        if (page >= 1 && page <= totalPages) setCurrentPage(page);
    };

    const handleItemsPerPageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setItemsPerPage(Number(e.target.value));
        setCurrentPage(1); 
    };



    const getPageNumbers = () => {
        const pages = [];
        let start = Math.max(1, currentPage - 2);
        const end = Math.min(totalPages, start + 4);
        if (end - start < 4) start = Math.max(1, end - 4);

        for (let i = start; i <= end; i++) {
            pages.push(i);
        }
        return pages;
    };

    return (
        <div className="flex flex-1 w-full bg-(--surface-color) overflow-hidden">
            
            <aside className="w-64 bg-(--surface-color) border-r border-(--light-neutral-color) hidden md:flex flex-col py-6">
                <nav className="flex flex-col gap-2 px-4">
                    <button className="flex items-center gap-3 px-4 py-3 bg-(--strong-surface-color) rounded-lg font-semibold text-(--strong-foreground-color)">
                        <Users className="w-5 h-5" /> Membros
                    </button>
                    <button className="flex items-center gap-3 px-4 py-3 text-(--foreground-color) hover:bg-(--strong-surface-color) rounded-lg transition-colors">
                        <UserPlus className="w-5 h-5" /> Convidar Membros
                    </button>
                    <button className="flex items-center gap-3 px-4 py-3 text-(--foreground-color) hover:bg-(--strong-surface-color) rounded-lg transition-colors">
                        <Shield className="w-5 h-5" /> Papeis
                    </button>
                </nav>
            </aside>

            <section className="flex-1 p-8 overflow-y-auto">
                <div className="max-w-6xl mx-auto space-y-6">
                    <div className="flex justify-between items-center">
                        <h1 className="text-3xl font-normal text-(--strong-foreground-color)">Membros</h1>
                        <Button 
                            variant="outline" 
                            className="flex items-center gap-2 bg-(--strong-surface-color) text-(--strong-foreground-color) border-(--light-neutral-color)"
                            onClick={() => alert("Redirecionando para rota de cadastro ainda não existente.")}
                        >
                            <Plus className="w-4 h-4" /> Cadastrar Membro
                        </Button>
                    </div>

                    <div className="flex justify-between items-center gap-4">
                        <div className="flex items-center gap-4 flex-1">
                            <div className="relative w-72">
                                <Search className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-(--light-neutral-color)" />
                                <Input 
                                    placeholder="Placeholder" 
                                    className="pl-10 w-full bg-(--surface-color)"
                                    value={searchTerm}
                                    onChange={(e) => {
                                         setSearchTerm(e.target.value);
                                             setCurrentPage(1); 
                                         }}
                                />
                            </div>
                        
                            <div className="relative" ref={filterRef}>
                                <Button 
                                    variant="outline" 
                                    className="flex items-center gap-2 bg-(--surface-color) border-(--light-neutral-color)"
                                    onClick={() => setIsFilterOpen(!isFilterOpen)}
                                >
                                    Filtros {statusFilter ? `(${statusFilter})` : ""} <Filter className="w-4 h-4" />
                                </Button>

                                
                                {isFilterOpen && (
                                    <div className="absolute top-full mt-2 left-0 w-48 bg-(--surface-color) border border-(--light-neutral-color) rounded-md shadow-lg z-50 py-1 text-left">
                                        <div className="px-4 py-2 text-xs font-semibold text-(--neutral-color) border-b border-(--light-neutral-color)">
                                            Status
                                        </div>
                                        <button 
                                            onClick={() => { setStatusFilter(""); setIsFilterOpen(false); setCurrentPage(1); }}
                                            className={`w-full px-4 py-2 text-sm text-left ${statusFilter === "" ? "bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)" : "text-(--neutral-color) hover:bg-(--strong-surface-color)"}`}
                                        >
                                            Todos
                                        </button>
                                        <button 
                                            onClick={() => { setStatusFilter("Ativo"); setIsFilterOpen(false); setCurrentPage(1); }}
                                            className={`w-full px-4 py-2 text-sm text-left ${statusFilter === "Ativo" ? "bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)" : "text-(--neutral-color) hover:bg-(--strong-surface-color)"}`}
                                        >
                                            Ativo
                                        </button>
                                        <button 
                                            onClick={() => { setStatusFilter("Inativo"); setIsFilterOpen(false); setCurrentPage(1); }}
                                            className={`w-full px-4 py-2 text-sm text-left ${statusFilter === "Inativo" ? "bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)" : "text-(--neutral-color) hover:bg-(--strong-surface-color)"}`}
                                        >
                                            Inativo
                                        </button>
                                    </div>
                                )}
                            </div>
                         
                            <div className="flex items-center gap-1 border border-(--light-neutral-color) rounded bg-(--surface-color) p-0.5">
                                <button 
                                    onClick={() => { setSortOrder("asc"); setCurrentPage(1); }}
                                    className={`p-1.5 rounded transition-colors ${sortOrder === "asc" ? "bg-(--strong-surface-color) text-(--strong-foreground-color)" : "text-(--neutral-color) hover:bg-(--strong-surface-color)"}`}
                                    title="Ordem Crescente (A-Z)"
                                >
                                    <ArrowDownAZ className="w-5 h-5" />
                                </button>
                                <button 
                                    onClick={() => { setSortOrder("desc"); setCurrentPage(1); }}
                                    className={`p-1.5 rounded transition-colors ${sortOrder === "desc" ? "bg-(--strong-surface-color) text-(--strong-foreground-color)" : "text-(--neutral-color) hover:bg-(--strong-surface-color)"}`}
                                    title="Ordem Decrescente (Z-A)"
                                >
                                    <ArrowUpAZ className="w-5 h-5" />
                                </button>
                            </div>
                        </div>
                        <Button variant="ghost" className="flex items-center gap-2 text-(--neutral-color) hover:text-(--strong-foreground-color)">
                            <Settings2 className="w-4 h-4" /> Colunas
                        </Button>
                    </div>

                    <div>
                        {isLoading ? (
                            <div className="flex justify-center items-center py-12 text-(--neutral-color) font-semibold border border-(--light-neutral-color) rounded-lg bg-(--surface-color)">
                                Buscando membros no servidor...
                            </div>
                        ) : hasError ? (
                            <div className="flex flex-col items-center justify-center py-12 text-(--neutral-color) border border-(--light-neutral-color) rounded-lg bg-(--surface-color)">
                                <AlertCircle className="w-12 h-12 text-(--error-color) mb-3" />
                                <p className="font-semibold text-(--strong-foreground-color)">Não foi possível carregar os membros</p>
                                <p className="text-sm">Aguardando disponibilidade da rota do backend.</p>
                            </div>
                        ) : members.length === 0 ? (
                            <div className="flex justify-center py-12 text-(--neutral-color) font-semibold border border-(--light-neutral-color) rounded-lg bg-(--surface-color)">
                                Nenhum membro encontrado.
                            </div>
                        ) : (
                            <>
                                <div className="border border-(--light-neutral-color) rounded-lg bg-(--surface-color) overflow-visible">
                                    <table className="w-full text-left border-collapse text-sm">
                                        <thead className="bg-(--strong-surface-color) text-(--neutral-color)">
                                            <tr>
                                                <th className="p-3 border-b border-(--light-neutral-color) font-semibold">Nome</th>
                                                <th className="p-3 border-b border-(--light-neutral-color) font-semibold">Papel</th>
                                                <th className="p-3 border-b border-(--light-neutral-color) font-semibold">Telefone</th>
                                                <th className="p-3 border-b border-(--light-neutral-color) font-semibold">E-Mail</th>
                                                <th className="p-3 border-b border-(--light-neutral-color) font-semibold">Status</th>
                                                <th className="p-3 border-b border-(--light-neutral-color) w-20 text-center">Ações</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {currentMembers.map((member, index) => (
                                                <tr key={member.id} className={`${index % 2 === 0 ? 'bg-(--surface-color)' : 'bg-(--strong-surface-color)'} hover:bg-(--light-neutral-color) transition-colors relative`}>
                                                    <td className="p-3 text-(--strong-foreground-color)">{member.fullName}</td>
                                                    <td className="p-3 text-(--neutral-color) capitalize">{member.role}</td>
                                                    <td className="p-3 text-(--neutral-color)">{member.phone}</td>
                                                    <td className="p-3 text-(--neutral-color)">{member.email}</td>
                                                    <td className="p-3 text-(--neutral-color)">{member.status}</td>
                                                    <td className="p-3 text-center flex items-center justify-center gap-2 relative">
                                                        <button 
                                                            onClick={() => alert(`Editar ID: ${member.id}`)}
                                                            className="p-1 hover:bg-(--strong-surface-color) rounded text-(--neutral-color) transition-colors"
                                                            title="Editar"
                                                        >
                                                            <Edit2 className="w-4 h-4" />
                                                        </button>
                                                        <div className="relative">
                                                            <button 
                                                                onClick={() => setOpenMenuId(openMenuId === member.id ? null : member.id)}
                                                                className="p-1 hover:bg-(--strong-surface-color) rounded text-(--neutral-color) transition-colors"
                                                                title="Mais opções"
                                                            >
                                                                <MoreVertical className="w-4 h-4" />
                                                            </button>

                                                            {openMenuId === member.id && (
                                                                <div ref={menuRef} className="absolute right-0 mt-2 w-44 bg-(--surface-color) border border-(--light-neutral-color) rounded-md shadow-lg z-50 py-1 text-left">
                                                                    <button 
                                                                        onClick={() => handleActionClick("Detalhes", member.id)}
                                                                        className="w-full px-4 py-2 text-xs text-(--neutral-color) hover:bg-(--strong-surface-color) flex items-center gap-2"
                                                                    >
                                                                        Detalhes
                                                                    </button>
                                                                    <button 
                                                                        onClick={() => handleActionClick("Enviar Convite", member.id)}
                                                                        className="w-full px-4 py-2 text-xs text-(--neutral-color) hover:bg-(--strong-surface-color) flex items-center gap-2"
                                                                    >
                                                                        <Mail className="w-3.5 h-3.5" /> Enviar Convite
                                                                    </button>
                                                                    <button 
                                                                        onClick={() => handleActionClick("Bloquear", member.id)}
                                                                        className="w-full px-4 py-2 text-xs text-(--error-color) hover:bg-(--strong-surface-color) flex items-center gap-2"
                                                                    >
                                                                        <Ban className="w-3.5 h-3.5" /> Bloquear
                                                                    </button>
                                                                </div>
                                                            )}
                                                        </div>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>

                                <div className="flex items-center justify-between mt-4 text-sm">
                                    <div className="flex border border-(--light-neutral-color) rounded overflow-hidden bg-(--surface-color)">
        
     
                                    <button onClick={() => goToPage(1)} disabled={currentPage === 1} className="px-3 py-1.5 border-r border-(--light-neutral-color) hover:bg-(--strong-surface-color) text-(--neutral-color) disabled:opacity-50">
                                         &lt;&lt; First
                                        </button>
                                            <button onClick={() => goToPage(currentPage - 1)} disabled={currentPage === 1} className="px-3 py-1.5 border-r border-(--light-neutral-color) hover:bg-(--strong-surface-color) text-(--neutral-color) disabled:opacity-50">
                                                 &lt; Previous
                                            </button>

        
                                            {getPageNumbers().map((page) => (
                                              <button 
                                          key={page}
                                         onClick={() => goToPage(page)}
                                              className={`px-3.5 py-1.5 border-r border-(--light-neutral-color) ${currentPage === page ? 'bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)' : 'hover:bg-(--strong-surface-color) text-(--neutral-color)'}`}
            >
                                                    {page}
                                              </button>
                                                    ))}

        
                                        <button onClick={() => goToPage(currentPage + 1)} disabled={currentPage === totalPages} className="px-3.5 py-1.5 border-r border-(--light-neutral-color) hover:bg-(--strong-surface-color) text-(--neutral-color) disabled:opacity-50">
                                          Next &gt;
                                         </button>
                                          <button onClick={() => goToPage(totalPages)} disabled={currentPage === totalPages} className="px-3.5 py-1.5 hover:bg-(--strong-surface-color) text-(--neutral-color) disabled:opacity-50">
                                         Last &gt;&gt;
                                          </button>
                                         </div>


                                         <select 
                                         value={itemsPerPage}
                                                 onChange={handleItemsPerPageChange}
                                                      className="border border-(--light-neutral-color) rounded px-2 py-1.5 bg-(--surface-color) text-(--neutral-color) outline-none"
    >
                                                    <option value="5">5</option>
                                                 <option value="10">10</option>
                                               <option value="20">20</option>
                                                     </select>
                                                </div>
                            </>
                        )}
                    </div>
                </div>
            </section>
        </div>
    );
}

export default MembersListPage;