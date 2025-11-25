import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap, Subject } from 'rxjs';
import { Cart, AddToCartRequest } from '../models/cart.model';

@Injectable({
    providedIn: 'root'
})
export class CartService {
    private readonly API_URL = 'http://localhost:8080/api/cart';

    public cart = signal<Cart | null>(null);
    cartItemCount = computed(() => {
        const currentCart = this.cart();
        return currentCart?.items.reduce((sum, item) => sum + item.quantity, 0) ?? 0;
    });

    // Subject to notify when cart items change (for product list refresh)
    public cartChanged$ = new Subject<void>();

    constructor(private http: HttpClient) { 
        this.getCart().subscribe();
    }

    getCart(): Observable<Cart> {
        return this.http.get<Cart>(this.API_URL).pipe(
            tap(cart => this.cart.set(cart))
        );
    }

    addToCart(request: AddToCartRequest): Observable<Cart> {
        return this.http.post<Cart>(`${this.API_URL}/items`, request).pipe(
            tap(cart => {
                this.cart.set(cart);
                this.cartChanged$.next();
            })
        );
    }

    updateQuantity(itemId: number, quantity: number): Observable<Cart> {
        const params = new HttpParams().set('quantity', quantity.toString());
        return this.http.put<Cart>(`${this.API_URL}/items/${itemId}`, null, { params }).pipe(
            tap(cart => {
                this.cart.set(cart);
                this.cartChanged$.next();
            })
        );
    }

    removeItem(itemId: number): Observable<Cart> {
        return this.http.delete<Cart>(`${this.API_URL}/items/${itemId}`).pipe(
            tap(cart => {
                this.cart.set(cart);
                this.cartChanged$.next();
            })
        );
    }

    clearCart(): Observable<void> {
        return this.http.delete<void>(this.API_URL).pipe(
            tap(() => {
                this.cart.set(null);
                this.cartChanged$.next();
            })
        );
    }

    refreshCart(): void {
        this.getCart().subscribe();
    }

    checkout(): Observable<string> {
        return this.http.post<string>(`${this.API_URL}/checkout`, {}).pipe(
            tap(() => {
                this.cart.set(null);
                this.cartChanged$.next();
            })
        );
    }
}
