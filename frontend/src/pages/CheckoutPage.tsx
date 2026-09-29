import {
    useState
} from "react";

import {
    Navigate,
    useNavigate
} from "react-router-dom";

import { useCart } from "../cart/CartContext";
import { useAuth } from "../auth/AuthContext";
import { createOrder } from "../api/ordersApi";

function CheckoutPage() {

    const { user } = useAuth();

    const {
        items,
        totalPrice,
        clearCart
    } = useCart();

    const navigate =
        useNavigate();

    const [loading, setLoading] =
        useState(false);

    const [error, setError] =
        useState("");

    if (!user) {
        return (
            <Navigate
                to="/login"
                replace
            />
        );
    }

    if (items.length === 0) {
        return (
            <Navigate
                to="/cart"
                replace
            />
        );
    }

    async function handleCreateOrder() {

        try {
            setLoading(true);
            setError("");

            const order =
                await createOrder({
                    items: items.map(item => ({
                        partId: item.part.id,
                        quantity: item.quantity
                    }))
                });

            clearCart();

            navigate(
                `/orders/${order.id}`
            );

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось оформить заказ. Проверьте наличие товаров."
            );

        } finally {
            setLoading(false);
        }
    }

    return (
        <div>

            <h1>
                Оформление заказа
            </h1>

            <div className="checkout">

                <h2>Состав заказа</h2>

                {items.map(item => (
                    <div
                        className="checkout-item"
                        key={item.part.id}
                    >
                        <span>
                            {item.part.name}
                            {" × "}
                            {item.quantity}
                        </span>

                        <strong>
                            {(
                                Number(item.part.price)
                                * item.quantity
                            ).toLocaleString("ru-RU")} ₽
                        </strong>
                    </div>
                ))}

                <hr />

                <div className="checkout-total">
                    Итого:{" "}
                    <strong>
                        {totalPrice.toLocaleString(
                            "ru-RU"
                        )} ₽
                    </strong>
                </div>

                {error && (
                    <p className="error">
                        {error}
                    </p>
                )}

                <button
                    className="checkout-button"
                    disabled={loading}
                    onClick={handleCreateOrder}
                >
                    {loading
                        ? "Оформление..."
                        : "Подтвердить заказ"}
                </button>

            </div>

        </div>
    );
}

export default CheckoutPage;