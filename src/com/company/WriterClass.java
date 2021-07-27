package com.company;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Writer;

public class WriterClass implements Writer{
    private Object data;
    private List<Producer> producers;
    private ParseFile parser;
    private String fileOut = null;
    private int num = 0;
    private FileOutputStream file;
    private Status status = Status.OK;
    private Logger logger;

    public WriterClass(String configFile, Logger logger){
        producers = new ArrayList<>();
        this.logger = logger;
        parser = new ParseFile(configFile, logger);
        fileOut = parser.outputFile();
        if(fileOut == null){
            this.status = Status.WRITER_ERROR;
            logger.log("Error outputfile null");
            return;
        }
        num = parser.num();
        if(num == 0){
            this.status = Status.WRITER_ERROR;
            logger.log("Error num on outputfile null");
            return;
        }
        try {
            file = new FileOutputStream(fileOut);
        }
        catch (IOException e) {
            this.status = Status.WRITER_ERROR;
            logger.log("Error can not open outputfile null");
        }
    }

    @Override
    public Status status() {
        return status;
    }

    public void loadDataFrom(Producer producer){
        if(producer.status() != Status.OK){
            this.status = Status.WRITER_ERROR;
            logger.log("Error producer is not OK");
            return;
        }
        data = producer.get();
    }

    public void run(){
        if(status != Status.OK) {
            this.status = Status.WRITER_ERROR;
            logger.log("Error status is not OK");
            return;
        }
        writeData();
    }


    private void writeData(){
        if (data == null) {
            this.status = Status.WRITER_ERROR;
            logger.log("Error inputData is null");
            return;
        }
        /*if(data.getClass() != byte[].class){
            status = Status.WRITER_ERROR;
            logger.log("Error inputData can't be converted to byte[]");
            return;
        }*/

        if(fileOut == null){
            this.status = Status.WRITER_ERROR;
            logger.log("Error outputfile null");
            return;
        }
        try {
            file.write((byte[])data);
        } catch (IOException e){
            this.status = Status.WRITER_ERROR;
            logger.log("Error can not write outputfile");
        }
    }

    public void addProducer(Producer producer){
        if (producer != null)
            producers.add(producer);
        else {
            this.status = Status.ERROR;
            logger.log("Error add producer writer");
        }
    }

    public void addProducers(List<Producer> producers){
        for (Producer producer: producers)
            addProducer(producer);
    }

}
