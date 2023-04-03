/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.protein;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.UUID;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class Parameters {
    public static int INTERPRO_SPLIT = 500;

    public static String FAST_PROTEIN_HOME = ".";
    public static String TEMP_DIR = "temp";
    public static boolean ZIP = false;
    public static String INTERPRO_HOME = ".";
    public static boolean PAUSE = false;

    public static String EMAIL_EBI_WS = "renato.simoes@ifsc.edu.br";

    public static void createTempDir() {
        try {
            // Define o diretório pai para o diretório temporário
            // Gera um nome aleatório para o diretório temporário
            String randomID = UUID.randomUUID().toString();
            // Cria um diretório temporário usando o nome aleatório
            File tempDir = new File(FAST_PROTEIN_HOME + "/temp/" + randomID);
            tempDir.mkdirs();
            TEMP_DIR = tempDir.getAbsolutePath();
            debug("Temp dir created: " + TEMP_DIR);
        } catch (Exception e) {
            info("Error creating temporary dir. Check your FastProtein installation.");
            info("\t" + e.getMessage());

            System.exit(0);
        }
    }

    /**
     * Return a String with a absolute path of a file (only String)
     *
     * @param file
     * @return
     */
    public static String getTemporaryFile(String file) {
        return String.format("%s/%s", TEMP_DIR, file);
    }

    public static void pause(){
        if(PAUSE) {
            Scanner s = new Scanner(System.in);
            System.out.println("Press enter to continue");
            s.nextLine();
        }
    }
}
