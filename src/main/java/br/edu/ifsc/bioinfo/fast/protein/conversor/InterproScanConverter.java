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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import javax.xml.bind.JAXBException;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class InterproScanConverter {
    private ArrayList<Protein> proteins;
    private int limit;
    public static final String PROTEIN_ACCESSION = "Protein_accession";
    public static final String SEQUENCE_MD5_DIGEST = "Sequence_MD5_digest";
    public static final String SEQUENCE_LENGTH = "Sequence_length";
    public static final String ANALYSIS = "Analysis";
    public static final String SIGNATURE_ACCESSION = "Signature_accession";
    public static final String SIGNATURE_DESCRIPTION = "Signature_description";
    public static final String START_LOCATION = "Start_location";
    public static final String STOP_LOCATION = "Stop_location";
    public static final String SCORE = "Score";
    public static final String STATUS = "Status";
    public static final String DATE = "Date";
    public static final String INTERPRO_ANNOTATIONS_ACCESSION = "InterPro_annotations_accession";
    public static final String INTERPRO_ANNOTATIONS_DESCRIPTION = "InterPro_annotations_description";
    public static final String GO_ANNOTATIONS = "GO_annotations";
    public static final String ADDITIONAL = "Additional";

    private File fasta;
    private static String[] header = {PROTEIN_ACCESSION, SEQUENCE_MD5_DIGEST, SEQUENCE_LENGTH, ANALYSIS, SIGNATURE_ACCESSION, SIGNATURE_DESCRIPTION, START_LOCATION, STOP_LOCATION, SCORE, STATUS, DATE, INTERPRO_ANNOTATIONS_ACCESSION, INTERPRO_ANNOTATIONS_DESCRIPTION, GO_ANNOTATIONS};

    public InterproScanConverter(File fasta, ArrayList<Protein> proteins) {
        this.proteins = proteins;
        this.fasta = fasta;
    }

    public void executeLocal() {
        if (isInstalled()) {
            try {
                info("Executing InterproScan - This step is complex and may significantly increase the processing time. But it's worth it! :)");
                String command = String.format("%s/bin/interproscan.sh %s %s %s", Parameters.FAST_PROTEIN_HOME, fasta.getAbsolutePath(), Parameters.getTemporaryFile("interproTemp.tsv"), Parameters.getTemporaryFile("interpro/"));
                debug("Command: " + command);
                CommandRunner.run(command);

                File interproResult = new File(Parameters.getTemporaryFile("interproTemp.tsv"));

                if (interproResult.exists()) {
                    debug("Parsing file: " + interproResult.getAbsolutePath());

                    StringBuilder sb = new StringBuilder();
                    sb.append(String.join("\t", header));
                    sb.append("\n");
                    ArrayList<String> lines = new ArrayList<>();
                    Scanner s = new Scanner(interproResult);
                    while (s.hasNext()) {
                        String line = s.nextLine();
                        String[] cols = line.split("\t");
                        if (cols.length != header.length) {
                            String[] newLine = createEmptyArray(header.length);
                            for (int i = 0; i < cols.length; i++) {
                                if (StringUtils.isBlank(cols[i])) {
                                    cols[i] = "-";
                                }
                                newLine[i] = cols[i];
                            }
                            lines.add(String.join("\t", newLine));
                        } else {
                            for (int i = 0; i < cols.length; i++) {
                                if (StringUtils.isBlank(cols[i])) {
                                    cols[i] = "-";
                                }
                            }
                            lines.add(String.join("\t", cols));
                        }
                    }
                    sb.append(String.join("\n", lines));
                    File interprotsv = new File(Parameters.getTemporaryFile("interpro.tsv"));
                    FileWriter fw = new FileWriter(interprotsv);

                    fw.write(sb.toString());
                    fw.close();
                    File interprotemp = new File(Parameters.getTemporaryFile("interpro"));
                    if (interprotemp.exists()) {
                        debug("Interpro - Deleting temp dir: " + interprotemp);
                        interprotemp.deleteOnExit();
                    }
                    debug("Interpro - Generating interpro.tsv - " + interprotsv);
                    //interproResult.delete();
                } else {
                    throw new Exception("Interpro - File not found: interpro.tsv");
                }
                debug("Parsing file end.");
            } catch (Exception ex) {
                error("Interproscan not executed, this feature will be skipped. Error:");
                error("\t" + ex.getMessage());
            }
        }

    }


    public void updateProteins(List<Protein> proteins) {
        File iprFile = new File(Parameters.getTemporaryFile("interpro.tsv"));
        updateProteins(proteins, iprFile);
    }

    public void updateProteins(List<Protein> proteins, File iprFile) {
        if (iprFile.exists()) {
            try {
                //Parsing interpro final file
                CsvReadOptions options = CsvReadOptions.builder(iprFile).separator('\t').build();
                Table tabela = Table.read().csv(options);
                if (tabela.rowCount() == 0) {
                    error("InterproScan - no rows to analyze");
                    return;
                }
                ArrayList<String> wego = new ArrayList<>();
                ArrayList<String> ids = new ArrayList<>();
                for (Protein protein : proteins) {
                    ids.add(protein.getId());
                    Table result = tabela.where(t -> t.stringColumn(PROTEIN_ACCESSION).equalsIgnoreCase(protein.getId()));
                    for (Row row : result) {
                        if (row.getString(ANALYSIS).equalsIgnoreCase("pfam")) {
                            protein.addPfam(row.getString(SIGNATURE_DESCRIPTION));
                        }
                        if (row.getString(ANALYSIS).equalsIgnoreCase("panther")) {
                            protein.addPanther(row.getString(SIGNATURE_DESCRIPTION));
                        }

                        protein.addInterpro(row.getString(INTERPRO_ANNOTATIONS_ACCESSION), row.getString(INTERPRO_ANNOTATIONS_DESCRIPTION));
                        protein.addGO(row.getString(GO_ANNOTATIONS));
                    }
                    wego.add(String.format("%s\t%s", protein.getId(), String.join("\t", protein.getSeparatedGO())));
                }
                if (!wego.isEmpty()) {
                    FileUtils.createFile(String.join("\n", wego), "wego.txt");
                }
            } catch (IOException ex) {
                error("Error processing interproscan files");
                error("\t" + ex.getMessage());
            }

        }
    }

    private static String[] createEmptyArray(int size) {
        String[] array = new String[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = "-";
        }
        return array;
    }

    public static boolean isInstalled() {
        if (StringUtils.isNotBlank(Parameters.INTERPRO_HOME)) {
            File interproexec = new File(Parameters.INTERPRO_HOME + "/interproscan.sh");
            if (interproexec.exists()) {
                debug("Interpro installed in " + Parameters.INTERPRO_HOME);
            } else {
                info("InterProScan is not installed. For better results, please install it using the following command:");
                info("    install_interpro");
                info("interproscan.sh not found in " + Parameters.INTERPRO_HOME);
                return false;
            }
            return true;
        } else {
            info("InterProScan is not installed. For better results, please install it using the following script:");
            info("    install_interpro");
            return false;
        }
    }
}
