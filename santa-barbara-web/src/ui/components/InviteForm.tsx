import { Button } from './button';
import { Input } from './input';
import type { InviteFormData } from '@/application/hooks/useInviteForm';

interface InviteFormProps {
  formData: InviteFormData;
  errors: Record<string, string | undefined>;
  isLoading: boolean;
  onInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
}

export function InviteForm({
  formData,
  errors,
  isLoading,
  onInputChange,
  onSubmit,
}: InviteFormProps) {
  return (
    <form onSubmit={onSubmit} className="w-full max-w-md space-y-6">
      {/* Username Field */}
      <div className="space-y-2">
        <label htmlFor="username" className="block text-sm font-medium">
          Insira seu nome de usuário:
        </label>
        <Input
          id="username"
          name="username"
          type="text"
          placeholder="Nome de usuário"
          value={formData.username}
          onChange={onInputChange}
          disabled={isLoading}
          variant="normal"
          className="w-full"
          aria-invalid={!!errors.username}
          aria-describedby={errors.username ? 'username-error' : undefined}
        />
        {errors.username && (
          <p
            id="username-error"
            className="text-sm text-red-600"
            role="alert"
          >
            {errors.username}
          </p>
        )}
      </div>

      {/* Password Field */}
      <div className="space-y-2">
        <label htmlFor="password" className="block text-sm font-medium">
          Senha:
        </label>
        <Input
          id="password"
          name="password"
          type="password"
          placeholder="Senha"
          value={formData.password}
          onChange={onInputChange}
          disabled={isLoading}
          variant="normal"
          className="w-full"
          aria-invalid={!!errors.password}
          aria-describedby={errors.password ? 'password-error' : undefined}
        />
        {errors.password && (
          <p
            id="password-error"
            className="text-sm text-red-600"
            role="alert"
          >
            {errors.password}
          </p>
        )}
      </div>

      {/* Confirm Password Field */}
      <div className="space-y-2">
        <label htmlFor="confirmPassword" className="block text-sm font-medium">
          Confirmar Senha:
        </label>
        <Input
          id="confirmPassword"
          name="confirmPassword"
          type="password"
          placeholder="Confirmar Senha"
          value={formData.confirmPassword}
          onChange={onInputChange}
          disabled={isLoading}
          variant="normal"
          className="w-full"
          aria-invalid={!!errors.confirmPassword}
          aria-describedby={
            errors.confirmPassword ? 'confirmPassword-error' : undefined
          }
        />
        {errors.confirmPassword && (
          <p
            id="confirmPassword-error"
            className="text-sm text-red-600"
            role="alert"
          >
            {errors.confirmPassword}
          </p>
        )}
      </div>

      {/* Submit Button */}
      <Button
        type="submit"
        disabled={isLoading}
        onClick={undefined}
        className="w-full"
      >
        {isLoading ? 'Processando...' : 'Aceitar Convite'}
      </Button>
    </form>
  );
}