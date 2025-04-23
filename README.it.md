![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white)
![Maven](https://img.shields.io/badge/apachemaven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)

# **INGV-FX parser** 🌎🫨

**INGV-FX Parser** è un’applicazione desktop JavaFX che consente di visualizzare in modo interattivo i **dati sismici** forniti dall’INGV *(Istituto Nazionale di Geofisica e Vulcanologia)*.
L’app effettua il fetch dei dati da un ![endpoint](https://webservices.ingv.it/swagger-ui/dist/?url=https://ingv.github.io/openapi/fdsnws/event/0.0.1/event.yaml) di rete e li mostra sia in una tabella dettagliata sia su una mappa geografica interattiva.

## **📼 Caratteristiche Principali**

![Dashboard](screenshots/dashboard.jpg)

- **Visualizzazione** dei terremoti in tabella con data, magnitudo e località.
- **Filtri** per data e limite di risultati.
- **Ricerca** testuale tra gli eventi.
- Visualizzazione geografica dei terremoti tramite marker su **mappa interattiva**.

![Map](screenshots/map.jpg)

## **⚒️ Dettagli**

L'applicazione é realizzata interamente in ![JavaFX](https://openjfx.io/openjfx-docs/) ed utilizza la seguente ![API](https://webservices.ingv.it/swagger-ui/dist/?url=https://ingv.github.io/openapi/fdsnws/event/0.0.1/event.yaml), fornita dall'INGV, per il fetch dei risultati.

Il progetto é stato testato su processore armV8 (M1) (![IntelliJ Idea](https://www.jetbrains.com/idea/)) con building mediante ![Apache Maven](https://maven.apache.org/). É stata utilizzata la **JDK 24** fornita da ![Oracle](https://www.oracle.com/java/technologies/downloads/), build 24+36-3646.
