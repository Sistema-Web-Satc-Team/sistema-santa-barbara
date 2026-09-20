import type { ChangeEvent } from "react";
import "@/ui/styles/pagination.css";

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
        <div className="pagination">
            <div className="pagination__controls">
                <button onClick={() => onPageChange(1)} disabled={currentPage === 1} className="pagination__button pagination__button--bordered">
                    &lt;&lt; First
                </button>
                <button onClick={() => onPageChange(currentPage - 1)} disabled={currentPage === 1} className="pagination__button pagination__button--bordered">
                    &lt; Previous
                </button>
                
                {getPageNumbers().map((page) => (
                    <button 
                        key={page}
                        onClick={() => onPageChange(page)}
                        className={`pagination__button pagination__button--bordered ${currentPage === page ? "pagination__button--selected" : ""}`}
                    >
                        {page}
                    </button>
                ))}

                <button onClick={() => onPageChange(currentPage + 1)} disabled={currentPage === totalPages} className="pagination__button pagination__button--bordered">
                    Next &gt;
                </button>
                <button onClick={() => onPageChange(totalPages)} disabled={currentPage === totalPages} className="pagination__button">
                    Last &gt;&gt;
                </button>
            </div>
            <select 
                value={itemsPerPage}
                onChange={onItemsPerPageChange}
                className="pagination__select"
            >
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="20">20</option>
            </select>
        </div>
    );
}