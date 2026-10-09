import { api } from "./api";

import type {
    Part,
    PartRequest
} from "../types/api";

export async function createPart(
    request: PartRequest
): Promise<Part> {

    const response =
        await api.post<Part>(
            "/parts",
            request
        );

    return response.data;
}

export async function updatePart(
    id: number,
    request: PartRequest
): Promise<Part> {

    const response =
        await api.put<Part>(
            `/parts/${id}`,
            request
        );

    return response.data;
}

export async function deletePart(
    id: number
): Promise<void> {

    await api.delete(
        `/parts/${id}`
    );
}