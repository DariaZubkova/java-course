package com.company;
import ru.spbstu.pipeline.logging.Logger;

public class Delimiter {
    public final String className;
    public final String configFile;
    private static final int numParam = 2;

    public Delimiter(String str, String delimiter, Logger logger){
        String [] param = str.split(delimiter);
        if (param.length != numParam){
            logger.log("Error number of param");
            className = null;
            configFile = null;
        } else {
            className = param[0];
            configFile = param[1];
        }
    }
}
