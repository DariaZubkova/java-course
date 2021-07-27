package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.Status;

class ParseConfigFile {
    private enum Parameters {
        read, write, executor, num
    }

    private String reader = null;
    private String writer = null;
    private String executors = null;
    private String stringNum = null;
    private int num = 0;
    private Status status = Status.OK;
    private Logger logger;

    private static final String GrammarEqual = "=";
    private static final String GrammarExecutors = ",";
    private static final String GrammarPoint = ";";
    private static final Map<ParseConfigFile.Parameters, String> map;

    static {
        map = new HashMap<>();

        map.put(ParseConfigFile.Parameters.read, "reader");
        map.put(ParseConfigFile.Parameters.write, "writer");
        map.put(Parameters.executor, "executors");
        map.put(ParseConfigFile.Parameters.num, "NUM");
    }

    public ParseConfigFile(String configFile, Logger logger) {
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

    public Delimiter writer() {
        if (writer == null) {
            this.status = Status.ERROR;
            logger.log("Error writer delimiter");
            return null;
        }
        return new Delimiter(writer, GrammarPoint, logger);
    }

    public Delimiter reader() {
        if (reader == null) {
            this.status = Status.ERROR;
            logger.log("Error reader delimiter");
            return null;
        }
        return new Delimiter(reader, GrammarPoint, logger);
    }


    public Delimiter[] executors() {
        if (executors == null) {
            this.status = Status.ERROR;
            logger.log("Error executors delimiter");
            return new Delimiter[0];
        }
        String[] executorsStr = executors.split(GrammarExecutors);
        Delimiter[] executor = new Delimiter[executorsStr.length];
        for (int i = 0; i < executor.length; i++)
            executor[i] = new Delimiter(executorsStr[i], GrammarPoint, logger);
        return executor;
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
            if (item.startsWith(map.get(Parameters.read))) {
                if (reader == null) {
                    int beg = map.get(Parameters.read).length();
                    int end = beg + GrammarEqual.length();
                    reader = item.substring(end);
                }
            }
            if (item.startsWith(map.get(Parameters.write))) {
                if (writer == null) {
                    int beg = map.get(Parameters.write).length();
                    int end = beg + GrammarEqual.length();
                    writer = item.substring(end);
                }
            }
            if (item.startsWith(map.get(Parameters.executor))) {
                if (executors == null) {
                    int beg = map.get(Parameters.executor).length();
                    int end = beg + GrammarEqual.length();
                    executors = item.substring(end);
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
