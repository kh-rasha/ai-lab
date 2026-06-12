# Reliability Evaluation

## 1. Promptstrategi

Jag använde en systemprompt som säger att AI:n bara ska svara med giltig JSON.

Jag skrev också att AI:n inte får returnera markdown eller extra förklaringar.

Detta gör svaret mer förutsägbart och enklare att läsa in i Java-programmet.

Exempel på önskat svar:

```json
{
  "sentiment": "positive",
  "score": 90
}
```

På detta sätt minskar risken för fel vid tolkning av svaret.

---

## 2. Felhantering

Applikationen innehåller flera lösningar för att hantera fel.

### Timeout

Jag använder timeout-inställningar så att programmet inte väntar för länge om API:t inte svarar.

### HTTP 429 (Rate Limit)

Om API:t svarar med felkod 429 gör programmet automatiskt nya försök.

Jag använder exponential backoff:

* Försök 1 → vänta 1 sekund
* Försök 2 → vänta 2 sekunder
* Försök 3 → vänta 4 sekunder

Om alla försök misslyckas returneras ett standardsvar.

### JSON-fel

Om AI:n returnerar ogiltig JSON fångas felet i en catch-sats.

Då returneras:

```json
{
  "sentiment": "unknown",
  "score": 0
}
```

Jag använder även validering i DTO-klassen för att kontrollera att data har rätt format.

---

## 3. Tillförlitlighetsbedömning

LLM-modeller är kraftfulla men inte helt tillförlitliga.

De kan ibland:

* Returnera felaktig JSON
* Ge olika svar på samma fråga
* Vara långsamma eller otillgängliga
* Träffa API-begränsningar (429)

För att minska dessa problem har jag lagt till:

* Tydliga instruktioner i prompten
* Validering av data
* Felhantering med try-catch
* Retry-logik med exponential backoff
* Standardsvar vid fel

Detta gör applikationen mer stabil och mer lämplig för verkliga användningsområden.
