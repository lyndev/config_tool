CREATE TABLE `${tabelName}`(
  <#list fields as field>
  `${field.name}` ${field.className},
  </#list>
  PRIMARY KEY(`${keyName}`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;