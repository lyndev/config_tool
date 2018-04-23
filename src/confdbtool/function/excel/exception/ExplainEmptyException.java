/**
 * @date 2014/5/15
 * @author ChenLong
 */

package confdbtool.function.excel.exception;

import confdbtool.function.excel.data.Field;
import java.util.Formatter;

/**
 * excel配置文件第5行(解释字段)未填异常
 * @author ChenLong
 */
public class ExplainEmptyException extends IllegalityExcelFormatException
{
    public ExplainEmptyException(String filePath, String sheetName, Field field)
    {
        super(filePath, sheetName, field);
    }
    
    @Override
    public String toString()
    {
        Formatter fmt = new Formatter();
        fmt.format("第5行解释字段有为空的列, 文件: [%s], 工作表: [%s], 字段名: [%s]", getFilePath(), getSheetName(), getField().getName());
        return fmt.toString();
    }
}
