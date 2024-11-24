package com.example.util;

import com.example.entity.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {
    public static List<User> readUsers(InputStream inputStream) {
        List<User> users = new ArrayList<>();
        try (XSSFWorkbook wb = new XSSFWorkbook(inputStream)) {
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            // do not read the first row
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }
                User user = new User();
                user.setName(row.getCell(0).getStringCellValue());
                user.setPhone(row.getCell(1).getStringCellValue());
                user.setAddress(row.getCell(2).getStringCellValue());
                user.setEmail(row.getCell(3).getStringCellValue());
                user.setAccount(row.getCell(4).getStringCellValue());
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}