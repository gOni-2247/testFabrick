import org.json.JSONObject;

import java.io.PrintStream;


//Classe le cui istanze modellano una singola transazione
public class Transaction {

    private String accountingDate; //Data in cui la transazione viene presa in carico
    private String amount; //Valore della transazione
    private String operationId; //Identificativo dell'operazione
    private String description; //Descrizione associata alla transazione
    private String currency; //Valuta in cui è espressa la cifra della transazione
    private String valueDate; //Data in cui è storicizzata la transazione
    private String transactionId; //Identificativo univoco della transazione

    //Costruttore senza argomenti. Assegna una stringa vuota ad ogni parametro
    public Transaction(){
        this.accountingDate = "";
        this.amount = "";
        this.operationId = "";
        this.description = "";
        this.currency = "";
        this.valueDate = "";
        this.transactionId = "";
    }

    //Costruttore che prende come parametro un JSON contenente le informazioni relative ad una transazione
    public Transaction(JSONObject payload){
        this.accountingDate = payload.getString("accountingDate");
        this.amount = payload.get("amount").toString();
        this.operationId = payload.getString("operationId");
        this.description = payload.getString("description");
        this.currency = payload.getString("currency");
        this.valueDate = payload.getString("valueDate");
        this.transactionId = payload.getString("transactionId");

    }

    //Metodi setter
    public void setAmount(String amount){
        this.amount = amount;
    }

    public void setAccountingDate(String accountingDate) {
        this.accountingDate = accountingDate;
    }

    public void setOperationId(String operationId){
        this.operationId = operationId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    //Metodi getter
    public String getAccountingDate(){
        return accountingDate;
    }

    public String getAmount(){
        return this.amount;
    }

    public String getOperationId(){
        return operationId;
    }

    public String getDescription(){
        return description;
    }

    public String getCurrency(){
        return currency;
    }

    public String getValueDate(){
        return valueDate;
    }

    public String getTransactionId(){
        return transactionId;
    }

    //Metodo per stampare una transazione
    public void print(PrintStream printStream){

        printStream.println("Accounting Date: "+accountingDate);
        printStream.println("Amount: "+amount);
        printStream.println("Operation ID: "+operationId);
        printStream.println("Description: " + description);
        printStream.println("Currency: "+ currency);
        printStream.println("Value Date: "+valueDate);
        printStream.println("Transaction ID: "+transactionId);
        printStream.println();
    }
}
