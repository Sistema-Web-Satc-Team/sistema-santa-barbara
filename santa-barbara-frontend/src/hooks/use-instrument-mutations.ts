import {
  createInstrumento,
  deleteInstrumento,
  updateInstrumento,
} from '#/api/instrumento'
import { getErrorMessage } from '#/lib/api-error'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'

export function useInstrumentMutations() {
  const queryClient = useQueryClient()

  const refreshList = () =>
    queryClient.invalidateQueries({ queryKey: ['instruments-list'] })

  const create = useMutation({
    mutationFn: createInstrumento,
    onSuccess: async () => {
      await refreshList()
      toast.success('Instrumento cadastrado.')
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível cadastrar o instrumento.'))
    },
  })

  const update = useMutation({
    mutationFn: updateInstrumento,
    onSuccess: async () => {
      await refreshList()
      toast.success('Instrumento atualizado.')
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível atualizar o instrumento.'))
    },
  })

  const remove = useMutation({
    mutationFn: deleteInstrumento,
    onSuccess: async () => {
      await refreshList()
      toast.success('Instrumento removido.')
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível remover o instrumento.'))
    },
  })

  return { create, update, remove }
}
