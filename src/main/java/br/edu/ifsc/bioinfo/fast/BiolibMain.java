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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.debug;

@CommandLine.Command(name = "FastProtein-1 (Biolib main version)", requiredOptionMarker = '*', abbreviateSynopsis = true,
        description = "Fast and simple way to know more about your proteins! :)\n" +
                "Explore the project in https://github.com/bioinformatics-ufsc/FastProtein\n" +
                "Run online in https://biolib.com/UFSC/FastProtein", version = "1.0", sortOptions = false)
public class BiolibMain implements Callable<Integer> {
    @CommandLine.Option(names = {"-i", "--input"}, required = true, description = "Input file")
    File input;
    @CommandLine.Option(names = {"-s", "--subcell", "--subcellular_localization"}, description = "Type of organism for wolfpsort:\n"
            + "\t\t\tanimal - Animal (default)\n"
            + "\t\t\tfungi - Fungi\n"
            + "\t\t\tplant - Plant\n"
            + "\t\t\tnone - No prediction\n", defaultValue = "animal")
    WolfPsortConverter.Type wolfpsortType;

    @CommandLine.Option(names = {"-sp", "--signalp", "--signal_peptide_organism"}, description = "Type of organism for SignalP-5:\n"
            + "\t\t\teuk - Eukarya (default)\n"
            + "\t\t\tarch - Archaea\n"
            + "\t\t\tgram_pos - Gram-positive\n"
            + "\t\t\tgram_neg - Gram-negative\n", defaultValue = "euk")
    SignalP5Converter.Organism signalPorganism;
    @CommandLine.Option(names = {"-ipr", "--interpro"},
            description = "Interproscan search\n"
                    + "true - execute\n"
                    + "false - don't execute (default)",
            defaultValue = "false")
    String interpro;

    @CommandLine.Option(names = {"-db", "--db-align"}, description = "FASTA file used to create a database for a local query")
    File fastaDb;

    @CommandLine.Option(names = {"-am", "--align-method"}, description = "Choose the alignment method:\n " +
            "\t blast \n" +
            "\t diamond (default)\n"
            , defaultValue = "diamond")
    AlignerLocalConverter.AlignerEnum aligner;

    @CommandLine.Option(names = {"-log", "--log"}, description = "(Advanced) Level of Log4J. Choose only one:\n" +
            "OFF,INFO,ALL"
            , defaultValue = "INFO", converter = LevelConverter.class)
    Level logLevel;


    @CommandLine.Option(names = {"-ipr_home", "--interpro_home"},
            description = "The installation directory for InterProScan. E,g /opt/interproscan-5.61-93.0" +
                    "\nThis attribute replaces the value of the INTERPRO_HOME system variable only during execution.", defaultValue = "/bioinformatic/interproscan-5.61-93.0")
    String interproHome;

    @Override
    public Integer call() throws Exception {
        Parameters.FAST_PROTEIN_HOME = ".";
        Parameters.createTempDir();
        String outputfolder = "fastprotein";
        File fileOutputFolder = new File(outputfolder);
        if (fileOutputFolder.exists())
            org.apache.commons.io.FileUtils.deleteDirectory(fileOutputFolder);
        fileOutputFolder.mkdirs();

        init(outputfolder);
        setLevel(logLevel);
        boolean interpro = this.interpro.equals("true");



        Parameters.INTERPRO_HOME = interproHome;



        debug("Setting permissions - init");
        chmod(String.format("%s/bin/blastp.sh", Parameters.FAST_PROTEIN_HOME));
        chmod(String.format("%s/bin/fastprotein.sh", Parameters.FAST_PROTEIN_HOME));
        chmod(String.format("%s/bin/interproscan.sh", Parameters.FAST_PROTEIN_HOME));
        chmod(String.format("%s/bin/phobius.sh", Parameters.FAST_PROTEIN_HOME));
        chmod(String.format("%s/bin/predgpi.sh", Parameters.FAST_PROTEIN_HOME));
        chmod(String.format("%s/bin/signalp5.sh", Parameters.FAST_PROTEIN_HOME));
        chmod(String.format("%s/bin/tmhmm2.sh", Parameters.FAST_PROTEIN_HOME));
        chmod(String.format("%s/bin/wolfpsort.sh", Parameters.FAST_PROTEIN_HOME));

        debug("Setting permissions - done");
        FastProtein.run(input, wolfpsortType, signalPorganism, fastaDb, aligner, interpro, outputfolder, Main.Source.biolib);
        return 0;

    }

    public static void main(String... args) {
        System.exit(new CommandLine(new BiolibMain()).execute(args));
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
