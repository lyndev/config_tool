/**
 * Auto generated, do not edit it
 *
 * ${explain}
 */
<#import "Class.ftl" as Class>
package ${package};

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
<#if imports??>
<#list imports as import>
import ${import};
</#list>
</#if>

public class ${className}
{
    private List<${beanClass}> list;
    private final Map<${.globals[keyClass]!keyClass}, ${beanClass}> map = new HashMap<>();
    private final ${daoClass} dao = new ${daoClass}();

    public void load()
    {
        list = dao.select();
        Iterator<${beanClass}> iter = list.iterator();
        while (iter.hasNext())
        {
            ${beanClass} bean = (${beanClass}) iter.next();
            map.put(bean.get${keyName?cap_first}(), bean);
        }
    }

    public List<${beanClass}> getList()
    {
        return list;
    }

    public Map<${.globals[keyClass]!keyClass}, ${beanClass}> getMap()
    {
        return map;
    }
}