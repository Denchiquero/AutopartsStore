import {type FormEvent, useEffect, useState } from "react";

import { getParts } from "../api/partsApi";
import type { Part } from "../types/api";

import { Link } from "react-router-dom";

function CatalogPage() {
    const [parts, setParts] = useState<Part[]>([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [searchInput, setSearchInput] = useState("");
    const [search, setSearch] = useState("");

    const [sortBy, setSortBy] = useState("id");
    const [direction, setDirection] = useState("asc");

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        loadParts();
    }, [search, sortBy, direction, page]);

    async function loadParts() {
        try {
            setLoading(true);
            setError("");

            const data = await getParts({
                search,
                sortBy,
                direction,
                page,
                size: 6,
            });

            setParts(data.content);
            setTotalPages(data.totalPages);
        } catch (error) {
            console.error(error);
            setError("Не удалось загрузить каталог");
        } finally {
            setLoading(false);
        }
    }

    function handleSearch(event: FormEvent) {
        event.preventDefault();

        setPage(0);
        setSearch(searchInput);
    }

    function handleSortChange(
        event: React.ChangeEvent<HTMLSelectElement>
    ) {
        setPage(0);
        setSortBy(event.target.value);
    }

    function handleDirectionChange(
        event: React.ChangeEvent<HTMLSelectElement>
    ) {
        setPage(0);
        setDirection(event.target.value);
    }

    return (
        <div>
            <h1>Каталог запчастей</h1>

            <div className="catalog-controls">
                <form
                    className="search-form"
                    onSubmit={handleSearch}
                >
                    <input
                        type="text"
                        placeholder="Название, SKU или артикул"
                        value={searchInput}
                        onChange={(event) =>
                            setSearchInput(event.target.value)
                        }
                    />

                    <button type="submit">
                        Найти
                    </button>
                </form>

                <div className="sort-controls">
                    <select
                        value={sortBy}
                        onChange={handleSortChange}
                    >
                        <option value="id">
                            По умолчанию
                        </option>

                        <option value="name">
                            По названию
                        </option>

                        <option value="price">
                            По цене
                        </option>

                        <option value="manufacturer">
                            По производителю
                        </option>
                    </select>

                    <select
                        value={direction}
                        onChange={handleDirectionChange}
                    >
                        <option value="asc">
                            По возрастанию
                        </option>

                        <option value="desc">
                            По убыванию
                        </option>
                    </select>
                </div>
            </div>

            {loading && (
                <p>Загрузка...</p>
            )}

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            {!loading && !error && parts.length === 0 && (
                <p>Запчасти не найдены</p>
            )}

            {!loading && !error && (
                <div className="parts-grid">
                    {parts.map((part) => (
                        <Link
                            to={`/parts/${part.id}`}
                            className="part-card"
                            key={part.id}
                        >
                            <div className="part-category">
                                {part.categoryName}
                            </div>

                            <h3>
                                {part.name}
                            </h3>

                            <p>
                                Производитель:{" "}
                                <strong>
                                    {part.manufacturerName}
                                </strong>
                            </p>

                            <p>
                                Артикул: {part.article}
                            </p>

                            <p>
                                SKU: {part.sku}
                            </p>

                            <div className="part-card-bottom">
                                <span className="part-price">
                                    {Number(part.price)
                                        .toLocaleString("ru-RU")} ₽
                                </span>

                                <span>
                                    В наличии:{" "}
                                    {part.stockQuantity}
                                </span>
                            </div>
                        </Link>
                    ))}
                </div>
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
                        disabled={page + 1 >= totalPages}
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

export default CatalogPage;