import { api } from "./api";

import type {
    VehicleApplication
} from "../types/api";

export async function getPartFitments(
    partId: number
): Promise<VehicleApplication[]> {

    const response =
        await api.get<VehicleApplication[]>(
            `/parts/${partId}/fitments`
        );

    return response.data;
}

export async function addPartFitment(
    partId: number,
    vehicleId: number
): Promise<void> {

    await api.post(
        `/parts/${partId}/fitments/${vehicleId}`
    );
}

export async function deletePartFitment(
    partId: number,
    vehicleId: number
): Promise<void> {

    await api.delete(
        `/parts/${partId}/fitments/${vehicleId}`
    );
}