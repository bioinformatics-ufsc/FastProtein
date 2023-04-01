/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.conversor;

import br.edu.ifsc.bioinfo.fast.util.CommandRunner;
import br.edu.ifsc.bioinfo.fast.protein.Parameters;
import br.edu.ifsc.bioinfo.fast.util.GeneOntologyUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class PredGPIConverter {
    private HashMap<String, Boolean> map = new HashMap<>();
    private HashMap<String, String> mapGOEnvidence = new HashMap<>();
    private File fasta;

    public PredGPIConverter(File fasta) {
        this.fasta = fasta;
    }

    public void execute() {
        info("Executing PredGPI");
        try {
            if (fasta == null) {
                throw new IOException("Fasta file is null");
            }
            String command = String.format("%s/bin/predgpi.sh %s %s", Parameters.FAST_PROTEIN_HOME, fasta.getAbsolutePath(), Parameters.getTemporaryFile("predgpi.txt"));
            debug("Command: " + command);
            CommandRunner.run(command);
            File predgpi = new File(Parameters.getTemporaryFile("predgpi.txt"));
            if(predgpi.exists()) {
                Scanner s = new Scanner(predgpi);
                debug("Parsing file: " + Parameters.getTemporaryFile("predgpi.txt"));
                while (s.hasNext()) {
                    String ln = s.nextLine();
                    if (!ln.trim().isBlank()) {
                        String[] res = ln.split("\t");
                        map.put(res[0], res[2].equals("GPI-anchor"));
                        String[] evidences = res[8].split(";");
                        for (String evidence : evidences) {
                            if (evidence.startsWith("Ontology_term=")) {
                                mapGOEnvidence.put(res[0], evidence.replaceAll("Ontology_term=", ""));
                            }
                        }

                    }
                }
                debug("Parsing file end.");
            }else{
                throw new Exception("PredGPI - file not found");
            }

        } catch (Exception ex) {
            error("Predgpi not executed, this feature will be skipped.");
            error("\t"+ex.getMessage());
        }
    }

    public Boolean isGPIAnchored(String id) {
        return (map.get(id) != null) ? map.get(id) : false;
    }

    public String getGOEvidence(String id) {
        String res = "- ";
        String go = mapGOEnvidence.get(id);
        if (go != null) {
            res = GeneOntologyUtil.getFullOntology(go);
        }
        return res;
    }
}
