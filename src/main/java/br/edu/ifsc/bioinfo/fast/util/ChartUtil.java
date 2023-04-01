package br.edu.ifsc.bioinfo.fast.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.LegendPosition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChartBuilder;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

public class ChartUtil {
    public static boolean createBarSubcell(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            Iterator<String> it = lines.iterator();
            //Skipping header
            it.next();
            int totalOther = 0;

            ArrayList<String> categoriasKeys = new ArrayList<>();
            ArrayList<Integer> categoriasValues = new ArrayList<>();

            ArrayList<String> localizationKeys = new ArrayList<>();
            ArrayList<Integer> localizationValues = new ArrayList<>();

            while (it.hasNext()) {
                String ln = it.next().trim();
                String[] cols = ln.split("\t");
                localizationKeys.add(cols[0]);
                localizationValues.add(Integer.valueOf(cols[2]));

                if (cols[1].trim().equals("Other")) {
                    totalOther += Integer.valueOf(cols[2]);
                } else {
                    categoriasKeys.add(cols[1]);
                    categoriasValues.add(Integer.valueOf(cols[2]));
                }
            }
            if (totalOther > 0) {
                categoriasKeys.add("Other");
                categoriasValues.add(totalOther);
            }
            createCategoryChart(localizationKeys, localizationValues, "Subcellular localization", "Proteins", "Localization", "subcell-all");
            createCategoryChart(categoriasKeys, categoriasValues, "Subcellular localization", "Proteins", "Localization", "subcell-resume");
            createPieChart(localizationKeys, localizationValues, "Subcellular localization", "subcell-all");
            createPieChart(categoriasKeys, categoriasValues, "Subcellular localization", "subcell-resume");

            return true;
        } catch (IOException e) {
            debug("Error generating subcell chart");
            return false;
        }
    }

    private static boolean createPieChart(ArrayList<String> keys, ArrayList<Integer> values,  String title, String file) {
        try {
            // Create Chart
            PieChart pieChart = new PieChartBuilder()
                    .width(800)
                    .height(600)
                    .title(title)
                    .theme(Styler.ChartTheme.GGPlot2)
                    .build();

            Font font = pieChart.getStyler().getBaseFont();
            for (int i = 0; i < keys.size(); i++) {
                pieChart.addSeries(keys.get(i), values.get(i));
            }

            pieChart.getStyler().setPlotBackgroundColor(Color.white);
            pieChart.getStyler().setLegendFont(new Font(font.getName(), font.getStyle(), 16));

            debug("Saving Pie kda");

            BitmapEncoder.saveBitmap(pieChart, Parameters.getTemporaryFile(file + "-pie.png"), BitmapEncoder.BitmapFormat.PNG);
            BitmapEncoder.saveBitmapWithDPI(pieChart, Parameters.getTemporaryFile(file + "-pie-300dpi.png"), BitmapEncoder.BitmapFormat.PNG, 300);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private static boolean createCategoryChart(ArrayList keys, ArrayList values, String titleX, String titleY, String serie, String file) {
        try {
            // Create Chart
            CategoryChart chartHist = new CategoryChartBuilder()
                    .width(800)
                    .height(600)
                    .title("")
                    .xAxisTitle(titleX)
                    .yAxisTitle(titleY)
                    .theme(Styler.ChartTheme.GGPlot2)
                    .build();
            Font font = chartHist.getStyler().getBaseFont();

            chartHist.addSeries(serie, keys, values);

            chartHist.getStyler().setPlotGridLinesColor(Color.white);
            chartHist.getStyler().setPlotBackgroundColor(Color.white);
            chartHist.getStyler().setLegendFont(new Font(font.getName(), font.getStyle(), 16));
            chartHist.getStyler().setAxisTitleFont(new Font(font.getName(), font.getStyle(), 20));
            chartHist.getStyler().setAxisTickLabelsFont(new Font(font.getName(), font.getStyle(), 16));
            chartHist.getStyler().setXAxisLabelRotation(45);
            chartHist.getStyler().setYAxisDecimalPattern("#");
            debug("Saving Histogram kda");

            BitmapEncoder.saveBitmap(chartHist, Parameters.getTemporaryFile(file + "-bar.png"), BitmapEncoder.BitmapFormat.PNG);
            BitmapEncoder.saveBitmapWithDPI(chartHist, Parameters.getTemporaryFile(file + "-bar-300dpi.png"), BitmapEncoder.BitmapFormat.PNG, 300);

            debug("Parsing file end.");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean generate(File file) {

        try {
            List<Double> kdaList = new ArrayList<>();
            List<Double> phList = new ArrayList<>();
            info("Generating chars");
            if (!file.exists()) {
                throw new Exception("File not found: " + file.getAbsolutePath());
            }
            debug("Parsing file: " + file.getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine(); // ignore first line (column name)
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                kdaList.add(Double.parseDouble(values[0]));
                phList.add(Double.parseDouble(values[1]));
            }
            br.close();

            // Create Chart
            XYChart chart = new XYChartBuilder().width(800).height(600).theme(Styler.ChartTheme.GGPlot2).build();

            // Customize Chart
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            chart.getStyler().setChartTitleVisible(false);
            chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
            chart.getStyler().setMarkerSize(16);
            Font font = chart.getStyler().getBaseFont();
            chart.getStyler().setPlotGridLinesColor(Color.white);
            chart.getStyler().setPlotBackgroundColor(Color.white);
            chart.getStyler().setLegendFont(new Font(font.getName(), font.getStyle(), 16));
            chart.getStyler().setAxisTitleFont(new Font(font.getName(), font.getStyle(), 20));
            chart.getStyler().setAxisTickLabelsFont(new Font(font.getName(), font.getStyle(), 16));

            chart.addSeries("Protein", phList, kdaList);
            chart.setYAxisTitle("Molecular mass (kDa)");
            chart.setXAxisTitle("Isoelectric Point (p.H)");
            chart.setTitle("");

            debug("Saving XY chart - kda vs pi");
            // passo 5: salvar gráfico em um arquivo PNG
            BitmapEncoder.saveBitmap(chart, Parameters.getTemporaryFile("kda-vs-pi.png"), BitmapEncoder.BitmapFormat.PNG);
            BitmapEncoder.saveBitmapWithDPI(chart, Parameters.getTemporaryFile("kda-vs-pi-300dpi.png"), BitmapEncoder.BitmapFormat.PNG, 300);
            // Create Chart

            Map<Integer, Integer> mapKda = groupValues(kdaList);
            // Series
            ArrayList<Integer> keys = new ArrayList<>();
            ArrayList<Integer> values = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : mapKda.entrySet()) {
                keys.add(entry.getKey());
                values.add(entry.getValue());
            }

            createCategoryChart(keys, values, "Molecular mass (kDa)", "Proteins","Proteins group","his-kda");

/*            CategoryChart chartHist = new CategoryChartBuilder()
                    .width(800)
                    .height(600)
                    .title("")
                    .xAxisTitle("Molecular mass (kDa)")
                    .yAxisTitle("Frequency")
                    .theme(Styler.ChartTheme.GGPlot2)
                    .build();

            chartHist.addSeries("Proteins group", keys, values);

            chartHist.getStyler().setPlotGridLinesColor(Color.white);
            chartHist.getStyler().setPlotBackgroundColor(Color.white);
            chartHist.getStyler().setLegendFont(new Font(font.getName(), font.getStyle(), 16));
            chartHist.getStyler().setAxisTitleFont(new Font(font.getName(), font.getStyle(), 20));
            chartHist.getStyler().setAxisTickLabelsFont(new Font(font.getName(), font.getStyle(), 16));
            debug("Saving Histogram kda");
            BitmapEncoder.saveBitmap(chartHist, Parameters.getTemporaryFile("his-kda.png"), BitmapEncoder.BitmapFormat.PNG);
            BitmapEncoder.saveBitmapWithDPI(chartHist, Parameters.getTemporaryFile("his-kda-300dpi.png"), BitmapEncoder.BitmapFormat.PNG, 300);
*/
            debug("Parsing file end.");

        } catch (Exception e) {
            error("Error creating charts");
            error("\t" + e.getMessage());
            return false;
        }
        return true;

    }


    private static Map<Integer, Integer> groupValues(List<Double> valores) {
        Map<Integer, Integer> series = new TreeMap<>();

        for (double valor : valores) {
            int serie = ((int) Math.floor(valor / 50) * 50) + 50;
            int size = series.getOrDefault(serie, 0);
            series.put(serie, size + 1);
        }

        return series;
    }

}
