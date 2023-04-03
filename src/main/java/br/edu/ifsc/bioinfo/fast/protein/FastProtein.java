package br.edu.ifsc.bioinfo.fast.protein;

import br.edu.ifsc.bioinfo.fast.Main;
import br.edu.ifsc.bioinfo.fast.protein.conversor.*;
import br.edu.ifsc.bioinfo.fast.protein.conversor.geneontology.GeneOntologyProcess;

import static br.edu.ifsc.bioinfo.fast.util.FileUtils.createFile;

import br.edu.ifsc.bioinfo.fast.protein.entity.Domain;
import br.edu.ifsc.bioinfo.fast.protein.entity.Protein;
import br.edu.ifsc.bioinfo.fast.protein.entity.Summary;
import br.edu.ifsc.bioinfo.fast.util.*;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import static br.edu.ifsc.bioinfo.fast.util.MarkdownHelper.*;
import static br.edu.ifsc.bioinfo.fast.util.ReportHelper.*;
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class FastProtein {

    private static Pattern patternErret = Pattern.compile("[KRHQSA][DENQ][E][L]");
    private static Pattern patternNglyc = Pattern.compile("[N][ARNDBCEQZGHILKMFSTWYV][ST]");
    private static long lastModified = new File(".").lastModified();

    public enum Output {
        csv(",", ".csv"), tsv("\t", ".tsv"), txt("txt", ".txt"), sep("sep", ".sep");
        private final String separator;
        private final String extension;

        private Output(String separator, String extension) {
            this.separator = separator;
            this.extension = extension;
        }

        public String getSeparator() {
            return separator;
        }

        public String getExtension() {
            return extension;
        }
    }

    public enum Field {

        ID("Id"), HEADER("Header"), LENGTH("Length"), KDA("kDa"), ISOELETRIC_POINT("Isoelectric Point"), HYDROPATHY("Hydropathy"), AROMATICITY("Aromaticity"), SUBCELLULAR_LOCALIZATION("Subcell Localization"), ERR_TOTAL("E.R Retention Total"), ERR_DOMAINS("E.R Retention Domains"), NGLYC_TOTAL("NGlyc Total"), NGLYC_DOMAINS("NGlyc Domains"), SEQUENCE("Sequence"), TRANSMEMBRANE("TMHMM-2"),

        PHOBIUS_TM("Phobius-TM"), SIGNAL_P5("SignalP5"), PHOBIUS_SP("Phobius-SP"), PREDGPI("PredGPI"), MEMBRANE_EVIDENCE("Membrane evidences"), MEMBRANE_EVIDENCE_DETAIL("Membrane evidences detail"), LOCAL_BLAST_DESC("Local blast description"), GO_ANNOTATION("Gene Ontology"), INTERPRO_ANNOTATION("Interpro Annotation"), PFAM_ANNOTATION("PFAM Annotation"), PANTHER_ANNOTATION("Panther Annotation");

        private String description;

        private Field(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

    }

    public static File generateCleanFasta(ArrayList<Protein> proteins) throws IOException {
        StringBuilder sbFasta = new StringBuilder();
        for (Protein protein : proteins) {
            sbFasta.append(">" + protein.getId() + "\n");
            sbFasta.append(protein.getSequence() + "\n");
        }
        File out = new File(Parameters.getTemporaryFile("clean.fasta"));
        FileWriter fw = new FileWriter(out);
        fw.write(sbFasta.toString());
        fw.flush();
        fw.close();
        return out;
    }

    private static void replaceSize(String value, Field field, HashMap<Field, Integer> mapSize) {
        int maxSize = mapSize.get(field);
        int length = value.length();
        if (length > maxSize) {
            mapSize.put(field, length);
        }
    }

    public static void run(File fileSource, WolfPsortConverter.Type psortType, SignalP5Converter.Organism signalPOption, File fastaBlastDb, boolean interpro, String outputFolder, Main.Source source) throws IOException {
        File fileOutputFolder = new File(outputFolder);
        if (fileOutputFolder.exists())
            org.apache.commons.io.FileUtils.deleteDirectory(fileOutputFolder);

        File fileTempDir = new File(Parameters.TEMP_DIR);
        long startTime = System.currentTimeMillis(); // Get start time in milliseconds
        StringBuilder outputmd = new StringBuilder();
        if (fileSource != null && fileSource.exists() && fileSource.isFile()) {
            org.apache.commons.io.FileUtils.copyFileToDirectory(fileSource, fileTempDir);
            fileSource = new File(Parameters.getTemporaryFile(fileSource.getName()));
        } else {
            error("Error: invalid FASTA input. Please inform a FASTA file");
            System.exit(0);
        }

        if (fastaBlastDb != null && fastaBlastDb.exists() && fastaBlastDb.isFile()) {
            org.apache.commons.io.FileUtils.copyFileToDirectory(fastaBlastDb, fileTempDir);
            fastaBlastDb = new File(Parameters.getTemporaryFile(fastaBlastDb.getName()));
        }

        LinkedHashMap<String, ProteinSequence> resp = null;

        ArrayList<String[]> goP = new ArrayList<>();
        ArrayList<String[]> goF = new ArrayList<>();
        ArrayList<String[]> goC = new ArrayList<>();
        String[] goHeader = {"GO", "Description", "Total"};
        Integer[] goHeaderAlignments = {LEFT, LEFT, RIGHT};

        try {
            resp = FastaReaderHelper.readFastaProteinSequence(fileSource);
        } catch (IOException ex) {
            error("Invalid FASTA file. Please check your informed file " + fileSource);
            ex.printStackTrace();
            System.exit(1);
        }

        info("#FastProtein Software 1.0");
        info("#Developed by Bioinformatic Laboratory - UFSC and IFSC");
        info("#An automated pipeline for proteomic profile analysis");
        info("#Questions and issues: renato.simoes@ifsc.edu.br");
        info("#See more about the project at https://github.com/bioinformatics-ufsc/FastProtein");
        info();
        info("Process started time: " + new Date());
        info("Temporary output folder: " + Parameters.TEMP_DIR);
        if (!Parameters.ZIP) {
            info("Final output folder: " + new File(outputFolder).getAbsolutePath());
        } else {
            info(String.format("Final output file: %s.zip\n", new File(outputFolder).getAbsolutePath()));
        }

        info();
        ArrayList<Protein> proteins = new ArrayList<>();

        for (ProteinSequence s : resp.values()) {
            String id = s.getAccession().getID();
            String proteinId = id.split(" ")[0];

            String sequence = s.getSequenceAsString().toUpperCase();
            Protein protein = new Protein(proteinId, sequence);
            protein.setHeader(s.getOriginalHeader());

            //Processing ER Retention
            Matcher erretMatcher = patternErret.matcher(sequence);
            while (erretMatcher.find()) {
                protein.addErretDomain(new Domain(erretMatcher.group(), erretMatcher.start(), erretMatcher.end()));
            }
            //Processing Nglyc domain
            Matcher nGlycmatcher = patternNglyc.matcher(sequence);
            while (nGlycmatcher.find()) {
                protein.addNglycDomain(new Domain(nGlycmatcher.group(), nGlycmatcher.start(), nGlycmatcher.end()));
            }


            if (!sequence.contains("*")) {
                proteins.add(protein);
            } else {
                info(String.format("Protein %s is skipped because contains the character *\n", protein.getId()));
            }

        }
        //Generate erret and n-glyc files
        StringBuilder sbErret = new StringBuilder();
        StringBuilder sbNglyc = new StringBuilder();

        sbErret.append("#Search for Endoplasmic Reticulum Retention sites by sequence [KRHQSA]-[DENQ]-E-L>.\n");
        sbErret.append("#Reference: https://prosite.expasy.org/PS00014\n");
        sbErret.append("ID\tDomains\n");

        sbNglyc.append("#Search for N-glycosylation sites by sequence N-{P}-[ST]-{P}.\n");
        sbNglyc.append("#Reference: https://prosite.expasy.org/PS00001\n");
        sbNglyc.append("ID\tDomains\n");
        for (Protein protein : proteins) {
            sbErret.append(String.format("%s\t%s\n", protein.getId(), protein.getErretDomainsAsString()));
            sbNglyc.append(String.format("%s\t%s\n", protein.getId(), protein.getnGlycDomainsAsString()));
        }

        try {
            createFile(sbErret.toString(), "erret.txt");
            createFile(sbNglyc.toString(), "nglyc.txt");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            //Generate clean fasta
            File cleanFasta = generateCleanFasta(proteins);
            debug("Clean fasta generated " + cleanFasta.getAbsolutePath());

            WolfPsortConverter wolfpsort = new WolfPsortConverter(cleanFasta);
            if (psortType != WolfPsortConverter.Type.none) {
                wolfpsort.execute(psortType);
            } else {
                info("Skipping - WoLFPSORT");
            }

            //Executing TMHMM2.0c
            TMHMM2Converter tmhmm2 = new TMHMM2Converter(cleanFasta);
            tmhmm2.execute();

            SignalP5Converter signalp5 = new SignalP5Converter(cleanFasta);
            signalp5.execute(signalPOption, proteins);

            //Executing PredGPI
            PredGPIConverter predgpi = new PredGPIConverter(cleanFasta);
            predgpi.execute();

            //Executing Phobius
            PhobiusConverter phobius = new PhobiusConverter(cleanFasta);
            phobius.execute();


            Summary summary = new Summary();

            //Executing InteproScan
            if (interpro) {
                InterproScanConverter iprScan = new InterproScanConverter(cleanFasta, proteins);
//                iprScan.executeLocal();
                iprScan.updateProteins(proteins);

                //creating files for type of GO's
                HashMap<String, Integer> mapP = sortByValue(GeneOntologyProcess.getMap(proteins, GeneOntologyUtil.Type.biological_process));
                HashMap<String, Integer> mapC = sortByValue(GeneOntologyProcess.getMap(proteins, GeneOntologyUtil.Type.cellular_component));
                HashMap<String, Integer> mapF = sortByValue(GeneOntologyProcess.getMap(proteins, GeneOntologyUtil.Type.molecular_function));

                for (Map.Entry<String, Integer> go : mapP.entrySet()) {

                    String description = GeneOntologyUtil.getOntology(go.getKey());
                    goP.add(new String[]{go.getKey(), description, go.getValue().toString()});
                    summary.addGoP(go.getKey(), description, go.getValue());

                }

                for (Map.Entry<String, Integer> go : mapF.entrySet()) {
                    String description = GeneOntologyUtil.getOntology(go.getKey());

                    goF.add(new String[]{go.getKey(), description, go.getValue().toString()});
                    summary.addGoF(go.getKey(), description, go.getValue());
                }

                for (Map.Entry<String, Integer> go : mapC.entrySet()) {
                    String description = GeneOntologyUtil.getOntology(go.getKey());

                    goC.add(new String[]{go.getKey(), description, go.getValue().toString()});
                    summary.addGoC(go.getKey(), description, go.getValue());
                }

                StringBuilder sbgop = new StringBuilder();
                sbgop.append(String.join("\t", goHeader)).append("\n");
                for (String[] cols : goP) {
                    sbgop.append(String.join("\t", cols)).append("\n");
                }
                createFile(sbgop.toString(), String.format("go-%s.txt", GeneOntologyUtil.Type.biological_process));

                StringBuilder sbgoc = new StringBuilder();
                sbgoc.append(String.join("\t", goHeader)).append("\n");
                for (String[] cols : goC) {
                    sbgoc.append(String.join("\t", cols)).append("\n");
                }
                createFile(sbgoc.toString(), String.format("go-%s.txt", GeneOntologyUtil.Type.cellular_component));

                StringBuilder sbgof = new StringBuilder();
                sbgof.append(String.join("\t", goHeader)).append("\n");
                for (String[] cols : goF) {
                    sbgof.append(String.join("\t", cols)).append("\n");
                }
                createFile(sbgof.toString(), String.format("go-%s.txt", GeneOntologyUtil.Type.molecular_function));

            } else {
                info("Skipping - InterproScan");
            }

            BlastLocalConverter localBlastConverter = new BlastLocalConverter();
            if (fastaBlastDb != null && fastaBlastDb.exists() && fastaBlastDb.isFile()) {
                info("Executing BLASTp");

                localBlastConverter.process(cleanFasta, fastaBlastDb);

            } else {
                if (fastaBlastDb == null) {
                    info("Skipping - BLASTp");
                } else if (fastaBlastDb.isDirectory()) {
                    info("Error: inform a FASTA file to perform a local BLASTp.");
                    System.exit(0);
                }
            }
            //Calculate column size to formated report
            HashMap<Field, Integer> mapSize = new HashMap<>();
            for (Field value : Field.values()) {
                mapSize.put(value, value.toString().length());
            }
            for (Field value : Field.values()) {
                replaceSize(value.toString(), value, mapSize);
            }

            int totalWithER = 0;
            int totalWithNglyc = 0;
            int totalTransmembrane = 0;
            int totalSignalp = 0;
            int totalGpi = 0;
            int totalMembraneProtein = 0;

            HashMap<String, Integer> subcellTotalMap = new HashMap<>();
            HashMap<String, Integer> erretMap = new HashMap<>();
            HashMap<String, Integer> nglycMap = new HashMap<>();

            ArrayList<Double> listKda = new ArrayList<>();
            ArrayList<Double> listIso = new ArrayList<>();
            ArrayList<Double> listHydro = new ArrayList<>();
            ArrayList<Double> listAro = new ArrayList<>();

            ArrayList<String> membraneProteinsFasta = new ArrayList<>();
            ArrayList<String> membraneProteinsSt = new ArrayList<>();

            membraneProteinsSt.add("ID\tEvidences length\tEvidences list\tComplete evidences");
            for (Protein protein : proteins) {
                try {
                    listKda.add(protein.getKda());
                    listIso.add(protein.getIsoelectricPointAvg());
                    listHydro.add(protein.getHydropathy());
                    listAro.add(protein.getAromaticity());

                    protein.setSubcellularLocalization(wolfpsort.getLocation(protein.getId()));
                    protein.setTransmembrane(tmhmm2.getTotalTransmembrane(protein.getId()));
                    protein.setSignalp5(signalp5.getSignal(protein.getId()));
                    protein.setGpi(predgpi.isGPIAnchored(protein.getId()));
                    protein.setGpiEvidence(predgpi.getGOEvidence(protein.getId()));

                    protein.setPhobiusSP(phobius.getSignalPeptide(protein.getId()));
                    protein.setPhobiusTM(phobius.getTM(protein.getId()));

                    protein.setLocalBlastHit(localBlastConverter.getAnnotation(protein.getId()));
                    //Generate data in protein for membrane evidences
                    protein.processMembraneEvidences();

                    if (protein.isGpi()) {
                        totalGpi++;
                    }
                    if (!protein.getMembraneEvidences().isEmpty()) {
                        totalMembraneProtein++;
                        membraneProteinsSt.add(String.format("%s\t%s\t%s\t%s", protein.getId(), protein.getMembraneEvidences().size(), protein.getMembraneEvidencesAsString(), protein.getMembraneFullEvidencesAsString()));
                        membraneProteinsFasta.add(protein.getFasta());
                    }

                    if (protein.getErretTotal() > 0) {
                        totalWithER++;
                    }
                    if (protein.getnGlycTotal() > 0) {
                        totalWithNglyc++;
                    }
                    if (protein.getTransmembrane() > 0 || protein.getPhobiusTM() > 0) {
                        totalTransmembrane++;
                    }
                    if ((!protein.getSignalp5().trim().equalsIgnoreCase("OTHER") && !protein.getSignalp5().trim().equalsIgnoreCase("-")) || protein.isPhobiusSP()) {
                        totalSignalp++;
                    }

                    if (subcellTotalMap.containsKey(protein.getSubcellularLocalization())) {
                        subcellTotalMap.computeIfPresent(protein.getSubcellularLocalization(), (k, v) -> v + 1);
                    } else {
                        subcellTotalMap.put(protein.getSubcellularLocalization(), 1);
                    }

                    for (Domain erretDomain : protein.getErretDomains()) {
                        if (erretMap.containsKey(erretDomain.getSequence())) {
                            erretMap.computeIfPresent(erretDomain.getSequence(), (k, v) -> v + 1);
                        } else {
                            erretMap.put(erretDomain.getSequence(), 1);
                        }
                    }
                    for (Domain domain : protein.getNglycDomains()) {
                        if (nglycMap.containsKey(domain.getSequence())) {
                            nglycMap.computeIfPresent(domain.getSequence(), (k, v) -> v + 1);
                        } else {
                            nglycMap.put(domain.getSequence(), 1);
                        }
                    }

                    replaceSize(protein.getId(), Field.ID, mapSize);
                    replaceSize(protein.getLength().toString(), Field.LENGTH, mapSize);
                    replaceSize(protein.getKdaStr(), Field.KDA, mapSize);
                    replaceSize(protein.getIsoelectricPointAvgStr(), Field.ISOELETRIC_POINT, mapSize);
                    replaceSize(protein.getHydropathyStr(), Field.HYDROPATHY, mapSize);
                    replaceSize(protein.getSubcellularLocalization(), Field.SUBCELLULAR_LOCALIZATION, mapSize);
                    replaceSize(protein.getTransmembrane().toString(), Field.TRANSMEMBRANE, mapSize);
                    replaceSize(protein.getPhobiusTM().toString(), Field.PHOBIUS_TM, mapSize);
                    replaceSize(protein.getSignalp5(), Field.SIGNAL_P5, mapSize);
                    replaceSize(protein.isPhobiusSP() ? "Y" : "-", Field.PHOBIUS_SP, mapSize);
                    replaceSize(protein.isGpi() ? "Y" : "-", Field.PREDGPI, mapSize);
                    replaceSize(protein.getMembraneEvidences().size() + "", Field.MEMBRANE_EVIDENCE, mapSize);
                    replaceSize(protein.getMembraneEvidencesAsString(), Field.MEMBRANE_EVIDENCE_DETAIL, mapSize);
                    replaceSize(protein.getErretTotal().toString(), Field.ERR_TOTAL, mapSize);
                    replaceSize(protein.getErretDomainsAsString(), Field.ERR_DOMAINS, mapSize);
                    replaceSize(protein.getnGlycTotal().toString(), Field.NGLYC_TOTAL, mapSize);
                    replaceSize(protein.getnGlycDomainsAsString(), Field.NGLYC_DOMAINS, mapSize);
                    replaceSize(protein.getHeader(), Field.HEADER, mapSize);
                    replaceSize(protein.getSequence(), Field.SEQUENCE, mapSize);

                } catch (CompoundNotFoundException ex) {
                    error("It is not possible to calculate molecular mass, isoelectric point and " + "hydropathy of the protein" + protein.getId() + ". " + "The sequence appears to be in error, check the contents of the FASTA file " + "and run the program again. Check for non-amino acid characters with X and *.");
                    ex.printStackTrace();
                    System.exit(1);
                }
            }

            //Create membrane protein file
            createFile(String.join("\n", membraneProteinsFasta), "membranes.fasta");
            createFile(String.join("\n", membraneProteinsSt), "membranes.txt");

            //FastProtein Summary
            StringBuilder sbSummary = new StringBuilder();
            int qtd = proteins.size();
            MathCalculator mathKda = new MathCalculator(listKda);
            MathCalculator mathIso = new MathCalculator(listIso);
            MathCalculator mathHydro = new MathCalculator(listHydro);
            MathCalculator mathAro = new MathCalculator(listAro);

            summary.setProteins(proteins);
            summary.setKdaMean(mathKda.mean());
            summary.setKdaSd(mathKda.sd());
            summary.setIsoMean(mathIso.mean());
            summary.setIsoSd(mathIso.sd());
            summary.setHydroMean(mathHydro.mean());
            summary.setHydroSd(mathHydro.sd());
            summary.setAromacityMean(mathAro.mean());
            summary.setAromacitySd(mathAro.sd());
            summary.setTotalTM(totalTransmembrane);
            summary.setTotalSp(totalSignalp);
            summary.setTotalER(totalWithER);
            summary.setTotalNGlyc(totalWithNglyc);
            summary.setTotalGPI(totalGpi);
            summary.setTotalMembrane(totalMembraneProtein);

            sbSummary.append("#FastProtein-1.0 Summary\n");
            sbSummary.append(String.format("\nProcessed proteins: %s", qtd));
            sbSummary.append(String.format("\nMolecular mass (kda) mean: %.2f ± %.2f", summary.getKdaMean(), summary.getKdaSd()));
            sbSummary.append(String.format("\nIsoelectric point mean: %.2f ± %.2f", summary.getIsoMean(), summary.getIsoSd()));
            sbSummary.append(String.format("\nHydrophicity mean: %.2f ± %.2f", summary.getHydroMean(), summary.getHydroSd()));
            sbSummary.append(String.format("\nAromaticity mean: %.2f ± %.2f", summary.getAromacityMean(), summary.getAromacitySd()));
            sbSummary.append(String.format("\nProteins with TM: %s", summary.getTotalTM()));
            sbSummary.append(String.format("\nProteins with GPI: %s", summary.getTotalGPI()));
            sbSummary.append(String.format("\nMembrane proteins: %s", summary.getTotalMembrane()));
            sbSummary.append(String.format("\nProteins with SP: %s", summary.getTotalSp()));

            sbSummary.append(String.format("\nProteins with E.R Retention domains: %s", summary.getTotalER()));
            sbSummary.append(String.format("\nProteins with NGlycosylation domains: %s", summary.getTotalNGlyc()));

            MarkdownHelper.appendHeader(outputmd, "FastProtein Software 1.0", 1);
            MarkdownHelper.appendHeader(outputmd, "Protein Information Software", 5);
            outputmd.append("\n");

            MarkdownHelper.appendLine(outputmd);
            MarkdownHelper.appendHeader(outputmd, "Summary", 3);
            LinkedHashMap<String, String> mapOutputmd = new LinkedHashMap<>();
            mapOutputmd.put("Processed proteins", String.format("%s", qtd));
            mapOutputmd.put("Molecular mass (kda) mean", String.format("%.2f &#177; %.2f", mathKda.mean(), mathKda.sd()));
            mapOutputmd.put("Isoelectric point mean", String.format("%.2f &#177; %.2f", mathIso.mean(), mathIso.sd()));
            mapOutputmd.put("Hydrophicity mean", String.format("%.2f &#177; %.2f", mathHydro.mean(), mathHydro.sd()));
            mapOutputmd.put("Aromaticity mean", String.format("%.2f &#177; %.2f", mathAro.mean(), mathAro.sd()));
            mapOutputmd.put("Proteins with TM", String.format("%s", totalTransmembrane));
            mapOutputmd.put("Proteins with SP", String.format("%s", totalSignalp));
            mapOutputmd.put("Proteins with GPI", String.format("%s", totalGpi));
            mapOutputmd.put("Membrane proteins", String.format("%s", totalMembraneProtein));

            mapOutputmd.put("Proteins with E.R Retention domains", String.format("%s", totalWithER));
            mapOutputmd.put("Proteins with NGlycosylation domains", String.format("%s", totalWithNglyc));

            MarkdownHelper.appendTableFromMap(outputmd, new String[]{"Information", "Value"}, mapOutputmd);

            //File use to build charts
            StringBuilder kdaisoSb = new StringBuilder();
            kdaisoSb.append(String.join(Output.csv.separator, "kda", "ph") + "\n");
            for (Protein protein : proteins) {
                try {
                    kdaisoSb.append(String.join(Output.csv.separator, protein.getKdaStr(), protein.getIsoelectricPointAvgStr()));
                    kdaisoSb.append("\n");
                } catch (CompoundNotFoundException ex) {
                    error("It is not possible to calculate molecular mass, isoelectric " + "point, aromaticity and hydropathy of the protein" + protein.getId() + ". " + "The sequence appears to be in error, check the contents of the FASTA " + "file and run the program again. Check for non-amino acid characters with X and *.");
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            File kdaisoFile = createFile(kdaisoSb.toString(), "kdaiso.csv");

            boolean charts = ChartUtil.generate(kdaisoFile);

            if (charts) {

                MarkdownHelper.appendHeader(outputmd, "Molecular mass (kDa) vs Isoelectric point (pH)", 3);
                MarkdownHelper.appendImage(outputmd, "Molecular mass (kDa) vs Isoelectric point (pH)", "kda-vs-pi.png");

                outputmd.append("\n");
                MarkdownHelper.appendHeader(outputmd, "Molecular mass (kDa) histogram", 3);
                MarkdownHelper.appendImage(outputmd, "Molecular mass (kDa) histogram", "his-kda-bar.png");

            }

            MarkdownHelper.appendLine(outputmd);

            MarkdownHelper.appendHeader(outputmd, "Subcellular localization (by WolfPSort) - Organism: " + psortType, 3);

            sbSummary.append("\nSubcellular localization (by WolfPSort) - Organism: " + psortType + " summary:\n");
            HashMap<String, Integer> mapOrdSubcell = sortByValue(subcellTotalMap);

            ArrayList<String> subcellSt = new ArrayList();
            subcellSt.add("wolfsort\tlocalization\tproteins");

            for (String subcell : mapOrdSubcell.keySet()) {

                sbSummary.append(String.format("\t%s\t%s: %s\n",
                        subcell,
                        WolfPsortConverter.getCategory(subcell),
                        mapOrdSubcell.get(subcell)));
                subcellSt.add(String.format("%s\t%s\t%s",
                        subcell,
                        WolfPsortConverter.getCategory(subcell),
                        mapOrdSubcell.get(subcell)));
            }
            File subcellFile = createFile(String.join("\n", subcellSt), "subcell.txt");
            boolean chartSubcell = ChartUtil.createBarSubcell(subcellFile);

            if (chartSubcell) {

                outputmd.append("\n");
                MarkdownHelper.appendImage(outputmd, "Subcellular Localizations", "subcell-resume-bar.png");
                MarkdownHelper.appendImage(outputmd, "Subcellular Localizations", "subcell-resume-pie.png");

                outputmd.append("\n");


            }





            summary.setSubcellLocalizations(mapOrdSubcell);
            MarkdownHelper.appendTableFromMap(outputmd, new String[]{"Subcellular localization", "Proteins"}, mapOrdSubcell);

            MarkdownHelper.appendLine(outputmd);


            if (totalWithER > 0) {
                MarkdownHelper.appendHeader(outputmd, "E.R Retention domain summary", 3);

                sbSummary.append("\nE.R Retention domain summary:\n");
                HashMap<String, Integer> mapOrdErret = sortByValue(erretMap);
                int max = mapOrdErret.keySet().size() > 10 ? 10 : mapOrdErret.size();
                String[] domains = mapOrdErret.keySet().toArray(new String[]{});
                HashMap<String, Integer> mapErret = new HashMap<>();
                for (int i = 0; i < mapOrdErret.keySet().size(); i++) {
                    String domain = domains[i];
                    if (i < max) {
                        mapErret.put(domain, mapOrdErret.get(domain));
                    }
                    sbSummary.append(String.format("\t%s: %s\n", domain, mapOrdErret.get(domain)));
                }
                summary.setErret(mapOrdErret);

                MarkdownHelper.appendTableFromMap(outputmd, new String[]{"Domain", "Quantity"}, mapErret);
                if (domains.length > 10) {
                    outputmd.append("Only top 10\n").append("\n");
                }
                MarkdownHelper.appendLine(outputmd);
            }
            if (totalWithNglyc > 0) {

                MarkdownHelper.appendHeader(outputmd, "NGlyc domain summary", 3);

                sbSummary.append("\nNGlyc Retention domain summary:\n");
                HashMap<String, Integer> mapOrdNglyc = sortByValue(nglycMap);
                int max = (mapOrdNglyc.keySet().size() > 10) ? 10 : mapOrdNglyc.size();
                String[] domains = mapOrdNglyc.keySet().toArray(new String[]{});

                HashMap<String, Integer> mapNGlyc = new HashMap<>();
                for (int i = 0; i < mapOrdNglyc.keySet().size(); i++) {
                    String domain = domains[i];
                    if (i < max) {
                        mapNGlyc.put(domain, mapOrdNglyc.get(domain));
                    }
                    sbSummary.append(String.format("\t%s: %s\n", domain, mapOrdNglyc.get(domain)));
                }

                summary.setNglyc(mapOrdNglyc);
                MarkdownHelper.appendTableFromMap(outputmd, new String[]{"Domain", "Quantity"}, mapNGlyc);
                if (domains.length > 10) {
                    outputmd.append("Only top 10\n").append("\n");
                }

                MarkdownHelper.appendLine(outputmd);
            }
            //Generate output.md
            String[] headerMd = {Field.ID.toString(),//left
                    Field.LENGTH.toString(),//center
                    Field.KDA.toString(),//center
                    Field.ISOELETRIC_POINT.toString(),//center
                    Field.HYDROPATHY.toString(),//center
                    Field.AROMATICITY.toString(),//center
                    Field.SUBCELLULAR_LOCALIZATION.toString(),
                    Field.TRANSMEMBRANE.toString(),
                    Field.PHOBIUS_TM.toString(),
                    Field.PREDGPI.toString(),
                    Field.MEMBRANE_EVIDENCE.toString(),
                    Field.MEMBRANE_EVIDENCE_DETAIL.toString(),
                    Field.SIGNAL_P5.toString(),
                    Field.PHOBIUS_SP.toString(),
                    Field.ERR_TOTAL.toString(),
                    Field.NGLYC_TOTAL.toString(),
                    Field.ERR_DOMAINS.toString(),
                    Field.NGLYC_DOMAINS.toString(),
                    Field.HEADER.toString(),
                    Field.LOCAL_BLAST_DESC.toString(),
                    Field.GO_ANNOTATION.toString(),
                    Field.INTERPRO_ANNOTATION.toString(),
                    Field.PFAM_ANNOTATION.toString(),
                    Field.PANTHER_ANNOTATION.toString()};
            Integer[] alignMd = {LEFT, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, CENTER, LEFT, LEFT, LEFT, LEFT, LEFT};

            ArrayList<String[]> dataOutpumd = new ArrayList<>();
            int totalOutmod = 0;
            for (Protein protein : proteins) {
                try {
                    dataOutpumd.add(new String[]{protein.getId(), protein.getLength().toString(), protein.getKdaStr(), protein.getIsoelectricPointAvgStr(), protein.getHydropathyStr(), protein.getAromaticityStr(), protein.getSubcellularLocalization(), protein.getTransmembrane().toString(), protein.getPhobiusTM().toString(), protein.isGpi() ? "Y" : "-", protein.getMembraneEvidences().size() + "", protein.getMembraneEvidencesAsString().replace("|", "&#124;"), protein.getSignalp5(), protein.isPhobiusSP() ? "Y" : "-", protein.getErretTotal().toString(), protein.getnGlycTotal().toString(), protein.getErretDomainsAsString(), protein.getnGlycDomainsAsString(), protein.getHeader().replace("|", "&#124;"), protein.getLocalBlastHit().replace("|", "&#124;"), protein.getCleanFullGO().replace("|", "&#124;"), protein.getCleanInterpro().replace("|", "&#124;"), protein.getCleanPfam().replace("|", "&#124;"), protein.getCleanPanther().replace("|", "&#124;")

                    });
                    totalOutmod++;

                } catch (CompoundNotFoundException ex) {
                    error("It is not possible to calculate molecular mass, isoelectric point," + " aromaticity and hydropathy of the protein" + protein.getId() + ". " + "The sequence appears to be in error, check the contents of the FASTA file " + "and run the program again. Check for non-amino acid characters with X and *.");
                    ex.printStackTrace();
                    System.exit(1);
                }
                if (totalOutmod > 25) {
                    break;
                }
            }

            createFile(sbSummary.toString(), "summary.txt");

            StringBuilder sbTxt = new StringBuilder();

            for (Output output : Output.values()) {

                String finalOut = "";
                //Insert Header
                if (output == Output.csv || output == Output.tsv) {
                    StringBuilder sbOut = new StringBuilder();
                    sbOut.append(String.join(output.getSeparator(), Field.ID.toString(), Field.LENGTH.toString(), Field.KDA.toString(), Field.ISOELETRIC_POINT.toString(), Field.HYDROPATHY.toString(), Field.AROMATICITY.toString(), Field.SUBCELLULAR_LOCALIZATION.toString(), Field.TRANSMEMBRANE.toString(), Field.PHOBIUS_TM.toString(), Field.PREDGPI.toString(), Field.MEMBRANE_EVIDENCE.toString(), Field.MEMBRANE_EVIDENCE_DETAIL.toString(), Field.SIGNAL_P5.toString(), Field.PHOBIUS_SP.toString(), Field.ERR_TOTAL.toString(), Field.NGLYC_TOTAL.toString(), Field.ERR_DOMAINS.toString(),

                            Field.NGLYC_DOMAINS.toString(), Field.HEADER.toString(), Field.LOCAL_BLAST_DESC.toString(), Field.GO_ANNOTATION.toString(), Field.INTERPRO_ANNOTATION.toString(), Field.PFAM_ANNOTATION.toString(), Field.PANTHER_ANNOTATION.toString(), Field.SEQUENCE.toString()));
                    sbOut.append("\n");
                    for (Protein protein : proteins) {
                        try {
                            sbOut.append(String.join(output.separator, protein.getId(), protein.getLength().toString(), protein.getKdaStr(), protein.getIsoelectricPointAvgStr(), protein.getHydropathyStr(), protein.getAromaticityStr(), protein.getSubcellularLocalization(), protein.getTransmembrane().toString(), protein.getPhobiusTM().toString(), protein.isGpi() ? "Y" : "-", protein.getMembraneEvidences().size() + "", protein.getMembraneEvidencesAsString(), protein.getSignalp5(), protein.isPhobiusSP() ? "Y" : "-", protein.getErretTotal().toString(), protein.getnGlycTotal().toString(), protein.getErretDomainsAsString(), protein.getnGlycDomainsAsString(), protein.getHeader(), protein.getLocalBlastHit(), protein.getCleanFullGO(), protein.getCleanInterpro(), protein.getCleanPfam(), protein.getCleanPanther(), protein.getSequence()));

                            sbOut.append("\n");
                        } catch (CompoundNotFoundException ex) {
                            error("It is not possible to calculate molecular mass, isoelectric " + "point, aromaticity and hydropathy of the protein" + protein.getId() + ". " + "The sequence appears to be in error, check the contents of the FASTA " + "file and run the program again. Check for non-amino acid characters with X and *.");
                            ex.printStackTrace();
                            System.exit(1);
                        }
                    }
                    finalOut = sbOut.toString();

                } else if (output == Output.txt) {

                    sbTxt.append(String.join(" ", StringUtils.rightPad(Field.ID.toString(), mapSize.get(Field.ID), ' '), StringUtils.center(Field.LENGTH.toString(), mapSize.get(Field.LENGTH), ' '), StringUtils.center(Field.KDA.toString(), mapSize.get(Field.KDA), ' '), StringUtils.center(Field.ISOELETRIC_POINT.toString(), mapSize.get(Field.ISOELETRIC_POINT), ' '), StringUtils.center(Field.HYDROPATHY.toString(), mapSize.get(Field.HYDROPATHY), ' '), StringUtils.center(Field.AROMATICITY.toString(), mapSize.get(Field.AROMATICITY), ' '), StringUtils.center(Field.SUBCELLULAR_LOCALIZATION.toString(), mapSize.get(Field.SUBCELLULAR_LOCALIZATION), ' '), StringUtils.center(Field.TRANSMEMBRANE.toString(), mapSize.get(Field.TRANSMEMBRANE), ' '), StringUtils.center(Field.PHOBIUS_TM.toString(), mapSize.get(Field.PHOBIUS_TM), ' '), StringUtils.center(Field.PREDGPI.toString(), mapSize.get(Field.PREDGPI), ' '), StringUtils.center(Field.MEMBRANE_EVIDENCE.toString(), mapSize.get(Field.MEMBRANE_EVIDENCE), ' '), StringUtils.center(Field.MEMBRANE_EVIDENCE_DETAIL.toString(), mapSize.get(Field.MEMBRANE_EVIDENCE_DETAIL), ' '), StringUtils.center(Field.SIGNAL_P5.toString(), mapSize.get(Field.SIGNAL_P5), ' '), StringUtils.center(Field.PHOBIUS_SP.toString(), mapSize.get(Field.PHOBIUS_SP), ' '), StringUtils.center(Field.ERR_TOTAL.toString(), mapSize.get(Field.ERR_TOTAL), ' '), StringUtils.center(Field.NGLYC_TOTAL.toString(), mapSize.get(Field.NGLYC_TOTAL), ' '), StringUtils.rightPad(Field.ERR_DOMAINS.toString(), mapSize.get(Field.ERR_DOMAINS), ' '), StringUtils.rightPad(Field.NGLYC_DOMAINS.toString(), mapSize.get(Field.NGLYC_DOMAINS), ' '), StringUtils.rightPad(Field.HEADER.toString(), mapSize.get(Field.HEADER), ' '), StringUtils.rightPad(Field.GO_ANNOTATION.toString(), mapSize.get(Field.GO_ANNOTATION), ' '), StringUtils.rightPad(Field.INTERPRO_ANNOTATION.toString(), mapSize.get(Field.INTERPRO_ANNOTATION), ' '), StringUtils.rightPad(Field.PFAM_ANNOTATION.toString(), mapSize.get(Field.PFAM_ANNOTATION), ' '), StringUtils.rightPad(Field.PANTHER_ANNOTATION.toString(), mapSize.get(Field.PANTHER_ANNOTATION), ' '), Field.SEQUENCE.toString()));
                    sbTxt.append("\n");
                    for (Protein protein : proteins) {
                        try {
                            sbTxt.append(String.join(" ", (StringUtils.rightPad(protein.getId(), mapSize.get(Field.ID), ' ')), (StringUtils.center(protein.getLength().toString(), mapSize.get(Field.LENGTH), ' ')), (StringUtils.center(protein.getKdaStr(), mapSize.get(Field.KDA), ' ')), (StringUtils.center(protein.getIsoelectricPointAvgStr(), mapSize.get(Field.ISOELETRIC_POINT), ' ')), (StringUtils.center(protein.getHydropathyStr(), mapSize.get(Field.HYDROPATHY), ' ')), (StringUtils.center(protein.getAromaticityStr(), mapSize.get(Field.AROMATICITY), ' ')), (StringUtils.center(protein.getSubcellularLocalization(), mapSize.get(Field.SUBCELLULAR_LOCALIZATION), ' ')), (StringUtils.center(protein.getTransmembrane().toString(), mapSize.get(Field.TRANSMEMBRANE), ' ')), (StringUtils.center(protein.getPhobiusTM().toString(), mapSize.get(Field.PHOBIUS_TM), ' ')), (StringUtils.center(protein.isGpi() ? "Y" : "-", mapSize.get(Field.PREDGPI), ' ')), (StringUtils.center(protein.getMembraneEvidences().size() + "", mapSize.get(Field.MEMBRANE_EVIDENCE), ' ')), (StringUtils.center(protein.getMembraneEvidencesAsString().toString(), mapSize.get(Field.MEMBRANE_EVIDENCE_DETAIL), ' ')), (StringUtils.center(protein.getSignalp5(), mapSize.get(Field.SIGNAL_P5), ' ')), (StringUtils.center(protein.isPhobiusSP() ? "Y" : "-", mapSize.get(Field.PHOBIUS_SP), ' ')), (StringUtils.center(protein.getErretTotal().toString(), mapSize.get(Field.ERR_TOTAL), ' ')), (StringUtils.center(protein.getnGlycTotal().toString(), mapSize.get(Field.NGLYC_TOTAL), ' ')), (StringUtils.rightPad(protein.getErretDomainsAsString(), mapSize.get(Field.ERR_DOMAINS), ' ')), (StringUtils.rightPad(protein.getnGlycDomainsAsString(), mapSize.get(Field.NGLYC_DOMAINS), ' ')), (StringUtils.rightPad(protein.getHeader(), mapSize.get(Field.HEADER), ' ')), (StringUtils.rightPad(protein.getLocalBlastHit(), mapSize.get(Field.LOCAL_BLAST_DESC), ' ')), (StringUtils.rightPad(protein.getCleanFullGO(), mapSize.get(Field.GO_ANNOTATION), ' ')), (StringUtils.rightPad(protein.getCleanInterpro(), mapSize.get(Field.INTERPRO_ANNOTATION), ' ')), (StringUtils.rightPad(protein.getCleanPfam(), mapSize.get(Field.PFAM_ANNOTATION), ' ')), (StringUtils.rightPad(protein.getCleanPanther(), mapSize.get(Field.PANTHER_ANNOTATION), ' ')), protein.getSequence()));

                            sbTxt.append("\n");
                        } catch (CompoundNotFoundException ex) {
                            error("It is not possible to calculate molecular mass, isoelectric point," + " aromaticity and hydropathy of the protein" + protein.getId() + ". " + "The sequence appears to be in error, check the contents of the FASTA file " + "and run the program again. Check for non-amino acid characters with X and *.");
                            ex.printStackTrace();
                            System.exit(1);
                        }
                    }
                    finalOut = sbTxt.toString();
                } else {
                    StringBuilder sbSep = new StringBuilder();

                    for (Protein protein : proteins) {
                        try {
                            sbSep.append(String.format("%s: %s\n", Field.ID.toString(), protein.getId()));
                            sbSep.append(String.format("%s: %s\n", Field.LENGTH.toString(), protein.getLength().toString()));
                            sbSep.append(String.format("%s: %s\n", Field.KDA.toString(), protein.getKdaStr()));
                            sbSep.append(String.format("%s: %s\n", Field.ISOELETRIC_POINT.toString(), protein.getIsoelectricPointAvgStr()));
                            sbSep.append(String.format("%s: %s\n", Field.HYDROPATHY.toString(), protein.getHydropathyStr()));
                            sbSep.append(String.format("%s: %s\n", Field.AROMATICITY.toString(), protein.getAromaticityStr()));
                            sbSep.append(String.format("%s: %s\n", Field.SUBCELLULAR_LOCALIZATION.toString(), protein.getSubcellularLocalization()));
                            sbSep.append(String.format("%s: %s\n", Field.TRANSMEMBRANE.toString(), protein.getTransmembrane().toString()));
                            sbSep.append(String.format("%s: %s\n", Field.PHOBIUS_TM.toString(), protein.getPhobiusTM().toString()));
                            sbSep.append(String.format("%s: %s\n", Field.PREDGPI.toString(), protein.isGpi() ? "Y" : "-"));
                            sbSep.append(String.format("%s: %s\n", Field.MEMBRANE_EVIDENCE.toString(), protein.getMembraneEvidences().size() + ""));
                            sbSep.append(String.format("%s: %s\n", Field.MEMBRANE_EVIDENCE_DETAIL.toString(), protein.getMembraneEvidencesAsString()));
                            sbSep.append(String.format("%s: %s\n", Field.SIGNAL_P5.toString(), protein.getSignalp5()));
                            sbSep.append(String.format("%s: %s\n", Field.PHOBIUS_SP.toString(), protein.isPhobiusSP() ? "Y" : "-"));
                            sbSep.append(String.format("%s: %s\n", Field.ERR_TOTAL.toString(), protein.getErretTotal().toString()));
                            sbSep.append(String.format("%s: %s\n", Field.ERR_DOMAINS.toString(), protein.getErretDomainsAsString()));
                            sbSep.append(String.format("%s: %s\n", Field.NGLYC_TOTAL.toString(), protein.getnGlycTotal().toString()));
                            sbSep.append(String.format("%s: %s\n", Field.NGLYC_DOMAINS.toString(), protein.getnGlycDomainsAsString()));
                            sbSep.append(String.format("%s: %s\n", Field.HEADER.toString(), protein.getHeader()));
                            sbSep.append(String.format("%s: %s\n", Field.LOCAL_BLAST_DESC.toString(), protein.getLocalBlastHit()));
                            sbSep.append(String.format("%s: %s\n", Field.GO_ANNOTATION.toString(), protein.getCleanFullGO()));
                            sbSep.append(String.format("%s: %s\n", Field.INTERPRO_ANNOTATION.toString(), protein.getCleanInterpro()));
                            sbSep.append(String.format("%s: %s\n", Field.PFAM_ANNOTATION.toString(), protein.getCleanPfam()));
                            sbSep.append(String.format("%s: %s\n", Field.PANTHER_ANNOTATION.toString(), protein.getCleanPanther()));
                            sbSep.append(String.format("%s: %s\n", Field.SEQUENCE.toString(), protein.getSequence()));
                            sbSep.append("\n");
                        } catch (CompoundNotFoundException ex) {
                            error("It is not possible to calculate molecular mass, isoelectric point," + " aromaticity and hydropathy of the protein" + protein.getId() + ". " + "The sequence appears to be in error, check the contents of the FASTA " + "file and run the program again. Check for non-amino acid characters with X and *.");
                            ex.printStackTrace();
                            System.exit(1);
                        }
                    }
                    finalOut = sbSep.toString();
                }
                createFile(finalOut, "output" + output.extension);
            }
            info("Creating ODF (Open Document Format) file ");
            tsvToSheets(Parameters.getTemporaryFile("output.tsv"), Parameters.getTemporaryFile("output.odf"));

            info("Creating XLS (Excel Spreadsheet) file");
            tsvToSheets(Parameters.getTemporaryFile("output.tsv"), Parameters.getTemporaryFile("output.xls"));

            if (interpro) {
                MarkdownHelper.appendHeader(outputmd, "Gene Ontology", 2);
                if (!goF.isEmpty()) {
                    MarkdownHelper.appendHeader(outputmd, "Molecular Function", 4);
                    MarkdownHelper.appendTable(outputmd, goHeader, goHeaderAlignments, goF);
                }
                if (!goC.isEmpty()) {
                    MarkdownHelper.appendHeader(outputmd, "Cellular Component", 4);
                    MarkdownHelper.appendTable(outputmd, goHeader, goHeaderAlignments, goC);
                }

                if (!goP.isEmpty()) {
                    MarkdownHelper.appendHeader(outputmd, "Biological Process", 4);
                    MarkdownHelper.appendTable(outputmd, goHeader, goHeaderAlignments, goP);
                }
                MarkdownHelper.appendLine(outputmd);
            }

            MarkdownHelper.appendTable(outputmd, headerMd, alignMd, dataOutpumd);
            if (proteins.size() > 10) {
                MarkdownHelper.appendHeader(outputmd, "Only top 10 proteins\n", 5);
            }

            MarkdownHelper.appendLine(outputmd);

            outputmd.append("\n");
            MarkdownHelper.appendHeader(outputmd, "Do you have a question or tips? Please contact us! E-mail: renato.simoes@ifsc.edu.br", 5);

            outputmd.append("Generated time: " + new Date());


            createFile(outputmd.toString(), "output.md");

            if (source == Main.Source.biolib) {
                String outBiolib = outputmd.toString();
                outBiolib = outBiolib.replace("his-kda-bar.png", "/fastprotein/his-kda-bar.png");
                outBiolib = outBiolib.replace("kda-vs-pi.png", "/fastprotein/kda-vs-pi.png");
                outBiolib = outBiolib.replace("subcell-resume-bar.png", "/fastprotein/subcell-resume-bar.png");
                outBiolib = outBiolib.replace("subcell-resume-pie.png", "/fastprotein/subcell-resume-pie.png");

                createFile(outBiolib, "output-biolib.md");
            }


            createFile(summary.getJSON(), "summary.json");
        } catch (IOException ex) {
            ex.printStackTrace();
            error("An error occurred when saving some file. " + ex.getMessage());
        }
        moveFiles();


        org.apache.commons.io.FileUtils.moveDirectory(new File(Parameters.TEMP_DIR), new File(outputFolder));

        if (Parameters.ZIP) {
            File out = new File(outputFolder);
            try {
                FileUtils.zip(out);
                info("File zip generated: " + out.getAbsolutePath());
                org.apache.commons.io.FileUtils.deleteDirectory(out);
            } catch (Exception e) {
                error("Error zip folder " + out.getAbsolutePath());
                error("\t" + e.getMessage());

            }
        }


        long endTime = System.currentTimeMillis(); // Get end time in milliseconds
        long totalTime = endTime - startTime; // Calculate total time in milliseconds

        int seconds = (int) (totalTime / 1000) % 60;
        int minutes = (int) ((totalTime / (1000 * 60)) % 60);
        int hours = (int) ((totalTime / (1000 * 60 * 60)) % 24);

        info();
        info("Processed proteins: " + proteins.size());
        info("Process started at: " + new Date(startTime));
        info("Process ended at: " + new Date(endTime));
        info(String.format("Total runtime: %d hours, %d minutes, %d seconds \n", hours, minutes, seconds));
        info("Check out your generated files in " + outputFolder);
        info("Thank you for using FastProtein, we hope it will be useful for your research. Please, cite us!");

    }

    public static void moveFiles() throws IOException {
        File tempDir = new File(Parameters.TEMP_DIR);
        File[] arqs = tempDir.listFiles();

        File tempRaw = new File(Parameters.getTemporaryFile("raw"));
        if (!tempRaw.exists()) {
            tempRaw.mkdirs();
        }
        for (File arq : arqs) {

            if (arq.isFile()) {
                var dest = switch (arq.getName()) {
                    case "predgpi.txt",
                            "kdaiso.csv",
                            "wolfpsort.txt",
                            "tmhmm2.txt",
                            "signalp5.txt",
                            "erret.txt",
                            "nglyc.txt",
                            "interpro.tsv",
                            "phobius.txt" -> tempRaw;
                    default -> tempDir;
                };
                //FileUtils.move(arq, dest);
                if (dest.equals(tempRaw)) {
                    File rawFile = new File(Parameters.getTemporaryFile("raw/"+arq.getName()));
                    if(rawFile.exists()){
                        org.apache.commons.io.FileUtils.copyFile(arq, rawFile);
                    }else {
                        org.apache.commons.io.FileUtils.moveFileToDirectory(arq, dest, true);
                    }
                }
            } else if (arq.getName().contains("TMHMM")) {
                //Removing temporary files generated by TMHMM
                //arq.deleteOnExit();
                org.apache.commons.io.FileUtils.deleteDirectory(arq);
            } else if (arq.getName().contains("blast_local") || arq.getName().contains("blast_remote")) {
                try {
                    //Moving files generated by blast results
                    org.apache.commons.io.FileUtils.moveDirectoryToDirectory(arq, tempRaw, true);
                    //FileUtils.move(arq, tempRaw);
                } catch (Exception e) {
                    error("Error moving directory " + arq.getAbsolutePath());
                }
            }
        }
    }

    // function to sort hashmap by values
    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(list, (i1, i2) -> i2.getValue().compareTo(i1.getValue()));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


}
