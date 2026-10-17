import { deleteMusica, updateMusica } from '#/api/musica'
import { getErrorMessage } from '#/lib/api-error'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'

export function useMusicaMutations() {
  const queryClient = useQueryClient()

  const refreshList = () => queryClient.invalidateQueries({ queryKey: ['musicas-list'] })

  const update = useMutation({
    mutationFn: updateMusica,
    onSuccess: async () => {
      await refreshList()
      toast.success('Música atualizada.')
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível atualizar a música.'))
    },
  })

  const remove = useMutation({
    mutationFn: deleteMusica,
    onSuccess: async () => {
      await refreshList()
      toast.success('Música removida.')
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível remover a música.'))
    },
  })

  return { update, remove }
}
