package org.Globant.tests;

import org.globant.reporting.Reporter;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class UpdateTransactionTest extends BaseTest{
    @Parameters({"endpoint"})
    @Test
    public void updateAccountNumberTest(String endpoint) {
        Reporter.info("Getting all transactions from the endpoint...");
        Reporter.info("Amount of transactions on the endpoint: "
                + getAllTransactions(endpoint).size() + " transactions.");
        Assert.assertTrue(createUpdateInformation(endpoint),
                "Account number not found on endpoint.");
        Reporter.info("A transaction was updated using an existing account number.");
    }
}
