
interface FetchByIdRequest {
    id: string;
}

interface ListResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalPages?: number;
  totalElements?: number;
}
