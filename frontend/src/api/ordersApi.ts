import { api } from "./api";

import type {
    CreateOrderRequest,
    Order,
    OrderStatus,
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
                    status: status || undefined
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

export async function changeOrderStatus(
    id: number,
    status: OrderStatus
): Promise<Order> {

    const response =
        await api.patch<Order>(
            `/orders/${id}/status`,
            {
                status
            }
        );

    return response.data;
}