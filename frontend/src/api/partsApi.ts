import { api } from "./api";
import type { PageResponse, Part } from "../types/api";

export interface PartsParams {
    search?: string;
    manufacturerId?: number;
    categoryId?: number;
    sortBy?: string;
    direction?: string;
    page?: number;
    size?: number;
}

export async function getParts(
    params: PartsParams = {}
): Promise<PageResponse<Part>> {

    const response = await api.get<PageResponse<Part>>(
        "/parts",
        {
            params,
        }
    );

    return response.data;
}

export async function getPart(
    id: number
): Promise<Part> {

    const response = await api.get<Part>(
        `/parts/${id}`
    );

    return response.data;
}