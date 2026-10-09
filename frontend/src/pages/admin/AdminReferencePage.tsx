import {
    type FormEvent,
    useEffect,
    useState
} from "react";

import {
    createManufacturer,
    deleteManufacturer,
    getManufacturers,
    updateManufacturer
} from "../../api/manufacturersApi";

import {
    createCategory,
    deleteCategory,
    getCategories,
    updateCategory
} from "../../api/categoriesApi";

import type {
    Category,
    Manufacturer
} from "../../types/api";

function AdminReferencePage() {

    const [manufacturers, setManufacturers] =
        useState<Manufacturer[]>([]);

    const [categories, setCategories] =
        useState<Category[]>([]);

    const [manufacturerId, setManufacturerId] =
        useState<number | null>(null);

    const [manufacturerName, setManufacturerName] =
        useState("");

    const [country, setCountry] =
        useState("");

    const [website, setWebsite] =
        useState("");

    const [categoryId, setCategoryId] =
        useState<number | null>(null);

    const [categoryName, setCategoryName] =
        useState("");

    const [categoryDescription, setCategoryDescription] =
        useState("");

    const [error, setError] =
        useState("");

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {

        try {

            const [
                manufacturerData,
                categoryData
            ] = await Promise.all([
                getManufacturers(),
                getCategories()
            ]);

            setManufacturers(
                manufacturerData
            );

            setCategories(
                categoryData
            );

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось загрузить справочники"
            );
        }
    }

    async function handleManufacturerSubmit(
        event: FormEvent
    ) {

        event.preventDefault();

        try {

            setError("");

            const request = {
                name: manufacturerName,
                country,
                website
            };

            if (manufacturerId === null) {

                const created =
                    await createManufacturer(
                        request
                    );

                setManufacturers(current => [
                    ...current,
                    created
                ]);

            } else {

                const updated =
                    await updateManufacturer(
                        manufacturerId,
                        request
                    );

                setManufacturers(current =>
                    current.map(item =>
                        item.id === updated.id
                            ? updated
                            : item
                    )
                );
            }

            clearManufacturerForm();

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось сохранить производителя"
            );
        }
    }

    function editManufacturer(
        manufacturer: Manufacturer
    ) {

        setManufacturerId(
            manufacturer.id
        );

        setManufacturerName(
            manufacturer.name
        );

        setCountry(
            manufacturer.country
        );

        setWebsite(
            manufacturer.website
        );
    }

    function clearManufacturerForm() {

        setManufacturerId(null);
        setManufacturerName("");
        setCountry("");
        setWebsite("");
    }

    async function handleDeleteManufacturer(
        manufacturer: Manufacturer
    ) {

        if (
            !window.confirm(
                `Удалить производителя "${manufacturer.name}"?`
            )
        ) {
            return;
        }

        try {

            await deleteManufacturer(
                manufacturer.id
            );

            setManufacturers(current =>
                current.filter(
                    item =>
                        item.id !==
                        manufacturer.id
                )
            );

        } catch (error) {

            console.error(error);

            setError(
                "Нельзя удалить производителя. Возможно, он используется запчастями."
            );
        }
    }

    async function handleCategorySubmit(
        event: FormEvent
    ) {

        event.preventDefault();

        try {

            setError("");

            const request = {
                name: categoryName,
                description:
                categoryDescription
            };

            if (categoryId === null) {

                const created =
                    await createCategory(
                        request
                    );

                setCategories(current => [
                    ...current,
                    created
                ]);

            } else {

                const updated =
                    await updateCategory(
                        categoryId,
                        request
                    );

                setCategories(current =>
                    current.map(item =>
                        item.id === updated.id
                            ? updated
                            : item
                    )
                );
            }

            clearCategoryForm();

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось сохранить категорию"
            );
        }
    }

    function editCategory(
        category: Category
    ) {

        setCategoryId(
            category.id
        );

        setCategoryName(
            category.name
        );

        setCategoryDescription(
            category.description
        );
    }

    function clearCategoryForm() {

        setCategoryId(null);
        setCategoryName("");
        setCategoryDescription("");
    }

    async function handleDeleteCategory(
        category: Category
    ) {

        if (
            !window.confirm(
                `Удалить категорию "${category.name}"?`
            )
        ) {
            return;
        }

        try {

            await deleteCategory(
                category.id
            );

            setCategories(current =>
                current.filter(
                    item =>
                        item.id !==
                        category.id
                )
            );

        } catch (error) {

            console.error(error);

            setError(
                "Нельзя удалить категорию. Возможно, она используется запчастями."
            );
        }
    }

    return (
        <div>

            <h1>
                Справочники
            </h1>

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            <div className="reference-grid">

                <section>

                    <h2>
                        Производители
                    </h2>

                    <form
                        className="small-admin-form"
                        onSubmit={
                            handleManufacturerSubmit
                        }
                    >

                        <input
                            placeholder="Название"
                            required
                            value={manufacturerName}
                            onChange={event =>
                                setManufacturerName(
                                    event.target.value
                                )
                            }
                        />

                        <input
                            placeholder="Страна"
                            value={country}
                            onChange={event =>
                                setCountry(
                                    event.target.value
                                )
                            }
                        />

                        <input
                            placeholder="Сайт"
                            value={website}
                            onChange={event =>
                                setWebsite(
                                    event.target.value
                                )
                            }
                        />

                        <div>
                            <button type="submit">
                                {manufacturerId === null
                                    ? "Добавить"
                                    : "Сохранить"}
                            </button>

                            {manufacturerId !== null && (
                                <button
                                    type="button"
                                    className="cancel-button"
                                    onClick={
                                        clearManufacturerForm
                                    }
                                >
                                    Отмена
                                </button>
                            )}
                        </div>

                    </form>

                    <div className="reference-list">

                        {manufacturers.map(
                            manufacturer => (

                                <div
                                    className="reference-item"
                                    key={manufacturer.id}
                                >

                                    <div>
                                        <strong>
                                            {manufacturer.name}
                                        </strong>

                                        <span>
                                            {manufacturer.country}
                                        </span>
                                    </div>

                                    <div className="admin-row-actions">

                                        <button
                                            onClick={() =>
                                                editManufacturer(
                                                    manufacturer
                                                )
                                            }
                                        >
                                            Изменить
                                        </button>

                                        <button
                                            className="danger-button"
                                            onClick={() =>
                                                handleDeleteManufacturer(
                                                    manufacturer
                                                )
                                            }
                                        >
                                            Удалить
                                        </button>

                                    </div>

                                </div>
                            )
                        )}

                    </div>

                </section>


                <section>

                    <h2>
                        Категории
                    </h2>

                    <form
                        className="small-admin-form"
                        onSubmit={
                            handleCategorySubmit
                        }
                    >

                        <input
                            placeholder="Название"
                            required
                            value={categoryName}
                            onChange={event =>
                                setCategoryName(
                                    event.target.value
                                )
                            }
                        />

                        <textarea
                            placeholder="Описание"
                            rows={3}
                            value={
                                categoryDescription
                            }
                            onChange={event =>
                                setCategoryDescription(
                                    event.target.value
                                )
                            }
                        />

                        <div>
                            <button type="submit">
                                {categoryId === null
                                    ? "Добавить"
                                    : "Сохранить"}
                            </button>

                            {categoryId !== null && (
                                <button
                                    type="button"
                                    className="cancel-button"
                                    onClick={
                                        clearCategoryForm
                                    }
                                >
                                    Отмена
                                </button>
                            )}
                        </div>

                    </form>

                    <div className="reference-list">

                        {categories.map(
                            category => (

                                <div
                                    className="reference-item"
                                    key={category.id}
                                >

                                    <div>
                                        <strong>
                                            {category.name}
                                        </strong>

                                        <span>
                                            {
                                                category.description
                                            }
                                        </span>
                                    </div>

                                    <div className="admin-row-actions">

                                        <button
                                            onClick={() =>
                                                editCategory(
                                                    category
                                                )
                                            }
                                        >
                                            Изменить
                                        </button>

                                        <button
                                            className="danger-button"
                                            onClick={() =>
                                                handleDeleteCategory(
                                                    category
                                                )
                                            }
                                        >
                                            Удалить
                                        </button>

                                    </div>

                                </div>
                            )
                        )}

                    </div>

                </section>

            </div>

        </div>
    );
}

export default AdminReferencePage;