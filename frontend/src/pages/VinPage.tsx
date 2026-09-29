import {
    type FormEvent,
    useState
} from "react";

import {
    Link
} from "react-router-dom";

import {
    decodeVin,
    getPartsByVin
} from "../api/vinApi";

import type {
    DecodedVin,
    Part
} from "../types/api";

function VinPage() {

    const [vin, setVin] =
        useState("");

    const [vehicle, setVehicle] =
        useState<DecodedVin | null>(null);

    const [parts, setParts] =
        useState<Part[]>([]);

    const [loading, setLoading] =
        useState(false);

    const [error, setError] =
        useState("");

    const [partsError, setPartsError] =
        useState("");

    async function handleSubmit(
        event: FormEvent
    ) {

        event.preventDefault();

        const normalizedVin =
            vin.trim().toUpperCase();

        if (normalizedVin.length !== 17) {

            setError(
                "VIN должен содержать 17 символов"
            );

            return;
        }

        try {

            setLoading(true);

            setError("");
            setPartsError("");

            setVehicle(null);
            setParts([]);

            const decoded =
                await decodeVin(
                    normalizedVin
                );

            setVehicle(decoded);

            /*
             * VIN может корректно декодироваться,
             * но в БД может не быть подходящей
             * VehicleApplication.
             */
            try {

                const compatibleParts =
                    await getPartsByVin(
                        normalizedVin
                    );

                setParts(
                    compatibleParts
                );

            } catch (error) {

                console.error(error);

                setPartsError(
                    "Для этого автомобиля совместимые запчасти пока не найдены"
                );
            }

        } catch (error) {

            console.error(error);

            setError(
                "Не удалось декодировать VIN"
            );

        } finally {

            setLoading(false);
        }
    }

    return (
        <div>

            <h1>
                Подбор запчастей по VIN
            </h1>

            <p className="vin-description">
                Введите 17-символьный VIN-код автомобиля.
            </p>

            <form
                className="vin-form"
                onSubmit={handleSubmit}
            >

                <input
                    value={vin}
                    maxLength={17}
                    placeholder="TYTCMR25ALFS12345"
                    onChange={event =>
                        setVin(
                            event.target.value
                                .toUpperCase()
                        )
                    }
                />

                <button
                    type="submit"
                    disabled={loading}
                >
                    {loading
                        ? "Поиск..."
                        : "Подобрать"}
                </button>

            </form>

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            {vehicle && (

                <div className="vehicle-card">

                    <div className="vehicle-title">

                        <div>
                            <span>
                                Автомобиль
                            </span>

                            <h2>
                                {vehicle.make}{" "}
                                {vehicle.model}
                            </h2>

                            <p>
                                {vehicle.generation},{" "}
                                {vehicle.modelYear}
                            </p>
                        </div>

                        <div className="vin-value">
                            VIN: {vehicle.vin}
                        </div>

                    </div>

                    <div className="vehicle-properties">

                        <div>
                            <span>
                                Двигатель
                            </span>

                            <strong>
                                {vehicle.engineCode}
                            </strong>
                        </div>

                        <div>
                            <span>
                                Объём
                            </span>

                            <strong>
                                {vehicle.engineVolume} л
                            </strong>
                        </div>

                        <div>
                            <span>
                                Мощность
                            </span>

                            <strong>
                                {vehicle.power} л.с.
                            </strong>
                        </div>

                        <div>
                            <span>
                                Топливо
                            </span>

                            <strong>
                                {vehicle.fuelType}
                            </strong>
                        </div>

                        <div>
                            <span>
                                КПП
                            </span>

                            <strong>
                                {vehicle.transmission}
                            </strong>
                        </div>

                        <div>
                            <span>
                                Привод
                            </span>

                            <strong>
                                {vehicle.driveType}
                            </strong>
                        </div>

                        <div>
                            <span>
                                Кузов
                            </span>

                            <strong>
                                {vehicle.bodyType}
                            </strong>
                        </div>

                    </div>

                </div>
            )}

            {vehicle && (

                <div className="vin-parts">

                    <h2>
                        Совместимые запчасти
                    </h2>

                    {partsError && (
                        <p>
                            {partsError}
                        </p>
                    )}

                    {parts.length > 0 && (

                        <div className="parts-grid">

                            {parts.map(part => (

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
                                        {part.manufacturerName}
                                    </p>

                                    <p>
                                        Артикул:{" "}
                                        {part.article}
                                    </p>

                                    <div className="part-card-bottom">

                                        <span className="part-price">

                                            {Number(
                                                part.price
                                            ).toLocaleString(
                                                "ru-RU"
                                            )} ₽

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

                </div>
            )}

        </div>
    );
}

export default VinPage;