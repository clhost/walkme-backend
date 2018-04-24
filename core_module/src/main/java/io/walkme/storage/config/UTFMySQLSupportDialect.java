package io.walkme.storage.config;

import org.hibernate.dialect.MySQL57Dialect;

public class UTFMySQLSupportDialect extends MySQL57Dialect {
    @Override
    public String getTableTypeString() {
        return "default charset=utf8 COLLATE utf8_general_ci";
    }
}
