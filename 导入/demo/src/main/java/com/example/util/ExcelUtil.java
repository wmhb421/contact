package com.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel读取,支持xls和xlsx
 */
public class ExcelUtil {

    /**
     * excel读取器
     */
    private static final ExcelUtil excelReader = new ExcelUtil();

    /**
     * 总行数
     */
    private int totalRows = 0;
    /**
     * 总列数
     */
    private int totalCells = 0;
    /**
     * 错误信息
     */
    private String errorInfo;

    /**
     * 构造方法
     */
    public ExcelUtil() {
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalCells() {
        return totalCells;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public boolean validateExcel(String filePath) {
        /** 检查文件名是否为空或者是否是Excel格式的文件 */
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
            errorInfo = "not excel," + filePath;
            return false;
        }
        /** 检查文件是否存在 */
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            errorInfo = "not exist," + filePath;
            return false;
        }
        return true;
    }

    private List<List<String>> read(String filePath) {
        List<List<String>> dataLst = new ArrayList<List<String>>();
        InputStream is = null;
        try {
            /** 验证文件是否合法 */
            if (!validateExcel(filePath)) {
                System.out.println(errorInfo);
                return null;
            }
            /** 判断文件的类型，是2003还是2007 */
            boolean isExcel2003 = true;
            if (isExcel2007(filePath)) {
                isExcel2003 = false;
            }
            /** 调用本类提供的根据流读取的方法 */
            File file = new File(filePath);
            is = new FileInputStream(file);
            dataLst = read(is, isExcel2003);
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                    e.printStackTrace();
                }
            }
        }
        /** 返回最后读取的结果 */
        return dataLst;
    }

    public List<List<String>> read(InputStream inputStream, boolean isExcel2003) {
        List<List<String>> dataLst = null;
        try {
            /** 根据版本选择创建Workbook的方式 */
            Workbook wb = null;
            if (isExcel2003) {
                wb = new HSSFWorkbook(inputStream);
            } else {
                wb = new XSSFWorkbook(inputStream);
            }
            dataLst = read(wb);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return dataLst;
    }

    private List<List<String>> read(Workbook wb) {
        List<List<String>> dataLst = new ArrayList<List<String>>();
        /** 得到第一个shell */
        Sheet sheet = wb.getSheetAt(0);
        /** 得到Excel的行数 */
        this.totalRows = sheet.getPhysicalNumberOfRows();
        /** 得到Excel的列数 */
        if (this.totalRows >= 1 && sheet.getRow(0) != null) {
            this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        /** 循环Excel的行 */
        for (int r = 0; r < this.totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            List<String> rowLst = new ArrayList<String>();
            /** 循环Excel的列 */
            for (int c = 0; c < this.getTotalCells(); c++) {
                Cell cell = row.getCell(c);
                String cellValue = "";
                if (null != cell) {
                    // 以下是判断数据的类型
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                            cellValue = cell.getNumericCellValue() + "";
                            break;
                        case HSSFCell.CELL_TYPE_STRING: // 字符串
                            cellValue = cell.getStringCellValue();
                            break;
                        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                            cellValue = cell.getBooleanCellValue() + "";
                            break;
                        case HSSFCell.CELL_TYPE_FORMULA: // 公式
                            cellValue = cell.getCellFormula() + "";
                            break;
                        case HSSFCell.CELL_TYPE_BLANK: // 空值
                            cellValue = "";
                            break;
                        case HSSFCell.CELL_TYPE_ERROR: // 故障
                            cellValue = "非法字符";
                            break;
                        default:
                            cellValue = "未知类型";
                            break;
                    }
                }
                rowLst.add(cellValue);
            }
            /** 保存第r行的第c列 */
            dataLst.add(rowLst);
        }
        return dataLst;
    }

    public static void main(String[] args) throws Exception {
        List<List<String>> list = ExcelUtil.readExcel("D:\\readExcel.xls");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                System.out.print("The" + (i) + "row");
                List<String> cellList = list.get(i);
                for (int j = 0; j < cellList.size(); j++) {
                    // System.out.print(" 第" + (j + 1) + "列值：");
                    System.out.print("    " + cellList.get(j));
                }
                System.out.println();
            }
        }
    }

    /**
     * 读取excel,首列如果内容前缀为##,则不会被读取
     */
    public static List<List<String>> readExcel(String filePath) {
        synchronized (excelReader) {
            List<List<String>> list = excelReader.read(filePath);
            if (null != list && list.size() > 0) {
                List<Integer> deleteList = new ArrayList<>();// 需要删除的行

                for (int i = 0; i < list.size(); i++) {
                    List<String> row = list.get(i);
                    if (null != row && row.size() > 0) {
                        String firstRow = row.get(0);
                        if (firstRow.startsWith("##")) {
                            deleteList.add(i);
                        }
                    }
                }

                for (int i = deleteList.size() - 1; i >= 0; i--) {
                    int index = deleteList.get(i);
                    list.remove(index);
                }
            }

            return list;
        }
    }

    /**
     * 读取excel,首列如果内容前缀为##,则不会被读取
     */
    public static List<String[]> readExcel2(String filePath) {
        List<List<String>> list = readExcel(filePath);
        List<String[]> resultList = new ArrayList<>(list.size());
        for (List<String> listTemp : list) {
            String[] strArr = new String[listTemp.size()];
            for (int i = 0; i < listTemp.size(); i++) {
                strArr[i] = listTemp.get(i);
            }
            resultList.add(strArr);
        }
        return resultList;
    }

    /**
     * 读取excel,首列如果内容前缀为##,则不会被读取
     */
    public static List<Map<String, String>> readExcel3(String filePath) {
        List<List<String>> list = readExcel(filePath);
        if (null == list || list.size() <= 1) {
            return null;
        }

        List<Map<String, String>> listMap = new ArrayList<>(list.size() - 1);
        List<String> title = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            Map<String, String> map = new HashMap<>();
            List<String> childList = list.get(i);
            for (int j = 0; j < title.size(); j++) {
                map.put(title.get(j), childList.get(j));
            }
            listMap.add(map);
        }
        return listMap;
    }

    /**
     * 是否是xls文件,95~2003的excel文件
     */
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 是否能是xlsx文件,excel2017以上的格式
     */
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

}
