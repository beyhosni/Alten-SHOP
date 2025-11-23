import { Product } from './product.model';

export interface CartItem {
    id?: number;
    product: Product;
    quantity: number;
    addedAt?: string;
}

export interface Cart {
    id?: number;
    items: CartItem[];
    createdAt?: string;
    updatedAt?: string;
}

export interface AddToCartRequest {
    productId: number;
    quantity: number;
}
