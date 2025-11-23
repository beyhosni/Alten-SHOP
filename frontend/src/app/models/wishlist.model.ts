import { Product } from './product.model';

export interface WishlistItem {
    id?: number;
    product: Product;
    addedAt?: string;
}

export interface Wishlist {
    id?: number;
    items: WishlistItem[];
    createdAt?: string;
    updatedAt?: string;
}
