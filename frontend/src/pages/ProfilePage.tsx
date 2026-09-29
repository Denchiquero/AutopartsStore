import {
    type FormEvent,
    useEffect,
    useState
} from "react";

import {
    useNavigate
} from "react-router-dom";

import {
    getProfile,
    updateProfile
} from "../api/profileApi";

import {
    useAuth
} from "../auth/AuthContext";

function ProfilePage() {

    const navigate = useNavigate();

    const {
        user,
        logout
    } = useAuth();

    const [name, setName] =
        useState("");

    const [phone, setPhone] =
        useState("");

    const [email, setEmail] =
        useState("");

    const [loading, setLoading] =
        useState(true);

    const [saving, setSaving] =
        useState(false);

    const [error, setError] =
        useState("");

    const [message, setMessage] =
        useState("");

    useEffect(() => {

        getProfile()
            .then(profile => {
                setName(profile.name);
                setPhone(profile.phone);
                setEmail(profile.email);
            })
            .catch(error => {
                console.error(error);

                setError(
                    "Не удалось загрузить профиль"
                );
            })
            .finally(() => {
                setLoading(false);
            });

    }, []);

    async function handleSubmit(
        event: FormEvent
    ) {

        event.preventDefault();

        try {

            setSaving(true);
            setError("");
            setMessage("");

            await updateProfile({
                name,
                phone,
                email
            });

            /*
             * Сейчас JWT использует email
             * как subject.
             *
             * Если email изменился,
             * старый JWT больше не подходит.
             */
            if (
                user &&
                email !== user.email
            ) {

                logout();

                navigate("/login");

                return;
            }

            setMessage(
                "Профиль сохранён"
            );

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось сохранить профиль"
            );

        } finally {

            setSaving(false);
        }
    }

    if (loading) {
        return <p>Загрузка...</p>;
    }

    return (
        <div className="profile-page">

            <h1>Профиль</h1>

            <form
                className="profile-form"
                onSubmit={handleSubmit}
            >

                <label>
                    Имя
                </label>

                <input
                    value={name}
                    required
                    onChange={event =>
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
                    onChange={event =>
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
                    onChange={event =>
                        setEmail(
                            event.target.value
                        )
                    }
                />

                {error && (
                    <p className="error">
                        {error}
                    </p>
                )}

                {message && (
                    <p className="success">
                        {message}
                    </p>
                )}

                <button
                    type="submit"
                    disabled={saving}
                >
                    {saving
                        ? "Сохранение..."
                        : "Сохранить"}
                </button>

            </form>

        </div>
    );
}

export default ProfilePage;