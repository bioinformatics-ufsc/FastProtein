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
public class TMHMM2Converter {
    private HashMap<String, Integer> map = new HashMap<>();
    private File fasta;

    public TMHMM2Converter(File fasta) {
        this.fasta = fasta;
    }

    public void execute() {
        info("Executing TMHMM-2.0c");
        try {
            if (fasta == null) {
                throw new IOException("Fasta file is null");
            }
            String command = String.format("%s/bin/tmhmm2.sh %s %s %s", Parameters.FAST_PROTEIN_HOME, fasta.getAbsolutePath(), Parameters.getTemporaryFile("tmhmm2.txt"), Parameters.TEMP_DIR);
            debug("Command: " +command);
            CommandRunner.run(command);

            File fileTMHMM = new File(Parameters.getTemporaryFile("tmhmm2.txt"));

            if (fileTMHMM.exists()) {
                debug("Parsing file: "+fileTMHMM.getAbsolutePath());
                Scanner s = new Scanner(fileTMHMM);
                while (s.hasNext()) {
                    String ln = s.nextLine();
                    if (!ln.trim().isBlank()) {
                        String[] res = ln.split("\t");
                        res[4] = res[4].split("=")[1];
                        map.put(res[0], Integer.parseInt(res[4]));
                    }
                }
            }else{
                throw new Exception("TMHMM-2.0c - file not found");
            }
            debug("Parsing file end.");

        } catch (Exception ex) {
            error("TMHMM not executed, this feature will be skipped.");
            error("\t"+ex.getMessage());
        }
    }

    public Integer getTotalTransmembrane(String id) {
        return (map.get(id) != null) ? map.get(id) : 0;
    }

}
