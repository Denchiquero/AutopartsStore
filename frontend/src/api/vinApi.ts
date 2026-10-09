import { api } from "./api";

import type {
    DecodedVin
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