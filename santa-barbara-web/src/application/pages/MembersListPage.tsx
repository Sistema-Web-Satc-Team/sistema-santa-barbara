import { useEffect, useRef, useState } from "react";
import { AlertCircle, ArrowDownAZ, ArrowUpAZ, Ban, Edit2, Filter, Mail, Plus, Search, Settings2, Shield, UserPlus, Users } from "lucide-react";
import { memberService } from "@/application/services/member.service";
import type { MemberData } from "@/application/model/MemberData";
import { Input } from "@/ui/components/input";
import { Button } from "@/ui/components/button";
import { DropdownActions } from "@/ui/components/DropdownActions";
import { Pagination } from "@/ui/components/pagination";
import { Table, type TableColumn } from "@/ui/components/table";

export function MembersListPage() {
    const [searchTerm, setSearchTerm] = useState("");
    const [statusFilter, setStatusFilter] = useState("");
    const [isFilterOpen, setIsFilterOpen] = useState(false);
    const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(5);
    const [members, setMembers] = useState<MemberData[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [hasError, setHasError] = useState(false);
    const filterRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
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
                setMembers(await memberService.getAllMembers());
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

    const filteredMembers = members.filter((member) => {
        const normalizedSearch = searchTerm.toLowerCase();
        const matchesSearch = member.fullName.toLowerCase().includes(normalizedSearch)
            || member.email.toLowerCase().includes(normalizedSearch);
        const matchesStatus = !statusFilter || member.status === statusFilter;
        return matchesSearch && matchesStatus;
    });

    const sortedMembers = [...filteredMembers].sort((firstMember, secondMember) => {
        const result = firstMember.fullName.localeCompare(secondMember.fullName);
        return sortOrder === "asc" ? result : -result;
    });

    const totalPages = Math.max(1, Math.ceil(sortedMembers.length / itemsPerPage));
    const pageStart = (currentPage - 1) * itemsPerPage;
    const currentMembers = sortedMembers.slice(pageStart, pageStart + itemsPerPage);

    const membersColumns: TableColumn<MemberData>[] = [
        {
            header: "Nome",
            accessor: "fullName",
            render: (member) => <span className="text-(--strong-foreground-color)">{member.fullName}</span>,
        },
        { header: "Papel", accessor: "role", render: (member) => <span className="capitalize">{member.role}</span> },
        { header: "Telefone", accessor: "phone" },
        { header: "E-Mail", accessor: "email" },
        { header: "Status", accessor: "status" },
        {
            header: "Ações",
            width: "w-24",
            align: "center",
            render: (member) => (
                <div className="flex items-center justify-center gap-2">
                    <button
                        onClick={() => alert(`Editar ID: ${member.id}`)}
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
                                    placeholder="Pesquisar..."
                                    className="pl-10 w-full bg-(--surface-color)"
                                    value={searchTerm}
                                    onChange={(event) => {
                                        setSearchTerm(event.target.value);
                                        setCurrentPage(1);
                                    }}
                                />
                            </div>

                            <div className="relative" ref={filterRef}>
                                <Button
                                    variant="outline"
                                    className="flex items-center gap-2 bg-(--surface-color) border-(--light-neutral-color)"
                                    onClick={() => setIsFilterOpen((isOpen) => !isOpen)}
                                >
                                    Filtros {statusFilter ? `(${statusFilter})` : ""} <Filter className="w-4 h-4" />
                                </Button>
                                {isFilterOpen && (
                                    <div className="absolute top-full mt-2 left-0 w-48 bg-(--surface-color) border border-(--light-neutral-color) rounded-md shadow-lg z-50 py-1 text-left">
                                        <div className="px-4 py-2 text-xs font-semibold text-(--neutral-color) border-b border-(--light-neutral-color)">Status</div>
                                        {["", "ativo", "inativo"].map((status) => (
                                            <button
                                                key={status || "todos"}
                                                onClick={() => {
                                                    setStatusFilter(status);
                                                    setIsFilterOpen(false);
                                                    setCurrentPage(1);
                                                }}
                                                className={`w-full px-4 py-2 text-sm text-left ${statusFilter === status ? "bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)" : "text-(--neutral-color) hover:bg-(--strong-surface-color)"}`}
                                            >
                                                {status || "Todos"}
                                            </button>
                                        ))}
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
                            <Table data={currentMembers} columns={membersColumns} keyExtractor={(member) => member.id} />
                            <Pagination
                                currentPage={currentPage}
                                totalPages={totalPages}
                                itemsPerPage={itemsPerPage}
                                onPageChange={setCurrentPage}
                                onItemsPerPageChange={(event) => changeItemsPerPage(Number(event.target.value))}
                            />
                        </>
                    )}
                </div>
            </section>
        </div>
    );
}

export default MembersListPage;