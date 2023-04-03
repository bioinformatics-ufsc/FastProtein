/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.util;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;
import br.edu.ifsc.bioinfo.fast.protein.entity.Protein;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class FASTASplitter {

    public static List<File> subfasta(List<Protein> proteins, int limit, String name) throws IOException {
        List<List<Protein>> output = ListUtils.partition(proteins, limit);
        ArrayList<File> files = new ArrayList<>();
        int i = 1;
        for (List<Protein> list : output) {
            String fileName = String.format("%s-%s.fasta", name, i++);
            debug("Generating fasta: " + fileName);
            File f = new File(Parameters.getTemporaryFile(fileName));
            FileWriter fw = new FileWriter(f);
            for (Protein protein : list) {
                fw.append(String.format(">%s\n%s\n", protein.getId(), protein.getSequence()));
            }
            fw.flush();
            fw.close();
            files.add(f);
        }
        debug("Splitting FASTA: " + output.size() + " file(s)");
        return files;
    }
}
