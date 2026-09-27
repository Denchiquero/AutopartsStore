import { useEffect, useState } from "react";
import { getParts } from "../api/partsApi";
import type { Part } from "../types/api";

function CatalogPage() {

    const [parts, setParts] = useState<Part[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        getParts()
            .then(data => {
                setParts(data.content);
            })
            .catch(() => {
                setError("Не удалось загрузить каталог");
            })
            .finally(() => {
                setLoading(false);
            });

    }, []);

    if (loading) {
        return <p>Загрузка...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div>
            <h1>Каталог запчастей</h1>

            <div className="parts-grid">

                {parts.map(part => (
                    <div
                        className="part-card"
                        key={part.id}
                    >
                        <h3>{part.name}</h3>

                        <p>
                            {part.manufacturerName}
                        </p>

                        <p>
                            Артикул: {part.article}
                        </p>

                        <p>
                            SKU: {part.sku}
                        </p>

                        <strong>
                            {part.price} ₽
                        </strong>

                        <p>
                            На складе: {part.stockQuantity}
                        </p>
                    </div>
                ))}

            </div>
        </div>
    );
}

export default CatalogPage;