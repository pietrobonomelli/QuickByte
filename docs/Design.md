**DOCUMENTO 4: DESIGN**

**1)Software Architecture**

L’architettura del nostro sistema si basa su un’architettura Cliente-server con una chiara separazione tra la logica e l’interfaccia utente, secondo il pattern Model-View-Controller (MVC).

1.1) **Architectural Views**
- **Logical View:** che descrive il sistema in termini di elementi di progettazione principali e delle loro interazioni:

<img src="./UML/Images_UML/Class_Diagram.png" width="800px" align="center">

Diagramma di classe

<img src="./UML/images_UML/State_Machine_Diagram.png" width="800px" align="center">
Diagramma di stato

- **Process View**: che descrive la struttura dinamica del sistema in termini di attività, processi, loro comunicazione e allocazione di funzionalità agli elementi di runtime;

  <img src="./UML/images_UML/Sequence_Diagram.png" width="800px" align="center">

Diagramma di sequenza

<img src="./UML/images_UML/Communication_Diagram.PNG" width="800px" align="center">

Diagramma di comunicazione

<img src="./UML/images_UML/Activity_Diagram.png" width="800px" align="center">

Diagramma di attivita


1.2) **stile architetturale**

adottiamo uno stile architetturale a stratti che prevede la suddivisione del sistema del sistema in livelli distinti per separare le responsabilità e semplificare la manutenzione:

- livello di interfaccia
- livello logico
- livello di database

1.3) **Libreria esterna con Maven**

Nel progetto, usiamo librerie esterne con Maven:

- MySQL Connector
- Junit per i test

**2) Software Design**

**2.1) Design pattern**

Nel nostro software usiamo i seguenti design pattern:

- MVC: per la separazione tra la logica e l'interfaccia utente
- Singleton: Utilizzato per la gestione della connessione al database, garantendo un'unica istanza condivisa.

**2.2) Misurazione del codice: (Da riportare i screenshot)**

Per garantire la qualità del codice, usiamo strumenti di analisi:

- Stan4J: per analizzare le dipendenze tra pacchetti.

(screen)

- SonarLint per identificare potenziali problemi di qualità e complessità.

(screen)


