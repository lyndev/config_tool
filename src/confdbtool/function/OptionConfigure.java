package confdbtool.function;

import confdbtool.util.TextFile;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author ChenLong
 */
public class OptionConfigure
{
    private final Logger log = Logger.getLogger(OptionConfigure.class);
    private final static String DEFAULT_CONFIG_PATH = "config/ConfDBTool.xml";
    private String excelPath;       // excel配置路径
    private String javaCodePath;    // java代码导出配置路径
    private String luaCodePath;     // lua代码导出配置路径
    private String jsCodePath;      // js代码导出配置路径
    private String csharpCodePath;  // c#代码导出配置路径
    private String mysqlUrl;        // mysql地址配置路径
    private String mysqlUser;       //  mysql用户名
    private String mysqlPasswd;     // mysql密码
    private String lookandfeel;

    public String getExcelPath()
    {
        return excelPath;
    }

    public void setExcelPath(String excelPath)
    {
        this.excelPath = excelPath;
    }

    public String getJavaCodePath()
    {
        return javaCodePath;
    }

    public void setJavaCodePath(String javaCodePath)
    {
        this.javaCodePath = javaCodePath;
    }

    public String getLuaCodePath()
    {
        return luaCodePath;
    }

    public void setLuaCodePath(String luaCodePath)
    {
        this.luaCodePath = luaCodePath;
    }
    public void setJSCodePath(String jsCodePath)
    {
        this.jsCodePath = jsCodePath;
    }
    
    public String getJSCodePath()
    {
        return jsCodePath;
    }

    public void setCSharpCodePath(String csharpCodePath)
    {
        this.csharpCodePath = csharpCodePath;
    }
    
    public String getCSharpCodePath()
    {
        return this.csharpCodePath;
    }

    
    public String getMysqlUrl()
    {
        return mysqlUrl;
    }

    public void setMysqlUrl(String mysqlUrl)
    {
        this.mysqlUrl = mysqlUrl;
    }

    public String getMysqlUser()
    {
        return mysqlUser;
    }

    public void setMysqlUser(String mysqlUser)
    {
        this.mysqlUser = mysqlUser;
    }

    public String getMysqlPasswd()
    {
        return mysqlPasswd;
    }

    public void setMysqlPasswd(String mysqlPasswd)
    {
        this.mysqlPasswd = mysqlPasswd;
    }

    public String getLookandfeel()
    {
        return lookandfeel;
    }

    public void setLookandfeel(String lookandfeel)
    {
        this.lookandfeel = lookandfeel;
    }

    public boolean hasConfig()
    {
        File xmlFile = new File(DEFAULT_CONFIG_PATH);
        return xmlFile.exists();
    }

    private void initFromXml() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File xmlFile = new File(DEFAULT_CONFIG_PATH);
        Document doc = null;
        try
        {
            doc = builder.parse(xmlFile);
        }
        catch (Exception ex)
        {
            log.error("cannot find config file", ex);
            return;
        }

        Element configure = doc.getDocumentElement();
        NodeList children = configure.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i)
        {
            Node child = children.item(i);
            if (child instanceof Element)
            {
                String name = child.getNodeName();
                if (name.equals("excel"))
                    excelPath = readPathConf(child);
                else if (name.equals("java"))
                    javaCodePath = readPathConf(child);
                else if (name.equals("lua"))
                    luaCodePath = readPathConf(child);
                else if (name.equals("js"))
                    jsCodePath = readPathConf(child);
                else if (name.equals("csharp"))
                    this.csharpCodePath = readPathConf(child);
                else if (name.equals("mysql"))
                    readMySQLConf(child);
                else if (name.equals("lookandfeel"))
                    readLookAndFeelConf(child);
                else
                    log.error("unknow node [" + name + "]");
            }
        }
    }

    private String readPathConf(Node node)
    {
        String res = null;
        NamedNodeMap attributes = node.getAttributes();
        for (int j = 0; j < attributes.getLength(); ++j)
        {
            Node attribute = attributes.item(j);
            String attName = attribute.getNodeName();
            String attValue = attribute.getNodeValue();
            if (attName.equals("path"))
            {
                res = attValue;
                break;
            }
        }
        return res;
    }

    private void readMySQLConf(Node node)
    {
        NamedNodeMap attributes = node.getAttributes();
        for (int j = 0; j < attributes.getLength(); ++j)
        {
            Node attribute = attributes.item(j);
            String attName = attribute.getNodeName();
            String attValue = attribute.getNodeValue();
            if (attName.equals("url"))
                mysqlUrl = attValue;
            if (attName.equals("user"))
                mysqlUser = attValue;
            if (attName.equals("passwd"))
                mysqlPasswd = attValue;
        }
    }

    private void readLookAndFeelConf(Node node)
    {
        NamedNodeMap attributes = node.getAttributes();
        for (int j = 0; j < attributes.getLength(); ++j)
        {
            Node attribute = attributes.item(j);
            String attName = attribute.getNodeName();
            String attValue = attribute.getNodeValue();
            if (attName.equals("name"))
            {
                lookandfeel = attValue;
                break;
            }
        }
    }

    public void writeToConfigFile() throws IOException, TemplateException
    {
        Configuration cfg = new Configuration();
        java.io.File path = new java.io.File(".//config//ftls//config//");
        cfg.setDirectoryForTemplateLoading(path);
        cfg.setObjectWrapper(new DefaultObjectWrapper());

        Template temp = cfg.getTemplate("ConfDBTool.ftl", "UTF-8"); // 读取模板

        HashMap<String, Object> root = new HashMap<>();
        root.put("excelPath", excelPath);
        root.put("javaPath", javaCodePath);
        root.put("luaPath", luaCodePath);
        root.put("jsPath", jsCodePath);
        root.put("csharpPath", csharpCodePath);
        root.put("mysqlUrl", mysqlUrl);
        root.put("mysqlUser", mysqlUser);
        root.put("mysqlPasswd", mysqlPasswd);
        root.put("lookandfeel", lookandfeel);

        Writer out = new StringWriter();
        temp.process(root, out);

        log.info(out.toString());
        File file = new File(DEFAULT_CONFIG_PATH);
        TextFile.write(out.toString(), file);

        out.close();
    }

    private OptionConfigure()
    {
        try
        {
            initFromXml();
        }
        catch (ParserConfigurationException e)
        {
            log.error("SAXException | ParserConfigurationException | IOException", e);
        }
    }

    /**
     * 用枚举来实现单例
     *
     * @return
     */
    public static OptionConfigure getInstance()
    {
        return Singleton.INSTANCE.getExcelManager();
    }

    private enum Singleton
    {

        INSTANCE;

        OptionConfigure optionReader;

        Singleton()
        {
            optionReader = new OptionConfigure();
        }

        OptionConfigure getExcelManager()
        {
            return optionReader;
        }
    }
}
