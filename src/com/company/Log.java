package com.company;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import ru.spbstu.pipeline.logging.Logger;


public class Log implements Logger{
    private static FileOutputStream log = null;
    public Log(){}

    @Override
    public void log(String s) {
        if(log == null) {
            try {
                log = new FileOutputStream("log.txt");
                log.write(s.getBytes());
            } catch (IOException e) {
                System.out.println("Can not open log file");
            }
        }else{
            try{
                log.write(s.getBytes());
            } catch (IOException e) {
                System.out.println("Can not open log file");
            }
        }
    }

    @Override
    public void log(Level level, String s) {
        log(s);
    }

    @Override
    public void log(String s, Throwable throwable) {
        log(s);
    }

    @Override
    public void log(Level level, String s, Throwable throwable) {
        log(s);
    }
}
