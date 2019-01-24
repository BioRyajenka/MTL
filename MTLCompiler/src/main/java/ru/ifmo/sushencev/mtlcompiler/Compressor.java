package ru.ifmo.sushencev.mtlcompiler;

import com.google.common.base.Joiner;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Jackson on 19.12.2016.
 */
public class Compressor {
    public static void main(String[] args) throws IOException {
        try (PrintWriter writer = new PrintWriter("M.compressed")) {
            writer.print(compress(Joiner.on("$").join(Util.getLines("M.obf"))));
        }
    }

    public static String compress(String contents) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(contents.getBytes());
        //System.out.println("String length : " + str.length());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            gzip.write(buffer, 0, bytesRead);
        }
        gzip.close();

        byte[] bytes = baos.toByteArray();
        System.out.println("bytes amount is " + bytes.length);
        byte[] encoded = Base64.getEncoder().encode(bytes);
        return new String(encoded);
    }
}