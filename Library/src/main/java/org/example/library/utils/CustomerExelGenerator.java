package org.example.library.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.library.model.Customer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CustomerExelGenerator {

    private List<Object[]> customersList;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public CustomerExelGenerator(List<Object[]> customersList) {
        this.customersList = customersList;
        workbook = new XSSFWorkbook();
    }

    private void writeHeader() {
        sheet = workbook.createSheet("Customer");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "First name", style);
        createCell(row, 1, "Last name", style);
        createCell(row, 2, "Email", style);
        createCell(row, 3, "Phone number", style);
        createCell(row, 4, "Country", style);
        createCell(row, 5, "City", style);
        createCell(row, 6, "Address", style);
        createCell(row, 7, "Total Order", style);
        createCell(row, 8, "Total Price", style);
    }

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle cellStyle) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof Double) {
            cell.setCellValue((Double) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(cellStyle);
    }

    private void write() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Object[] record : customersList) {
            Customer customer = (Customer) record[0];
            Long totalOrder = (Long) record[1];
            Double totalPrice = (Double) record[2];

            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, customer.getFirstName(), style);
            createCell(row, columnCount++, customer.getLastName(), style);
            createCell(row, columnCount++, customer.getUsername(), style);
            createCell(row, columnCount++, customer.getPhoneNumber(), style);
            createCell(row, columnCount++, customer.getCity().getCountry().getName(), style);
            if (customer.getCity() != null) {
                createCell(row, columnCount++, customer.getCity().getName(), style);
            } else {
                createCell(row, columnCount++, "", style);
            }
            createCell(row, columnCount++, customer.getAddress(), style);
            createCell(row, columnCount++, totalOrder, style);
            createCell(row, columnCount++, totalPrice, style);
        }
    }

    public void generateExcelFile(HttpServletResponse response) throws IOException {
        writeHeader();
        write();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }
}
