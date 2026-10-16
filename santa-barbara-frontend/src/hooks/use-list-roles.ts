
import { fetchRoles, type Role } from "#/api/funcoes";
import { keepPreviousData, useQuery } from "@tanstack/react-query";

interface ListRolesProps {
    data: Role[] | undefined,
    state: { isLoading: boolean, isEmpty: boolean }
}

export function useListRoles(): ListRolesProps {
    const { data, isLoading } = useQuery({
        queryKey: ['roles-list'],
        
        queryFn: fetchRoles,
        placeholderData: keepPreviousData,
    
        staleTime: 1000 * 60 * 5,
        retry: false,                    
        refetchOnWindowFocus: false,
    });

    const isEmpty = !isLoading && (!data || data.length === 0);


    return { 
        data: data,
        state: { isLoading, isEmpty } 
    };
}