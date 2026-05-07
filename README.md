# Lab 14 : Persistance Locale Sécurisée sous Android (Java)

Ce projet implémente une solution complète de stockage local pour Android, couvrant différents types de persistance tout en respectant les meilleures pratiques de sécurité.

## Objectifs d'apprentissage
- Utilisation des **SharedPreferences** pour les paramètres non sensibles.
- Mise en œuvre de **EncryptedSharedPreferences** pour sécuriser les secrets (tokens).
- Gestion du stockage interne (fichiers texte UTF-8 et JSON via `org.json`).
- Gestion du cache temporaire et purge manuelle.
- Exportation de fichiers vers le stockage externe spécifique à l'application.
- Application d'une checklist sécurité (évitement des logs sensibles, `MODE_PRIVATE`).

## Fonctionnalités
1. **Préférences Standards** : Enregistre le nom, la langue et le thème.
2. **Stockage Chiffré** : Utilise `androidx.security:security-crypto` pour chiffrer l'API Token sur le disque.
3. **Fichiers Internes** :
   - `students.json` : Liste d'objets `Student` sérialisée.
   - `note.txt` : Journal d'opérations en UTF-8.
4. **Gestion du Cache** : Stockage de l'état de l'interface dans le répertoire cache.
5. **Nettoyage Complet** : Bouton permettant de supprimer toutes les données locales (fichiers, cache et préférences).

## Structure du Projet
- `ui/MainActivity.java` : Contrôleur principal de l'interface.
- `prefs/` : Classes `AppPrefs` et `SecurePrefs`.
- `files/` : Classes `InternalTextStore` et `StudentsJsonStore`.
- `cache/` : Classe `CacheStore`.
- `external/` : Classe `ExternalAppFilesStore`.
- `model/` : Modèle de données `Student`.

## Vérification
- **Logcat** : Vérifiez les étiquettes `SecureStorageJava` pour suivre les opérations sans fuite de secrets.
- **Device File Explorer** :
  - Données : `/data/data/com.example.lab14mob/files/`
  - Préférences : `/data/data/com.example.lab14mob/shared_prefs/`
  - Cache : `/data/data/com.example.lab14mob/cache/`

## Dépendances
- AndroidX AppCompat
- AndroidX Security Crypto (`1.1.0-alpha06`)
- Google Material Design


https://github.com/user-attachments/assets/dd3dbe2c-93e6-4396-ad04-20bfa403b0af

