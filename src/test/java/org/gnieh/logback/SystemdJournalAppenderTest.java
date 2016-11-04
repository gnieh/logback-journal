package org.gnieh.logback;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class SystemdJournalAppenderTest {

    private static final Logger logger = LoggerFactory.getLogger(SystemdJournalAppenderTest.class);

    private static final Logger logWithSource = LoggerFactory.getLogger("log-with-source");

    @After
    public void clearMdc() {
        MDC.clear();
    }

    @Test
    public void testLogSimple() throws Exception {
        MDC.put(SystemdJournal.MESSAGE_ID, "15bbd5156ff24b6ea41468b102598b04");
        logger.info("toto");
    }

    @Test
    public void testLogException() throws Exception {
        MDC.put(SystemdJournal.MESSAGE_ID, "722fa2bde8344f88975c8d6abcd884c8");
        try {
            throw new Exception("Glups");
        } catch (Exception e) {
            logger.error("some error occurred", e);
        }
    }

    @Test
    public void testWithStringPlaceholder() throws Exception {
        MDC.put(SystemdJournal.MESSAGE_ID, "we get away with %s, since it uses the null terminator arg");
        logger.info("fine");
        MDC.put(SystemdJournal.MESSAGE_ID, "we get away with %i as well, and it converts to 0");
        logger.info("fine");
    }

    @Test
    public void testLogWithTwoStringPlaceholder() throws Exception {
        // It will crash unless % is escaped (% -> %%)
        MDC.put(SystemdJournal.MESSAGE_ID, "this %s %s crashes since there's no 2nd subsequent arg");
        logger.info("boom");

        MDC.put(SystemdJournal.MESSAGE_ID, "this %1$ causes the JVM to abort");
        logger.info("boom");
    }

    @Test
    public void testWithCustomMdc() throws Exception {
        MDC.put("some-key", "some value");
        logger.info("some message");

        MDC.put("special_key%s==", "value with special characters: %s %s %1$");
        logger.info("some other message");
    }

    @Test
    public void testLogWithSource() throws Exception {
        logWithSource.info("some message");
    }

    @Test
    public void testLogWithSourceAndException() throws Exception {
        Exception exception = new RuntimeException("some exception");
        logWithSource.error("some exception", exception);
    }


}
