import { api } from "./api";

import type {
    DecodedVin,
    Part
} from "../types/api";

export async function decodeVin(
    vin: string
): Promise<DecodedVin> {

    const response =
        await api.get<DecodedVin>(
            `/vin/${vin}`
        );

    return response.data;
}

export async function getPartsByVin(
    vin: string
): Promise<Part[]> {

    const response =
        await api.get<Part[]>(
            `/vin/${vin}/parts`
        );

    return response.data;
}