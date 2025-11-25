
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { CartComponent } from './cart.component';
import { CartService } from '../../services/cart.service';
import { Cart, CartItem } from '../../models/cart.model';
import { of } from 'rxjs';

describe('CartComponent', () => {
  let component: CartComponent;
  let fixture: ComponentFixture<CartComponent>;
  let cartServiceSpy: jasmine.SpyObj<CartService>;

  const mockCart: Cart = {
    id: 1,
    items: [
      {
        id: 1,
        product: {
          id: 1,
          code: 'P001',
          name: 'Test Product',
          price: 100,
          quantity: 5,
          category: 'Electronics',
          inventoryStatus: 'INSTOCK',
          rating: 4.5,
          createdAt: Date.now(),
          updatedAt: Date.now()
        },
        quantity: 2,
        addedAt: new Date()
      }
    ]
  };

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('CartService', ['getCart', 'updateQuantity', 'removeItem', 'clearCart', 'checkout']);

    await TestBed.configureTestingModule({
      imports: [FormsModule, ButtonModule, InputNumberModule, NoopAnimationsModule],
      declarations: [CartComponent],
      providers: [
        { provide: CartService, useValue: spy }
      ]
    })
      .compileComponents();

    cartServiceSpy = TestBed.inject(CartService) as jasmine.SpyObj<CartService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CartComponent);
    component = fixture.componentInstance;
    cartServiceSpy.getCart.and.returnValue(of(mockCart));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display cart items', () => {
    const cartItems = fixture.debugElement.queryAll(By.css('.grid.align-items-center'));
    expect(cartItems.length).toBe(1);

    const productName = fixture.debugElement.query(By.css('.font-bold')).nativeElement.textContent;
    expect(productName).toContain('Test Product');
  });

  it('should calculate total price correctly', () => {
    const totalPriceElement = fixture.debugElement.query(By.css('.text-xl.font-bold')).nativeElement;
    expect(totalPriceElement.textContent).toContain('$200'); // 100 * 2
  });

  it('should update item quantity', () => {
    const plusButton = fixture.debugElement.query(By.css('.pi-plus'));
    plusButton.nativeElement.click();

    expect(cartServiceSpy.updateQuantity).toHaveBeenCalledWith(1, 3);
  });

  it('should remove item from cart', () => {
    const trashButton = fixture.debugElement.query(By.css('.pi-trash'));
    trashButton.nativeElement.click();

    expect(cartServiceSpy.removeItem).toHaveBeenCalledWith(mockCart.items[0]);
  });

  it('should clear cart', () => {
    const clearCartButton = fixture.debugElement.query(By.css('.p-button-danger'));
    clearCartButton.nativeElement.click();

    expect(cartServiceSpy.clearCart).toHaveBeenCalled();
  });

  it('should checkout', () => {
    spyOn(window, 'alert');

    const checkoutButton = fixture.debugElement.query(By.css('.p-button-success'));
    checkoutButton.nativeElement.click();

    expect(cartServiceSpy.checkout).toHaveBeenCalled();
  });

  it('should show empty cart message when cart is empty', () => {
    cartServiceSpy.getCart.and.returnValue(of({ id: 1, items: [] }));
    fixture.detectChanges();

    const emptyCartMessage = fixture.debugElement.query(By.css('.text-center'));
    expect(emptyCartMessage.nativeElement.textContent).toContain('Your cart is empty');
  });
});
