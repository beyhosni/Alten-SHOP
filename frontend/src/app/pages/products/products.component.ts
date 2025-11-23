import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { PaginatorModule } from 'primeng/paginator';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DataViewModule } from 'primeng/dataview';
import { RatingModule } from 'primeng/rating';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, PaginatorModule, ButtonModule, TagModule, DataViewModule, RatingModule, FormsModule, ReactiveFormsModule, DropdownModule, InputNumberModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit {
  products = signal<Product[]>([]);
  totalRecords = 0;
  rows = 10;
  first = 0;

  // Filter properties
  categories = signal<{ label: string; value: string }[]>([]);
  selectedCategory = signal<string | null>(null);
  minPrice = signal<number | null>(null);
  maxPrice = signal<number | null>(null);
  searchText = signal<string>('');

  constructor(
    private productService: ProductService,
    private cartService: CartService
  ) { }

  ngOnInit() {
    this.loadCategories();
    this.loadProducts(0, this.rows);
    
    // Listen for cart changes to refresh products
    this.cartService.cartChanged$.subscribe(() => {
      this.loadProducts(this.first / this.rows, this.rows);
    });
  }

  loadCategories() {
    // Get unique categories from products
    this.productService.getProducts(0, 100).subscribe({
      next: (data: any) => {
        const productList = data.content || data;
        const uniqueCategories = [...new Set(productList.map((p: Product) => p.category))] as string[];
        this.categories.set([
          { label: 'All Categories', value: '' },
          ...uniqueCategories.map((cat: string) => ({ label: cat, value: cat }))
        ]);
      },
      error: (err) => console.error('Error loading categories', err)
    });
  }

  loadProducts(page: number, size: number) {
    this.productService.getProducts(page, size).subscribe({
      next: (data: any) => {
        let productList = data.content || data;

        // Apply client-side filters
        if (this.selectedCategory()) {
          productList = productList.filter((p: Product) => p.category === this.selectedCategory());
        }

        if (this.minPrice() !== null) {
          productList = productList.filter((p: Product) => p.price >= this.minPrice()!);
        }

        if (this.maxPrice() !== null) {
          productList = productList.filter((p: Product) => p.price <= this.maxPrice()!);
        }

        if (this.searchText()) {
          const search = this.searchText().toLowerCase();
          productList = productList.filter((p: Product) => 
            p.name.toLowerCase().includes(search) || 
            p.description.toLowerCase().includes(search)
          );
        }

        this.products.set(productList);
        this.totalRecords = productList.length;
      },
      error: (err) => console.error('Error loading products', err)
    });
  }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
    this.loadProducts(event.page, event.rows);
  }

  onFilterChange() {
    this.first = 0; // Reset to first page when filtering
    this.loadProducts(0, this.rows);
  }

  resetFilters() {
    this.selectedCategory.set(null);
    this.minPrice.set(null);
    this.maxPrice.set(null);
    this.searchText.set('');
    this.first = 0;
    this.loadProducts(0, this.rows);
  }

  addToCart(product: Product) {
    if (product.id) {
      this.cartService.addToCart({ productId: product.id, quantity: 1 }).subscribe({
        next: () => {
          console.log('Added to cart');
          // Product quantity will be updated automatically via cartChanged$ subscription
        },
        error: (err) => console.error('Error adding to cart', err)
      });
    }
  }

  getSeverity(status: string): 'success' | 'secondary' | 'info' | 'warning' | 'danger' | 'contrast' | undefined {
    switch (status) {
      case 'INSTOCK':
        return 'success';
      case 'LOWSTOCK':
        return 'warning';
      case 'OUTOFSTOCK':
        return 'danger';
      default:
        return 'info';
    }
  }
}
