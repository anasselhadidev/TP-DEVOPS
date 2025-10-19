# Module Artwork

Ce module implémente la gestion des œuvres d'art pour la plateforme Infinitum Art.

## Structure du Module

```
Artwork/
├── controller/
│   └── ArtworkController.java
├── DTO/
│   ├── ArtworkResponse.java
│   └── ArtworkListResponse.java
├── modele/
│   └── Artwork.java
├── repo/
│   └── ArtworkRepo.java
├── service/
│   └── ArtworkService.java
└── README.md
```

## Modèle Artwork

La classe `Artwork` contient les champs suivants :
- `id`: Clé primaire
- `title`: Titre de l'œuvre
- `description`: Description détaillée
- `imageUrl`: URL de l'image
- `price`: Prix (BigDecimal)
- `isForSale`: Indique si l'œuvre est à vendre
- `creationDate`: Date de création
- `artist`: Relation avec l'artiste (Users)
- `client`: Relation avec le client (Users)
- `commissionId`: ID de la commission (optionnel)
- `viewCount`: Nombre de vues
- `favoriteCount`: Nombre de favoris

## Endpoints API

### 1. Top This Week
**GET** `/api/homepage/top-this-week`

Retourne les œuvres les plus populaires basées sur un score pondéré :
- Score = (viewCount * 0.7) + (favoriteCount * 0.3)

**Paramètres :**
- `page` (default: 0): Numéro de page
- `size` (default: 10, max: 50): Taille de la page

**Réponse :**
```json
{
  "artworks": [...],
  "currentPage": 0,
  "totalPages": 1,
  "totalElements": 8,
  "size": 10
}
```

### 2. Discover More
**GET** `/api/homepage/discover-more`

Retourne des œuvres sélectionnées aléatoirement pour la découverte.

**Paramètres :**
- `page` (default: 0): Numéro de page
- `size` (default: 10, max: 50): Taille de la page

### 3. Detailed Artwork Information
**GET** `/api/artwork/{artworkId}`

Retourne les détails complets d'une œuvre spécifique et incrémente automatiquement le compteur de vues.

**Réponse :**
```json
{
  "id": 1,
  "title": "Starry Night",
  "description": "A masterpiece depicting the night sky...",
  "imageUrl": "https://example.com/starry-night.jpg",
  "price": 1500000.00,
  "isForSale": false,
  "creationDate": "1889-06-01T00:00:00",
  "artist": {
    "id": 2,
    "username": "Vincent Van Gogh",
    "email": "vincent@art.com",
    "imageUrl": null
  },
  "client": null,
  "commissionId": null,
  "viewCount": 1501,
  "favoriteCount": 89
}
```

### 4. Artworks by Artist
**GET** `/api/artwork/artist/{artistId}`

Retourne toutes les œuvres d'un artiste spécifique.

### 5. Artworks by Client
**GET** `/api/artwork/client/{clientId}`

Retourne toutes les œuvres commandées par un client spécifique.

### 6. Artworks for Sale
**GET** `/api/artwork/for-sale`

Retourne toutes les œuvres disponibles à la vente.

## Fonctionnalités

### Calcul du Score de Popularité
Le système utilise un algorithme pondéré pour déterminer les œuvres les plus populaires :
- 70% du poids pour le nombre de vues
- 30% du poids pour le nombre de favoris

### Incrémentation Automatique des Vues
Chaque fois qu'une œuvre est consultée via l'endpoint `/api/artwork/{artworkId}`, le compteur de vues est automatiquement incrémenté.

### Pagination
Tous les endpoints de liste supportent la pagination avec les paramètres `page` et `size`.

### Validation
- Les paramètres de pagination sont validés (page ≥ 0, 1 ≤ size ≤ 50)
- Les IDs d'œuvres sont validés et retournent une erreur 404 si non trouvés

## Données de Test

Le fichier `data.sql` contient des données de test avec :
- 3 artistes célèbres (Van Gogh, Picasso, Da Vinci)
- 2 clients
- 8 œuvres d'art avec différents niveaux de popularité

## Sécurité

Tous les endpoints sont publics et ne nécessitent pas d'authentification pour la consultation des œuvres d'art.

