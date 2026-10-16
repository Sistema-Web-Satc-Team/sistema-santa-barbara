import { Button } from '#/components/ui/button';
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from '#/components/ui/select';


interface DataPaginationProps {
  pagination: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  },
  onSizeChange: (size: number) => void;
  onPageChange: (page: number) => void;
  pagesSizes: number[];
}

export function DataPagination({
  pagination,
  onSizeChange,
  onPageChange,
  pagesSizes = [10,20,50]
}: DataPaginationProps) {

  return (
    <div className="flex items-center justify-between mt-4">
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <span>Itens por página</span>
        <Select value={String(pagination.size)} onValueChange={(v) => onSizeChange(Math.floor(Number(v)))}>
          <SelectTrigger className="w-20">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {pagesSizes.map((s) => (
              <SelectItem key={s} value={String(s)}>{s}</SelectItem>
            ))}
          </SelectContent>
        </Select>
        {pagination.totalElements !== undefined && <span>{pagination.totalElements} no total</span>}
      </div>

      <div className="flex items-center gap-2">
        <Button variant="outline" size="sm" disabled={pagination.page === 0} onClick={() => onPageChange(pagination.page - 1)}>
          Anterior
        </Button>
        <span className="text-sm">
          Página {pagination.page + 1}
          {pagination.totalPages !== undefined && ` de ${Math.max(pagination.totalPages, 1)}`}
        </span>
        <Button variant="outline" size="sm" disabled={pagination.page + 1 >= pagination.totalPages} onClick={() => onPageChange(pagination.page + 1)}>
          Próxima
        </Button>
      </div>
    </div>
  );
}