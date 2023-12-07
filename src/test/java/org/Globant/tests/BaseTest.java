package org.Globant.tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.Globant.endpoints.BankTransaction;
import org.globant.reporting.Reporter;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.restassured.RestAssured.given;

public class BaseTest {

 /*   Gets all the bank transaction available on the endpoint.
        the HTTP GET request is applied .
        @param endpoint The endpoint in which the bank transactions are located
        @return a list of all the bank transactions on the endpoint.
  */


    protected List<BankTransaction> getAllTransactions (String endpoint){
        RestAssured.baseURI = endpoint;
        RequestSpecification httpRequest = given();
        Response response = httpRequest.request(Method.GET, "");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200, "Transactions were not obtained properly");

        JsonPath jsonPathEvaluator = response.jsonPath();
        return jsonPathEvaluator.getList("", BankTransaction.class);



    }

    /*Deletes a bank transaction on the endpoint.
    * The HTTP DELETE request is applied.
    * @param endpoint The endpoint in which the bank transactions are located.
    * @param transaction the transaction to be deleted.
    * @return an integer representing the status code obtained after the delete request
    * is applied.
    * */

    protected int deleteTransaction(String endpoint, BankTransaction transaction){
        Response response = given().contentType("application/json").when().delete(endpoint + transaction.getId());
        return response.getStatusCode();
    }

    /*Deletes all bank transactions on the endpoint.
     * The HTTP DELETE request is applied.
     * @param endpoint The endpoint in which the bank transactions are located.
     * @return true if all the bank transactions are deleted succesfully, otherwise
     * the return is false.
     * */

    protected boolean deleteAllTransactions(String endpoint) {
        List<BankTransaction> transactions = getAllTransactions(endpoint);
        if (transactions.isEmpty()) {
            return Reporter.error("No Transactions were found on the endpoint.");
        } else {
            for (BankTransaction transaction : transactions) {
                int statusCode = deleteTransaction(endpoint, transaction);
                if (statusCode != 200) {
                    Reporter.error("Transaction cannot be deleted. " +
                            "Status code: " + statusCode);
                }
            }
        }
        return true;
    }


/*
    @Initializes the POJO with ten random data to create ten bank transactions.
    @param amount, the amount of bank transactions to be created. In this case,
    ten bank transactions.
    @return A list of ten bank transactions created based on random data.

*/

    protected List<BankTransaction> createTransactions (int amount){
        List<BankTransaction> transactions = new ArrayList<>();

        Faker feku = new Faker(Locale.forLanguageTag("es-US"));
       
        for (int i = 0; i < amount; i++){
            transactions.add(new BankTransaction(
                    feku.name().firstName(),
                    feku.name().lastName(),
                    feku.number().numberBetween(0, 999999),
                    feku.number().randomDouble(2, 0, 10000),
                    feku.options().option("withdrawal", "payment", "invoice ","deposit"),
                    feku.internet().emailAddress(),
                    feku.random().nextBoolean(),
                    feku.address().country(),
                    feku.phoneNumber().cellPhone()
            ));

        }
        return transactions;

    }

    /* Checks a list and looks for duplicate items.
    @param items A list of items.
    @return true if no duplicate items found in the list, otherwise the return is false.
     */


    private <T> boolean checkForDuplicates(List<T> items) {
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                if (items.get(i) != null && items.get(i).equals(items.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }


    /*Checks a bank transactions list and looks for duplicate emails.
    * @param transactionsList A list of bank transactions.
    * @return True if there are no duplicate emails in the list,
    * otherwise the return is false.
    * */

    protected boolean checkDuplicateEmailList(List<BankTransaction> transactions) {
        List<String> emailList = new ArrayList<>();
        for (BankTransaction transaction : transactions) {
            emailList.add(transaction.getEmail());
        }
        return checkForDuplicates(emailList);
    }


    /*Checks a bank transactions list extracted from the endpoint and looks
    * for duplicate emails.
    * @param endpoint The endpoint in which the bank transactions are located.
    * @return True if there are no duplicate emails on the endpoint,
    * otherwise the return is false.
    * */


    protected boolean checkDuplicateEmailEndpoint(String endpoint){
        List<BankTransaction> transactions = getAllTransactions(endpoint);
        return checkDuplicateEmailList(transactions);
    }

    /*Posts a bank transaction on the endpoint.
    * The HTTP POST request is applied.
    * @param endpoint the endpoint in which the bank transactions are located
    * @param transaction the transaction to be posted.
    * @return An integer representing the status code obtained after the post request
    * is applied.
    * */
    protected int createTransaction(String endpoint, BankTransaction transaction){
        Response response = given().contentType("application/json").body(transaction).when().post(endpoint);
        return response.getStatusCode();
    }

    /*Posts all bank transactions created on the endpoint.
    *The HTTP POST request is applied.
    *
    * @param endpoint the endpoint in which the bank transactions are located.
    * @return true if all the bank transactions are posted successfully, otherwise
    * the return is false.
    * */

    protected boolean uploadAllTransactions(String endpoint) {
        List<BankTransaction> transactions = createTransactions(10);
        if (transactions.isEmpty()) {
            return Reporter.error("No transactions were created. ");
        } else {
            if (!checkDuplicateEmailList(transactions)) {
                return false;
            }
            for (BankTransaction transaction : transactions) {
                int statusCode = createTransaction(endpoint, transaction);
                if (statusCode != 201) {
                    Reporter.error("Transaction cannot be posted. " + "Status code: " + statusCode);
                }
            }
        }
        return true;
    }


    /*Updates a bank transaction on the endpoint.
    * The HTTP PUT request is applied.
    * @param endpoint The endpoint in which the bank transactions are located.
    * @param transaction The transaction to be updated.
    * @return An integer representing the status code obtained after the put request
    * is applied.
    * */

    protected int updateTransaction(String endpoint, BankTransaction transaction) {
        Response response = given().contentType("application/json").body(transaction).when().put(endpoint);
        return  response.getStatusCode();
    }

    /*Updates the account number of a bank transaction created on the endpoint.
    * the HTTP PUT request is applied.
    * @param endpoint The endpoint in which the bank transactions are located.
    * @return true if the bank transaction is updated successfully, otherwise
    * the return is false
    * */

    protected boolean createUpdateInformation (String endpoint){
        List<BankTransaction> transactions = getAllTransactions(endpoint);
        List<Integer> accountsNumbers = new ArrayList<>();
        int min = 0;
        int max = 9;
        int maxAccountNumber = 999999;
        int randomIndex = (int) (Math.random() * (max - min + 1 ) + min);
        int newAccountNumber = (int) (Math.random() * (maxAccountNumber - min + 1) + min);

        if (transactions.isEmpty()){
            return Reporter.error("No Transactions were found on the endpoint. ");
        } else { // Updated line
            for (BankTransaction transaction : transactions) {
                accountsNumbers.add(transaction.getAccountNumber());
            }
            int accountNumberUpdate = accountsNumbers.get(randomIndex);
            if (!accountsNumbers.contains(accountNumberUpdate)) {
                return false;
            }
            for (BankTransaction transaction : transactions){
                if (transaction.getAccountNumber() == accountNumberUpdate) {
                    Reporter.info("The transaction with account number" + accountNumberUpdate + "will be updated");
                    transaction.setAccountNumber(newAccountNumber);
                    Reporter.info("The Account Number " + accountNumberUpdate + "was replaced by the new account number " + newAccountNumber + ".");
                    String updateEndpoint = endpoint + transaction.getId();
                    int statusCode = updateTransaction(updateEndpoint, transaction);
                    if (statusCode != 200){
                        Reporter.error("The transaction cannot be updated. " + "Status code: " + statusCode);
                    }
                }
            }
        }
        return true;
    }

}
