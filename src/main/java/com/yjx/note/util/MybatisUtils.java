package com.yjx.note.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
// ✅ 改用 SLF4J
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class MybatisUtils {
    static SqlSessionFactory ssf = null;

    // ✅ 使用 LoggerFactory
    private static final Logger logger = LoggerFactory.getLogger(MybatisUtils.class);

    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream is = Resources.getResourceAsStream(resource);
            ssf = new SqlSessionFactoryBuilder().build(is);
            logger.info("数据库连接成功 ✔");
        } catch (IOException e) {
            logger.error("数据库连接失败 ❌", e); // 可以直接传入异常
        }
    }

    public static SqlSession getSqlSession() {
        return ssf.openSession(true);
    }
}