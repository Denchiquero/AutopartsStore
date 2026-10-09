import { api } from "./api";

import type {
    Manufacturer,
    ManufacturerRequest
} from "../types/api";

export async function getManufacturers():
    Promise<Manufacturer[]> {

    const response =
        await api.get<Manufacturer[]>(
            "/manufacturers"
        );

    return response.data;
}

export async function createManufacturer(
    request: ManufacturerRequest
): Promise<Manufacturer> {

    const response =
        await api.post<Manufacturer>(
            "/manufacturers",
            request
        );

    return response.data;
}

export async function updateManufacturer(
    id: number,
    request: ManufacturerRequest
): Promise<Manufacturer> {

    const response =
        await api.put<Manufacturer>(
            `/manufacturers/${id}`,
            request
        );

    return response.data;
}

export async function deleteManufacturer(
    id: number
): Promise<void> {

    await api.delete(
        `/manufacturers/${id}`
    );
}