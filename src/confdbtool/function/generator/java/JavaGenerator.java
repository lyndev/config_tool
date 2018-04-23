package confdbtool.function.generator.java;

import confdbtool.function.IPublish;
import confdbtool.function.excel.ConfigExcelReader;
import confdbtool.function.excel.data.Datas;
import confdbtool.function.excel.data.Field;
import confdbtool.util.TextFile;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * 通过模版生成Bean/Dao/Container/SqlMap/GameDataManager等Java代码
 *
 * @author ChenLong
 */
public class JavaGenerator
{
    private final Logger log = Logger.getLogger(JavaGenerator.class);
    private final static String DEFAULT_FTL_DIR          = ".//config//ftls//java//"; // 默认ftl文件目录
    private final static String DEFAULT_BEAN_SUBDIR      = "bean";
    private final static String DEFAULT_DAO_SUBDIR       = "dao";
    private final static String DEFAULT_CONTAINER_SUBDIR = "container";
    private final static String DEFAULT_SQLMAP_SUBDIR    = "sqlmap";
    
    private String basePackage; // 最上层包名, 例如com.game, 将生成com.game.bean, com.game.dao, com.game.container, com.game.sqlmap包
    private String javaOutDir;  // 生成的java代码放到哪个目录下
    private String luaOutDir;   // 生成的lua代码放到哪个目录下
    private String jsOutDir;    // 生成的lua代码放到哪个目录下
    private String csharpOutDir;// 生成的c#代码放到哪个目录下
    private Configuration cfg;

    public JavaGenerator(String basePackage, String javaOutDir, String luaOutDir, String jsOutdir, String csharpOutDir)
    {
        this(basePackage, javaOutDir, luaOutDir, jsOutdir, csharpOutDir, DEFAULT_FTL_DIR);
    }

    public JavaGenerator(String basePackage, String javaOutDir, String luaOutDir, String jsOutDir, String csharpOutDir, String ftlDir)
    {
        this.basePackage  = basePackage;
        this.javaOutDir   = javaOutDir;
        this.luaOutDir    = luaOutDir;
        this.jsOutDir     = jsOutDir;
        this.csharpOutDir = csharpOutDir;
        try
        {
            cfg = new Configuration();
            java.io.File path = new java.io.File(ftlDir);
            cfg.setDirectoryForTemplateLoading(path);
            cfg.setObjectWrapper(new DefaultObjectWrapper());
        }
        catch (IOException e)
        {
            log.error("IOException", e);
        }
    }

    /**
     * 生成的结果类
     */
    public static class GenerateResult
    {
        private String name;
        private String content;

        public GenerateResult(String className, String classContent)
        {
            this.name = className;
            this.content = classContent;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }
    }

    /**
     * 生成代码
     *
     * @param excelPath excel文件路径
     * @throws IOException
     */
    public void generateCode(String excelPath) throws IOException
    {
        mkdirBaseDir();

        List<String> containers = new ArrayList<>();
        List<String> sqlmaps = new ArrayList<>();

        ConfigExcelReader excelReader = new ConfigExcelReader();
        ArrayList<Datas> excelContent = excelReader.load(excelPath, true);

        GenerateResult luaResult       = generateLuaFromExcel(excelContent);
        GenerateResult jsResult        = generateJSFromExcel(excelContent);
        GenerateResult csharpResult    = generateCsharpFromExcel(excelContent);
        GenerateResult beanResult      = generateBeanFromExcel(excelContent);
        GenerateResult daoResult       = generateDaoFromExcel(excelContent);
        GenerateResult containerResult = generateContainerFromExcel(excelContent);
        GenerateResult sqlmapResult    = generateSqlMapFromExcel(excelContent);

        containers.add(containerResult.getName());
        sqlmaps.add(sqlmapResult.getName());
        writeFile(generateOutFilePath(DEFAULT_BEAN_SUBDIR, beanResult.name, ".java"), beanResult.getContent());
        writeFile(generateOutFilePath(DEFAULT_DAO_SUBDIR, daoResult.name, ".java"), daoResult.getContent());
        writeFile(generateOutFilePath(DEFAULT_CONTAINER_SUBDIR, containerResult.name, ".java"), containerResult.getContent());
        writeFile(generateOutFilePath(DEFAULT_SQLMAP_SUBDIR, sqlmapResult.name, ".xml"), sqlmapResult.getContent());
        //writeFile(generateOutFilePath());

        writeFile(generateOutFilePath("", "GameDataManager", ".java"), generateGameDataManager(containers));
        writeFile(generateOutFilePath("", "db-game-data-config", ".xml"), generateGameDataConfig(sqlmaps));
    }

    /**
     * 生成所有代码
     *
     * @param excelPaths excel文件路径
     * @param publish
     * @throws IOException
     */
    public void generateAll(List<String> excelPaths, IPublish publish) throws IOException
    {
        Validate.notEmpty(excelPaths);
        mkdirBaseDir();

        List<String> containers = new ArrayList<>();
        List<String> sqlmaps = new ArrayList<>();
        for (String path : excelPaths)
        {
            publish.publishInfo(path);
            ConfigExcelReader excelReader = new ConfigExcelReader();
            ArrayList<Datas> excelContent = excelReader.load(path, true);

            GenerateResult luaResult       = generateLuaFromExcel(excelContent);
            GenerateResult jsResult        = generateJSFromExcel(excelContent);
            GenerateResult csharpResult    = generateCsharpFromExcel(excelContent);
            GenerateResult beanResult      = generateBeanFromExcel(excelContent);
            GenerateResult daoResult       = generateDaoFromExcel(excelContent);
            GenerateResult containerResult = generateContainerFromExcel(excelContent);
            GenerateResult sqlmapResult    = generateSqlMapFromExcel(excelContent);

            containers.add(containerResult.getName());
            sqlmaps.add(sqlmapResult.getName());
            writeFile(generateOutFilePath(DEFAULT_BEAN_SUBDIR, beanResult.name, ".java"), beanResult.getContent());
            writeFile(generateOutFilePath(DEFAULT_DAO_SUBDIR, daoResult.name, ".java"), daoResult.getContent());
            writeFile(generateOutFilePath(DEFAULT_CONTAINER_SUBDIR, containerResult.name, ".java"), containerResult.getContent());
            writeFile(generateOutFilePath(DEFAULT_SQLMAP_SUBDIR, sqlmapResult.name, ".xml"), sqlmapResult.getContent());
            //writeFile(generateOutFilePath());
        }
        writeFile(generateOutFilePath("", "GameDataManager", ".java"), generateGameDataManager(containers));
        writeFile(generateOutFilePath("", "db-game-data-config", ".xml"), generateGameDataConfig(sqlmaps));
    }

    private String generateOutFilePath(String parentDir, String className, String suffix)
    {
        return javaOutDir + "/" + basePackage.replace(".", "/") + "/" + parentDir.replace(".", "/") + "/" + className + suffix;
    }

    private boolean mkdirBaseDir()
    {
        File baseDir = new File(javaOutDir + "/" + basePackage.replace(".", "/"));
        deleteDir(baseDir);
        File beanDir = new File(javaOutDir + "/" + basePackage.replace(".", "/") + "/" + DEFAULT_BEAN_SUBDIR);
        File daoDir = new File(javaOutDir + "/" + basePackage.replace(".", "/") + "/" + DEFAULT_DAO_SUBDIR);
        File containerDir = new File(javaOutDir + "/" + basePackage.replace(".", "/") + "/" + DEFAULT_CONTAINER_SUBDIR);
        File sqlMapDir = new File(javaOutDir + "/" + basePackage.replace(".", "/") + "/" + DEFAULT_SQLMAP_SUBDIR);
        return beanDir.mkdirs() && daoDir.mkdirs() && containerDir.mkdirs() && sqlMapDir.mkdirs();
    }

    private static boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (String children1 : children)
            {
                boolean success = deleteDir(new File(dir, children1));
                if (!success)
                    return false;
            }
        }
        return dir.delete(); // 目录此时为空，可以删除
    }

    private void writeFile(String filePath, String fileContent) throws IOException
    {
        File file = new File(filePath);
        TextFile.write(fileContent, file);
    }

    /**
     * 根据excel配置文件生成Lua
     *
     * @param excelContent
     * @return
     */
    private GenerateResult generateLuaFromExcel(ArrayList<Datas> excelContent)
    {
        for (int index = 0; index < excelContent.size(); index++)
        {
            Datas datas = excelContent.get(index);
            String fileName = datas.getSheetName();
            ArrayList<Field> fields = datas.getFields();

            String path = luaOutDir + "\\" + fileName + ".lua";
            OutputStreamWriter osw;
            try
            {
                osw = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
                
                osw.write(fileName + "_index={");
                for (int i = 0; i < fields.size(); ++i)
                {
                    String data = null;
                    data = "[\"" + fields.get(i).getName() + "\"]=" + (i+1) + ",\r\n";
                    osw.write(data, 0, data.length());
                }
                osw.write("}\r\n\r\n");
                
                osw.write(fileName + "={");
                String[][] datas1 = datas.getDatas();
                if (datas1 == null)
                {
                    log.error("generateLuaFromExcel:" + path);
                    continue;
                }
                
                for (String[] values : datas1)
                {
                    int count = 0;
                    if(values[0] != null &&  values[0]!="null")
                    {
                        for (int i = 0, j = 0; i < values.length && j < fields.size(); i++, j++)
                        {
                            String data = "";
                            String value = values[i];

                            String type = fields.get(j).getJavaClassName();
                            String getName = fields.get(j).getName();
                            if (count == 0)
                            {
                                if (type.equals("int") == false)
                                {
                                    // 每一条数据的key
                                    data = "[" + "\"" + value + "\"" + "]" + "={";
                                }
                                else
                                {
                                    data = "[" + value + "]" + "={";
                                }

                                // 特殊表处理，建立索引key
                                if ("q_debris".equalsIgnoreCase(fileName))
                                {
                                    data = values[1] +"_"+ values[2] +"_"+ values[3];
                                    data = "[" + "\"" + data + "\"" + "]" + "={";
                                }
                                if ("q_rank_growth".equalsIgnoreCase(fileName))
                                {
                                    data = values[1] +"_"+ values[2];
                                    data = "[" + "\"" + data + "\"" + "]" + "={"; 
                                }
                                if ("q_card_star".equalsIgnoreCase(fileName))
                                {
                                    data = values[1] +"_"+ values[2];
                                    data = "[" + "\"" + data + "\"" + "]" + "={";
                                }

                                osw.write(data, 0, data.length());
                            }

                            if (type.equals("int") == false)
                            {
                                //data = getName + " = " + "\"" + value + "\"";
                                data = "\"" + value + "\"";
                            }
                            else
                            {
                                //data = getName + " = " + value;
                                data = value;
                            }


                            if (i != values.length - 1)
                            {
                                data = data + ",";
                            }
                            if(data!=null && data!="null"){
                                osw.write(data, 0, data.length());
                            }
                            count++;
                        }
                        osw.write("},\r\n");
                    }
                }
                osw.write("}\r\n");
                
                osw.write(fileName + ".GetTempData = function (key, strName)\r\n" 
                        +"                             if not " + fileName + "[key] or not " + fileName+"_index[strName]" + " or type(key) == \"function\" then\r\n" 
                        +"                                    log_info(\"" + fileName + ".log\"," + "\" the data is nil or the index data is nil or key is function key =%s , strName = %s\" , tostring(key) , tostring( strName))\r\n"
                        +"                                    return nil\r\n" 
                        +"                             end\r\n" 
                        +"                             if type("+fileName+"[key]) ~= \"table\" then\r\n"
                        +"                                    log_info(\"" + fileName + ".log\"," + "\" the data is not table key =%s , strName = %s\" , tostring(key) , tostring( strName))\r\n"
                        + "                                return nil\r\n" 
                        +"                             end\r\n"
                        +"                             local pData = " + fileName + "[key][" +fileName+"_index[strName]]\r\n"
                        +"                             if not pData then\r\n"
                        +"                                 log_info(\"" + fileName + ".log\"," + "\" the data is nil key =%s , strName = %s\" , tostring(key) , tostring( strName))\r\n"
                        +"                                 return nil\r\n"
                        +"                             end\r\n"
                        +"                             return pData\r\n" 
                        +"                         end\r\n");
                
                osw.close();
            }
            catch (IOException ex)
            {
                log.error("UnsupportedEncodingException | FileNotFoundException", ex);
            }
        }
        return null;
    }

     /**
     * 根据excel配置文件生成CSharp
     *
     * @param excelContent
     * @return
     */
    private GenerateResult generateCsharpFromExcel(ArrayList<Datas> excelContent)
    {
        //TODO:根据要圣城的CSharp规则进行生成代码
        return null;
    }
    
    /**
     * 根据excel配置文件生成JS
     *
     * @param excelContent
     * @return
     */
    private GenerateResult generateJSFromExcel(ArrayList<Datas> excelContent)
    {
        for (int index = 0; index < excelContent.size(); index++)
        {
            Datas datas = excelContent.get(index);
            String fileName = datas.getSheetName();
            ArrayList<Field> fields = datas.getFields();

            String path = this.jsOutDir + "\\" + fileName + ".js";
            OutputStreamWriter osw;
            try
            {
                osw = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
                
                // 字段索引结构
                osw.write("var " + fileName + "_index={");
                for (int i = 0; i < fields.size(); ++i)
                {
                    String data = null;
                    data = fields.get(i).getName() + ":" + (i) + ",\r\n";
                    osw.write(data, 0, data.length());
                }
                osw.write("}\r\n\r\n");
                
                // 配置表数据填充
                osw.write("var "+ fileName+"_Map = {\r\n");
                String[][] datas1 = datas.getDatas();
                if (datas1 == null)
                {
                    log.error("generateLuaFromExcel:" + path);
                    continue;
                }
                
                for (String[] values : datas1)
                {
                    int count = 0;
                    if(values[0] != null &&  values[0]!="null")
                    {
                        for (int i = 0, j = 0; i < values.length && j <= fields.size(); i++, j++)
                        {
                            String data = "";
                            String value = values[i];

                            String type = fields.get(j).getJavaClassName();
                            String getName = fields.get(j).getName();
                            String key = "";
                            if (count == 0)
                            {
                                if (type.equals("int") == false)
                                {
                                    // 每一条数据的key
                                    key = "\"" + value + "\": [";
                                }
                                else
                                {
                                    key =   value + ": [";
                                }                            
                                osw.write(key, 0, key.length());
                            }

                            if (type.equals("int") == false && type.equals("float") == false)
                            {
                                data = "\"" + value + "\"";
                            }
                            else
                            {
                                data = value;
                            }


                            if (i != values.length - 1)
                            {   
                                if(data == ""){
                                    data = "0";
                                }
                                data = data + ",";
                            }
                            if(data!=null && data!="null"){
                                osw.write(data, 0, data.length());
                            }
                             count++;
                        }
                        osw.write("],\r\n");
                    }
                }
                osw.write("}\n\r\n");
                // JS函数获取配置数据
                osw.write("var getData = function (id, field) {\n" +
                "    var dataArray = "+ fileName+"_Map[id]\n" +
                "    if (dataArray) {\n" +
                "    	var index = "+ fileName+"_index[field]\n" +
                "    	return dataArray[index]\n" +
                "    } else {\n" +
                "    	cc.error(\"data not find in ["+ fileName+"], id: %d, field: %s\"," +" id, field)\n" +
                "    }\n" +
                "}\r\n");
                
                osw.write("\r\n");
                // JS模块导出
                 osw.write("module.exports = {\n" +
                "\tgetData: getData,\n"+
                "\t"+ fileName+"_index : " + fileName+"_index,\n" + 
                "\t"+ fileName+"_Map :" +  fileName+"_Map,\n}");
                osw.close();
            }
            catch (IOException ex)
            {
                log.error("UnsupportedEncodingException | FileNotFoundException", ex);
            }
        }
        return null;
    }

    /**
     * 根据excel配置文件生成Bean
     *
     * @param excelContent
     * @return
     */
    private GenerateResult generateBeanFromExcel(ArrayList<Datas> excelContent)
    {
        try
        {
            Iterator<Datas> iter = excelContent.iterator();
            while (iter.hasNext())
            {
                Datas dataInfos = (Datas) iter.next();
                Template temp = cfg.getTemplate("Bean.ftl"); // 读取模板

                HashMap<String, Object> root = new HashMap<>();
                root.put("package", basePackage + "." + DEFAULT_BEAN_SUBDIR);
                root.put("explain", dataInfos.getSheetName());
                root.put("className", generateBeanClassName(dataInfos.getSheetName()));
                root.put("fields", dataInfos.getFields());

                Writer out = new StringWriter();
                temp.process(root, out);
                GenerateResult res = new GenerateResult(generateBeanClassName(dataInfos.getSheetName()), out.toString());
                out.close();
                return res;
            }
        }
        catch (IOException | TemplateException e)
        {
            log.error("IOException | TemplateException", e);
        }
        return null;
    }

    /**
     * 根据excel配置文件生成Dao
     *
     * @param excelContent
     * @return
     */
    private GenerateResult generateDaoFromExcel(ArrayList<Datas> excelContent)
    {
        try
        {
            Iterator<Datas> iter = excelContent.iterator();
            while (iter.hasNext())
            {
                Datas dataInfos = (Datas) iter.next();
                Template temp = cfg.getTemplate("Dao.ftl"); // 读取模板

                List<String> codeImports = new ArrayList<>();
                codeImports.add(basePackage + ".bean." + generateBeanClassName(dataInfos.getSheetName()));

                HashMap<String, Object> root = new HashMap<>();
                root.put("package", basePackage + "." + DEFAULT_DAO_SUBDIR);
                root.put("explain", dataInfos.getSheetName());
                root.put("imports", codeImports);
                root.put("className", generateDaoClassName(dataInfos.getSheetName()));
                root.put("beanClass", generateBeanClassName(dataInfos.getSheetName()));
                root.put("xmlName", dataInfos.getSheetName());

                Writer out = new StringWriter();
                temp.process(root, out);

                GenerateResult res = new GenerateResult(generateDaoClassName(dataInfos.getSheetName()), out.toString());
                out.close();
                return res;
            }
        }
        catch (IOException | TemplateException e)
        {
            log.error("IOException | TemplateException", e);
        }
        return null;
    }

    /**
     * 根据excel配置文件生成Container
     *
     * @param excelContent
     * @return
     */
    private GenerateResult generateContainerFromExcel(ArrayList<Datas> excelContent)
    {
        try
        {
            Iterator<Datas> iter = excelContent.iterator();
            while (iter.hasNext())
            {
                Datas dataInfos = (Datas) iter.next();
                Template temp = cfg.getTemplate("Container.ftl"); // 读取模板

                List<String> codeImports = new ArrayList<>();
                codeImports.add(basePackage + ".bean." + generateBeanClassName(dataInfos.getSheetName()));
                codeImports.add(basePackage + ".dao." + generateDaoClassName(dataInfos.getSheetName()));

                HashMap<String, Object> root = new HashMap<>();
                root.put("package", basePackage + "." + DEFAULT_CONTAINER_SUBDIR);
                root.put("explain", dataInfos.getSheetName());
                root.put("imports", codeImports);
                root.put("className", generateContainerClassName(dataInfos.getSheetName()));
                root.put("beanClass", generateBeanClassName(dataInfos.getSheetName()));
                root.put("daoClass", generateDaoClassName(dataInfos.getSheetName()));
                root.put("keyClass", dataInfos.getKey().getJavaClassName());
                root.put("keyName", dataInfos.getKey().getName());

                Writer out = new StringWriter();
                temp.process(root, out);

                GenerateResult res = new GenerateResult(generateContainerClassName(dataInfos.getSheetName()), out.toString());
                out.close();
                return res;
            }
        }
        catch (IOException | TemplateException e)
        {
            log.error("IOException | TemplateException", e);
        }
        return null;
    }

    /**
     * 根据excel配置文件生成Bean
     *
     * @param excelPath
     * @param excelContent
     * @return
     */
    private GenerateResult generateSqlMapFromExcel(ArrayList<Datas> excelContent)
    {
        try
        {
            Iterator<Datas> iter = excelContent.iterator();
            while (iter.hasNext())
            {
                Datas dataInfos = (Datas) iter.next();
                Template temp = cfg.getTemplate("SqlMap.ftl"); // 读取模板

                HashMap<String, Object> root = new HashMap<>();
                root.put("name", dataInfos.getSheetName());
                root.put("beanClass", generateBeanFullClassName(dataInfos.getSheetName()));
                root.put("fields", dataInfos.getFields());

                Writer out = new StringWriter();
                temp.process(root, out);

                GenerateResult res = new GenerateResult(dataInfos.getSheetName(), out.toString());
                out.close();
                return res;
            }
        }
        catch (IOException | TemplateException e)
        {
            log.error("IOException | TemplateException", e);
        }
        return null;
    }

    private String generateGameDataManager(List<String> containers)
    {
        try
        {
            Template temp = cfg.getTemplate("GameDataManager.ftl"); // 读取模板

            HashMap<String, Object> root = new HashMap<>();
            root.put("package", basePackage);
            root.put("dataPackage", basePackage);
            root.put("containers", containers);

            Writer out = new StringWriter();
            temp.process(root, out);

            String res = out.toString();
            log.info(res);
            out.close();
            return res;
        }
        catch (IOException e)
        {
            log.error("IOException", e);
        }
        catch (TemplateException e)
        {
            log.error("TemplateException", e);
        }
        return null;
    }

    private String generateGameDataConfig(List<String> sqlmaps)
    {
        try
        {
            Template temp = cfg.getTemplate("db-game-data-config.ftl"); // 读取模板

            HashMap<String, Object> root = new HashMap<>();
            root.put("baseDir", basePackage.replace(".", "/"));
            root.put("sqlmaps", sqlmaps);

            Writer out = new StringWriter();
            temp.process(root, out);

            String res = out.toString();
            log.info(res);
            out.close();
            return res;
        }
        catch (IOException e)
        {
            log.error("IOException", e);
        }
        catch (TemplateException e)
        {
            log.error("TemplateException", e);
        }
        return null;
    }

    private String generateBeanClassName(String plainName)
    {
        return plainName + "Bean";
    }

    private String generateDaoClassName(String plainName)
    {
        return plainName + "Dao";
    }

    private String generateContainerClassName(String plainName)
    {
        return plainName + "Container";
    }

    private String generateBeanFullClassName(String plainName)
    {
        return basePackage + ".bean." + generateBeanClassName(plainName);
    }

    public static void main(String[] args) throws IOException
    {

/*        List<String> excels = new ArrayList<>();
        excels.add("./5 背包仓库开格配置数据库.xlsx");
        excels.add("./4 技能基本数据库.xlsx");
        excels.add("./7 商店配置数据库.xlsx");
        JavaGenerator jg = new JavaGenerator("game.data", ".", ".",  ".");
        //jg.generateAll(excels);*/
    }
}
