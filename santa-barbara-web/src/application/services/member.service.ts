import axios from "axios";
import { Api } from "@/application/api/Api";
import type { MemberData } from "@/application/model/MemberData";

export interface UpdateMemberRequest {
    nome?: string;
    sobrenome?: string;
    email?: string;
    papeis?: string[];
    telefone?: string;
    endereco?: string;
    status?: string;
    dataNascimento?: string;
}

export interface MemberDetails {
    id: string;
    nome?: string;
    sobrenome?: string;
    nomeDeUsuario?: string;
    email?: string;
    telefone?: string;
    endereco?: string;
    papeis?: string[];
    status?: string;
    dataNascimento?: string;
}

interface MembersResponse {
    content?: MemberDetails[];
}

function toMemberData(member: MemberDetails): MemberData {
    return {
        id: member.id,
        fullName: [member.nome, member.sobrenome].filter(Boolean).join(" "),
        role: member.papeis?.join(", ") || "",
        phone: member.telefone || "",
        endereco: member.endereco || "",
        email: member.email || "",
        status: member.status?.toLowerCase() || "",
    };
}

export const memberService = {
    getAllMembers: async (): Promise<MemberData[]> => {
        const endpoint = Api.getRooutResource() + "membros";
        
        const response = await axios.get<MembersResponse | MemberDetails[]>(endpoint, { withCredentials: true });
        const members = Array.isArray(response.data) ? response.data : response.data.content || [];

        return members.map(toMemberData);
    },

    getMemberById: async (id: string): Promise<MemberDetails> => {
        const response = await axios.get<MemberDetails>(
            `${Api.getRooutResource()}membros/${id}`,
            { withCredentials: true }
        );

        return response.data;
    },

    updateMember: async (id: string, data: UpdateMemberRequest): Promise<void> => {
        await axios.patch(
            `${Api.getRooutResource()}membros/${id}`,
            data,
            { withCredentials: true }
        );
    },
};