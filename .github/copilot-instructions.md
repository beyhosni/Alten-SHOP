# Copilot Instructions - Alten E-Commerce Application

## Architecture Overview

**Alten E-Commerce** is a full-stack e-commerce application with:
- **Backend**: Spring Boot 3.2.0 (Java 21) with JWT-based security + H2 database
- **Frontend**: Angular 18 standalone components with signals-based state management
- **Deployment**: Docker-based (docker-compose orchestration)

### Key Components

#### Backend Structure (`backend/src/main/java/com/alten/shop/`)
- **Controllers**: REST endpoints (Auth, Product, Cart, Wishlist, Contact)
- **Services**: Business logic layer (handles domain operations)
- **Repository**: JPA interfaces for H2 database persistence
- **Security**: JWT authentication via `JwtService` + `JwtAuthenticationFilter`
- **Config**: `SecurityConfig` for Spring Security + CORS, `SwaggerConfig` for OpenAPI docs
- **DTO**: Data transfer objects for request/response validation

#### Frontend Structure (`frontend/src/app/`)
- **Services**: API communication (`AuthService`, `ProductService`, `CartService`, etc.)
- **Interceptors**: `jwtInterceptor` (auto-injects Bearer token), `errorInterceptor`
- **Guards**: Route protection (currently not implemented, but recommended pattern)
- **Models**: TypeScript interfaces for type safety
- **Pages**: Routable components (Login, Products, Cart, Contact)
- **Components**: Reusable UI components

---

## Critical Developer Workflows

### Backend Build & Test
```bash
cd backend
mvn clean package          # Full build + tests
mvn test                   # Run unit tests only
mvn spring-boot:run        # Local development (port 8080)
```
**Key**: Tests use `@WebMvcTest` + Mockito for controllers; services use `@ExtendWith(MockitoExtension.class)`

### Frontend Build & Development
```bash
cd frontend
npm install                # Install dependencies
npm start                  # Dev server (port 4200, proxies to localhost:8080)
npm test                   # Run Karma tests
npm run build              # Production build
```

### Full Stack (Docker)
```bash
# From project root
docker-compose up --build  # First time (~5-10 min)
docker-compose up -d       # Subsequent runs (~2 min, uses cache)
docker-compose down -v     # Clean shutdown + volumes
```

**Default Credentials**: `admin@admin.com` / `admin123`

**Access Points**:
- Frontend: http://localhost:4200
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

---

## Authentication Flow (Critical Pattern)

### Backend JWT Implementation
1. **JwtService** generates/validates tokens (uses JJWT library)
   - Secret: `jwt.secret` property (default: 404E6352..., configurable via env)
   - Expiration: `jwt.expiration` (default: 86400000ms = 24 hours)
   - Token contains: email claim + expiration
2. **JwtAuthenticationFilter** intercepts requests
   - Extracts "Bearer {token}" from `Authorization` header
   - Skips `/account` and `/token` endpoints (public auth endpoints)
   - Sets Spring SecurityContext if token valid
3. **SecurityConfig** chains JWT filter before authentication
   - CORS configured for `http://localhost:4200`
   - Session policy: STATELESS (no cookies)

### Frontend Token Handling
- **AuthService**: Manages JWT token in localStorage (`auth_token`, `current_user`)
- **jwtInterceptor**: Automatically injects `Authorization: Bearer {token}` on all requests except `/account` and `/token`
- **errorInterceptor**: Should handle 401/403 responses
- **currentUser signal**: Reactive user state using Angular signals

---

## Project-Specific Patterns & Conventions

### Backend
- **Validation**: Use `@Valid` + `@NotNull`, `@NotBlank`, `@Email` from jakarta.validation
- **Exceptions**: Currently throws generic `RuntimeException` (consider custom exceptions)
- **Error Responses**: Status codes (201 CREATED for register, 400 for validation, 403 for auth failures)
- **DTOs**: Immutable objects for API boundaries; use Lombok `@Builder` pattern
- **Controllers**: `@CrossOrigin(origins = "http://localhost:4200")` on each controller
- **Admin Check**: Hardcoded check `user.email == "admin@admin.com"` (refactor needed for scalability)

### Frontend
- **Routing**: Hash-based routes defined in `app.routes.ts`
- **Signals**: `signal<>()` for reactive state (replaces RxJS for some use cases)
- **HTTP**: `HttpClient` with interceptors; services provide RxJS Observables
- **Styling**: SCSS with PrimeNG/PrimeFlex components
- **Models**: Interfaces in `models/auth.model.ts`, etc.

---

## Cross-Component Communication Patterns

### Auth Flow (Request → Response Cycle)
1. Frontend: `AuthService.register/login()` → POST `/account` or `/token`
2. Backend: `AuthController` → `AuthService.register/login()`
3. Backend: Generates JWT via `JwtService.generateToken(email)`
4. Frontend: `AuthService.handleAuthResponse()` saves token + user to localStorage
5. Frontend: All subsequent requests include JWT via `jwtInterceptor`

### Product Listing (Typical CRUD Flow)
1. Frontend: `ProductService.getProducts()` → GET `/api/products`
2. Backend: `ProductController.getProducts()` → `ProductService`
3. Backend: Returns paginated list from H2 database
4. Frontend: Displays via PrimeNG table/components in `ProductsComponent`

### Cart/Wishlist (User-Specific)
- Extracted from `Authentication` object in request (Spring Security Context)
- Email used as lookup key to fetch user-specific cart/wishlist from database
- Example in `CartController`: `authentication.getPrincipal()` returns user email

---

## External Dependencies & Integration Points

### Backend Dependencies
- **Spring Boot 3.2.0**: Web, Data JPA, Security, Validation
- **JJWT**: JWT token generation/parsing
- **H2 Database**: In-memory DB (DDL auto-create-drop on startup)
- **Lombok**: Annotation processor for getters, setters, builders
- **Swagger/OpenAPI 3.0**: API documentation at `/swagger-ui.html`

### Frontend Dependencies
- **Angular 18**: Core framework with standalone components
- **PrimeNG 20.3.0**: Rich UI components (tables, buttons, forms)
- **RxJS 7.8.0**: Observable/Promise utilities
- **TypeScript 5.5.2**: Language layer

### Environment Variables
- **Backend** (docker-compose): `SPRING_PROFILES_ACTIVE`, `JWT_SECRET`, `JWT_EXPIRATION`
- **Frontend**: API_URL hardcoded to `http://localhost:8080` in `AuthService`

---

## Testing Patterns

### Backend Testing
- **Controller Tests**: `@WebMvcTest` + `MockMvc` + `@MockBean` for dependencies
- **Service Tests**: `@ExtendWith(MockitoExtension.class)` + `@Mock` / `@InjectMocks`
- **JWT Tests**: Use `ReflectionTestUtils.setField()` to inject test secret key
- **Test Structure**: Given-When-Then style assertions using AssertJ

### Frontend Testing
- **Framework**: Karma + Jasmine (default Angular CLI)
- **Interceptors**: Should mock HTTP calls to test interceptor logic
- **Services**: Mock `HttpClient` responses

---

## Key Files Reference

| Purpose | Path |
|---------|------|
| Backend entry point | `backend/src/main/java/com/alten/shop/ShopApplication.java` |
| Security config | `backend/src/main/java/com/alten/shop/config/SecurityConfig.java` |
| JWT service | `backend/src/main/java/com/alten/shop/security/JwtService.java` |
| Frontend config | `frontend/src/app/app.config.ts` |
| Routes | `frontend/src/app/app.routes.ts` |
| Auth service | `frontend/src/app/services/auth.service.ts` |
| JWT interceptor | `frontend/src/app/interceptors/jwt.interceptor.ts` |
| Docker setup | `docker-compose.yml` |

---

## Known Limitations & Improvement Opportunities

1. **Admin Authorization**: Hardcoded email check (should use role-based approach)
2. **Error Handling**: Generic `RuntimeException` instead of custom exceptions
3. **Security**: JWT secret stored in plain text in docker-compose (use secrets management)
4. **Frontend Routing**: No route guards implemented (need to prevent cart access when logged out)
5. **State Management**: Mixed localStorage + signals (consider unified reactive store)
6. **API Base URL**: Hardcoded in frontend (should use environment configs)
