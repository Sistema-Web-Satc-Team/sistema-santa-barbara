import { useState } from "react";
import { toast } from "sonner";

export interface UsePaginationResponse {
  data: {
    page: number;
    size: number;
    
    totalPages: number;
    totalElements: number;
  };

  state: {
    hasNext: boolean;
    hasPrevious: boolean;
  }

  actions: {
    setPage: (page: number) => void;
    setSize: (size: number) => void;
    resetPage: () => void;
    setTotalElements: (total: number) => void;
    setTotalPages: (total: number) => void;
  };
}

interface UsePaginationOptions {
  initialSize?: number;
}

export function usePagination(options: UsePaginationOptions = {}): UsePaginationResponse {
  const [page, setPage] = useState(0);
  const [size, setSizeState] = useState(options.initialSize ?? 10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  return {

    data: {
      page: page,
      size: size,
      totalPages: totalPages,
      totalElements: totalElements
    },

    state: {
      hasNext: page + 1 < totalPages,
      hasPrevious: page > 0
    },

    actions: {
      setPage: (newPage: number) => {
        if (totalPages > 0 && newPage >= totalPages) {
          toast.warning("Não é possível navegar para uma página além do total!");
          return;
        }

        if (newPage < 0) {
          toast.warning("Não existem páginas negativas!");
          return;
        }
        setPage(newPage);
      },
      setTotalElements,
      setTotalPages,
      setSize: (newSize) => {
        setSizeState(newSize);
        setPage(0); 
      },
      resetPage: () => setPage(0),
    }

  };
}
