import {
    useEffect,
    useState
} from "react";

import {
    getParts
} from "../../api/partsApi";

import {
    getVehicles
} from "../../api/vehiclesApi";

import {
    addPartFitment,
    deletePartFitment,
    getPartFitments
} from "../../api/fitmentApi";

import type {
    Part,
    VehicleApplication
} from "../../types/api";

function vehicleName(
    vehicle: VehicleApplication
) {

    return `${vehicle.make} ${vehicle.model}
            ${vehicle.generation}
            ${vehicle.yearFrom}-${vehicle.yearTo},
            ${vehicle.engineCode}`;
}

function AdminFitmentsPage() {

    const [parts, setParts] =
        useState<Part[]>([]);

    const [vehicles, setVehicles] =
        useState<VehicleApplication[]>([]);

    const [fitments, setFitments] =
        useState<VehicleApplication[]>([]);

    const [partId, setPartId] =
        useState<number>(0);

    const [vehicleId, setVehicleId] =
        useState<number>(0);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    useEffect(() => {
        loadInitialData();
    }, []);

    useEffect(() => {

        if (partId === 0) {

            setFitments([]);

            return;
        }

        loadFitments();

    }, [partId]);

    async function loadInitialData() {

        try {

            const [
                partsData,
                vehicleData
            ] = await Promise.all([
                getParts({
                    page: 0,
                    size: 100
                }),
                getVehicles()
            ]);

            setParts(
                partsData.content
            );

            setVehicles(
                vehicleData
            );

            if (
                partsData.content.length > 0
            ) {
                setPartId(
                    partsData.content[0].id
                );
            }

            if (
                vehicleData.length > 0
            ) {
                setVehicleId(
                    vehicleData[0].id
                );
            }

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось загрузить данные"
            );

        } finally {

            setLoading(false);
        }
    }

    async function loadFitments() {

        try {

            setError("");

            const data =
                await getPartFitments(
                    partId
                );

            setFitments(data);

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось загрузить совместимость"
            );
        }
    }

    async function handleAdd() {

        if (
            partId === 0 ||
            vehicleId === 0
        ) {
            return;
        }

        try {

            setError("");

            await addPartFitment(
                partId,
                vehicleId
            );

            await loadFitments();

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось добавить совместимость"
            );
        }
    }

    async function handleDelete(
        currentVehicleId: number
    ) {

        try {

            setError("");

            await deletePartFitment(
                partId,
                currentVehicleId
            );

            setFitments(current =>
                current.filter(
                    vehicle =>
                        vehicle.id !==
                        currentVehicleId
                )
            );

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось удалить совместимость"
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
                    Совместимость
            </h1>

            <p>
            Связь запчастей с конфигурациями автомобилей
    </p>

    </div>

    </div>

    {error && (
        <p className="error">
            {error}
            </p>
    )}

    <div className="fitment-layout">

    <section className="fitment-panel">

        <h2>
            Запчасть
        </h2>
        <select
            value={partId}
            onChange={event =>
                setPartId(
                    Number(
                        event.target.value
                    )
                )
            }
        >

    {parts.map(part => (

        <option
            key={part.id}
        value={part.id}
            >
            {part.name}
        {" — "}
        {part.article}
        </option>

    ))}

    </select>

    <h2>
    Добавить автомобиль
    </h2>

    <select
    value={vehicleId}
    onChange={event =>
    setVehicleId(
        Number(
            event.target.value
        )
    )
}
>

    {vehicles.map(vehicle => (

        <option
            key={vehicle.id}
        value={vehicle.id}
            >
            {vehicleName(
                    vehicle
                )}
            </option>

    ))}

    </select>

    <button
    className="admin-main-button"
    onClick={handleAdd}
        >
        Добавить совместимость
    </button>

    </section>


    <section className="fitment-panel">

        <h2>
            Совместимые автомобили
    </h2>

    {fitments.length === 0 ? (

        <p>
            Для этой запчасти
        совместимость не задана
    </p>

    ) : (

        <div className="fitment-list">

            {fitments.map(vehicle => (

                    <div
                        className="fitment-item"
                key={vehicle.id}
                    >

                    <div>

                        <strong>
                            {vehicle.make}
        {" "}
        {vehicle.model}
        </strong>

        <span>
        {vehicle.generation}
        {" • "}
        {vehicle.yearFrom}
                                            –
                                            {vehicle.yearTo}
        </span>

        <span>
        {vehicle.engineCode}
        {" • "}
        {vehicle.engineVolume} л
        {" • "}
        {vehicle.power} л.с.
    </span>

    <span>
    {vehicle.transmission}
        {" • "}
        {vehicle.driveType}
        {" • "}
        {vehicle.bodyType}
        </span>

        </div>

        <button
        className="danger-button"
        onClick={() =>
        handleDelete(
            vehicle.id
        )
    }
    >
        Удалить
        </button>

        </div>

    ))}

        </div>
    )}

    </section>

    </div>

    </div>
);
}

export default AdminFitmentsPage;