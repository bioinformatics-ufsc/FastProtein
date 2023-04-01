/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.conversor.geneontology;

import br.edu.ifsc.bioinfo.fast.protein.entity.Protein;
import br.edu.ifsc.bioinfo.fast.util.GeneOntologyUtil;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author renato
 */
public class GeneOntologyProcess {

    private static void addHit(HashMap<String, Integer> map, String key) {
        if(map.containsKey(key)){
            map.put(key, map.get(key)+1);
        }else{
            map.put(key, 1);
        }
    }

    public static HashMap<String, Integer> getMap(List<Protein> proteins, GeneOntologyUtil.Type type) {
        HashMap<String, Integer> map = new HashMap<>();
        for (Protein protein : proteins) {
            for (String go : protein.getSeparatedGO()) {
                if (GeneOntologyUtil.getType(go) == type) {
                    addHit(map, go);
                }
            }
        }
        return map;
    }

}
