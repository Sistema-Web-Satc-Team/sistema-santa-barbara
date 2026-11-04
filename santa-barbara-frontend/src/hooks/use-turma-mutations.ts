import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
    atualizarTurma,
    cancelarTurma,
    criarTurma,
    interromperTurma,
    removerProfessor,
    substituirProfessor,
    vincularProfessor,
    type AtualizarTurmaRequest,
    type CriarTurmaRequest,
} from "#/api/turmas";
import {
    desistirDaMatricula,
    matricularAluno,
    removerMatricula,
    transferirMatricula,
    type MatricularRequest,
} from "#/api/matriculas";

export function useTurmaMutations() {
    const queryClient = useQueryClient();

    // Matrícula e vínculo mexem na lista, no detalhe e na marca "temTurma" de alunos e professores.
    const aoConcluir = async () => {
        await Promise.all([
            queryClient.invalidateQueries({ queryKey: ["turmas"] }),
            queryClient.invalidateQueries({ queryKey: ["turma"] }),
            queryClient.invalidateQueries({ queryKey: ["alunos"] }),
            queryClient.invalidateQueries({ queryKey: ["professores"] }),
        ]);
    };

    const criar = useMutation({
        mutationFn: (body: CriarTurmaRequest) => criarTurma(body),
        onSuccess: aoConcluir,
    });

    const atualizar = useMutation({
        mutationFn: ({ idTurma, body }: { idTurma: number; body: AtualizarTurmaRequest }) =>
            atualizarTurma(idTurma, body),
        onSuccess: aoConcluir,
    });

    const cancelar = useMutation({
        mutationFn: (idTurma: number) => cancelarTurma(idTurma),
        onSuccess: aoConcluir,
    });

    const interromper = useMutation({
        mutationFn: (idTurma: number) => interromperTurma(idTurma),
        onSuccess: aoConcluir,
    });

    const vincularProf = useMutation({
        mutationFn: ({ idTurma, idProfessor }: { idTurma: number; idProfessor: string }) =>
            vincularProfessor(idTurma, idProfessor),
        onSuccess: aoConcluir,
    });

    const removerProf = useMutation({
        mutationFn: ({ idTurma, idProfessor }: { idTurma: number; idProfessor: string }) =>
            removerProfessor(idTurma, idProfessor),
        onSuccess: aoConcluir,
    });

    const substituirProf = useMutation({
        mutationFn: ({
            idTurma,
            idNovoProfessor,
            justificativa,
        }: {
            idTurma: number;
            idNovoProfessor: string;
            justificativa?: string;
        }) => substituirProfessor(idTurma, idNovoProfessor, justificativa),
        onSuccess: aoConcluir,
    });

    const matricular = useMutation({
        mutationFn: (body: MatricularRequest) => matricularAluno(body),
        onSuccess: aoConcluir,
    });

    const removerMat = useMutation({
        mutationFn: (idMatricula: number) => removerMatricula(idMatricula),
        onSuccess: aoConcluir,
    });

    const transferir = useMutation({
        mutationFn: (v: { idMatricula: number; idNovaTurma: number; justificativa: string }) =>
            transferirMatricula(v.idMatricula, {
                idNovaTurma: v.idNovaTurma,
                justificativa: v.justificativa,
            }),
        onSuccess: aoConcluir,
    });

    const desistir = useMutation({
        mutationFn: (v: { idMatricula: number; justificativa: string }) =>
            desistirDaMatricula(v.idMatricula, v.justificativa),
        onSuccess: aoConcluir,
    });

    return {
        criar: criar.mutateAsync,
        atualizar: atualizar.mutateAsync,
        cancelar: cancelar.mutateAsync,
        interromper: interromper.mutateAsync,
        vincularProfessor: vincularProf.mutateAsync,
        removerProfessor: removerProf.mutateAsync,
        substituirProfessor: substituirProf.mutateAsync,
        matricular: matricular.mutateAsync,
        removerMatricula: removerMat.mutateAsync,
        transferirMatricula: transferir.mutateAsync,
        desistirDaMatricula: desistir.mutateAsync,
    };
}
