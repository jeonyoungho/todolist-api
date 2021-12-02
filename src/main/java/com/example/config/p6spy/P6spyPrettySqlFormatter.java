package com.example.config.p6spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Profile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Profile({"local", "test"})
public class P6spyPrettySqlFormatter implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formalSql(category, sql);

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");

        return format.format(currentDate) + " | " + "OperationTime: " + elapsed + "ms" + sql;
    }

    private String formalSql(String category, String sql) {
        if (sql == null || sql.trim().equals("")) return sql;

        // Only format Statement, distinguish DDL And DML
        if (Category.STATEMENT.getName().equals(category)) {
            String tmpsql = sql.trim().toLowerCase(Locale.ROOT);

            if (tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
        }

        return sql;
    }


}
