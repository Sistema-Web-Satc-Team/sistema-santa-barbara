import type { Convite, CriarConviteRequest } from "@/application/model/ConviteModel";
import {
  criarConvite,
  listarConvites,
} from "@/application/services/ConviteService";
import { ConviteForm } from "@/ui/components/ConviteForm";
import { ConviteTable } from "@/ui/components/ConviteTable";
import "@/ui/styles/ConvidarMembrosPage.css";
import { useEffect, useState } from "react";

export default function ConvidarMembrosPage() {
  const [convites, setConvites] = useState<Convite[]>([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucessoMensagem, setSucessoMensagem] = useState("");

  useEffect(() => {
    carregarConvites();
  }, []);

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
        <header className="convidar-membros-page__header">
          <h1>Convidar Novos Membros</h1>
        </header>

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

        <section className="convidar-membros-page__form-section">
          <div className="convidar-membros-page__card">
            <h2>Enviar Convite</h2>
            <ConviteForm
              onSubmit={handleEnviarConvite}
              carregando={carregando}
            />
          </div>
        </section>

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
