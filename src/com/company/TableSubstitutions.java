package com.company;

import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import ru.spbstu.pipeline.Executor;

public class TableSubstitutions implements Executor {
    enum modes{
        ENCODE, DECODE
    };

    private Object inputData;
    private Object outputData;
    private List<Producer> producers;
    private List<Consumer> consumers;
    private Status status = Status.OK;
    private Logger logger;
    private String fileParam = null;
    private int num = 0;
    private String mode = null;

    public TableSubstitutions(String configFile, Logger logger){
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
        this.logger = logger;
        ParseTable parser = new ParseTable(configFile, logger);
        fileParam = parser.paramFile();
        if(fileParam == null){
            status = Status.ERROR;
            logger.log("Error paramfile null");
            return;
        }
        num = parser.num();
        if(num == 0){
            status = Status.ERROR;
            logger.log("Error num in paramfile null");
        }
        mode = parser.mode();
        if(mode == null){
            status = Status.ERROR;
            logger.log("Error mode in paramfile null");
        }
    }

    public Status status(){
        return this.status;
    }

    public void loadDataFrom(Producer producer){
        if(producer.status() != Status.OK) {
            status = Status.ERROR;
            logger.log("Error producer is not OK");
            return;
        }
        inputData = producer.get();
    }
    public void run(){
        try {
            for (Consumer consumer: consumers){
                //consumer.loadDataFrom(this);
                if (mode.equals(modes.ENCODE.toString())) {
                    FileInputStream fin = new FileInputStream(fileParam);

                    //Массив пар
                    byte[] paramBuffer = new byte[fin.available()];
                    fin.read(paramBuffer);
                    int numberRes = 0;

                    ArrayList<Byte> paramByte = new ArrayList<Byte>();
                    for (byte b : paramBuffer) {
                        if (b != ' ' && b != '\r' && b != '\n') {
                            paramByte.add(numberRes, (Byte) b);
                            numberRes++;
                        }
                    }

                    //Входные данные
                    byte[] inBuffer = (byte[]) inputData;
                    ArrayList<Byte> inByte = new ArrayList<Byte>();
                    for (int i = 0; i < inBuffer.length; i++) {
                        if (inBuffer[i] != 0)
                            inByte.add(i, (Byte) inBuffer[i]);
                    }

                    //Максимальное количество пар
                    int paramElements = 2;
                    int limitNumberSymbol = 256;
                    if ((paramByte.size() / paramElements) > limitNumberSymbol) {
                        status = Status.EXECUTOR_ERROR;
                        logger.log("Error: The number of param in the file exceeds 256");
                        return;
                    }

                    //Проверка: повторяются ли первые элементы из пар
                    ArrayList<Byte> copyByte = new ArrayList<Byte>(paramByte);
                    int numberSymbolRepeat = 1;
                    for (int pos1 = 0; pos1 < paramByte.size(); pos1 += paramElements) {
                        int number = 0;
                        for (int pos2 = 0; pos2 < copyByte.size(); pos2 += paramElements) {
                            if (paramByte.get(pos1).equals(copyByte.get(pos2)))
                                number++;
                        }
                        if (number > numberSymbolRepeat) {
                            status = Status.EXECUTOR_ERROR;
                            logger.log("Error: The first elements in the files are repeated");
                            return;
                        }
                    }

                    //Совершаем подстановку
                    for (int pos2 = 0; pos2 < inByte.size(); pos2++) {
                        for (int pos1 = 0; pos1 < paramByte.size(); pos1 += paramElements) {
                            if (paramByte.get(pos1).equals(inByte.get(pos2))) {
                                inByte.set(pos2, paramByte.get(pos1 + 1));
                                pos2++;
                            }
                        }
                    }
                    byte[] outBuffer = new byte[inByte.size()];
                    for (int i = 0; i < inByte.size(); i++) {
                        outBuffer[i] = inByte.get(i).byteValue();
                    }
                    outputData = outBuffer;
                }
                else {
                    FileInputStream fin = new FileInputStream(fileParam);

                    //Массив пар
                    byte[] paramBuffer = new byte[fin.available()];
                    fin.read(paramBuffer);
                    int numberRes = 0;

                    ArrayList<Byte> paramByte = new ArrayList<Byte>();
                    for (byte b : paramBuffer) {
                        if (b != ' ' && b != '\r' && b != '\n') {
                            paramByte.add(numberRes, (Byte) b);
                            numberRes++;
                        }
                    }

                    //Входные данные
                    byte[] inBuffer = (byte[]) inputData;
                    ArrayList<Byte> inByte = new ArrayList<Byte>();
                    for (int i = 0; i < inBuffer.length; i++) {
                        if (inBuffer[i] != 0)
                            inByte.add(i, (Byte) inBuffer[i]);
                    }

                    //Максимальное количество пар
                    int paramElements = 2;
                    int limitNumberSymbol = 256;
                    if ((paramByte.size() / paramElements) > limitNumberSymbol) {
                        status = Status.EXECUTOR_ERROR;
                        logger.log("Error: The number of param in the file exceeds 256");
                        return;
                    }

                    //Проверка: повторяются ли первые элементы из пар
                    ArrayList<Byte> copyByte = new ArrayList<Byte>(paramByte);
                    int numberSymbolRepeat = 1;
                    for (int pos1 = 0; pos1 < paramByte.size(); pos1 += paramElements) {
                        int number = 0;
                        for (int pos2 = 0; pos2 < copyByte.size(); pos2 += paramElements) {
                            if (paramByte.get(pos1).equals(copyByte.get(pos2)))
                                number++;
                        }
                        if (number > numberSymbolRepeat) {
                            status = Status.EXECUTOR_ERROR;
                            logger.log("Error: The first elements in the files are repeated");
                            return;
                        }
                    }

                    //Совершаем подстановку
                    for (int pos2 = 0; pos2 < inByte.size(); pos2++) {
                        for (int pos1 = 0; pos1 < paramByte.size(); pos1 += paramElements) {
                            if (paramByte.get(pos1 + 1).equals(inByte.get(pos2))) {
                                inByte.set(pos2, paramByte.get(pos1));
                                pos2++;
                            }
                        }
                    }
                    byte[] outBuffer = new byte[inByte.size()];
                    for (int i = 0; i < inByte.size(); i++) {
                        outBuffer[i] = inByte.get(i).byteValue();
                    }
                    outputData = outBuffer;
                }
                consumer.loadDataFrom(this);
                consumer.run();
            }
        } catch(IOException e){
            status = Status.EXECUTOR_ERROR;
            logger.log("Error can not read paramfile");
        }
    }

    public Object get(){
        return outputData;
    }

    public void addConsumer(Consumer consumer) {
        if (consumer != null)
            consumers.add(consumer);
        else {
            status = Status.ERROR;
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
            status = Status.ERROR;
            logger.log("Error add producer executor");
        }
    }

    public void addProducers(List<Producer> producers){
        for (Producer producer: producers)
            addProducer(producer);
    }

}
