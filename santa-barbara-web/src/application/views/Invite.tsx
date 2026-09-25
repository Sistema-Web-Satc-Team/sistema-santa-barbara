import axios from 'axios';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { Api } from '../api/Api';
import { useInviteForm, type InviteFormData } from '../hook/useInviteForm';
import { InviteForm } from './InviteForm';

export default function Invite() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const [isValidating, setIsValidating] = useState<boolean>(true);
  const [isValidInvite, setIsValidInvite] = useState<boolean>(false);

  const {
    formData,
    errors,
    isLoading,
    handleChange,
    handleSubmit,
  } = useInviteForm();

  useEffect(() => {
    const validateInvite = async () => {
      if (!id) {
        setIsValidating(false);
        setIsValidInvite(false);
        return;
      }

      try {
        const response = await axios.get(`${Api.getRooutResource()}convites/validar/${id}`, {
          withCredentials: true,
        });

        if (response.data === true || response.status === 200) {
          setIsValidInvite(true);
        } else {
          setIsValidInvite(false);
        }
      } catch (error) {
        console.error("Erro ao validar convite:", error);
        setIsValidInvite(false);
      } finally {
        setIsValidating(false);
      }
    };

    validateInvite();
  }, [id]);

  const handleFormSubmit = async (data: InviteFormData) => {
    try {
      await axios.post(
        `${Api.getRooutResource()}convites/aceitar`,
        {
          idConvite: id,
          senha: data.password,
          nomeDeUsuario: data.username
        },
        { withCredentials: true }
      );
      navigate('/login');
    } catch (error) {
      console.error("Erro ao aceitar convite:", error);
      alert("Ocorreu um erro ao aceitar o convite. Tente novamente.");
    }
  };

  if (isValidating) {
    return (
      <div className="min-h-screen w-full flex items-center justify-center bg-white">
        <p className="text-gray-600 font-medium">Carregando...</p>
      </div>
    );
  }

  if (!isValidInvite) {
    return (
      <div className="min-h-screen w-full flex flex-col items-center justify-center bg-white px-4">
        <h1 className="text-2xl font-bold text-red-600 mb-2">Convite Inválido ou Expirado</h1>
        <p className="text-gray-600 text-center">Este link de convite não é mais válido ou já foi utilizado.</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen w-full bg-white flex flex-col">
      <main className="flex-1 flex flex-col items-center justify-center px-8 py-12">
        <div className="w-full max-w-md">
          <h1 className="text-3xl font-bold text-center text-gray-900 mb-4">
            Você foi convidado!
          </h1>
          <p className="text-center text-gray-700 mb-8">
            Finalize seu cadastro abaixo para acessar.
          </p>

          <InviteForm
            formData={formData}
            errors={errors as any}
            isLoading={isLoading}
            onInputChange={handleChange}
            onSubmit={(e) => {
              e.preventDefault();
              handleSubmit(handleFormSubmit);
            }}
          />

          <div className="mt-8 text-center text-xs text-gray-500">
            <p>
              Já possui cadastro?{' '}
              <a href="/login" className="text-blue-600 hover:underline">
                Faça login
              </a>
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}