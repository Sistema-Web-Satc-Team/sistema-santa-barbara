import { useCallback, useState } from 'react';

export interface InviteFormData {
  username: string;
  password: string;
  confirmPassword: string;
}

export interface FormErrors {
  username?: string;
  password?: string;
  confirmPassword?: string;
}

export function useInviteForm() {
  const [formData, setFormData] = useState<InviteFormData>({
    username: '',
    password: '',
    confirmPassword: '',
  });

  const [errors, setErrors] = useState<FormErrors>({});
  const [isLoading, setIsLoading] = useState(false);

  const validateForm = useCallback((): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.username.trim()) {
      newErrors.username = 'Nome de usuário é obrigatório';
    } else if (formData.username.length < 3) {
      newErrors.username = 'Nome de usuário deve ter pelo menos 3 caracteres';
    }

    if (!formData.password) {
      newErrors.password = 'Senha é obrigatória';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Senha deve ter pelo menos 6 caracteres';
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'Confirmação de senha é obrigatória';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'As senhas não correspondem';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [formData]);

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    
    if (errors[name as keyof FormErrors]) {
      setErrors((prev) => ({
        ...prev,
        [name]: undefined,
      }));
    }
  }, [errors]);

  const handleSubmit = useCallback(
    async (onSubmit: (data: InviteFormData) => Promise<void>) => {
      if (!validateForm()) {
        return;
      }

      setIsLoading(true);
      try {
        await onSubmit(formData);
      } catch (error) {
        console.error('Erro ao processar convite:', error);
      } finally {
        setIsLoading(false);
      }
    },
    [validateForm, formData]
  );

  return {
    formData,
    errors,
    isLoading,
    handleChange,
    handleSubmit,
  };
}