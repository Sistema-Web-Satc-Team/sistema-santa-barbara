
interface FetchByIdRequest {
  id: string;
}

interface PageDefaultResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}


interface PageFilters<Entity> {
  page?: string;
  size?: string;
  sort?: `${keyof Entity},${"asc" | "desc"}`[];
}