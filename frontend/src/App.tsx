import {
    Route,
    Routes
} from "react-router-dom";

import Header from "./components/Header";

import CatalogPage from "./pages/CatalogPage";
import VinPage from "./pages/VinPage";
import LoginPage from "./pages/LoginPage";
import OrdersPage from "./pages/OrdersPage";
import ProfilePage from "./pages/ProfilePage";

function App() {

    return (
        <>
            <Header />

            <main className="container">

                <Routes>

                    <Route
                        path="/"
                        element={<CatalogPage />}
                    />

                    <Route
                        path="/vin"
                        element={<VinPage />}
                    />

                    <Route
                        path="/login"
                        element={<LoginPage />}
                    />

                    <Route
                        path="/orders"
                        element={<OrdersPage />}
                    />

                    <Route
                        path="/profile"
                        element={<ProfilePage />}
                    />

                </Routes>

            </main>
        </>
    );
}

export default App;