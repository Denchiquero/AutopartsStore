import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { getPart } from "../api/partsApi";
import type { Part } from "../types/api";
import { useCart } from "../cart/CartContext";

function PartPage() {
    const { id } = useParams();
    const { addToCart } = useCart();
    const [part, setPart] = useState<Part | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        if (!id) {
            return;
        }

        getPart(Number(id))
            .then((data) => {
                setPart(data);
            })
            .catch((error) => {
                console.error(error);
                setError("Запчасть не найдена");
            })
            .finally(() => {
                setLoading(false);
            });

    }, [id]);

    if (loading) {
        return <p>Загрузка...</p>;
    }

    if (error || !part) {
        return (
            <div>
                <p>{error}</p>

                <Link to="/">
                    Вернуться в каталог
                </Link>
            </div>
        );
    }

    return (
        <div className="part-page">
            <Link
                className="back-link"
                to="/"
            >
                ← Назад в каталог
            </Link>

            <div className="part-details">
                <div>
                    <span className="part-category">
                        {part.categoryName}
                    </span>

                    <h1>
                        {part.name}
                    </h1>

                    <p className="part-description">
                        {part.description}
                    </p>

                    <div className="part-info">
                        <p>
                            <strong>Производитель:</strong>{" "}
                            {part.manufacturerName}
                        </p>

                        <p>
                            <strong>Артикул:</strong>{" "}
                            {part.article}
                        </p>

                        <p>
                            <strong>SKU:</strong>{" "}
                            {part.sku}
                        </p>

                        <p>
                            <strong>На складе:</strong>{" "}
                            {part.stockQuantity}
                        </p>
                    </div>
                </div>

                <div className="part-buy">
                    <div className="part-page-price">
                        {Number(part.price)
                            .toLocaleString("ru-RU")} ₽
                    </div>

                    {part.stockQuantity > 0 ? (
                        <button
                            onClick={() =>
                                addToCart(part)
                            }
                        >
                            Добавить в корзину
                        </button>
                    ) : (
                        <button disabled>
                            Нет в наличии
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default PartPage;