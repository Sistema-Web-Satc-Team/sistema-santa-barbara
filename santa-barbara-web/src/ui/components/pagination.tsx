import { ArrowLeft, ArrowRight } from "lucide-react";
import type { ChangeEvent } from "react";
import "@/ui/styles/pagination.css";

type PageItem = number | "...";

interface PaginationProps {
    currentPage: number;
    totalPages: number;
    itemsPerPage: number;
    onPageChange: (page: number) => void;
    onItemsPerPageChange: (e: ChangeEvent<HTMLSelectElement>) => void;
}

export function Pagination({ currentPage, totalPages, itemsPerPage, onPageChange, onItemsPerPageChange }: PaginationProps) {
    const getPageNumbers = (): PageItem[] => {
        const pages: PageItem[] = [];
        const maxVisiblePages = 5;

        if (totalPages <= maxVisiblePages) {
            for (let page = 1; page <= totalPages; page++) {
                pages.push(page);
            }
        } else {
            pages.push(1);

            if (currentPage > 3) {
                pages.push("...");
            }

            const startPage = Math.max(2, currentPage - 1);
            const endPage = Math.min(totalPages - 1, currentPage + 1);

            for (let page = startPage; page <= endPage; page++) {
                pages.push(page);
            }

            if (currentPage < totalPages - 2) {
                pages.push("...");
            }

            pages.push(totalPages);
        }

        return pages;
    };

    return (
        <div className="pagination">
            <button
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 1}
                className="pagination__navigation pagination__navigation--previous"
            >
                <ArrowLeft size={16} /> Previous
            </button>

            <div className="pagination__pages">
                {getPageNumbers().map((page, index) => (
                    <button
                        key={`${page}-${index}`}
                        disabled={page === "..."}
                        onClick={() => typeof page === "number" && onPageChange(page)}
                        className={`pagination__page ${
                            page === currentPage
                                ? "pagination__page--active"
                                : page === "..."
                                    ? "pagination__page--ellipsis"
                                    : "pagination__page--available"
                        }`}
                    >
                        {page}
                    </button>
                ))}
            </div>

            <button
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
                className="pagination__navigation pagination__navigation--next"
            >
                Next <ArrowRight size={16} />
            </button>

            <select
                value={itemsPerPage}
                onChange={onItemsPerPageChange}
                className="pagination__select"
            >
                <option value={1}>1 / página</option>
                <option value={5}>5 / página</option>
                <option value={10}>10 / página</option>
                <option value={20}>20 / página</option>
            </select>
        </div>
    );
}