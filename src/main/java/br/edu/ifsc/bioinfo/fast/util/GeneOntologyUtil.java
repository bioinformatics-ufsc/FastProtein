/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.util;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import org.biojava.nbio.ontology.Ontology;
import org.biojava.nbio.ontology.Term;
import org.biojava.nbio.ontology.io.OboParser;
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * Downloaded from http://geneontology.org/docs/download-ontology/
 *
 * @author renato
 */
public class GeneOntologyUtil {

    public enum Type {
        cellular_component("C"),
        molecular_function("F"),
        biological_process("P");

        private String type;

        Type(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }

    }

    private static OboParser parser = new OboParser();
    private static Ontology ontology;

    private static HashMap<String, String> goMembrane = new HashMap<>();

    static {
        try {
            BufferedReader oboFile = new BufferedReader(new InputStreamReader(new FileInputStream(Parameters.FAST_PROTEIN_HOME + "/data/go.obo")));
            ontology = parser.parseOBO(oboFile, "Gene Ontology", "Core ontology (OBO Format)");

            Scanner sGO = new Scanner(new File(Parameters.FAST_PROTEIN_HOME + "/data/go-membrane.txt"));
            while (sGO.hasNext()) {
                String[] lns = sGO.nextLine().split("=");
                goMembrane.put(lns[0], lns[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isGOMembrane(String go) {
        return goMembrane.get(go) != null;
    }

    public static Type getType(String go) {
        try {
            Term term = ontology.getTerm(go);
            if (term != null) {
                switch ((String) term.getAnnotation().getProperty("namespace")) {
                    case "cellular_component":
                        return Type.cellular_component;
                    case "molecular_function":
                        return Type.molecular_function;
                    case "biological_process":
                        return Type.biological_process;
                }
            }
            return null;
        } catch (Exception e) {
            error(String.format("Type for GO %s not found: %s\n", go, e.getMessage()));
            return null;
        }

    }

    public static String getOntology(String go) {
        try {
            Term term = ontology.getTerm(go);

            if (term != null) {
                return term.getDescription();
            }
            return "";
        } catch (Exception e) {
            error(String.format("GO %s not found: %s\n", go, e.getMessage()));
            return "";
        }
    }

    public static Term getTerm(String go) {
        return ontology.getTerm(go);
    }

    public static String getFullOntology(String go) {
        Term term = ontology.getTerm(go);
        if (term != null) {
            String type = "";
            switch ((String) term.getAnnotation().getProperty("namespace")) {
                case "cellular_component":
                    type = "C";
                    break;
                case "molecular_function":
                    type = "F";
                    break;
                case "biological_process":
                    type = "P";
                    break;
            }
            return String.format("%s:%s - %s", type, go, term.getDescription());
        }
        return "";
    }

}
