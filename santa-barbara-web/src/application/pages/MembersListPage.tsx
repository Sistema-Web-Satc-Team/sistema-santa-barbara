import type { MemberData } from "@/application/model/MemberData";
import { memberService } from "@/application/services/member.service";
import { papelService } from "@/application/services/papel.service";
import { Badge } from "@/ui/components/badge";
import { Button } from "@/ui/components/button";
import { DropdownActions } from "@/ui/components/DropdownActions";
import { FilterDropdown } from "@/ui/components/Filterdropdown";
import { Pagination } from "@/ui/components/pagination";
import { SearchBox } from "@/ui/components/searchBox";
import { Table, type TableColumn } from "@/ui/components/table";
import { AlertCircle, ArrowDownAZ, ArrowUpAZ, Ban, Edit2, Mail, Plus, Settings2, Shield, UserPlus, Users } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { EditMemberModal } from "../components/EditMemberModal";

function formatPhone(phone: string) {
    const digits = phone.replace(/\D/g, "");

    if (digits.length === 11) {
        return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
    }

    if (digits.length === 10) {
        return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    }

    return phone;
}



export function MembersListPage() {
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedPapel, setSelectedPapel] = useState<string>("");
    const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(5);
    const [members, setMembers] = useState<MemberData[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [hasError, setHasError] = useState(false);
    const [isLastPage, setIsLastPage] = useState(false);
    const [availableRoles, setAvailableRoles] = useState<string[]>([]);

    const [statusFilter, setStatusFilter] = useState<string>();

    const [editingMember, setEditingMember] = useState<MemberData | null>(null);
    const [refreshKey, setRefreshKey] = useState(0);
    const filterRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
    const fetchRoles = async () => {
        try {
            const roles = await papelService.getPapeis();
            setAvailableRoles(roles);
        } catch (error) {
            console.error("Erro ao carregar papéis do sistema:", error);
        }
    };
    fetchRoles();
}, []);

    useEffect(() => {
        const fetchMembers = async () => {
            try {
                setIsLoading(true);
                const response = await memberService.getMembers({
                    page: currentPage,
                    limit: itemsPerPage,
                    papel: selectedPapel
                });
                
                setMembers(response.content);
                setIsLastPage(response.content.length < itemsPerPage);
                setHasError(false);
            } catch (error) {
                console.error("Erro ao buscar a lista de membros:", error);
                setHasError(true);
            } finally {
                setIsLoading(false);
            }
        };

        fetchMembers();
    }, [currentPage, itemsPerPage, selectedPapel, refreshKey]);


    const filteredMembers = members.filter((member) => {
        const normalizedSearch = searchTerm.toLowerCase();

        let nomeCompleto = member.nome.trim() + ' ' + member.sobrenome.trim();
        const matchesSearch = nomeCompleto.toLowerCase().includes(normalizedSearch)
            || member.email.toLowerCase().includes(normalizedSearch);
        const matchesStatus = !statusFilter || member.status === statusFilter;
        return matchesSearch && matchesStatus;
    });

    const sortedMembers = [...filteredMembers].sort((firstMember, secondMember) => {
        let nomeCompletoPrimeiroMembro = firstMember.nome.trim() + ' ' + firstMember.sobrenome.trim();
        let nomeCompletoSegundoMembro = secondMember.nome.trim() + ' ' + secondMember.sobrenome.trim();
        const result = nomeCompletoPrimeiroMembro.localeCompare(nomeCompletoSegundoMembro);
        return sortOrder === "asc" ? result : -result;
    });

    const totalPages = Math.max(1, Math.ceil(sortedMembers.length / itemsPerPage));
    const pageStart = (currentPage - 1) * itemsPerPage;
    const currentMembers = sortedMembers.slice(pageStart, pageStart + itemsPerPage);

    const membersColumns: TableColumn<MemberData>[] = [
        {
            header: "Nome",
            accessor: "nome",
            render: (member) => <span className="text-(--strong-foreground-color)">{member.nome} {member.sobrenome}</span>,
        },
        { header: "Papel", accessor: "papeis", render: (member) => <span className="capitalize">{member.papeis?.join(", ")}</span> },
        { header: "Telefone", accessor: "telefone", render: (member) => <span className="table-phone">{formatPhone(member.telefone)}</span> },
        { header: "E-Mail", accessor: "email" },
        {
            header: "Status",
            accessor: "status",
            render: (member) => (
                <Badge variant={member.status.toUpperCase() === "INATIVO" ? "inactive" : "active"}>
                    {member.status.toLowerCase()}
                </Badge>
            ),
        },
        {
            header: "Ações",
            width: "w-24",
            align: "center",
            render: (member) => (
                <div className="flex items-center justify-center gap-2">
                    <button
                        onClick={() => handleEditClick(member)}
                        className="p-1 hover:bg-(--strong-surface-color) rounded text-(--neutral-color) transition-colors"
                        title="Editar"
                    >
                        <Edit2 className="w-4 h-4" />
                    </button>
                    <DropdownActions
                        options={[
                            { label: "Detalhes", onClick: () => alert(`Detalhes ID: ${member.id}`) },
                            { label: "Enviar Convite", icon: <Mail className="w-3.5 h-3.5" />, onClick: () => alert(`Convite para: ${member.email}`) },
                            { label: "Bloquear", isDanger: true, icon: <Ban className="w-3.5 h-3.5" />, onClick: () => alert(`Bloqueou: ${member.id}`) },
                        ]}
                    />
                </div>
            ),
        },
    ];

    const changeItemsPerPage = (value: number) => {
        setItemsPerPage(value);
        setCurrentPage(1);
    };

    const handleEditClick = (member: MemberData) => {
        setEditingMember(member);
    };

    return (
        <div className="flex flex-1 w-full bg-(--surface-color) overflow-hidden">
            <aside className="w-80 bg-(--surface-color) border-r border-(--light-neutral-color) hidden md:flex flex-col">
                <nav className="flex flex-col">
                    <button className="flex items-center gap-4 px-6 py-5 bg-(--strong-surface-color) border-b border-(--light-neutral-color) font-semibold text-(--strong-foreground-color)">
                        <Users className="w-7 h-7" /> Membros
                    </button>
                    <button className="flex items-center gap-4 px-6 py-5 text-(--foreground-color) border-b border-(--light-neutral-color) hover:bg-(--strong-surface-color) transition-colors">
                        <UserPlus className="w-7 h-7" /> Convidar Membros
                    </button>
                    <button className="flex items-center gap-4 px-6 py-5 text-(--foreground-color) border-b border-(--light-neutral-color) hover:bg-(--strong-surface-color) transition-colors">
                        <Shield className="w-7 h-7" /> Papeis
                    </button>
                </nav>
            </aside>

            <section className="flex-1 p-8 overflow-y-auto">
                <div className="max-w-6xl mx-auto space-y-6">
                    <div className="flex justify-between items-center">
                        <h1 className="text-3xl font-normal text-(--strong-foreground-color)">Membros</h1>
                        <Button
                            variant="outline"
                            className="button-outline--brand-dark flex items-center gap-2 bg-(--strong-surface-color) border-(--light-neutral-color)"
                            onClick={() => alert("Redirecionando para rota de cadastro ainda não existente.")}
                        >
                            <Plus className="w-4 h-4" /> Cadastrar Membro
                        </Button>
                    </div>

                    <div className="flex justify-between items-center gap-4">
                        <div className="flex items-center gap-4 flex-1">
                            <SearchBox
                                value={searchTerm}
                                onChange={(value) => {
                                    setSearchTerm(value);
                                    setCurrentPage(1);
                                }}
                            />

                            <FilterDropdown
                                label="Filtros"
                                options={availableRoles}
                                selected={selectedPapel}
                                onSelect={(papel) => {
                                    setSelectedPapel(papel);
                                    setCurrentPage(1);
                                }}
                            />

                            <div className="flex items-center gap-1 border border-(--brand-color)/50 rounded bg-(--surface-color) p-0.5">
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
                            <Table data={members} columns={membersColumns} keyExtractor={(member) => String(member.id)} />
                            <Pagination
                                currentPage={currentPage}
                                totalPages={isLastPage ? currentPage : currentPage + 1}
                                itemsPerPage={itemsPerPage}
                                onPageChange={setCurrentPage}
                                onItemsPerPageChange={(event) => changeItemsPerPage(Number(event.target.value))}
                            />
                        </>
                    )}
                </div>
            </section>

            {editingMember && (
                <EditMemberModal
                    member={editingMember}
                    onClose={() => setEditingMember(null)}
                    onSuccess={() => {
                        setEditingMember(null);
                        setRefreshKey((key) => key + 1);
                    }}
                />
            )}
        </div>
    );
}

export default MembersListPage;