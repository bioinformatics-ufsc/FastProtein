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
import java.util.stream.Collectors;

import br.edu.ifsc.bioinfo.fast.util.CommandRunner;
import br.edu.ifsc.bioinfo.fast.util.FastTime;
import br.edu.ifsc.bioinfo.fast.util.FileUtils;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class AlignerLocalConverter {
    public enum AlignerEnum{
        blastp("blastp.sh"), diamond("diamond.sh");
        public String command;
        AlignerEnum(String command){
            this.command=command;
        }
    }
    private HashMap<String, String> map = new HashMap<>();
    public FastTime fastTime = new FastTime();

    private AlignerEnum aligner;
    public AlignerLocalConverter(AlignerEnum aligner){
        this.aligner=aligner;
    }

    public void process(File inputFasta, String dbFasta) {
        fastTime.start();
        String name = aligner==AlignerEnum.blastp?"BLASTp":"Diamond";

        info("Executing "+name+" - Search for similarity" );
        Parameters.pause();
        try {

            File searchResults = FileUtils.hasFileOnTemp(String.format("%1$s_local/%1$s-firsthit.txt", aligner));
            if (searchResults == null) {
                debug("Starting Blast local execution");
                String command = String.format("%s/bin/%s %s %s %s", Parameters.FAST_PROTEIN_HOME, aligner.command,
                        inputFasta.getAbsolutePath(), dbFasta, Parameters.TEMP_DIR);
                debug("Command: " + command);
                fastTime.startStep();
                CommandRunner.run(command);
                fastTime.endStep();
                debug("Command: executed");
                searchResults = new File(Parameters.getTemporaryFile(String.format("%1$s_local/%1$s-firsthit.txt", aligner)));
            } else {
                info("Processing existing file - " + searchResults.getAbsolutePath());
            }

            if (searchResults.exists()) {
                debug("Parsing file: " + searchResults);
                parseResult(searchResults);
                debug("Parsing end.");
            }
        } catch (Exception e) {
            info("Error running "+aligner+": " + e.getMessage());
            info("\t" + e.getMessage());
        }
        fastTime.end();
        fastTime.showTime();
    }

    private void parseResult(File searchFirstHitFile) {
        File f = searchFirstHitFile;
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
                            String cov = cols[3];
                            String sseqid = cols[7];
                            String stitle = cols[8];
                            String description = stitle;
                            String annotation = String.format("%s [identities=%s%%, cover=%s%%]", description, ident, cov);
                            map.put(id, annotation);
                        }
                    }
                }
            } catch (Exception e) {
                error("Error parsing "+aligner+" files");
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
