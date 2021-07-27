package com.company;

import ru.spbstu.pipeline.logging.UtilLogger;

import java.io.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final int numArgs = 1;
    private static final String  fileLog= "log.txt";
    public static void main(String [] args){
        UtilLogger utilLogger;
        Logger logger= Logger.getLogger("log");
        utilLogger = UtilLogger.of(logger);
        FileHandler fileHandler;
        Formatter SFormatter = new SimpleFormatter();
        try {
            fileHandler = new FileHandler(fileLog, false);
        }
        catch (IOException e) {
            utilLogger.log("Can not open logger");
            return;
        }
        fileHandler.setFormatter(SFormatter);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        if(args.length != numArgs)
            System.out.println("Error number of arguments");
        String configFile = args[0];
        ManagerClass manager = new ManagerClass(configFile);
        manager.run();
    }
}