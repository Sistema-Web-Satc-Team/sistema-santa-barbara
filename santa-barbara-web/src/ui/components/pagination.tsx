import type { ChangeEvent } from "react";

interface PaginationProps {
    currentPage: number;
    totalPages: number;
    itemsPerPage: number;
    onPageChange: (page: number) => void;
    onItemsPerPageChange: (e: ChangeEvent<HTMLSelectElement>) => void;
}

export function Pagination({ currentPage, totalPages, itemsPerPage, onPageChange, onItemsPerPageChange }: PaginationProps) {
    const getPageNumbers = () => {
        const pages = [];
        let start = Math.max(1, currentPage - 2);
        const end = Math.min(totalPages, start + 4);
        if (end - start < 4) start = Math.max(1, end - 4);

        for (let i = start; i <= end; i++) {
            pages.push(i);
        }
        return pages;
    };

    return (
        <div className="flex items-center justify-between mt-4 text-sm">
            <div className="flex border border-(--light-neutral-color) rounded overflow-hidden bg-(--surface-color)">
                <button onClick={() => onPageChange(1)} disabled={currentPage === 1} className="px-3 py-1.5 border-r border-(--light-neutral-color) hover:bg-(--strong-surface-color) text-(--neutral-color) disabled:opacity-50 disabled:cursor-not-allowed">
                    &lt;&lt; First
                </button>
                <button onClick={() => onPageChange(currentPage - 1)} disabled={currentPage === 1} className="px-3 py-1.5 border-r border-(--light-neutral-color) hover:bg-(--strong-surface-color) text-(--neutral-color) disabled:opacity-50 disabled:cursor-not-allowed">
                    &lt; Previous
                </button>
                
                {getPageNumbers().map((page) => (
                    <button 
                        key={page}
                        onClick={() => onPageChange(page)}
                        className={`px-3.5 py-1.5 border-r border-(--light-neutral-color) ${currentPage === page ? 'bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)' : 'hover:bg-(--strong-surface-color) text-(--neutral-color)'}`}
                    >
                        {page}
                    </button>
                ))}

                <button onClick={() => onPageChange(currentPage + 1)} disabled={currentPage === totalPages} className="px-3.5 py-1.5 border-r border-(--light-neutral-color) hover:bg-(--strong-surface-color) text-(--neutral-color) disabled:opacity-50 disabled:cursor-not-allowed">
                    Next &gt;
                </button>
                <button onClick={() => onPageChange(totalPages)} disabled={currentPage === totalPages} className="px-3.5 py-1.5 hover:bg-(--strong-surface-color) text-(--neutral-color) disabled:opacity-50 disabled:cursor-not-allowed">
                    Last &gt;&gt;
                </button>
            </div>
            <select 
                value={itemsPerPage}
                onChange={onItemsPerPageChange}
                className="border border-(--light-neutral-color) rounded px-2 py-1.5 bg-(--surface-color) text-(--neutral-color) outline-none"
            >
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="20">20</option>
            </select>
        </div>
    );
}