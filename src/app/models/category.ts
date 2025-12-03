export interface Category {
  id: number;
  name: string;
}

export interface CategoryResponse {
  code: number;
  result: Category[];
}
