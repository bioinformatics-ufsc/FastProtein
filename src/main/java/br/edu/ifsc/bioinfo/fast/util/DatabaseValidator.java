package br.edu.ifsc.bioinfo.fast.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DatabaseValidator {

    // Método para verificar se um banco de dados BLAST é válido
    public static boolean isValidBlastDatabase(String databasePath) {
        String command = "blastdbcmd -db " + databasePath + " -info";
        return executeCommand(command);
    }


    // Método auxiliar para executar o comando e verificar se ele foi bem-sucedido
    private static boolean executeCommand(String command) {
        Process process = null;
        try {
            // Executa o comando no sistema operacional
            process = Runtime.getRuntime().exec(command);
            // Lê a saída do comando
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Imprime a saída para depuração (opcional)
                System.out.println(line);
            }
            // Espera o processo terminar e obtém o código de saída
            int exitCode = process.waitFor();
            // Se o código de saída for 0, o comando foi bem-sucedido
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static void main(String[] args) {
        // Testando os métodos
        String blastDbPath = "/bioinformatic/git-ufsc/FastProtein/docker/fastprotein/bin/temp1/input1";

        boolean isBlastValid = isValidBlastDatabase(blastDbPath);

        System.out.println("BLAST Database Valid: " + isBlastValid);
    }
}
