<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper  PUBLIC "-//ibatis.apache.org//DTD Mapper 3.0//EN"  "http://ibatis.apache.org/dtd/ibatis-3-mapper.dtd">
<mapper namespace="${name}">
    <resultMap id="bean" type="${beanClass}" >
        <#list fields as field>
            <result column="${field.name}" property="${field.name}" jdbcType="${field.dbClassName}" />
        </#list>
    </resultMap>

    <select id="selectAll" resultMap="bean">
        select * from ${name}
    </select>
</mapper>
