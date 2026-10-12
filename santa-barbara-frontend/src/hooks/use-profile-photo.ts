import { deleteProfilePhoto, saveProfilePhoto } from '#/api/auth'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { toast } from 'sonner'

function getErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const data: unknown = error.response?.data
    if (
      typeof data === 'object' &&
      data !== null &&
      'message' in data &&
      typeof data.message === 'string'
    ) {
      return data.message
    }
  }
  return fallback
}

export function useProfilePhoto() {
  const queryClient = useQueryClient()

  const refreshProfile = () =>
    queryClient.invalidateQueries({ queryKey: ['user-profile'] })

  const save = useMutation({
    mutationFn: ({ photo, replace }: { photo: Blob; replace: boolean }) =>
      saveProfilePhoto(photo, replace),
    onSuccess: async () => {
      await refreshProfile()
      toast.success('Foto de perfil atualizada.')
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível salvar a foto.'))
    },
  })

  const remove = useMutation({
    mutationFn: deleteProfilePhoto,
    onSuccess: async () => {
      await refreshProfile()
      toast.success('Foto de perfil removida.')
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível remover a foto.'))
    },
  })

  return { save, remove }
}
