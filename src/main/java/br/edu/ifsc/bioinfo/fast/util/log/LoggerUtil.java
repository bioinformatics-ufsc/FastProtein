package br.edu.ifsc.bioinfo.fast.util.log;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;
import org.apache.log4j.*;

import java.io.IOException;

public class LoggerUtil {
    private static final Logger logger = LogManager.getLogger(LoggerUtil.class);
    public static void init(String outputfolder){
        FileAppender fileAppender = null;
        try {
            String logFilePath = outputfolder+"/console.log"; // Define the log file path dynamically
            fileAppender = new FileAppender(new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p: %m%n"), logFilePath);
            debug("Logger output file: " + logFilePath);
            logger.addAppender(fileAppender);
            fileAppender.setImmediateFlush(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void disable(){
        logger.setLevel(Level.OFF);
    }
    public static void setLevel(Level level){
        logger.setLevel(level);
    }
    public static void enable(){
        logger.setLevel(Level.ALL);
    }
    public static void info(String message){
        logger.info(message);
    }
    public static void info(){
        info("");
    }
    public static void error(String message){
        logger.error(message);
    }
    public static void warn(String message){
        logger.warn(message);
    }
    public static void debug(String message){
        logger.debug(message);
    }
}
