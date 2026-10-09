import {
    type FormEvent,
    useEffect,
    useState
} from "react";

import {
    getParts
} from "../../api/partsApi";

import {
    createPart,
    deletePart,
    updatePart
} from "../../api/adminPartsApi";

import {
    getManufacturers
} from "../../api/manufacturersApi";

import {
    getCategories
} from "../../api/categoriesApi";

import type {
    Category,
    Manufacturer,
    Part,
    PartRequest
} from "../../types/api";

const emptyForm: PartRequest = {
    name: "",
    sku: "",
    article: "",
    description: "",
    price: 0,
    manufacturerId: 0,
    categoryId: 0
};

function AdminPartsPage() {

    const [parts, setParts] =
        useState<Part[]>([]);

    const [manufacturers, setManufacturers] =
        useState<Manufacturer[]>([]);

    const [categories, setCategories] =
        useState<Category[]>([]);

    const [form, setForm] =
        useState<PartRequest>(emptyForm);

    const [editingId, setEditingId] =
        useState<number | null>(null);

    const [formVisible, setFormVisible] =
        useState(false);

    const [loading, setLoading] =
        useState(true);

    const [saving, setSaving] =
        useState(false);

    const [error, setError] =
        useState("");

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {

        try {

            setLoading(true);

            const [
                partsData,
                manufacturersData,
                categoriesData
            ] = await Promise.all([
                getParts({
                    page: 0,
                    size: 100
                }),
                getManufacturers(),
                getCategories()
            ]);

            setParts(
                partsData.content
            );

            setManufacturers(
                manufacturersData
            );

            setCategories(
                categoriesData
            );

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось загрузить каталог"
            );

        } finally {

            setLoading(false);
        }
    }

    function openCreateForm() {

        setEditingId(null);

        setForm({
            ...emptyForm,
            manufacturerId:
                manufacturers[0]?.id ?? 0,
            categoryId:
                categories[0]?.id ?? 0
        });

        setFormVisible(true);
        setError("");
    }

    function openEditForm(
        part: Part
    ) {

        setEditingId(part.id);

        setForm({
            name: part.name,
            sku: part.sku,
            article: part.article,
            description: part.description,
            price: Number(part.price),
            manufacturerId:
            part.manufacturerId,
            categoryId:
            part.categoryId
        });

        setFormVisible(true);
        setError("");
    }

    function closeForm() {

        setFormVisible(false);
        setEditingId(null);
        setForm(emptyForm);
    }

    async function handleSubmit(
        event: FormEvent
    ) {

        event.preventDefault();

        try {

            setSaving(true);
            setError("");

            if (editingId === null) {

                const created =
                    await createPart(form);

                setParts(current => [
                    ...current,
                    created
                ]);

            } else {

                const updated =
                    await updatePart(
                        editingId,
                        form
                    );

                setParts(current =>
                    current.map(part =>
                        part.id === updated.id
                            ? updated
                            : part
                    )
                );
            }

            closeForm();

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось сохранить запчасть"
            );

        } finally {

            setSaving(false);
        }
    }

    async function handleDelete(
        part: Part
    ) {

        const confirmed =
            window.confirm(
                `Удалить "${part.name}"?`
            );

        if (!confirmed) {
            return;
        }

        try {

            setError("");

            await deletePart(
                part.id
            );

            setParts(current =>
                current.filter(
                    item =>
                        item.id !== part.id
                )
            );

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось удалить запчасть"
            );
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
                        Запчасти
                    </h1>

                    <p>
                        Управление каталогом
                    </p>
                </div>

                <button
                    className="admin-main-button"
                    onClick={openCreateForm}
                >
                    Добавить запчасть
                </button>

            </div>

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            {formVisible && (

                <form
                    className="admin-form"
                    onSubmit={handleSubmit}
                >

                    <h2>
                        {editingId === null
                            ? "Новая запчасть"
                            : "Редактирование"}
                    </h2>

                    <div className="admin-form-grid">

                        <label>
                            Название

                            <input
                                required
                                value={form.name}
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        name:
                                        event.target.value
                                    })
                                }
                            />
                        </label>

                        <label>
                            SKU

                            <input
                                required
                                value={form.sku}
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        sku:
                                        event.target.value
                                    })
                                }
                            />
                        </label>

                        <label>
                            Артикул

                            <input
                                required
                                value={form.article}
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        article:
                                        event.target.value
                                    })
                                }
                            />
                        </label>

                        <label>
                            Цена

                            <input
                                type="number"
                                min="0"
                                step="0.01"
                                required
                                value={form.price}
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        price:
                                            Number(
                                                event.target.value
                                            )
                                    })
                                }
                            />
                        </label>

                        <label>
                            Производитель

                            <select
                                required
                                value={
                                    form.manufacturerId
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        manufacturerId:
                                            Number(
                                                event.target.value
                                            )
                                    })
                                }
                            >

                                {manufacturers.map(
                                    manufacturer => (

                                        <option
                                            key={
                                                manufacturer.id
                                            }
                                            value={
                                                manufacturer.id
                                            }
                                        >
                                            {
                                                manufacturer.name
                                            }
                                        </option>

                                    )
                                )}

                            </select>

                        </label>

                        <label>
                            Категория

                            <select
                                required
                                value={
                                    form.categoryId
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        categoryId:
                                            Number(
                                                event.target.value
                                            )
                                    })
                                }
                            >

                                {categories.map(
                                    category => (

                                        <option
                                            key={
                                                category.id
                                            }
                                            value={
                                                category.id
                                            }
                                        >
                                            {
                                                category.name
                                            }
                                        </option>

                                    )
                                )}

                            </select>

                        </label>

                    </div>

                    <label className="admin-description">
                        Описание

                        <textarea
                            rows={4}
                            value={
                                form.description
                            }
                            onChange={event =>
                                setForm({
                                    ...form,
                                    description:
                                    event.target.value
                                })
                            }
                        />
                    </label>

                    <div className="admin-form-actions">

                        <button
                            type="submit"
                            disabled={saving}
                        >
                            {saving
                                ? "Сохранение..."
                                : "Сохранить"}
                        </button>

                        <button
                            type="button"
                            className="cancel-button"
                            onClick={closeForm}
                        >
                            Отмена
                        </button>

                    </div>

                </form>
            )}

            <div className="admin-table-wrapper">

                <table className="admin-table">

                    <thead>

                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                        <th>Производитель</th>
                        <th>Категория</th>
                        <th>Артикул</th>
                        <th>Цена</th>
                        <th>Остаток</th>
                        <th>Действия</th>
                    </tr>

                    </thead>

                    <tbody>

                    {parts.map(part => (

                        <tr key={part.id}>

                            <td>
                                {part.id}
                            </td>

                            <td>
                                {part.name}
                            </td>

                            <td>
                                {part.manufacturerName}
                            </td>

                            <td>
                                {part.categoryName}
                            </td>

                            <td>
                                {part.article}
                            </td>

                            <td>
                                {Number(
                                    part.price
                                ).toLocaleString(
                                    "ru-RU"
                                )} ₽
                            </td>

                            <td>
                                {part.stockQuantity}
                            </td>

                            <td>

                                <div className="admin-row-actions">

                                    <button
                                        onClick={() =>
                                            openEditForm(
                                                part
                                            )
                                        }
                                    >
                                        Изменить
                                    </button>

                                    <button
                                        className="danger-button"
                                        onClick={() =>
                                            handleDelete(
                                                part
                                            )
                                        }
                                    >
                                        Удалить
                                    </button>

                                </div>

                            </td>

                        </tr>

                    ))}

                    </tbody>

                </table>

            </div>

        </div>
    );
}

export default AdminPartsPage;