# Gruppenprojekt:

![image](https://github.com/user-attachments/assets/21a6c44c-2092-4c24-8d7c-811c3bbbff62)

HWG Ludwigshafen IBAIT23 ATdIT 2, Projekt Gruppe 2

Die Entwicklung einer Software für die Abstimmung zwischen unserer Firma und dem Touristen hinsichtlich der Planung und Vorbereitung für seinen Flug ins Weltall. 

Lehrbeauftragte: Andreas Heck und Patrick Gutgesell

Gruppen Mitglieder: Daniel Justo Blazquez Perez, Luc Alex Hampele, Philipp Lennard Rohr, Lisa Schmidt, Joline Zeidler

# 1. Modellierung  
  
*1.1 Allgemeine Einführung in die Modellierungsdateien*
    
Alle genannten Dateien und Modelle bilden die konzeptionelle Grundlage für unseren Entwicklungsprozess im Rahmen der Flugvorbereitung.
Wir möchten Ihnen ein Verzeichnis der Dateien zur Verfügung stellen, die die Grundlage für unsere Implementierung bilden. 

Dabei gehen wir davon aus, dass wir die genannten Informationen bereits übermittelt bekommen:
- Vor-/Nachname
- Geburtsdatum
- Flugdatum
- E-Mail-Adresse
     
*1.1.1 Prozesse*   
  
Um eine Software zur Flugvorbereitung in den Weltraumn effektiv zu entwickeln, müssen wir zunächst die wichtigsten Prozesse und Softwareelemente modellieren.
Die BPMN-Modelle zur Veranschaulichung unserer Prozesse befinden sich im Ordner [Documents/Prozessdiagramme](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/tree/fc13d99ae925e3c2fffa1bcf49efef8cf49fc79e/Documents/Prozessdiagramme).      
Eine ausführliche Erläuterung dazu finden Sie in der Datei [Documents/Prozessdiagramme/README.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/tree/f5677d9eefaf08bb6447bcca0dc48176935dbb87/Documents/UI/Customer)
     
*1.1.2 UI-Modelle*   
  
Die verschiedenen UI-Modelle sind im Ordner [Documents/UI](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/tree/fc13d99ae925e3c2fffa1bcf49efef8cf49fc79e/Documents/UI) abgelegt. Dieser ist nochmal in jeweils vier Unterordner für die verschiedenen Perspektiven der Nutzer unterteilt.    
- Eine bebilderte Erläuterung der Benutzeroberfläche des Kunden finden Sie in [Documents/UI/Customer/README.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/fc13d99ae925e3c2fffa1bcf49efef8cf49fc79e/Documents/UI/README.md)   
- Eine bebilderte Erläuterung der Benutzeroberfläche des Arztes finden Sie in [Documents/UI/Doctor/README.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/ba0ce62f2fbd961f6f652225bf09cf86e283b75b/Documents/UI/Doctor/README.md)   
- Eine bebilderte Erläuterung der Benutzeroberfläche des Anzugsspezialisten finden Sie in [Documents/UI/Specialist/README.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/ba0ce62f2fbd961f6f652225bf09cf86e283b75b/Documents/UI/Specialist/README.md)   
- Eine bebilderte Erläuterung der Benutzeroberfläche des Trainers finden Sie in [Documents/UI/Trainer/README.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/ba0ce62f2fbd961f6f652225bf09cf86e283b75b/Documents/UI/Trainer/README.md)    
   
*1.1.3 Personas*  
  
Die verschiedenen Personas, welche wir erstellt haben um besser die Kundenbedürfnisse zu erkennen,sind in diesem Ordner zu finden: [Documents/Personas](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/tree/fc13d99ae925e3c2fffa1bcf49efef8cf49fc79e/Documents/Personas)
Eine detaillierte Beschreibung zu jeder einzelnen Persona ist außerdem in diesem Dokument zu finden: [Documents/Personas/README.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/fc13d99ae925e3c2fffa1bcf49efef8cf49fc79e/Documents/Personas/README.md)

    
Im Rahmen unseres Weltraumtourismus-Projekts haben wir uns entschieden, den Medical-Check-Prozess umzusetzen. Dieser Prozess ist Teil der Flugvorbereitung und dient der Erfassung von Gesundheitsdaten potenzieller Weltraumtouristinnen. Vor dem Antritt ihres Raumflugs geben die Teilnehmerinnen relevante medizinische Informationen an, die zur Einschätzung der Flugtauglichkeit erforderlich sind. Anschließend wird automatisch ein Termin mit einer Ärztin oder einem Arzt in unserem Unternehmen vereinbart, um eine persönliche Untersuchung durchzuführen und die finale Freigabe für den Flug zu erteilen. Weitere Informationen zur Modellierung finden Sie in den oben genannten README-Dateien.
BPMN: [Documents/Prozessdiagramme/README.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/fc13d99ae925e3c2fffa1bcf49efef8cf49fc79e/Documents/Prozessdiagramme/README.md)
UI: [Documents/UI](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/tree/f5677d9eefaf08bb6447bcca0dc48176935dbb87/Documents/UI)
Personas: [Documents/Personas/README.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/fc13d99ae925e3c2fffa1bcf49efef8cf49fc79e/Documents/Personas/README.md)

# 2. Programmierung
*2.1 Codeübersicht und Dateistruktur*   

Der Quellcode unseres Projektes befindet sich in diesem Git-Repository. Hier liegen alle relevanten Dateien, die zur Ausführung der Anwendung benötigt werden.
Eine ausführliche Anleitung zur Nutzung und zum Starten des Projekts finden Sie weiter unten. Für einen reibungslosen Einstieg in den Code empfehlen wir, zunächst die [Datenmodell-README](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/tree/d99d08d8aa9907b215128221fc9b4c66932858b9/Documents/Dokumentationen/Datenmodell) in diesem Ordner zu lesen. Dort finden Sie alle notwendigen Informationen zur Architektur, zu Abhängigkeiten und zum Aufbau der Anwendung.

*2.2 Projektumfang*   
   
Im Rahmen unseres Projekts haben wir den Medical-Check-Prozess für zukünftige Weltraumtourist*innen implementiert - dieser beginnt nach dem ersten Login mit einem Pop-up zur Eingabe medizinischer Daten.  

Die folgende Tabelle zeigt, welche Teile des Projekts implementiert wurden (**In Scope**) und welche lediglich gemockt wurden (**Out of Scope**):

| Bereich              | In Scope (implementiert)                                                                                                       | Out of Scope (gemockt / nicht implementiert)                                                                                       |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| **Kunde (User)**     | - Kompletter medizinischer Check inkl. Fragebögen, Angaben und Dokumenten                                                    | - Startseite (Homepage nach Login)                                                                                                 |
| **Arzt**             | - Vollständiger Prozess zur Bewertung der Flugtauglichkeit<br>- Einsicht und Verwaltung von Kundendaten<br>- Dokumentenmanagement | - Startseite (Homepage nach Login)<br>- Fitting-Checkliste und zugehöriger Prozess<br>- Training-Checkliste und zugehöriger Prozess |
| **Trainer**          | *nicht implementiert*                                                                                                          | - Gesamter Trainerbereich, inkl. Trainingsverwaltung, Meetingseite und Ausstellung von Flight Permits                              |
| **Designer (Anzug)** | *nicht implementiert*                                                                                                          | - Gesamter Designerbereich, inkl. Maßverarbeitung und Anzugsanpassung                                                              |


Es ist zu beachten, dass im aktuellen Prototyp die Funktionalitäten der Datei-Uploads vereinfacht implementiert sind. Hochgeladene Dateien werden zwar mit einer Benutzerkennung im Dateinamen versehen und in einem zentralen Verzeichnis gespeichert, die Anzeige dieser Dokumente für den Arzt erfolgt jedoch durch Auflistung aller Dateien in diesem Verzeichnis.

*2.3 Programmausführung*

Repository öffnen und Programm ausführen   
   
1. Installieren Sie eine IDE – Wir empfehlen IntelliJ IDEA.  
2. Download-Link: [IntelliJ IDEA Download](https://www.jetbrains.com/idea/download/?section=mac)  
3. Folgen Sie dem Installationsassistenten von IntelliJ – Belassen Sie die vorausgewählten Optionen.  
4. Starten Sie IntelliJ – Nach der Installation sehen Sie auf dem Startbildschirm die Optionen: "New Project", "Open", "Get from VCS".  
5. Wählen Sie „Get from VCS“ – (VCS = Version Control System)      
   
Repository klonen 

1. Fügen Sie folgende URL in das URL-Feld ein: https://github.com/lisjuscha/Weltraumtourismus_Gruppe2   
2. Wählen Sie den gewünschten Speicherort.   
3. Klicken Sie auf "Clone" – unten rechts.   
4. Projekt vertrauen – Klicken Sie im erscheinenden Popup auf "Trust the Project".   

Programm starten      
    
1. Maven Build Script laden: Beim Öffnen des Projekts erscheint rechts unten ein Popup: "Maven build script found". Klicken Sie auf "Load" und warten Sie, bis der Ladebalken verschwindet.  
2. Projekt-JDK einrichten: Klicken Sie auf das Drei-Balken-Menü oben links → "Project Structure..." → Im Tab "Project" beim Feld "SDK:" auf den Pfeil klicken → "Download JDK" → Im Popup "Vendor:" aufklappen und "SAP SapMachine" auswählen → "Download" → "OK".
3. Wechseln Sie in "SAP SapMachine" auf die Java-Version Java17: command I → SapMachine installieren → SapMachine17 (langfristiger Support) als JDK → rechtsklick auf SapMachine17 → Als Standartumgebung verwenden
4. Launcher-Klasse manuell finden: Code → Weltraumtourismus_Gruppe2 → src → main → java → com → example → flightprep → Main.java
5. Anwendung starten – Drücken Sie oben rechts auf das grüne Dreieck.

*2.4 Testen*  
   
Durch unsere Unit-Test haben wir eine Statement-Coverage von 70% sowie eine Branch-Coverage von 63%.
![image](https://github.com/user-attachments/assets/8701f271-b5f9-49ec-841f-79409e15e554)

# 3. Dokumentation    

*3.1 User-Dokumentation*   
    
Um das Programm ohne Probleme nutzen zu können, haben wir eine ausführliche Dokumentation mit sowohl Bildern als auch Texten erstellt, die Sie durch den ganzen Prozess leitet. Dabei haben wir zwischen der des Arztes und der des Kunden der ins All fliegt unterschieden. Die jeweiligen Dokumentationen finden Sie hier:  
    
User-Dokumentation Arzt: [Documents/Dokumentationen/User-Dokumentation-Arzt.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/5ebf489040f3eb8b78b943787bd10658bcba87d9/Documents/Dokumentationen/User-Dokumentation-Arzt.md)      
User-Dokumentation Kunde: [Documents/Dokumentationen/User-Dokumentation-Kunde.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/5ebf489040f3eb8b78b943787bd10658bcba87d9/Documents/Dokumentationen/User-Dokumentation-Kunde.md)  
  
*3.2 Technische Dokumentation*   
   
Die technische Dokumentation für das reibungslose Verständnis des Quellcodes befindet sich hier: [Documentation/Dokumentationen/Technische-Dokumentation.md](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/fa1c00927b0ebf41f523dc31d9babbf52bb09ba4/Documents/Dokumentationen/Technische-Dokumentation.md)

# 4. Mehrwert unserer Anwendung

*4.1 Mehrwert*   

Im Mittelpunkt steht ein Astronautentraining, das nicht nur spannend ist, sondern gezielt auf eine realistische Vorbereitung ausgerichtet wurde. Durch den Einsatz realistischer Simulationen entsteht eine eindrucksvolle Erfahrung, die gleichzeitig das Sicherheitsgefühl stärkt. Anstelle von Nervosität rückt ein Gefühl der Kontrolle und Vorfreude in den Vordergrund – eine wichtige Voraussetzung für jede Reise ins All. Die Struktur unseres Programms ist datenbasiert und darauf ausgelegt, möglichst effizient zu funktionieren: Der Aufwand für Teilnehmende bleibt gering, der Ressourceneinsatz bewusst minimal – bei gleichbleibend hoher Qualität.Besonderes Augenmerk legen wir auf die individuelle Anpassung: Alle Maßnahmen – vom Trainingsablauf bis zur persönlichen Betreuung – werden auf die jeweiligen Voraussetzungen und Bedürfnisse abgestimmt. Auf diese Weise entsteht ein Training, das nicht standardisiert, sondern persönlich und nachvollziehbar ist – ein Prozess, der Orientierung bietet und gleichzeitig Raum lässt für eigene Entwicklungen.
   
*4.2 Unterscheidungsmerkmale*    
   
Unsere Stärke liegt in der Verbindung mehrerer Elemente: eine eigene Infrastruktur, moderne Lernmethoden und individuell gestaltbare Erlebnisse.  Die Vorbereitung ist bewusst hybrid aufgebaut – digital und vor Ort. Der Einstieg erfolgt über medizinische Checks und interaktive Online-Trainings. Die praktische Vertiefung findet anschließend direkt in unserem eigenen Spaceport statt. Im Bereich Bildung setzen wir auf ein Konzept, das Information mit Erlebnis verbindet: Edutainment. Durch Augmented-Reality-Module, Apps und interaktive Inhalte wird Raumfahrtwissen greifbar – verständlich, spannend und immersiv. Auch in diesem Bereich spielt die Personalisierung eine zentrale Rolle: Vom maßgeschneiderten Raumanzug bis hin zu kleinen, persönlichen Details wie Lieblingsmusik oder Erinnerungsstücken – individuelle Identität wird nicht nur berücksichtigt, sondern bewusst sichtbar gemacht. Und selbst Wünsche wie eine eigene Flagge im Cockpit sind bei uns möglich.

# 5. Präsentation
Die Präsentation befindet sich hier: [Documents/Presentation](https://github.com/lisjuscha/Weltraumtourismus_Gruppe2/blob/b5eb7e25f669e37e63cc42af3bb3bf08cdf62b3d/Documents/Presentation/Flight%20Preparation%20-%20Gruppe%202.pptx)

# Noch Fragen?
Bitte kontaktieren Sie uns bei weiteren Fragen direkt:   
DanielJusto.BlazquezPerez@studmail.hwg-lu.de   
LucAlex.Hampele@studmail.hwg-lu.de   
PhilippLennard.Rohr@studmail.hwg-lu.de  
Lisa.Schmidt@studmail.hwg-lu.de  
Joline.Zeidler@studmail.hwg-lu.de   
 
 



