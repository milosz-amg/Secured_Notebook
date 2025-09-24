## 📄README
# Secured Notebook
**Secured Notebook** to aplikacja mobilna (Android) przeznaczona do bezpiecznego przechowywania notatek. Dane notatek są zabezpieczone mechanizmami szyfrowania, aby chronić prywatność użytkownika.

---

## Funkcje (plany / założenia)

- Tworzenie, edycja i usuwanie notatek  
- Zabezpieczenie notatek hasłem / kluczem szyfrowania  
- Szyfrowana baza danych (lokalna)  
- (Opcjonalnie) synchronizacja zaszyfrowanych danych z serwerem lub backup  
- Intuicyjny i prosty interfejs użytkownika  

---

## Architektura i struktura

├── .idea/                   # Konfiguracje IDE
├── app/                     # Kod aplikacji Android
├── gradle/                  # Wrapper Gradle
├── build.gradle.kts         # Konfiguracja Gradle (Kotlin DSL)
├── gradle.properties
├── gradlew / gradlew\.bat    # Skrypty uruchomienia
└── settings.gradle.kts


- Kod aplikacji znajduje się w katalogu `app/`.  
- Gradle służy do budowania projektu — konfiguracja w `build.gradle.kts`.  
- Projekt może być rozszerzony o moduły takie jak testy jednostkowe, warstwa sieciowa, synchronizacja itp.

---

## Jak uruchomić

1. Sklonuj repozytorium  
   ```bash
   git clone https://github.com/milosz-amg/Secured_Notebook.git
   cd Secured_Notebook


2. Otwórz projekt w Android Studio
3. Upewnij się, że masz zainstalowaną odpowiednią wersję SDK Android
4. Zbuduj i uruchom aplikację na emulatorze lub urządzeniu fizycznym
5. (Jeśli wymagane) skonfiguruj hasło / klucz szyfrowania przy pierwszym uruchomieniu

---

## Technologie użyte / narzędzia

* Android SDK / Android Studio
* Java
* Gradle
* (W przyszłości) biblioteki kryptograficzne do szyfrowania danych

---

## Wyzwania / pytania do rozstrzygnięcia

* Który algorytm szyfrowania będzie użyty (AES, ChaCha20, itp.)?
* Gdzie przechowywać klucz szyfrowania (Klucz użytkownika, keystore Androida)?
* Czy wspierać synchronizację między urządzeniami?
* Jak obsłużyć backup danych i ich odzyskiwanie?
* Jak zabezpieczyć integralność danych / wykrywanie modyfikacji?

---

## Licencja
MIT, Apache 2.0
