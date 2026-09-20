import { useEffect, useState } from "react";
import { AlertCircle, ArrowDownAZ, ArrowUpAZ, Ban, Edit2, Mail, Plus, Settings2, Shield, UserPlus, Users } from "lucide-react";
import { memberService } from "@/application/services/member.service";
import type { MemberData } from "@/application/model/MemberData";
import { Button } from "@/ui/components/button";
import { DropdownActions } from "@/ui/components/DropdownActions";
import { FilterDropdown } from "@/ui/components/Filterdropdown";
import { Pagination } from "@/ui/components/pagination";
import { SearchBox } from "@/ui/components/searchBox";
import { Table, type TableColumn } from "@/ui/components/table";
import { papelService } from "@/application/services/papel.service";

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
    }, [currentPage, itemsPerPage, selectedPapel]);

    const membersColumns: TableColumn<MemberData>[] = [
        {
            header: "Nome",
            accessor: "nome",
            render: (member) => <span className="text-(--strong-foreground-color)">{member.nome} {member.sobrenome}</span>,
        },
        { header: "Papel", accessor: "papeis", render: (member) => <span className="capitalize">{member.papeis?.join(", ")}</span> },
        { header: "Telefone", accessor: "telefone" },
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
        </div>
    );
}

export default MembersListPage;