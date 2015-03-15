package com.tsoft.dbutils.dwt;

import java.io.*;
import org.apache.poi.hssf.util.*;
import org.apache.poi.hssf.usermodel.*;
import com.tsoft.dbutils.lib.*;
import com.tsoft.dbutils.lib.metadb.*;

public class DWT {
    public void execute(String fileName) {
        MDDatabase database = new MDDatabase();
        database.open(fileName);

        try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("DWT");

            HSSFRow row;
            HSSFCell cell;

            HSSFCellStyle headerStyle = wb.createCellStyle();
            headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            headerStyle.setBottomBorderColor(HSSFColor.BLACK.index);
            headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            headerStyle.setLeftBorderColor(HSSFColor.BLACK.index);
            headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            headerStyle.setRightBorderColor(HSSFColor.BLACK.index);
            headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            headerStyle.setTopBorderColor(HSSFColor.BLACK.index);
            headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            HSSFCellStyle objectStyle = wb.createCellStyle();
            HSSFFont objectFont = wb.createFont();
            objectFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            objectStyle.setFont(objectFont);

            HSSFCellStyle fieldStyle = wb.createCellStyle();
            fieldStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            fieldStyle.setBottomBorderColor(HSSFColor.BLACK.index);
            fieldStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            fieldStyle.setLeftBorderColor(HSSFColor.BLACK.index);
            fieldStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            fieldStyle.setRightBorderColor(HSSFColor.BLACK.index);
            fieldStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            fieldStyle.setTopBorderColor(HSSFColor.BLACK.index);
            headerStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);
            fieldStyle.setWrapText(true);

            short line = 1;
            row = sheet.createRow(line);
            cell = row.createCell((short) 0);
            cell.setCellValue(new HSSFRichTextString("test"));
            line++;
            for (int i = 0; i < database.getDataSetList().size(); i++) {
                MDDataSet dataSet = (MDDataSet) database.getDataSetList().get(i);
                if (dataSet.getType() == MDDataSet.TABLE) {
                    row = sheet.createRow(line);
                    cell = row.createCell((short) 0);
                    cell.setCellValue(new HSSFRichTextString(dataSet.getName() + " " + (dataSet.getComment() == null ? "" : dataSet.getComment())));
                    cell.setCellStyle(objectStyle);
                    line++;
                    row = sheet.createRow(line);
                    cell = row.createCell((short) 0);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 1);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 2);
                    cell.setCellValue(new HSSFRichTextString("Null"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 3);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 4);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 5);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    line++;

                    for (int j = 0; j < dataSet.getFieldList().size(); j++) {
                        MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(j);
                        row = sheet.createRow(line);
                        cell = row.createCell((short) 0);
                        cell.setCellValue(new HSSFRichTextString(field.getName()));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 1);
                        cell.setCellValue(new HSSFRichTextString(field.getDataType()));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 2);
                        cell.setCellValue(new HSSFRichTextString(field.getNullable() ? "yes" : ""));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 3);
                        cell.setCellValue(new HSSFRichTextString(field.getPrimary() ? "yes" : ""));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 4);
                        String foreignConstraint = "";
                        if (field.getRefTableName() != null) {
                            foreignConstraint += field.getRefTableName() + '.' + field.getRefColumnName();
                        }
                        cell.setCellValue(new HSSFRichTextString(foreignConstraint));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 5);
                        cell.setCellValue(new HSSFRichTextString(field.getComment()));
                        cell.setCellStyle(fieldStyle);
                        line++;
                    }
                    line++;
                }
            }
            line++;

            // �������������
            row = sheet.createRow(line);
            cell = row.createCell((short) 0);
            cell.setCellValue(new HSSFRichTextString("test"));
            line++;
            for (int i = 0; i < database.getDataSetList().size(); i++) {
                MDDataSet dataSet = (MDDataSet) database.getDataSetList().get(i);
                if (dataSet.getType() == MDDataSet.VIEW) {
                    row = sheet.createRow(line);
                    cell = row.createCell((short) 0);
                    cell.setCellValue(new HSSFRichTextString(dataSet.getName() + " " + (dataSet.getComment() == null ? "" : dataSet.getComment())));
                    cell.setCellStyle(objectStyle);
                    line++;
                    row = sheet.createRow(line);
                    cell = row.createCell((short) 0);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 1);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 2);
                    cell.setCellValue(new HSSFRichTextString("Null"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 3);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 4);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    cell = row.createCell((short) 5);
                    cell.setCellValue(new HSSFRichTextString("test"));
                    cell.setCellStyle(headerStyle);
                    line++;

                    for (int j = 0; j < dataSet.getFieldList().size(); j++) {
                        MDDataSetField field = (MDDataSetField) dataSet.getFieldList().get(j);
                        row = sheet.createRow(line);
                        cell = row.createCell((short) 0);
                        cell.setCellValue(new HSSFRichTextString(field.getName()));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 1);
                        cell.setCellValue(new HSSFRichTextString(field.getDataType()));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 2);
                        cell.setCellValue(new HSSFRichTextString(field.getNullable() ? "yes" : ""));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 3);
                        cell.setCellValue(new HSSFRichTextString(field.getPrimary() ? "yes" : ""));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 4);
                        String foreignConstraint = "";
                        if (field.getRefTableName() != null) {
                            foreignConstraint += field.getRefTableName() + '.' + field.getRefColumnName();
                        }
                        cell.setCellValue(new HSSFRichTextString(foreignConstraint));
                        cell.setCellStyle(fieldStyle);
                        cell = row.createCell((short) 5);
                        cell.setCellValue(new HSSFRichTextString(field.getComment()));
                        cell.setCellStyle(fieldStyle);
                        line++;
                    }
                    line++;
                }
            }
            line++;

            row = sheet.createRow(line);
            cell = row.createCell((short) 0);
            cell.setCellValue(new HSSFRichTextString("test"));
            line++;
            for (int i = 0; i < database.getFunctionList().size(); i++) {
                MDFunction function = (MDFunction) database.getFunctionList().get(i);
                row = sheet.createRow(line);
                cell = row.createCell((short) 0);
                cell.setCellValue(new HSSFRichTextString(function.getName()));
                cell.setCellStyle(objectStyle);
                line++;
                if (function.isCommented()) {
                    for (int j = 0; j < function.getCommentLineList().size(); j++) {
                        row = sheet.createRow(line);
                        cell = row.createCell((short) 0);
                        cell.setCellValue(new HSSFRichTextString(function.getCommentLineList().get(j)));
                        line++;
                    }
                }
                line++;
            }

            sheet.setColumnWidth((short) 0, toWidth(160));
            sheet.setColumnWidth((short) 1, toWidth(110));
            sheet.setColumnWidth((short) 2, toWidth(30));
            sheet.setColumnWidth((short) 3, toWidth(30));
            sheet.setColumnWidth((short) 4, toWidth(200));
            sheet.setColumnWidth((short) 5, toWidth(200));

            FileOutputStream fileOut = new FileOutputStream(fileName.substring(0, fileName.length() - 3) + "xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private short toWidth(int pixels) {
        return (short) ((pixels * 8) / ((double) 1 / 4.5));
    }

    public static void main(String[] args) {
        String fileName = "object.xml";
        if (args.length == 1) {
            fileName = args[0];
        }

        File file = new File(fileName);
        if (args.length > 1 || !file.exists()) {
            System.exit(0);
        }

        System.out.println("Started at: " + StringUtils.getNow());

        DWT dwt = new DWT();
        dwt.execute(fileName);

        System.out.println("Stopped at: " + StringUtils.getNow());
    }
}
