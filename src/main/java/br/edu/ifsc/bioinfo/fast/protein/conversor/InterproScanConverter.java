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
import br.edu.ifsc.bioinfo.fast.util.FastTime;
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
    public FastTime fastTime = new FastTime();

    private File fasta;
    private static String[] header = {PROTEIN_ACCESSION, SEQUENCE_MD5_DIGEST, SEQUENCE_LENGTH, ANALYSIS, SIGNATURE_ACCESSION, SIGNATURE_DESCRIPTION, START_LOCATION, STOP_LOCATION, SCORE, STATUS, DATE, INTERPRO_ANNOTATIONS_ACCESSION, INTERPRO_ANNOTATIONS_DESCRIPTION, GO_ANNOTATIONS, ADDITIONAL};

    public InterproScanConverter(File fasta, ArrayList<Protein> proteins) {
        this.proteins = proteins;
        this.fasta = fasta;
    }

    /**
     *
     * @param input
     * @param disablePreCalc if true, the flag -dp is added in InterProScan command:
     *                       Optional.  Disables use of the precalculated match lookup
     *                       service.  All match calculations will be run locally.
     * @return
     * @throws Exception
     */
    public boolean execute(File input, boolean disablePreCalc) throws Exception {
        String arqResult = input.getAbsolutePath() + ".txt";
        String command = String.format("%s/interproscan.sh -i %s -o %s -T %s --goterms -f tsv %s",
                Parameters.INTERPRO_HOME, input.getAbsolutePath(),
                arqResult,
                Parameters.getTemporaryFile("interpro/"),
                disablePreCalc?"-dp":"");
        debug("Command: " + command);
        fastTime.startStep();
        CommandRunner.run(command);
        fastTime.endStep();
        return new File(arqResult).exists();
    }

    public File executeLocal() {
        boolean error=false;
        Parameters.pause();
        ArrayList<File> filesToDelete = new ArrayList<>();
        int count = 3;
        if (isInstalled()) {
            try {

                info("Executing InterProScan - Search for Gene Ontology");
                info("   This step is complex and may significantly increase the processing time. But it's worth it! :)");
                debug("Splitting file");
                List<File> files = FASTASplitter.subfasta(proteins, Parameters.INTERPRO_SPLIT, "interpro");
                filesToDelete.addAll(files);
                debug(files.size() + " generated");
                ArrayList<File> generated = new ArrayList<>();
                for (File file : files) {

                    try {
                        File fileProcess = FileUtils.hasFileOnTemp(file.getName() + ".txt");
                        if (fileProcess == null) {
                            boolean fileGenerate = execute(file, false);
                            if (!fileGenerate) {
                                error = true;
                                info(" " + file.getName() + " not generated, we will try to run calculation locally (see -dp parameter in InterProScan command)");
                                fileGenerate = execute(file, true);
                            }

                            if (fileGenerate) {
                                error = false;
                                String arqResult = file.getAbsolutePath() + ".txt";
                                fileProcess = new File(arqResult);
                                debug("Adding file to process: " + fileProcess);
                                generated.add(fileProcess);
                                debug("Adding file to delete in the end of the process" + file.getAbsolutePath());
                                filesToDelete.add(file);
                            } else {
                                info("File not found " + file.getName() + ".txt" + " this dataset will be ignored");
                                info("This error was caused by InterProScan, probably due to network issues. " +
                                                "\nInterProScan queries a remote server to speed up its execution. " +
                                                "\nCheck if there is any network blocking for 'www.ebi.ac.uk'. " );

                            }
                        } else {
                            info("Processing existing file - " + fileProcess.getAbsolutePath());
                            debug("Adding file to process: " + fileProcess);
                            generated.add(fileProcess);
                            debug("Adding file to delete in the end of the process" + file.getAbsolutePath());
                            filesToDelete.add(file);
                        }
                    }catch(Exception e){
                        info("Error processing the file: " +file.getAbsolutePath() +" skipping it");
                        e.printStackTrace();
                    }
                }
                debug("Join interpro files: " + generated.size());
                ArrayList<String> allLines = new ArrayList<>();

                for (File arq : generated) {
                    if (arq != null) {
                        debug("Processing: " + arq.getAbsolutePath());
                        try {
                            allLines.addAll(org.apache.commons.io.FileUtils.readLines(arq));
                            debug("Deleting processed file: " + arq.getAbsolutePath());
                            filesToDelete.add(arq);
                        } catch (Exception e) {
                            info("File not found: " + arq.getAbsolutePath());
                            e.printStackTrace();
                        }
                    }else{
                        info("File not found " + arq.getAbsolutePath());
                        error=true;
                    }
                }
                if(error){
                    info("There are some missing files. We are interrupting the process because it may cause inconsistencies in the final intepro.tsv output");
                    info("Run again your data with flag -cdt "+Parameters.TEMP_DIR);
                    info("This flag will copy all your generated files and will process again! (You will save some precious minutes! :D)");
                    System.exit(0);
                }else{
                    Parameters.addFilesToDelete(filesToDelete);
                }

                debug("Creating interproTemp.tsv");

                File interproResult = FileUtils.createFile(String.join("\n", allLines), "interproTemp.tsv");

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

                    File interprotemp = new File(Parameters.getTemporaryFile("interproTemp.tsv"));
                    if (interprotemp.exists()) {
                        Parameters.addFileToDelete(interprotemp);
                    }
                    debug("Interpro - Generating interpro.tsv ");
                    debug("Generating file end - " + interprotsv.getAbsolutePath());
                    return interprotsv;
                } else {
                    info("Interpro - File not found: " + interproResult.getAbsolutePath());
                    info("There are some missing files. We are interrupting the process because it may cause inconsistencies in the final interpro.tsv output");
                    info("Run again your data with flag -cdt "+Parameters.TEMP_DIR);
                    info("This flag will copy all your generated files and will process again! (You will save some precious minutes! :D)");
                    System.exit(0);
                }

            } catch (Exception ex) {
                info("InterProScan not executed, this feature will be skipped. Try to run again. Error:");
                info("\t" + ex.getMessage());
                System.exit(0);
            }
        }else{
            info("InterProScan not Installed. Remove the flags -ipr or check the installation process at https://github.com/bioinformatics-ufsc/FastProtein");

            System.exit(0);
        }
        return null;

    }


    public void updateProteins(List<Protein> proteins) {
        fastTime.start();
        File iprFile = FileUtils.hasFileOnTemp("interpro.tsv");
        if (iprFile == null) {
            iprFile = executeLocal();
            if(iprFile!=null){
                debug("Processing existing file - " + iprFile.getAbsolutePath());
                updateProteins(proteins, iprFile);
            }else{
                info("File not found - interpro.tsv");
            }
        } else {
            info("Processing existing file - " + iprFile.getAbsolutePath());
            updateProteins(proteins, iprFile);
        }
        fastTime.end();
        fastTime.showTime();
    }

    public void updateProteins(List<Protein> proteins, File iprFile) {
        if (iprFile.exists()) {
            try {
                //Parsing interpro final file
                CsvReadOptions options = CsvReadOptions.builder(iprFile).separator('\t').build();
                Table tabela = Table.read().csv(options);
                if (tabela.rowCount() == 0) {
                    error("InterProScan - no rows to analyze");
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
                error("Error processing InterProScan files");
                error("\t" + ex.getMessage());
                ex.printStackTrace();
            }

        } else {
            info("Erro file not found: " + iprFile.getAbsolutePath());
            info("Skipping InterProScan");
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
