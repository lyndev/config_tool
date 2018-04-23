/**
 * Auto generated, do not edit it
 *
 * ${explain}
 */
package ${package};

public class ${className}
{
    <#list fields as field>
    private ${field.javaClassName} ${field.name}; // ${field.explain}
    </#list>
    <#list fields as field>

    /**
     * get ${field.explain}
     * @return
     */
    public ${field.javaClassName} get${field.name?cap_first}()
    {
        return ${field.name};
    }

    /**
     * set ${field.explain}
     */
    public void set${field.name?cap_first}(${field.javaClassName} ${field.name})
    {
        this.${field.name} = ${field.name};
    }
    </#list>
}
