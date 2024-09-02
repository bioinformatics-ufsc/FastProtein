/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast;

import br.edu.ifsc.bioinfo.fast.protein.FastProtein;
import br.edu.ifsc.bioinfo.fast.protein.Parameters;
import br.edu.ifsc.bioinfo.fast.protein.conversor.AlignerLocalConverter;
import br.edu.ifsc.bioinfo.fast.protein.conversor.SignalP5Converter;
import br.edu.ifsc.bioinfo.fast.protein.conversor.WolfPsortConverter;
import br.edu.ifsc.bioinfo.fast.util.CommandRunner;
import br.edu.ifsc.bioinfo.fast.util.log.LevelConverter;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "FastProtein-1", requiredOptionMarker = '*', abbreviateSynopsis = true,
        description = "Fast and simple way to know more about your proteins! :)\n"
        + "Explore the project in https://github.com/bioinformatics-ufsc/FastProtein\n"
        + "Run online in https://biolib.com/UFSC/FastProtein\n\n",
        version = "1.0", sortOptions = false)
public class Main implements Callable<Integer> {

    public enum Source {
        biolib, other
    }

    @CommandLine.Option(names = {"-i", "--input"}, required = true, description = "Input file")
    File input;
    @CommandLine.Option(names = {"-s", "--subcell"}, description = "Type of organism for wolfpsort:\n"
            + "\t\t\tanimal - Animal (default)\n"
            + "\t\t\tfungi - Fungi\n"
            + "\t\t\tplant - Plant\n", defaultValue = "animal")
    WolfPsortConverter.Type wolfpsortType;

    @CommandLine.Option(names = {"-sp", "--signalp"}, description = "Type of organism for SignalP-5:\n"
            + "\t\t\teuk - Eukarya (default)\n"
            + "\t\t\tarch - Archaea\n"
            + "\t\t\tgram_pos - Gram-positive\n"
            + "\t\t\tgram_neg - Gram-negative\n", defaultValue = "euk")
    SignalP5Converter.Organism signalPorganism;

    @CommandLine.Option(names = {"-ipr", "--interpro"},
            description = "Interproscan search \n",
            defaultValue = "false")
    boolean interpro;

    @CommandLine.Option(names = {"-dp", "--interprodp"},
            description = "Add -dp flag in interproscan command \n",
            defaultValue = "false")
    boolean interproDp;

    @CommandLine.Option(names = {"-db", "--db-align"}, description = "FASTA, Diamond or Blast database file. ")
    String dbSearch;

    @CommandLine.Option(names = {"-am", "--align-method"}, description = "Choose the alignment method:\n "
            + "\t blastp \n"
            + "\t diamond (default)\n",
             defaultValue = "diamond")
    AlignerLocalConverter.AlignerEnum aligner;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output folder for generated file (default is 'fastprotein_results')", defaultValue = "fastprotein_results")
    String outputFolder;

    @CommandLine.Option(names = {"-zip", "--zip"}, description = "Zip your output folder (default is false)", defaultValue = "false")
    boolean zip;

    @CommandLine.Option(names = {"-log", "--log"}, description = "(Advanced) Level of Log4J. Choose only one (default is INFO):\n"
            + "OFF,INFO,ALL",
             defaultValue = "INFO", converter = LevelConverter.class)
    Level logLevel;

    @CommandLine.Option(names = {"-ipr_home", "--interpro_home"},
            description = "The installation directory for InterProScan. E,g /opt/interproscan-5.61-93.0"
            + "\nThis attribute replaces the value of the INTERPRO_HOME system variable only during execution.", defaultValue = "")
    String interproHome;

    @CommandLine.Option(names = {"-ipr_split", "--interpro_split"},
            description = "Split a file in <ipr_split> proteins group. (default is 500)", defaultValue = "500")
    int interproSplit;

    @CommandLine.Option(names = {"-cdt", "--copy_dir_to_temp"},
            description = "Copy all contents from a given dir to temporary folder.", defaultValue = "")
    String copyOutputContentTemp;

    @CommandLine.Option(names = {"-pause", "--pause]"},
            description = "Pause the processing after each third-party software execution (requires typing enter to continue, default is false).",
            defaultValue = "false")
    boolean pause;

    @CommandLine.Option(names = {"-dtf",},
            description = "Delete temporary files (default is true) \n",
            defaultValue = "true")
    boolean deleteTempFile;

    @CommandLine.Option(names = {"-ts", "-truncate"},
            description = "Truncate sequence - remove all non-aminoacid code from the entire sequence (default is false)\n",
            defaultValue = "false")
    boolean truncateSequence;

    @Override
    public Integer call() throws Exception {
        Parameters.ZIP = zip;
        Parameters.PAUSE = pause;
        Parameters.DELETE_TEMP_FILE = deleteTempFile;
        Parameters.TRUNCATE_SEQUENCE = truncateSequence;

        String fastproteinHome = System.getenv("FASTPROTEIN_HOME");

        if (StringUtils.isNotBlank(fastproteinHome)) {
            Parameters.FAST_PROTEIN_HOME = fastproteinHome;
        } else {
            Parameters.FAST_PROTEIN_HOME = ".";
        }
        Parameters.createTempDir();
        File fileOutputFolder = new File(outputFolder);
        if (fileOutputFolder.exists()) {
            org.apache.commons.io.FileUtils.deleteDirectory(fileOutputFolder);
        }
        fileOutputFolder.mkdirs();

        init(outputFolder);
        setLevel(logLevel);

        Parameters.INTERPRO_HOME = System.getenv("INTERPRO_HOME");
        if (Parameters.INTERPRO_HOME == null) {
            Parameters.INTERPRO_HOME = "";
        }

        debug("InterProScan Home (env) = " + Parameters.INTERPRO_HOME);

        if (interproHome != null && !interproHome.trim().equals("")) {
            Parameters.INTERPRO_HOME = interproHome;
            debug("New InterProScan Home  = " + Parameters.INTERPRO_HOME);
        }
        if (Parameters.INTERPRO_HOME.endsWith("/")) {
            Parameters.INTERPRO_HOME = Parameters.INTERPRO_HOME.substring(0, Parameters.INTERPRO_HOME.length() - 1);
        }
        Parameters.INTERPRO_SPLIT = interproSplit;
        debug("InterProScan Home = " + Parameters.INTERPRO_HOME);

        if (!copyOutputContentTemp.isEmpty()) {
            info("Copying files from " + copyOutputContentTemp + " to " + Parameters.TEMP_DIR);
            FileUtils.copyDirectory(new File(copyOutputContentTemp), new File(Parameters.TEMP_DIR));
            info("Files copied.");
            Parameters.pause();
        }

        FastProtein.run(input, wolfpsortType, signalPorganism, dbSearch, aligner, interpro, outputFolder, Source.other);
        return 0;

    }

    public static void main(String... args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }

    private static void chmod(String sh) {
        try {
            CommandRunner.run("chmod +x " + sh);
        } catch (Exception e) {
            debug("Error set chmod +x to " + sh);
            e.printStackTrace();
        }
    }

}
