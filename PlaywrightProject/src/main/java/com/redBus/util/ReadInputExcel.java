package com.redBus.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadInputExcel {
	
	public static String[][] readExcelFile() throws IOException {
		String filePath ="C:\\Users\\Pradip\\eclipse-workspace\\Playwright\\PlaywrightProject\\src\\test\\resources\\TestData\\TestData.xlsx";
        FileInputStream fis = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        List<String[]> dataList = new ArrayList<>();
        for (Row row : sheet) {
            List<String> cellList = new ArrayList<>();
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case NUMERIC:
                        cellList.add(String.valueOf(cell.getNumericCellValue()));
                        break;
                    case STRING:
                        cellList.add(cell.getStringCellValue());
                        break;
                    case BOOLEAN:
                        cellList.add(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    case FORMULA:
                        cellList.add(cell.getCellFormula());
                        break;
                    case BLANK:
                        cellList.add("");
                        break;
                    default:
                        cellList.add("");
                }
            }
            String[] cellArray = new String[cellList.size()];
            cellArray = cellList.toArray(cellArray);
            dataList.add(cellArray);
        }
        workbook.close();
        fis.close();

        String[][] dataArray = new String[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = dataList.get(i);
        }

        return dataArray;
    }

	public static void main(String[] args) {
		//String excelFilePath ="C:\\Users\\Pradip\\eclipse-workspace\\Playwright\\PlaywrightProject\\src\\test\\resources\\TestData\\TestData.xlsx";
        try {
            String[][] data = readExcelFile();
            
            System.out.println(data[1][11]);
            // Print the read data
            for (String[] row : data) {
                for (String cell : row) {
                    System.out.print(cell + "\t");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
}


