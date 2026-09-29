import { api } from "./api";

import type {
    CreateOrderRequest,
    Order,
    PageResponse
} from "../types/api";

export async function createOrder(
    request: CreateOrderRequest
): Promise<Order> {

    const response =
        await api.post<Order>(
            "/orders",
            request
        );

    return response.data;
}

export async function getOrders(
    page = 0,
    size = 10,
    status?: string
): Promise<PageResponse<Order>> {

    const response =
        await api.get<PageResponse<Order>>(
            "/orders",
            {
                params: {
                    page,
                    size,
                    status:
                        status || undefined
                }
            }
        );

    return response.data;
}

export async function getOrder(
    id: number
): Promise<Order> {

    const response =
        await api.get<Order>(
            `/orders/${id}`
        );

    return response.data;
}