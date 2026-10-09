import {
    useEffect,
    useState
} from "react";

import {
    Link
} from "react-router-dom";

import {
    getMovements
} from "../../api/inventoryApi";

import type {
    InventoryMovement,
    MovementType
} from "../../types/api";

function getMovementName(
    type: MovementType
) {

    switch (type) {

        case "RECEIPT":
            return "Приход";

        case "WRITE_OFF":
            return "Списание";

        case "ORDER":
            return "Заказ";

        case "RETURN":
            return "Возврат";
    }
}

function AdminMovementsPage() {

    const [movements, setMovements] =
        useState<InventoryMovement[]>([]);

    const [page, setPage] =
        useState(0);

    const [totalPages, setTotalPages] =
        useState(0);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    useEffect(() => {

        async function load() {

            try {

                setLoading(true);
                setError("");

                const data =
                    await getMovements(
                        page,
                        20
                    );

                setMovements(
                    data.content
                );

                setTotalPages(
                    data.totalPages
                );

            } catch (error) {

                console.error(error);

                setError(
                    "Не удалось загрузить историю склада"
                );

            } finally {

                setLoading(false);
            }
        }

        load();

    }, [page]);

    return (
        <div>

            <div className="admin-header">

                <div>
                    <h1>
                        Движения склада
                    </h1>

                    <p>
                        История прихода, списания,
                        заказов и возвратов
                    </p>
                </div>

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
                            <th>Дата</th>
                            <th>Запчасть</th>
                            <th>SKU</th>
                            <th>Операция</th>
                            <th>Количество</th>
                            <th>Заказ</th>
                            <th>Комментарий</th>
                        </tr>

                        </thead>

                        <tbody>

                        {movements.map(
                            movement => (

                                <tr
                                    key={movement.id}
                                >

                                    <td>
                                        {new Date(
                                            movement.createdAt
                                        ).toLocaleString(
                                            "ru-RU"
                                        )}
                                    </td>

                                    <td>

                                        <Link
                                            to={
                                                `/parts/${movement.partId}`
                                            }
                                        >
                                            {
                                                movement.partName
                                            }
                                        </Link>

                                    </td>

                                    <td>
                                        {movement.sku}
                                    </td>

                                    <td>

                                        <span
                                            className={
                                                `movement movement-${movement.type.toLowerCase()}`
                                            }
                                        >
                                            {getMovementName(
                                                movement.type
                                            )}
                                        </span>

                                    </td>

                                    <td>
                                        {movement.quantity}
                                    </td>

                                    <td>

                                        {movement.orderId
                                            ? (
                                                <Link
                                                    to={
                                                        `/orders/${movement.orderId}`
                                                    }
                                                >
                                                    #{movement.orderId}
                                                </Link>
                                            )
                                            : "—"}

                                    </td>

                                    <td>
                                        {movement.comment || "—"}
                                    </td>

                                </tr>

                            ))}

                        </tbody>

                    </table>

                </div>
            )}

            {!loading &&
                movements.length === 0 && (

                    <p>
                        Движений пока нет
                    </p>
                )}

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
                        Страница {page + 1}
                        {" из "}
                        {totalPages}
                    </span>

                    <button
                        disabled={
                            page + 1 >= totalPages
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

export default AdminMovementsPage;