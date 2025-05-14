# Gruppenprojekt:

![image](https://github.com/user-attachments/assets/21a6c44c-2092-4c24-8d7c-811c3bbbff62)

HWG Ludwigshafen IBAIT23 ATdIT 2, Projekt Gruppe 2

Die Entwicklung einer Software für die Abstimmung zwischen unserer Firma und dem Touristen hinsichtlich der Planung und Vorbereitung für seinen Flug ins Weltall. 

Lehrbeauftragte: Andreas Heck und Patrick Gutgesell

Gruppen Mitglieder: Daniel Justo Blazquez Perez, Luc Alex Hampele, Philipp Lennard Rohr, Lisa Schmidt, Joline Zeidler

# 1. Modellierung
*1.1 Allgemeine Einführung in die Modellierungsdateien*
    
Alle genannten Dateien und Modelle bilden die konzeptionelle Grundlage für unseren Entwicklungsprozess im Rahmen der Flugvorbereitung.
Verzeichnis der wichtigsten Dateien Wir möchten Ihnen ein Verzeichnis der Dateien zur Verfügung stellen, die die Grundlage für unsere Implementierung bilden.    
     
*1.1.1 Prozesse*   
Um eine Software zur Flugvorbereitung in den Weltraumn effektiv zu entwickeln, müssen wir zunächst die wichtigsten Prozesse und Softwareelemente modellieren.
Die BPMN-Modelle zur Veranschaulichung unserer Prozesse befinden sich im Ordner Documents/BPMN.   
Eine ausführliche Erläuterung dazu finden Sie in der Datei https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/tree/686ce58a3d2e6ebcade29221d088397603dd6806/Documents/Prozessdiagramme.
     
*1.1.2 Datenmodell*  
Das Datenmodell, das die Struktur unserer Datenbank abbildet - einschließlich der Gesundheitsdaten der Tourist*innen und relevanter Verwaltungsinformationen - ist im Ordner data/FlightPreperation.db hinterlegt.   
Eine detaillierte Beschreibung finden Sie in data/FlightPreperation.db/README.md.  
     
*1.1.3 UI-Modelle*  
Die verschiedenen UI-Modelle sind im Ordner Documents/UI abgelegt. Dieser ist nochmal in die zwei Unterordner für die verschiedenen Perspektiven der Nutzer unterteilt, den Tourist und den Arzt .
Eine bebilderte Erläuterung der Benutzeroberfläche finden Sie in Documents/UI/README.md.  
     
Im Rahmen unseres Weltraumtourismus-Projekts haben wir uns entschieden, den Medical-Check-Prozess umzusetzen. Dieser Prozess ist Teil der Flugvorbereitung und dient der Erfassung von Gesundheitsdaten potenzieller Weltraumtouristinnen. Vor dem Antritt ihres Raumflugs geben die Teilnehmerinnen relevante medizinische Informationen an, die zur Einschätzung der Flugtauglichkeit erforderlich sind. Anschließend wird automatisch ein Termin mit einer Ärztin oder einem Arzt in unserem Unternehmen vereinbart, um eine persönliche Untersuchung durchzuführen und die finale Freigabe für den Flug zu erteilen. Weitere Informationen zur Modellierung finden Sie in den oben genannten README-Dateien.
BPMN: Documents/"placeholder"
UI: Documents/UI
Datenmodell: Documents/"placeholder"

# 2. Programmierung
*2.1 Codeübersicht und Dateistruktur*   
   
Der Quellcode unseres Projekts befindet sich im Verzeichnis Code/“placeholder“.  
In diesem Ordner liegen alle relevanten Dateien, die zur Ausführung der Anwendung benötigt werden. Eine ausführliche Anleitung zur Nutzung und zum Starten des Projekts finden Sie weiter unten. Für einen reibungslosen Einstieg empfehlen wir, zunächst die README.md-Datei im Hauptverzeichnis des Code-Ordners zu lesen. Dort finden Sie alle notwendigen Informationen zur Architektur, zu Abhängigkeiten und zum  Aufbau der Anwendung.

*2.2 Projektumfang*   
   
Im Rahmen unseres Projekts haben wir den Medical-Check-Prozess für zukünftige Weltraumtourist*innen implementiert - dieser beginnt nach dem ersten Login mit einem Pop-up zur Eingabe medizinischer Daten. Der Server sowie dessen HTTP-Antworten wurden mit Mock-Ups simuliert. Der eigentliche Login-Prozess selbst war nicht Teil unseres Projektumfangs.
Die Implementierung umfasst ausschließlich die Client-seitige Logik, einschließlich der Benutzeroberfläche, der Controller und der Verarbeitung von ein- und ausgehenden API-Aufrufen. Die serverseitige Logik wie z. B. das Session-Handling oder die Validierung der Anfragen wird im Projekt lediglich vorgetäuscht.

*2.3 Programmausführung*

Repository öffnen und Programm ausführen
Installieren Sie eine IDE – Wir empfehlen IntelliJ IDEA.  
Download-Link: IntelliJ IDEA Download   
Folgen Sie dem Installationsassistenten von IntelliJ – Belassen Sie die vorausgewählten Optionen.  
Starten Sie IntelliJ – Nach der Installation sehen Sie auf dem Startbildschirm die Optionen: "New Project", "Open", "Get from VCS".  
Wählen Sie „Get from VCS“ – (VCS = Version Control System)    
   
Repository klonen – Fügen Sie folgende URL in das URL-Feld ein:   
https://github.com/lisjuscha/Weltraumtourismus_Gruppe2   
Wählen Sie den gewünschten Speicherort.   
Klicken Sie auf "Clone" – unten rechts.   
Projekt vertrauen – Klicken Sie im erscheinenden Popup auf "Trust the Project".   

//Technisch
Maven Build Script laden – Beim Öffnen des Projekts erscheint rechts unten ein Popup: "Maven build script found". Klicken Sie auf "Load" und warten Sie, bis der Ladebalken verschwindet.  
Projekt-JDK einrichten 
Klicken Sie auf das Drei-Balken-Menü oben links → "Project Structure..." → Im Tab "Project" beim Feld "SDK:" auf den Pfeil klicken → "Download JDK" → Im Popup "Vendor:" aufklappen und "SAP SapMachine" auswählen → "Download" → "OK".
Launcher-Klasse finden
Verwenden Sie diesen Pfad: Launcher.java.
Oder manuell:
Code → bad\_walden\_stadtwerke → src → main → java → com.bad\_walden\_stadtwerke → Launcher
Anwendung starten – Drücken Sie oben rechts auf das grüne Dreieck.

# 3. User-Dokumentation    
   
Um das Programm ohne Probleme nutzen zu können, haben wir eine ausführliche Dokumentation mit sowohl Bildern als auch Texten erstellt, die Sie durch den ganzen Prozess leitet. Dabei haben wir zwischen der des Arztes und der des Kunden der ins All fliegt unterschieden. Die jeweiligen Dokumentationen finden Sie hier:  
    
User-Dokumentation Arzt: [Documents/User-Dokumentation-Arzt.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/fc7d398943869b6fd1280bf46186e04b863fcedb/Documents/User-Dokumentation-Arzt.md)  
User-Dokumentation Kunde: [Documents/User-Dokumentation-Kunde.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/f22f387388eba4a3bc88c9b41b4566ca3faf3069/Documents/User-Dokumentation-Kunde.md)

# 4. Präsentation
Die Präsentation befindet sich hier: Documents/FlightPreparation-Gruppe2.pdf

Noch Fragen?
Bitte kontaktieren Sie uns bei weiteren Fragen direkt:
DanielJusto.BlazquezPerez@studmail.hwg-lu.de
LucAlex.Hampele@studmail.hwg-lu.de
PhilippLennard.Rohr@studmail.hwg-lu.de
Lisa.Schmidt@studmail.hwg-lu.de
Joline.Zeidler@studmail.hwg-lu.de




