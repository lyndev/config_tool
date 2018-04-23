/**
 * @date 2014/5/15
 * @author ChenLong
 */

package confdbtool.function.excel.exception;

import confdbtool.function.excel.data.Field;

/**
 * 无法转化为数值异常
 * @author ChenLong
 */
public class CannotCastNumberException extends IllegalityExcelFormatException
{
    public CannotCastNumberException(String filePath, String sheetName, Field field)
    {
        super(filePath, sheetName, field);
    }
}
