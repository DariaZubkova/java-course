package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.Status;

public class ParseFile {
    private enum Parameters {
        input, output, num
    }

    private String fileIn = null;
    private String fileOut = null;
    private String stringNum = null;
    private int num = 0;
    private Status status = Status.OK;
    private Logger logger;

    private static final String GrammarEqual = "=";
    private static final Map<Parameters, String> map;

    static {
        map = new HashMap<>();

        map.put(Parameters.input, "INPUT_FILE");
        map.put(Parameters.output, "OUTPUT_FILE");
        map.put(Parameters.num, "NUM");
    }

    public ParseFile(String configFile, Logger logger) {
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

    public String inputFile(){
        return fileIn;
    }

    public String outputFile(){
        return fileOut;
    }

    public int num(){
        return num;
    }

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
            if (item.startsWith(map.get(Parameters.input))) {
                if (fileIn == null) {
                    int beg = map.get(Parameters.input).length();
                    int end = beg + GrammarEqual.length();
                    fileIn = item.substring(end);
                }
            }
            if (item.startsWith(map.get(Parameters.output))) {
                if (fileOut == null) {
                    int beg = map.get(Parameters.output).length();
                    int end = beg + GrammarEqual.length();
                    fileOut = item.substring(end);
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
