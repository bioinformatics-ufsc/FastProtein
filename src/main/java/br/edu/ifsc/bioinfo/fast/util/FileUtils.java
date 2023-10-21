/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.bioinfo.fast.util;

import br.edu.ifsc.bioinfo.fast.protein.Parameters;

import java.io.*;
import java.util.zip.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static br.edu.ifsc.bioinfo.fast.util.log.LoggerUtil.*;

/**
 * @author renato
 */
public class FileUtils {

    public static File move(File sourceFile, File targetDirectory) {

        // Use the renameTo() method to move the file to the new directory
        File newFile = new File(targetDirectory, sourceFile.getName());
        boolean success = sourceFile.renameTo(newFile);

        // Check if the file was successfully moved
        if (success) {
            String res = String.format("Moving file %s to %s", sourceFile.getAbsolutePath(), newFile.getAbsolutePath());
            debug(res);
            return newFile;
        } else {
            info(String.format("Error moving file %s to %s\n", sourceFile.getAbsolutePath(), targetDirectory.getAbsolutePath()));
            System.exit(0);
        }
        return null;
    }

    public static File copy(File file, File directory) {

        Path sourcePath = file.toPath();
        Path destinationPath = Paths.get(directory.getPath(), sourcePath.getFileName().toString());

        try {
            Path copied = Files.copy(sourcePath, destinationPath);
            return copied.toFile();
        } catch (IOException e) {
            info("Error copying file " + file.getAbsolutePath());
            info("\t" + e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public static File hasFileOnTemp(String file) {
        File arq = new File(Parameters.getTemporaryFile(file));
        debug("Looking for " + Parameters.getTemporaryFile(file));
        if (arq.exists()) {
            debug("    --->exists.");
            return arq;
        } else {
            arq = new File(Parameters.getTemporaryFile("raw/" + file));
            debug("Looking for " + Parameters.getTemporaryFile("raw/" + file));
            if (arq.exists()) {
                debug("    --->exists.");
                return arq;
            }
        }
        return null;
    }

    public static File createFile(String content, String fileOut) throws IOException {
        /*FileWriter fw = null;
        File outfile = new File(Parameters.getTemporaryFile(fileOut));
        if (outfile.exists())
            outfile.delete();
        fw = new FileWriter(outfile);
        fw.append(content);
        fw.close();
        return outfile;*/


        // Create a FileWriter object with the file name
        File file = new File(Parameters.getTemporaryFile(fileOut));

        // Create a BufferedWriter object to write to the file
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

        // Write to the file
        writer.write(content);

        // Close the BufferedWriter
        writer.close();
        return file;


    }

    public static void copyDirectory(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String[] files = source.list();
            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyDirectory(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }

    /**
     * Zip a folder in the same path as your original folder
     *
     * @param sourceFolder
     * @throws Exception
     */
    public static void zip(File sourceFolder) throws Exception {

        // create output stream to write the zip file
        FileOutputStream fos = new FileOutputStream(sourceFolder.getAbsolutePath() + ".zip");
        ZipOutputStream zos = new ZipOutputStream(fos);
        // add the folder to the zip file
        addFolderToZip("", sourceFolder.getAbsolutePath(), zos);
        // close the streams
        zos.close();
        fos.close();
    }

    private static void addFolderToZip(String path, String sourceFolder, ZipOutputStream zos) throws IOException {
        File folder = new File(sourceFolder);
        if (folder.listFiles() == null) {
            // if the folder is empty, create an empty entry in the zip file
            zos.putNextEntry(new ZipEntry(path + folder.getName() + "/"));
        } else {
            // if the folder is not empty, create an entry for each file/folder in the folder
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    addFolderToZip(path + file.getName() + "/", file.getAbsolutePath(), zos);
                } else {
                    FileInputStream fis = new FileInputStream(file);
                    zos.putNextEntry(new ZipEntry(path + file.getName()));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    fis.close();
                }
            }
        }
    }
}
