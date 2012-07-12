/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.csv;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.csvreader.CsvReader;

/**
 * @author fanky10
 *
 */
public class CSVFileRead {

    private static final String CSV_DATA_PATH = "data";
    private static final String CSV_DATA_FILENAME = "tiempos.csv";
    private static final Character CSV_CHARACTER_SEPARATOR = ';';
    public static boolean VERBOSE = false;

    public static void main(String[] args) {
        try {
            CSVFileRead.VERBOSE = true;
            List<TiempoVO> tiempos = getTiempos();
            for (TiempoVO t : tiempos) {
                System.out.print(t.getTiempoRaccion() + "\t");
                System.out.print(t.getTiempoCien() + "\t");
                System.out.println(t.getTiempoFin());
            }
        } catch (Exception e) {
            System.err.println("exception reading data: " + e.getMessage());
        }
    }

    public static List<TiempoVO> getTiempos() throws IOException {
        List<TiempoVO> result = new ArrayList<TiempoVO>();
        CsvReader fileRead = new CsvReader(CSV_DATA_PATH + java.io.File.separator + CSV_DATA_FILENAME, CSV_CHARACTER_SEPARATOR);
        fileRead.readHeaders();
        while (fileRead.readRecord()) {
            result.add(new TiempoVO(fileRead.get("reaccion"), fileRead.get("cien"), fileRead.get("fin")));
        }

        return result;
    }

    private static void verbose(String str) {
        if (VERBOSE) {
            System.out.println(str);
        }
    }
}
