import { Link } from "react-router-dom";

function Header() {

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

                <Link to="/orders">
                    Заказы
                </Link>

                <Link to="/profile">
                    Профиль
                </Link>

                <Link to="/login">
                    Войти
                </Link>
            </nav>

        </header>
    );
}

export default Header;