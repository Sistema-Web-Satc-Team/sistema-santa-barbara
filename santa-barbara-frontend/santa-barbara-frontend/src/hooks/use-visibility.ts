import { useState } from "react";

export interface Visibility<T> {
    columnVisibility: Record<keyof T, boolean>;
    toggleColumn: (column: keyof T) => void;
}

export function useVisibility<T>(initialHiddenFields: (keyof T)[]): Visibility<T> {
    const [keys, setKeys] = useState<Record<keyof T, boolean>>((() => {
        const result: Record<keyof T, boolean> = initialHiddenFields
            .reduce<Record<keyof T, boolean>>(
                (acc, currentKey) => {
                    acc[currentKey] = true;
                    return acc;
                },
                {} as Record<keyof T, boolean>
            )
        return result;
    })())

    const toggleColumn = (column: keyof T) => {
        setKeys(prev => ({ ...prev, [column]: !prev[column] }));
    };

    return {
        columnVisibility: keys,
        toggleColumn
    };
}