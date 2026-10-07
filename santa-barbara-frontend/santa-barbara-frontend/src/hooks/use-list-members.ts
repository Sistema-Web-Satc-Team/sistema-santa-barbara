import { fetchMembers, type Member, type MembersFilterRequest } from "#/api/member";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { useFilter } from "./use-filter";

export interface ListMembersProps {
    data: ListResponse<Member> | undefined;
    state: { 
        isLoading: boolean; 
        isEmpty: boolean;
    };
    actions: {
        clearFilters: () => void;
        setFilter: (key: keyof MembersFilterRequest, value: any) => void; 
    }
}

export function useListMembers(): ListMembersProps {
    const {filters, setFilter, clearFilters } = useFilter<MembersFilterRequest>({});

    const { data, isLoading } = useQuery({
        queryKey: ['members-list', filters],
        
        queryFn: () => fetchMembers(filters),
        placeholderData: keepPreviousData,
    
        staleTime: 1000 * 60 * 5,
        retry: false,                    
        refetchOnWindowFocus: false,
    });

    const isEmpty = !isLoading && (!data?.content || data.content.length === 0);

    return { 
        data,
        state: { isLoading, isEmpty },
        actions: { setFilter, clearFilters }
    };
}