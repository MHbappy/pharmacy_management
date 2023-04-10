package com.pharmacy.management.config;

import com.pharmacy.management.dto.request.ProductRequestDTO;
import com.pharmacy.management.dto.request.ProductRequestExcelDTO;
import com.pharmacy.management.dto.request.UserDataExcelDTO;
import com.pharmacy.management.model.Product;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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


    public static List<UserDataExcelDTO> excelToUser(InputStream is, String fileType) {
        List<UserDataExcelDTO> userDataExcelDTOS = new ArrayList<>();
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
                UserDataExcelDTO productRequestDTO = new UserDataExcelDTO();
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
                            String email = currentCell.getStringCellValue();
                            if (email.isEmpty()){
                                continue;
                            }
                            email = (email == null ? "" : email );
                            productRequestDTO.setEmail(email);
                            System.out.println("email : " + email);

                            break;
                        case 1:
                            String password = "";
                            if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                password = (int) currentCell.getNumericCellValue() + "";
                            }else {
                                password = currentCell.getStringCellValue();
                            }
                            password = (password == null ? "" : password);
                            productRequestDTO.setPassword(password);
                            System.out.println("password : " + password);
                            break;
                        case 2:
                            String numId = "";
                            if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                numId = (int) currentCell.getNumericCellValue() + "";
                            }else {
                                numId = currentCell.getStringCellValue();
                            }
                            numId = (numId == null ? "" : numId);
                            productRequestDTO.setNumId(numId);
                            System.out.println("numId : " + numId);
                            break;
                        case 3:
                            String firstName = currentCell.getStringCellValue();
                            firstName = (firstName == null ? "" : firstName);
                            productRequestDTO.setFirstName(firstName);
                            System.out.println("firstName : " + firstName);
                            break;
                        case 4:
                            String lastName = currentCell.getStringCellValue();
                            lastName = (lastName == null ? "" : lastName);
                            productRequestDTO.setLastName(lastName);
                            System.out.println("lastName : " + lastName);
                            break;
                        case 5:
                            String state = "";
                            if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                state = (int) currentCell.getNumericCellValue() + "";
                            }else {
                                state = currentCell.getStringCellValue();
                            }
                            state = (state == null ? "" : state);
                            productRequestDTO.setState(state);
                            System.out.println("state : " + state);
                            break;
                        case 6:
                            String codeCity = "";
                            if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                codeCity = (int) currentCell.getNumericCellValue() + "";
                            }else {
                                codeCity = currentCell.getStringCellValue();
                            }
                            codeCity = (codeCity == null ? "" : codeCity);
                            productRequestDTO.setCodeCity(codeCity);
                            System.out.println("codeCity : " + codeCity);
                            break;
                        case 7:
                            String codeLocation = "";
                            if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                codeLocation = (int) currentCell.getNumericCellValue() + "";
                            }else {
                                codeLocation = currentCell.getStringCellValue();
                            }
                            productRequestDTO.setCodeLocation(codeLocation);
                            System.out.println("codeLocation : " + codeLocation);
                            break;
                        case 8:
                            String address = currentCell.getStringCellValue();
                            productRequestDTO.setAddress(address);
                            System.out.println("address : " + address);
                            break;
                        case 9:
                            String phone = "";
                            if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                phone = (int) currentCell.getNumericCellValue() + "";
                            }else {
                                phone = currentCell.getStringCellValue();
                            }
                            productRequestDTO.setHomePhone(phone);
                            System.out.println("phone : " + phone);
                            break;
                        case 10:
                            Date birthDate = currentCell.getDateCellValue();
                            LocalDate birthDateLocalDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(birthDate));
                            productRequestDTO.setBirthDate(birthDateLocalDate);
                            System.out.println("birthDate : " + birthDate);
                            break;
                        case 11:
                            Date hireDate = currentCell.getDateCellValue();
                            LocalDate hireDateLocalDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(hireDate));
                            productRequestDTO.setHireDate(hireDateLocalDate);
                            System.out.println("birthDate : " + hireDate);
                            break;
                        case 12:
                            String companyPolicy = currentCell.getStringCellValue();
                            productRequestDTO.setCompanyPolicyName(companyPolicy);
                            System.out.println("companyPolicy : " + companyPolicy);
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                userDataExcelDTOS.add(productRequestDTO);
            }

            workbook.close();
            return userDataExcelDTOS;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

}
