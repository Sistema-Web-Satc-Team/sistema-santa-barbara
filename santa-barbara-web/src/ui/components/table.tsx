import React from "react";
import "@/ui/styles/table.css";


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
        <div className="table-container">
            <table className="data-table">
                <thead className="data-table__head">
                    <tr>
                        {columns.map((col, index) => (
                            <th 
                                key={index} 
                                className={`data-table__header ${col.align === "center" ? "data-table__cell--center" : ""} ${col.align === "right" ? "data-table__cell--right" : ""}`}
                                style={{ width: col.width === "w-24" ? "6rem" : col.width }}
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
                            className={`data-table__row ${rowIndex % 2 === 0 ? "data-table__row--even" : "data-table__row--odd"}`}
                        >
                            {columns.map((col, colIndex) => (
                                <td 
                                    key={colIndex} 
                                    className={`data-table__cell ${col.align === "center" ? "data-table__cell--center" : ""} ${col.align === "right" ? "data-table__cell--right" : ""}`}
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