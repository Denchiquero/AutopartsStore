import {
    Link,
    useNavigate
} from "react-router-dom";

import { useAuth } from "../auth/AuthContext";
import { useCart } from "../cart/CartContext";

function Header() {

    const navigate = useNavigate();

    const {
        user,
        loading,
        logout
    } = useAuth();

    const { totalItems } = useCart();

    function handleLogout() {

        logout();

        navigate("/");
    }

    return (
        <header className="header">

            <Link
                className="logo"
                to="/"
            >
                AutoParts
            </Link>

            <nav>

                <Link to="/">
                    Каталог
                </Link>

                <Link to="/vin">
                    Подбор по VIN
                </Link>

                <Link to="/cart">
                    Корзина
                    {totalItems > 0 && (
                        <span className="cart-count">
            {totalItems}
        </span>
                    )}
                </Link>

                {user && (
                    <Link to="/orders">
                        Заказы
                    </Link>
                )}

                {user && (
                    <Link to="/profile">
                        Профиль
                    </Link>
                )}

                {!loading && !user && (
                    <Link to="/login">
                        Войти
                    </Link>
                )}

                {!loading && user && (
                    <>
                        <span className="user-email">
                            {user.email}
                        </span>

                        <button
                            className="logout-button"
                            onClick={handleLogout}
                        >
                            Выйти
                        </button>
                    </>
                )}

                {user?.role === "ADMIN" && (
                    <Link to="/admin/inventory">
                        Админка
                    </Link>
                )}

            </nav>

        </header>
    );
}

export default Header;