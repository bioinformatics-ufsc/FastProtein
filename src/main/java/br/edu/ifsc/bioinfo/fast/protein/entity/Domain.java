/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein.entity;

/**
 * @author renato
 */
public class Domain {
    private String sequence;
    private int start;
    private int end;

    public Domain(String domain, int start, int end) {
        this.sequence = domain;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("%s[%s-%s]", sequence, start, end);
    }

    public String getSequence() {
        return sequence;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }


}
