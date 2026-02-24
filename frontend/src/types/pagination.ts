export interface Pagination<T> {
  currentPage: number;
  data: T[];
  pageSize: number;
  totalElements: number;
  totalPages: number;
}
