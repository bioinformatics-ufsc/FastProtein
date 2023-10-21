package br.edu.ifsc.bioinfo.fast.util;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;

import java.io.File;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

public class ChartUtil {
    public static boolean createCharts(File outputTSV) {
        try {
            info("Creating Charts");
            String command = String.format("%s/bin/charts.py %s", Parameters.FAST_PROTEIN_HOME, outputTSV.getAbsolutePath());
            debug("Command " + command);
            CommandRunner.run(command);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            debug("Error generating charts: " + e.getMessage());
            return false;
        }
    }
}
