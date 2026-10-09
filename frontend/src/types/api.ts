export interface PageResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}

export interface Part {
    id: number;
    name: string;
    sku: string;
    article: string;
    description: string;
    price: number;

    manufacturerId: number;
    manufacturerName: string;

    categoryId: number;
    categoryName: string;

    stockQuantity: number;
}

export type UserRole = "USER" | "ADMIN";

export interface LoginRequest {
    email: string;
    password: string;
}

export interface LoginResponse {
    userId: number;
    email: string;
    role: UserRole;
    customerId: number | null;
    token: string;
}

export interface RegisterRequest {
    name: string;
    phone: string;
    email: string;
    password: string;
}

export interface RegisterResponse {
    userId: number;
    email: string;
    role: UserRole;
    customerId: number;
    customerName: string;
}

export interface CurrentUser {
    userId: number;
    email: string;
    role: UserRole;
    customerId: number | null;
}

export interface CartItem {
    part: Part;
    quantity: number;
}

export interface CreateOrderItemRequest {
    partId: number;
    quantity: number;
}

export interface CreateOrderRequest {
    items: CreateOrderItemRequest[];
}

export type OrderStatus =
    | "CREATED"
    | "CONFIRMED"
    | "CANCELLED"
    | "COMPLETED";

export interface OrderItem {
    partId: number;
    sku: string;
    partName: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
}

export interface Order {
    id: number;

    customerId: number;
    customerName: string;

    createdAt: string;

    status: OrderStatus;

    totalPrice: number;

    items: OrderItem[];
}

export interface Profile {
    userId: number;
    email: string;
    role: UserRole;
    customerId: number | null;
    name: string;
    phone: string;
}

export interface UpdateProfileRequest {
    name: string;
    phone: string;
    email: string;
}

export interface DecodedVin {
    vehicleId: number | null;

    vin: string;
    make: string;
    model: string;
    generation: string;

    modelYear: number;

    engineCode: string;
    engineVolume: number;
    power: number;

    fuelType: string;
    transmission: string;
    driveType: string;
    bodyType: string;
}

export interface Stock {
    partId: number;
    sku: string;
    partName: string;
    manufacturer: string;
    category: string;
    price: number;
    quantity: number;
}

export interface Manufacturer {
    id: number;
    name: string;
    country: string;
    website: string;
}

export interface Category {
    id: number;
    name: string;
    description: string;
}

export interface PartRequest {
    name: string;
    sku: string;
    article: string;
    description: string;
    price: number;
    manufacturerId: number;
    categoryId: number;
}

export interface ManufacturerRequest {
    name: string;
    country: string;
    website: string;
}

export interface CategoryRequest {
    name: string;
    description: string;
}

export interface VehicleApplication {
    id: number;

    make: string;
    model: string;
    generation: string;

    yearFrom: number;
    yearTo: number;

    engineCode: string;
    engineVolume: number;
    power: number;

    fuelType: string;
    transmission: string;
    driveType: string;
    bodyType: string;
}

export interface VehicleApplicationRequest {
    make: string;
    model: string;
    generation: string;

    yearFrom: number;
    yearTo: number;

    engineCode: string;
    engineVolume: number;
    power: number;

    fuelType: string;
    transmission: string;
    driveType: string;
    bodyType: string;
}

export type MovementType =
    | "RECEIPT"
    | "WRITE_OFF"
    | "ORDER"
    | "RETURN";

export interface InventoryMovement {
    id: number;

    partId: number;
    sku: string;
    partName: string;

    type: MovementType;
    quantity: number;

    createdAt: string;

    orderId: number | null;
    comment: string | null;
}