import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";

import type {
    CartItem,
    Part
} from "../types/api";

interface CartContextType {
    items: CartItem[];

    addToCart: (
        part: Part,
        quantity?: number
    ) => void;

    removeFromCart: (
        partId: number
    ) => void;

    changeQuantity: (
        partId: number,
        quantity: number
    ) => void;

    clearCart: () => void;

    totalPrice: number;
    totalItems: number;
}

const CartContext =
    createContext<CartContextType | undefined>(
        undefined
    );

export function CartProvider({
                                 children
                             }: {
    children: React.ReactNode;
}) {

    const [items, setItems] =
        useState<CartItem[]>(() => {

            const saved =
                localStorage.getItem("cart");

            if (!saved) {
                return [];
            }

            try {
                return JSON.parse(saved);
            } catch {
                return [];
            }
        });

    useEffect(() => {

        localStorage.setItem(
            "cart",
            JSON.stringify(items)
        );

    }, [items]);

    function addToCart(
        part: Part,
        quantity = 1
    ) {

        setItems(currentItems => {

            const existing =
                currentItems.find(
                    item =>
                        item.part.id === part.id
                );

            if (existing) {

                return currentItems.map(item => {

                    if (item.part.id !== part.id) {
                        return item;
                    }

                    const newQuantity =
                        Math.min(
                            item.quantity + quantity,
                            part.stockQuantity
                        );

                    return {
                        ...item,
                        quantity: newQuantity
                    };
                });
            }

            return [
                ...currentItems,
                {
                    part,
                    quantity: Math.min(
                        quantity,
                        part.stockQuantity
                    )
                }
            ];
        });
    }

    function removeFromCart(
        partId: number
    ) {

        setItems(currentItems =>
            currentItems.filter(
                item =>
                    item.part.id !== partId
            )
        );
    }

    function changeQuantity(
        partId: number,
        quantity: number
    ) {

        setItems(currentItems =>
            currentItems.map(item => {

                if (item.part.id !== partId) {
                    return item;
                }

                const newQuantity =
                    Math.max(
                        1,
                        Math.min(
                            quantity,
                            item.part.stockQuantity
                        )
                    );

                return {
                    ...item,
                    quantity: newQuantity
                };
            })
        );
    }

    function clearCart() {
        setItems([]);
    }

    const totalPrice =
        items.reduce(
            (sum, item) =>
                sum +
                Number(item.part.price)
                * item.quantity,
            0
        );

    const totalItems =
        items.reduce(
            (sum, item) =>
                sum + item.quantity,
            0
        );

    return (
        <CartContext.Provider
            value={{
                items,
                addToCart,
                removeFromCart,
                changeQuantity,
                clearCart,
                totalPrice,
                totalItems
            }}
        >
            {children}
        </CartContext.Provider>
    );
}

export function useCart() {

    const context =
        useContext(CartContext);

    if (!context) {
        throw new Error(
            "useCart must be used inside CartProvider"
        );
    }

    return context;
}