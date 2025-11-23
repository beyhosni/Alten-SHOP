import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private readonly API_URL = 'http://localhost:8080/api/products';

    constructor(private http: HttpClient) { }

    getProducts(page?: number, size?: number): Observable<Product[]> {
        let params = new HttpParams();
        if (page !== undefined && size !== undefined) {
            params = params.set('page', page.toString()).set('size', size.toString());
        }
        return this.http.get<Product[]>(this.API_URL, { params });
    }

    getProductById(id: number): Observable<Product> {
        return this.http.get<Product>(`${this.API_URL}/${id}`);
    }

    getProductByCode(code: string): Observable<Product> {
        return this.http.get<Product>(`${this.API_URL}/code/${code}`);
    }

    getProductsByCategory(category: string): Observable<Product[]> {
        return this.http.get<Product[]>(`${this.API_URL}/category/${category}`);
    }

    getProductsByStatus(status: string): Observable<Product[]> {
        return this.http.get<Product[]>(`${this.API_URL}/status/${status}`);
    }

    // Admin only
    createProduct(product: Product): Observable<Product> {
        return this.http.post<Product>(this.API_URL, product);
    }

    // Admin only
    updateProduct(id: number, product: Product): Observable<Product> {
        return this.http.put<Product>(`${this.API_URL}/${id}`, product);
    }

    // Admin only
    deleteProduct(id: number): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
}
