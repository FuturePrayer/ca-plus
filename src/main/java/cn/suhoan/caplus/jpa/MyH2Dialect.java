package cn.suhoan.caplus.jpa;

import org.hibernate.dialect.H2Dialect;

/**
 * @author FuturePrayer
 * @date 2025/4/22
 */
public class MyH2Dialect extends H2Dialect {
    @Override
    public String getAlterColumnTypeString(String columnName, String columnType, String columnDefinition) {
        return "alter column " + columnName + " " + columnDefinition;
    }
}
