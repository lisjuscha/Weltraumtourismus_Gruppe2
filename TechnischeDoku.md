# Technische Dokumentation

## Inhaltsverzeichnis

1.  [Einleitung und Ziele des Projekts](#1-einleitung-und-ziele-des-projekts)
2.  [Architekturüberblick](#2-architekturüberblick)
3.  [Paketstruktur](#3-paketstruktur)
    *   [com.example.flightprep.controller](#31-comexampleflightprepcontroller)
        *   [com.example.flightprep.controller.BasicController](#311-comexampleflightprepcontrollerbasiccontroller)
        *   [com.example.flightprep.controller.Customer](#312-comexampleflightprepcontrollercustomer)
        *   [com.example.flightprep.controller.Doctor](#313-comexampleflightprepcontrollerdoctor)
        *   [com.example.flightprep.controller.Login](#314-comexampleflightprepcontrollerlogin)
    *   [com.example.flightprep.dao](#32-comexampleflightprepdao)
    *   [com.example.flightprep.service](#33-comexampleflightprepservice)
    *   [com.example.flightprep.util](#34-comexampleflightpreputil)
    *   [com.example.flightprep.database](#35-comexampleflightprepdatabase)
4.  [Datenmodell (Detaillierter)](#4-datenmodell-detaillierter)
    *   [Tabelle: User](#41-tabelle-user)
    *   [Tabelle: Customer](#42-tabelle-customer)
    *   [Tabelle: Doctor](#43-tabelle-doctor)
    *   [Tabelle: medical_data](#44-tabelle-medical_data)
    *   [Tabelle: appointments](#45-tabelle-appointments)
5.  [User Interface (UI) und User Experience (UX) Fluss](#5-user-interface-ui-und-user-experience-ux-fluss)
6.  [Abhängigkeiten und externe Bibliotheken](#6-abhängigkeiten-und-externe-bibliotheken)
7.  [Build- und Deployment-Prozess](#7-build--und-deployment-prozess)
8.  [Fehlerbehandlung und Logging](#8-fehlerbehandlung-und-logging)
9.  [Sicherheitsaspekte](#9-sicherheitsaspekte)
10. [Konfiguration](#10-konfiguration)
11. [Teststrategie](#11-teststrategie)
12. [Glossar](#12-glossar)

## 1. Einleitung und Ziele des Projekts

Die Anwendung "FlightPrep" dient der Verwaltung und Vorbereitung von Passagieren für Weltraumflüge. Ziel ist es, den Prozess der medizinischen Überprüfung und Dokumentensammlung sowohl für Passagiere (Kunden) als auch für das medizinische Personal (Ärzte) zu digitalisieren und zu vereinfachen.

**Hauptziele:**

*   Erfassung medizinischer Basisdaten und Fragebögen von Kunden.
*   Verwaltung von Arztterminen für die Flugtauglichkeitsprüfung.
*   Sicheres Hochladen und Verwalten relevanter Dokumente.
*   Unterstützung der Ärzte bei der Bewertung der Flugtauglichkeit von Kunden.
*   Bereitstellung einer klaren Übersicht über den Vorbereitungsstatus für Kunden.

**Zielgruppe:**

*   **Kunden (Passagiere):** Personen, die einen Weltraumflug gebucht haben und die notwendigen medizinischen Vorbereitungen treffen müssen.
*   **Ärzte:** Medizinisches Fachpersonal, das für die Überprüfung der medizinischen Daten und die Erteilung der Flugtauglichkeit zuständig ist.

## 2. Architekturüberblick

Die Anwendung folgt im Wesentlichen einer **Model-View-Controller (MVC)** ähnlichen Architektur, erweitert um eine klare **Service-Schicht** und eine **Data Access Object (DAO)-Schicht**.

*   **View (Benutzeroberfläche):** Realisiert mit JavaFX und FXML. Definiert die Darstellung der Daten und die Interaktionsmöglichkeiten für den Benutzer. Die FXML-Dateien befinden sich im `resources` Verzeichnis (`src/main/resources/com/example/flightprep`).
*   **Controller (`com.example.flightprep.controller`):** Verbindet die View mit der Anwendungslogik. Nimmt Benutzereingaben entgegen, validiert diese und delegiert die Verarbeitung an die Service-Schicht. Aktualisiert die View basierend auf den Ergebnissen.
*   **Service (`com.example.flightprep.service`):** Kapselt die Geschäftslogik der Anwendung. Koordiniert die DAOs und führt komplexere Operationen aus. Dient als Schnittstelle zwischen Controllern und DAOs.
*   **Model (`com.example.flightprep.model`):** Repräsentiert die Datenstrukturen der Anwendung (z.B. `User`, `Customer`, `MedicalData`).
*   **DAO (`com.example.flightprep.dao`):** Ist verantwortlich für den direkten Zugriff auf die Datenbank. Kapselt alle SQL-Abfragen und Datenbankinteraktionen.
*   **Datenbank (`FlightPreperation.db`):** Eine SQLite-Datenbank zur persistenten Speicherung der Anwendungsdaten.

Ein typischer Ablauf einer Benutzeraktion ist: `View -> Controller -> Service -> DAO -> Datenbank` und zurück.

## 3. Paketstruktur

### 3.1 com.example.flightprep.controller

Dieses Paket ist verantwortlich für die Handhabung der Benutzerinteraktionen und die Steuerung des Anwendungsflusses. Es enthält Sub-Pakete für verschiedene Benutzerrollen und Funktionalitäten.

#### 3.1.1 com.example.flightprep.controller.BasicController

Dieses Paket enthält abstrakte Basis-Controller-Klassen, die gemeinsame Funktionalitäten für andere Controller bereitstellen.

*   **`GeneralController.java`**: Eine abstrakte Klasse, die grundlegende Methoden zur Anzeige von Erfolgs- und Fehler, Erfolgs und Informationsmeldungen(Alerts) für alle Controller bereitstellt.
*   **`CustomerController.java`**: Erweitert `GeneralController` und implementiert Navigationsmethoden, die spezifisch für Kundenansichten sind (z.B. Wechsel zur Home-, Vorbereitungs-, Kalender- oder "Mein Flug"-Ansicht).
*   **`DocController.java`**: Erweitert `GeneralController` und implementiert Navigationsmethoden, die spezifisch für Arztansichten sind (z.B. Wechsel zur Home-, Kalender- oder Patienten-Ansicht).
*   **`PatientDataDisplayController.java`**: Erweitert `DocController` und ist verantwortlich für die Anzeige von medizinischen Patientendaten. Enthält `@FXML`-annotierte Labels für verschiedene medizinische Informationen und eine Methode `updateUI`, um diese Labels mit den Daten eines `MedicalData`-Objekts zu befüllen. Diese Überklasse stellt die Funtionalität für Controller breit, die Daten bestimmter Kunden anzuzeigen.

#### 3.1.2 com.example.flightprep.controller.Customer

Dieses Sub-Paket enthält Controller-Klassen, die spezifisch für die Kundenansichten und -aktionen zuständig sind.

*   **`CustomerSurveyController.java`**: Verwaltet die Logik für den medizinischen Fragebogen, den Kunden ausfüllen müssen. Dies beinhaltet die Validierung der Eingaben und das Speichern der Daten.
*   **`CustomerPrepController.java`**: Steuert die Hauptansicht für Kunden, die den Fortschritt ihrer Vorbereitungen anzeigt (Fragebogen ausgefüllt, Termin gebucht, Dokumente hochgeladen). Ermöglicht die Navigation zu den entsprechenden Sektionen.
*   **`CustomerAppointmentController.java`**: Kümmert sich um die Terminbuchung für Kunden. Zeigt verfügbare Zeitfenster an und ermöglicht es Kunden, einen Termin auszuwählen und zu buchen.
*   **`CustomerHomeController.java`**: Ein einfacher Controller, der möglicherweise als Basis oder für eine allgemeine Kunden-Startseite dient. Aktuell enthält er keine spezifische Logik.
*   **`CustomerUploadController.java`**: Ermöglicht Kunden das Hochladen von erforderlichen Dokumenten. Beinhaltet Funktionen zur Dateiauswahl, Validierung der Dateigröße und zum Speichern der Dateien.

#### 3.1.3 com.example.flightprep.controller.Doctor

Dieses Sub-Paket beinhaltet Controller für die Arztansichten und -funktionalitäten.

*   **`DocPatientsController.java`**: Zeigt eine Liste aller Patienten an, die ihre Dokumente hochgeladen haben. Ermöglicht dem Arzt, die Zusammenfassung eines Patienten einzusehen.
*   **`DocCalendarController.java`**: Stellt einen Kalender dar, der die gebuchten Termine der Patienten anzeigt. Termine sind farblich nach Risikogruppe markiert. Ermöglicht die Navigation zwischen Wochen und das Aufrufen der Patientendaten.
*   **`DocPatientSummaryController.java`**: Zeigt eine detaillierte Zusammenfassung der medizinischen Daten eines spezifischen Patienten an, inklusive der hochgeladenen Dokumente. Ermöglicht dem Arzt, die Flugtauglichkeit zu bestätigen oder abzulehnen und einen Kommentar hinzuzufügen.
*   **`DocHomeController.java`**: Ein Basis-Controller für die Startansicht des Arztes. Aktuell ohne spezifische Funktionalität.
*   **`DocPatientDataController.java`**: Verantwortlich für die Anzeige der medizinischen Daten eines Patienten. Wird verwendet, um die Daten in der Benutzeroberfläche darzustellen.

#### 3.1.4 com.example.flightprep.controller.Login

Dieses Sub-Paket ist für den Anmeldevorgang zuständig.

*   **`LoginController.java`**: Handhabt die Benutzeranmeldung. Authentifiziert Benutzer anhand ihrer ID und ihres Passworts. Leitet Benutzer je nach ihrer Rolle (Arzt oder Kunde) zur entsprechenden Startseite weiter.

### 3.2 com.example.flightprep.dao

Dieses Paket enthält Data Access Object (DAO) Klassen, die für die Interaktion mit der Datenbank zuständig sind. Jede DAO-Klasse ist typischerweise für eine bestimmte Entität oder einen bestimmten Aspekt der Daten verantwortlich.

*   **`CustomerDAO.java`**: Verantwortlich für Datenbankoperationen im Zusammenhang mit Kunden. Beinhaltet Methoden zum Abrufen von Kunden mit hochgeladenen Dateien, Speichern von Flugtauglichkeitserklärungen, Aktualisieren der Risikogruppe eines Kunden, Aktualisieren des Datei-Upload-Status, Aktualisieren des Termin-Status, Abrufen des Termin-Status, Aktualisieren und Abrufen des Fragebogen-Status sowie Abrufen des Flugdatums.
*   **`UserDAO.java`**: Zuständig für Datenbankoperationen im Zusammenhang mit Benutzern (sowohl Kunden als auch Ärzte). Enthält Methoden zum Abrufen eines Benutzers anhand der Benutzer-ID, wobei zwischen Kunden und Ärzten unterschieden wird und die entsprechenden spezifischen Daten geladen werden.
*   **`AppointmentDAO.java`**: Handhabt Datenbankoperationen für Termine. Bietet Methoden, um zu prüfen, ob ein Zeitfenster bereits gebucht ist, einen neuen Termin zu buchen und Termine für ein bestimmtes Datum abzurufen.
*   **`MedicalDataDAO.java`**: Verantwortlich für das Speichern und Abrufen von medizinischen Daten der Kunden. Enthält Methoden zum Speichern neuer oder zum Ersetzen vorhandener medizinischer Daten und zum Abrufen medizinischer Daten anhand der Benutzer-ID.

### 3.3 com.example.flightprep.service

Dieses Paket beinhaltet Service-Klassen, die die Geschäftslogik der Anwendung kapseln. Sie agieren als Zwischenschicht zwischen den Controllern und den DAOs.

*   **`UserService.java`**: Stellt Logik für Benutzeroperationen bereit, insbesondere die Authentifizierung von Benutzern. Verwendet `UserDAO`, um Benutzerdaten abzurufen und Passwörter zu überprüfen.
*   **`CustomerService.java`**: Bietet eine Singleton-Instanz für kundenbezogene Geschäftslogik. Nutzt `CustomerDAO`, `UserDAO` und `MedicalDataDAO` für Operationen wie das Abrufen von Kunden, das Einreichen medizinischer Daten (inklusive Risikoklassifizierung durch `RiskClassifierAI`), das Abrufen medizinischer Daten, das Speichern von Flugtauglichkeitserklärungen, das Auflisten von Patientendokumenten und das Aktualisieren verschiedener Kundenstatus.
*   **`AppointmentService.java`**: Eine Singleton-Klasse, die für die Geschäftslogik rund um Termine zuständig ist. Verwendet `AppointmentDAO` und `CustomerDAO`. Hauptfunktionen umfassen das Buchen von Terminen (nach Validierung des Zeitfensters), das Überprüfen der Gültigkeit eines Terminslots (unter Berücksichtigung von Vergangenheit, Flugdatum und bereits gebuchten Terminen), das Abrufen verfügbarer Zeitfenster und das Zusammenstellen von Terminen für eine Woche. Bietet auch eine Methode zur farblichen Kennzeichnung von Risikogruppen.
*   **`FileUploadService.java`**: Kümmert sich um die Logik des Datei-Uploads. Definiert Upload- und temporäre Verzeichnisse sowie eine maximale Dateigröße. Methoden umfassen das Verschieben von temporären Dateien in das endgültige Upload-Verzeichnis (wobei dem Dateinamen die Benutzer-ID vorangestellt wird), das Speichern einer hochgeladenen Datei in einem temporären Verzeichnis mit Zeitstempel und die Validierung der Dateigröße. Stellt auch sicher, dass die notwendigen Verzeichnisse existieren.

### 3.4 com.example.flightprep.util

Dieses Paket enthält Utility-Klassen, die Hilfsfunktionen für verschiedene Teile der Anwendung bereitstellen.

*   **`RiskClassifierAI.java`**: Eine einfache "KI" zur Klassifizierung des Risikos von Patienten basierend auf ihrem BMI (Body Mass Index). Berechnet den BMI aus Größe und Gewicht und gibt eine Risikogruppe (1, 2 oder 3) zurück. Enthält eine Hilfsmethode zum Parsen von String-Werten zu Double.
*   **`SceneSwitcher.java`**: Stellt statische Methoden zum Wechseln von Szenen in der JavaFX-Anwendung bereit. Ermöglicht das Laden neuer FXML-Dateien und das Setzen als Wurzel der aktuellen Szene oder das Erstellen einer neuen Szene in einem neuen Stage. Wendet auch ein Standard-CSS-Stylesheet an.
*   **`SessionManager.java`**: Verwaltet die aktuelle Benutzersitzung. Speichert den angemeldeten Benutzer (`currentUser`) und die ID eines ausgewählten Patienten (`selectedPatientId`). Bietet statische Methoden zum Setzen und Abrufen dieser Informationen sowie zum Löschen der Sitzung.
*   **`RadioButoonReader.java`**: Eine Hilfsklasse mit einer statischen Methode `getToggleGroupBoolean`, die den ausgewählten RadioButton in einer `ToggleGroup` liest und `true` zurückgibt, wenn der Text des Buttons "Yes" ist, andernfalls `false`.

### 3.5 com.example.flightprep.database

Dieses Paket ist für die Einrichtung und Verwaltung der Datenbankverbindung zuständig.

*   **`SQLiteConnection.java`**: Implementiert das `DatabaseConnection`-Interface und stellt eine Verbindung zu einer SQLite-Datenbank her (`FlightPreperation.db` im `data`-Verzeichnis). Lädt den SQLite JDBC-Treiber, deaktiviert Auto-Commit für Transaktionsmanagement und bietet eine Methode zum Schließen der Verbindung (inklusive Rollback bei nicht-committeten Änderungen).
*   **`DatabaseConnection.java`**: Ein Interface, das Methoden für das Abrufen (`getConnection`) und Schließen (`closeConnection`) einer Datenbankverbindung definiert. Dies ermöglicht eine lose Kopplung und potenzielle Erweiterbarkeit für andere Datenbanktypen.
*   **`DatabaseFactory.java`**: Eine Factory-Klasse, die eine Singleton-Instanz von `DatabaseConnection` (spezifisch `SQLiteConnection`) bereitstellt. Dies zentralisiert die Erzeugung des Datenbankverbindungsobjekts.

## 4. Datenmodell (Detaillierter)

Die Datenbank `FlightPreperation.db` scheint die folgenden Tabellen zu enthalten:

### 4.1 Tabelle: User

Speichert allgemeine Benutzerinformationen.

*   `user_id` (TEXT, PRIMARY KEY): Eindeutige ID des Benutzers.
*   `password` (TEXT): Passwort des Benutzers.
*   `role` (TEXT): Rolle des Benutzers (z.B. "customer", "doctor").

### 4.2 Tabelle: Customer

Speichert spezifische Informationen für Kunden, erweitert die `User`-Tabelle.

*   `user_id` (TEXT, PRIMARY KEY, FOREIGN KEY references `User(user_id)`): Eindeutige ID des Kunden.
*   `first_name` (TEXT): Vorname des Kunden.
*   `last_name` (TEXT): Nachname des Kunden.
*   `email` (TEXT): E-Mail-Adresse des Kunden.
*   `form_submitted` (BOOLEAN): Status, ob der medizinische Fragebogen eingereicht wurde.
*   `appointment_made` (BOOLEAN): Status, ob ein Termin gebucht wurde.
*   `file_uploaded` (BOOLEAN): Status, ob Dateien hochgeladen wurden.
*   `flight_date` (TEXT): Flugdatum des Kunden (Format: "dd.MM.yyyy").
*   `risk_group` (INTEGER): Zugeordnete Risikogruppe des Kunden.
*   `declaration` (BOOLEAN, NULLABLE): Flugtauglichkeitserklärung des Arztes (true/false).
*   `comment` (TEXT, NULLABLE): Kommentar des Arztes zur Flugtauglichkeit.

### 4.3 Tabelle: Doctor

Speichert spezifische Informationen für Ärzte, erweitert die `User`-Tabelle.

*   `user_id` (TEXT, PRIMARY KEY, FOREIGN KEY references `User(user_id)`): Eindeutige ID des Arztes.
*   `first_name` (TEXT): Vorname des Arztes.
*   `last_name` (TEXT): Nachname des Arztes.
*   `email` (TEXT): E-Mail-Adresse des Arztes.

### 4.4 Tabelle: medical_data

Speichert die detaillierten medizinischen Informationen, die von Kunden im Fragebogen angegeben werden.

*   `user_id` (TEXT, PRIMARY KEY, FOREIGN KEY references `User(user_id)`): ID des Kunden, zu dem die Daten gehören.
*   `height` (TEXT): Größe des Kunden.
*   `weight` (TEXT): Gewicht des Kunden.
*   `alcohol_consumption` (TEXT): Angaben zum Alkoholkonsum.
*   `smoking_status` (TEXT): Angaben zum Rauchstatus.
*   `training_status` (BOOLEAN): Angaben zur sportlichen Betätigung.
*   `disability_status` (BOOLEAN): Angaben zu Behinderungen.
*   `disability_details` (TEXT, NULLABLE): Details zu Behinderungen.
*   `heart_disease` (BOOLEAN): Angaben zu Herzerkrankungen.
*   `high_blood_pressure` (BOOLEAN): Angaben zu Bluthochdruck.
*   `irregular_heartbeat` (BOOLEAN): Angaben zu Herzrhythmusstörungen.
*   `stroke_history` (BOOLEAN): Angaben zu Schlaganfall in der Vorgeschichte.
*   `asthma` (BOOLEAN): Angaben zu Asthma.
*   `lung_disease` (BOOLEAN): Angaben zu Lungenerkrankungen.
*   `seizure_history` (BOOLEAN): Angaben zu Krampfanfällen in der Vorgeschichte.
*   `neurological_disorder` (BOOLEAN): Angaben zu neurologischen Erkrankungen.
*   `hsp_respiratory_cardio` (BOOLEAN): Angaben zu Krankenhausaufenthalten wegen Atemwegs-/Herz-Kreislauf-Erkrankungen.
*   `hsp_heart_lung` (BOOLEAN): Angaben zu Krankenhausaufenthalten wegen Herz-/Lungenerkrankungen.
*   `persc_med` (BOOLEAN): Angaben zur Einnahme verschreibungspflichtiger Medikamente.
*   `allergies` (BOOLEAN): Angaben zu Allergien.
*   `surgery` (BOOLEAN): Angaben zu Operationen.
*   `ser_injury` (BOOLEAN): Angaben zu schweren Verletzungen.

### 4.5 Tabelle: appointments

Speichert Informationen über gebuchte Termine.

*   `appointment_id` (INTEGER, PRIMARY KEY): Eindeutige ID des Termins.
*   `customer_id` (TEXT, FOREIGN KEY references `Customer(user_id)`): ID des Kunden, der den Termin gebucht hat.
*   `doctor_id` (TEXT, FOREIGN KEY references `Doctor(user_id)`): ID des Arztes, bei dem der Termin stattfindet.
*   `date` (TEXT): Datum des Termins (Format: "dd.MM.yyyy").
*   `time` (TEXT): Uhrzeit des Termins (Format: "HH:mm").

*Hinweis: Ein Entity-Relationship-Diagramm (ERD) könnte hier zur besseren Visualisierung der Beziehungen zwischen den Tabellen eingefügt werden.*

## 5. User Interface (UI) und User Experience (UX) Fluss

Die Anwendung bietet separate Oberflächen für Kunden und Ärzte.

**Kunden-Fluss (Beispielhaft):**

1.  **Login:** Kunde meldet sich mit Benutzer-ID und Passwort an.
2.  **Home-Bildschirm:** Übersicht über den aktuellen Status der Flugvorbereitung.
3.  **Medizinischer Fragebogen:** Ausfüllen und Absenden des Fragebogens.
4.  **Terminbuchung:** Auswahl eines verfügbaren Termins beim Arzt.
5.  **Dokumenten-Upload:** Hochladen erforderlicher medizinischer Dokumente.
6.  **Mein Flug:** (Potenzielle Ansicht mit Flugdetails – aktuell nicht detailliert implementiert).

**Arzt-Fluss (Beispielhaft):**

1.  **Login:** Arzt meldet sich mit Benutzer-ID und Passwort an.
2.  **Home-Bildschirm:** Dashboard mit relevanten Informationen.
3.  **Patientenübersicht:** Liste der Patienten mit eingereichten Unterlagen.
4.  **Patienten-Detailansicht/Akte:** Einsicht in medizinische Daten und hochgeladene Dokumente eines Patienten.
5.  **Flugtauglichkeitserklärung:** Abgabe der Bewertung (tauglich/nicht tauglich) mit Kommentar.
6.  **Kalenderansicht:** Übersicht über gebuchte Termine.

Die Navigation erfolgt über Menüs und Schaltflächen, die den Benutzer durch die notwendigen Schritte leiten. `SceneSwitcher.java` ist die zentrale Utility-Klasse für den Wechsel zwischen verschiedenen Ansichten (FXML-Dateien).

## 6. Abhängigkeiten und externe Bibliotheken

Die Anwendung nutzt Maven für das Dependency Management. Die wichtigsten Abhängigkeiten sind in der `pom.xml` definiert:

*   **JavaFX (`org.openjfx`):**
    *   `javafx-controls` (Version 17.0.15): Für die UI-Steuerelemente.
    *   `javafx-fxml` (Version 17.0.15): Für das Laden von FXML-Dateien zur Definition der UI-Struktur.
*   **SQLite JDBC Driver (`org.xerial`):**
    *   `sqlite-jdbc` (Version 3.49.1.0): Treiber für die Kommunikation mit der SQLite-Datenbank.
*   **JUnit (`org.junit.jupiter`):** (Für Tests)
    *   `junit-jupiter-api` (Version 5.10.2)
    *   `junit-jupiter-engine` (Version 5.10.2)
    *   `junit-jupiter` (Version 5.10.0)
*   **Mockito (`org.mockito`):** (Für Tests)
    *   `mockito-core` (Version 5.5.0)
    *   `mockito-junit-jupiter` (Version 5.5.0)

Die Wahl von JavaFX erfolgte aufgrund der Anforderungen an eine Desktop-Anwendung mit grafischer Benutzeroberfläche. SQLite wurde als leichtgewichtige, dateibasierte Datenbank gewählt, die keine separate Serverinstallation erfordert.

## 7. Build- und Deployment-Prozess

**Build:**

Die Anwendung wird mit Apache Maven gebaut. Der Build-Prozess wird durch die `pom.xml` gesteuert.

*   **Kompilierung:** Der `maven-compiler-plugin` (Version 3.13.0) wird verwendet, um den Java-Quellcode (Source und Target Version 17) zu kompilieren.
*   **JavaFX-spezifischer Build:** Der `javafx-maven-plugin` (Version 0.0.8) wird für JavaFX-spezifische Aufgaben genutzt, wie z.B. das Ausführen der Anwendung (`mvn clean javafx:run`). Er kann auch für das Erstellen eines JLink-Images oder eines nativen Installers konfiguriert werden (aktuell konfiguriert für `jlinkZipName: app`, `jlinkImageName: app`).

**Deployment/Ausführung:**

*   **Voraussetzungen:** Eine Java Runtime Environment (JRE) Version 17 oder höher ist erforderlich.
*   **Start:** Die Anwendung kann direkt über die IDE gestartet werden (Main-Klasse: `com.example.flightprep.Main`) oder via Maven mit dem Befehl `mvn clean javafx:run`.
*   Für ein eigenständiges Deployment könnte ein JLink-Image oder ein nativer Installer mit dem `javafx-maven-plugin` erstellt werden.

## 8. Fehlerbehandlung und Logging

**Fehlerbehandlung:**

*   Die `GeneralController`-Klasse stellt zentrale Methoden (`showError`, `showSuccess`, `createAlert`) zur Anzeige von Dialogfenstern (Alerts) für Benutzerfeedback bei Fehlern, erfolgreichen Operationen oder zur Informationsdarstellung bereit.
*   Spezifische Controller-Klassen nutzen diese Methoden, um den Benutzer über den Ausgang von Aktionen zu informieren.
*   Datenbankoperationen in den DAOs werfen `SQLException`s, die von den Service-Klassen gefangen und behandelt oder an die Controller weitergegeben werden, um dem Benutzer eine entsprechende Meldung anzuzeigen.
*   Bei kritischen Fehlern (z.B. Laden von FXML-Dateien) werden StackTraces auf der Konsole ausgegeben (`e.printStackTrace()`).

**Logging:**

*   Ein dediziertes Logging-Framework (wie Log4j oder SLF4j) scheint derzeit nicht explizit verwendet zu werden. Fehler- und Statusmeldungen werden primär über `System.err.println()` (siehe `SQLiteConnection`) oder `e.printStackTrace()` ausgegeben, was für die Entwicklungsphase nützlich ist, aber für eine Produktionsumgebung durch ein strukturiertes Logging ersetzt werden sollte.

## 9. Sicherheitsaspekte

*   **Authentifizierung:** Die Benutzerauthentifizierung erfolgt in `UserService` durch Vergleich der eingegebenen Benutzer-ID und des Passworts mit den in der Datenbank gespeicherten Werten. **Wichtiger Hinweis:** Das Speichern und Vergleichen von Klartext-Passwörtern (`password.equals(user.getPassword())`) stellt ein erhebliches Sicherheitsrisiko dar. Passwörter sollten immer gehasht (z.B. mit bcrypt oder Argon2) und gesalzen gespeichert werden.
*   **Datenzugriff:** Der Zugriff auf Patientendaten ist auf authentifizierte Ärzte beschränkt. Die Rollenprüfung (`user.getRole()`) im `LoginController` steuert den Zugriff auf verschiedene Anwendungsbereiche.
*   **Datenspeicherung:** Medizinische Daten sind sensible Informationen. Die SQLite-Datenbankdatei (`FlightPreperation.db`) wird lokal gespeichert. Bei einem Deployment in einer produktiven Umgebung müssten Maßnahmen zum Schutz dieser Datei vor unbefugtem Zugriff erwogen werden (z.B. Verschlüsselung des Dateisystems oder der Datenbank selbst, falls unterstützt).
*   **Datei-Uploads:** Der `FileUploadService` prüft die Dateigröße (`isValidFileSize`). Es könnten weitere Validierungen (z.B. Dateityp-Überprüfung anhand des Inhalts statt nur der Erweiterung) implementiert werden, um das Hochladen schädlicher Dateien zu verhindern. Die Dateinamen werden beim Speichern um die `userId` ergänzt, um Kollisionen zu vermeiden.

## 10. Konfiguration

*   **Datenbankpfad:** Der Pfad zur SQLite-Datenbank (`jdbc:sqlite:data/FlightPreperation.db`) ist derzeit fest im Code (`SQLiteConnection.java`) hinterlegt. Für mehr Flexibilität könnte dieser Pfad in eine externe Konfigurationsdatei ausgelagert werden.
*   **Upload-/Temp-Verzeichnisse:** Pfade für Datei-Uploads (`data/uploads`) und temporäre Dateien (`data/temp`) sind in `FileUploadService.java` ebenfalls fest kodiert und könnten konfigurierbar gemacht werden.
*   **Maximale Dateigröße:** Die maximale Dateigröße für Uploads (`MAX_FILE_SIZE`) ist in `FileUploadService.java` definiert.

## 11. Teststrategie

*(Dieser Abschnitt ist vorerst leer und kann später mit Details zur Testabdeckung, verwendeten Testarten und Testwerkzeugen gefüllt werden.)*

## 12. Glossar

*   **DAO (Data Access Object):** Ein Entwurfsmuster, das eine abstrakte Schnittstelle zu einer Datenbank oder einem anderen persistenten Speichermechanismus bereitstellt.
*   **FXML (FX Markup Language):** Eine XML-basierte Sprache zur Definition von JavaFX-Benutzeroberflächen.
*   **JDBC (Java Database Connectivity):** Eine Java-API, die den Zugriff auf relationale Datenbanken ermöglicht.
*   **JRE (Java Runtime Environment):** Die Laufzeitumgebung, die zum Ausführen von Java-Anwendungen benötigt wird.
*   **Maven:** Ein Build-Automatisierungs- und Projektmanagement-Werkzeug.
*   **MVC (Model-View-Controller):** Ein Software-Architekturmuster, das die Anwendungslogik von der Benutzeroberfläche trennt.
*   **UI (User Interface):** Benutzeroberfläche.
*   **UX (User Experience):** Benutzererfahrung.