package com.company;

import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParseExecutor {

    private enum Parameters {
        mode, num
    }

    private String stringNum = null;
    private int num = 0;
    private String mode = null;
    private Status status = Status.OK;
    private Logger logger;

    private static final String GrammarEqual = "=";
    private static final Map<Parameters, String> map;

    static {
        map = new HashMap<>();

        map.put(Parameters.mode, "MODE");
        map.put(Parameters.num, "NUM");
    }

    public ParseExecutor(String configFile, Logger logger) {
        this.logger = logger;
        try{
            readConfig(configFile);
        } catch(IOException e){
            this.status = Status.ERROR;
            logger.log("Error can not read file " + configFile);
        }
    }

    public Status status() {
        return status;
    }

    public int num(){
        return num;
    }

    public String mode() {return mode;}

    private void readConfig(String configFile) throws IOException {
        FileInputStream fin = new FileInputStream(configFile);
        String str;
        String strDelete = "\n"; //Разделение
        byte[] buffer = new byte[fin.available()];
        fin.read(buffer);
        str = new String(buffer);
        str = str.replaceAll(" ", "");
        str = str.replaceAll("\r", "");
        String[] parameters = str.split(strDelete);
        for (String item : parameters) {
            if (item.startsWith(map.get(Parameters.mode))) {
                if (mode == null) {
                    int beg = map.get(Parameters.mode).length();
                    int end = beg + GrammarEqual.length();
                    mode = item.substring(end);
                }
            }
            if (item.startsWith(map.get(Parameters.num))) {
                if (stringNum == null) {
                    int beg = map.get(Parameters.num).length();
                    int end = beg + GrammarEqual.length();
                    stringNum = item.substring(end);
                    num = Integer.parseInt(stringNum);
                }
            }
        }

        fin.close();
    }

}
