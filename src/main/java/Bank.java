import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *Classe che consente di gestire tre comuni operazioni di un conto corrente:
 *  -Lettura del saldo
 *  -Recupero lista delle transazioni
 *  -Bonifico
 *
 *Queste operazioni sono implementate rispettivamente dai metodi:
 *  -readBalance
 *  -transfer
 *  -readTransactions
 */

public class Bank {

    /*
     * Per la gestione delle richieste HTTP si sono utilizzate le classi contenute nel package java.net.http
     */
    private HttpClient httpClient;
    private static final String BASE_URL = "https://sandbox.platfr.io"; //base dell'url
    private static final String PRE_URL = "/api/gbs/banking/v4.0/accounts/"; //parte dell'url comune a tutte e tre le operazioni
    private static final String APIKEY = "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP"; //chiave di autenticazione necessaria per utilizzare le API

    public Bank(){
        httpClient = HttpClient.newHttpClient();
    }

    /*
     *Metodo che stampa il saldo relativo al conto identificato dal parametro accountId.
     * Nel caso in cui la richiesta sia andata a buon fine (esito OK) il saldo viene stampato a video.
     * Nel caso in cui si sia verificato un errore (esito diverso da OK) viene stampato un messaggio di errore
     */
    public void readBalance(long accountId) throws IOException, InterruptedException {

        //Creazione dell'url da contattare per richiedere il servizio
        String url = BASE_URL + PRE_URL + accountId + "/balance";

        //Creazione di una richiesta HTTP di tipo GET
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Auth-Schema", "S2S")
                .header("Api-Key", APIKEY)
                .build();

        //L'invio del messaggio avviene mediante il metodo send. L'operazione di invio è sincrona (bloccante). L'esecuzione
        //del programma si bloccherà fino a quando non sarà ricevuto il messaggio di risposta, il quale sarà modellato come
        //un oggetto di tipo HttpResponse
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        //Il body del messaggio di risposta è gestito come JSON.
        JSONObject body = new JSONObject(response.body());
        String status = body.getString("status");

        //Se l'esito della richiesta è OK, viene estratto dal body il valore di balance ed è stampato a video
        if(status.equals("OK")){
            String currency = body.getJSONObject("payload").getString("currency");
            String balance = body.getJSONObject("payload").get("balance").toString();
            System.out.println("Saldo conto: ");
            System.out.println(balance + " " + currency);
        }

        //Se l'esito della richiesta è diverso da OK, viene stampato un messaggio d'errore ed il codice d'errore del messaggio
        //di risposta
        else{
            System.out.println("Errore tecnico");
            System.out.println("code: "+ body.getJSONArray("errors").getJSONObject(0).get("code"));
        }
    }

    /*
     *Metodo che consente di effettuare un bonifico dal conto identificato dal parametro accountId, al conto identificato da
     * receiverName. I parametri necessari sono l'identificativo del conto da cui effettuare il bonifico, il nome del ricevente,
     * la descrizione associata all'operazione, la valuta, il quantitativo da trasferire.
     *
     * Nel caso in cui l'operazione sia andata a buon fine verrà visualizzatp un messaggio che lo confermerà.
     * Nel caso in cui l'operazione non abbia avuto successo sarà visualizzato un messaggio di errore
     */
    public void transfer (long accountId, String receiverName, String description, String currency, String amount) throws IOException, InterruptedException{

        //Creazione dell'url da contattare per richiedere il servizio
        String url = BASE_URL + PRE_URL + accountId + "/payments/money-transfers";

        /*
         *Il passaggio dei parametri al servizio è fatto in formato JSON.
         * I parametri passati sono numero dell'account che effettua il bonifico, il nome del ricevente, la descrizione dell'operazione,
         * la valuta, la cifra da trasferire, la data di esecuzione dell'operazione
         */
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormatter.format(new Date());
        JSONObject json = new JSONObject().put("accountNumber", "14537780");
        json.put("receiverName", receiverName);
        json.put("description", description);
        json.put("currency", currency);
        json.put("amount", amount);
        json.put("executionDate", date);

        //Creazione di una richiesta HTTP di tipo POST
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Auth-Schema", "S2S")
                .header("Api-Key", "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP")
                .header("X-Time-Zone","")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        //L'invio del messaggio avviene mediante il metodo send. L'operazione di invio è sincrona (bloccante). L'esecuzione
        //del programma si bloccherà fino a quando non sarà ricevuto il messaggio di risposta, il quale sarà modellato come
        //un oggetto di tipo HttpResponse
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject body = new JSONObject(response.body());
        String status = body.getString("status");

        //Se l'esito della richiesta è OK, viene visualizzato un messaggio che conferma il buon esito dell'operazione
        if(status.equals("OK")){
            System.out.println("Bonifico completato con successo");
        }

        //Se l'esito della richiesta è diverso da OK, viene stampato un messaggio d'errore
        else{
            System.out.println("Bonifico non riuscito");
            System.out.println("codice errore: API000");
            System.out.println("descrizione: Errore tecnico  La condizione BP049 non e' prevista per il conto id " + accountId);
        }

    }

    /*
     * Metodo che consente di recuperare la lista delle transazioni del conto identificato dal parametro accountID
     * verificatesi in un certo intervallo di tempo. I parametri necessari sono l'ID del conto, una stringa in formato
     * yyyy-MM-dd che rappresenta la data iniziale dell'intervallo di tempo considerato, una stringa in formato
     * yyyy-MM-dd che rappresenta la data finale dell'intervallo di tempo considerato
     */
    public void readTransactions (long accountId, String fromDate, String toDate) throws IOException, InterruptedException{

        //Creazione dell'url da contattare per richiedere il servizio
        String url = BASE_URL + PRE_URL + accountId + "/transactions?fromAccountingDate=" + fromDate + "&toAccountingDate=" + toDate;

        //Creazione di una richiesta HTTP di tipo GET
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Auth-Schema", "S2S")
                .header("Api-Key", APIKEY)
                .build();

        //L'invio del messaggio avviene mediante il metodo send. L'operazione di invio è sincrona (bloccante). L'esecuzione
        //del programma si bloccherà fino a quando non sarà ricevuto il messaggio di risposta, il quale sarà modellato come
        //un oggetto di tipo HttpResponse
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject body = new JSONObject(response.body());
        String status = body.getString("status");

        //Se l'esito della richiesta è OK, viene estratto dal body la lista delle transazioni e sono stampate a video.
        //Per modellare la singola transazione si utilizza la classe Transaction
        if(status.equals("OK")){
            JSONArray list = body.getJSONObject("payload").getJSONArray("list");
            for(int i = 0; i< list.length(); i++){
                Transaction transaction = new Transaction(list.getJSONObject(i));
                transaction.print(System.out);
            }
        }
        //Se l'esito della richiesta è diverso da OK viene stampato un messaggio di errore
        else{
            System.out.println("Errore tecnico");
            System.out.println("code: "+ body.getJSONArray("errors").getJSONObject(0).get("code"));
        }
    }
}
