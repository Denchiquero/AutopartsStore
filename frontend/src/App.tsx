import {
    Route,
    Routes
} from "react-router-dom";

import Header from "./components/Header";
import ProtectedRoute from "./components/ProtectedRoute";

import CatalogPage from "./pages/CatalogPage";
import VinPage from "./pages/VinPage";
import LoginPage from "./pages/LoginPage";
import OrdersPage from "./pages/OrdersPage";
import ProfilePage from "./pages/ProfilePage";
import PartPage from "./pages/PartPage";
import RegisterPage from "./pages/RegisterPage";
import CartPage from "./pages/CartPage";
import CheckoutPage from "./pages/CheckoutPage";
import OrderPage from "./pages/OrderPage";

import AdminRoute from "./components/AdminRoute";
import InventoryPage from "./pages/admin/InventoryPage";

function App() {

    return (
        <>
            <Header />

            <main className="container">

                <Routes>

                    <Route
                        path="/register"
                        element={<RegisterPage />}
                    />

                    <Route
                        path="/parts/:id"
                        element={<PartPage />}
                    />

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
                        element={
                            <ProtectedRoute>
                                <OrdersPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/profile"
                        element={
                            <ProtectedRoute>
                                <ProfilePage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/cart"
                        element={<CartPage />}
                    />

                    <Route
                        path="/checkout"
                        element={
                            <ProtectedRoute>
                                <CheckoutPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/orders/:id"
                        element={
                            <ProtectedRoute>
                                <OrderPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/admin/inventory"
                        element={
                            <AdminRoute>
                                <InventoryPage />
                            </AdminRoute>
                        }
                    />

                </Routes>

            </main>
        </>
    );
}

export default App;