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
  allProducts: Product[] = []; // Store all products for pagination/filtering
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
    this.loadAllProducts();
  }

  loadAllProducts() {
    // Load all products once
    this.productService.getProducts(0, 100).subscribe({
      next: (data: any) => {
        this.allProducts = data.content || data;
        this.loadCategories();
        this.applyFiltersAndPaginate();
        
        // Listen for cart changes to refresh products
        this.cartService.cartChanged$.subscribe(() => {
          // Reload products from backend to get updated quantities
          this.loadAllProducts();
        });
      },
      error: (err) => console.error('Error loading products', err)
    });
  }

  loadCategories() {
    // Get unique categories from all products
    const uniqueCategories = [...new Set(this.allProducts.map((p: Product) => p.category))] as string[];
    this.categories.set([
      { label: 'All Categories', value: '' },
      ...uniqueCategories.map((cat: string) => ({ label: cat, value: cat }))
    ]);
  }

  applyFiltersAndPaginate() {
    let filtered = [...this.allProducts];

    // Apply client-side filters
    if (this.selectedCategory()) {
      filtered = filtered.filter((p: Product) => p.category === this.selectedCategory());
    }

    if (this.minPrice() !== null) {
      filtered = filtered.filter((p: Product) => p.price >= this.minPrice()!);
    }

    if (this.maxPrice() !== null) {
      filtered = filtered.filter((p: Product) => p.price <= this.maxPrice()!);
    }

    if (this.searchText()) {
      const search = this.searchText().toLowerCase();
      filtered = filtered.filter((p: Product) => 
        p.name.toLowerCase().includes(search) || 
        p.description.toLowerCase().includes(search)
      );
    }

    // Set total and paginate
    this.totalRecords = filtered.length;
    const startIndex = this.first;
    const endIndex = this.first + this.rows;
    this.products.set(filtered.slice(startIndex, endIndex));
  }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
    this.applyFiltersAndPaginate();
  }

  onFilterChange() {
    this.first = 0; // Reset to first page when filtering
    this.applyFiltersAndPaginate();
  }

  resetFilters() {
    this.selectedCategory.set(null);
    this.minPrice.set(null);
    this.maxPrice.set(null);
    this.searchText.set('');
    this.first = 0;
    this.applyFiltersAndPaginate();
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
