package com.aventstack.extentreports;

import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.model.ScreenCapture;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class AppenderTest {
    private static final String JSON_ARCHIVE = "target/json/jsonArchive.json";

    @Test
    public void testWithLogs() throws IOException {
        // initial, create archive:
        ExtentReports extent = new ExtentReports();
        JsonFormatter json = new JsonFormatter(JSON_ARCHIVE);
        extent.attachReporter(json);
        ExtentTest test1 = extent.createTest("Testname1", "description1")
                .pass("Pass")
                .skip("Skip")
                .fail("Fail");
        ExtentTest test2 = extent.createTest("Testname2", "description2")
                .warning("Warn")
                .info("Info");
        extent.flush();

        // post, check archive
        extent = new ExtentReports();
        extent.createDomainFromJsonArchive(JSON_ARCHIVE);
        List<com.aventstack.extentreports.model.Test> list = extent.getReport().getTestList();
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getStatus(), test1.getStatus());
        Assert.assertEquals(list.get(1).getStatus(), test2.getStatus());
        Assert.assertEquals(list.get(0).getName(), test1.getModel().getName());
        Assert.assertEquals(list.get(1).getName(), test2.getModel().getName());
        Assert.assertEquals(list.get(0).getDescription(), test1.getModel().getDescription());
        Assert.assertEquals(list.get(1).getDescription(), test2.getModel().getDescription());
        Assert.assertEquals(list.get(0).getLogs().size(), test1.getModel().getLogs().size());
        Assert.assertEquals(list.get(1).getLogs().size(), test2.getModel().getLogs().size());
    }

    @Test
    public void testWithChildren() throws IOException {
        // initial, create archive:
        ExtentReports extent = new ExtentReports();
        JsonFormatter json = new JsonFormatter(JSON_ARCHIVE);
        extent.attachReporter(json);
        ExtentTest test1 = extent.createTest("Testname1", "description1");
        test1.createNode("Child1")
                .pass("Pass")
                .skip("Skip")
                .fail("Fail");
        ExtentTest test2 = extent.createTest("Testname2", "description2");
        test2.createNode("Child2")
                .warning("Warn")
                .info("Info");
        test2.createNode("Child3")
                .pass("Pass");
        extent.flush();

        // post, check archive
        extent = new ExtentReports();
        extent.createDomainFromJsonArchive(JSON_ARCHIVE);
        List<com.aventstack.extentreports.model.Test> list = extent.getReport().getTestList();

        // parent checks
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getChildren().size(), 1);
        Assert.assertEquals(list.get(1).getChildren().size(), 2);
        Assert.assertEquals(list.get(0).getStatus(), test1.getStatus());
        Assert.assertEquals(list.get(1).getStatus(), test2.getStatus());
        Assert.assertEquals(list.get(0).getName(), test1.getModel().getName());
        Assert.assertEquals(list.get(1).getName(), test2.getModel().getName());
        Assert.assertEquals(list.get(0).getDescription(), test1.getModel().getDescription());
        Assert.assertEquals(list.get(1).getDescription(), test2.getModel().getDescription());
        Assert.assertEquals(list.get(0).getLogs().size(), test1.getModel().getLogs().size());
        Assert.assertEquals(list.get(1).getLogs().size(), test2.getModel().getLogs().size());
    }

    @Test
    public void children() throws IOException {
        // initial, create archive:
        ExtentReports extent = new ExtentReports();
        JsonFormatter json = new JsonFormatter(JSON_ARCHIVE);
        extent.attachReporter(json);
        ExtentTest test1 = extent.createTest("Testname1", "description1");
        ExtentTest child1 = test1.createNode("Child1")
                .pass("Pass")
                .skip("Skip")
                .fail("Fail");
        ExtentTest test2 = extent.createTest("Testname2", "description2");
        ExtentTest child2 = test2.createNode("Child2")
                .warning("Warn")
                .info("Info");
        ExtentTest child3 = test2.createNode("Child3")
                .pass("Pass");
        extent.flush();

        // post, check archive
        extent = new ExtentReports();
        extent.createDomainFromJsonArchive(JSON_ARCHIVE);
        List<com.aventstack.extentreports.model.Test> list = extent.getReport().getTestList();

        // children checks
        Assert.assertEquals(list.get(0).getChildren().get(0).getName(), child1.getModel().getName());
        Assert.assertEquals(list.get(1).getChildren().get(0).getName(), child2.getModel().getName());
        Assert.assertEquals(list.get(1).getChildren().get(1).getName(), child3.getModel().getName());
        Assert.assertEquals(list.get(0).getChildren().get(0).getLogs().size(), child1.getModel().getLogs().size());
        Assert.assertEquals(list.get(1).getChildren().get(0).getLogs().size(), child2.getModel().getLogs().size());
        Assert.assertEquals(list.get(1).getChildren().get(1).getLogs().size(), child3.getModel().getLogs().size());
    }

    @Test
    public void testWithMedia() throws IOException {
        // initial, create archive:
        ExtentReports extent = new ExtentReports();
        JsonFormatter json = new JsonFormatter(JSON_ARCHIVE);
        extent.attachReporter(json);
        ExtentTest test1 = extent.createTest("Testname1")
                .addScreenCaptureFromPath("img.png")
                .fail("Fail", MediaEntityBuilder.createScreenCaptureFromPath("img.png").build());
        extent.flush();

        // post, check archive
        extent = new ExtentReports();
        extent.createDomainFromJsonArchive(JSON_ARCHIVE);
        List<com.aventstack.extentreports.model.Test> list = extent.getReport().getTestList();

        // parent checks
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getMedia().size(), 1);
        Assert.assertNotNull(list.get(0).getLogs().get(0).getMedia());
        Assert.assertEquals(list.get(0).getMedia().get(0).getPath(), test1.getModel().getMedia().get(0).getPath());
        Assert.assertEquals(list.get(0).getLogs().get(0).getMedia().getPath(),
                test1.getModel().getLogs().get(0).getMedia().getPath());
    }

    @Test
    public void testWithMediaBase64() throws IOException {
        // initial, create archive:
        ExtentReports extent = new ExtentReports();
        JsonFormatter json = new JsonFormatter(JSON_ARCHIVE);
        extent.attachReporter(json);
        ExtentTest test1 = extent.createTest("Testname1")
                .addScreenCaptureFromBase64String("base64string")
                .fail("Fail", MediaEntityBuilder.createScreenCaptureFromBase64String("base64string").build());
        extent.flush();

        // post, check archive
        extent = new ExtentReports();
        extent.createDomainFromJsonArchive(JSON_ARCHIVE);
        List<com.aventstack.extentreports.model.Test> list = extent.getReport().getTestList();

        // parent checks
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getMedia().size(), 1);
        Assert.assertNotNull(list.get(0).getLogs().get(0).getMedia());
        Assert.assertEquals(((ScreenCapture) list.get(0).getMedia().get(0)).getBase64(),
                ((ScreenCapture) test1.getModel().getMedia().get(0)).getBase64());
        Assert.assertEquals(((ScreenCapture) list.get(0).getLogs().get(0).getMedia()).getBase64(),
                ((ScreenCapture) test1.getModel().getLogs().get(0).getMedia()).getBase64());
    }

    @Test
    public void appendWithSparkReporter() throws IOException {
        ExtentReports extent = new ExtentReports();
        String testNamePrefix = "Append Test ";
        String textWithoutScreenshotPrefix = "Just some random text ";
        String textWithScreenshotPrefix = "Some text explaining the screenshot ";
        for (int i = 1; i < 3; i++) {
            String reportPath = "target/append/index.html";
            String jsonPath = "target/append/index.json";
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            JsonFormatter jsonReporter = new JsonFormatter(jsonPath);
            extent.createDomainFromJsonArchive(jsonPath);
            extent.attachReporter(jsonReporter, sparkReporter);
            Media screenshot = MediaEntityBuilder.createScreenCaptureFromPath("img.png").build();
            ExtentTest test = extent.createTest(testNamePrefix + i, "Test Description")
                    .log(Status.INFO, textWithoutScreenshotPrefix + i, null, null)
                    .log(Status.INFO, textWithScreenshotPrefix + i, null, screenshot);
            extent.flush();
        }
        for (com.aventstack.extentreports.model.Test test : extent.getReport().getTestList()) {
            if (test.getName().startsWith(testNamePrefix)) {
                String runNumber = test.getName().substring(testNamePrefix.length());
                for (Log logEntry : test.getLogs()) {
                    Assert.assertEquals(Status.INFO, logEntry.getStatus());
                    String details = logEntry.getDetails();
                    if (logEntry.getMedia() != null) Assert.assertEquals(details, textWithScreenshotPrefix + runNumber);
                    else Assert.assertEquals(details, textWithoutScreenshotPrefix + runNumber);
                }
            }
        }
        
    }

}
