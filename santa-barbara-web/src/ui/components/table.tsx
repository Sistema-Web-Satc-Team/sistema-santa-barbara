import React from "react";


export interface TableColumn<T> {
    header: string; 
    accessor?: keyof T; 
    render?: (item: T) => React.ReactNode; 
    width?: string;
    align?: "left" | "center" | "right";
}


interface TableProps<T> {
    data: T[];
    columns: TableColumn<T>[];
    keyExtractor: (item: T) => string; 
}

export function Table<T>({ data, columns, keyExtractor }: TableProps<T>) {
    return (
        <div className="border border-[var(--light-neutral-color)] rounded-lg bg-[var(--surface-color)] overflow-visible">
            <table className="w-full text-left border-collapse text-sm">
                <thead className="bg-[var(--strong-surface-color)] text-[var(--neutral-color)]">
                    <tr>
                        {columns.map((col, index) => (
                            <th 
                                key={index} 
                                className={`p-3 border-b border-[var(--light-neutral-color)] font-semibold ${col.width || ""} ${col.align === "center" ? "text-center" : ""}`}
                            >
                                {col.header}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {data.map((item, rowIndex) => (
                        <tr 
                            key={keyExtractor(item)} 
                            className={`${rowIndex % 2 === 0 ? 'bg-[var(--surface-color)]' : 'bg-[var(--strong-surface-color)]'} hover:bg-[var(--light-neutral-color)] transition-colors relative`}
                        >
                            {columns.map((col, colIndex) => (
                                <td 
                                    key={colIndex} 
                                    className={`p-3 text-[var(--neutral-color)] ${col.align === "center" ? "text-center" : ""}`}
                                >
                    
                                    {col.render ? col.render(item) : (col.accessor ? String(item[col.accessor]) : null)}
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}