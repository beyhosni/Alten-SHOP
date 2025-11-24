# Guide Docker - Alten E-commerce

## 📦 Architecture Docker

L'application est composée de 2 services :
- **Backend** : Spring Boot (Java 21) sur port 8080
- **Frontend** : Angular 18 + Nginx sur port 4200 (mappé sur port 80 du container)

## 🚀 Lancement Rapide

### Prérequis
- Docker Desktop installé et démarré
- Ports 8080 et 4200 disponibles

### Démarrer l'application

```bash
# À la racine du projet
docker-compose up --build
```

**Première fois** : Le build prendra 5-10 minutes (téléchargement des dépendances)  
**Fois suivantes** : ~2 minutes (cache Docker)

### Accéder à l'application

- **Frontend** : http://localhost:4200
- **Backend API** : http://localhost:8080
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **H2 Console** : http://localhost:8080/h2-console

### Arrêter l'application

```bash
# Arrêter les containers
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

## 🔧 Commandes Utiles

### Build et Démarrage

```bash
# Build et démarrer en mode détaché
docker-compose up -d --build

# Voir les logs
docker-compose logs -f

# Voir les logs d'un service spécifique
docker-compose logs -f backend
docker-compose logs -f frontend

# Redémarrer un service
docker-compose restart backend
```

### Gestion des Containers

```bash
# Lister les containers en cours
docker-compose ps

# Arrêter les services
docker-compose stop

# Démarrer les services arrêtés
docker-compose start

# Supprimer les containers
docker-compose down

# Supprimer containers + volumes + images
docker-compose down -v --rmi all
```

### Debug

```bash
# Accéder au shell du backend
docker exec -it alten-backend sh

# Accéder au shell du frontend
docker exec -it alten-frontend sh

# Voir les logs en temps réel
docker-compose logs -f --tail=100

# Inspecter un container
docker inspect alten-backend
```

## 📋 Structure des Fichiers Docker

```
Alten-SHOP/
├── docker-compose.yml          # Orchestration des services
├── backend/
│   ├── Dockerfile             # Image backend (multi-stage)
│   └── .dockerignore          # Fichiers à exclure
└── frontend/
    ├── Dockerfile             # Image frontend (multi-stage)
    ├── nginx.conf             # Configuration nginx
    └── .dockerignore          # Fichiers à exclure
```

## 🏗️ Détails des Dockerfiles

### Backend (Spring Boot)

**Stage 1 - Build** :
- Image : `maven:3.9-eclipse-temurin-21-alpine`
- Télécharge les dépendances Maven
- Compile l'application
- Crée le JAR

**Stage 2 - Runtime** :
- Image : `eclipse-temurin:21-jre-alpine`
- Copie uniquement le JAR
- Image finale : ~200 MB

### Frontend (Angular)

**Stage 1 - Build** :
- Image : `node:20-alpine`
- Installe les dépendances npm
- Build production Angular

**Stage 2 - Runtime** :
- Image : `nginx:alpine`
- Copie les fichiers buildés
- Configuration nginx pour SPA
- Image finale : ~50 MB

## 🔐 Variables d'Environnement

### Backend

| Variable | Valeur par défaut | Description |
|----------|-------------------|-------------|
| `SPRING_PROFILES_ACTIVE` | `docker` | Profile Spring |
| `SERVER_PORT` | `8080` | Port du serveur |
| `JWT_SECRET` | (défini) | Clé secrète JWT |
| `JWT_EXPIRATION` | `86400000` | Expiration JWT (24h) |

### Modifier les Variables

Éditez `docker-compose.yml` :

```yaml
services:
  backend:
    environment:
      - JWT_EXPIRATION=172800000  # 48h
```

## 🏥 Health Checks

Les deux services ont des health checks configurés :

**Backend** :
- Endpoint : `/actuator/health`
- Intervalle : 30s
- Timeout : 10s
- Retries : 3

**Frontend** :
- Endpoint : `/`
- Intervalle : 30s
- Timeout : 10s
- Retries : 3

Le frontend attend que le backend soit healthy avant de démarrer.

## 🌐 Réseau Docker

Les services communiquent via un réseau bridge `alten-network` :

```yaml
networks:
  alten-network:
    driver: bridge
```

Le frontend peut accéder au backend via `http://backend:8080`.

## 📊 Volumes

Actuellement, aucun volume persistant n'est configuré (base H2 en mémoire).

Pour persister les données :

```yaml
services:
  backend:
    volumes:
      - backend-data:/app/data
    environment:
      - SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/alten-db

volumes:
  backend-data:
```

## 🧪 Tester avec Postman

1. **Démarrer l'application** :
   ```bash
   docker-compose up -d
   ```

2. **Attendre que les services soient healthy** :
   ```bash
   docker-compose ps
   ```

3. **Importer la collection Postman** :
   - `Alten-Ecommerce-API.postman_collection.json`
   - `Alten-Ecommerce.postman_environment.json`

4. **Tester** :
   - Login Admin : `admin@admin.com` / `admin123`
   - Tous les endpoints sont disponibles

## 🚨 Dépannage

### Port déjà utilisé

```bash
# Vérifier les ports
netstat -ano | findstr :8080
netstat -ano | findstr :4200

# Modifier les ports dans docker-compose.yml
ports:
  - "8081:8080"  # Backend sur 8081
  - "4201:80"    # Frontend sur 4201
```

### Build échoue

```bash
# Nettoyer le cache Docker
docker system prune -a

# Rebuild sans cache
docker-compose build --no-cache
```

### Container ne démarre pas

```bash
# Voir les logs détaillés
docker-compose logs backend
docker-compose logs frontend

# Vérifier le statut
docker-compose ps
```

### Problème de mémoire

```bash
# Augmenter la mémoire Docker Desktop
# Settings → Resources → Memory → 4 GB minimum
```

## 🎯 Scénarios de Test

### Test Complet

```bash
# 1. Démarrer
docker-compose up -d

# 2. Vérifier les logs
docker-compose logs -f

# 3. Attendre "Started ShopApplication"
# 4. Ouvrir http://localhost:4200
# 5. Tester avec Postman
# 6. Arrêter
docker-compose down
```

### Rebuild après modifications

```bash
# Backend modifié
docker-compose up -d --build backend

# Frontend modifié
docker-compose up -d --build frontend

# Tout rebuild
docker-compose up -d --build
```



## 💡 Bonnes Pratiques

1. **Toujours utiliser `--build`** lors de modifications du code
2. **Vérifier les logs** avec `-f` pour voir les erreurs
3. **Utiliser `down -v`** pour nettoyer complètement
4. **Ne pas commiter** les variables sensibles
5. **Tester localement** avant de déployer

## 🚀 Production

Pour la production, créez un `docker-compose.prod.yml` :

```yaml
version: '3.8'

services:
  backend:
    image: alten-backend:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JWT_SECRET=${JWT_SECRET}  # Depuis .env
    restart: always

  frontend:
    image: alten-frontend:latest
    restart: always
```

Puis :

```bash
docker-compose -f docker-compose.prod.yml up -d
```

---

**Application dockerisée prête ! 🐳**

Lancez `docker-compose up --build` et testez sur http://localhost:4200
