import {
    useEffect,
    useState
} from "react";

import {
    Link,
    useParams
} from "react-router-dom";

import {
    getOrder
} from "../api/ordersApi";

import type {
    Order,
    OrderStatus
} from "../types/api";

function getStatusName(
    status: OrderStatus
) {

    switch (status) {

        case "CREATED":
            return "Создан";

        case "CONFIRMED":
            return "Подтверждён";

        case "COMPLETED":
            return "Завершён";

        case "CANCELLED":
            return "Отменён";
    }
}

function OrderPage() {

    const { id } =
        useParams();

    const [order, setOrder] =
        useState<Order | null>(null);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    useEffect(() => {

        if (!id) {
            return;
        }

        getOrder(Number(id))
            .then(setOrder)

            .catch(error => {

                console.error(error);

                setError(
                    "Заказ не найден"
                );
            })

            .finally(() => {
                setLoading(false);
            });

    }, [id]);

    if (loading) {
        return <p>Загрузка...</p>;
    }

    if (error || !order) {

        return (
            <div>

                <p className="error">
                    {error}
                </p>

                <Link to="/orders">
                    ← Вернуться к заказам
                </Link>

            </div>
        );
    }

    return (
        <div>

            <Link
                className="back-link"
                to="/orders"
            >
                ← Мои заказы
            </Link>

            <div className="order-header">

                <div>
                    <h1>
                        Заказ №{order.id}
                    </h1>

                    <p>
                        {new Date(
                            order.createdAt
                        ).toLocaleString(
                            "ru-RU"
                        )}
                    </p>
                </div>

                <span
                    className={
                        `order-status status-${order.status.toLowerCase()}`
                    }
                >
                    {getStatusName(
                        order.status
                    )}
                </span>

            </div>

            <div className="order-details">

                <h2>
                    Состав заказа
                </h2>

                {order.items.map(item => (

                    <div
                        className="order-item"
                        key={item.partId}
                    >

                        <div>

                            <Link
                                to={`/parts/${item.partId}`}
                            >
                                <strong>
                                    {item.partName}
                                </strong>
                            </Link>

                            <p>
                                SKU: {item.sku}
                            </p>

                        </div>

                        <span>
                            {item.quantity} шт.
                        </span>

                        <span>
                            {Number(
                                item.unitPrice
                            ).toLocaleString(
                                "ru-RU"
                            )} ₽ / шт.
                        </span>

                        <strong>
                            {Number(
                                item.totalPrice
                            ).toLocaleString(
                                "ru-RU"
                            )} ₽
                        </strong>

                    </div>
                ))}

                <div className="order-total">

                    <span>
                        Итого
                    </span>

                    <strong>
                        {Number(
                            order.totalPrice
                        ).toLocaleString(
                            "ru-RU"
                        )} ₽
                    </strong>

                </div>

            </div>

        </div>
    );
}

export default OrderPage;