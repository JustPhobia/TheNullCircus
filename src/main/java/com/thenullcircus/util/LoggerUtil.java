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
                File logsDir = new File("logs");
                if(!logsDir.exists()) {
                    logsDir.mkdirs();
                }

                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.ALL);

                FileHandler fileHandler = new FileHandler(LOG_FILE, true);
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new SimpleFormatter());

                logger.addHandler(consoleHandler);
                logger.addHandler(fileHandler);
                logger.setUseParentHandlers(false);

                logger.info("[LOGGER_INIT] Logging configured for class: " + clazz.getSimpleName());

            } catch (IOException e) {
                logger.warning("[LOGGER_ERR] Could not set up file logging handlers: " + e.getMessage());
            }
        }
        return logger;
    }
}