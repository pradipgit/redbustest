package com.redBus.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtils {
	private Workbook workbook;
    private Sheet sheet;

    public ExcelUtils(String filePath, String sheetName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fileInputStream);
        sheet = workbook.getSheet(sheetName);
        fileInputStream.close();
    }

    public String readCellData(int rowNumber, int columnNumber) {
        Row row = sheet.getRow(rowNumber);
        Cell cell = row.getCell(columnNumber);
        return cell.getStringCellValue();
    }

    public void writeCellData(int rowNumber, int columnNumber, String data) throws IOException {
        Row row = sheet.getRow(rowNumber);
        if (row == null) {
            row = sheet.createRow(rowNumber);
        }

        Cell cell = row.getCell(columnNumber);
        if (cell == null) {
            cell = row.createCell(columnNumber);
        }

        cell.setCellValue(data);

        // Write the output to the file
        FileOutputStream fileOutputStream = new FileOutputStream("path/to/your/excel/file.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
    }

    public void closeWorkbook() throws IOException {
        if (workbook != null) {
            workbook.close();
        }
    }
}
