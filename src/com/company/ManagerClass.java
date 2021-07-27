package com.company;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Reader;
import ru.spbstu.pipeline.Writer;
import ru.spbstu.pipeline.logging.UtilLogger;



public class ManagerClass {
    private List<Executor> executors;
    private Reader reader;
    private Writer writer;
    private Logger logger;
    private Status status = Status.OK;

    public ManagerClass(String configFile){
        buildPipeline(configFile);
    }

    public ManagerClass(String configFile, Logger logger){
        this.logger = logger;
        buildPipeline(configFile);
    }

    public Status status() {
        return status;
    }

    public void run(){
        if (reader.status() != Status.OK) {
            this.status = Status.READER_ERROR;
            logger.log("Error reader");
            return;
        }
        reader.run();
    }

    private void buildPipeline(String configFile){
        executors = new ArrayList<>();
        ParseConfigFile parser = new ParseConfigFile(configFile, logger);
        if(parser.status() != Status.OK){
            this.status = Status.ERROR;
            logger.log("Error status is not OK");
            return;
        }
        Delimiter readerParam =  parser.reader();
        Delimiter writerParam =  parser.writer();
        Delimiter [] executorParams = parser.executors();
        try{
            Class readerExtra = Class.forName(readerParam.className);
            String readerConfig = readerParam.configFile;
            Class[] readerParameter ={String.class, Logger.class};
            reader = (ReaderClass) readerExtra.getConstructor(readerParameter).newInstance(readerConfig, logger);
        } catch (ClassNotFoundException | NullPointerException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e){
            this.status = Status.ERROR;
            logger.log("Error exception in constructor " + readerParam.className);
        }
        try{
            Class writerExtra = Class.forName(writerParam.className);
            String writerConfig = writerParam.configFile;
            Class[] writerParameter ={String.class, Logger.class};
            writer = (WriterClass) writerExtra.getConstructor(writerParameter).newInstance(writerConfig, logger);
        } catch (ClassNotFoundException | NullPointerException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e){
            this.status = Status.ERROR;
            logger.log("Error exception in constructor " + writerParam.className);
        }
        if(executorParams.length == 0){
            reader.addConsumer(writer);
            writer.addProducer(reader);
        } else {
            for (Delimiter executorParam: executorParams) {
                Executor executor = null;
                try{
                    Class executorExtra = Class.forName(executorParam.className);
                    String executorConfig = executorParam.configFile;
                    Class[] executorParameter ={String.class, Logger.class};
                    executor = (Executor) executorExtra.getConstructor(executorParameter).newInstance(executorConfig, logger);
                    executors.add(executor);
                } catch (ClassNotFoundException | NullPointerException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e){
                    this.status = Status.ERROR;
                    logger.log("Error exception in constructor " + executorParam.className);
                }
            }
            for (int i = 1; i < executors.size(); i++){
                executors.get(i-1).addConsumer(executors.get(i));
                executors.get(i).addProducer(executors.get(i-1));
            }
            reader.addConsumer(executors.get(0));
            executors.get(0).addProducer(reader);
            writer.addProducer(executors.get(executors.size() - 1));
            executors.get(executors.size() - 1).addConsumer(writer);
        }
    }

}
