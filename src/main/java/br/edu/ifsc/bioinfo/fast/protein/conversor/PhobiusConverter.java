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
public class PhobiusConverter {
    private HashMap<String, Boolean> mapSP = new HashMap<>();
    private HashMap<String, Integer> mapTM = new HashMap<>();
    private File fasta;

    public PhobiusConverter(File fasta) {
        this.fasta = fasta;
    }

    public void execute() {
        info("Executing Phobius");
        try {
            if (fasta == null) {
                throw new IOException("Fasta file is null");
            }
            String command = String.format("%s/bin/phobius.sh %s %s", Parameters.FAST_PROTEIN_HOME, fasta.getAbsolutePath(), Parameters.getTemporaryFile("phobius.txt"));
            debug("Command: " + command);
            CommandRunner.run(command);

            File phobius = new File(Parameters.getTemporaryFile("phobius.txt"));

            if (phobius.exists()) {
                debug("Parsing file: " + phobius.getAbsolutePath());
                Scanner s = new Scanner(phobius);
                s.nextLine();
                while (s.hasNext()) {
                    String ln = s.nextLine();
                    if (!ln.trim().isBlank()) {
                        String line = ln.replaceAll("\\s+", "\t");

                        String[] res = line.split("\t");

                        mapTM.put(res[0], Integer.parseInt(res[1]));
                        mapSP.put(res[0], res[2].equals("Y"));
                    }
                }

            } else {
                throw new Exception("Phobius - file not found");
            }
            debug("Parsing file end.");

        } catch (Exception ex) {
            error("Phobius not executed, this feature will be skipped.");
            error("\t"+ex.getMessage());
        }
    }

    public Boolean getSignalPeptide(String id) {
        return (mapSP.get(id) != null) ? mapSP.get(id) : false;
    }

    public Integer getTM(String id) {
        return (mapTM.get(id) != null) ? mapTM.get(id) : 0;
    }
}
