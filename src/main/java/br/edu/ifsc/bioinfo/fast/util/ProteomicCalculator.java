/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.util;

import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.aaproperties.PeptidePropertiesImpl;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;

/**
 * @author renato.simoes
 */
public class ProteomicCalculator {

    private static PeptidePropertiesImpl peptideProperties = new PeptidePropertiesImpl();

    public static double getMolecularWeight(String sequence) throws CompoundNotFoundException {
        return peptideProperties.getMolecularWeight(new ProteinSequence(sequence));
    }

    public static double getIsoelectricPoint(String sequence) throws CompoundNotFoundException {
        return peptideProperties.getIsoelectricPoint(new ProteinSequence(sequence));
    }

    public static double getHydropathy(String sequence) throws CompoundNotFoundException {
        return peptideProperties.getAvgHydropathy(new ProteinSequence(sequence));
    }


    /**
     * Relative frequency of F Y or W
     * <p>
     * https://www.ncbi.nlm.nih.gov/pmc/articles/PMC310293/pdf/nar00039-0320.pdf
     *
     * @param sequence
     * @return
     */
    public static double getAromaticity(String sequence) {
        sequence = sequence.toUpperCase();
        int total = sequence.length();
        int qtdF = StringUtils.countMatches(sequence, "F");
        int qtdY = StringUtils.countMatches(sequence, "Y");
        int qtdW = StringUtils.countMatches(sequence, "W");
        return (qtdF + qtdY + qtdW) / (double) (total);
    }
}
