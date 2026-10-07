
import { fetchRoles } from "#/api/papeis";
import { keepPreviousData, useQuery } from "@tanstack/react-query";

interface ListRolesProps {
    data: string[] | undefined,
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

    const formattedData = data?.map( (value) => {
        return value.replaceAll('_', ' ')
    })

    return { 
        data: formattedData,
        state: { isLoading, isEmpty } 
    };
}