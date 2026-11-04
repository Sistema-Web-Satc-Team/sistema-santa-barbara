import { useState } from "react";
import { mensagemDeErro } from "#/lib/mensagem-erro";

/**
 * Controla uma ação assíncrona de um diálogo: estado de envio e mensagem de erro da API.
 * As regras de negócio (conflito de horário, turma lotada, professor na própria turma)
 * voltam do backend e aparecem dentro do próprio diálogo.
 */
export function useAcaoAsync() {
    const [pendente, setPendente] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    async function executar(acao: () => Promise<void>, mensagemPadrao: string): Promise<boolean> {
        setErro(null);
        setPendente(true);
        try {
            await acao();
            return true;
        } catch (err) {
            setErro(mensagemDeErro(err, mensagemPadrao));
            return false;
        } finally {
            setPendente(false);
        }
    }

    return { pendente, erro, executar, limparErro: () => setErro(null) };
}
