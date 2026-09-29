import { api } from "./api";

import type {
    Profile,
    UpdateProfileRequest
} from "../types/api";

export async function getProfile():
    Promise<Profile> {

    const response =
        await api.get<Profile>(
            "/profile"
        );

    return response.data;
}

export async function updateProfile(
    request: UpdateProfileRequest
): Promise<Profile> {

    const response =
        await api.put<Profile>(
            "/profile",
            request
        );

    return response.data;
}