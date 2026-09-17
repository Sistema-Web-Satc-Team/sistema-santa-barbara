import { useEffect, useState } from "react";
import { ConviteForm } from "@/ui/components/ConviteForm";
import { ConviteTable } from "@/ui/components/ConviteTable";
import { Convite, CriarConviteRequest } from "@/application/model/ConviteModel";
import {
  listarConvites,
  criarConvite,
} from "@/application/services/ConviteService";
import "@/ui/styles/ConvidarMembrosPage.css";

/**
 * Página de Convidar Membros
 * Gerencia o estado dos convites e coordena a comunicação com a API
 */
export default function ConvidarMembrosPage() {
  const [convites, setConvites] = useState<Convite[]>([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucessoMensagem, setSucessoMensagem] = useState("");

  // Carregar convites ao montar o componente
  useEffect(() => {
    carregarConvites();
  }, []);

  // Limpar mensagem de sucesso após 3 segundos
  useEffect(() => {
    if (sucessoMensagem) {
      const timer = setTimeout(() => setSucessoMensagem(""), 3000);
      return () => clearTimeout(timer);
    }
  }, [sucessoMensagem]);

  async function carregarConvites() {
    setCarregando(true);
    setErro("");
    try {
      const dados = await listarConvites();
      setConvites(dados);
    } catch (error) {
      console.error("Erro ao carregar convites:", error);
      setErro("Não foi possível carregar os convites. Tente novamente mais tarde.");
      setConvites([]);
    } finally {
      setCarregando(false);
    }
  }

  async function handleEnviarConvite(
    emailConvidado: string,
    papelDesignado: string
  ) {
    setCarregando(true);
    setErro("");

    try {
      const dados: CriarConviteRequest = {
        emailConvidado,
        papelDesignado: papelDesignado as 'ALUNO' | 'PROFESSOR' | 'ADMINISTRADOR',
      };

      const novoConvite = await criarConvite(dados);
      setConvites((previos) => [novoConvite, ...previos]);
      setSucessoMensagem("✅ Convite enviado com sucesso!");
    } catch (error) {
      console.error("Erro ao criar convite:", error);
      setErro("Erro ao enviar convite. Tente novamente.");
      throw error;
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="convidar-membros-page">
      <div className="convidar-membros-page__container">
        {/* Header */}
        <header className="convidar-membros-page__header">
          <h1>Convidar Novos Membros</h1>
        </header>

        {/* Mensagens de Feedback */}
        {sucessoMensagem && (
          <div className="convidar-membros-page__sucesso">
            {sucessoMensagem}
          </div>
        )}

        {erro && (
          <div className="convidar-membros-page__erro">
            {erro}
          </div>
        )}

        {/* Formulário */}
        <section className="convidar-membros-page__form-section">
          <div className="convidar-membros-page__card">
            <h2>Enviar Convite</h2>
            <ConviteForm
              onSubmit={handleEnviarConvite}
              carregando={carregando}
            />
          </div>
        </section>

        {/* Tabela de Convites Recentes */}
        <section className="convidar-membros-page__table-section">
          <div className="convidar-membros-page__card">
            <h2>Convites Recentes</h2>
            <ConviteTable convites={convites} carregando={carregando} />
          </div>
        </section>
      </div>
    </div>
  );
}