import {
    type FormEvent,
    useEffect,
    useState
} from "react";

import {
    createVehicle,
    deleteVehicle,
    getVehicles,
    updateVehicle
} from "../../api/vehiclesApi";

import type {
    VehicleApplication,
    VehicleApplicationRequest
} from "../../types/api";

const emptyForm: VehicleApplicationRequest = {
    make: "",
    model: "",
    generation: "",

    yearFrom: new Date().getFullYear(),
    yearTo: new Date().getFullYear(),

    engineCode: "",
    engineVolume: 0,
    power: 0,

    fuelType: "",
    transmission: "",
    driveType: "",
    bodyType: ""
};

function AdminVehiclesPage() {

    const [vehicles, setVehicles] =
        useState<VehicleApplication[]>([]);

    const [form, setForm] =
        useState<VehicleApplicationRequest>(
            emptyForm
        );

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
        loadVehicles();
    }, []);

    async function loadVehicles() {

        try {

            setLoading(true);
            setError("");

            const data =
                await getVehicles();

            setVehicles(data);

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось загрузить автомобили"
            );

        } finally {

            setLoading(false);
        }
    }

    function openCreateForm() {

        setEditingId(null);

        setForm({
            ...emptyForm
        });

        setFormVisible(true);
        setError("");
    }

    function openEditForm(
        vehicle: VehicleApplication
    ) {

        setEditingId(vehicle.id);

        setForm({
            make: vehicle.make,
            model: vehicle.model,
            generation: vehicle.generation,

            yearFrom: vehicle.yearFrom,
            yearTo: vehicle.yearTo,

            engineCode: vehicle.engineCode,
            engineVolume:
                Number(vehicle.engineVolume),
            power: vehicle.power,

            fuelType: vehicle.fuelType,
            transmission:
            vehicle.transmission,
            driveType: vehicle.driveType,
            bodyType: vehicle.bodyType
        });

        setFormVisible(true);
        setError("");
    }

    function closeForm() {

        setEditingId(null);

        setForm({
            ...emptyForm
        });

        setFormVisible(false);
    }

    async function handleSubmit(
        event: FormEvent
    ) {

        event.preventDefault();

        if (
            form.yearFrom >
            form.yearTo
        ) {

            setError(
                "Год начала выпуска не может быть больше года окончания"
            );

            return;
        }

        try {

            setSaving(true);
            setError("");

            if (editingId === null) {

                const created =
                    await createVehicle(
                        form
                    );

                setVehicles(current => [
                    ...current,
                    created
                ]);

            } else {

                const updated =
                    await updateVehicle(
                        editingId,
                        form
                    );

                setVehicles(current =>
                    current.map(vehicle =>
                        vehicle.id ===
                        updated.id
                            ? updated
                            : vehicle
                    )
                );
            }

            closeForm();

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось сохранить автомобиль"
            );

        } finally {

            setSaving(false);
        }
    }

    async function handleDelete(
        vehicle: VehicleApplication
    ) {

        const confirmed =
            window.confirm(
                `Удалить ${vehicle.make} ${vehicle.model} ${vehicle.generation}?`
            );

        if (!confirmed) {
            return;
        }

        try {

            setError("");

            await deleteVehicle(
                vehicle.id
            );

            setVehicles(current =>
                current.filter(
                    item =>
                        item.id !==
                        vehicle.id
                )
            );

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось удалить автомобиль. Возможно, он используется в совместимости запчастей."
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
                        Автомобили
                    </h1>

                    <p>
                        Управление конфигурациями автомобилей
                    </p>
                </div>

                <button
                    className="admin-main-button"
                    onClick={openCreateForm}
                >
                    Добавить автомобиль
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
                            ? "Новая конфигурация"
                            : "Редактирование конфигурации"}
                    </h2>

                    <div className="admin-form-grid">

                        <label>
                            Марка

                            <input
                                required
                                value={form.make}
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        make:
                                        event.target.value
                                    })
                                }
                            />
                        </label>

                        <label>
                            Модель

                            <input
                                required
                                value={form.model}
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        model:
                                        event.target.value
                                    })
                                }
                            />
                        </label>

                        <label>
                            Поколение

                            <input
                                required
                                value={
                                    form.generation
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        generation:
                                        event.target.value
                                    })
                                }
                            />
                        </label>

                        <label>
                            Код двигателя

                            <input
                                required
                                value={
                                    form.engineCode
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        engineCode:
                                        event.target.value
                                    })
                                }
                            />
                        </label>

                        <label>
                            Год от

                            <input
                                type="number"
                                required
                                value={
                                    form.yearFrom
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        yearFrom:
                                            Number(
                                                event.target.value
                                            )
                                    })
                                }
                            />
                        </label>

                        <label>
                            Год до

                            <input
                                type="number"
                                required
                                value={
                                    form.yearTo
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        yearTo:
                                            Number(
                                                event.target.value
                                            )
                                    })
                                }
                            />
                        </label>

                        <label>
                            Объём двигателя

                            <input
                                type="number"
                                min="0"
                                step="0.1"
                                required
                                value={
                                    form.engineVolume
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        engineVolume:
                                            Number(
                                                event.target.value
                                            )
                                    })
                                }
                            />
                        </label>

                        <label>
                            Мощность, л.с.

                            <input
                                type="number"
                                min="0"
                                required
                                value={
                                    form.power
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        power:
                                            Number(
                                                event.target.value
                                            )
                                    })
                                }
                            />
                        </label>

                        <label>
                            Топливо

                            <select
                                required
                                value={
                                    form.fuelType
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        fuelType:
                                        event.target.value
                                    })
                                }
                            >

                                <option value="">
                                    Выберите
                                </option>

                                <option value="GASOLINE">
                                    Бензин
                                </option>

                                <option value="DIESEL">
                                    Дизель
                                </option>

                                <option value="HYBRID">
                                    Гибрид
                                </option>

                                <option value="ELECTRIC">
                                    Электро
                                </option>

                            </select>

                        </label>

                        <label>
                            Коробка передач

                            <select
                                required
                                value={
                                    form.transmission
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        transmission:
                                        event.target.value
                                    })
                                }
                            >

                                <option value="">
                                    Выберите
                                </option>

                                <option value="MANUAL">
                                    Механика
                                </option>

                                <option value="AUTOMATIC">
                                    Автомат
                                </option>

                                <option value="ROBOT">
                                    Робот
                                </option>

                                <option value="CVT">
                                    Вариатор
                                </option>

                            </select>

                        </label>

                        <label>
                            Привод

                            <select
                                required
                                value={
                                    form.driveType
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        driveType:
                                        event.target.value
                                    })
                                }
                            >

                                <option value="">
                                    Выберите
                                </option>

                                <option value="FWD">
                                    Передний
                                </option>

                                <option value="RWD">
                                    Задний
                                </option>

                                <option value="AWD">
                                    Полный
                                </option>

                            </select>

                        </label>

                        <label>
                            Кузов

                            <select
                                required
                                value={
                                    form.bodyType
                                }
                                onChange={event =>
                                    setForm({
                                        ...form,
                                        bodyType:
                                        event.target.value
                                    })
                                }
                            >

                                <option value="">
                                    Выберите
                                </option>

                                <option value="SEDAN">
                                    Седан
                                </option>

                                <option value="HATCHBACK">
                                    Хэтчбек
                                </option>

                                <option value="WAGON">
                                    Универсал
                                </option>

                                <option value="SUV">
                                    Кроссовер / SUV
                                </option>

                                <option value="COUPE">
                                    Купе
                                </option>

                                <option value="VAN">
                                    Фургон / минивэн
                                </option>

                            </select>

                        </label>

                    </div>

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
                        <th>Автомобиль</th>
                        <th>Поколение</th>
                        <th>Годы</th>
                        <th>Двигатель</th>
                        <th>КПП</th>
                        <th>Привод</th>
                        <th>Кузов</th>
                        <th>Действия</th>
                    </tr>

                    </thead>

                    <tbody>

                    {vehicles.map(vehicle => (

                        <tr key={vehicle.id}>

                            <td>
                                {vehicle.id}
                            </td>

                            <td>
                                <strong>
                                    {vehicle.make}
                                    {" "}
                                    {vehicle.model}
                                </strong>
                            </td>

                            <td>
                                {vehicle.generation}
                            </td>

                            <td>
                                {vehicle.yearFrom}
                                {"–"}
                                {vehicle.yearTo}
                            </td>

                            <td>
                                {vehicle.engineCode}
                                <br />

                                <span className="table-secondary">
                                        {vehicle.engineVolume} л
                                    {" / "}
                                    {vehicle.power} л.с.
                                    </span>
                            </td>

                            <td>
                                {vehicle.transmission}
                            </td>

                            <td>
                                {vehicle.driveType}
                            </td>

                            <td>
                                {vehicle.bodyType}
                            </td>

                            <td>

                                <div className="admin-row-actions">

                                    <button
                                        onClick={() =>
                                            openEditForm(
                                                vehicle
                                            )
                                        }
                                    >
                                        Изменить
                                    </button>

                                    <button
                                        className="danger-button"
                                        onClick={() =>
                                            handleDelete(
                                                vehicle
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

export default AdminVehiclesPage;