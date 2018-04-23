/**
 * @date 2014/5/14
 * @author ChenLong
 */
package confdbtool.function.excel.exception;

import confdbtool.function.excel.data.Field;
import java.util.Formatter;

/**
 *
 * @author ChenLong
 */
public class IllegalityExcelFormatException extends RuntimeException
{
    private String filePath;
    private String sheetName;
    private Field field;

    public IllegalityExcelFormatException(String filePath, String sheetName, Field field)
    {
        this.filePath = filePath;
        this.sheetName = sheetName;
        this.field = field;
    }

    @Override
    public String toString()
    {
        Formatter fmt = new Formatter();
        fmt.format("fileName=[%s], sheetName=[%s], %s", getFilePath(), getSheetName(), getField().toString());
        return fmt.toString();
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getSheetName()
    {
        return sheetName;
    }

    public void setSheetName(String sheetName)
    {
        this.sheetName = sheetName;
    }

    public Field getField()
    {
        return field;
    }

    public void setField(Field field)
    {
        this.field = field;
    }
}
