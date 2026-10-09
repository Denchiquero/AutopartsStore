import { api } from "./api";

import type {
    Part,
    VehicleApplication,
    VehicleApplicationRequest
} from "../types/api";

export async function getVehicleParts(
    vehicleId: number
): Promise<Part[]> {

    const response =
        await api.get<Part[]>(
            `/vehicles/${vehicleId}/parts`
        );

    return response.data;
}

export async function getVehicles():
    Promise<VehicleApplication[]> {

    const response =
        await api.get<VehicleApplication[]>(
            "/vehicles"
        );

    return response.data;
}

export async function createVehicle(
    request: VehicleApplicationRequest
): Promise<VehicleApplication> {

    const response =
        await api.post<VehicleApplication>(
            "/vehicles",
            request
        );

    return response.data;
}

export async function updateVehicle(
    id: number,
    request: VehicleApplicationRequest
): Promise<VehicleApplication> {

    const response =
        await api.put<VehicleApplication>(
            `/vehicles/${id}`,
            request
        );

    return response.data;
}

export async function deleteVehicle(
    id: number
): Promise<void> {

    await api.delete(
        `/vehicles/${id}`
    );
}