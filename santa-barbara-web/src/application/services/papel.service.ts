import axios from "axios";
import { Api } from "@/application/api/Api";

interface PapeisResponse {
    content: string[];
    pageNumber: number;
    pageSize: number;
}

export const papelService = {
    getPapeis: async (): Promise<string[]> => {
        const response = await axios.get<PapeisResponse>(
            `${Api.getRooutResource()}papeis?page=0&size=50`,
            { withCredentials: true }
        );
        
        return response.data.content ?? [];
    }
};