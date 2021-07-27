package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.Reader;
import ru.spbstu.pipeline.Consumer;

class ReaderClass implements Reader {
    private List<Consumer> consumers;
    private Logger logger;
    private String fileIn = null;
    private int num = 0;
    private byte[] readData = null;
    private Status status = Status.OK;

    public ReaderClass(String configFile, Logger logger){
        consumers = new ArrayList<>();
        this.logger = logger;
        ParseFile parser = new ParseFile(configFile, logger);
        fileIn = parser.inputFile();
        if(fileIn == null){
            this.status = Status.READER_ERROR;
            logger.log("Error inputfile null");
            return;
        }
        num = parser.num();
        if(num == 0){
            this.status = Status.READER_ERROR;
            logger.log("Error num in inputfile null");
        }
    }

    public Status status() {
        return status;
    }

    @Override
    public void run(){
        try{
            for (Consumer consumer: consumers) {
                FileInputStream fin = new FileInputStream(fileIn);
                readData = new byte[num];
                while (fin.read(readData, 0, num) != -1) {
                    consumer.loadDataFrom(this);
                    consumer.run();
                    readData = new byte[num];
                }
                fin.close();
            }
        } catch(IOException e){
            this.status = Status.READER_ERROR;
            logger.log("Error can not read inputfile " + fileIn);
        }
    }

    @Override
    public Object get(){
        return readData;
    }

    @Override
    public void addConsumer(Consumer consumer) {
        if (consumer != null)
            consumers.add(consumer);
        else {
            this.status = Status.ERROR;
            logger.log("Error add consumer reader");
        }
    }

    @Override
    public void addConsumers(List<Consumer> consumers){
        for (Consumer consumer: consumers)
            addConsumer(consumer);
    }


}
