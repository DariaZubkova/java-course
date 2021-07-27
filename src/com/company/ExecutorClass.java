package com.company;

import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import ru.spbstu.pipeline.Executor;

public class ExecutorClass implements Executor {
    private enum Modes {
        ENCODE, DECODE
    }
    private Object inputData;
    private Object outputData;
    private List<Producer> producers;
    private List<Consumer> consumers;
    private Status status = Status.OK;
    private Logger logger;
    private int num = 0;
    private String mode = null;

    public ExecutorClass(String configFile, Logger logger){
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
        this.logger = logger;
        ParseExecutor parser = new ParseExecutor(configFile, logger);
        num = parser.num();
        if(num == 0){
            this.status = Status.ERROR;
            logger.log("Error num in inputfile null");
        }
        mode = parser.mode();
        if(mode == null){
            this.status = Status.ERROR;
            logger.log("Error mode in inputfile null");
        }
    }

    public Status status(){
        return this.status;
    }

    public void loadDataFrom(Producer producer){
        if(producer.status() != Status.OK){
            this.status = Status.ERROR;
            logger.log("Error producer is not OK");
            return;
        }
        inputData = producer.get();
    }
    public void run(){
        for (Consumer consumer: consumers){
            if (mode.equals(Modes.ENCODE.toString())) {
                //Входные данные
                byte[] inBuffer = (byte[]) inputData;
                ArrayList<Byte> inByte = new ArrayList<Byte>();
                for (int i = 0; i < inBuffer.length; i++) {
                    if (inBuffer[i] != 0)
                        inByte.add(i, (Byte) inBuffer[i]);
                }

                //Совершаем подстановку
                byte[] outBuffer = new byte[inByte.size() + 2];
                for (int i = 1; i < inByte.size() + 1; i++) {
                    outBuffer[i] = inByte.get(i - 1).byteValue();
                }
                outBuffer[0] = '1';
                outBuffer[inByte.size() + 1] = '1';
                outputData = outBuffer;
            }
            else {
                //Входные данные
                byte[] inBuffer = (byte[]) inputData;
                ArrayList<Byte> inByte = new ArrayList<Byte>();
                for (int i = 0; i < inBuffer.length; i++) {
                    if (inBuffer[i] != 0)
                        inByte.add(i, (Byte) inBuffer[i]);
                }

                //Совершаем подстановку
                byte[] outBuffer = new byte[inByte.size() - 2];
                for (int i = 0; i < inByte.size() - 2; i++) {
                    outBuffer[i] = inByte.get(i + 1).byteValue();
                }
                outputData = outBuffer;
            }
            consumer.loadDataFrom(this);
            consumer.run();
        }
    }

    public Object get(){
        return outputData;
    }

    public void addConsumer(Consumer consumer) {
        if (consumer != null)
            consumers.add(consumer);
        else {
            this.status = Status.ERROR;
            logger.log("Error add consumer executor");
        }
    }

    public void addConsumers(List<Consumer> consumers){
        for (Consumer consumer: consumers)
            addConsumer(consumer);
    }

    public void addProducer(Producer producer){
        if (producer != null)
            producers.add(producer);
        else {
            this.status = Status.ERROR;
            logger.log("Error add producer executor");
        }
    }

    public void addProducers(List<Producer> producers){
        for (Producer producer: producers)
            addProducer(producer);
    }

}

