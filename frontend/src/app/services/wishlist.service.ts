import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Wishlist } from '../models/wishlist.model';

@Injectable({
    providedIn: 'root'
})
export class WishlistService {
    private readonly API_URL = 'http://localhost:8080/api/wishlist';

    private wishlist = signal<Wishlist | null>(null);
    wishlistItemCount = computed(() => this.wishlist()?.items.length ?? 0);

    constructor(private http: HttpClient) { }

    getWishlist(): Observable<Wishlist> {
        return this.http.get<Wishlist>(this.API_URL).pipe(
            tap(wishlist => this.wishlist.set(wishlist))
        );
    }

    addToWishlist(productId: number): Observable<Wishlist> {
        const params = new HttpParams().set('productId', productId.toString());
        return this.http.post<Wishlist>(`${this.API_URL}/items`, null, { params }).pipe(
            tap(wishlist => this.wishlist.set(wishlist))
        );
    }

    removeFromWishlist(itemId: number): Observable<Wishlist> {
        return this.http.delete<Wishlist>(`${this.API_URL}/items/${itemId}`).pipe(
            tap(wishlist => this.wishlist.set(wishlist))
        );
    }

    clearWishlist(): Observable<void> {
        return this.http.delete<void>(this.API_URL).pipe(
            tap(() => this.wishlist.set(null))
        );
    }

    refreshWishlist(): void {
        this.getWishlist().subscribe();
    }
}
