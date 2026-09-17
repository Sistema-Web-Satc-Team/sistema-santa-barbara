import { useState } from "react";
import { Button } from "@/ui/components/button";
import { Input } from "@/ui/components/input";
import "@/ui/styles/ConviteForm.css";

export interface ConviteFormProps {
  onSubmit: (emailConvidado: string, papelDesignado: string) => Promise<void>;
  carregando?: boolean;
  erro?: string;
}

export function ConviteForm({
  onSubmit,
  carregando = false,
  erro,
}: ConviteFormProps) {
  const [email, setEmail] = useState("");
  const [papel, setPapel] = useState("");
  const [erroLocal, setErroLocal] = useState("");

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErroLocal("");

    if (!email.trim()) {
      setErroLocal("Email é obrigatório");
      return;
    }

    if (!papel) {
      setErroLocal("Papel é obrigatório");
      return;
    }

    try {
      await onSubmit(email, papel);
      setEmail("");
      setPapel("");
    } catch (error) {
      setErroLocal("Erro ao enviar convite. Tente novamente.");
    }
  }

  return (
    <form onSubmit={handleSubmit} className="convite-form">
      <div className="convite-form__group">
        <label htmlFor="email" className="convite-form__label">
          Email do Convidado <span className="convite-form__required">*</span>
        </label>
        <Input
          id="email"
          type="email"
          placeholder="exemplo@gmail.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={carregando}
          required
        />
      </div>

      <div className="convite-form__group">
        <label htmlFor="papel" className="convite-form__label">
          Papel / Permissão <span className="convite-form__required">*</span>
        </label>
        <select
          id="papel"
          className="convite-form__select"
          value={papel}
          onChange={(e) => setPapel(e.target.value)}
          disabled={carregando}
          required
        >
          <option value="">Selecione um papel...</option>
          <option value="ALUNO">Aluno</option>
          <option value="PROFESSOR">Professor</option>
          <option value="ADMINISTRADOR">Administrador</option>
        </select>
      </div>

      {(erro || erroLocal) && (
        <div className="convite-form__error">
          {erro || erroLocal}
        </div>
      )}

      <Button
        type="submit"
        disabled={carregando}
        variant="normal"
      >
        {carregando ? "Enviando..." : "Enviar Convite"}
      </Button>
    </form>
  );
}
