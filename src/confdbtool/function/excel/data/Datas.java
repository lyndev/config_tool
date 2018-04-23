/**
 * @date 2014/4/29
 */
package confdbtool.function.excel.data;

import java.util.ArrayList;

/**
 *
 */
public class Datas
{
    //文件名
    private String filename;
    //sheet名
    private String sheetName;
    //主键名称
    private Field key;
    //主键组合列
    private ArrayList<Field> keys = new ArrayList<Field>();
    //字段信息表
    private ArrayList<Field> fields = new ArrayList<Field>();

    private String[][] datas;

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public ArrayList<Field> getFields()
    {
        return fields;
    }

    public void setFields(ArrayList<Field> fields)
    {
        this.fields = fields;
    }

    public String getSheetName()
    {
        return sheetName;
    }

    public void setSheetName(String sheetName)
    {
        this.sheetName = sheetName;
    }

    public String[][] getDatas()
    {
        return datas;
    }

    public void setDatas(String[][] datas)
    {
        this.datas = datas;
    }

    public ArrayList<Field> getKeys()
    {
        return keys;
    }

    public void setKeys(ArrayList<Field> keys)
    {
        this.keys = keys;
    }

    public Field getKey()
    {
        return key;
    }

    public void setKey(Field key)
    {
        this.key = key;
    }
}
