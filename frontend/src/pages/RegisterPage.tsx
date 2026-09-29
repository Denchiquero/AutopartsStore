import {
    type FormEvent,
    useState
} from "react";

import {
    Link,
    useNavigate
} from "react-router-dom";

import {
    register
} from "../api/authApi";

import {
    useAuth
} from "../auth/AuthContext";

function RegisterPage() {

    const navigate = useNavigate();
    const { login } = useAuth();

    const [name, setName] =
        useState("");

    const [phone, setPhone] =
        useState("");

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [error, setError] =
        useState("");

    const [loading, setLoading] =
        useState(false);

    async function handleSubmit(
        event: FormEvent
    ) {

        event.preventDefault();

        try {
            setLoading(true);
            setError("");

            await register({
                name,
                phone,
                email,
                password
            });

            // После регистрации сразу логинимся
            await login(
                email,
                password
            );

            navigate("/");

        } catch (error) {
            console.error(error);

            setError(
                "Не удалось зарегистрироваться"
            );

        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="auth-page">

            <form
                className="auth-form"
                onSubmit={handleSubmit}
            >

                <h1>Регистрация</h1>

                <label>
                    Имя
                </label>

                <input
                    value={name}
                    required
                    onChange={(event) =>
                        setName(
                            event.target.value
                        )
                    }
                />

                <label>
                    Телефон
                </label>

                <input
                    value={phone}
                    required
                    onChange={(event) =>
                        setPhone(
                            event.target.value
                        )
                    }
                />

                <label>
                    Email
                </label>

                <input
                    type="email"
                    value={email}
                    required
                    onChange={(event) =>
                        setEmail(
                            event.target.value
                        )
                    }
                />

                <label>
                    Пароль
                </label>

                <input
                    type="password"
                    value={password}
                    required
                    minLength={8}
                    onChange={(event) =>
                        setPassword(
                            event.target.value
                        )
                    }
                />

                {error && (
                    <p className="error">
                        {error}
                    </p>
                )}

                <button
                    type="submit"
                    disabled={loading}
                >
                    {loading
                        ? "Регистрация..."
                        : "Зарегистрироваться"}
                </button>

                <p>
                    Уже есть аккаунт?{" "}
                    <Link to="/login">
                        Войти
                    </Link>
                </p>

            </form>

        </div>
    );
}

export default RegisterPage;