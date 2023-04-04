package com.pharmacy.management.config;

import com.pharmacy.management.model.Product;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Serial", "Roll", "Applicant Name", "Fathers Name", "Registered", "Written Exam", "Computer", "Viva", "Updated By"};
    static String SHEET = "passed";

    public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }


    public static Workbook getWorkbookFromFile(File file, String fileType) {
        FileInputStream myInput = null;
        Workbook myWorkBook = null;
        try {
            myInput = new FileInputStream(file);
            if (fileType == "XLSX") {
                myWorkBook = new XSSFWorkbook(myInput);
            } else if (fileType == "XLS") {
                myWorkBook = new HSSFWorkbook(myInput);
            }
            return myWorkBook;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File can't be read");
        }

    }


    public static List<Product> excelToProduct(InputStream is, String fileType) {
        try {
            Workbook workbook = null;
            try {
                if (fileType.equalsIgnoreCase("XLSX")) {
                    workbook = new XSSFWorkbook(is);
                } else if (fileType.equalsIgnoreCase("XLS")) {
                    workbook = new HSSFWorkbook(is);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File can't be read");
            }

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<Product> products = new ArrayList<Product>();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            String newValue = ((int) currentCell.getNumericCellValue()) + "";
                            System.out.println(newValue);
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }
            }

            workbook.close();
            return products;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
