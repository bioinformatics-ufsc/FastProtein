package br.edu.ifsc.bioinfo.fast.util;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

public class FastTime {
    private long start;
    private long end;
    private ArrayList<Long> externalCommands = new ArrayList<>();

    private long startStep;

    public void addExternalCommand(long time){
        externalCommands.add(time);
    }

    public void startStep(){
        startStep = System.currentTimeMillis();
    }
    public void endStep(){
        long endStep  = System.currentTimeMillis();
        externalCommands.add(endStep-startStep);
    }
    public void start(){
        start = System.currentTimeMillis();
    }

    public void end(){
        end = System.currentTimeMillis();
    }
    public long getTotal(){
        return end-start;
    }
    public long getExternalCommandsTotal(){
        return externalCommands.stream().reduce(0L,Long::sum);
    }

    private static DateTimeFormatter formatter;

    static {
        formatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.MINUTE_OF_HOUR, 1)
                .appendLiteral(":")
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendLiteral(":")
                .appendValue(ChronoField.MILLI_OF_SECOND, 3)
                .toFormatter();
    }


    public String getFormattedCommand(){

        return format(getExternalCommandsTotal());
    }
    public String getFormattedFast(){

        return format(getTotal()-getExternalCommandsTotal());
    }

    public String getFormattedTotal(){
        return format(getTotal());
    }

    public void showTime() {
        info("\tExternal Command time: " + getFormattedCommand());
        info("\t     FastProtein time: " + getFormattedFast());
        info("\t           Total time: " + getFormattedTotal());
    }

    private static String format(long time){
        Duration duration = Duration.ofMillis(time);
        return duration.toString().substring(2);
    }
    public static void main(String[] args) throws Exception{
        FastTime fast = new FastTime();
        fast.start();
        Thread.sleep(7000);
       // fast.startStep();
        Thread.sleep(1000*3);
        //fast.endStep();
        Thread.sleep(1000);
       // fast.startStep();
        Thread.sleep(5000);
        //fast.endStep();

        fast.end();
        System.out.println(fast.toString());
    }
}
