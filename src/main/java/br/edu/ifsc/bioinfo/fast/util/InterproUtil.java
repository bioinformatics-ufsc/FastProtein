/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.util;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author renato
 */
public class InterproUtil {
    private static HashMap<String, String> interproMembrane = new HashMap<>();
    static {
        try {
            Scanner sInterpro = new Scanner(new File(Parameters.FAST_PROTEIN_HOME + "/data/interpro-membrane.txt"));
            while (sInterpro.hasNext()) {
                String[] lns = sInterpro.nextLine().split("=");
                interproMembrane.put(lns[0], lns[1]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InterproUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isInterproMembrane(String interpro) {
        return interproMembrane.get(interpro) != null;
    }

}
