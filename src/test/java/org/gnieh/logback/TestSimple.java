package org.gnieh.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TestSimple {

    static Logger logger = LoggerFactory.getLogger(TestSimple.class);

    public static void main(String[] args) {
        MDC.put(SystemdJournal.MESSAGE_ID, "15bbd5156ff24b6ea41468b102598b04");
        try {
            logger.info("toto");
            throw new Exception("Glups");
        } catch (Exception e) {
            logger.error("some error occurred", e);
        }
    }

}
