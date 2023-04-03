/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.conversor;

import br.edu.ifsc.bioinfo.fast.util.CommandRunner;
import br.edu.ifsc.bioinfo.fast.protein.Parameters;
import br.edu.ifsc.bioinfo.fast.protein.entity.Protein;
import br.edu.ifsc.bioinfo.fast.util.FASTASplitter;
import br.edu.ifsc.bioinfo.fast.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class SignalP5Converter {

    /**
     * --organism
     */
    public enum Organism {
        euk,
        arch,
        gram_pos,
        gram_neg
    }

    private HashMap<String, String> map = new HashMap<>();
    private File fasta;

    public SignalP5Converter(File fasta) {
        this.fasta = fasta;
    }

    public void execute(Organism org, ArrayList<Protein> proteins) {
        Parameters.pause();
        info("Executing SignalP-5");
        try {

            File signalpFile = FileUtils.hasFileOnTemp("signalp5.txt");
            if (signalpFile == null) {
                debug("Spliting file");
                List<File> files = FASTASplitter.subfasta(proteins, 3000, "signalp");
                debug(files.size() + " generated");
                ArrayList<File> generated = new ArrayList<>();
                for (File file : files) {
                    String arqResult = file.getAbsolutePath() + ".txt";

                    String command = String.format("%s/bin/signalp5.sh %s %s %s", Parameters.FAST_PROTEIN_HOME, file.getAbsolutePath(), org.toString(), arqResult);
                    debug("Command:" + command);
                    CommandRunner.run(command);
                    File fileProcess = new File(arqResult);
                    debug("Adding file to process: " + arqResult);
                    generated.add(fileProcess);
                    file.delete();
                    debug("Deleting file to process" + arqResult);

                }
                debug("Adding header in SignalP5 file");
                StringBuilder signalPout = new StringBuilder();
                signalPout.append("# SignalP-5.0	Organism: euk	").append("\n");
                signalPout.append("# ID	Prediction	SP(Sec/SPI)	OTHER	CS Position").append("\n");
                debug("Adding lines");
                for (File file : generated) {
                    if (file.exists()) {
                        Scanner s = new Scanner(file);
                        while (s.hasNext()) {
                            String ln = s.nextLine();
                            if (!ln.startsWith("#")) {
                                signalPout.append(ln).append("\n");
                            }
                        }
                    }
                    file.delete();
                }
                signalpFile = FileUtils.createFile(signalPout.toString(), "signalp5.txt");
                debug("Creating Signalp5 file " + Parameters.getTemporaryFile("signalp5.txt"));
            }else {
                info("Processing existing file - " + signalpFile.getAbsolutePath());
            }

            if(signalpFile.exists()) {
                Scanner s = new Scanner(signalpFile);
                s.nextLine();
                s.nextLine();
                while (s.hasNext()) {
                    String ln = s.nextLine();
                    if (!ln.trim().isBlank() || !ln.startsWith("#")) {
                        String[] res = ln.split("\t");
                        map.put(res[0], res[1]);
                    }
                }
                debug("Parsing file end.");
            }else{
                debug(signalpFile.getAbsolutePath() + " not exists");
            }
        } catch (Exception ex) {
            error("SignalP5 not executed, this feature will be skipped.");
            error("\t" + ex.getMessage());
        }
    }

    public String getSignal(String id) {
        return (map.get(id) != null) ? map.get(id) : "-";
    }
}
