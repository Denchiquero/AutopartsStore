import { api } from "./api";
import type { Stock } from "../types/api";

export interface InventoryOperationRequest {
    quantity: number;
    comment: string;
}

export async function getStock():
    Promise<Stock[]> {

    const response =
        await api.get<Stock[]>(
            "/inventory"
        );

    return response.data;
}

export async function receipt(
    partId: number,
    request: InventoryOperationRequest
): Promise<Stock> {

    const response =
        await api.post<Stock>(
            `/inventory/parts/${partId}/receipt`,
            request
        );

    return response.data;
}

export async function writeOff(
    partId: number,
    request: InventoryOperationRequest
): Promise<Stock> {

    const response =
        await api.post<Stock>(
            `/inventory/parts/${partId}/write-off`,
            request
        );

    return response.data;
}