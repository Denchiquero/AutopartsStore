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
import AdminOrdersPage from "./pages/admin/AdminOrdersPage";
import AdminPartsPage from "./pages/admin/AdminPartsPage";
import AdminReferencePage from "./pages/admin/AdminReferencePage";
import AdminFitmentsPage from "./pages/admin/AdminFitmentsPage";
import AdminVehiclesPage
    from "./pages/admin/AdminVehiclesPage";
import AdminMovementsPage
    from "./pages/admin/AdminMovementsPage";


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

                    <Route
                        path="/admin/orders"
                        element={
                            <AdminRoute>
                                <AdminOrdersPage />
                            </AdminRoute>
                        }
                    />

                    <Route
                        path="/admin/parts"
                        element={
                            <AdminRoute>
                                <AdminPartsPage />
                            </AdminRoute>
                        }
                    />

                    <Route
                        path="/admin/reference"
                        element={
                            <AdminRoute>
                                <AdminReferencePage />
                            </AdminRoute>
                        }
                    />

                    <Route
                        path="/admin/reference"
                        element={
                            <AdminRoute>
                                <AdminReferencePage />
                            </AdminRoute>
                        }
                    />

                    <Route
                        path="/admin/fitments"
                        element={
                            <AdminRoute>
                                <AdminFitmentsPage />
                            </AdminRoute>
                        }
                    />

                    <Route
                        path="/admin/vehicles"
                        element={
                            <AdminRoute>
                                <AdminVehiclesPage />
                            </AdminRoute>
                        }
                    />

                    <Route
                        path="/admin/movements"
                        element={
                            <AdminRoute>
                                <AdminMovementsPage />
                            </AdminRoute>
                        }
                    />

                </Routes>

            </main>
        </>
    );
}

export default App;