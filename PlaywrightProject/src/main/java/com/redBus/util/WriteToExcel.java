package com.redBus.util;

import java.io.IOException;

public class WriteToExcel {
	
	public static void WriteToExcelResult( int rowNumber,int columnNumber,String data) {
		String filePath = "path/to/your/excel/file.xlsx";
        String sheetName = "Sheet1";

        try {
            ExcelUtils excelUtils = new ExcelUtils(filePath, sheetName);

            excelUtils.writeCellData(rowNumber, columnNumber, data);

            // Optionally read the data back to verify
            String readData = excelUtils.readCellData(rowNumber, columnNumber);
            System.out.println("Data in cell (" + rowNumber + "," + columnNumber + "): " + readData);

            excelUtils.closeWorkbook();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	}


