/**
 * Auto generated, do not edit it
 */
package ${package};

<#list containers as container>
import ${dataPackage}.container.${container};
</#list>
import org.apache.log4j.Logger;
import org.apache.ibatis.session.SqlSessionFactory;

public class GameDataManager
{
    private final Logger log = Logger.getLogger(GameDataManager.class);
    <#list containers as container>
    public volatile ${container} ${container} = new ${container}();
    </#list>

    private SqlSessionFactory sessionFactory;

    public void loadAll()
    {
	log.info("Start load all game data ...");
        <#list containers as container>
        ${container}.load();
        </#list>
    }

    public GameDataManager setSqlSessionFactory(SqlSessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
        return this;
    }

    public SqlSessionFactory getSqlSessionFactory()
    {
        return sessionFactory;
    }

    /**
     * 获取GameServer的实例对象.
     *
     * @return
     */
    public static GameDataManager getInstance()
    {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton
    {
        INSTANCE;
        GameDataManager manager;

        Singleton()
        {
            this.manager = new GameDataManager();
        }

        GameDataManager getProcessor()
        {
            return manager;
        }
    }
}
