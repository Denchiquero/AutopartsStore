export interface PageResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}

export interface Part {
    id: number;
    name: string;
    sku: string;
    article: string;
    description: string;
    price: number;

    manufacturerId: number;
    manufacturerName: string;

    categoryId: number;
    categoryName: string;

    stockQuantity: number;
}