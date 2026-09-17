import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router";
import { Button } from "@/ui/components/button";
import { Input } from "@/ui/components/input";
import { obterConvite, registrarNovoUsuario } from "@/application/services/ConviteService";
import { Convite, RegistrarNovoUsuarioRequest } from "@/application/model/ConviteModel";
import { Eye, EyeOff } from "lucide-react";
import "@/ui/styles/AceitarConvitePage.css";

/**
 * Página para aceitar convite e registrar novo usuário
 * Acesso público - sem autenticação necessária
 */
export default function AceitarConvitePage() {
  const { idConvite } = useParams<{ idConvite: string }>();
  const navigate = useNavigate();

  const [convite, setConvite] = useState<Convite | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erroCarregamento, setErroCarregamento] = useState("");
  const [enviando, setEnviando] = useState(false);

  // Formulário
  const [nomeCompleto, setNomeCompleto] = useState("");
  const [telefone, setTelefone] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [mostrarSenha, setMostrarSenha] = useState(false);
  const [mostrarConfirmarSenha, setMostrarConfirmarSenha] = useState(false);
  const [erro, setErro] = useState("");

  // Carregar dados do convite
  useEffect(() => {
    if (!idConvite) return;

    async function carregarConvite() {
      setCarregando(true);
      setErroCarregamento("");
      try {
        const dados = await obterConvite(idConvite);
        setConvite(dados);
      } catch (error) {
        console.error("Erro ao carregar convite:", error);
        setErroCarregamento("Convite inválido ou expirado. Entre em contato com o administrador.");
      } finally {
        setCarregando(false);
      }
    }

    carregarConvite();
  }, [idConvite]);

  function formatarTelefone(valor: string): string {
    const apenasNumeros = valor.replace(/\D/g, "");
    if (apenasNumeros.length <= 2) return apenasNumeros;
    if (apenasNumeros.length <= 7) {
      return `(${apenasNumeros.slice(0, 2)}) ${apenasNumeros.slice(2)}`;
    }
    return `(${apenasNumeros.slice(0, 2)}) ${apenasNumeros.slice(2, 7)}-${apenasNumeros.slice(7, 11)}`;
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro("");

    // Validações
    if (!nomeCompleto.trim()) {
      setErro("Nome completo é obrigatório");
      return;
    }

    if (!telefone.trim()) {
      setErro("Telefone é obrigatório");
      return;
    }

    if (senha.length < 6) {
      setErro("Senha deve ter no mínimo 6 caracteres");
      return;
    }

    if (senha !== confirmarSenha) {
      setErro("As senhas não conferem");
      return;
    }

    if (!idConvite || !convite) {
      setErro("Convite inválido");
      return;
    }

    setEnviando(true);
    try {
      const dados: RegistrarNovoUsuarioRequest = {
        email: convite.emailConvidado,
        nomeCompleto,
        telefone,
        senha,
        idConvite,
      };

      await registrarNovoUsuario(dados);
      
      // Sucesso - redirecionar para login
      alert("✅ Cadastro realizado com sucesso! Você será redirecionado para o login.");
      navigate("/login");
    } catch (error) {
      console.error("Erro ao registrar:", error);
      setErro("Erro ao registrar. Tente novamente mais tarde.");
    } finally {
      setEnviando(false);
    }
  }

  if (carregando) {
    return (
      <div className="aceitar-convite-page">
        <div className="aceitar-convite-page__container">
          <div className="aceitar-convite-page__loading">
            Carregando convite...
          </div>
        </div>
      </div>
    );
  }

  if (erroCarregamento || !convite) {
    return (
      <div className="aceitar-convite-page">
        <div className="aceitar-convite-page__container">
          <div className="aceitar-convite-page__erro-grande">
            {erroCarregamento}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="aceitar-convite-page">
      <div className="aceitar-convite-page__container">
        {/* Logo */}
        <div className="aceitar-convite-page__logo">
          <div className="aceitar-convite-page__logo-inner">
            <span>BANDA</span>
            <span>MUSICAL</span>
            <span>SB</span>
          </div>
        </div>

        {/* Linha verde */}
        <div className="aceitar-convite-page__divider" />

        {/* Título */}
        <h1 className="aceitar-convite-page__title">Você foi convidado!</h1>

        {/* Mensagem */}
        <div className="aceitar-convite-page__message">
          <p>Você foi convidado para participar do</p>
          <p className="aceitar-convite-page__message-strong">sistema da</p>
          <p className="aceitar-convite-page__message-strong">banda como {convite.papelDesignado === 'ALUNO' ? 'Aluno' : convite.papelDesignado === 'PROFESSOR' ? 'Professor' : 'Administrador'}</p>
          <p>Finalize seu cadastro abaixo para acessar.</p>
        </div>

        {/* Formulário */}
        <form onSubmit={handleSubmit} className="aceitar-convite-page__form">
          {/* Email (desabilitado) */}
          <div className="aceitar-convite-page__form-group">
            <label className="aceitar-convite-page__form-label">
              E-mail (Seu Login)
            </label>
            <input
              type="email"
              value={convite.emailConvidado}
              disabled
              className="aceitar-convite-page__form-input aceitar-convite-page__form-input--disabled"
            />
          </div>

          {/* Nome Completo */}
          <div className="aceitar-convite-page__form-group">
            <label className="aceitar-convite-page__form-label">
              Nome Completo <span className="aceitar-convite-page__required">*</span>
            </label>
            <Input
              type="text"
              value={nomeCompleto}
              onChange={(e) => setNomeCompleto(e.target.value)}
              placeholder="Digite seu nome completo"
              disabled={enviando}
              required
            />
          </div>

          {/* Telefone */}
          <div className="aceitar-convite-page__form-group">
            <label className="aceitar-convite-page__form-label">
              Telefone / Whatsapp
            </label>
            <Input
              type="tel"
              value={telefone}
              onChange={(e) => setTelefone(formatarTelefone(e.target.value))}
              placeholder="(00) 00000-0000"
              disabled={enviando}
            />
          </div>

          {/* Senhas lado a lado */}
          <div className="aceitar-convite-page__senhas">
            <div className="aceitar-convite-page__form-group">
              <label className="aceitar-convite-page__form-label">
                Criar Senha <span className="aceitar-convite-page__required">*</span>
              </label>
              <div className="aceitar-convite-page__password-wrapper">
                <input
                  type={mostrarSenha ? "text" : "password"}
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  placeholder="••••••••••"
                  className="aceitar-convite-page__form-input"
                  disabled={enviando}
                  required
                />
                <button
                  type="button"
                  onClick={() => setMostrarSenha(!mostrarSenha)}
                  className="aceitar-convite-page__password-toggle"
                >
                  {mostrarSenha ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <div className="aceitar-convite-page__form-group">
              <label className="aceitar-convite-page__form-label">
                Confirmar Senha <span className="aceitar-convite-page__required">*</span>
              </label>
              <div className="aceitar-convite-page__password-wrapper">
                <input
                  type={mostrarConfirmarSenha ? "text" : "password"}
                  value={confirmarSenha}
                  onChange={(e) => setConfirmarSenha(e.target.value)}
                  placeholder="••••••••••"
                  className="aceitar-convite-page__form-input"
                  disabled={enviando}
                  required
                />
                <button
                  type="button"
                  onClick={() => setMostrarConfirmarSenha(!mostrarConfirmarSenha)}
                  className="aceitar-convite-page__password-toggle"
                >
                  {mostrarConfirmarSenha ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>
          </div>

          {/* Erro */}
          {erro && (
            <div className="aceitar-convite-page__erro">
              {erro}
            </div>
          )}

          {/* Botão */}
          <Button
            type="submit"
            variant="normal"
            disabled={enviando}
            className="aceitar-convite-page__submit-button"
          >
            {enviando ? "Finalizando..." : "Finalizar Cadastro"}
          </Button>
        </form>

        {/* Rodapé */}
        <p className="aceitar-convite-page__footer">
          Ao finalizar seu cadastro, você concordará com nossos termos de serviço.
        </p>
      </div>
    </div>
  );
}