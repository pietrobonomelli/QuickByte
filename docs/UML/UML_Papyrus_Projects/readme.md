# Progetti Papyrus - QuickByte

 Per questioni di comodità, i file Papyrus contenenti gli UML sono stati caricati separatamente in queste cartelle.

## Contenuto della cartella

- **QuickByte_UML1** - Rota Leonardo  
- **QuickByte_UML2** - Bonomelli Pietro  
- **QuickByte_UML3** - Anes Hamza   

Ogni progetto è stato curato principalmente dalla persona indicata, ma con il supporto e gli allineamenti del team per garantire coerenza e qualità complessiva.

## Una breve descrizione per ogni UML:

### Diagramma di Attività (Activity Diagram)
Il diagramma di attività rappresenta il flusso operativo dell'applicazione, suddiviso in tre attori principali: Cliente, Titolare e Corriere.

- Il cliente può visualizzare l'elenco dei ristoranti, selezionarne uno, scegliere i piatti e procedere con l'ordine.
- Il titolare riceve l'ordine e può accettarlo o rifiutarlo. Se accettato, viene avviata la preparazione del cibo. Se rifiutato, viene richiesto un nuovo metodo di pagamento.
- Il corriere prende in carico l'ordine e lo consegna al cliente.
- Il cliente può recensire il ristorante.

L'activity diagram mostra la gestione dell'ordine dall'inizio alla fine, comprese le interazioni tra i vari ruoli.

### Diagramma delle Classi (Class Diagram)
Il diagramma delle classi rappresenta la struttura dell'applicazione, con le classi principali e le loro relazioni.

- Utente è la classe base, con attributi comuni (email, nome, telefono) e operazioni generali come registrazione, login e modifica profilo.
- Cliente, Titolare e Corriere ereditano da Utente e hanno metodi specifici:
    - Il Cliente può visualizzare ristoranti, effettuare ordini e controllarne lo stato.
    - Il Titolare può gestire il menu, gli ordini e le recensioni.
    - Il Corriere può accettare consegne e aggiornare lo stato di un ordine.
- L'Ordine è collegato al Cliente, al Corriere e al Ristorante, con stato aggiornabile tramite un'enumerazione (Accettato, In Preparazione, Pronto, In Consegna, Consegnato).
- Il Carrello contiene i piatti selezionati dal cliente.
- Il Metodo di Pagamento è associato al Cliente per memorizzare i dati della carta.
Questo schema rappresenta la logica dei dati e le relazioni tra gli elementi chiave del sistema.

### Diagramma di Comunicazione (Communication Diagram )
Il diagramma di comunicazione mostra l'interazione tra gli attori e il sistema per la gestione degli ordini.

- Il Cliente accede al sistema e seleziona un ristorante, sceglie i piatti e li aggiunge al carrello.
- Il Sistema verifica la disponibilità del ristorante e del piatto, poi aggiorna il database.
Una volta confermato l'ordine, il Portale di Pagamento gestisce la transazione.
- Se il pagamento va a buon fine, il sistema assegna l'ordine a un corriere disponibile.
- Il Corriere riceve la notifica dell'ordine, lo ritira dal ristorante e lo consegna al cliente.
- Il Sistema aggiorna lo stato dell’ordine durante ogni fase del processo.
Questo diagramma evidenzia il flusso di comunicazione tra i vari attori e il sistema per completare l’ordine.

### Diagramma delle Componenti (Component Diagram)
Il diagramma UML delle componenti rappresenta l'architettura del sistema, mostrando le principali parti e le loro interazioni.

- WebApp (Frontend) usa le API del Backend per gestire utenti, ordini e ristoranti.
- Il Backend dipende dal Database per la memorizzazione dei dati e interagisce con servizi esterni:
    - Payment Gateway per l’elaborazione dei pagamenti.
    - Notifiche API per inviare aggiornamenti via email/SMS.

### Diagramma di Sequenza (Sequence Diagram)
Questo diagramma rappresenta il flusso di interazioni tra i vari attori e componenti del sistema "QuickByte". Le interazioni sono mostrate come messaggi scambiati tra i partecipanti, organizzati lungo una sequenza temporale. Il diagramma illustra le seguenti operazioni principali:

- Il login di un utente con controllo delle credenziali.
- L'inserimento di un ristorante o di un piatto, con la gestione di errori in caso di dati già esistenti.
- La selezione di un ristorante, la visualizzazione del menu e l'aggiunta di piatti al carrello.
- La conferma dell'ordine, il pagamento e l'assegnazione dell'ordine a un corriere.
- L'aggiornamento dello stato della consegna dell'ordine.

### Diagramma a Stati (State Machine Diagram)

Questo diagramma rappresenta gli stati attraverso i quali un cliente può passare mentre utilizza il sistema QuickByte. Alcuni degli stati principali includono:

- Registrazione e login.
- Navigazione tra i ristoranti disponibili.
- Selezione del menu e aggiunta di piatti al carrello.
- Conferma dell'ordine e pagamento.
- Chiusura del programma.

### Diagramma dei Casi d'Uso (Use Case Diagram)

Questo diagramma rappresenta le principali funzionalità offerte dal sistema QuickByte e le interazioni tra gli attori coinvolti. Gli attori principali sono:

- Cliente, che può visualizzare i ristoranti, selezionare piatti, effettuare ordini e ricevere aggiornamenti sullo stato dell’ordine.
- Titolare del ristorante, che può creare e gestire i dati del ristorante, il menu e gli ordini.
- Corriere, che visualizza gli ordini disponibili, li prende in carico e aggiorna il loro stato.
- Utente generico, che può registrarsi, accedere e gestire il proprio account.
