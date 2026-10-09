import { api } from "./api";

import type {
    Category,
    CategoryRequest
} from "../types/api";

export async function getCategories():
    Promise<Category[]> {

    const response =
        await api.get<Category[]>(
            "/categories"
        );

    return response.data;
}

export async function createCategory(
    request: CategoryRequest
): Promise<Category> {

    const response =
        await api.post<Category>(
            "/categories",
            request
        );

    return response.data;
}

export async function updateCategory(
    id: number,
    request: CategoryRequest
): Promise<Category> {

    const response =
        await api.put<Category>(
            `/categories/${id}`,
            request
        );

    return response.data;
}

export async function deleteCategory(
    id: number
): Promise<void> {

    await api.delete(
        `/categories/${id}`
    );
}