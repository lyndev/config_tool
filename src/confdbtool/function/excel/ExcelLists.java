/**
 * @date 2014/3/31 16:10
 * @author ChenLong
 */
package confdbtool.function.excel;

import confdbtool.function.OptionConfigure;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ChenLong
 */
public class ExcelLists
{
    public static List<String> getFileList()
    {
        String excelPath = OptionConfigure.getInstance().getExcelPath();
        if (excelPath == null || excelPath.isEmpty())
            return null;
        File dir = new File(excelPath);
        String[] children = dir.list();
        List<String> list = new ArrayList<>();
        if (children == null)
        {
            // 不存在或者不是目录
        }
        else
        {
            for (String children1 : children)
            {
                if (children1.startsWith("~")) // Excel打开时的临时文件
                    continue;
                // 文件名
                Pattern pattern = Pattern.compile("(.+).xlsx");
                Matcher matcher = pattern.matcher(children1);
                if (matcher.find())
                {
                    list.add(matcher.group(0));
                }
            }
        }
        return list;
    }
}
