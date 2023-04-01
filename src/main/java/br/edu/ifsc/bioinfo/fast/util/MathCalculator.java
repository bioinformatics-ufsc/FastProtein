/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.util;

import java.util.ArrayList;

/**
 *
 * @author renato
 */
public class MathCalculator {

    private ArrayList<Double> numbers;

    public MathCalculator( ArrayList<Double> n) {
        this.numbers = n;
    }

    public double mean() {
        return sum() / numbers.size();
    }

    public double sum() {
        double sum = 0;
        for (double d : numbers) {
            sum += d;
        }
        return sum;
    }

    public double sd() {
        double sd = 0.0;
        double mean = mean();
        

        for (double num : numbers) {
            sd += Math.pow(num - mean, 2);
        }

        return Math.sqrt(sd / numbers.size());
    }
}
