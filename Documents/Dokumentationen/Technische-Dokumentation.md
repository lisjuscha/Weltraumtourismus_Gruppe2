# Technische Dokumentation

## Inhaltsverzeichnis

1.  [Einleitung und Ziele des Projekts](#1-einleitung-und-ziele-des-projekts)
2.  [Vorbedingungen](#2-vorbedingungen)
    *   [2.1 Technische Voraussetzungen](#21-technische-voraussetzungen)
    *   [2.2 Daten-Voraussetzungen für Kunden](#22-daten-voraussetzungen-für-kunden)
    *   [2.3 Daten-Voraussetzungen für Ärzte](#23-daten-voraussetzungen-für-ärzte)
3.  [Architekturüberblick](#3-architekturüberblick)
4.  [Paketstruktur](#4-paketstruktur)
    *   [com.example.flightprep.controller](#41-comexampleflightprepcontroller)
        *   [com.example.flightprep.controller.BasicController](#411-comexampleflightprepcontrollerbasiccontroller)
        *   [com.example.flightprep.controller.Customer](#412-comexampleflightprepcontrollercustomer)
        *   [com.example.flightprep.controller.Doctor](#413-comexampleflightprepcontrollerdoctor)
        *   [com.example.flightprep.controller.Login](#414-comexampleflightprepcontrollerlogin)
    *   [com.example.flightprep.dao](#42-comexampleflightprepdao)
    *   [com.example.flightprep.service](#43-comexampleflightprepservice)
    *   [com.example.flightprep.util](#44-comexampleflightpreputil)
    *   [com.example.flightprep.database](#45-comexampleflightprepdatabase)
5.  [Datenmodell](#5-datenmodell)
6.  [User Interface (UI) und User Experience (UX) Fluss](#6-user-interface-ui-und-user-experience-ux-fluss)
    *   [6.1 FXML-Dateien und zugehörige Controller](#61-fxml-dateien-und-zugehörige-controller)
        *   [6.1.1 Login-Ansicht](#611-login-ansicht)
        *   [6.1.2 Kunden-Ansichten (`CustomerScreens`)](#612-kunden-ansichten-customerscreens)
        *   [6.1.3 Arzt-Ansichten (`DocScreens`)](#613-arzt-ansichten-docscreens)
7.  [Abhängigkeiten und externe Bibliotheken](#7-abhängigkeiten-und-externe-bibliotheken)
8.  [Build- und Deployment-Prozess](#8-build--und-deployment-prozess)
9.  [Fehlerbehandlung](#9-fehlerbehandlung)
10. [Teststrategie](#10-teststrategie)
11. [Ausblick](#12-ausblick)
    *   [11.1 Architekturelle Verbesserungen](#121-architekturelle-verbesserungen)
    *   [11.2 Funktionale und Sicherheitsrelevante Erweiterungen](#122-funktionale-und-sicherheitsrelevante-erweiterungen)
12. [Glossar](#13-glossar)

## 1. Einleitung und Ziele des Projekts

Die Anwendung "FlightPrep" dient der Verwaltung und Vorbereitung von Passagieren für Weltraumflüge. Dieser Prototyp fokussiert sich auf die Digitalisierung und Vereinfachung des medizinischen Überprüfungsprozesses sowohl für Kunden (Passagiere) als auch für das medizinische Personal (Ärzte).

**Hauptziele:**

*   Erfassung medizinischer Basisdaten der Kunden mittels Fragebögen.
*   Verwaltung von Arztterminen für die Flugtauglichkeitsprüfung.
*   Hochladen und Verwalten relevanter Dokumente.
*   Unterstützung der Ärzte bei der Bewertung der Flugtauglichkeit von Kunden.
*   Feststellung und Dokumentation der Flugtauglichkeit von Kunden für weiterführende Prozesse.

**Hinweis**

Es ist zu beachten, dass im aktuellen Prototyp die Funktionalitäten der Datei-Uploads vereinfacht implementiert sind. Hochgeladene Dateien werden zwar mit einer Benutzerkennung im Dateinamen versehen und in einem zentralen Verzeichnis gespeichert, die Anzeige dieser Dokumente für den Arzt erfolgt jedoch durch Auflistung aller Dateien in diesem Verzeichnis.

**Zielgruppe:**

*   **Kunden (Passagiere):** Personen, die einen Weltraumflug gebucht haben und die notwendigen medizinischen Vorbereitungen treffen müssen.
*   **Ärzte:** Medizinisches Fachpersonal, das für die Überprüfung der medizinischen Daten und die Erteilung der Flugtauglichkeit zuständig ist.

**Prozessauslöser und -abschluss im Kontext des Prototyps:**

Der **Startpunkt** für den in "FlightPrep" abgebildeten Vorbereitungsprozess ist die Bereitstellung initialer Kundendaten in der Datenbank, typischerweise durch einen vorgelagerten Buchungsprozess. Diese Daten ermöglichen es dem Kunden, sich anzumelden und die medizinische Erfassung zu beginnen.

Das **primäre Ziel und somit das Ende des Prozesses innerhalb dieses Prototyps** ist die Feststellung der Flugtauglichkeit durch den Arzt. Dies wird dokumentiert, indem der Arzt in der `Customer`-Tabelle die Spalte `declaration` mit einem entsprechenden Wert (0 für "nicht tauglich" oder 1 für "tauglich") versieht. Dieser Datenbankeintrag dient als **Trigger und Datenbasis für potenziell nachfolgende Prozesse** (z.B. die finale Flugfreigabe, operative Flugplanung etc.), die nicht Teil dieses Prototyps sind.

## 2. Vorbedingungen

Für die erfolgreiche Nutzung und Entwicklung der "FlightPrep"-Anwendung müssen folgende Vorbedingungen erfüllt sein:

### 2.1 Technische Voraussetzungen

*   **Java Development Kit (JDK):** Eine installierte Java-Version, z.B. JDK 17 oder höher.
*   **Maven:** Apache Maven muss konfiguriert sein, um das Projekt zu bauen und Abhängigkeiten zu verwalten.
*   **Betriebssystem:** Standard-Betriebssysteme (Windows, macOS, Linux), die JavaFX unterstützen.
*   **IDE (Empfohlen):** Eine integrierte Entwicklungsumgebung wie IntelliJ IDEA oder Eclipse, konfiguriert für Java und Maven.

### 2.2 Daten-Voraussetzungen für Kunden

Da die "FlightPrep"-Anwendung auf einen vorgelagerten Buchungsprozess aufbaut, müssen für jeden Kunden, der den Vorbereitungsprozess durchlaufen soll, bestimmte Basisdaten in der Datenbank vorhanden sein:

1.  **Benutzerkonto in `User`-Tabelle:**
    *   Ein Eintrag in der `User`-Tabelle ist erforderlich.
    *   Dieser Eintrag muss eine eindeutige `user_id` (Benutzerkennung) enthalten.
    *   Das Feld `password` muss gesetzt sein.
    *   Das Feld `role` muss den Wert "Customer" haben.

2.  **Kundendetails in `Customer`-Tabelle:**
    *   Ein korrespondierender Eintrag in der `Customer`-Tabelle, verknüpft über die `user_id`, ist notwendig.
    *   Folgende Felder müssen in diesem Eintrag ausgefüllt sein (entsprechend den `NOT NULL`-Anforderungen der Datenbank):
        *   `user_id` (Fremdschlüssel user_id der `User`-Tabelle)
        *   `first_name` (Vorname des Kunden)
        *   `last_name` (Nachname des Kunden)
        *   `email` (E-Mail-Adresse des Kunden)
        *   `flight_date` (Geplantes Flugdatum des Kunden)
        *   `birth_date` (Geburtsdatum des Kunden)

Diese Daten sollten durch ein externes System (z.B. die Buchungsplattform) vor der Nutzung von "FlightPrep" bereitgestellt. Ohne diese initialen Daten kann sich ein Kunde nicht anmelden oder den medizinischen Vorbereitungsprozess starten.

### 2.3 Daten-Voraussetzungen für Ärzte

Ähnlich wie bei den Kunden, müssen auch für Ärzte bestimmte Daten initial in der Datenbank vorhanden sein, damit diese sich anmelden und ihre Aufgaben in der Anwendung wahrnehmen können.

1.  **Benutzerkonto in `User`-Tabelle:**
    *   Ein Eintrag in der `User`-Tabelle ist für den Arzt erforderlich.
    *   Dieser Eintrag muss eine eindeutige `user_id` (Benutzerkennung) enthalten.
    *   Das Feld `password` muss gesetzt sein.
    *   Das Feld `role` muss den Wert "Doctor" haben.

2.  **Arztdetails in `Doctor`-Tabelle:**
    *   Ein korrespondierender Eintrag in der `Doctor`-Tabelle, verknüpft über die `user_id`, ist notwendig.
    *   Folgende Felder müssen in diesem Eintrag ausgefüllt sein (entsprechend den `NOT NULL`-Anforderungen der Datenbank):
        *   `user_id` (Fremdschlüssel user_id der `User`-Tabelle)
        *   `first_name` (Vorname des Arztes)
        *   `last_name` (Nachname des Arztes)
        *   `email` (E-Mail-Adresse des Arztes)

**Wichtiger Hinweis zur aktuellen Implementierung:**
Die Anwendung ist derzeit für die Nutzung durch einen Arzt ausgelegt. Für Test- und Demonstrationszwecke ist folgender Arzt fest im System hinterlegt:
*   **Benutzer-ID (`user_id`):** `Juergen1`
*   **Passwort:** `123`

Ohne diese spezifischen oder entsprechend angelegten Arzt-Daten kann die Funktionalität für die Arztrolle nicht genutzt werden.

## 3. Architekturüberblick

Die Anwendung "FlightPrep" basiert auf einer etablierten Schichtenarchitektur, die an das **Model-View-Controller (MVC)**-Muster angelehnt ist. Diese Architektur wird durch eine explizite **Service-Schicht** zur Kapselung der Geschäftslogik und eine **Data Access Object (DAO)-Schicht** für den standardisierten Datenbankzugriff erweitert. Eine solche Schichtentrennung fördert die Modularität, Wartbarkeit und Testbarkeit der Anwendung.

Die Hauptkomponenten dieser Architektur sind:

*   **View (Benutzeroberfläche):**
    *   **Verantwortlichkeit:** Präsentation der Daten für den Benutzer und Entgegennahme von Benutzereingaben. In "FlightPrep" wird dies durch JavaFX und FXML-Dateien realisiert, welche die Struktur und das Layout der einzelnen Ansichten definieren.
    *   **Interaktion:** Die View interagiert direkt mit dem Controller, um Benutzeraktionen zu melden und Aktualisierungen der dargestellten Daten zu empfangen.

*   **Controller (`com.example.flightprep.controller`):
    *   **Verantwortlichkeit:** Dient als Vermittler zwischen der View und der Anwendungslogik (Service-Schicht). Empfängt Eingaben von der View, validiert diese gegebenenfalls und initiiert entsprechende Aktionen in der Service-Schicht. Nach Verarbeitung durch die Service-Schicht ist der Controller dafür zuständig, die View mit den neuen Daten oder Ergebnissen zu aktualisieren.
    *   **Interaktion:** Nimmt UI-Events entgegen, delegiert die Verarbeitung an die Service-Schicht und aktualisiert die View.

*   **Service (`com.example.flightprep.service`):
    *   **Verantwortlichkeit:** Beinhaltet die Kernlogik und die Geschäftsregeln der Anwendung. Koordiniert Datenflüsse, führt Berechnungen und Validierungen durch, die über einfache Datenmanipulation hinausgehen. Nutzt die DAO-Schicht, um auf die Datenbank zuzugreifen.
    *   **Interaktion:** Wird von den Controllern aufgerufen, um Geschäftsoperationen auszuführen. Orchestriert einen oder mehrere DAO-Aufrufe, um die benötigten Daten zu lesen oder zu schreiben.

*   **Model (`com.example.flightprep.model`):
    *   **Verantwortlichkeit:** Repräsentiert die Datenstrukturen und Entitäten der Anwendung (z.B. `User`, `Customer`, `MedicalData`). Diese Objekte werden zwischen den Schichten transportiert, insbesondere zwischen DAO, Service und Controller, um Daten zu halten und zu übergeben.
    *   **Interaktion:** Dient als Datenträger zwischen den Schichten.

*   **DAO (Data Access Object) (`com.example.flightprep.dao`):
    *   **Verantwortlichkeit:** Abstrahiert und kapselt den direkten Zugriff auf die Datenbank. Jedes DAO ist typischerweise für die Persistenzoperationen (Erstellen, Lesen, Aktualisieren, Löschen - CRUD) einer bestimmten Entität zuständig. Es verbirgt die Details der SQL-Abfragen und des Datenbankverbindungshandlings vor den höheren Schichten (insbesondere der Service-Schicht).
    *   **Interaktion:** Wird von der Service-Schicht genutzt, um Datenbankoperationen durchzuführen.

*   **Datenbank (`FlightPreperation.db`):
    *   **Verantwortlichkeit:** Die persistente Speicherung der Anwendungsdaten. In diesem Projekt wird eine lokale SQLite-Datenbank verwendet.
    *   **Interaktion:** Ausschließlich über die DAO-Schicht angesprochen.

**Typischer Ablauf einer Benutzeraktion:**

Ein typischer Ablauf beginnt in der **View**, wenn ein Benutzer eine Aktion auslöst (z.B. Klick auf einen Button). Diese Aktion wird an den zuständigen **Controller** weitergeleitet. Der Controller validiert die Eingabe und ruft eine Methode in der **Service-Schicht** auf. Der Service implementiert die dazugehörige Geschäftslogik und verwendet dafür ein oder mehrere **DAOs**, um auf die **Datenbank** zuzugreifen (Daten lesen oder schreiben). Die Ergebnisse werden dann von der DAO-Schicht an die Service-Schicht und von dort zurück an den Controller gegeben, welcher schließlich die View aktualisiert, um dem Benutzer das Ergebnis der Aktion anzuzeigen. Dieser klare Daten- und Kontrollfluss trägt zur strukturierten und wartbaren Natur der Anwendung bei.

## 4. Paketstruktur

### 4.1 com.example.flightprep.controller

Dieses Paket enthält die Controller-Klassen der Anwendung, welche die Benutzerinteraktionen aus den FXML-Views verarbeiten und als Bindeglied zur Service-Schicht fungieren. Die Unterteilung in Sub-Pakete (`Customer`, `Doctor`, `Login`, `BasicController`) dient der Strukturierung nach Benutzerrollen und grundlegenden Controller-Funktionen, was die Übersichtlichkeit und Wartbarkeit fördert.

#### 4.1.1 com.example.flightprep.controller.BasicController

Dieses Paket bündelt abstrakte Basis-Controller-Klassen, die gemeinsame Funktionalitäten (z.B. für Alerts und rollenspezifische Navigation) bereitstellen. Dies dient der Code-Wiederverwendung und Konsistenz über verschiedene spezifische Controller hinweg.

*   **`GeneralController.java`**: Eine abstrakte Klasse, die grundlegende Methoden zur Anzeige von Erfolgs-, Fehler- und Informationsmeldungen (Alerts) bereitstellt. Dies zentralisiert die Logik für Benutzerfeedback, verbessert die Wartbarkeit und sichert ein konsistentes User Experience.
*   **`CustomerController.java`**: Erweitert `GeneralController` und implementiert rollenspezifische Navigationsmethoden für Kundenansichten (z.B. Wechsel zur Home-, Vorbereitungs-, Kalender- oder "Mein Flug"-Ansicht). Dies kapselt die kundenbezogene Navigation und fördert Wiederverwendung sowie Lesbarkeit.
*   **`DocController.java`**: Erweitert `GeneralController` und implementiert rollenspezifische Navigationsmethoden für Arztansichten (z.B. Wechsel zur Home-, Kalender- oder Patienten-Ansicht), analog zum `CustomerController` für die Arztrolle.
*   **`PatientDataDisplayController.java`**: Erweitert `DocController` und ist verantwortlich für die Anzeige von medizinischen Patientendaten. Diese Klasse kapselt die UI-Logik zur Darstellung von Patientendaten, was Wiederverwendung ermöglicht, falls diese Daten in verschiedenen Arztansichten konsistent angezeigt werden sollen. Enthält `@FXML`-annotierte Labels für verschiedene medizinische Informationen und eine Methode `updateUI`.

#### 4.1.2 com.example.flightprep.controller.Customer

Dieses Sub-Paket enthält Controller-Klassen, die spezifisch für die Kundenansichten und -aktionen zuständig sind.

*   **`CustomerSurveyController.java`**: Verwaltet die Logik für den medizinischen Fragebogen, den Kunden ausfüllen müssen. Dies beinhaltet die Validierung der Eingaben und das Speichern der Daten.
*   **`CustomerPrepController.java`**: Steuert die Hauptansicht für Kunden, die den Fortschritt ihrer Vorbereitungen anzeigt (Fragebogen ausgefüllt, Termin gebucht, Dokumente hochgeladen). Ermöglicht die Navigation zu den entsprechenden Sektionen unter bestimmten Bedingungen.
*   **`CustomerAppointmentController.java`**: Kümmert sich um die Terminbuchung für Kunden. Zeigt verfügbare Zeitfenster an und ermöglicht es Kunden, einen Termin auszuwählen und zu buchen. Nach erfolgreicher Buchung wird zur Kalenderansicht navigiert.
*   **`CustomerCalendarController.java`**: Steuert die Kalenderansichtfür den Kunden. Zeigt Details zum medizinischen Termin und zum Flugdatum an und ermöglicht dem Kunden, sowohl den Arzttermin als auch das Flugdatum zu ändern, indem zu den entsprechenden Änderungsansichten navigiert wird.
*   **`CustomerChangeFlightDateController.java`**: Verantwortlich für die Benutzeroberfläche und Logik, die es Kunden ermöglicht, ein neues Flugdatum  auszuwählen. Das System validiert das ausgewählte Datum (darf nicht in der Vergangenheit liegen und muss ggf. einen Mindestabstand zu einem bestehenden Arzttermin einhalten). Änderungen werden gespeichert und der Nutzer wird zurück zur Kalenderansicht geleitet. Fehlermeldungen und Erfolgsbestätigungen werden über zentrale Alert-Dialoge angezeigt.
*   **`CustomerHomeController.java`**: Ein einfacher Controller, der möglicherweise als Basis oder für eine allgemeine Kunden-Startseite dient. Aktuell enthält er keine spezifische Logik.
*   **`CustomerUploadController.java`**: Ermöglicht Kunden das Hochladen von erforderlichen Dokumenten. Beinhaltet Funktionen zur Dateiauswahl, Validierung der Dateigröße und zum Speichern der Dateien.

#### 4.1.3 com.example.flightprep.controller.Doctor

Dieses Sub-Paket beinhaltet Controller für die Arztansichten und -funktionalitäten.

*   **`DocPatientsController.java`**: Zeigt eine Liste aller Patienten an, deren Unterlagen zur Prüfung bereitstehen. Ermöglicht dem Arzt, die Zusammenfassung eines Patienten einzusehen.
*   **`DocCalendarController.java`**: Stellt einen Kalender dar, der die gebuchten Termine der Patienten anzeigt. Termine sind farblich nach Risikogruppe markiert. Ermöglicht die Navigation zwischen Wochen und das Aufrufen der Patientendaten.
*   **`DocPatientSummaryController.java`**: Zeigt eine detaillierte Zusammenfassung der medizinischen Daten eines spezifischen Patienten an, inklusive der hochgeladenen Dokumente. Ermöglicht dem Arzt, die Flugtauglichkeit zu bestätigen oder abzulehnen und einen Kommentar hinzuzufügen.
*   **`DocHomeController.java`**: Ein Basis-Controller für die Startansicht des Arztes. Aktuell ohne spezifische Funktionalität.
*   **`DocPatientDataController.java`**: Verantwortlich für die Anzeige der medizinischen Daten eines Patienten. Wird verwendet, um die Daten in der Benutzeroberfläche darzustellen.

#### 4.1.4 com.example.flightprep.controller.Login

Dieses Sub-Paket ist für den Anmeldevorgang zuständig.

*   **`LoginController.java`**: Handhabt die Benutzeranmeldung. Authentifiziert Benutzer anhand ihrer ID und ihres Passworts. Leitet Benutzer je nach ihrer Rolle (Arzt oder Kunde) zur entsprechenden Startseite weiter.

### 4.2 com.example.flightprep.dao

Dieses Paket implementiert die Data Access Object (DAO)-Schicht und enthält Klassen für den direkten Datenbankzugriff. Jede DAO-Klasse ist für die Persistenzoperationen einer spezifischen Entität (z.B. `CustomerDAO` für Kundendaten) zuständig und kapselt die dafür notwendigen SQL-Abfragen und das Datenbank-Handling.

*   **`CustomerDAO.java`**: Verantwortlich für Datenbankoperationen im Zusammenhang mit Kunden. Beinhaltet Methoden zum Abrufen von Kunden mit hochgeladenen Dateien, Speichern von Flugtauglichkeitserklärungen, Aktualisieren der Risikogruppe eines Kunden, Aktualisieren des Datei-Upload-Status, Aktualisieren des Termin-Status, Abrufen des Termin-Status, Aktualisieren und Abrufen des Fragebogen-Status, Abrufen des Flugdatums sowie Aktualisieren des Flugdatums.
*   **`UserDAO.java`**: Zuständig für Datenbankoperationen im Zusammenhang mit Benutzern (sowohl Kunden als auch Ärzte). Enthält Methoden zum Abrufen eines Benutzers anhand der Benutzer-ID, wobei zwischen Kunden und Ärzten unterschieden wird und die entsprechenden spezifischen Daten geladen werden.
*   **`AppointmentDAO.java`**: Handhabt Datenbankoperationen für Termine. Bietet Methoden, um zu prüfen, ob ein Zeitfenster bereits gebucht ist, einen neuen Termin zu buchen, Termine für ein bestimmtes Datum abzurufen und Termine anhand der Kunden-ID abzurufen.
*   **`MedicalDataDAO.java`**: Verantwortlich für das Speichern und Abrufen von medizinischen Daten der Kunden. Enthält Methoden zum Speichern neuer oder zum Ersetzen vorhandener medizinischer Daten und zum Abrufen medizinischer Daten anhand der Benutzer-ID.

### 4.3 com.example.flightprep.service

Dieses Paket bildet die Service-Schicht der Anwendung. Es enthält Klassen, welche die Geschäftslogik kapseln und als Zwischenschicht zwischen Controllern und DAOs dienen, um Geschäftsregeln zu zentralisieren und wiederverwendbar zu machen. Einige Services sind als Singletons implementiert (z.B. `CustomerService`), um die Verwaltung zustandsloser Logik zu vereinfachen.

*   **`UserService.java`**: Stellt Logik für Benutzeroperationen bereit, insbesondere die Authentifizierung von Benutzern. Verwendet `UserDAO`, um Benutzerdaten abzurufen und Passwörter zu überprüfen.
*   **`CustomerService.java`**: Bietet eine Singleton-Instanz für kundenbezogene Geschäftslogik. Nutzt `CustomerDAO`, `UserDAO` und `MedicalDataDAO` für Operationen wie das Abrufen von Kunden, das Einreichen medizinischer Daten (inklusive Risikoklassifizierung durch `RiskClassifierAI`), das Abrufen medizinischer Daten, das Speichern von Flugtauglichkeitserklärungen, das Auflisten von Patientendokumenten, das Aktualisieren des Flugdatums mit Validierung und das Aktualisieren verschiedener Kundenstatus.
*   **`AppointmentService.java`**: Eine Singleton-Klasse, die für die Geschäftslogik rund um Termine zuständig ist. Verwendet `AppointmentDAO` und `CustomerDAO`. Hauptfunktionen umfassen das Buchen von Terminen (nach Validierung des Zeitfensters), das Überprüfen der Gültigkeit eines Terminslots (unter Berücksichtigung von Vergangenheit, Flugdatum und bereits gebuchten Terminen), das Abrufen verfügbarer Zeitfenster, das Zusammenstellen von Terminen für eine Woche und das Abrufen von Terminen für einen bestimmten Kunden. Bietet auch eine Methode zur farblichen Kennzeichnung von Risikogruppen.
*   **`FileUploadService.java`**: Kümmert sich um die Logik des Datei-Uploads. Definiert Upload- und temporäre Verzeichnisse sowie eine maximale Dateigröße. Methoden umfassen das Verschieben von temporären Dateien in das endgültige Upload-Verzeichnis (wobei dem Dateinamen die Benutzer-ID vorangestellt wird), das Speichern einer hochgeladenen Datei in einem temporären Verzeichnis mit Zeitstempel und die Validierung der Dateigröße. Stellt auch sicher, dass die notwendigen Verzeichnisse existieren.

### 4.4 com.example.flightprep.util

Dieses Paket enthält Utility-Klassen, die allgemeine Hilfsfunktionen für verschiedene Teile der Anwendung bereitstellen. Die Bündelung solcher Helferklassen fördert die Code-Wiederverwendung, entlastet andere Pakete von Detailaufgaben und verbessert somit die allgemeine Lesbarkeit und Wartbarkeit des Codes.

*   **`RiskClassifierAI.java`**: Eine einfache "KI" zur Klassifizierung des Risikos von Patienten basierend auf ihrem BMI (Body Mass Index). Berechnet den BMI aus Größe und Gewicht und gibt eine Risikogruppe (1, 2 oder 3) zurück. Enthält eine Hilfsmethode zum Parsen von String-Werten zu Double.
*   **`SceneSwitcher.java`**: Stellt statische Methoden zum Wechseln von Szenen in der JavaFX-Anwendung bereit. Ermöglicht das Laden neuer FXML-Dateien und das Setzen als Wurzel der aktuellen Szene oder das Erstellen einer neuen Szene in einem neuen Stage. Wendet auch ein Standard-CSS-Stylesheet an.
*   **`SessionManager.java`**: Verwaltet die aktuelle Benutzersitzung. Speichert den angemeldeten Benutzer (`currentUser`) und die ID eines ausgewählten Patienten (`selectedPatientId`). Bietet statische Methoden zum Setzen und Abrufen dieser Informationen sowie zum Löschen der Sitzung.
*   **`RadioButtonReader.java`**:  Eine Hilfsklasse mit einer statischen Methode `getToggleGroupBoolean`, die den ausgewählten RadioButton in einer `ToggleGroup` liest und `true` zurückgibt, wenn der Text des Buttons "Yes" ist, andernfalls `false`.

### 4.5 com.example.flightprep.database

Dieses Paket ist für die Einrichtung und Verwaltung der Datenbankverbindung zuständig. Es stellt die notwendigen Klassen bereit, um eine Verbindung zur SQLite-Datenbank herzustellen und zu verwalten, wobei ein Interface (`DatabaseConnection`) für Flexibilität und eine Factory (`DatabaseFactory`) für die zentrale Erzeugung der Verbindung genutzt werden.

*   **`DatabaseConnection.java`**: Ein Interface, das Methoden für das Abrufen (`getConnection`) und Schließen (`closeConnection`) einer Datenbankverbindung definiert. Dies ermöglicht eine lose Kopplung und potenzielle Erweiterbarkeit für andere Datenbanktypen.
*   **`SQLiteConnection.java`**: Implementiert das `DatabaseConnection`-Interface und stellt eine Verbindung zu einer SQLite-Datenbank her (`FlightPreperation.db` im `data`-Verzeichnis). Lädt den SQLite JDBC-Treiber, deaktiviert Auto-Commit für Transaktionsmanagement und bietet eine Methode zum Schließen der Verbindung (inklusive Rollback bei nicht-committeten Änderungen).
*   **`DatabaseFactory.java`**: Eine Factory-Klasse, die eine Singleton-Instanz von `DatabaseConnection` (spezifisch `SQLiteConnection`) bereitstellt. Dies zentralisiert die Erzeugung des Datenbankverbindungsobjekts.

## 5. Datenmodell

Das Datenmodell der Anwendung "FlightPrep" ist in einer lokalen SQLite-Datenbank (`FlightPreperation.db`) realisiert und umfasst Tabellen für Benutzer (User), Kunden (Customer), Ärzte (Doctor), Termine (appointments) und medizinische Daten (medical_data). Diese Struktur ermöglicht die Verwaltung der Benutzerrollen, die Erfassung von Kundendetails und medizinischen Informationen sowie die Organisation von Arztterminen.
Eine detaillierte Beschreibung des Datenmodells, inklusive eines Entity-Relationship-Diagramms und genauer Tabellendefinitionen, finden Sie im separaten Dokument: [Datenmodell](./Datenmodell/Datenmodell.md).

## 6. User Interface (UI) und User Experience (UX) Fluss

Die Anwendung bietet separate Oberflächen für Kunden und Ärzte.

**Kunden-Fluss (Beispielhaft):**

1.  **Login:** Kunde meldet sich mit Benutzer-ID und Passwort an.
2.  **Home-Bildschirm:** Übersicht über den aktuellen Status der Flugvorbereitung - nicht implementiert
3.  **Medizinischer Fragebogen:** Ausfüllen und Absenden des Fragebogens.
4.  **Terminbuchung/Kalenderansicht:** Anzeige des Flugdatums und des medizinischen Termins. Möglichkeit zur Buchung oder Änderung des medizinischen Termins und zur Änderung des Flugdatums.
5.  **Dokumenten-Upload:** Hochladen erforderlicher medizinischer Dokumente.
6.  **Mein Flug:** (Potenzielle Ansicht mit Flugdetails – aktuell nicht detailliert implementiert).

**Arzt-Fluss (Beispielhaft):**

1.  **Login:** Arzt meldet sich mit Benutzer-ID und Passwort an.
2.  **Home-Bildschirm:** Dashboard mit relevanten Informationen - nicht implementiert
3.  **Kalenderansicht:** Übersicht über gebuchte Termine. 
4.  **Patientenübersicht:** Liste der Patienten mit eingereichten Unterlagen.
5. **Patienten-Detailansicht/Akte:** Einsicht in medizinische Daten und hochgeladene Dokumente eines Patienten.
6. **Flugtauglichkeitserklärung:** Abgabe der Bewertung (tauglich/nicht tauglich) mit Kommentar.


Die Navigation erfolgt über Menüs und Schaltflächen, die den Benutzer durch die notwendigen Schritte leiten. `SceneSwitcher.java` ist die zentrale Utility-Klasse für den Wechsel zwischen verschiedenen Ansichten (FXML-Dateien).

### 6.1 FXML-Dateien und zugehörige Controller

Die Benutzeroberfläche der Anwendung ist durch FXML-Dateien definiert, die sich im Verzeichnis `src/main/resources/com/example/flightprep/` befinden. Jede FXML-Datei ist mit einem Controller-Klasse im Paket `com.example.flightprep.controller` verbunden, die die Logik der jeweiligen Ansicht steuert.

#### 6.1.1 Login-Ansicht

*   **`Login.fxml`**
    *   **Beschreibung:** Stellt das Anmeldefenster für Benutzer (Kunden und Ärzte) dar. Ermöglicht die Eingabe von Benutzer-ID und Passwort.
    *   **Controller:** `com.example.flightprep.controller.Login.LoginController`

#### 6.1.2 Kunden-Ansichten (`CustomerScreens`)

*   **`CustomerHome.fxml`**
    *   **Beschreibung:** Eine Platzhalteransicht für die Startseite für angemeldete Kunden.
    *   **Controller:** `com.example.flightprep.controller.Customer.CustomerHomeController`
*   **`CustomerPrep1.fxml`**
    *   **Beschreibung:** Zeigt den Fortschritt der Flugvorbereitung des Kunden in verschiedenen Bereichen an (z.B. Status des Fragebogens, des Termins und der Datei-Uploads).
    *   **Controller:** `com.example.flightprep.controller.Customer.CustomerPrepController`
*   **`CustomerSurvey.fxml`**
    *   **Beschreibung:** Stellt den medizinischen Fragebogen dar, den Kunden ausfüllen müssen.
    *   **Controller:** `com.example.flightprep.controller.Customer.CustomerSurveyController`
*   **`CustomerAppointment.fxml`**
    *   **Beschreibung:** Ermöglicht Kunden die Ansicht und Buchung von verfügbaren Arztterminen.
    *   **Controller:** `com.example.flightprep.controller.Customer.CustomerAppointmentController`
*   **`CustomerCalendar.fxml`**
    *   **Beschreibung:** Zeigt dem Kunden medizinische Termindetails und Fluginformationen in einer Kalenderansicht. Ermöglicht das Ändern des Arzttermins und des Flugdatums.
    *   **Controller:** `com.example.flightprep.controller.Customer.CustomerCalendarController`
*   **`CustomerChangeFlightDate.fxml`**
    *   **Beschreibung:** Stellt ein Formular mit `DatePicker` zur Änderung des Flugdatums bereit. Interagiert mit `CustomerChangeFlightDateController` für Validierung und Speicherung.
    *   **Controller:** `com.example.flightprep.controller.Customer.CustomerChangeFlightDateController`
*   **`CustomerUpload.fxml`**
    *   **Beschreibung:** Ermöglicht Kunden das Hochladen von erforderlichen Dokumenten.
    *   **Controller:** `com.example.flightprep.controller.Customer.CustomerUploadController`
*   **`CustomerMyFlight.fxml`**
    *   **Beschreibung:** Eine Platzhalteransicht für zukünftige Informationen zum Flug des Kunden.
    *   **Controller:** `com.example.flightprep.controller.Customer.CustomerMyFlightController`

#### 6.1.3 Arzt-Ansichten (`DocScreens`)

*   **`DocHome.fxml`**
    *   **Beschreibung:** Die Startseite für angemeldete Ärzte.
    *   **Controller:** `com.example.flightprep.controller.Doctor.DocHomeController`
*   **`DocCalendar.fxml`**
    *   **Beschreibung:** Zeigt dem Arzt eine Kalenderübersicht der gebuchten Patiententermine.
    *   **Controller:** `com.example.flightprep.controller.Doctor.DocCalendarController`
*   **`DocPatients.fxml`**
    *   **Beschreibung:** Listet Patienten auf, deren Unterlagen zur Prüfung bereitstehen. Ermöglicht den Zugriff auf Patientendetails und leitet den Prozess zur Feststellung der Flugtauglichkeit ein.
    *   **Controller:** `com.example.flightprep.controller.Doctor.DocPatientsController`
*   **`DocPatientData.fxml`**
    *   **Beschreibung:** Zeigt detaillierte medizinische Daten eines ausgewählten Patienten an (aus dem Fragebogen).
    *   **Controller:** `com.example.flightprep.controller.Doctor.DocPatientDataController`
*   **`DocPatientSummary.fxml`**
    *   **Beschreibung:** Bietet eine Zusammenfassung der Patientendaten, einschließlich hochgeladener Dokumente, und ermöglicht dem Arzt die Abgabe der Flugtauglichkeitserklärung.
    *   **Controller:** `com.example.flightprep.controller.Doctor.DocPatientSummaryController`

## 7. Abhängigkeiten und externe Bibliotheken

Die Anwendung "FlightPrep" nutzt Maven für das Dependency Management. Die zentralen Abhängigkeiten, wie in der `pom.xml` definiert, umfassen:

*   **JavaFX (`org.openjfx`):** Version `${javafx.version}` (aktuell 17.0.15)
    *   `javafx-controls`: Stellt die UI-Steuerelemente bereit.
    *   `javafx-fxml`: Ermöglicht das Laden von FXML-Dateien zur Definition der UI-Struktur.
*   **SQLite JDBC Driver (`org.xerial`):** Version `3.49.1.0`
    *   `sqlite-jdbc`: Wird für die Kommunikation mit der SQLite-Datenbank benötigt.
*   **JUnit Jupiter (`org.junit.jupiter`):** Version `${junit.version}` (aktuell 5.10.2)
    *   `junit-jupiter-api` und `junit-jupiter-engine`: Standard-Framework für die Durchführung von Unit-Tests in Java.
*   **Mockito (`org.mockito`):** Versionen um `5.5.0`
    *   `mockito-core`, `mockito-junit-jupiter`, `mockito-inline`: Dienen zur Erstellung von Mock-Objekten und zur Integration mit JUnit, um Abhängigkeiten in Tests zu isolieren.
*   **TestFX (`org.testfx`):** Version `4.0.17`
    *   `testfx-core` und `testfx-junit5`: Frameworks zur Durchführung von UI-Tests für JavaFX-Anwendungen.

Zusätzlich wird das **JaCoCo Maven Plugin (`org.jacoco`)** Version `${jacoco.version}` (aktuell 0.8.12) im Build-Prozess verwendet, um die Code-Testabdeckung zu analysieren.

Die Wahl von JavaFX erfolgte aufgrund der Anforderungen an eine Desktop-Anwendung mit grafischer Benutzeroberfläche. SQLite wurde als leichtgewichtige, dateibasierte Datenbank gewählt, die keine separate Serverinstallation erfordert und gut für Prototypen und kleinere Anwendungen geeignet ist. Die Testbibliotheken ermöglichen eine strukturierte Überprüfung der Anwendungslogik und der Benutzeroberfläche.

## 8. Build- und Deployment-Prozess

**Build:**

Die Anwendung wird mit Apache Maven gebaut. Der Build-Prozess wird durch die `pom.xml` gesteuert.

*   **Kompilierung:** Der `maven-compiler-plugin` (Version 3.13.0) wird verwendet, um den Java-Quellcode (Source und Target Version 17) zu kompilieren.
*   **JavaFX-spezifischer Build:** Der `javafx-maven-plugin` (Version 0.0.8) wird für JavaFX-spezifische Aufgaben genutzt, wie z.B. das Ausführen der Anwendung (`mvn clean javafx:run`). Er kann auch für das Erstellen eines JLink-Images oder eines nativen Installers konfiguriert werden (aktuell konfiguriert für `jlinkZipName: app`, `jlinkImageName: app`).

**Deployment/Ausführung:**

*   **Voraussetzungen:** Eine Java Runtime Environment (JRE) Version 17 oder höher ist erforderlich.
*   **Start:** Die Anwendung kann direkt über die IDE gestartet werden (Main-Klasse: `com.example.flightprep.Main`) oder via Maven mit dem Befehl `mvn clean javafx:run`.
*   Für ein eigenständiges Deployment könnte ein JLink-Image oder ein nativer Installer mit dem `javafx-maven-plugin` erstellt werden.

## 9. Fehlerbehandlung

**Fehlerbehandlung:**

*   Die `GeneralController`-Klasse stellt zentrale Methoden (`showError`, `showSuccess`, `createAlert`) zur Anzeige von Dialogfenstern (Alerts) für Benutzerfeedback bei Fehlern, erfolgreichen Operationen oder zur Informationsdarstellung bereit.
*   Spezifische Controller-Klassen nutzen diese Methoden, um den Benutzer über den Ausgang von Aktionen zu informieren.
*   Datenbankoperationen in den DAOs werfen `SQLException`s, die von den Service-Klassen gefangen und behandelt oder an die Controller weitergegeben werden, um dem Benutzer eine entsprechende Meldung anzuzeigen.
*   Bei kritischen Fehlern (z.B. Laden von FXML-Dateien) werden StackTraces auf der Konsole ausgegeben (`e.printStackTrace()`).

## 10. Teststrategie

Die Teststrategie der Anwendung zielt darauf ab, die korrekte Funktionalität auf verschiedenen Ebenen sicherzustellen. Hierfür werden folgende Ansätze und Werkzeuge genutzt:

*   **Unit-Tests:** Mit JUnit Jupiter (Version `${junit.version}`) werden einzelne Komponenten und Methoden isoliert getestet. Mockito (Versionen um `5.5.0`) wird eingesetzt, um Abhängigkeiten zu simulieren und so fokussierte Tests der Geschäftslogik in Service-Klassen und die Datenzugriffslogik in DAOs zu ermöglichen.
*   **UI-Tests:** TestFX (Version `4.0.17`) wird verwendet, um die Interaktion mit der JavaFX-Benutzeroberfläche zu automatisieren und das Verhalten der Controller-Klassen im Zusammenspiel mit den FXML-Views zu überprüfen.
*   **Code Coverage:** Das JaCoCo-Maven-Plugin (Version `${jacoco.version}`) wird eingesetzt, um die Testabdeckung des Codes zu messen. Die generierten Berichte (siehe Abbildung) helfen dabei, nicht getestete Bereiche der Anwendung zu identifizieren und die Testqualität kontinuierlich zu verbessern.

Die Tests befinden sich im Verzeichnis `src/test/java`. Ziel ist es, eine hohe Testabdeckung zu erreichen, um die Stabilität und Zuverlässigkeit der Anwendung zu gewährleisten.

![image](https://github.com/user-attachments/assets/8701f271-b5f9-49ec-841f-79409e15e554)

## 11. Ausblick

Obwohl der aktuelle Prototyp die Kernanforderungen für die medizinische Vorbereitung von Passagieren erfolgreich abbildet, eröffnet der Entwicklungsprozess stets Möglichkeiten für zukünftige Erweiterungen und Verfeinerungen. Die folgende Aufstellung identifiziert potenzielle Bereiche, in denen die Anwendung hinsichtlich Architektur, Funktionalität und Sicherheit weiterentwickelt werden könnte, um sie für einen umfassenderen oder produktiven Einsatz noch robuster, wartbarer und benutzerfreundlicher zu gestalten:

### 11.1 Architekturelle Verbesserungen

*   **Logging-Framework:**
    *   Einführung eines dedizierten Logging-Frameworks (z.B. Log4j, SLF4j). Dies ermöglicht eine strukturierte, konfigurierbare und flexiblere Protokollierung von Anwendungsereignissen, Warnungen und Fehlern, was die Diagnose und Wartung erheblich verbessert.
*   **DAO-Abstraktion und Generics:**
    *   Obwohl die DAO-Schicht bereits existiert, könnte eine weitere Abstraktionsebene durch ein generisches DAO-Interface und eine Basisimplementierung eingeführt werden. Dies würde redundanten Code in den spezifischen DAOs reduzieren und die Wartbarkeit erhöhen.
*   **Konfigurationsmanagement:**
    *   Auslagerung von Konfigurationsparametern wie Datenbankpfaden, Upload-Verzeichnissen und maximalen Dateigrößen in externe Konfigurationsdateien (z.B. `.properties` oder `.yaml`-Dateien). Dies erhöht die Flexibilität beim Deployment und der Anpassung an verschiedene Umgebungen.

### 11.2 Funktionale und Sicherheitsrelevante Erweiterungen

*   **Passwortsicherheit:**
    *   Implementierung von Passwort-Hashing (z.B. mit bcrypt oder Argon2) anstelle der Speicherung von Klartext-Passwörtern. Dies ist ein kritischer Schritt zur Absicherung von Benutzerkonten.
*   **Datenbanksicherheit:**
    *   Für sensible medizinische Daten könnte eine Verschlüsselung der Datenbankdatei selbst oder der Datenbankinhalte in Betracht gezogen werden, falls dies von der gewählten Datenbanktechnologie unterstützt wird und die Performance-Auswirkungen akzeptabel sind.
*   **Datei-Uploads:**
    *   **Speicherort:** Erwägung, hochgeladene Dateien direkt in der Datenbank (z.B. als BLOBs) zu speichern, anstatt im Dateisystem. Um so weitere Funtionalität zu gewähren, die Uploads kundenspezifisch anzuzeigen.

Diese potenziellen Verbesserungen würden die Funkrionalität, Sicherheit, Wartbarkeit, Robustheit und Konfigurierbarkeit der Anwendung steigern.

## 12. Glossar

*   **DAO (Data Access Object):** Ein Entwurfsmuster, das eine abstrakte Schnittstelle zu einer Datenbank oder einem anderen persistenten Speichermechanismus bereitstellt.
*   **DBML (Database Markup Language):** Eine einfache, menschenlesbare Sprache zur Definition von Datenbankschemata.
*   **ERD (Entity-Relationship-Diagramm):** Eine grafische Darstellung der Entitäten in einer Datenbank und der Beziehungen zwischen ihnen.
*   **FXML (FX Markup Language):** Eine XML-basierte Sprache zur Definition von JavaFX-Benutzeroberflächen.
*   **JDBC (Java Database Connectivity):** Eine Java-API, die den Zugriff auf relationale Datenbanken ermöglicht.
*   **JRE (Java Runtime Environment):** Die Laufzeitumgebung, die zum Ausführen von Java-Anwendungen benötigt wird.
*   **Maven:** Ein Build-Automatisierungs- und Projektmanagement-Werkzeug.
*   **MVC (Model-View-Controller):** Ein Software-Architekturmuster, das die Anwendungslogik von der Benutzeroberfläche trennt.
*   **UI (User Interface):** Benutzeroberfläche.
*   **UX (User Experience):** Benutzererfahrung.
*   **Singleton:** Ein Entwurfsmuster, das sicherstellt, dass von einer Klasse nur ein einziges Objekt existiert.
*   **BMI:** Body Mass Index - eine Maßzahl zur Bewertung des Körpergewichts.
*   **FXML:** XML-basiertes Format zur Beschreibung von JavaFX-Benutzeroberflächen.
*   **JDBC:** Java Database Connectivity - eine API für den Zugriff auf Datenbanken aus Java.
*   **Singleton:** Ein Entwurfsmuster, das sicherstellt, dass von einer Klasse nur ein einziges Objekt existiert.
*   **DAO:** Data Access Object - ein Entwurfsmuster, das den Zugriff auf eine Datenquelle kapselt.
*   **Maven:** Ein Build-Automatisierungs- und Projektmanagement-Tool.
*   **BMI:** Body Mass Index - eine Maßzahl zur Bewertung des Körpergewichts.
*   **ERD:** Entity-Relationship-Diagramm - eine grafische Darstellung von Entitäten und deren Beziehungen in einem Datenmodell.
*   **DBML:** Database Markup Language - eine einfache Sprache zur Definition von Datenbankschemata.
