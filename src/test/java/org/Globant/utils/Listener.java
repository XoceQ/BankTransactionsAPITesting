package org.Globant.utils;

import org.globant.reporting.Reporter;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Listener implements ITestListener {
    @Override
    public void onTestSuccess(ITestResult result ){
        Reporter.info("Test: " +  result.getName() + "[PASSED]");
    }

    @Override
    public void onTestFailure(ITestResult result){
        Reporter.error("Test: " +  result.getName() + "[FAILED]");
    }


}
