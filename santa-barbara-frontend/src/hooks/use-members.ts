import { fetchMembers, type Member, type MembersFilterRequest } from "#/api/member";
import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { useFilter } from "./use-filter";

export interface ListMembersProps {
    state: {
        members: PageDefaultResponse<Member> | undefined;
        isLoading: boolean; 
        isEmpty: boolean;
    };
    actions: {
        clearFilters: () => void;
        setFilter: (key: keyof MembersFilterRequest, value: any) => void; 
        refresh: () => void;
    }
}

export function useMembers(): ListMembersProps {
    const {filters, setFilter, clearFilters } = useFilter<MembersFilterRequest>({});
    const queryClient = useQueryClient()

    const { data, isLoading } = useQuery({
        queryKey: ['members-list', filters],
        
        queryFn: () => fetchMembers(filters),
        placeholderData: keepPreviousData,
    
        staleTime: 1000 * 60 * 5,
        retry: false,                    
        refetchOnWindowFocus: false,
    });
    
    async function refresh() {
        await queryClient.invalidateQueries({ queryKey: ['members-list'] })
    }

    const isEmpty = !isLoading && (!data?.content || data.content.length === 0);

    return { 
        state: { members: data, isLoading, isEmpty },
        actions: { setFilter, clearFilters, refresh }
    };
}