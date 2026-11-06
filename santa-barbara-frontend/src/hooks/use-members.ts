import { fetchMembers } from "#/api/member";
import type { Member, MembersFilterRequest } from "#/api/member";
import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { useFilter } from "./use-filter";

export interface ListMembersProps {
    state: {
        members: PageDefaultResponse<Member> | undefined;
        isLoading: boolean; 
        error: Error | null;
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

    const { data, isLoading, error, isError } = useQuery({
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

    const isEmpty = !isLoading && !isError && (!data?.content || data.content.length === 0);

    return { 
        state: { members: data, isLoading, error, isEmpty },
        actions: { setFilter, clearFilters, refresh }
    };
}