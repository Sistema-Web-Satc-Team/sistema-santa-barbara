import type { Member } from "#/api/member";
import { MemberTable } from "#/components/business/member-table";
import { Button } from '#/components/ui/button';
import { ButtonGroup } from "#/components/ui/button-group";
import { Field, FieldLabel } from '#/components/ui/field';
import { Input } from "#/components/ui/input";
import { Spinner } from '#/components/ui/spinner';
import { useListMembers } from "#/hooks/use-list-members";
import { useVisibility } from "#/hooks/use-visibility";
import { createFileRoute } from '@tanstack/react-router';
import { useRef } from "react";

export const Route = createFileRoute('/dashboard/_layout/contatos/')({
  component: Contatos,
})

function Contatos() {
    const { data: membros, state, actions } = useListMembers();
    const { columnVisibility } = useVisibility<Member>(['id', 'dataNascimento', 'nomeDeUsuario', 'papeis'])
    const searchTermRef = useRef<string>("")
   



    if (state.isLoading) {
        return <div className="w-full flex items-center justify-center min-h-screen">
            <Spinner />
        </div>
    }

    if (state.isEmpty) {
        return <div className="w-full flex items-center justify-center min-h-screen text-destructive text-xl">Nenhum membro cadastrado!</div>;
    }

    return (
        <>

            <div className="flex flex-row mb-8">
                <h2 className="text-3xl font-bold mr-auto">Contatos</h2>
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



            <MemberTable membros={membros?.content ?? []} columnVisibility={columnVisibility} />

        </>
    )
}