/**
 * Modelo de dados para Convite
*/

interface Convite {
  id: string;
  emailConvidado: string;
  papelDesignado: 'ALUNO' | 'PROFESSOR' | 'ADMINISTRADOR';
  dataConvite: Date;
  status: 'PENDENTE' | 'ACEITO' | 'EXPIRADO' | 'RECUSADO';
  dataExpiracao?: Date;
  nomeConvidante?: string;
}

interface CriarConviteRequest {
  emailConvidado: string;
  papelDesignado: 'ALUNO' | 'PROFESSOR' | 'ADMINISTRADOR';
}

interface ConviteResponse {
  id: string;
  emailConvidado: string;
  papelDesignado: string;
  dataConvite: string;
  status: string;
}

interface RegistrarNovoUsuarioRequest {
  email: string;
  nomeCompleto: string;
  telefone: string;
  senha: string;
  idConvite: string;
}


export type { Convite, ConviteResponse, CriarConviteRequest, RegistrarNovoUsuarioRequest };
