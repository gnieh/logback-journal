package org.gnieh.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSimple {

    static Logger logger = LoggerFactory.getLogger(TestSimple.class);

    public static void main(String[] args) {
        try {
            logger.info("toto");
            throw new Exception();
        } catch (Exception e) {
            logger.error("some error occurred", e);
        }
    }

}
