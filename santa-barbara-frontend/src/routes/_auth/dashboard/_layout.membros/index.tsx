import { MemberFormDialog } from "#/components/business/membros/member-form-dialog";
import { MemberTable } from "#/components/business/membros/member-table";
import { Button } from '#/components/ui/button';
import { ButtonGroup } from "#/components/ui/button-group";
import { Field, FieldLabel } from '#/components/ui/field';
import { Input } from "#/components/ui/input";
import { Spinner } from '#/components/ui/spinner';
import { useMembers } from "#/hooks/use-members";
import type { MemberFormValues } from "#/schemas/form-schema-member";
import { createFileRoute } from '@tanstack/react-router';
import { UserPlus } from "lucide-react";
import { useRef, useState } from "react";
import { registrarMembro } from "./registrar-membro";

export const Route = createFileRoute('/_auth/dashboard/_layout/membros/')({
  component: Membros,
})

function Membros() {
    const { state, actions } = useMembers();
    const searchTermRef = useRef<string>("")
    const [isOpen, setIsOpen] = useState(false);



    if (state.isLoading) {
        return <div className="w-full flex items-center justify-center min-h-screen">
            <Spinner />
        </div>
    }




    return (
        <>

            <div className="flex flex-row mb-8">
                <h2 className="text-3xl font-bold mr-auto">Membros</h2>

                <div>
                    <Button onClick={() => setIsOpen(true)} className="text-primary-foreground font-semibold">
                        <UserPlus />
                        Novo Membro
                    </Button>
                    <MemberFormDialog onSubmit={async (values: MemberFormValues) => await registrarMembro(values) } open={isOpen} onOpenChange={setIsOpen} />
                </div>
            </div>

           
   
            <Field className="mb-4">
                <FieldLabel htmlFor="input-button-group">Buscar</FieldLabel>
                <ButtonGroup>
                    <Input placeholder="Busque por email ou nome..."
                    
                        onChange={(e) => { searchTermRef.current = e.target.value;}}

                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                actions.setFilter("search", searchTermRef.current);
                            }
                        }}
                    />

                    <Button variant="outline"
                        onClick={() => actions.setFilter("search", searchTermRef.current)}
                    >
                        Buscar
                    </Button>
                </ButtonGroup>
            </Field>



            {state.isEmpty ? (
                    <div className="w-full flex items-center justify-center p-12 text-muted-foreground">
                        Nenhum membro encontrado.
                    </div>
                ) : (
                    <MemberTable 
                        membros={state.members?.content ?? []}
                    />
            )}

        </>
    )
}