/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.entity;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;
import br.edu.ifsc.bioinfo.fast.util.ProteomicCalculator;
import br.edu.ifsc.bioinfo.fast.util.GeneOntologyUtil;
import br.edu.ifsc.bioinfo.fast.util.InterproUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.biojava.nbio.aaproperties.PeptideProperties;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;
import static org.biojava.nbio.aaproperties.Utils.*;
/**
 * @author renato.simoes
 */
public class Protein {

    private String id = "";
    private String header = "";
    private String sequence = "";
    private String subcellularLocalization = "";
    private Integer transmembrane = 0;
    private Integer phobiusTM = 0;
    private boolean phobiusSP = false;
    private String signalp5 = "";
    private String localAlignmentHit = "";
    private ArrayList<Domain> nglycDomains = new ArrayList<>();
    private ArrayList<Domain> erretDomains = new ArrayList<>();

    private TreeSet<String> interpro = new TreeSet<>();
    private TreeSet<String> go = new TreeSet<>();
    private TreeSet<String> pfam = new TreeSet<>();
    private TreeSet<String> panther = new TreeSet<>();

    private HashMap<String, String> interproMap = new HashMap<>();
    private HashMap<String, String> goMap = new HashMap<>();

    private boolean gpi = false;
    private String gpiEvidence;
    private TreeSet<String> membraneEvidences;
    private TreeSet<String> membraneFullEvidences;

    public Protein(String id) {
        this.id = id;
    }

    public Protein(String id, String sequence) {
        this.id = id;
        this.sequence = sequence;
        removeLastStopCodon();
        if(Parameters.TRUNCATE_SEQUENCE){
            truncateSequence();
        }
    }

    public TreeSet<String> getSeparatedGO() {
        TreeSet<String> gos = new TreeSet<>();
        for (String rawGo : go) {
            if (rawGo.contains("|")) {
                String[] goUnit = rawGo.split("[|]");
                gos.addAll(Arrays.asList(goUnit));
            } else {
                gos.add(rawGo);
            }
        }
        return gos;
    }

    public TreeSet<String> getFullSeparatedGO() {
        TreeSet<String> gos = getSeparatedGO();
        TreeSet<String> newGos = getSeparatedGO();
        for (String go1 : gos) {
            newGos.add(String.format("%s:%s - %s", GeneOntologyUtil.getType(go1), go1, GeneOntologyUtil.getOntology(go1)));
        }

        return newGos;
    }

    public String getCleanGOWegoFormat() {
        return String.format("%s\t%s", id, getCleanGO().replaceAll(", ", "\t"));
    }

    public String getCleanFullGO() {

        return String.join(", ", getFullSeparatedGO());
    }

    public String getCleanGO() {

        return String.join(", ", getSeparatedGO());
    }

    public String getCleanInterpro() {
        return String.join(", ", interpro);
    }

    public String getCleanPfam() {
        return String.join(", ", pfam);
    }

    public String getCleanPanther() {
        return String.join(", ", panther);
    }

    public void addInterpro(String interproId, String interproAnnot) {
        if (!interproId.trim().equals("-")) {
            interproMap.put(interproId, interproAnnot);
            interpro.add(String.format("%s - %s", interproId, interproAnnot));
        }
    }

    public void addGO(String go) {
        if (!go.trim().equals("-")) {
            this.go.add(go);
        }
        for (String goid : getSeparatedGO()) {
            goMap.put(goid, GeneOntologyUtil.getOntology(goid));
        }
    }

    public void addPfam(String pfam) {
        if (!pfam.trim().equals("-")) {
            this.pfam.add(pfam);
        }
    }

    public void addPanther(String panther) {
        if (!panther.trim().equals("-")) {
            this.panther.add(panther);
        }
    }

    public void setLocalAlignmentHit(String localAlignmentHit) {
        this.localAlignmentHit = localAlignmentHit;
    }

    public String getLocalAlignmentHit() {
        return localAlignmentHit;
    }

    public void setTransmembrane(Integer transmembrane) {
        this.transmembrane = transmembrane;
    }

    public Integer getTransmembrane() {
        return transmembrane;
    }

    public void addNglycDomain(Domain domain) {
        nglycDomains.add(domain);
    }

    public void addErretDomain(Domain domain) {
        erretDomains.add(domain);
    }

    public String getSubcellularLocalization() {
        return subcellularLocalization;
    }

    public void setSubcellularLocalization(String subcellularLocalization) {
        this.subcellularLocalization = subcellularLocalization;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the sequence
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the length
     */
    public Integer getLength() {
        return sequence.length();
    }

    /**
     * @return the kda
     */
    public Double getKda() throws CompoundNotFoundException {
        return ProteomicCalculator.getMolecularWeight(sequence) / 1000;
    }

    public String getKdaStr() throws CompoundNotFoundException {
        return String.format("%.2f", getKda());
    }

    /**
     * @return the isoelectricPointAvg
     */
    public Double getIsoelectricPointAvg() throws CompoundNotFoundException {

        return ProteomicCalculator.getIsoelectricPoint(sequence);
    }

    public String getIsoelectricPointAvgStr() throws CompoundNotFoundException {
        return String.format("%.2f", getIsoelectricPointAvg());
    }

    /**
     * @return the hydropathy
     */
    public Double getHydropathy() throws CompoundNotFoundException {

        return ProteomicCalculator.getHydropathy(sequence);
    }

    public String getHydropathyStr() throws CompoundNotFoundException {
        return String.format("%.2f", getHydropathy());
    }

    /**
     * @return the erretTotal
     */
    public Integer getErretTotal() {
        return erretDomains.size();
    }

    /**
     * @return the erretDomains
     */
    public String getErretDomainsAsString() {
        ArrayList<String> domains = new ArrayList<>();
        for (Domain erretDomain : erretDomains) {
            domains.add(erretDomain.toString());
        }
        return String.join(";", domains);
    }

    /**
     * @return the nGlycTotal
     */
    public Integer getnGlycTotal() {
        return nglycDomains.size();
    }

    /**
     * @return the nGlycDomains
     */
    public String getnGlycDomainsAsString() {
        ArrayList<String> domains = new ArrayList<>();
        for (Domain nglycDomain : nglycDomains) {
            domains.add(nglycDomain.toString());
        }
        return String.join(";", domains);
    }

    public double getAromaticity() {
        return ProteomicCalculator.getAromaticity(sequence);
    }

    public String getAromaticityStr() throws CompoundNotFoundException {
        return String.format("%.2f", getAromaticity());
    }

    public ArrayList<Domain> getNglycDomains() {
        return nglycDomains;
    }

    public ArrayList<Domain> getErretDomains() {
        return erretDomains;
    }

    public String getSignalp5() {
        if (signalp5.equals("OTHER"))
            return "-";
        return signalp5;
    }

    public void setSignalp5(String signalp5) {
        this.signalp5 = signalp5;
    }

    public String toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("header", header);
        json.put("sequence", sequence);
        json.put("subcellular_localization", subcellularLocalization);
        json.put("tm", transmembrane);
        json.put("phobius_tm", phobiusSP);
        json.put("signalp5", signalp5);
        json.put("phobius_sp", phobiusSP);
        json.put("local_alignment_hit", localAlignmentHit);
        json.put("gpi-anchored", gpi);
        json.put("gpi-anchored-evidence", gpiEvidence);
        json.put("membrane", membraneEvidences.size());
        json.put("membrane_evidence", getMembraneEvidencesAsString());
        json.put("membrane_evidence_complete", getMembraneFullEvidencesAsString());


        JSONArray nglyc = new JSONArray();
        for (Domain nglycDomain : nglycDomains) {
            nglyc.add(nglycDomain.toString());
        }
        json.put("nglyc", nglyc);
        JSONArray erret = new JSONArray();
        for (Domain erretDomain : erretDomains) {
            erret.add(erretDomain.toString());
        }
        json.put("erret", erret);

        JSONArray pfamArray = new JSONArray();
        for (String pf : pfam) {
            pfamArray.add(pf);
        }
        json.put("pfam", pfamArray);
        JSONArray pantherArray = new JSONArray();
        for (String pt : panther) {
            pantherArray.add(pt);
        }
        json.put("panther", pantherArray);

        JSONArray interproIdArray = new JSONArray();
        JSONArray interproDescArray = new JSONArray();
        for (Map.Entry<String, String> entry : interproMap.entrySet()) {
            interproIdArray.add(entry.getKey());
            interproDescArray.add(entry.getValue());
        }

        JSONArray goIdArray = new JSONArray();
        JSONArray goDescArray = new JSONArray();
        for (Map.Entry<String, String> entry : goMap.entrySet()) {
            goIdArray.add(entry.getKey());
            goDescArray.add(entry.getValue());
        }
        json.put("interpro_id", interproIdArray);
        json.put("interpro_desc", interproDescArray);
        json.put("go_id", goIdArray);
        json.put("go_desc", goDescArray);
        return json.toString();
    }

    public void setGpi(boolean gpi) {
        this.gpi = gpi;
    }

    public boolean isGpi() {
        return gpi;
    }

    public void setGpiEvidence(String gpiEvidence) {
        this.gpiEvidence = gpiEvidence;
    }

    public String getGpiEvidence() {
        return gpiEvidence;
    }

    public void processMembraneEvidences() {
        membraneEvidences = new TreeSet<>();
        membraneFullEvidences = new TreeSet<String>();
        if (gpi) {
            membraneEvidences.add("GPI");
        }
        if (transmembrane > 0) {
            membraneEvidences.add("TM");
        }

        if (phobiusTM > 0) {
            membraneEvidences.add("PHOBIUS_TM");
        }

        if (subcellularLocalization.toLowerCase().contains("plas")) {
            membraneEvidences.add("SL");
        }

        membraneFullEvidences.addAll(membraneEvidences);

        for (String currentGO : goMap.keySet()) {
            if (GeneOntologyUtil.isGOMembrane(currentGO)) {
                membraneEvidences.add("GO");
                membraneFullEvidences.add(String.format("%s - %s", currentGO, goMap.get(currentGO)));
            }
        }

        for (String currentIPR : interproMap.keySet()) {
            if (InterproUtil.isInterproMembrane(currentIPR)) {
                membraneEvidences.add("IPR");
                membraneFullEvidences.add(String.format("%s:%s", currentIPR, interproMap.get(currentIPR)));
            }
        }

    }

    public TreeSet<String> getMembraneEvidences() {
        return membraneEvidences;
    }

    public String getMembraneEvidencesAsString() {
        return String.join("|", membraneEvidences);
    }

    public String getMembraneFullEvidencesAsString() {
        return String.join("|", membraneFullEvidences);
    }

    public Integer getPhobiusTM() {
        return phobiusTM;
    }

    public boolean isPhobiusSP() {
        return phobiusSP;
    }

    public void setPhobiusSP(boolean phobiusSP) {
        this.phobiusSP = phobiusSP;
    }

    public void setPhobiusTM(Integer phobiusTM) {
        this.phobiusTM = phobiusTM;
    }

    public String getFasta() {
        return String.format(">%s\n%s", id, sequence);
    }

    public void removeLastStopCodon(){
        if(sequence.endsWith("*")){
            debug(id+ " - Removing the last stop codon from the sequence");
            sequence=sequence.substring(0,sequence.length()-1);
        }
    }

    public boolean isSequenceValid(){
        return !doesSequenceContainInvalidChar(sequence, PeptideProperties.standardAASet);
    }

    public void truncateSequence(){
        if(!isSequenceValid()) {
            debug(id+ " - removing all non-aminoacid code from the sequence");
            String newSequence = cleanSequence(sequence, PeptideProperties.standardAASet);
            sequence = newSequence.replaceAll("-", "");
        }
    }
}
