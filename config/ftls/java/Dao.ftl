/**
 * Auto generated, do not edit it
 *
 * ${explain}
 */
package ${package};

import game.data.GameDataManager;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
<#if imports??>
<#list imports as import>
import ${import};
</#list>
</#if>

public class ${className}
{
    public List<${beanClass}> select()
    {
        try
        (SqlSession session = GameDataManager.getInstance().getSqlSessionFactory().openSession())
        {
            List<${beanClass}> list = session.selectList("${xmlName}.selectAll");
            return list;
        }
    }
}
