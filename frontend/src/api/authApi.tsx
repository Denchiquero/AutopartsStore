import { api } from "./api";

import type {
    CurrentUser,
    LoginRequest,
    LoginResponse,
    RegisterRequest,
    RegisterResponse
} from "../types/api";

export async function login(
    request: LoginRequest
): Promise<LoginResponse> {

    const response =
        await api.post<LoginResponse>(
            "/auth/login",
            request
        );

    return response.data;
}

export async function register(
    request: RegisterRequest
): Promise<RegisterResponse> {

    const response =
        await api.post<RegisterResponse>(
            "/auth/register",
            request
        );

    return response.data;
}

export async function getCurrentUser():
    Promise<CurrentUser> {

    const response =
        await api.get<CurrentUser>(
            "/auth/me"
        );

    return response.data;
}