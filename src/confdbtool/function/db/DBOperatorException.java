/**
 * @date 2014/5/15
 * @author ChenLong
 */

package confdbtool.function.db;

import confdbtool.function.excel.data.Field;
import java.util.Formatter;

/**
 *
 * @author ChenLong
 */
public class DBOperatorException extends RuntimeException
{
    private String filePath;
    private String sheetName;

    public DBOperatorException(String filePath, String sheetName)
    {
        this.filePath = filePath;
        this.sheetName = sheetName;
    }

    @Override
    public String toString()
    {
        Formatter fmt = new Formatter();
        fmt.format("fileName=[%s], sheetName=[%s]", getFilePath(), getSheetName());
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
}
