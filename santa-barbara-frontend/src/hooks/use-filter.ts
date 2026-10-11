import { useState } from "react";

export interface Filters<T> {
    filters: Partial<T>;
    clearFilters: () => void;
    setFilter: (key: keyof T, value: T[keyof T]) => void; 
}


export function useFilter<T>(initialFilters: Partial<T>): Filters<T> {
    const [filters, setFilters] = useState<Partial<T>>(initialFilters);

    const clearFilters = () => {
        setFilters({})
    }

    const setFilter = (key: keyof T, value: T[keyof T]) => {
        setFilters((prev) => ({...prev, [key]: value}));
    }

    return {
        filters,
        clearFilters,
        setFilter
    }    
}