import axios from 'axios';
import { useEffect, useState } from 'react';
import { Api } from '../api/Api';
import { papelService } from '../services/papel.service';

export interface SelectOption {
  value: string;
  label: string;
}

export interface Invite {
  id: string;
  email: string;
  roles: string[];
  date: string;
  status: 'ENVIADO' | 'ACEITO' | 'REJEITADO' | 'REENVIADO' | 'FALHA';
}

export interface FormErrors {
  email?: string;
  roles?: string;
  submit?: string;
}

export interface DataTempMembros {
  id: string,
  nome: string,
  sobrenome: string,
  nomeDeUsuario: string,
  telefone: string,
  endereco: string,
  email: string,
  papeis: string[],
  idade: 0,
  dataNascimento: string,
  status: string
}

export const useInvites = () => {
  const [formData, setFormData] = useState<{ email: string; }>({
    email: ''
  });
  

  const [errors, setErrors] = useState<FormErrors>({});
  const [loading, setLoading] = useState<boolean>(false);

  // Dados iniciais correspondentes à tabela do protótipo
  const [invites, setInvites] = useState<Invite[]>([]);


  const [availableRoles, setAvailableRoles] = useState<string[]>([]);

  const [membros, setMembros] = useState<DataTempMembros[]>([])

  useEffect(() => {
      const fetchRoles = async () => {
          try {
              const roles = await papelService.getPapeis();
              setAvailableRoles(roles);
          } catch (error) {
              console.error("Erro ao carregar papéis do sistema:", error);
          }
      };

      const fetchMembers = async () => {
        try {
          const response = await axios.get(Api.getRooutResource() + "membros?size=100&page=0", { withCredentials: true });
          const data = response.data ?? [];
          setMembros(data.content.map((v: any) => v));
        } catch (error) {
          console.error("Erro ao carregar membros do sistema:", error);
        }
      };

      const fetchMembersAndInvitations = async () => {
        try {
          const response = await axios.get(Api.getRooutResource() + "convites", { withCredentials: true });
          const data = response.data ?? [];

          setInvites(data.content.map((v: any) => {
              return {
                id: v.idConvite || v.id,
                email: v.membro?.email || v.email,
                roles: v.membro?.papeis || v.roles || [],
                date: v.expiraEm || v.expiradoEm,
                status: v.status
              } as Invite;
            }
          ));
        } catch (error) {
          console.error("Erro ao carregar os convites dos membros:", error);
        }
      };


      fetchMembersAndInvitations();
      fetchMembers();
      fetchRoles();
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors.email) {
      setErrors((prev) => ({ ...prev, email: undefined }));
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

      // todo -> buscar membro 
      // enviar convite
      // TEMPORARIO REMOVER FUTURAMENTE

      const membro = membros.find((m) => m.email === formData.email);

      console.log(membro)

      if (!membro) {
        return false;
      }

      const request = await axios.post(Api.getRooutResource() + "convites", {
        idMembro: membro.id
      }, { withCredentials: true })

      const response = request.data;

      const newInvite: Invite = {
        id: response.idConvite,
        email: formData.email,
        roles: membro.papeis,
        date: response.expiradoEm ? new Date(response.expiradoEm).toLocaleDateString('pt-BR') : '',
        status: 'ENVIADO'
      };

      setInvites((prev) => [newInvite, ...prev]);
      setFormData({ email: ''});
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

  const resendInvite = async (id: string) => {
    try {
      setLoading(true);
      const response = await axios.put(
        `${Api.getRooutResource()}convites/${id}`, 
        {}, 
        { withCredentials: true }
      );

      const updatedData = response.data;

      setInvites((prev) =>
        prev.map((invite) =>
          invite.id === id
            ? { ...invite, status: 'REENVIADO', date: updatedData.expiraEm || invite.date }
            : invite
        )
      );
    } catch (error) {
      console.error("Erro ao reenviar convite:", error);
      alert("Não foi possível reenviar o convite.");
    } finally {
      setLoading(false);
    }
  };

  return {
    formData,
    invites,
    errors,
    loading,
    availableRoles,
    handleInputChange,
    sendInvite,
    deleteInvite,
    resendInvite
  };
};

export default useInvites;