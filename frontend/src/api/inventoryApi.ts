import { api } from "./api";
import type {
    InventoryMovement,
    PageResponse,
    Stock
} from "../types/api";

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

export async function getMovements(
    page = 0,
    size = 20
): Promise<PageResponse<InventoryMovement>> {

    const response =
        await api.get<PageResponse<InventoryMovement>>(
            "/inventory/movements",
            {
                params: {
                    page,
                    size
                }
            }
        );

    return response.data;
}