/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.conversor;

import br.edu.ifsc.bioinfo.fast.util.CommandRunner;
import br.edu.ifsc.bioinfo.fast.protein.Parameters;
import br.edu.ifsc.bioinfo.fast.util.FastTime;
import br.edu.ifsc.bioinfo.fast.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Scanner;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class WolfPsortConverter {
    public FastTime fastTime = new FastTime();
    private static HashMap<String, String> mapLocalization = new HashMap<>();

    static {
        mapLocalization.put("cysk", "Cytoskeleton");
        mapLocalization.put("cyto", "Cytosol");
        mapLocalization.put("E.R.", "E.R");
        mapLocalization.put("extr", "Extracellular");
        mapLocalization.put("golg", "Golgi apparatus");
        mapLocalization.put("mito", "Mitochondrion");
        mapLocalization.put("nucl", "Nucleus");
        mapLocalization.put("plas", "Plasma membrane");
        mapLocalization.put("pero", "Peroxisome");
        mapLocalization.put("vacu", "Vacuolar membrane");
        mapLocalization.put("chlo", "Chloroplast");
        mapLocalization.put("lyso", "Lysosome");
    }

    public enum Type {
        animal, fungi, plant, none;
    }

    private HashMap<String, String> map = new HashMap<>();
    private File fasta;

    private long startTime;
    private long endTime;
    private long commandTime;

    public WolfPsortConverter(File fasta) {
        this.fasta = fasta;
    }

    public void execute(Type type) {
        fastTime.start();

        Parameters.pause();
        info("Executing WoLFPSORT - Subcellular localization prediction");

        try {
            if (fasta == null) {
                throw new IOException("Fasta file is null");
            }
            File wolfpsortFile = FileUtils.hasFileOnTemp("wolfpsort.txt");
            if (wolfpsortFile == null) {

                String command = String.format("%s/bin/wolfpsort.sh %s %s %s", Parameters.FAST_PROTEIN_HOME, type, fasta.getAbsolutePath(), Parameters.getTemporaryFile("wolfpsort.txt"));
                debug("Command " + command);
                fastTime.startStep();
                CommandRunner.run(command);
                fastTime.endStep();
                wolfpsortFile = new File(Parameters.getTemporaryFile("wolfpsort.txt"));
            } else {
                info("Processing existing file - " + wolfpsortFile.getAbsolutePath());
            }

            if (wolfpsortFile.exists()) {
                debug("Parsing file: " + wolfpsortFile.getAbsolutePath());
                Scanner s = new Scanner(wolfpsortFile);

                while (s.hasNext()) {
                    String line = s.nextLine();
                    if (!line.startsWith("#") && !line.trim().isBlank()) {
                        String[] val = line.split(" ");
                        map.put(val[0], val[1]);
                    }
                }
            } else {
                throw new Exception("WoLFPSORT - File not found");
            }
            debug("Parsing file end.");

        } catch (Exception ex) {
            error("WolfPsort not executed, this feature will be skipped.");
            error("\t" + ex.getMessage());
        }
        fastTime.end();
        fastTime.showTime();
    }


    public String getLocation(String id) {
        String loc = (map.get(id) != null) ? map.get(id) : "-";
        return getCategory(loc);
    }

    public static String getCategory(String local) {
        if (mapLocalization.get(local) != null) {
            return mapLocalization.get(local);
        } else {
            return "Other";
        }
    }

}
