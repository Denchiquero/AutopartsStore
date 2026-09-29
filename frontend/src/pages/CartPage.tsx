import {
    Link,
    useNavigate
} from "react-router-dom";

import { useCart } from "../cart/CartContext";
import { useAuth } from "../auth/AuthContext";

function CartPage() {

    const {
        items,
        removeFromCart,
        changeQuantity,
        totalPrice,
        clearCart
    } = useCart();

    const { user } = useAuth();

    const navigate = useNavigate();

    function handleCheckout() {

        if (!user) {
            navigate("/login");
            return;
        }

        navigate("/checkout");
    }

    if (items.length === 0) {

        return (
            <div className="empty-cart">

                <h1>Корзина</h1>

                <p>
                    Корзина пока пуста
                </p>

                <Link to="/">
                    Перейти в каталог
                </Link>

            </div>
        );
    }

    return (
        <div>

            <h1>Корзина</h1>

            <div className="cart-layout">

                <div className="cart-items">

                    {items.map(item => (

                        <div
                            className="cart-item"
                            key={item.part.id}
                        >

                            <div className="cart-item-info">

                                <Link
                                    to={`/parts/${item.part.id}`}
                                >
                                    <h3>
                                        {item.part.name}
                                    </h3>
                                </Link>

                                <p>
                                    {item.part.manufacturerName}
                                </p>

                                <p>
                                    Артикул:{" "}
                                    {item.part.article}
                                </p>

                            </div>

                            <div className="cart-quantity">

                                <button
                                    onClick={() =>
                                        changeQuantity(
                                            item.part.id,
                                            item.quantity - 1
                                        )
                                    }
                                >
                                    −
                                </button>

                                <span>
                                    {item.quantity}
                                </span>

                                <button
                                    disabled={
                                        item.quantity >=
                                        item.part.stockQuantity
                                    }
                                    onClick={() =>
                                        changeQuantity(
                                            item.part.id,
                                            item.quantity + 1
                                        )
                                    }
                                >
                                    +
                                </button>

                            </div>

                            <div className="cart-item-price">

                                {(
                                    Number(item.part.price)
                                    * item.quantity
                                ).toLocaleString("ru-RU")} ₽

                            </div>

                            <button
                                className="remove-button"
                                onClick={() =>
                                    removeFromCart(
                                        item.part.id
                                    )
                                }
                            >
                                Удалить
                            </button>

                        </div>

                    ))}

                </div>

                <aside className="cart-summary">

                    <h2>Итого</h2>

                    <div className="cart-total">
                        {totalPrice.toLocaleString(
                            "ru-RU"
                        )} ₽
                    </div>

                    <button
                        className="checkout-button"
                        onClick={handleCheckout}
                    >
                        Оформить заказ
                    </button>

                    <button
                        className="clear-cart-button"
                        onClick={clearCart}
                    >
                        Очистить корзину
                    </button>

                </aside>

            </div>

        </div>
    );
}

export default CartPage;