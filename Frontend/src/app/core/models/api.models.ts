export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}

export interface ApiErrorResponse {
  timestamp: number;
  status: number;
  error: string;
  message: string;
  path: string;
}
