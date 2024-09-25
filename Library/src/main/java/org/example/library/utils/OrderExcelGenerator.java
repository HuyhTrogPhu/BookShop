package org.example.library.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.library.model.Order;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class OrderExcelGenerator {

    private List<Order> orderList;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private CellStyle dateCellStyle;

    public OrderExcelGenerator(List<Order> orderList) {
        this.orderList = orderList;
        workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        dateCellStyle = workbook.createCellStyle();
        short dateFormat = createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss");
        dateCellStyle.setDataFormat(dateFormat);
    }

    private void writeHeader(){
        sheet = workbook.createSheet("Order");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Customer name", style);
        createCell(row, 1, "Email", style);
        createCell(row, 2, "Order date", style);
        createCell(row, 3, "Total items", style);
        createCell(row, 4, "Total price", style);
        createCell(row, 5, "Tax", style);
        createCell(row, 6, "Grand total", style);
        createCell(row, 7, "Status", style );
        createCell(row, 8, "Delivery Date", style);


    }

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle cellStyle) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);

        if (valueOfCell == null) {
            cell.setCellValue("");
        } else if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof Double) {
            cell.setCellValue((Double) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else if (valueOfCell instanceof Date) {
            cell.setCellValue((Date) valueOfCell);
        } else {
            System.err.println("Unsupported cell value type: " + valueOfCell.getClass().getSimpleName());
        }
        cell.setCellStyle(cellStyle);
    }



    private void write(){
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for(Order record : orderList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, record.getCustomer().getFirstName() + record.getCustomer().getLastName(), style);
            createCell(row, columnCount++, record.getCustomer().getUsername(), style);
            createCell(row, columnCount++, record.getOrderDate(), style);
            createCell(row, columnCount++, record.getQuantity(), style);
            createCell(row, columnCount++, record.getTotalPrice() + "$", style);
            createCell(row, columnCount++, record.getTax() + "%", style);
            createCell(row, columnCount++, record.getTotalPrice() * 1.02 + "$", style);
            createCell(row, columnCount++, record.isAccept() ? "Paid" : "UnPaid", style);
            createCell(row, columnCount++, record.getDeliveryDate(), style);
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
