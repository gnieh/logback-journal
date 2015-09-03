package org.gnieh.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TestPercentEscaping {

    static Logger logger = LoggerFactory.getLogger(TestPercentEscaping.class);

    public static void main(String[] args) {
        MDC.put(SystemdJournal.MESSAGE_ID, "we get away with %s, since it uses the null terminator arg");
        logger.info("fine");
        MDC.put(SystemdJournal.MESSAGE_ID, "we get away with %i as well, and it converts to 0");
        logger.info("fine");

        // Everything below will crash unless % is escaped (% -> %%)
        
        MDC.put(SystemdJournal.MESSAGE_ID, "this %s %s crashes since there's no 2nd subsequent arg");
        logger.info("boom");
        
        MDC.put(SystemdJournal.MESSAGE_ID, "this %1$ causes the JVM to abort");
        logger.info("boom");
    }

}
