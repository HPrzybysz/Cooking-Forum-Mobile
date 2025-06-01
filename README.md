# Dokumentacja Aplikacji Cooking Forum Mobile


## Opis ogólny
Aplikacja Cooking Forum Mobile to mobilna platforma wymiany przepisów kulinarnych z następującymi cechami:

- **Rejestracja i logowanie** użytkowników
- **Tworzenie i publikowanie** przepisów kulinarnych
- **System kategorii** dla przepisów
- **Mechanizm polubień** i ulubionych przepisów
- **Integracja z zewnętrznym API** (TheMealDB)

## Struktura bazy danych

- Diagram ER: ERDdiagram.png

### Architektura aplikacji
- architecture.png

#### Kluczowe klasy

- Aktywności:

  1. LoginActivity - zarządzanie logowaniem

  2. MainActivity - główny ekran aplikacji

  3. CreateRecipeActivity - formularz przepisu

  4. ProfileActivity - profil użytkownika

- Adaptery:

  1. RecipeAdapter - lista przepisów

  2. CategoriesAdapter - lista kategorii

- Pomocnicze:

  1. DatabaseHelper - operacje na bazie danych

  2. SessionManager - zarządzanie sesją

  3. ApiClient - komunikacja z API

#### Zarządzanie przepisami

- Tworzenie nowych przepisów

- Edycja istniejących

-   Przeglądanie przepisów

-   Filtrowanie po kategoriach

#### Interakcje społecznościowe

-   System polubień

-   Lista ulubionych

-   Przeglądanie przepisów innych użytkowników

### API zewnętrzne
#### TheMealDB

- Endpointy:

1.    GET /categories.php - pobiera listę kategorii

2.    GET /filter.php?c={category} - przepisy w kategorii

3.    GET /lookup.php?i={id} - szczegóły przepisu

### **Podział Pracy**
#### Wszystko:
- Hubert Przybysz
