# 📚 GUIDE D'IMPLÉMENTATION - Alten E-Commerce

## PARTIE 1: SHOP - AFFICHAGE PRODUITS & PANIER

### ✅ 1. Afficher les informations pertinentes d'un produit sur la liste

**Frontend Component: `products.component.ts`**
```typescript
import { Component, OnInit, signal } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, PaginatorModule, ButtonModule, TagModule, DataViewModule, RatingModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit {
  products = signal<Product[]>([]);  // State réactive avec Angular Signals
  totalRecords = 0;
  rows = 10;
  first = 0;

  constructor(
    private productService: ProductService,
    private cartService: CartService
  ) { }

  ngOnInit() {
    this.loadProducts(0, this.rows);
  }

  loadProducts(page: number, size: number) {
    this.productService.getProducts(page, size).subscribe({
      next: (data: any) => {
        if (data.content) {
          this.products.set(data.content);
          this.totalRecords = data.totalElements;
        } else {
          this.products.set(data);
          this.totalRecords = data.length;
        }
      },
      error: (err) => console.error('Error loading products', err)
    });
  }
}
```

**Frontend Template: `products.component.html`**
```html
<div class="card">
    <p-dataView [value]="products()" [paginator]="true" [rows]="rows" 
        [first]="first" [lazy]="true" (onPage)="onPageChange($event)" 
        [totalRecords]="totalRecords" layout="list">
        
        <ng-template pTemplate="list" let-products>
            <div class="grid grid-nogutter">
                <div class="col-12" *ngFor="let product of products; let first = first">
                    <div class="flex flex-column xl:flex-row xl:align-items-start p-4 gap-4">
                        <!-- IMAGE DU PRODUIT -->
                        <img class="w-9 sm:w-16rem xl:w-10rem shadow-2 block mx-auto border-round"
                            [src]="product.image" [alt]="product.name" />
                        
                        <div class="flex flex-column sm:flex-row justify-content-between align-items-center 
                            xl:align-items-start flex-1 gap-4">
                            
                            <div class="flex flex-column align-items-center sm:align-items-start gap-3">
                                <!-- NOM DU PRODUIT -->
                                <div class="text-2xl font-bold text-900">{{ product.name }}</div>
                                
                                <!-- RATING -->
                                <p-rating [(ngModel)]="product.rating" [readonly]="true"></p-rating>
                                
                                <!-- DESCRIPTION -->
                                <div class="text-700">{{ product.description }}</div>
                                
                                <!-- CATÉGORIE ET STATUT -->
                                <div class="flex align-items-center gap-3">
                                    <span class="flex align-items-center gap-2">
                                        <i class="pi pi-tag"></i>
                                        <span class="font-semibold">{{ product.category }}</span>
                                    </span>
                                    <p-tag [value]="product.inventoryStatus"
                                        [severity]="getSeverity(product.inventoryStatus)"></p-tag>
                                </div>
                            </div>
                            
                            <div class="flex sm:flex-column align-items-center sm:align-items-end gap-3 sm:gap-2">
                                <!-- PRIX -->
                                <span class="text-2xl font-semibold">{{ '$' + product.price }}</span>
                                
                                <!-- QUANTITÉ EN STOCK -->
                                <span class="font-semibold">Quantity: {{ product.quantity }}</span>
                                
                                <!-- BOUTON AJOUTER AU PANIER -->
                                <button pButton icon="pi pi-shopping-cart"
                                    class="md:align-self-end mb-2 p-button-rounded"
                                    [disabled]="product.inventoryStatus === 'OUTOFSTOCK' || product.quantity === 0"
                                    (click)="addToCart(product)"></button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </ng-template>
    </p-dataView>
</div>
```

**Informations affichées:**
- ✅ **Image** - Image du produit avec ombre et border-radius
- ✅ **Nom** - Titre en gros caractères gras
- ✅ **Rating** - Notation 5 étoiles en lecture seule
- ✅ **Description** - Texte descriptif complet
- ✅ **Catégorie** - Avec icône
- ✅ **Statut Inventaire** - Badge coloré (INSTOCK=vert, LOWSTOCK=orange, OUTOFSTOCK=rouge)
- ✅ **Prix** - Formaté avec symbole $
- ✅ **Quantité en stock** - Nombre d'unités disponibles

---

### ✅ 2. Ajouter un produit au panier depuis la liste

**Component Method:**
```typescript
addToCart(product: Product) {
  if (product.id) {
    this.cartService.addToCart({ 
      productId: product.id, 
      quantity: 1 
    }).subscribe({
      next: () => console.log('Added to cart'),
      error: (err) => console.error('Error adding to cart', err)
    });
  }
}
```

**Template Button:**
```html
<button pButton icon="pi pi-shopping-cart"
    class="md:align-self-end mb-2 p-button-rounded"
    [disabled]="product.inventoryStatus === 'OUTOFSTOCK' || product.quantity === 0"
    (click)="addToCart(product)">
</button>
```

**Comportement:**
- ✅ Bouton shopping cart icon
- ✅ Désactivé si produit OUT_OF_STOCK ou quantity = 0
- ✅ Appel API POST `/api/cart/items`
- ✅ Panier mis à jour automatiquement (signal)

---

### ✅ 3. Supprimer un produit du panier

**Cart Service:**
```typescript
removeItem(itemId: number): Observable<Cart> {
  return this.http.delete<Cart>(`${this.API_URL}/items/${itemId}`).pipe(
    tap(cart => this.cart.set(cart))
  );
}
```

**Cart Component:**
```typescript
removeItem(item: CartItem) {
  if (item.product.id) {
    this.cartService.removeItem(item.product.id).subscribe();
  }
}
```

**Template:**
```html
<div class="col-12 md:col-2 flex justify-content-between align-items-center">
  <span class="font-bold">{{ '$' + (item.product.price * item.quantity) }}</span>
  
  <!-- BOUTON SUPPRIMER -->
  <button pButton icon="pi pi-trash" 
    class="p-button-danger p-button-text p-button-rounded"
    (click)="removeItem(item)">
  </button>
</div>
```

**API Endpoint:**
```
DELETE /api/cart/items/{itemId}
```

---

### ✅ 4. Badge indiquant la quantité de produits dans le panier

**Navbar Component:**
```typescript
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, BadgeModule, ButtonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {
  cartCount;  // Signal réactif

  constructor(private cartService: CartService) {
    this.cartCount = this.cartService.cartItemCount;
  }

  ngOnInit(): void {
    this.cartService.refreshCart();
  }
}
```

**Cart Service (calcul du total):**
```typescript
export class CartService {
  public cart = signal<Cart | null>(null);
  
  // Computed signal - recalculé automatiquement quand cart change
  cartItemCount = computed(() => {
    const currentCart = this.cart();
    return currentCart?.items.reduce((sum, item) => sum + item.quantity, 0) ?? 0;
  });
}
```

**Template:**
```html
<div class="flex align-items-center gap-3">
  <!-- SHOPPING CART BUTTON -->
  <button pButton icon="pi pi-shopping-cart" 
    class="p-button-rounded p-button-text p-button-lg"
    routerLink="/cart">
    
    <!-- BADGE AVEC NOMBRE D'ARTICLES -->
    <p-badge *ngIf="cartCount() > 0" 
      [value]="cartCount().toString()" 
      severity="danger" 
      class="absolute"
      style="top: 0; right: 0">
    </p-badge>
  </button>
</div>
```

**Fonctionnalités:**
- ✅ Badge affichant le nombre total d'articles
- ✅ Badge rouge (danger severity)
- ✅ Positionné en haut à droite du bouton panier
- ✅ Mise à jour automatique avec signal réactif
- ✅ Badge masqué si panier vide (cartCount = 0)

---

### ✅ 5. Visualiser la liste des produits du panier

**Cart Component:**
```typescript
import { CartService } from '../../services/cart.service';
import { computed } from '@angular/core';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, ButtonModule, InputNumberModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  cart;
  totalPrice;

  constructor(public cartService: CartService) {
    this.cart = this.cartService.cart;
    
    // Computed signal pour le total
    this.totalPrice = computed(() => {
      const currentCart = this.cart();
      return currentCart?.items.reduce(
        (sum, item) => sum + (item.product.price * item.quantity), 
        0
      ) ?? 0;
    });
  }

  ngOnInit() {
    this.cartService.getCart().subscribe();
  }

  updateQuantity(item: CartItem, quantity: number) {
    if (item.product.id) {
      this.cartService.updateQuantity(item.product.id, quantity).subscribe();
    }
  }

  removeItem(item: CartItem) {
    if (item.product.id) {
      this.cartService.removeItem(item.product.id).subscribe();
    }
  }

  clearCart() {
    this.cartService.clearCart().subscribe();
  }
}
```

**Template:**
```html
<div class="card p-4">
    <h2 class="mb-4">Shopping Cart</h2>

    <div *ngIf="cart()?.items?.length; else emptyCart">
        <!-- HEADER -->
        <div class="grid header font-bold mb-2 border-bottom-1 surface-border pb-2 hidden md:flex">
            <div class="col-6">Product</div>
            <div class="col-2">Price</div>
            <div class="col-2">Quantity</div>
            <div class="col-2">Total</div>
        </div>

        <!-- LIGNE POUR CHAQUE ARTICLE -->
        <div class="grid align-items-center mb-3 border-bottom-1 surface-border pb-3" 
          *ngFor="let item of cart()!.items">
          
            <!-- IMAGE ET NOM -->
            <div class="col-12 md:col-6 flex align-items-center gap-3">
                <img [src]="item.product.image"
                    [alt]="item.product.name" 
                    class="w-4rem shadow-2 border-round" />
                <div class="flex flex-column">
                    <span class="font-bold">{{ item.product.name }}</span>
                    <span class="text-sm text-600">{{ item.product.category }}</span>
                </div>
            </div>
            
            <!-- PRIX UNITAIRE -->
            <div class="col-12 md:col-2">
                <span class="md:hidden font-bold mr-2">Price:</span>
                {{ '$' + item.product.price }}
            </div>
            
            <!-- CONTRÔLES DE QUANTITÉ -->
            <div class="col-12 md:col-2 flex align-items-center gap-2">
                <!-- BOUTON MOINS -->
                <button pButton icon="pi pi-minus" 
                  class="p-button-text p-button-rounded p-button-sm"
                  (click)="updateQuantity(item, item.quantity - 1)" 
                  [disabled]="item.quantity <= 1">
                </button>
                
                <!-- AFFICHAGE QUANTITÉ -->
                <span class="font-bold w-2rem text-center">{{ item.quantity }}</span>
                
                <!-- BOUTON PLUS -->
                <button pButton icon="pi pi-plus" 
                  class="p-button-text p-button-rounded p-button-sm"
                  (click)="updateQuantity(item, item.quantity + 1)">
                </button>
            </div>
            
            <!-- TOTAL DE LA LIGNE ET SUPPRIMER -->
            <div class="col-12 md:col-2 flex justify-content-between align-items-center">
                <span class="font-bold">{{ '$' + (item.product.price * item.quantity) }}</span>
                
                <!-- BOUTON SUPPRIMER -->
                <button pButton icon="pi pi-trash" 
                  class="p-button-danger p-button-text p-button-rounded"
                  (click)="removeItem(item)">
                </button>
            </div>
        </div>

        <!-- TOTAL DU PANIER -->
        <div class="flex justify-content-end mt-4">
            <div class="text-xl font-bold">
                Total: {{ '$' + totalPrice() }}
            </div>
        </div>

        <!-- BOUTONS D'ACTION -->
        <div class="flex justify-content-end mt-4 gap-3">
            <button pButton label="Clear Cart" 
              class="p-button-outlined p-button-danger" 
              (click)="clearCart()">
            </button>
            <button pButton label="Checkout" 
              class="p-button-success">
            </button>
        </div>
    </div>

    <!-- PANIER VIDE -->
    <ng-template #emptyCart>
        <div class="text-center p-5">
            <i class="pi pi-shopping-cart text-6xl text-500 mb-3"></i>
            <div class="text-xl font-semibold text-900">Your cart is empty</div>
            <button pButton label="Go Shopping" 
              routerLink="/products" 
              class="mt-3">
            </button>
        </div>
    </ng-template>
</div>
```

**Affichage du panier:**
- ✅ Image du produit
- ✅ Nom du produit
- ✅ Catégorie
- ✅ Prix unitaire
- ✅ Contrôles de quantité (+/-)
- ✅ Sous-total par article
- ✅ Bouton supprimer
- ✅ Total général du panier
- ✅ Boutons Clear Cart et Checkout
- ✅ Message si panier vide avec lien "Go Shopping"

---

## PARTIE 2: CONTACT

### ✅ 1. Menu Contact dans la barre latérale

**Navbar Template:**
```html
<div class="flex justify-content-between align-items-center px-5 py-3 surface-card shadow-2 mb-4">
    <div class="flex align-items-center gap-4">
        <!-- LOGO -->
        <a routerLink="/" class="text-2xl font-bold text-900 no-underline">Alten Shop</a>
        
        <!-- MENU NAVIGATION -->
        <div class="hidden md:flex gap-3">
            <a routerLink="/products" routerLinkActive="text-primary font-bold"
                class="text-700 hover:text-900 no-underline cursor-pointer 
                transition-colors transition-duration-150">
              Products
            </a>
            
            <!-- LIEN CONTACT -->
            <a routerLink="/contact" routerLinkActive="text-primary font-bold"
                class="text-700 hover:text-900 no-underline cursor-pointer 
                transition-colors transition-duration-150">
              Contact
            </a>
        </div>
    </div>
    
    <!-- ICONES DROITE -->
    <div class="flex align-items-center gap-3">
        <!-- SHOPPING CART AVEC BADGE -->
        <button pButton icon="pi pi-shopping-cart" 
            class="p-button-rounded p-button-text p-button-lg"
            routerLink="/cart">
            <p-badge *ngIf="cartCount() > 0" [value]="cartCount().toString()" 
                severity="danger" class="absolute"></p-badge>
        </button>
    </div>
</div>
```

**Routes Configuration (`app.routes.ts`):**
```typescript
import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { ProductsComponent } from './pages/products/products.component';
import { CartComponent } from './pages/cart/cart.component';
import { ContactComponent } from './pages/contact/contact.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'products', component: ProductsComponent },
    { path: 'cart', component: CartComponent },
    { path: 'contact', component: ContactComponent },  // ✅ Route Contact
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];
```

---

### ✅ 2. Page Contact avec formulaire

**Contact Component:**
```typescript
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ContactService } from '../../services/contact.service';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    ButtonModule, 
    InputTextModule, 
    InputTextareaModule, 
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss'
})
export class ContactComponent {
  contactForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private contactService: ContactService,
    private messageService: MessageService
  ) {
    // Construction du formulaire réactif
    this.contactForm = this.fb.group({
      email: [
        '', 
        [Validators.required, Validators.email]  // ✅ Email obligatoire et valide
      ],
      message: [
        '', 
        [Validators.required, Validators.maxLength(300)]  // ✅ Max 300 caractères
      ]
    });
  }

  onSubmit() {
    if (this.contactForm.valid) {
      this.contactService.sendMessage(this.contactForm.value).subscribe({
        next: () => {
          // ✅ Afficher le message de succès
          this.messageService.add({ 
            severity: 'success', 
            summary: 'Success', 
            detail: 'Demande de contact envoyée avec succès' 
          });
          this.contactForm.reset();
        },
        error: (err: any) => {
          this.messageService.add({ 
            severity: 'error', 
            summary: 'Error', 
            detail: 'Erreur lors de l\'envoi du message' 
          });
          console.error(err);
        }
      });
    } else {
      this.contactForm.markAllAsTouched();
    }
  }
}
```

**Contact Service:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Contact } from '../models/contact.model';

@Injectable({
    providedIn: 'root'
})
export class ContactService {
    private readonly API_URL = 'http://localhost:8080/api/contact';

    constructor(private http: HttpClient) { }

    sendMessage(contact: Contact): Observable<Contact> {
        return this.http.post<Contact>(this.API_URL, contact);
    }
}
```

---

### ✅ 3. Validation du formulaire

**Template avec Validation:**
```html
<div class="card p-4 mx-auto" style="max-width: 600px;">
    <h2 class="mb-4 text-center">Contact Us</h2>

    <form [formGroup]="contactForm" (ngSubmit)="onSubmit()" class="flex flex-column gap-4">
        
        <!-- CHAMP EMAIL -->
        <div class="flex flex-column gap-2">
            <label htmlFor="email" class="font-bold">Email</label>
            
            <input pInputText id="email" 
              formControlName="email" 
              placeholder="Your email address"
              [ngClass]="{'ng-invalid ng-dirty': 
                contactForm.get('email')?.touched && 
                contactForm.get('email')?.invalid}" />
            
            <!-- MESSAGE D'ERREUR EMAIL REQUIS -->
            <small class="p-error"
                *ngIf="contactForm.get('email')?.touched && 
                  contactForm.get('email')?.errors?.['required']">
              Email is required.
            </small>
            
            <!-- MESSAGE D'ERREUR EMAIL INVALIDE -->
            <small class="p-error"
                *ngIf="contactForm.get('email')?.touched && 
                  contactForm.get('email')?.errors?.['email']">
              Invalid email format.
            </small>
        </div>

        <!-- CHAMP MESSAGE -->
        <div class="flex flex-column gap-2">
            <label htmlFor="message" class="font-bold">Message</label>
            
            <textarea pInputTextarea id="message" 
              formControlName="message" 
              rows="5"
              placeholder="Your message (max 300 chars)"
              [ngClass]="{'ng-invalid ng-dirty': 
                contactForm.get('message')?.touched && 
                contactForm.get('message')?.invalid}">
            </textarea>
            
            <!-- MESSAGES D'ERREUR ET COMPTEUR DE CARACTÈRES -->
            <div class="flex justify-content-between">
              <!-- MESSAGE REQUIS -->
              <small class="p-error"
                  *ngIf="contactForm.get('message')?.touched && 
                    contactForm.get('message')?.errors?.['required']">
                Message is required.
              </small>
              
              <!-- MESSAGE TROP LONG -->
              <small class="p-error"
                  *ngIf="contactForm.get('message')?.touched && 
                    contactForm.get('message')?.errors?.['maxlength']">
                Message must be less than 300 characters.
              </small>
              
              <!-- COMPTEUR EN TEMPS RÉEL -->
              <small class="text-500">
                {{ contactForm.get('message')?.value?.length || 0 }} / 300
              </small>
            </div>
        </div>

        <!-- BOUTON ENVOYER -->
        <button pButton type="submit" 
          label="Send Message" 
          class="p-button-primary w-full"
          [disabled]="contactForm.invalid">
        </button>
    </form>
    
    <!-- TOAST NOTIFICATION -->
    <p-toast></p-toast>
</div>
```

**Validations:**
- ✅ **Email requis** - Validators.required
- ✅ **Email valide** - Validators.email
- ✅ **Message requis** - Validators.required
- ✅ **Max 300 caractères** - Validators.maxLength(300)
- ✅ **Bouton désactivé** si formulaire invalide
- ✅ **Messages d'erreur** affichés après interaction
- ✅ **Compteur en temps réel** affichant les caractères utilisés

---

### ✅ 4. Message de succès

**Toast Notification:**
```typescript
this.messageService.add({ 
  severity: 'success',  // Vert
  summary: 'Success',
  detail: 'Demande de contact envoyée avec succès'  // ✅ Message exact
});
```

**Affichage:**
- ✅ Notification toast verte avec icône de succès
- ✅ Message en français: "Demande de contact envoyée avec succès"
- ✅ Formulaire réinitialisé après envoi
- ✅ Gestion des erreurs avec notification rouge

---

## BONUS: PAGINATION ET FILTRAGE

### ✅ Système de Pagination

**Component:**
```typescript
export class ProductsComponent implements OnInit {
  products = signal<Product[]>([]);
  totalRecords = 0;
  rows = 10;        // 10 articles par page
  first = 0;        // Index du premier élément

  loadProducts(page: number, size: number) {
    this.productService.getProducts(page, size).subscribe({
      next: (data: any) => {
        if (data.content) {
          this.products.set(data.content);
          this.totalRecords = data.totalElements;  // Total de produits
        }
      }
    });
  }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
    this.loadProducts(event.page, event.rows);  // Charger nouvelle page
  }
}
```

**Template PrimeNG DataView:**
```html
<p-dataView 
  [value]="products()" 
  [paginator]="true" 
  [rows]="rows" 
  [first]="first" 
  [lazy]="true"
  (onPage)="onPageChange($event)" 
  [totalRecords]="totalRecords" 
  layout="list">
  
  <ng-template pTemplate="list" let-products>
    <!-- Afficher les 10 produits de la page -->
  </ng-template>
</p-dataView>
```

**Backend API:**
```
GET /api/products?page=0&size=10
Response:
{
  "content": [...],      // 10 produits
  "totalElements": 50,   // 50 produits au total
  "totalPages": 5,       // 5 pages
  "currentPage": 0
}
```

**Fonctionnalités:**
- ✅ Lazy loading (chargement à la demande)
- ✅ 10 articles par page par défaut
- ✅ Numérotation des pages
- ✅ Navigation avant/après
- ✅ Total d'éléments affichés

---

### ✅ Filtrage par Catégorie et Statut

**Backend Endpoints:**
```
GET /api/products/category/{category}
GET /api/products/status/{status}
```

**Product Service:**
```typescript
getProductsByCategory(category: string): Observable<Product[]> {
  return this.http.get<Product[]>(`${this.API_URL}/category/${category}`);
}

getProductsByStatus(status: string): Observable<Product[]> {
  return this.http.get<Product[]>(`${this.API_URL}/status/${status}`);
}
```

**Filtres disponibles:**
- ✅ **Par Catégorie**: Electronics, Furniture, Clothing, etc.
- ✅ **Par Statut**: INSTOCK, LOWSTOCK, OUTOFSTOCK

---

### ✅ Ajuster la quantité

**Dans la liste des produits (Bonus):**
- Affichage de la quantité en stock
- Bouton "Ajouter au panier" avec quantité 1 par défaut

**Dans le panier:**
```html
<!-- BOUTON MOINS -->
<button pButton icon="pi pi-minus" 
  class="p-button-text p-button-rounded p-button-sm"
  (click)="updateQuantity(item, item.quantity - 1)" 
  [disabled]="item.quantity <= 1">
</button>

<!-- AFFICHAGE QUANTITÉ -->
<span class="font-bold w-2rem text-center">{{ item.quantity }}</span>

<!-- BOUTON PLUS -->
<button pButton icon="pi pi-plus" 
  class="p-button-text p-button-rounded p-button-sm"
  (click)="updateQuantity(item, item.quantity + 1)">
</button>
```

**Cart Service:**
```typescript
updateQuantity(itemId: number, quantity: number): Observable<Cart> {
  const params = new HttpParams().set('quantity', quantity.toString());
  return this.http.put<Cart>(`${this.API_URL}/items/${itemId}`, null, { params }).pipe(
    tap(cart => this.cart.set(cart))
  );
}
```

**Fonctionnalités:**
- ✅ Boutons +/- pour augmenter/diminuer
- ✅ Bouton - désactivé si quantité = 1
- ✅ Mise à jour en temps réel du total
- ✅ Mise à jour du badge du panier automatiquement

---

## 🏗️ ARCHITECTURE GLOBALE

### Frontend Stack:
- **Framework**: Angular 18 (Standalone Components)
- **State Management**: Angular Signals (réactif)
- **HTTP**: HttpClient avec intercepteurs (JWT)
- **UI Components**: PrimeNG 17.14.0
- **Styling**: SCSS + PrimeFlex

### Backend Stack:
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Security**: JWT + Spring Security
- **Database**: H2 (in-memory)
- **ORM**: JPA/Hibernate

### Communication:
- **Protocol**: REST API
- **Auth**: JWT Bearer tokens
- **Data Format**: JSON
- **CORS**: Configuré pour localhost:4200

---

## ✅ RÉSUMÉ DES IMPLÉMENTATIONS

| Fonctionnalité | Frontend | Backend | Status |
|---|---|---|---|
| Affichage produits | ✅ ProductsComponent | ✅ GET /api/products | 📦 READY |
| Ajouter au panier | ✅ CartService | ✅ POST /api/cart/items | 📦 READY |
| Supprimer du panier | ✅ CartComponent | ✅ DELETE /api/cart/items/{id} | 📦 READY |
| Badge panier | ✅ NavbarComponent | ✅ CartService signal | 📦 READY |
| Voir panier | ✅ CartComponent | ✅ GET /api/cart | 📦 READY |
| Menu Contact | ✅ NavbarComponent | - | 📦 READY |
| Page Contact | ✅ ContactComponent | ✅ POST /api/contact | 📦 READY |
| Validation email | ✅ Reactive Forms | ✅ @Email annotation | 📦 READY |
| Validation message | ✅ maxLength(300) | ✅ @Size(max=300) | 📦 READY |
| Message succès | ✅ Toast notification | ✅ 201 CREATED | 📦 READY |
| Pagination | ✅ p-dataView | ✅ Spring Data Page | 📦 READY |
| Filtrage catégorie | ✅ Service method | ✅ Repository query | 📦 READY |
| Filtrage statut | ✅ Service method | ✅ Repository query | 📦 READY |
| Ajuster quantité | ✅ +/- buttons | ✅ PUT /api/cart/items | 📦 READY |

---

**Application entièrement implémentée et déployée sur Docker! 🎉**
