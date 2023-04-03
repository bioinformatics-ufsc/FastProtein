/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.conversor;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.edu.ifsc.bioinfo.fast.util.CommandRunner;
import br.edu.ifsc.bioinfo.fast.util.FileUtils;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class BlastLocalConverter {
    private HashMap<String, String> map = new HashMap<>();


    public void process(File inputFasta, File dbFasta) {
        Parameters.pause();
        try {

            File blastResults = FileUtils.hasFileOnTemp("blast_local/blast-firsthit.txt");
            if (blastResults == null) {
                debug("Starting Blast local execution");
                String command = String.format("%s/bin/blast.sh %s %s %s", Parameters.FAST_PROTEIN_HOME, inputFasta.getAbsolutePath(), dbFasta.getAbsolutePath(), Parameters.TEMP_DIR);
                debug("Command: " + command);
                CommandRunner.run(command);
                debug("Command: executed");
                blastResults = new File(Parameters.getTemporaryFile("blast_local/blast-firsthit.txt"));
            } else {
                info("Processing existing file - " + blastResults.getAbsolutePath());
            }

            if (blastResults.exists()) {
                debug("Parsing file: " + blastResults);
                parseResult(blastResults);
                debug("Parsing end.");
            }
        } catch (Exception e) {
            info("Error running blast.sh: " + e.getMessage());
            info("\t" + e.getMessage());
        }
    }

    private void parseResult(File blastFirstHitFile) {
        File f = blastFirstHitFile;
        if (f.exists()) {
            try {
                Path path = f.toPath();
                List<String> lines = Files.lines(path).collect(Collectors.toList());
                Iterator<String> it = lines.iterator();
                //Skipping 1st line
                it.next();
                while (it.hasNext()) {
                    String ln = it.next();
                    if (!ln.isEmpty()) {
                        String[] cols = ln.split("\t");
                        if (cols.length >= 8) {
                            String id = cols[0];
                            String ident = cols[2];
                            String pos = cols[4];
                            String sseqid = cols[7];
                            String stitle = cols[8];
                            String description = stitle;
                            String annotation = String.format("%s [identities=%s%%, positives=%s%%]", description, ident, pos);
                            map.put(id, annotation);
                        }
                    }
                }
            } catch (Exception e) {
                error("Error parsing BLASTp files");
                error("\t" + e.getMessage());
            }

        }
    }

    public String getAnnotation(String proteinId) {
        if (!map.containsKey(proteinId))
            return "-";
        else
            return map.get(proteinId);
    }

}
