import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";

import {
    getCurrentUser,
    login as loginRequest
} from "../api/authApi";

import type {
    CurrentUser
} from "../types/api";

interface AuthContextType {
    user: CurrentUser | null;
    loading: boolean;

    login: (
        email: string,
        password: string
    ) => Promise<void>;

    logout: () => void;
}

const AuthContext =
    createContext<AuthContextType | undefined>(
        undefined
    );

export function AuthProvider({
                                 children
                             }: {
    children: React.ReactNode;
}) {

    const [user, setUser] =
        useState<CurrentUser | null>(null);

    const [loading, setLoading] =
        useState(true);

    useEffect(() => {

        const token =
            localStorage.getItem("token");

        if (!token) {
            setLoading(false);
            return;
        }

        getCurrentUser()
            .then(setUser)
            .catch(() => {
                localStorage.removeItem("token");
                setUser(null);
            })
            .finally(() => {
                setLoading(false);
            });

    }, []);

    async function login(
        email: string,
        password: string
    ) {

        const response =
            await loginRequest({
                email,
                password
            });

        localStorage.setItem(
            "token",
            response.token
        );

        setUser({
            userId: response.userId,
            email: response.email,
            role: response.role,
            customerId: response.customerId
        });
    }

    function logout() {

        localStorage.removeItem("token");

        setUser(null);
    }

    return (
        <AuthContext.Provider
            value={{
                user,
                loading,
                login,
                logout
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {

    const context =
        useContext(AuthContext);

    if (!context) {
        throw new Error(
            "useAuth must be used inside AuthProvider"
        );
    }

    return context;
}