/*
 * Copyright 2009 Solnet Solutions Limited (http://www.solnetsolutions.co.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tohu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

import org.apache.commons.httpclient.HttpConnection;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.util.URI;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tohu.test.element.Element;
import org.tohu.test.element.ElementFactory;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * This is the parent class of all Selenium Unit Tests.
 * 
 * @author John Bebbington
 */
public abstract class AbstractSeleniumTest {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractSeleniumTest.class);
	
	/** Supported browser types */
	public static enum BrowserType {
		FIREFOX, IEXPLORER, SAFARI
	};

	/** Supported Answer types */
	public static enum AnswerType {
		TEXT, NUMBER, DECIMAL, DATE, BOOLEAN, LIST
	};

	/** Supported HTML widget types */
	public static enum WidgetType {
		TEXT, TEXTAREA, DROPDOWN, RADIO_BOOLEAN, RADIO_GROUP, DATEPICKER, BUTTON, LINK, IMAGE, CHECKBOX, PARAGRAPH, FILE
	};

	/** The selenium browser handle */
	protected static DefaultSelenium selenium = null;
	/** The maximum number of milliseconds to wait for the AJAX call and subsequent processing to complete */
	protected static String processingTimeout = "10000";
	/** URL of the page currently being tested */
	protected static String testURL = null;
	/** ID of hidden field which stores a flag indicating the JQuery client is processing, see dynamicUI_main.js */
	protected static String isGUIBusyID = null;
	/** System time at which the test case started for debugging purposes */
	protected static long startTime = System.currentTimeMillis();
	/** Background thread to run the Selenium Server */
	protected static SeleniumServerThread seleniumThread = null;
	/** Background thread to run the Jerry Server */
	protected static JettyServerThread jettyThread = null;
	/** Flag indicating if debug messages should be printed to the console */
	protected static boolean DEBUG = false;
	/** The test browser type */
	protected static BrowserType browserType = null;

    protected static Map<WidgetType, Element> elementMap;
	/**
	 * Starts up the Selenium Server and/or Jetty Server if required and starts the specified
	 * browser.
	 * 
	 * @param browserType Type of browser the test is to be run in, one of: FIREFOX, IEXPLORER or SAFARI.
	 * @param testURL URL of the web page to be tested.
	 * @param isGUIBusyID ID of hidden field which stores a flag indicating the JQuery client is processing.
	 * @throws Exception
	 */
	public static void basicSetUp(
			BrowserType browserType,
			String testURL,
			String isGUIBusyID) throws Exception {
		debug("AbstractSeleniumTest.basicSetUp() browserType="
				+ browserType
				+ " testURL="
				+ testURL);

		AbstractSeleniumTest.testURL = testURL;
		AbstractSeleniumTest.isGUIBusyID = isGUIBusyID;
		AbstractSeleniumTest.browserType = browserType;
		String browserString = null;
		switch (browserType) {
			case FIREFOX:
				browserString = "*firefoxproxy";
				break;
			case IEXPLORER:
				browserString = "*iexploreproxy";
				break;
			case SAFARI:
				browserString = "*safariproxy";
				break;
		}

		if (!isHTTPServerRunning("localhost", 4444)) {
			//  Selenium Server not running on localhost:4444, start it as a separate thread.
			debug("AbstractSeleniumTest.basicSetUp() Starting Selenium Server");
			seleniumThread = new SeleniumServerThread(Thread.currentThread());
			seleniumThread.start();
			debug("AbstractSeleniumTest.basicSetUp() Waiting for Selenium Server");
			LockSupport.parkNanos(60000000000l);
			debug("AbstractSeleniumTest.basicSetUp() Selenium Server Started");
		}

		// Determine the Selenium base URL and if we need to start a Jetty Server.
		URI uri = new URI(testURL);
		// TODO Replace the above line with this one if Selenium ever allows a Jetty version
		//      more recent then 5.1, see http://jira.openqa.org/browse/SRC-176
		//HttpURI uri = new HttpURI(testURL);

		String baseURL = "http://localhost:8080/";
		if (uri.getScheme().equals("http")) {
			baseURL = "http://" + uri.getHost() + ":" + uri.getPort() + "/";
			if (!isHTTPServerRunning(uri.getHost(), uri.getPort())) {
				// HTTP Server not running there.
				if (uri.getHost().equals("localhost")) {
					// Start Jetty Server as a separate thread.
					debug("AbstractSeleniumTest.basicSetUp() Starting Jetty Server");
					jettyThread = new JettyServerThread(Thread.currentThread(), uri.getPort());
					jettyThread.start();
					debug("AbstractSeleniumTest.basicSetUp() Waiting for Jetty Server");
					LockSupport.parkNanos(60000000000l);
					debug("AbstractSeleniumTest.basicSetUp() Jetty Server Started");
				}
				else {
					// Nothing we can do Jetty Server only runs locally.
					fail("No HTTP server running at " + baseURL);
				}
			}
		}
		debug("AbstractSeleniumTest.basicSetUp() baseURL=" + baseURL);

		debug("AbstractSeleniumTest.basicSetUp() Starting browser");
		selenium = new DefaultSelenium("localhost", 4444, browserString, baseURL);
		try {
			selenium.start();
		}
		catch (Exception ex) {
			debug("AbstractSeleniumTest.basicSetUp() exception=" + ex);
			if (((browserType == BrowserType.SAFARI) && ex.toString().contains(
				"java.lang.RuntimeException: Safari could not be found in the path!"))
					|| ((browserType == BrowserType.FIREFOX) && ex.toString().contains(
						"java.lang.RuntimeException: Firefox could not be found in the path!"))
					|| ((browserType == BrowserType.IEXPLORER) && ex.toString().contains(
						"java.lang.RuntimeException: Internet Explorer could not be found in the path!"))) {
				// Browser not installed, treat this as a successful test.
				debug("AbstractSeleniumTest.basicSetUp() browser not installed.");
				assumeTrue(false);
			}
			throw ex;
		}

		elementMap = new HashMap<WidgetType, Element>();
        for(WidgetType widget : WidgetType.values()) {
            Element element = ElementFactory.getElement(widget);
            element.setSelenium(selenium);
            elementMap.put(widget, element);          
        }
	        
		debug("AbstractSeleniumTest.basicSetUp() done");
	}

	/**
	 * Opens the test URL with the specified parameters.
	 * 
	 * @param testParams Array of parameters to be passed to the test page.
	 * @throws Exception
	 */
	public static void openURL(String[] testParams) throws Exception {
		// Add parameters.
		String fullURL = testURL + "?isGUIBusyID=" + isGUIBusyID;
		for (int i = 0; i < testParams.length; i++) {
			fullURL += "&" + testParams[i];
		}
		debug("AbstractSeleniumTest.openURL() fullURL=" + fullURL);
		selenium.open(fullURL);
		// Wait up to 30 seconds for the initial page to load.
		selenium.waitForPageToLoad("30000");
		// Make sure we have focus on the main browser window.
		selenium.selectWindow("null");
		debug("AbstractSeleniumTest.openURL() done");
	}

	/**
	 * Closes the browser and stops any background threads which were started in basicSetUp.
	 * 
	 * @throws Exception
	 */
	public static void basicTearDown() throws Exception {
		selenium.stop();
		if (jettyThread != null) {
			debug("AbstractSeleniumTest.basicTearDown() Stopping Jetty Server");
			jettyThread.stopServer();
			jettyThread.join();
		}
		if (seleniumThread != null) {
			debug("AbstractSeleniumTest.basicTearDown() Stopping Selenium Server");
			seleniumThread.stopServer();
			seleniumThread.join();
		}
		debug("AbstractSeleniumTest.basicTearDown() Done");
	}

	/**
	 * Waits up to 10 seconds for the AJAX call and subsequent processing to complete.
	 * @param step Description of where we are for debugging purposes.
	 */
	protected static void waitWhileGUIIsBusy(String step) {
		debug("AbstractWebDriverTest.waitWhileGUIIsBusy() step=" + step);
		selenium.waitForCondition("(selenium.getValue(\"//input[@id='"
				+ isGUIBusyID
				+ "']\") == 'false')", processingTimeout);
		debug("AbstractWebDriverTest.waitWhileGUIIsBusy() done");
	}

	/**
	 * Set the value in the text field for the specified question to the specified answer.
	 * @param quesionID
	 * @param answer
	 */
	protected void setTextAnswer(String questionID, String answer) {
		// Set field value.
		selenium.type(questionID + "_input", answer);
		waitWhileGUIIsBusy("setTextAnswer: questionID=" + questionID);
	}

	/**
	 * Returns the value in the text field for the specified question.
	 * @param quesionID
	 * @return String
	 */
	protected String getTextAnswer(String questionID) {
		return selenium.getValue(questionID + "_input");
	}

	/**
	 * Activates the specified action.
	 * @param quesionID
	 */
	protected void mouseDownControl(String actionID) {
		selenium.mouseDown(actionID);
		waitWhileGUIIsBusy("clickControl: actionID=" + actionID);
	}

	/**
	 * Toggles the checkbox for the specified question.
	 * @param quesionID
	 */
	protected void clickCheckboxAnswer(String questionID) {
		try {
			selenium.click(questionID + "_input");
		}
		catch (RuntimeException ex) {
			if ((browserType == BrowserType.IEXPLORER)
					&& (jettyThread != null)
					&& ex.getMessage().equals(
						"ERROR: Command execution failure. Please search the forum at http://clearspace.openqa.org for error details from the log window.  The error message is: Unspecified error.")) {
				// For some reason Selenium throws this exception when simulating a click event in
				// IE running against the Jetty Server/Test Harness, but seems to continue fine if
				// you ignore it.
				debug("AbstractSeleniumTest.clickCheckboxAnswer() Ignoring exception: " + ex);
			}
			else {
				throw ex;
			}
		}
		waitWhileGUIIsBusy("clickCheckboxAnswer: questionID=" + questionID);
	}

	/**
	 * Returns the current checkbox value for the specified question.
	 * @param quesionID
	 * @return boolean
	 */
	protected boolean getCheckboxAnswer(String questionID) {
		return selenium.isChecked(questionID + "_input");
	}

	/**
	 * Sets the radio button group for the specified question to the specified answer value.
	 * @param quesionID
	 * @param answer
	 */
	protected void clickRadioButtonAnswer(String questionID, String answer) {
		try {
			selenium.click(questionID + "_input_" + answer);
		}
		catch (RuntimeException ex) {
			if ((browserType == BrowserType.IEXPLORER)
					&& (jettyThread != null)
					&& ex.getMessage().equals(
						"ERROR: Command execution failure. Please search the forum at http://clearspace.openqa.org for error details from the log window.  The error message is: Unspecified error.")) {
				// For some reason Selenium throws this exception when simulating a click event in
				// IE running against the Jetty Server/Test Harness, but seems to continue fine if
				// you ignore it.
				debug("AbstractSeleniumTest.clickRadioButtonAnswer() Ignoring exception: " + ex);
			}
			else {
				throw ex;
			}
		}
		waitWhileGUIIsBusy("clickRadioButtonAnswer: questionID=" + questionID);
	}

	/**
	 * Gets the current value of the radio button group for the specified question.
	 * @param quesionID
	 * @return The answer
	 */
	protected String getRadioButtonAnswer(String questionID) {
		if (browserType == BrowserType.IEXPLORER) {
			return selenium.getAttribute("//span[@id='"
					+ questionID
					+ "_input']/input[@type='radio' and @checked='true']/@value");
		}
		else {
			return selenium.getAttribute("//span[@id='"
					+ questionID
					+ "_input']/input[@type='radio' and @checked='']/@value");
		}
	}

	/**
	 * Sets the drop-down list for the specified question to the specified answer value.
	 * @param quesionID
	 * @param answer
	 */
	protected void selectDropDownAnswer(String questionID, String answer) {
		selenium.select(questionID + "_input", "value=" + answer);
		waitWhileGUIIsBusy("selectDropDownAnswer: questionID=" + questionID);
	}

	/**
	 * Gets the selected drop-down list value for the specified question.
	 * @param quesionID
	 * @return the Answer
	 */
	protected String getDropDownAnswer(String questionID) {
		return selenium.getSelectedValue(questionID + "_input");
	}

	/**
	 * Checks that all properties of the Note HTML match those specified.
	 * 
	 * @param path XPath to the note's primary div.
	 * @param widget Type of HTML widget.
	 * @param id The note ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param label The note label text to check for (null means don't check). 
	 */
	protected void checkNote(
			WidgetType widget,
			String id,
			String[] presentationStyles,
			String label) {
		assertTrue("Note " + id + " not found ", 
		            selenium.isElementPresent("css=#" + id));
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute("css=#" + id + "@class");
			debug("AbstractSeleniumTest.checkNote() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Note", true);
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
		if (label != null) {
			if (widget == WidgetType.IMAGE) {
					assertTrue(id + " no image", 
					           selenium.isElementPresent("css=#" + id + "_image"));
			}
			else {
				assertEquals(id + " wrong label", 
				             label, 
				             selenium.getText("css=#" + id + "_label"));
			}
		}

	}

	/**
	 * Checks that all properties of a Control HTML match those specified.
	 * 
	 * @param path XPath to the control's primary div.
	 * @param widget Type of HTML widget.
	 * @param id The control ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param label The control label text to check for (null means don't check). 
	 */
	protected void checkControl(
			WidgetType widget,
			String id,
			String[] presentationStyles,
			String label) {
		if (widget == WidgetType.BUTTON) {
			assertTrue("Button control " + id + " not found", 
			           selenium.isElementPresent("css=#" + id));
			if (label != null) {
				assertEquals("Button " + id + " wrong label", 
				             label, 
				             selenium.getAttribute("css=#" + id + "@value"));
			}
		}
		else {
			assertTrue("Link control " + id + " not found", selenium.isElementPresent("css=#" + id));
			if (label != null) {
				assertEquals("Link " + id + " wrong label", 
				             label, 
				             selenium.getText("css=#" + id));
			}
		}
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute("css=#" + id + "@class");
			debug("AbstractSeleniumTest.checkControl() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Control", true);
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
	}

	/**
	 * Checks that all properties of a Group HTML match those specified.
	 * 
	 * @param path XPath to the group's primary div.
	 * @param id The group ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param label The group label text to check for (null means don't check).
	 * @param items List of item IDs to check for, in order (null means don't check). 
	 */
	protected void checkGroup(
			String id,
			String[] presentationStyles,
			String label,
			String[] items) {
		assertTrue("Group " + id + " not found", selenium.isElementPresent("css=#" + id));
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute("css=#" + id + "@class");
			debug("AbstractSeleniumTest.checkGroup() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Group", true);
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
		if (label != null) {
			assertEquals(id + " wrong label", 
			        label,
			        selenium.getText("css=#" + id + "_label"));
		}
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				assertTrue(
					id + " item " + i + " missing",
					selenium.isElementPresent("css=#" + items[i]));		
			}
		}
	}

	/**
	 * Checks that all properties of a Questionnaire HTML match those specified.
	 * 
	 * @param path XPath to the questionnaire's primary div.
	 * @param id The questionnaire ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param label The questionnaire label text to check for (null means don't check).
	 * @param items List of item IDs to check for, in order (null means don't check). 
	 */
	protected void checkQuestionnaire(
			String id,
			String[] presentationStyles,
			String label,
			String[][] items) {
		assertTrue(
			"Questionnaire form " + id + " not found",
			selenium.isElementPresent("css=#" + id + "_form"));
		assertTrue(
			"Questionnaire group " + id + " not found",
			selenium.isElementPresent("css=#" + id));
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute("css=#" + id + "@class");
			debug("AbstractSeleniumTest.checkGroup() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Questionnaire", true);
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
		if (label != null) {
			assertEquals(id + " wrong label", 
			        label, 
			        selenium.getText("css=#" + id + "_label"));
		}
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
			    assertTrue(selenium.isElementPresent("css=#" + items[i][0]));
				assertEquals(
					id + " item " + i + " missing",
					items[i][1],
					selenium.getText("css=#" + items[i][0] + "_label"));
			}
		}
	}

	/**
	 * Checks the specified style is present or not present.
	 * 
	 * @param id ID of the HTML element.
	 * @param classAttr The class attribute of the HTML element.
	 * @param className The name of the class to check for.
	 * @param present The presence to check for.
	 */
	protected void checkStyle(String id, String classAttr, String className, boolean present) {
		if (present) {
			assertTrue(
				id + " does not have style '" + className + "'",
				(" " + classAttr + " ").contains(" " + className + " "));
		}
		else {
			assertFalse(id + " has style '" + className + "'", (" " + classAttr + " ").contains(" "
					+ className
					+ " "));
		}
	}

	/**
	 * Checks that the specified message is displayed in an alert box.
	 * 
	 * @param message The message text.
	 */
	protected void checkAlert(String message) {
		assertTrue("Alert '" + message + "' not displayed", selenium.isAlertPresent());
		assertEquals("Alert '" + message + "' not displayed", message, selenium.getAlert());
	}

	/**
	 * Returns true if an item with the specified ID is present on the page.
	 * @param itemID
	 * @return boolean
	 */
	protected boolean isItemPresent(String itemID) {
		return selenium.isElementPresent("//div[@id='" + itemID + "']");
	}

	/**
	 * Handles an exception thrown by the test case.
	 */
	protected static void handleException(String msg, Exception ex) throws Exception {
		error(msg, ex);
		logger.error(selenium.retrieveLastRemoteControlLogs());
		//Thread.sleep(60000);
		throw ex;
	}

	/**
	 * Displays debugging messages to the stdout with the time in milliseconds since the test case started.
	 * @param msg The message.
	 */
	protected static void debug(String msg) {
		if (logger.isDebugEnabled()) {
			logger.debug("time=" + (System.currentTimeMillis() - startTime) + ": " + msg);
		}
	}

	/**
	 * Displays error messages to the stderr with the time in milliseconds since the test case started.
	 * @param msg The message.
	 * @param ex Optional exception.
	 */
	protected static void error(String msg, Exception ex) {
		logger.error("time=" + (System.currentTimeMillis() - startTime) + ": " + msg);
		if (ex != null) {
			ex.printStackTrace();
		}
	}
	
    // Check all the JavaScript hooks have been called.
    protected void assertHooks() {
        assertEquals(
            "preQuestionnaireLoad hook not called",
            "true",
            selenium.getEval("window.calledPreQuestionnaireLoad"));
        assertEquals(
            "preRefreshScreen hook not called",
            "true",
            selenium.getEval("window.calledPreRefreshScreen"));
        assertEquals(
            "postRefreshScreen hook not called",
            "true",
            selenium.getEval("window.calledPostRefreshScreen"));
        assertEquals(
            "preProcessDelete hook not called",
            "true",
            selenium.getEval("window.calledPreProcessDelete"));
        assertEquals(
            "postProcessDelete hook not called",
            "true",
            selenium.getEval("window.calledPostProcessDelete"));
        assertEquals(
            "preProcessCreate hook not called",
            "true",
            selenium.getEval("window.calledPreProcessCreate"));
        assertEquals(
            "postProcessCreate hook not called",
            "true",
            selenium.getEval("window.calledPostProcessCreate"));
        assertEquals(
            "preProcessUpdate hook not called",
            "true",
            selenium.getEval("window.calledPreProcessUpdate"));
        assertEquals(
            "postProcessUpdate hook not called",
            "true",
            selenium.getEval("window.calledPostProcessUpdate"));
        assertEquals(
            "preChangeEvent hook not called",
            "true",
            selenium.getEval("window.calledPreChangeEvent"));
        assertEquals(
            "postChangeEvent hook not called",
            "true",
            selenium.getEval("window.calledPostChangeEvent"));
        assertEquals(
            "onChangeEvent hook not called",
            "true",
            selenium.getEval("window.calledOnChangeEvent"));
        assertEquals(
            "preActionEvent hook not called",
            "true",
            selenium.getEval("window.calledPreActionEvent"));
        assertEquals(
            "postActionEvent hook not called",
            "true",
            selenium.getEval("window.calledPostActionEvent"));
        assertEquals(
            "onActionEvent hook not called",
            "true",
            selenium.getEval("window.calledOnActionEvent"));
        assertEquals(
            "onGetQuestionnaireActions hook not called",
            "true",
            selenium.getEval("window.calledOnGetQuestionnaireActions"));
        assertEquals(
            "onGetObjectClass hook not called",
            "true",
            selenium.getEval("window.calledOnGetObjectClass"));     
    }
	   
	/**
	 * Returns true if there is an HTTP server running on the specified host and port.
	 * @param host Host name.
	 * @param port Port number.
	 * @return boolean
	 */
	protected static boolean isHTTPServerRunning(String host, int port) {
		boolean retVal = false;
		try {
			HttpConnection connection = new HttpConnection(host, port);
			connection.open();
			retVal = true;
			connection.close();
		}
		catch (IOException ioe) {
			retVal = false;
		}
		return retVal;
	}

	/**
	 * Thread class for running the Selenium Server on localhost:4444.
	 */
	protected static class SeleniumServerThread extends Thread {
		private SeleniumServer server = null;
		private Thread parentThread = null;

		public SeleniumServerThread(Thread parentThread) {
			this.parentThread = parentThread;
		}

		public void run() {
			try {
				RemoteControlConfiguration rcc = new RemoteControlConfiguration();
				// Uncomment for debugging.
				//rcc.setBrowserSideLogEnabled(true);
				//rcc.setDebugMode(true);
				//rcc.setLogOutFileName("SeleniumServer.log");
				server = new SeleniumServer(rcc);
				server.start();
			}
			catch (Exception ex) {
				error("SeleniumServerThread.run()", ex);
			}
			finally {
				// Notify the parent thread that the server has finished starting up.
				LockSupport.unpark(parentThread);
			}
		}

		public void stopServer() throws Exception {
			server.stop();
		}
	}

	/**
	 * Thread class for running a Jetty HTTP Server which serves up the test page and the
	 * dummy xml files for the AJAX calls.
	 */
	protected static class JettyServerThread extends Thread {
		private HttpServer server = null;
		//private Server server = null;
		private Thread parentThread = null;
		private int port = 0;

		public JettyServerThread(Thread parentThread, int port) {
			this.parentThread = parentThread;
			this.port = port;
			debug("JettyServerThread() port=" + port);
		}

		public void run() {
			try {
				server = new HttpServer();
				SocketListener listener = new SocketListener();
				listener.setPort(port);
				listener.setMinThreads(1);
				listener.setMaxThreads(5);
				server.addListener(listener);
				HttpContext context = server.addContext("/");
				context.setResourceBase(this.getClass().getResource("../../../../test-classes/").toExternalForm());
				context.setRedirectNullPath(true);
				context.addWelcomeFile("test.html");
				debug("JettyServerThread.run() serving " + context.getBaseResource());
				ResourceHandler handler = new ResourceHandler();
				handler.setAcceptRanges(true);
				handler.setDirAllowed(true);
				context.addHandler(handler);
				//context.addHandler(new NotFoundHandler());
				//server.setStopGracefully(true);
				// Uncomment for debugging.
				//server.setTrace(true);
				//NCSARequestLog log = new NCSARequestLog("JettyServer.log");
				//log.setExtended(true);
				//log.setLogCookies(true);
				//log.setLogLatency(true);
				//server.setRequestLog(log);
				server.start();

				// TODO Replace the above code with this if Selenium ever allows a Jetty version
				//      more recent then 5.1, see http://jira.openqa.org/browse/SRC-176
				//ResourceHandler handler = new ResourceHandler();
				//handler.setWelcomeFiles(new String[] { "test.html" });
				//handler.setResourceBase(this.getClass().getResource("../../../").toExternalForm());
				//debug("JettyServerThread.run() serving " + handler.getBaseResource());
				//HandlerList handlers = new HandlerList();
				//handlers.setHandlers(new Handler[] { handler, new DefaultHandler() });
				//server = new Server(port);
				//server.setHandler(handlers);
				//server.start();
			}
			catch (Exception ex) {
				error("JettyHandlerThread.run()", ex);
			}
			finally {
				// Notify the parent thread that the server has finished starting up.
				LockSupport.unpark(parentThread);
			}
		}

		public void stopServer() throws Exception {
			server.stop();
			server.destroy();
		}
	}

}
