/**
 * @date 2014/4/29
 */
package confdbtool.function.excel;

import confdbtool.function.excel.data.Field;
import confdbtool.function.excel.data.Datas;
import confdbtool.function.excel.exception.CannotCastNumberException;
import confdbtool.function.excel.exception.ExplainEmptyException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 */
public class ConfigExcelReader
{
    private final Logger log = Logger.getLogger(ConfigExcelReader.class);

    public ArrayList<Datas> loadname(String fileName)
    {
        ArrayList<Datas> result = new ArrayList<>();
        try
        {
            FileInputStream in = new FileInputStream(fileName);
            XSSFWorkbook wb = new XSSFWorkbook(in);
            int sheetNumber = wb.getNumberOfSheets();
            for (int i = 0; i < sheetNumber; i++)
            {
                XSSFSheet sheet = wb.getSheetAt(i); //获得工作表
                
                if (sheet.getSheetName() == null || "changelog".equals(sheet.getSheetName()) || sheet.getSheetName().indexOf("说明") != -1) //更改日志略过
                {
                    continue;
                }
                else //数据工作表
                {
                    String name = sheet.getSheetName();
                    Datas datas = new Datas();
                    datas.setFilename(fileName);
                    datas.setSheetName(name);
                    result.add(datas);
                }
            }
        }
        catch (IOException e)
        {
            log.error("read error", e);
        }
        return result;
    }

    /**
     * 获得excel数据
     *
     * @param fileName
     * @param dataLoad
     * @return
     */
    public ArrayList<Datas> load(String fileName, boolean dataLoad) throws CannotCastNumberException
    {
        log.info("load [" + fileName + "] excel file");
        ArrayList<Datas> result = new ArrayList<>();
        int currentRow = 0;
        int currentCell = 0;
        try
        {
            FileInputStream in = new FileInputStream(fileName);
            XSSFWorkbook wb = new XSSFWorkbook(in);
            XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(wb);
            int sheetNumber = wb.getNumberOfSheets(); //获得工作表数量
            for (int i = 0; i < sheetNumber; ++i)
            {
                XSSFSheet sheet = wb.getSheetAt(i); //获得工作表
                if (sheet.getSheetName() == null 
                        || "changelog".equals(sheet.getSheetName()) 
                        || sheet.getSheetName().contains("说明")) //更改日志略过
                {
                    continue;
                }
                else if (sheet.getSheetName().startsWith("Sheet"))
                {
                    log.warn("Excel配置表: " + "[" + fileName + "], 工作表名: [" + sheet.getSheetName() + "]无效！");
                    continue;
                }
                else //数据工作表
                {
                    String sheetName = sheet.getSheetName();

                    Datas datas = new Datas();
                    datas.setFilename(fileName);
                    datas.setSheetName(sheetName);

                    String[][] fields = new String[5][];

                    XSSFRow row = sheet.getRow(4);
                    int cellNumber = row.getPhysicalNumberOfCells();

                    //读取表字段信息
                    for (int j = 0; j < 5; j++)
                    {
                        currentRow = j;
                        row = sheet.getRow(j);
                        if (row == null)
                            continue;
                        fields[j] = new String[cellNumber];
                        for (int k = 0; k < cellNumber; ++k)
                        {
                            currentCell = k;
                            XSSFCell cell = row.getCell(k);
                            if (cell == null)
                                continue;
                            fields[j][k] = cell.toString();
                        }
                    }

                    for (int j = 0; j < fields[0].length; j++)
                    {
                        //不读取
                        if (fields[1][j] == null || "".equals(fields[1][j].trim()))
                            continue;

                        //字段信息
                        Field field = new Field();
                        field.setName(fields[1][j].trim());
                        if (fields[2] == null || fields[2][j] == null || "".equals(fields[2][j].trim()))
                        {
                            field.setClassName("int");
                            field.setJavaClassName("int");
                            field.setDbClassName("INTEGER");
                        }
                        else
                        {
                            field.setClassName(fields[2][j].trim());
                            if (fields[2][j].trim().contains("varchar"))
                            {
                                field.setJavaClassName("String");
                                field.setDbClassName("VARCHAR");
                            }
                            else if (fields[2][j].trim().contains("bigint"))
                            {
                                field.setJavaClassName("long");
                                field.setDbClassName("BIGINT");
                            }
                            else if (fields[2][j].trim().contains("smallint"))
                            {
                                field.setJavaClassName("short");
                                field.setDbClassName("SMALLINT");
                            }
                            else if (fields[2][j].trim().contains("tinyint"))
                            {
                                field.setJavaClassName("byte");
                                field.setDbClassName("TINYINT");
                            }
                            else if (fields[2][j].trim().contains("int"))
                            {
                                field.setJavaClassName("int");
                                field.setDbClassName("INTEGER");
                            }
                            else if (fields[2][j].trim().contains("blob"))
                            {
                                field.setJavaClassName("byte[]");
                                field.setDbClassName("LONGVARBINARY");
                            }
                            else if (fields[2][j].trim().contains("text"))
                            {
                                field.setJavaClassName("String");
                                field.setDbClassName("LONGVARCHAR");
                            }
                            else if (fields[2][j].trim().contains("float"))
                            {
                                field.setJavaClassName("float");
                                field.setDbClassName("FLOAT");
                            }
                        }
                        if (fields[4][j] == null || fields[4][j].isEmpty() || fields[4][j].equalsIgnoreCase(""))
                        {
                            throw new ExplainEmptyException(fileName, sheetName, field);
                        }
                        field.setExplain(fields[4][j]);
                        field.setCell(j);

                        if (fields[0].length > j && fields[0][j] != null && !"".equals(fields[0][j].trim()))
                        {
                            if ((int) Double.parseDouble(fields[0][j]) == 1)
                            {
                                datas.getKeys().add(field);
                                datas.setKey(field);
                            }
                        }

                        datas.getFields().add(field);
                    }

                    //设置主键 
                    if (datas.getKeys().size() > 1)
                    {
                        //字段信息
                        Field field = new Field();

                        StringBuffer buf = new StringBuffer();
                        for (int j = 0; j < datas.getKeys().size(); ++j)
                        {
                            buf.append("_").append(fields[1][datas.getKeys().get(j).getCell()]);
                        }
                        if (buf.length() > 0)
                            buf.deleteCharAt(0);
                        field.setName(buf.toString());
                        field.setClassName("varchar(255)");
                        field.setDbClassName("VARCHAR");
                        field.setJavaClassName("String");
                        buf = new StringBuffer();
                        for (int j = 0; j < datas.getKeys().size(); ++j)
                        {
                            buf.append("+").append(fields[4][datas.getKeys().get(j).getCell()]);
                        }
                        if (buf.length() > 0)
                            buf.deleteCharAt(0);
                        field.setExplain(buf.toString());
                        field.setCell(-1);
                        datas.getFields().add(0, field);

                        datas.setKey(field);
                    }
                    int rows = sheet.getPhysicalNumberOfRows() + 1;
                    if (dataLoad &&rows > 5)
                    {
                        String[][] data = new String[rows - 5][datas.getFields().size()];
                        for (int j = 5; j < rows; j++)
                        {
                            currentRow = j;
                            row = sheet.getRow(j);
                            if (row == null)
                                continue;
                            for (int k = 0; k < datas.getFields().size(); k++)
                            {
                                currentCell = k;
                                Field field = datas.getFields().get(k);
                                if (field.getCell() == -1)
                                {
                                    StringBuilder buf = new StringBuilder();
                                    for (int l = 0; l < datas.getKeys().size(); l++)
                                    {
                                        XSSFCell cell = row.getCell(datas.getKeys().get(l).getCell());
                                        if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA)
                                        {
                                            buf.append("_").append(String.valueOf((int) evaluator.evaluate(cell).getNumberValue()));
                                        }
                                        else
                                        {
                                            if ("int".equals(datas.getKeys().get(l).getClassName()) || "bigint".equals(datas.getKeys().get(l).getClassName()) || "smallint".equals(datas.getKeys().get(l).getClassName()) || "tinyint".equals(datas.getKeys().get(l).getClassName()))
                                            {
                                                buf.append("_").append(String.valueOf((long) cell.getNumericCellValue()));
                                            }
                                            else
                                            {
                                                buf.append("_").append(cell.toString().trim());
                                            }
                                        }
                                    }
                                    if (buf.length() > 0)
                                        buf.deleteCharAt(0);
                                    data[j - 5][k] = buf.toString();
                                }
                                else
                                {
                                    XSSFCell cell = row.getCell(field.getCell());
                                    if (cell == null || ("").equals(cell.toString().trim()))
                                    {
                                        if ("int".equals(field.getClassName()) || "bigint".equals(field.getClassName()) || "smallint".equals(field.getClassName()) || "tinyint".equals(field.getClassName()))
                                            data[j - 5][k] = "0";
                                        else
                                            data[j - 5][k] = "";
                                    }
                                    else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA)
                                    {
                                        data[j - 5][k] = String.valueOf((int) evaluator.evaluate(cell).getNumberValue());
                                    }
                                    else
                                    {
                                        try 
                                        {
                                            if ("int".equals(field.getClassName()) || "bigint".equals(field.getClassName()) || "smallint".equals(field.getClassName()) || "tinyint".equals(field.getClassName()))
                                            {
                                                data[j - 5][k] = String.valueOf((long) Math.round(cell.getNumericCellValue()));	//四舍五入
                                            }
                                            else if("float".equals(field.getClassName()))
                                            {
                                                 data[j - 5][k] = String.valueOf((float)cell.getNumericCellValue());
                                            }
                                            else
                                            {
                                                /*
                                                if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                                                    data[j - 5][k] = String.valueOf((long) cell.getNumericCellValue());
                                                else
                                                        */
                                                  data[j - 5][k] = cell.toString().trim();
                                            }
                                        }
                                        catch (IllegalStateException ex)
                                        {
                                            if (ex.getMessage().contains("Cannot get a numeric value from a text cell"))
                                            {
                                                CannotCastNumberException castEx = new CannotCastNumberException(fileName, sheetName, field);
                                                castEx.addSuppressed(ex);
                                                throw castEx;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        datas.setDatas(data);
                    }
                    result.add(datas);
                }
            }

            in.close();
        }
        catch (FileNotFoundException e)
        {
            log.error("FileNotFoundException", e);
        }
        catch (IOException e)
        {
            log.error("IOException", e);
        }
        catch (NumberFormatException e)
        {
            log.error(currentRow + "," + currentCell, e);
        }
        return result;
    }
    
    public static void main(String[] args)
    {
        ConfigExcelReader reader = new ConfigExcelReader();
        try
        {
            reader.load("./test_excel/物品表.xlsx", true);
        }
        catch (ExplainEmptyException ex)
        {
            System.out.printf("解释字段为空, fileName=[%s], sheetName=[%s], %s", ex.getFilePath(), ex.getSheetName(), ex.getField().toString());
        }
        catch (CannotCastNumberException ex)
        {
            System.out.printf("无法转换成数值异常, fileName=[%s], sheetName=[%s], %s", ex.getFilePath(), ex.getSheetName(), ex.getField().toString());
        }
    }
}
