# Alten E-commerce Application

![Java](https://img.shields.io/badge/Java-23-orange?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen?style=flat&logo=spring)
![Angular](https://img.shields.io/badge/Angular-18-red?style=flat&logo=angular)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker)


Application e-commerce complète développée pour Alten, avec un backend Spring Boot et un frontend Angular.

## 🚀 Démarrage Rapide avec Docker

### Prérequis
- Docker Desktop installé et démarré
- Ports 8080 et 4200 disponibles

### Lancer l'application

```bash
docker-compose up --build
```

### Accès
- **Frontend** : http://localhost:4200
- **Backend API** : http://localhost:8080
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **H2 Console** : http://localhost:8080/h2-console

### Compte Admin
- Email : `admin@admin.com`
- Password : `admin123`

## 📦 Stack Technique

### Backend
- Java 21
- Spring Boot 3.2.0
- Spring Security + JWT
- H2 Database
- Maven
- Swagger/OpenAPI

### Frontend
- Angular 18
- Standalone Components
- Signals
- SCSS
- Nginx (production)

## 📚 Documentation

- **[DOCKER-GUIDE.md](DOCKER-GUIDE.md)** - Guide complet Docker
- **[POSTMAN-GUIDE.md](POSTMAN-GUIDE.md)** - Guide collection Postman
- **[Backend Walkthrough](walkthrough.md)** - Documentation backend
- **[Frontend Walkthrough](frontend-walkthrough.md)** - Documentation frontend

## 🧪 Tests avec Postman

1. Importer les fichiers :
   - `Alten-Ecommerce-API.postman_collection.json`
   - `Alten-Ecommerce.postman_environment.json`

2. Sélectionner l'environnement "Alten E-commerce Environment"

3. Tester les endpoints (22 requêtes disponibles)

## 🏗️ Structure du Projet

```
Alten-SHOP/
├── backend/                 # Spring Boot API
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                # Angular 18 App
│   ├── src/
│   ├── package.json
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml       # Orchestration Docker
├── DOCKER-GUIDE.md
├── POSTMAN-GUIDE.md
└── README.md
```

## 🔐 Fonctionnalités

### Authentification
- ✅ Inscription utilisateur
- ✅ Connexion JWT
- ✅ Gestion de session
- ✅ Admin-only operations

### Produits
- ✅ Catalogue complet
- ✅ Filtres (catégorie, stock)
- ✅ Pagination
- ✅ CRUD (admin uniquement)

### Panier
- ✅ Ajouter/Retirer produits
- ✅ Modifier quantités
- ✅ Vider le panier
- ✅ Panier par utilisateur

### Wishlist
- ✅ Ajouter/Retirer produits
- ✅ Vider la wishlist
- ✅ Wishlist par utilisateur

### Contact
- ✅ Formulaire de contact
- ✅ Validation (max 300 chars)

## 🛠️ Développement Local

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
ng serve
```

## 📊 API Endpoints

### Public
- `POST /account` - Créer un compte
- `POST /token` - Se connecter

### Protégés (JWT requis)
- `GET /api/products` - Liste produits
- `GET/POST/PUT/DELETE /api/cart/*` - Gestion panier
- `GET/POST/DELETE /api/wishlist/*` - Gestion wishlist
- `POST /api/contact` - Envoyer message

### Admin uniquement
- `POST /api/products` - Créer produit
- `PUT /api/products/{id}` - Modifier produit
- `DELETE /api/products/{id}` - Supprimer produit

## 🐳 Commandes Docker Utiles

```bash
# Démarrer
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Arrêter
docker-compose down

# Rebuild
docker-compose up --build

# Nettoyer tout
docker-compose down -v --rmi all
```

## 📝 Notes

- Base de données H2 en mémoire (données perdues au redémarrage)
- JWT expire après 24h
- CORS configuré pour localhost:4200
- Swagger accessible sans authentification

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit (`git commit -m 'Add AmazingFeature'`)
4. Push (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT.

---

**Développé avec ❤️ pour Alten**
