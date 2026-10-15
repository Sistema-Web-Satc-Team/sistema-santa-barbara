import { Button } from '#/components/ui/button';
import {
    Dialog,
    DialogContent,
    DialogFooter,
    DialogHeader,
    DialogTitle
} from '#/components/ui/dialog';
import { useAppForm } from '#/lib/form';
import { ehMenor } from '#/lib/idade';
import { mensagemDeErro, ResponsavelCriadoError } from '#/routes/_auth/dashboard/_layout.membros/registrar-membro';
import { useRef } from 'react';
import { toast } from 'sonner';
import { formMemberSchema, memberDefaultValues, type MemberFormValues } from '../../../schemas/form-schema-member';
import { PersonContactFormFields } from './person-contact-form-fields';
import { PersonFormFields } from './person-form-fields';
import { ResponsavelFormFields } from './responsaveis/responsavel-form-fields';


interface MemberFormDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSubmit: (values: MemberFormValues, opts?: { idResponsavelCriado?: string | null }) => Promise<void>
}

export function MemberFormDialog({ open, onOpenChange, onSubmit }: MemberFormDialogProps) {

    const criadoRef = useRef<string | null>(null)

    const form = useAppForm({
        defaultValues: memberDefaultValues,
        validators: {
            onSubmit: formMemberSchema,
        },
        onSubmit: async ({ value }) => {
            try {
                await onSubmit(value, { idResponsavelCriado: criadoRef.current })
                criadoRef.current = null
                toast.success('Membro cadastrado com sucesso.')
                form.reset()
                onOpenChange(false)
            } catch (err) {
                if (err instanceof ResponsavelCriadoError) criadoRef.current = err.idResponsavel
                toast.error(mensagemDeErro(err))
            }
        },
        
    });

    function handleOpenChange(next: boolean) {
        if (!next) { form.reset(); criadoRef.current = null }
        onOpenChange(next)
    }

    return (
        <Dialog open={open} onOpenChange={handleOpenChange}>
            <DialogContent className="flex flex-col md:max-w-xl max-w-md max-h-[90vh] overflow-hidden p-0 gap-0">
                <DialogHeader className="p-4 pb-2 border-b shrink-0">
                    <DialogTitle>Cadastrar Membro</DialogTitle>
                </DialogHeader>

                <form 
                    onSubmit={(e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        form.handleSubmit();
                    }}
                    className="flex flex-col flex-1 min-h-0"
                >

                    <div className="flex-1 min-h-0 overflow-y-auto p-4 space-y-4">
                        <h4 className='font-thin mb-2 text-xs text-muted-foreground'>INFORMAÇÕES BÁSICAS: </h4>
                        <PersonFormFields form={form} />

                        <h4 className='font-thin mt-8 mb-2 text-xs text-muted-foreground'>INFORMAÇÕES DE CONTATO: </h4>
                        <PersonContactFormFields form={form}/>


                        <form.Subscribe selector={(s) => ehMenor(s.values.dataNascimento)}>
                            {(menor) =>
                                menor && (
                                <>
                                    <h4 className="mt-8 mb-2 text-xs font-thin text-muted-foreground">
                                    INFORMAÇÕES DO RESPONSÁVEL:
                                    </h4>
                                    <ResponsavelFormFields
                                        form={form}
                                        fields="responsavel"
                                    />
                                </>
                                )
                            }
                        </form.Subscribe>
                    </div>

                    


                    <DialogFooter className="p-4 border-t shrink-0">
                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => handleOpenChange(false)}
                        >
                            Cancelar
                        </Button>

                        <form.Subscribe selector={(state) => [state.isSubmitting]}>
                            {([isSubmitting]) => (
                                <Button type="submit" disabled={isSubmitting}>
                                    {isSubmitting ? 'Salvando...' : 'Salvar'}
                                </Button>
                            )}
                        </form.Subscribe>
                    </DialogFooter>
                </form>

            </DialogContent>
        </Dialog>
    );
}
