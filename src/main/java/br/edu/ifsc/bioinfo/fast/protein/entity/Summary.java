/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.entity;

import com.github.underscore.U;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author renato
 */
public class Summary {


    public class Information {

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the desc
         */
        public String getDesc() {
            return desc;
        }

        /**
         * @return the total
         */
        public int getTotal() {
            return total;
        }

        private String id;
        private String desc;
        private int total;

        public Information(String id, String desc, int total) {
            this.id = id;
            this.desc = desc;
            this.total = total;
        }

        public String toString() {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("desc", desc);
            json.put("total", total);

            return json.toString();
        }

    }

    private double kdaMean;
    private double kdaSd;
    private double isoMean;
    private double isoSd;
    private double hydroMean;
    private double hydroSd;
    private double aromacityMean;
    private double aromacitySd;
    private int totalMembrane;
    private int totalTM;
    private int totalSp;
    private int totalER;
    private int totalNGlyc;
    private int totalGPI;


    private HashMap<String, Integer> subcellLocalizations = new HashMap<>();
    private HashMap<String, Integer> erret = new HashMap<>();
    private HashMap<String, Integer> nglyc = new HashMap<>();

    private ArrayList<Information> go_C = new ArrayList<>();
    private ArrayList<Information> go_P = new ArrayList<>();
    private ArrayList<Information> go_F = new ArrayList<>();

    private ArrayList<Information> interpro = new ArrayList<>();

    private ArrayList<Protein> proteins = new ArrayList<>();

    public int getTotalGPI() {
        return totalGPI;
    }

    public void addGoC(String goId, String desc, int total) {
        go_C.add(new Information(goId, desc, total));
    }

    public void addGoF(String goId, String desc, int total) {
        go_F.add(new Information(goId, desc, total));
    }

    public void addGoP(String goId, String desc, int total) {
        go_P.add(new Information(goId, desc, total));
    }

    public void addInterpro(String interproId, String desc, int total) {
        interpro.add(new Information(interproId, desc, total));
    }

    public HashMap<String, Integer> getErret() {
        return erret;
    }

    public HashMap<String, Integer> getNglyc() {
        return nglyc;
    }

    public void setErret(HashMap<String, Integer> erret) {
        this.erret = erret;
    }

    public void setNglyc(HashMap<String, Integer> nglyc) {
        this.nglyc = nglyc;
    }

    public void setSubcellLocalizations(HashMap<String, Integer> subcellLocalizations) {
        this.subcellLocalizations = subcellLocalizations;
    }

    public HashMap<String, Integer> getSubcellLocalizations() {
        return subcellLocalizations;
    }

    public void putSubcell(String local, int total) {
        subcellLocalizations.put(local, total);
    }

    public void setProteins(ArrayList<Protein> proteins) {
        this.proteins = proteins;
    }

    public ArrayList<Protein> getProteins() {
        return proteins;
    }

    /**
     * @return the kdaMean
     */
    public double getKdaMean() {
        return kdaMean;
    }

    /**
     * @param kdaMean the kdaMean to set
     */
    public void setKdaMean(double kdaMean) {
        this.kdaMean = kdaMean;
    }

    /**
     * @return the kdaSd
     */
    public double getKdaSd() {
        return kdaSd;
    }

    /**
     * @param kdaSd the kdaSd to set
     */
    public void setKdaSd(double kdaSd) {
        this.kdaSd = kdaSd;
    }

    /**
     * @return the isoMean
     */
    public double getIsoMean() {
        return isoMean;
    }

    /**
     * @param isoMean the isoMean to set
     */
    public void setIsoMean(double isoMean) {
        this.isoMean = isoMean;
    }

    /**
     * @return the isoSd
     */
    public double getIsoSd() {
        return isoSd;
    }

    /**
     * @param isoSd the isoSd to set
     */
    public void setIsoSd(double isoSd) {
        this.isoSd = isoSd;
    }

    /**
     * @return the hydroMean
     */
    public double getHydroMean() {
        return hydroMean;
    }

    /**
     * @param hydroMean the hydroMean to set
     */
    public void setHydroMean(double hydroMean) {
        this.hydroMean = hydroMean;
    }

    /**
     * @return the hydroSd
     */
    public double getHydroSd() {
        return hydroSd;
    }

    /**
     * @param hydroSd the hydroSd to set
     */
    public void setHydroSd(double hydroSd) {
        this.hydroSd = hydroSd;
    }

    /**
     * @return the aromacityMean
     */
    public double getAromacityMean() {
        return aromacityMean;
    }

    /**
     * @param aromacityMean the aromacityMean to set
     */
    public void setAromacityMean(double aromacityMean) {
        this.aromacityMean = aromacityMean;
    }

    /**
     * @return the aromacitySd
     */
    public double getAromacitySd() {
        return aromacitySd;
    }

    /**
     * @param aromacitySd the aromacitySd to set
     */
    public void setAromacitySd(double aromacitySd) {
        this.aromacitySd = aromacitySd;
    }

    /**
     * @return the totalMembrane
     */
    public int getTotalMembrane() {
        return totalMembrane;
    }

    /**
     * @param totalMembrane the totalMembrane to set
     */
    public void setTotalMembrane(int totalMembrane) {
        this.totalMembrane = totalMembrane;
    }

    /**
     * @return the totalSp
     */
    public int getTotalSp() {
        return totalSp;
    }

    /**
     * @param totalSp the totalSp to set
     */
    public void setTotalSp(int totalSp) {
        this.totalSp = totalSp;
    }

    /**
     * @return the totalER
     */
    public int getTotalER() {
        return totalER;
    }

    /**
     * @param totalER the totalER to set
     */
    public void setTotalER(int totalER) {
        this.totalER = totalER;
    }

    /**
     * @return the totalNGlyc
     */
    public int getTotalNGlyc() {
        return totalNGlyc;
    }

    /**
     * @param totalNGlyc the totalNGlyc to set
     */
    public void setTotalNGlyc(int totalNGlyc) {
        this.totalNGlyc = totalNGlyc;
    }

    public void setTotalGPI(int totalGPI) {
        this.totalGPI = totalGPI;
    }

    public void setTotalTM(int totalTM) {
        this.totalTM = totalTM;
    }

    public int getTotalTM() {
        return totalTM;
    }

    public String getJSON() throws Exception {
        JSONObject json = new JSONObject();
        json.put("software", "FastProtein");
        json.put("version", "1.0");
        json.put("date", new Date().toString());
        json.put("proteins_size", getProteins().size());
        json.put("kda_mean", String.format("%.2f",getKdaMean()));
        json.put("kda_sd", String.format("%.2f",getKdaSd()));
        json.put("iso_mean", String.format("%.2f",getIsoMean()));
        json.put("iso_sd", String.format("%.2f",getIsoSd()));
        json.put("hydrophobicity_mean", String.format("%.2f",getHydroMean()));
        json.put("hydrophobicity_sd", String.format("%.2f",getHydroSd()));
        json.put("aromaticity_mean", String.format("%.2f",getAromacityMean()));
        json.put("aromaticity_mean_sd", String.format("%.2f",getAromacitySd()));
        json.put("total_tm", getTotalTM());
        json.put("total_sp", getTotalSp());
        json.put("total_nglyc", getTotalNGlyc());
        json.put("total_erret", getTotalER());
        json.put("total_gpi", getTotalGPI());
        json.put("total_membrane", getTotalMembrane());

        json.put("erret", getErret());
        json.put("nglyc", getNglyc());
        json.put("subcellular_localization", getSubcellLocalizations());

        JSONArray interproArray = new JSONArray();
        for (Information information : interpro) {
            interproArray.add(information);
        }
        json.put("interpro", interproArray);

        JSONArray goArray = new JSONArray();
        for (Information information : go_C) {
            goArray.add(information);
        }
        json.put("go", goArray);

        JSONArray proteinsJson = new JSONArray();
        for (Protein protein : getProteins()) {
            proteinsJson.add(protein.toJson());
        }

        json.put("proteins", proteinsJson);
        return U.formatJson(json.toString());
    }
}
