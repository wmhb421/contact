package com.example.controller;

import com.example.common.Result;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.dto.UserDto;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // add
    @PostMapping
    public Result add(@RequestBody User user) {
        userService.save(user);
        return Result.success();
    }
    @PutMapping
    public Result update(@RequestBody User user) {
        userService.save(user);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        return Result.success(userService.findById(id));
    }
    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.findAll());
    }
    @GetMapping("/page")
    public Result<Page<User>> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String name) {
        return Result.success(userService.findPage(pageNum, pageSize, name));
    }
    @PostMapping("/contacts/{contactId}/bookmark")
    public ResponseEntity<?> bookmarkContact(@PathVariable Long contactId, @RequestBody BookmarkRequest request) {
        userService.bookmarkContact(contactId, request.isBookmark());
        return ResponseEntity.ok().build();
    }
    public static class BookmarkRequest {
        private boolean bookmark;

        // getters and setters
        public boolean isBookmark() {
            return bookmark;
        }

        public void setBookmark(boolean bookmark) {
            this.bookmark = bookmark;
        }
    }

    @RequestMapping(value = "UserExcelDownloads", method = RequestMethod.GET)
    public void downloadAllClassmate(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();//创建HSSFWorkbook对象,  excel的文档对象
        HSSFSheet sheet = workbook.createSheet("信息表"); //excel的表单

        List<User> classmateList = userService.userinfor();

        String fileName = "userinf" + ".xls";//设置要导出的文件的名字
        //新增数据行，并且设置单元格数据
        int rowNum = 1;
        String[] headers = {"name", "phone", "address", "email", "account"};
        //headers表示excel表中第一行的表头
        HSSFRow row = sheet.createRow(0);
        //在excel表中添加表头
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        //在表中存放查询到的数据放入对应的列
        for (User user : classmateList) {
            HSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(user.getName());
            row1.createCell(1).setCellValue(user.getPhone());
            row1.createCell(2).setCellValue(user.getAddress());
            row1.createCell(3).setCellValue(user.getEmail());
            row1.createCell(4).setCellValue(user.getAccount());
            rowNum++;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }
}