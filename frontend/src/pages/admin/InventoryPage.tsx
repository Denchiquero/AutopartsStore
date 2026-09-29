import {
    type FormEvent,
    useEffect,
    useState
} from "react";

import {
    getStock,
    receipt,
    writeOff
} from "../../api/inventoryApi";

import type {
    Stock
} from "../../types/api";

type Operation =
    | "receipt"
    | "writeOff";

function InventoryPage() {

    const [stock, setStock] =
        useState<Stock[]>([]);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const [activePartId, setActivePartId] =
        useState<number | null>(null);

    const [operation, setOperation] =
        useState<Operation | null>(null);

    const [quantity, setQuantity] =
        useState(1);

    const [comment, setComment] =
        useState("");

    const [submitting, setSubmitting] =
        useState(false);

    const [operationError, setOperationError] =
        useState("");

    useEffect(() => {
        loadStock();
    }, []);

    async function loadStock() {

        try {

            setLoading(true);
            setError("");

            const data =
                await getStock();

            setStock(data);

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось загрузить склад"
            );

        } finally {

            setLoading(false);
        }
    }

    function openOperation(
        partId: number,
        selectedOperation: Operation
    ) {

        setActivePartId(partId);
        setOperation(selectedOperation);

        setQuantity(1);
        setComment("");
        setOperationError("");
    }

    function closeOperation() {

        setActivePartId(null);
        setOperation(null);

        setQuantity(1);
        setComment("");
        setOperationError("");
    }

    async function handleOperation(
        event: FormEvent
    ) {

        event.preventDefault();

        if (
            activePartId === null ||
            operation === null
        ) {
            return;
        }

        if (quantity <= 0) {

            setOperationError(
                "Количество должно быть больше нуля"
            );

            return;
        }

        try {

            setSubmitting(true);
            setOperationError("");

            let updatedStock: Stock;

            if (operation === "receipt") {

                updatedStock =
                    await receipt(
                        activePartId,
                        {
                            quantity,
                            comment
                        }
                    );

            } else {

                updatedStock =
                    await writeOff(
                        activePartId,
                        {
                            quantity,
                            comment
                        }
                    );
            }

            setStock(currentStock =>
                currentStock.map(item =>
                    item.partId ===
                    updatedStock.partId
                        ? updatedStock
                        : item
                )
            );

            closeOperation();

        } catch (error) {

            console.error(error);

            setOperationError(
                operation === "receipt"
                    ? "Не удалось выполнить приход"
                    : "Не удалось выполнить списание"
            );

        } finally {

            setSubmitting(false);
        }
    }

    if (loading) {
        return <p>Загрузка...</p>;
    }

    return (
        <div>

            <div className="admin-header">

                <div>

                    <h1>
                        Склад
                    </h1>

                    <p>
                        Управление остатками запчастей
                    </p>

                </div>

            </div>

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            <div className="admin-table-wrapper">

                <table className="admin-table">

                    <thead>

                    <tr>
                        <th>ID</th>
                        <th>Запчасть</th>
                        <th>Производитель</th>
                        <th>Категория</th>
                        <th>SKU</th>
                        <th>Цена</th>
                        <th>Остаток</th>
                        <th>Действия</th>
                    </tr>

                    </thead>

                    <tbody>

                    {stock.map(item => (

                        <>
                            <tr key={item.partId}>

                                <td>
                                    {item.partId}
                                </td>

                                <td>
                                    {item.partName}
                                </td>

                                <td>
                                    {item.manufacturer}
                                </td>

                                <td>
                                    {item.category}
                                </td>

                                <td>
                                    {item.sku}
                                </td>

                                <td>
                                    {Number(
                                        item.price
                                    ).toLocaleString(
                                        "ru-RU"
                                    )} ₽
                                </td>

                                <td>
                                    <strong
                                        className={
                                            item.quantity === 0
                                                ? "stock-zero"
                                                : item.quantity <= 5
                                                    ? "stock-low"
                                                    : ""
                                        }
                                    >
                                        {item.quantity}
                                    </strong>
                                </td>

                                <td>

                                    <div className="inventory-actions">

                                        <button
                                            className="receipt-button"
                                            onClick={() =>
                                                openOperation(
                                                    item.partId,
                                                    "receipt"
                                                )
                                            }
                                        >
                                            Приход
                                        </button>

                                        <button
                                            className="writeoff-button"
                                            onClick={() =>
                                                openOperation(
                                                    item.partId,
                                                    "writeOff"
                                                )
                                            }
                                        >
                                            Списание
                                        </button>

                                    </div>

                                </td>

                            </tr>

                            {activePartId === item.partId && (

                                <tr
                                    key={
                                        `${item.partId}-operation`
                                    }
                                >

                                    <td
                                        colSpan={8}
                                        className="inventory-operation-cell"
                                    >

                                        <form
                                            className="inventory-operation-form"
                                            onSubmit={handleOperation}
                                        >

                                            <strong>

                                                {operation === "receipt"
                                                    ? "Приход товара"
                                                    : "Списание товара"}

                                            </strong>

                                            <input
                                                type="number"
                                                min="1"
                                                value={quantity}
                                                onChange={event =>
                                                    setQuantity(
                                                        Number(
                                                            event.target.value
                                                        )
                                                    )
                                                }
                                            />

                                            <input
                                                type="text"
                                                placeholder="Комментарий"
                                                value={comment}
                                                onChange={event =>
                                                    setComment(
                                                        event.target.value
                                                    )
                                                }
                                            />

                                            <button
                                                type="submit"
                                                disabled={submitting}
                                            >

                                                {submitting
                                                    ? "Сохранение..."
                                                    : "Выполнить"}

                                            </button>

                                            <button
                                                type="button"
                                                className="cancel-button"
                                                onClick={closeOperation}
                                            >
                                                Отмена
                                            </button>

                                            {operationError && (

                                                <span className="error">
                                                        {operationError}
                                                    </span>

                                            )}

                                        </form>

                                    </td>

                                </tr>
                            )}

                        </>

                    ))}

                    </tbody>

                </table>

            </div>

        </div>
    );
}

export default InventoryPage;