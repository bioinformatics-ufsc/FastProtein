/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.util;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.biojava.nbio.ontology.Ontology;
import org.biojava.nbio.ontology.Term;
import org.biojava.nbio.ontology.Triple;
import org.biojava.nbio.ontology.io.OboParser;

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
        go = cleanGONumber(go);
        return goMembrane.get(go) != null;
    }

    public static Type getType(String go) {
        go = cleanGONumber(go);

        try {
            Term term;
            if(ontology.containsTerm(go)) {
                term = ontology.getTerm(go);
            }else{
                term = findAlternative(go);
            }
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
        go = cleanGONumber(go);

        try {
            Term term;
            if(ontology.containsTerm(go)) {
                term = ontology.getTerm(go);
            }else{
                term = findAlternative(go);
            }

            if (term != null) {
                return term.getDescription();
            }

            return "";
        } catch (Exception e) {
            debug(String.format("GO %s not found: %s\n", go, e.getMessage()));
            debug("To avoid this error, update the file " + Parameters.FAST_PROTEIN_HOME + "/data/obo.obo:");
            debug(Parameters.FAST_PROTEIN_HOME + "/bin/update_gos.sh");
            return "";
        }
    }

    public static Term getTerm(String go) {
        go = cleanGONumber(go);
        Term term;
        if(ontology.containsTerm(go)) {
            term = ontology.getTerm(go);
        }else{
            term = findAlternative(go);
        }
        return term;
    }

    public static String getFullOntology(String go) {
        go = cleanGONumber(go);
        Term term;
        if(ontology.containsTerm(go)) {
            term = ontology.getTerm(go);
        }else{
            term = findAlternative(go);
        }
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

    public static String cleanGONumber(String input) {
        String regex = "GO:\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group();
        }

        return "-";
    }

    public static Term findAlternative(String alternative) {
        for (Term t : ontology.getTerms()) {
            if (t instanceof Triple) {
                Triple triple = (Triple) t;
                if (triple.getSubject() != null) {
                    if (triple.getSubject().getAnnotation() != null) {
                        for (Object key : triple.getSubject().getAnnotation().keys()) {
                            if (key instanceof String && ((String) key).equals("alt_id")) {
                                ArrayList list = (ArrayList) triple.getSubject().getAnnotation().getProperty("alt_id");
                                if (list.contains(alternative)) {
                                    debug(alternative + " is alternative of: " + triple.getSubject().toString());
                                    return getTerm(triple.getSubject().toString());
                                }
                            }
                        }
                    }
                }
            }
        }
        debug(alternative + " not found as alternative GO");
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getType("GO:0047952"));
        System.out.println(getFullOntology("GO:0047952"));
        System.out.println(getOntology("GO:0047952"));
        System.out.println(getType("GO:0036439"));
        System.out.println(getFullOntology("GO:0036439"));
        System.out.println(getOntology("GO:0036439"));
        System.out.println(getType("GO:0004367"));
        System.out.println(getFullOntology("GO:0004367"));
        System.out.println(getOntology("GO:0004367"));
    }


}
