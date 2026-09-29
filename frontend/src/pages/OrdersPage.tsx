import {
    useEffect,
    useState
} from "react";

import {
    Link
} from "react-router-dom";

import {
    getOrders
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

function OrdersPage() {

    const [orders, setOrders] =
        useState<Order[]>([]);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const [page, setPage] =
        useState(0);

    const [totalPages, setTotalPages] =
        useState(0);

    useEffect(() => {

        async function loadOrders() {

            try {

                setLoading(true);
                setError("");

                const data =
                    await getOrders(
                        page,
                        10
                    );

                setOrders(
                    data.content
                );

                setTotalPages(
                    data.totalPages
                );

            } catch (error) {

                console.error(error);

                setError(
                    "Не удалось загрузить заказы"
                );

            } finally {

                setLoading(false);
            }
        }

        loadOrders();

    }, [page]);

    return (
        <div>

            <h1>Мои заказы</h1>

            {loading && (
                <p>Загрузка...</p>
            )}

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            {!loading &&
                !error &&
                orders.length === 0 && (

                    <div className="empty-orders">

                        <p>
                            У вас пока нет заказов
                        </p>

                        <Link to="/">
                            Перейти в каталог
                        </Link>

                    </div>
                )}

            <div className="orders-list">

                {orders.map(order => (

                    <Link
                        to={`/orders/${order.id}`}
                        className="order-card"
                        key={order.id}
                    >

                        <div>
                            <h3>
                                Заказ №{order.id}
                            </h3>

                            <p>
                                {new Date(
                                    order.createdAt
                                ).toLocaleString(
                                    "ru-RU"
                                )}
                            </p>
                        </div>

                        <div>
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

                        <strong>
                            {Number(
                                order.totalPrice
                            ).toLocaleString(
                                "ru-RU"
                            )} ₽
                        </strong>

                    </Link>

                ))}

            </div>

            {totalPages > 1 && (

                <div className="pagination">

                    <button
                        disabled={page === 0}
                        onClick={() =>
                            setPage(
                                page - 1
                            )
                        }
                    >
                        Назад
                    </button>

                    <span>
                        Страница{" "}
                        {page + 1} из{" "}
                        {totalPages}
                    </span>

                    <button
                        disabled={
                            page + 1
                            >= totalPages
                        }
                        onClick={() =>
                            setPage(
                                page + 1
                            )
                        }
                    >
                        Далее
                    </button>

                </div>
            )}

        </div>
    );
}

export default OrdersPage;