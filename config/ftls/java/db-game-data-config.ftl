<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//ibatis.apache.org//DTD Config 3.0//EN" "http://ibatis.apache.org/dtd/ibatis-3-config.dtd">
<configuration>
    <environments default="development">
	<environment id="development">
	    <transactionManager type="JDBC"/>
	    <dataSource type="POOLED">
		<property name="driver" value="com.mysql.jdbc.Driver"/>
		<property name="url" value="${r"${url}"}"/>
		<property name="username" value="${r"${username}"}"/>
		<property name="password" value="${r"${password}"}"/>
		<property name="poolPingQuery" value="select 1"/>
		<property name="poolPingEnabled" value="true"/>
		<property name="poolPingConnectionsNotUsedFor" value="3600000"/>
	    </dataSource> 
	</environment> 
    </environments> 
    <mappers>
<#list sqlmaps as sqlmap>
        <mapper resource="${baseDir}/sqlmap/${sqlmap}.xml" />
</#list>
    </mappers>
</configuration>
