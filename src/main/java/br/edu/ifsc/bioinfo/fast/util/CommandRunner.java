package br.edu.ifsc.bioinfo.fast.util;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author renato
 */
import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

public class CommandRunner {

    public static void run(String command) throws IOException, InterruptedException {
        String s;
        Process p = Runtime.getRuntime().exec(command);
        //p.waitFor();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String stSuccess = "";
        while ((s = stdInput.readLine()) != null) {
            stSuccess += s;
            stSuccess += "\n";
            debug(s);
        }

        boolean error = false;
        String stError = "";

        while ((s = stdError.readLine()) != null) {
            stError += s;
            stError += "\n";
            error = true;
            //error(s);
        }

        if (error) {
            FileWriter fw = new FileWriter(new File( Parameters.getTemporaryFile("error.txt")));
            fw.append(stError);
            fw.flush();
            fw.close();
        }
        stdInput.close();
        stdError.close();
    }
}
