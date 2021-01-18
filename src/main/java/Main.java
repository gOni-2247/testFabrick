import java.io.IOException;


//Prova minimale delle funzionalità implementate nella classe Bank
public class Main {

    public static void main(String args[]) throws IOException, InterruptedException, IOException {

        Bank account = new Bank();

        account.readBalance(14537780);

        System.out.println();

        account.readTransactions(14537780, "2019-01-01", "2019-12-01");

        System.out.println();

        account.transfer(14537780, "Nome Cognome", "descrizione", "EUR", "10000");
        
      
<<<<<<< HEAD
        System.out.println("Questa mofifica e' buona");
=======
        
>>>>>>> features2
        
        System.out.println("Aggiungo un altra features qui!");


    }
}

