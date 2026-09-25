
import { useEffect } from 'react';
import { useInviteForm, type InviteFormData } from '../hook/useInviteForm';
import { InviteForm } from './InviteForm';

export default function Invite() {
  const {
    formData,
    errors,
    isLoading,
    successMessage,
    handleChange,
    handleSubmit,
    setSuccessMessage,
  } = useInviteForm();

  // Limpar mensagem de sucesso após 3 segundos
  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage('');
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [successMessage, setSuccessMessage]);

  const handleFormSubmit = async (data: InviteFormData) => {
    // TODO: Integrar com API real
    console.log('Dados do formulário:', data);
    // Simular chamada API
    await new Promise((resolve) => setTimeout(resolve, 1000));
  };

  return (
    <div className="min-h-screen w-full bg-white flex flex-col">
      <header className="w-full border-b-2 border-blue-500 bg-[#ededed]">
  <div className="h-44 flex items-center px-12 py-3">
    <img
      src="/logo.svg"
      alt="Banda Musical Santa Bárbara"
      className="h-full w-auto max-h-36 object-contain"
    />
  </div>
</header>

      {/* Main Content */}
      <main className="flex-1 flex flex-col items-center justify-center px-8 py-12">
        <div className="w-full max-w-md">
          {/* Title */}
          <h1 className="text-3xl font-bold text-center text-gray-900 mb-4">
            Você foi convidado!
          </h1>

          {/* Subtitle */}
          <p className="text-center text-gray-700 mb-8">
            Finalize seu cadastro abaixo para acessar.
          </p>

          {/* Success Message */}
          {successMessage && (
            <div
              className="mb-6 p-4 bg-green-50 border border-green-200 rounded-md"
              role="alert"
            >
              <p className="text-green-700 text-sm font-medium">
                {successMessage}
              </p>
            </div>
          )}

          {/* Form */}
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

          {/* Footer Info */}
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