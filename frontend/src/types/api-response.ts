export interface ApiResponse<T> {
  statusCode: number;
  error: string | null;
  message: string;
  data: T;
}

export type ApiSuccess<T> = ApiResponse<T> & {
  error: null;
};

export type ApiError = ApiResponse<null> & {
  error: string;
};
