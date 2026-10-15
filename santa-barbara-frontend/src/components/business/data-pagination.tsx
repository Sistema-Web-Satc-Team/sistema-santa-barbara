import { Button } from '#/components/ui/button';
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from '#/components/ui/select';
import type { UsePaginationResponse } from '#/hooks/use-pagination';


interface DataPaginationProps {
  pagination: UsePaginationResponse;
  pagesSizes?: number[];
}

export function DataPagination({
  pagination,
  pagesSizes = [10,20,50]
}: DataPaginationProps) {

  return (
    <div className="flex items-center justify-between mt-4">
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <span>Itens por página</span>
        <Select value={String(pagination.data.size)} onValueChange={(v) => pagination.actions.setSize(Number(v))}>
          <SelectTrigger className="w-20">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {pagesSizes.map((s) => (
              <SelectItem key={s} value={String(s)}>{s}</SelectItem>
            ))}
          </SelectContent>
        </Select>
        {pagination.data.totalElements !== undefined && <span>{pagination.data.totalElements} no total</span>}
      </div>

      <div className="flex items-center gap-2">
        <Button variant="outline" size="sm" disabled={pagination.data.page === 0} onClick={() => pagination.actions.setPage(pagination.data.page - 1)}>
          Anterior
        </Button>
        <span className="text-sm">
          Página {pagination.data.page + 1}
          {pagination.data.totalPages !== undefined && ` de ${Math.max(pagination.data.totalPages, 1)}`}
        </span>
        <Button variant="outline" size="sm" disabled={!pagination.state.hasNext} onClick={() => pagination.actions.setPage(pagination.data.page + 1)}>
          Próxima
        </Button>
      </div>
    </div>
  );
}