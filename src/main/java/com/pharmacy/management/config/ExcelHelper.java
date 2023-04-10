package com.pharmacy.management.config;

import com.pharmacy.management.dto.request.ProductRequestDTO;
import com.pharmacy.management.dto.request.ProductRequestExcelDTO;
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

    public static List<ProductRequestExcelDTO> excelToProduct(InputStream is, String fileType) {
        List<ProductRequestExcelDTO> productRequestDTOList = new ArrayList<>();
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
//            List<Product> products = new ArrayList<>();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                ProductRequestExcelDTO productRequestDTO = new ProductRequestExcelDTO();
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
                        //name
                        case 0:
                            String name = currentCell.getStringCellValue();
                            name = (name == null ? "" : name );
                            productRequestDTO.setName(name);
                            System.out.println("name : " + name);

                            break;
                        case 1:
                            String code = currentCell.getStringCellValue();
                            code = (code == null ? "" : code);
                            productRequestDTO.setCode(code);
                            System.out.println("code : " + code);
                            break;
                        case 2:
                            String description = currentCell.getStringCellValue();
                            description = (description == null ? "" : description);
                            productRequestDTO.setDescription(description);
                            System.out.println("description : " + description);
                            break;
                        case 3:
                            String strength = currentCell.getStringCellValue();
                            strength = (strength == null ? "" : strength);
                            productRequestDTO.setStrength(strength);
                            System.out.println("strength : " + strength);
                            break;
                        case 4:
                            String stsMedicine = currentCell.getStringCellValue();
                            stsMedicine = (stsMedicine == null ? "" : stsMedicine);
                            productRequestDTO.setStsMedicine(stsMedicine);
                            System.out.println("stsMedicine : " + stsMedicine);
                            break;
                        case 5:
                            String category = currentCell.getStringCellValue();
                            category = (category == null ? "" : category);
                            productRequestDTO.setCategoryName(category);
                            System.out.println("category : " + category);
                            break;
                        case 6:
                            String supplier = currentCell.getStringCellValue();
                            supplier = (supplier == null ? "" : supplier);
                            productRequestDTO.setSupplierName(supplier);

                            System.out.println("supplier : " + supplier);
                            break;
                        case 7:
                            Double unitPrice = currentCell.getNumericCellValue();
                            productRequestDTO.setUnitPrice(unitPrice);
                            System.out.println("unitPrice : " + unitPrice);
                            break;
                        case 8:
                            int limitCost = (int) currentCell.getNumericCellValue();
                            productRequestDTO.setLimitCost(limitCost);
                            System.out.println("limitCost : " + limitCost);
                            break;
                        case 9:
                            System.out.println("limitUnit : start ");
                            int limitUnit = (int)currentCell.getNumericCellValue();
                            productRequestDTO.setLimitUnit(limitUnit);
                            System.out.println("limitUnit : " + limitUnit);
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }

                productRequestDTOList.add(productRequestDTO);
            }

            workbook.close();
            return productRequestDTOList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }


    public static List<ProductRequestExcelDTO> excelToUser(InputStream is, String fileType) {
        List<ProductRequestExcelDTO> productRequestDTOList = new ArrayList<>();
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
//            List<Product> products = new ArrayList<>();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                ProductRequestExcelDTO productRequestDTO = new ProductRequestExcelDTO();
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
                        //name
                        case 0:
                            String name = currentCell.getStringCellValue();
                            name = (name == null ? "" : name );
                            productRequestDTO.setName(name);
                            System.out.println("name : " + name);

                            break;
                        case 1:
                            String code = currentCell.getStringCellValue();
                            code = (code == null ? "" : code);
                            productRequestDTO.setCode(code);
                            System.out.println("code : " + code);
                            break;
                        case 2:
                            String description = currentCell.getStringCellValue();
                            description = (description == null ? "" : description);
                            productRequestDTO.setDescription(description);
                            System.out.println("description : " + description);
                            break;
                        case 3:
                            String strength = currentCell.getStringCellValue();
                            strength = (strength == null ? "" : strength);
                            productRequestDTO.setStrength(strength);
                            System.out.println("strength : " + strength);
                            break;
                        case 4:
                            String stsMedicine = currentCell.getStringCellValue();
                            stsMedicine = (stsMedicine == null ? "" : stsMedicine);
                            productRequestDTO.setStsMedicine(stsMedicine);
                            System.out.println("stsMedicine : " + stsMedicine);
                            break;
                        case 5:
                            String category = currentCell.getStringCellValue();
                            category = (category == null ? "" : category);
                            productRequestDTO.setCategoryName(category);
                            System.out.println("category : " + category);
                            break;
                        case 6:
                            String supplier = currentCell.getStringCellValue();
                            supplier = (supplier == null ? "" : supplier);
                            productRequestDTO.setSupplierName(supplier);

                            System.out.println("supplier : " + supplier);
                            break;
                        case 7:
                            Double unitPrice = currentCell.getNumericCellValue();
                            productRequestDTO.setUnitPrice(unitPrice);
                            System.out.println("unitPrice : " + unitPrice);
                            break;
                        case 8:
                            int limitCost = (int) currentCell.getNumericCellValue();
                            productRequestDTO.setLimitCost(limitCost);
                            System.out.println("limitCost : " + limitCost);
                            break;
                        case 9:
                            System.out.println("limitUnit : start ");
                            int limitUnit = (int)currentCell.getNumericCellValue();
                            productRequestDTO.setLimitUnit(limitUnit);
                            System.out.println("limitUnit : " + limitUnit);
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }

                productRequestDTOList.add(productRequestDTO);
            }

            workbook.close();
            return productRequestDTOList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

}
