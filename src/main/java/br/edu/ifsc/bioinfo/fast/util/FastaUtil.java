/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsc.bioinfo.fast.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author renato
 */
public class FastaUtil {
     // Método estático para extrair o nome com base nas condições fornecidas
    public static String extractName(String text) {
        // Verifica se a string contém 'OS='
        if (text.contains("OS=")) {
            // Expressão regular para capturar texto entre o primeiro espaço e 'OS='
            Pattern pattern = Pattern.compile("\\s(.*?)\\sOS=");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } 
        // Se 'OS=' não estiver presente, verifica se a string contém '['
        else if (text.contains("[")) {
            // Expressão regular para capturar texto entre o primeiro espaço e o caractere '['
            Pattern pattern = Pattern.compile("\\s(.*?)\\s\\[");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } 
        // Se nenhuma das condições for satisfeita, retorna tudo após o primeiro espaço
        else {
            Pattern pattern = Pattern.compile("\\s(.*)");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        // Retorna a string inteira se nenhum espaço for encontrado
        return text;
    }

    public static void main(String[] args) {
        // Testando o método com diferentes strings
        String text1 = "A0A1A8WE70 DNA topoisomerase 2";
        String text2 = "sp|P86938|JBP1_TRYB2 Thymine dioxygenase JBP1 OS=Trypanosoma brucei brucei (strain 927/4 GUTat10.1) OX=185431 GN=JBP1 PE=1 SV=1";
        String text3 = "A0A1C3KYZ4 Alanine--tRNA ligase";
        String text4 = "ExampleText";

        System.out.println("Extracted Name 1: " + extractName(text1));
        System.out.println("Extracted Name 2: " + extractName(text2));
        System.out.println("Extracted Name 3: " + extractName(text3));
        System.out.println("Extracted Name 4: " + extractName(text4));
    }
}
