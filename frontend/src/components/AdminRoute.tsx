import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

function AdminRoute({
                        children
                    }: {
    children: React.ReactNode;
}) {
    const {
        user,
        loading
    } = useAuth();

    if (loading) {
        return <p>Загрузка...</p>;
    }

    if (!user) {
        return (
            <Navigate
                to="/login"
                replace
            />
        );
    }

    if (user.role !== "ADMIN") {
        return (
            <Navigate
                to="/"
                replace
            />
        );
    }

    return children;
}

export default AdminRoute;