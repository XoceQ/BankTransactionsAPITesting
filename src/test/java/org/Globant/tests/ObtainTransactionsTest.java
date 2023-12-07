package org.Globant.tests;

import org.globant.reporting.Reporter;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ObtainTransactionsTest extends BaseTest{
    @Parameters({"endpoint"})
    @Test
    public void getTransactionsTest(String endpoint){
        Reporter.info("Getting all transactions from the endpoint...");
        Reporter.info("Amount of transactions on the endpoint: " + getAllTransactions(endpoint).size() + "transactions.");
        Reporter.info("Checking all transactions from the endpoint looking for duplicate emails");
        Assert.assertTrue(checkDuplicateEmailEndpoint(endpoint), "Two or more transactions on the endpoint have the same email address.");
        Reporter.info("All transactions have different emails. No duplicated emails were found.");


    }
}
