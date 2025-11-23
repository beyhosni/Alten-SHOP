# Guide d'Utilisation - Collection Postman Alten E-commerce

## 📦 Fichiers Créés

- **[Alten-Ecommerce-API.postman_collection.json](file:///c:/Users/33656/Documents/Alten/Alten-SHOP/Alten-Ecommerce-API.postman_collection.json)** - Collection complète avec tous les endpoints
- **[Alten-Ecommerce.postman_environment.json](file:///c:/Users/33656/Documents/Alten/Alten-SHOP/Alten-Ecommerce.postman_environment.json)** - Environnement avec variables

## 🚀 Installation

### 1. Importer dans Postman

1. Ouvrir Postman
2. Cliquer sur **Import** (en haut à gauche)
3. Glisser-déposer les 2 fichiers JSON ou cliquer sur **Upload Files**
4. Sélectionner les fichiers :
   - `Alten-Ecommerce-API.postman_collection.json`
   - `Alten-Ecommerce.postman_environment.json`

### 2. Activer l'Environnement

1. Dans Postman, cliquer sur le menu déroulant en haut à droite
2. Sélectionner **Alten E-commerce Environment**

## 📋 Structure de la Collection

### 1. Authentication (3 requêtes)
- **Register User** - Créer un compte utilisateur
- **Login User** - Se connecter (sauvegarde auto du JWT)
- **Login Admin** - Se connecter en tant qu'admin

### 2. Products (9 requêtes)
- **Get All Products** - Liste complète
- **Get Products Paginated** - Avec pagination
- **Get Product by ID** - Détail par ID
- **Get Product by Code** - Détail par code
- **Get Products by Category** - Filtrer par catégorie
- **Get Products by Status** - Filtrer par stock
- **Create Product** - ⚠️ Admin uniquement
- **Update Product** - ⚠️ Admin uniquement
- **Delete Product** - ⚠️ Admin uniquement

### 3. Shopping Cart (5 requêtes)
- **Get Cart** - Voir le panier
- **Add to Cart** - Ajouter un produit
- **Update Cart Item Quantity** - Modifier quantité
- **Remove from Cart** - Retirer un produit
- **Clear Cart** - Vider le panier

### 4. Wishlist (4 requêtes)
- **Get Wishlist** - Voir la wishlist
- **Add to Wishlist** - Ajouter un produit
- **Remove from Wishlist** - Retirer un produit
- **Clear Wishlist** - Vider la wishlist

### 5. Contact (1 requête)
- **Send Contact Message** - Envoyer un message

## 🔐 Authentification JWT

### Fonctionnement Automatique

La collection est configurée pour gérer automatiquement le JWT :

1. **Lors du Login/Register** :
   - Le token JWT est automatiquement sauvegardé dans `{{jwt_token}}`
   - L'email est sauvegardé dans `{{user_email}}`

2. **Pour toutes les autres requêtes** :
   - Le token est automatiquement ajouté dans le header `Authorization: Bearer {{jwt_token}}`

### Scripts de Test Automatiques

Les requêtes **Login** et **Register** contiennent des scripts qui s'exécutent automatiquement :

```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set('jwt_token', response.token);
    pm.environment.set('user_email', response.email);
    console.log('Token saved:', response.token);
}
```

## 📝 Scénarios de Test

### Scénario 1 : Utilisateur Normal

1. **Register User** → Créer un compte
2. **Get All Products** → Voir les produits
3. **Add to Cart** (productId: 1, quantity: 2) → Ajouter au panier
4. **Get Cart** → Vérifier le panier
5. **Add to Wishlist** (productId: 2) → Ajouter à la wishlist
6. **Get Wishlist** → Vérifier la wishlist
7. **Create Product** → ❌ Devrait retourner 403 Forbidden

### Scénario 2 : Admin

1. **Login Admin** (admin@admin.com / admin123) → Se connecter
2. **Create Product** → ✅ Créer un produit
3. **Update Product** → ✅ Modifier un produit
4. **Delete Product** → ✅ Supprimer un produit

### Scénario 3 : Panier Complet

1. **Login User** → Se connecter
2. **Add to Cart** (productId: 1, quantity: 2)
3. **Add to Cart** (productId: 2, quantity: 1)
4. **Get Cart** → Voir 2 items
5. **Update Cart Item Quantity** (itemId: 1, quantity: 5)
6. **Remove from Cart** (itemId: 2)
7. **Clear Cart** → Vider complètement

## 🔧 Variables d'Environnement

### Variables Disponibles

| Variable | Description | Exemple |
|----------|-------------|---------|
| `{{base_url}}` | URL de l'API | `http://localhost:8080` |
| `{{jwt_token}}` | Token JWT (auto-rempli) | `eyJhbGc...` |
| `{{user_email}}` | Email utilisateur (auto-rempli) | `john@example.com` |

### Modifier les Variables

1. Cliquer sur l'icône 👁️ en haut à droite
2. Cliquer sur **Edit** à côté de l'environnement
3. Modifier les valeurs
4. Cliquer sur **Save**

## 🎯 Exemples de Requêtes

### Créer un Produit (Admin)

```json
POST {{base_url}}/api/products
Authorization: Bearer {{jwt_token}}

{
  "code": "LAPTOP002",
  "name": "MacBook Pro 16",
  "description": "Powerful laptop for developers",
  "image": "https://via.placeholder.com/400",
  "category": "Electronics",
  "price": 2499.99,
  "quantity": 10,
  "internalReference": "APPLE-MBP-16",
  "shellId": 13,
  "inventoryStatus": "INSTOCK",
  "rating": 4.9
}
```

### Ajouter au Panier

```json
POST {{base_url}}/api/cart/items
Authorization: Bearer {{jwt_token}}

{
  "productId": 1,
  "quantity": 3
}
```

### Envoyer un Message de Contact

```json
POST {{base_url}}/api/contact
Authorization: Bearer {{jwt_token}}

{
  "email": "support@example.com",
  "message": "J'ai une question sur le produit LAPTOP001. Pouvez-vous me donner plus d'informations ?"
}
```

## ⚠️ Points Importants

### Endpoints Publics (pas de JWT requis)
- `POST /account` - Créer un compte
- `POST /token` - Se connecter

### Endpoints Protégés (JWT requis)
- Tous les endpoints `/api/*`

### Endpoints Admin Uniquement
- `POST /api/products` - Créer
- `PUT /api/products/{id}` - Modifier
- `DELETE /api/products/{id}` - Supprimer

### Vérification Admin
Seul l'utilisateur avec l'email `admin@admin.com` peut effectuer les opérations CUD sur les produits.

## 🐛 Dépannage

### Token Expiré
Si vous obtenez une erreur 401 :
1. Re-exécuter **Login User** ou **Login Admin**
2. Le nouveau token sera automatiquement sauvegardé

### Backend Non Démarré
Si vous obtenez une erreur de connexion :
```bash
cd backend
mvn spring-boot:run
```

### Vérifier le Token
1. Cliquer sur l'icône 👁️ en haut à droite
2. Vérifier que `jwt_token` contient une valeur
3. Si vide, re-exécuter Login

## 📊 Codes de Réponse HTTP

| Code | Signification |
|------|---------------|
| 200 | OK - Succès |
| 201 | Created - Ressource créée |
| 204 | No Content - Suppression réussie |
| 400 | Bad Request - Données invalides |
| 401 | Unauthorized - Token manquant/invalide |
| 403 | Forbidden - Accès refusé (non admin) |
| 404 | Not Found - Ressource introuvable |

## 🚀 Utilisation Avancée

### Exécuter Toute la Collection

1. Cliquer sur les 3 points `...` à côté de la collection
2. Cliquer sur **Run collection**
3. Sélectionner les requêtes à exécuter
4. Cliquer sur **Run Alten E-commerce API**

### Exporter les Résultats

1. Après avoir exécuté la collection
2. Cliquer sur **Export Results**
3. Choisir le format (JSON, CSV)

## 💡 Conseils

1. **Toujours commencer par Login** pour obtenir un token valide
2. **Utiliser l'admin** pour tester les opérations CUD sur les produits
3. **Vérifier la console Postman** pour voir les logs des scripts
4. **Sauvegarder les exemples** de réponses pour documentation

---

**Collection prête à l'emploi ! 🎉**

Importez les fichiers dans Postman et commencez à tester votre API immédiatement.
