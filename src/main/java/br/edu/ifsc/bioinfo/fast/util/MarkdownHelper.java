/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.steppschuh.markdowngenerator.image.Image;
import net.steppschuh.markdowngenerator.link.Link;
import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.rule.HorizontalRule;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.emphasis.ItalicText;
import net.steppschuh.markdowngenerator.text.heading.Heading;

/**
 * @author renato
 */
public class MarkdownHelper {

    public static Integer CENTER = Table.ALIGN_CENTER;
    public static Integer RIGHT = Table.ALIGN_RIGHT;
    public static Integer LEFT = Table.ALIGN_LEFT;

    public static void appendHeader(StringBuilder base, String txt, int level) {
        base.append(new Heading(txt, level)).append("\n");
    }

    public static void appendList(StringBuilder base, List<Object> items) {

        base.append(new UnorderedList<>(items));
    }

    public static <K extends Object, V extends Object> void appendTableFromMap(StringBuilder base, String[] header, HashMap<K, V> map) {
        Table.Builder tableBuilder = new Table.Builder().withAlignments(LEFT, LEFT).addRow(header);

        for (Map.Entry entry : map.entrySet()) {
            tableBuilder.addRow(entry.getKey(), entry.getValue());
        }

        base.append(tableBuilder.build()).append("\n");
    }

    public static void appendTable(StringBuilder base, String[] header, Integer[] alignments, ArrayList<String[]> data) {
        Table.Builder tableBuilder = new Table.Builder().withAlignments(alignments).addRow(header);

        for (String[] strings : data) {
            tableBuilder.addRow(strings);
        }

        base.append(tableBuilder.build()).append("\n");
    }

    public static String getItalic(String txt) {
        return new ItalicText(txt).toString();
    }

    public static String getBold(String txt) {
        return new BoldText(txt).toString();
    }

    public static String getLink(String txt, String url) {
        return new Link(txt, url).toString();
    }

    public static String getLink(String url) {
        return new Link(url).toString();
    }

    public static void appendLine(StringBuilder base) {
        base.append(new HorizontalRule().toString()).append("\n");
    }

    public static void appendImage(StringBuilder base, String text, String url) {
        base.append(new Image(text, url).toString()).append("\n");
    }
}
