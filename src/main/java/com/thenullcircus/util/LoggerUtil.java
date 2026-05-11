package com.thenullcircus.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class LoggerUtil {
    private static final String LOG_FILE = "logs/thenullcircuslog";
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        if (logger.getHandlers().length == 0) {
            try{
                new File("logs").mkdirs();
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.ALL);

                FileHandler fileHandler = new FileHandler(LOG_FILE,true);
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new SimpleFormatter());

                logger.addHandler(consoleHandler);
                logger.addHandler(fileHandler);
                logger.setUseParentHandlers(false);

            } catch (IOException e) {
                logger.warning("Could not set up file logging: " + e.getMessage());
            }
        }
        return  logger;
    }

}
