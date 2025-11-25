
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CartService } from './cart.service';
import { Cart, CartItem } from '../models/cart.model';

describe('CartService', () => {
  let service: CartService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CartService]
    });
    service = TestBed.inject(CartService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get cart', () => {
    const mockCart: Cart = {
      id: 1,
      items: []
    };

    service.getCart().subscribe(cart => {
      expect(cart).toEqual(mockCart);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/cart');
    expect(req.request.method).toBe('GET');
    req.flush(mockCart);
  });

  it('should add item to cart', () => {
    const mockCart: Cart = {
      id: 1,
      items: []
    };

    const addToCartRequest = {
      productId: 1,
      quantity: 2
    };

    service.addToCart(addToCartRequest).subscribe(cart => {
      expect(cart).toEqual(mockCart);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/cart/items');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(addToCartRequest);
    req.flush(mockCart);
  });

  it('should update cart item quantity', () => {
    const mockCart: Cart = {
      id: 1,
      items: []
    };

    service.updateQuantity(1, 3).subscribe(cart => {
      expect(cart).toEqual(mockCart);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/cart/items/1?quantity=3');
    expect(req.request.method).toBe('PUT');
    req.flush(mockCart);
  });

  it('should remove item from cart', () => {
    const mockCart: Cart = {
      id: 1,
      items: []
    };

    service.removeItem(1).subscribe(cart => {
      expect(cart).toEqual(mockCart);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/cart/items/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(mockCart);
  });

  it('should clear cart', () => {
    service.clearCart().subscribe(() => {
      expect(service.cart()).toBeNull();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/cart');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should checkout', () => {
    const mockResponse = 'Order placed successfully';

    service.checkout().subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(service.cart()).toBeNull();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/cart/checkout');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
