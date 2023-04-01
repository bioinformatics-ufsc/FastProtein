package br.edu.ifsc.bioinfo.fast.util.log;

import org.apache.log4j.Level;
import picocli.CommandLine;

public class LevelConverter implements CommandLine.ITypeConverter<Level> {
    public Level convert(String type) {
        switch (type.toUpperCase()) {
            case "OFF":
                return Level.OFF;
            case "ALL":
                return Level.ALL;
            default:
                return Level.INFO;
        }
    }
}
