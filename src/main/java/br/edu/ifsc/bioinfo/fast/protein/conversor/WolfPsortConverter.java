/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.conversor;

import br.edu.ifsc.bioinfo.fast.util.CommandRunner;
import br.edu.ifsc.bioinfo.fast.protein.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class WolfPsortConverter {
    private static HashMap<String, String> mapLocalization = new HashMap<>();
    static{
        mapLocalization.put("cyts","Cytoskeleton");
        mapLocalization.put("cyto","Cytosol");
        mapLocalization.put("E.R.","Endoplasmic Reticulum");
        mapLocalization.put("extr","Extracellular");
        mapLocalization.put("golg","Golgi apparatus");
        mapLocalization.put("mito","Mitochondrion");
        mapLocalization.put("nucl","Nucleus");
        mapLocalization.put("plas","Plasma membrane");
        mapLocalization.put("pero","Peroxisome");
        mapLocalization.put("vacu","Vacuolar membrane");
        mapLocalization.put("chlo","Chloroplast");
    }

    public enum Type {
        animal, fungi, plant, none;
    }

    private HashMap<String, String> map = new HashMap<>();
    private File fasta;

    public WolfPsortConverter(File fasta) {
        this.fasta = fasta;
    }

    public void execute(Type type) {

        info("Executing WoLFPSORT");

        try {
            if (fasta == null) {
                throw new IOException("Fasta file is null");
            }
            String command = String.format("%s/bin/wolfpsort.sh %s %s %s", Parameters.FAST_PROTEIN_HOME, type, fasta.getAbsolutePath(), Parameters.getTemporaryFile("wolfpsort.txt"));
            debug("Command " +command);
            CommandRunner.run(command);

            File wolfpsortFile = new File(Parameters.getTemporaryFile("wolfpsort.txt"));
            if(wolfpsortFile.exists()) {
                debug("Parsing file: " + wolfpsortFile.getAbsolutePath());
                Scanner s = new Scanner(wolfpsortFile);

                while (s.hasNext()) {
                    String line = s.nextLine();
                    if (!line.startsWith("#") && !line.trim().isBlank()) {
                        String[] val = line.split(" ");
                        map.put(val[0], val[1]);
                    }
                }
            }else{
                throw new Exception("WoLFPSORT - File not found");
            }
            debug("Parsing file end.");

        } catch (Exception ex) {
            error("WolfPsort not executed, this feature will be skipped.");
            error("\t"+ex.getMessage());
        }
    }

    public String getLocation(String id) {
        return (map.get(id) != null) ? map.get(id) : "-";
    }

    public static String getCategory(String local){
        if(mapLocalization.get(local)!=null){
            return mapLocalization.get(local);
        }else{
            return "Other";
        }
    }
}
