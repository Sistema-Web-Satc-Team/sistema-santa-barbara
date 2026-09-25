import { useState } from 'react';

export interface SelectOption {
  value: string;
  label: string;
}

export interface Invite {
  id: string | number;
  email: string;
  roles: string[];
  date: string;
  status: 'Pendente' | 'Aceito' | 'Expirado';
}

export interface FormErrors {
  email?: string;
  roles?: string;
  submit?: string;
}

export const BAND_ROLES: SelectOption[] = [
  { value: 'aluno', label: 'Aluno' },
  { value: 'professor', label: 'Professor' },
  { value: 'vocalista', label: 'Vocalista' },
  { value: 'guitarra', label: 'Guitarra' },
  { value: 'baixo', label: 'Baixo' },
  { value: 'bateria', label: 'Bateria' },
  { value: 'teclado', label: 'Teclado' },
  { value: 'percussao', label: 'Percussão' },
  { value: 'produtor', label: 'Produtor Musical' },
  { value: 'manager', label: 'Manager' },
  { value: 'tecnico', label: 'Técnico de Som' },
  { value: 'admin', label: 'Administrador' }
];

export const useInvites = () => {
  const [formData, setFormData] = useState<{ email: string; roles: string[] }>({
    email: '',
    roles: ['aluno', 'professor']
  });

  const [errors, setErrors] = useState<FormErrors>({});
  const [loading, setLoading] = useState<boolean>(false);

  // Dados iniciais correspondentes à tabela do protótipo
  const [invites, setInvites] = useState<Invite[]>([
    {
      id: 1,
      email: 'joao.madeira@exemplo.com',
      roles: ['Aluno'],
      date: '10/10/2025',
      status: 'Pendente'
    },
    {
      id: 2,
      email: 'maria.quartzo@exemplo.com',
      roles: ['Aluno', 'Professor'],
      date: '28/12/2025',
      status: 'Aceito'
    },
    {
      id: 3,
      email: 'gabriel.diamante@exemplo.com',
      roles: ['Aluno'],
      date: '12/05/2025',
      status: 'Expirado'
    }
  ]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors.email) {
      setErrors((prev) => ({ ...prev, email: undefined }));
    }
  };

  const handleRoleChange = (roles: string | string[]) => {
    const rolesArray = Array.isArray(roles) ? roles : [roles];
    setFormData((prev) => ({ ...prev, roles: rolesArray }));
    if (errors.roles) {
      setErrors((prev) => ({ ...prev, roles: undefined }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!formData.email.trim()) {
      newErrors.email = 'O e-mail é obrigatório';
    } else if (!emailRegex.test(formData.email)) {
      newErrors.email = 'Insira um e-mail válido';
    }

    if (formData.roles.length === 0) {
      newErrors.roles = 'Selecione pelo menos um papel';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const sendInvite = async (): Promise<boolean> => {
    if (!validateForm()) {
      return false;
    }

    setLoading(true);

    try {
      // Simulação de chamada de rede / API
      await new Promise((resolve) => setTimeout(resolve, 500));

      const newInvite: Invite = {
        id: Date.now(),
        email: formData.email,
        roles: formData.roles.map(
          (r) => BAND_ROLES.find((opt) => opt.value === r)?.label || r
        ),
        date: new Date().toLocaleDateString('pt-BR'),
        status: 'Pendente'
      };

      setInvites((prev) => [newInvite, ...prev]);
      setFormData({ email: '', roles: [] });
      setErrors({});
      return true;
    } catch {
      setErrors({ submit: 'Erro ao enviar convite' });
      return false;
    } finally {
      setLoading(false);
    }
  };

  const deleteInvite = (id: string | number) => {
    setInvites((prev) => prev.filter((item) => item.id !== id));
  };

  const resendInvite = (id: string | number) => {
    setInvites((prev) =>
      prev.map((item) =>
        item.id === id
          ? { ...item, date: new Date().toLocaleDateString('pt-BR'), status: 'Pendente' }
          : item
      )
    );
  };

  return {
    formData,
    invites,
    errors,
    loading,
    handleInputChange,
    handleRoleChange,
    sendInvite,
    deleteInvite,
    resendInvite
  };
};

export default useInvites;