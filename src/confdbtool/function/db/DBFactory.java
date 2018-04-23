package confdbtool.function.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

/**
 *
 * <b>DB工厂.</b>
 * <p>
 * 使用枚举来实现单例, 可以避免多线程问题, 并且, 当有多个不同的数据库时, 每个数据库都作为枚举的一个属性来定义即可, 使用简单, 维护方便.
 * <p>
 * <b>Sample:</b>
 *
 * @author <a href="mailto:wjv.1983@gmail.com">wangJingWei</a>
 * @version 1.0.0
 */
public enum DBFactory
{
    GAME_DATA_DB("gameDataDBLogger", "config/db-game-data-config.xml"); // 策划配置数据库

    private final Logger logger;

    private SqlSessionFactory sessionFactory;

    /**
     * 初始化一个DB实例.
     *
     * @param loggerName logger名称
     * @param config mybatis配置文件的相对路径
     */
    private DBFactory(String loggerName, String config)
    {
        logger = Logger.getLogger(loggerName);
        try
        {
            try (InputStream in = new FileInputStream(config))
            {
                sessionFactory = new SqlSessionFactoryBuilder().build(in);
            }
        }
        catch (IOException e)
        {
            logger.error(e, e);
        }
    }

    /**
     * 获取日志管理器.
     *
     * @return
     */
    public Logger getLogger()
    {
        return logger;
    }

    /**
     * 获取SQLSeesionFactory.
     *
     * @return
     */
    public SqlSessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

}
