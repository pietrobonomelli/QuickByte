**TESTING**

**Introduzione**

Questo documento descrive il piano di test per il nostro progetto. L’obiettivo è garantire la qualità del software attraverso test strutturati, verificando il corretto funzionamento dei moduli implementati e la conformità ai requisiti.

Gli strumenti che utilizziamo sono:

- Junit 4 per i test di unità
- JaCoCo per misurare la copertura dei test.

**1)Copertura**

I test sono stati effettuati principalmente sulle classi del pacchetto database e dao che contiene la logica del sistema

<img src="./images/Copertura dao.png" width="600px" high="800px" align="center">

<img src="./images/Copertura database.png" width="600px" high="800px" align="center">

2)Casi di test

2\.1) Il test testAggiungiIndirizzo\_Success verifica se un nuovo indirizzo viene correttamente aggiunto e poi recuperato dal database tramite la classe IndirizzoDAO

<img src="./images/caso di test 1.png" width="800px" align="center">

2\.2) il test testAggiungiMenu\_Duplicate verifica che l'inserimento di un menu duplicato nel database generi un'**eccezione SQL**

<img src="./images/caso di test 2.png" width="800px" align="center">


2\.3) il test testUpdateStatoOrdine verifica se l’aggiornamento di un stato di ordine viene correttamaneto aggiornato

<img src="./images/caso di test 3.png" width="800px" align="center">
