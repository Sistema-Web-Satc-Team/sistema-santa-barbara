/**
 * Modelo de dados para Convite
*/

export interface Convite {
  id: string;
  emailConvidado: string;
  papelDesignado: 'ALUNO' | 'PROFESSOR' | 'ADMINISTRADOR';
  dataConvite: Date;
  status: 'PENDENTE' | 'ACEITO' | 'EXPIRADO' | 'RECUSADO';
  dataExpiracao?: Date;
  nomeConvidante?: string;
}

export interface CriarConviteRequest {
  emailConvidado: string;
  papelDesignado: 'ALUNO' | 'PROFESSOR' | 'ADMINISTRADOR';
}

export interface ConviteResponse {
  id: string;
  emailConvidado: string;
  papelDesignado: string;
  dataConvite: string;
  status: string;
}

export interface RegistrarNovoUsuarioRequest {
  email: string;
  nomeCompleto: string;
  telefone: string;
  senha: string;
  idConvite: string;
}
