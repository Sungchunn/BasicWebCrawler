package io.muzoo.ssc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogging {
    private static final Logger logger = LoggerFactory.getLogger(TestLogging.class);

    public static void main(String[] args) {
        logger.info("Test logging works!");
        logger.debug("Debugging information: TestLogging initialized successfully.");
        logger.warn("Warning: This is a sample warning message.");
        logger.error("Error: This is a sample error message.");

        try {
            logger.info("Attempting a sample calculation: 10 / 2");
            int result = 10 / 2;
            logger.info("Calculation successful, result: {}", result);

            logger.info("Attempting a division by zero to generate an exception");
            int errorResult = 10 / 0;
        } catch (ArithmeticException e) {
            logger.error("An exception occurred: Division by zero", e);
        }

        logger.info("TestLogging execution completed.");
    }
}