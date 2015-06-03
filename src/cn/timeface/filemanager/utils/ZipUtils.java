package cn.timeface.filemanager.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by rayboot on 15/6/2.
 */
public class ZipUtils {

    public static void zipFiles(List<File> fileList, OutputStream os) throws IOException {

        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os));
        try {
            for (int i = 0; i < fileList.size(); ++i) {
                File file = fileList.get(i);
                String filename = file.getName();

                byte[] bytes = readFile(file);
                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);
                zos.write(bytes);
                zos.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            zos.close();
        }
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    public static void main(String[] args) {

        File zipFile = new File("/Users/rayboot/Downloads/zipImages.zip");

        try {
            List<File> imageFiles = new ArrayList<>(10);
            imageFiles.add(new File("/Users/rayboot/Downloads/IMG_1349.JPG"));
            imageFiles.add(new File("/Users/rayboot/Downloads/IMG_7122.JPG"));
            imageFiles.add(new File("/Users/rayboot/Downloads/IMG_1359.JPG"));

            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            zipFiles(imageFiles, fileOutputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
