import axios from "axios";
import { Api } from "@/application/api/Api";
import type { MemberData } from "@/application/model/MemberData";

export const memberService = {
    getAllMembers: async (): Promise<MemberData[]> => {
        const endpoint = Api.getRooutResource() + "membros";
        
      
        const response = await axios.get(endpoint);
        
        return response.data;
    }
};