package br.edu.ifsc.bioinfo.fast.util;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

public class ReportHelper {
    public static void tsvToSheets(String tsv, String name) {
        try {
            List<String> linhas = Files.readAllLines(new File(tsv).toPath());

            WritableWorkbook workbook = Workbook.createWorkbook(new File(name));
            WritableSheet sheet = workbook.createSheet("FastProtein", 0);

            for (int i = 0; i < linhas.size(); i++) {
                String[] cols = linhas.get(i).split("\t");
                for (int j = 0; j < cols.length; j++) {
                    WritableCell wc = new Label(j, i, cols[j]);
                    if(i==0){
                        sheet.addCell(wc);
                    }else {
                        switch (j) {
                            case 1, 2, 3, 4, 5, 7, 8,
                                    10, 14, 15 -> sheet.addCell(new Number(j,i,Double.parseDouble(cols[j])));
                            default -> sheet.addCell(wc);
                        }
                    }
                }
            }

            workbook.write();
            workbook.close();

        } catch (Exception e) {
            error("Error generating sheets");
            error("\t " + e.getMessage());
        }
    }
}
