/**
 * @date 2014/4/29
 */
package confdbtool.function.db;

import confdbtool.function.excel.ConfigExcelReader;
import confdbtool.function.excel.data.Datas;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

/**
 *
 */
public class DBOperator
{
    private final Logger log = Logger.getLogger(DBOperator.class);
    private Configuration cfg;
    private SqlSessionFactory sqlMapper;

    public DBOperator()
    {
        try
        {
            sqlMapper = DBFactory.GAME_DATA_DB.getSessionFactory();
            cfg = new Configuration();
            java.io.File path = new java.io.File(".//config//ftls//db//");
            cfg.setDirectoryForTemplateLoading(path);
            cfg.setObjectWrapper(new DefaultObjectWrapper());
        }
        catch (IOException e)
        {
            log.error("IOException", e);
        }
    }

    public void insertDataFromExcel(String excelPath)
    {
        ConfigExcelReader excelReader = new ConfigExcelReader();
        ArrayList<Datas> excelContent = excelReader.load(excelPath, true);
        try
        {
            Iterator<Datas> iter = excelContent.iterator();
            while (iter.hasNext())
            {
                Datas dataInfos = (Datas) iter.next();
                Template temp = cfg.getTemplate("Insert.ftl"); // 读取模板

                HashMap<String, Object> root = new HashMap<>();
                root.put("tabelName", dataInfos.getSheetName());
                root.put("fields", dataInfos.getFields());

                SqlSession session = sqlMapper.openSession();
                Connection con = session.getConnection();
                Statement stat = con.createStatement();

                StringBuilder sb = new StringBuilder();
                sb.append("TRUNCATE `").append(dataInfos.getSheetName()).append("`;");

                stat.addBatch("TRUNCATE `" + dataInfos.getSheetName() + "`");
                String[][] datas = dataInfos.getDatas();
                if (datas != null)
                {
                    for (String[] data : dataInfos.getDatas())
                    {
                        Writer out = new StringWriter();
                        root.put("datas", data);
                        temp.process(root, out);
                        stat.addBatch(out.toString());
                        sb.append(out.toString());
                        out.close();
                    }
                }
                else
                {
                    log.error("导入的数据不存在！");
                }
                log.info(sb.toString());
                try
                {
                    stat.executeBatch();
                }
                catch (BatchUpdateException ex)
                {
                    log.error("executeBatch exception", ex);
                    DBOperatorException dbex = new DBOperatorException(dataInfos.getFilename(), dataInfos.getSheetName());
                    dbex.addSuppressed(ex);
                    throw dbex;
                }
                con.commit();
                session.close();
            }
        }
        catch (IOException e)
        {
            log.error("IOException", e);
        }
        catch (TemplateException e)
        {
            log.error("TemplateException", e);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
    }

    public void createTableFromExcel(String excelPath)
    {
        ConfigExcelReader excelReader = new ConfigExcelReader();
        ArrayList<Datas> excelContent = excelReader.load(excelPath, true);
        try
        {
            Iterator<Datas> iter = excelContent.iterator();
            while (iter.hasNext())
            {
                Datas dataInfos = (Datas) iter.next();

                Template temp = cfg.getTemplate("Create.ftl"); //读取模板

                HashMap<String, Object> root = new HashMap<>();
                root.put("tabelName", dataInfos.getSheetName());
                root.put("keyName", dataInfos.getKey().getName());
                root.put("fields", dataInfos.getFields());

                Writer out = new StringWriter();
                temp.process(root, out);

                StringBuilder sb = new StringBuilder();
                sb.append("DROP TABLE IF EXISTS `").append(dataInfos.getSheetName()).append("`;\n");
                sb.append(out.toString());
                log.info("\n" + sb.toString());

                SqlSession session = sqlMapper.openSession();
                Connection con = session.getConnection();
                Statement stat = con.createStatement();
                stat.addBatch("DROP TABLE IF EXISTS `" + dataInfos.getSheetName() + "`;");
                stat.addBatch(out.toString());
                log.info(stat.toString());
                try
                {
                    stat.executeBatch();
                }
                catch (BatchUpdateException ex)
                {
                    log.error("executeBatch exception", ex);
                    DBOperatorException dbex = new DBOperatorException(dataInfos.getFilename(), dataInfos.getSheetName());
                    dbex.addSuppressed(ex);
                    throw dbex;
                }
                con.commit();
                session.close();
                out.close();

                log.info(dataInfos.getFilename() + " 【" + dataInfos.getSheetName() + "】数据表创建完成");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("UnsupportedEncodingException", e);
        }
        catch (IOException e)
        {
            log.error("IOException", e);
        }
        catch (TemplateException | SQLException e)
        {
            log.error("TemplateException | SQLException", e);
        }
    }

    public static void main(String[] args)
    {
        DBOperator dbOperator = new DBOperator();
        dbOperator.createTableFromExcel("./5 背包仓库开格配置数据库.xlsx");
        dbOperator.insertDataFromExcel("./5 背包仓库开格配置数据库.xlsx");
    }
}
