import {
    useEffect,
    useState
} from "react";

import {
    Link
} from "react-router-dom";

import {
    changeOrderStatus,
    getOrders
} from "../../api/ordersApi";

import type {
    Order,
    OrderStatus
} from "../../types/api";

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

function AdminOrdersPage() {

    const [orders, setOrders] =
        useState<Order[]>([]);

    const [statusFilter, setStatusFilter] =
        useState("");

    const [page, setPage] =
        useState(0);

    const [totalPages, setTotalPages] =
        useState(0);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const [changingId, setChangingId] =
        useState<number | null>(null);

    useEffect(() => {
        loadOrders();
    }, [page, statusFilter]);

    async function loadOrders() {

        try {

            setLoading(true);
            setError("");

            const data =
                await getOrders(
                    page,
                    10,
                    statusFilter
                );

            setOrders(data.content);
            setTotalPages(data.totalPages);

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось загрузить заказы"
            );

        } finally {

            setLoading(false);
        }
    }

    async function handleStatusChange(
        orderId: number,
        status: OrderStatus
    ) {

        try {

            setChangingId(orderId);
            setError("");

            const updated =
                await changeOrderStatus(
                    orderId,
                    status
                );

            setOrders(currentOrders =>
                currentOrders.map(order =>
                    order.id === updated.id
                        ? updated
                        : order
                )
            );

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось изменить статус заказа"
            );

        } finally {

            setChangingId(null);
        }
    }

    return (
        <div>

            <div className="admin-header">

                <div>
                    <h1>
                        Заказы
                    </h1>

                    <p>
                        Управление заказами покупателей
                    </p>
                </div>

                <select
                    value={statusFilter}
                    onChange={event => {
                        setPage(0);

                        setStatusFilter(
                            event.target.value
                        );
                    }}
                >

                    <option value="">
                        Все статусы
                    </option>

                    <option value="CREATED">
                        Создан
                    </option>

                    <option value="CONFIRMED">
                        Подтверждён
                    </option>

                    <option value="COMPLETED">
                        Завершён
                    </option>

                    <option value="CANCELLED">
                        Отменён
                    </option>

                </select>

            </div>

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            {loading ? (

                <p>
                    Загрузка...
                </p>

            ) : (

                <div className="admin-table-wrapper">

                    <table className="admin-table">

                        <thead>

                        <tr>
                            <th>ID</th>
                            <th>Покупатель</th>
                            <th>Дата</th>
                            <th>Сумма</th>
                            <th>Статус</th>
                            <th>Действия</th>
                        </tr>

                        </thead>

                        <tbody>

                        {orders.map(order => (

                            <tr key={order.id}>

                                <td>
                                    <Link
                                        to={`/orders/${order.id}`}
                                    >
                                        #{order.id}
                                    </Link>
                                </td>

                                <td>
                                    {order.customerName}
                                </td>

                                <td>
                                    {new Date(
                                        order.createdAt
                                    ).toLocaleString(
                                        "ru-RU"
                                    )}
                                </td>

                                <td>
                                    {Number(
                                        order.totalPrice
                                    ).toLocaleString(
                                        "ru-RU"
                                    )} ₽
                                </td>

                                <td>

                                        <span
                                            className={
                                                `order-status status-${order.status.toLowerCase()}`
                                            }
                                        >
                                            {getStatusName(
                                                order.status
                                            )}
                                        </span>

                                </td>

                                <td>

                                    <div className="order-actions">

                                        {order.status === "CREATED" && (
                                            <>
                                                <button
                                                    disabled={
                                                        changingId ===
                                                        order.id
                                                    }
                                                    onClick={() =>
                                                        handleStatusChange(
                                                            order.id,
                                                            "CONFIRMED"
                                                        )
                                                    }
                                                >
                                                    Подтвердить
                                                </button>

                                                <button
                                                    className="danger-button"
                                                    disabled={
                                                        changingId ===
                                                        order.id
                                                    }
                                                    onClick={() =>
                                                        handleStatusChange(
                                                            order.id,
                                                            "CANCELLED"
                                                        )
                                                    }
                                                >
                                                    Отменить
                                                </button>
                                            </>
                                        )}

                                        {order.status === "CONFIRMED" && (
                                            <>
                                                <button
                                                    disabled={
                                                        changingId ===
                                                        order.id
                                                    }
                                                    onClick={() =>
                                                        handleStatusChange(
                                                            order.id,
                                                            "COMPLETED"
                                                        )
                                                    }
                                                >
                                                    Завершить
                                                </button>

                                                <button
                                                    className="danger-button"
                                                    disabled={
                                                        changingId ===
                                                        order.id
                                                    }
                                                    onClick={() =>
                                                        handleStatusChange(
                                                            order.id,
                                                            "CANCELLED"
                                                        )
                                                    }
                                                >
                                                    Отменить
                                                </button>
                                            </>
                                        )}

                                        {(order.status === "COMPLETED" ||
                                            order.status === "CANCELLED") && (

                                            <span className="no-actions">
                                                    —
                                                </span>

                                        )}

                                    </div>

                                </td>

                            </tr>

                        ))}

                        </tbody>

                    </table>

                </div>
            )}

            {!loading &&
                orders.length === 0 && (

                    <p>
                        Заказы не найдены
                    </p>
                )}

            {totalPages > 1 && (

                <div className="pagination">

                    <button
                        disabled={page === 0}
                        onClick={() =>
                            setPage(page - 1)
                        }
                    >
                        Назад
                    </button>

                    <span>
                        Страница {page + 1} из {totalPages}
                    </span>

                    <button
                        disabled={
                            page + 1 >= totalPages
                        }
                        onClick={() =>
                            setPage(page + 1)
                        }
                    >
                        Далее
                    </button>

                </div>
            )}

        </div>
    );
}

export default AdminOrdersPage;