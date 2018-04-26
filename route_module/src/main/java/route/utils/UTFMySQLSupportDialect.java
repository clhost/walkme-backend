package route.utils;

import org.hibernate.dialect.MySQL57Dialect;

@SuppressWarnings("unused")
public class UTFMySQLSupportDialect extends MySQL57Dialect {
    @Override
    public String getTableTypeString() {
        return "default charset=utf8 COLLATE utf8_general_ci";
    }
}
