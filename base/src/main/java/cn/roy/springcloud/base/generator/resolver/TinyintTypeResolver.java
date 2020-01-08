package cn.roy.springcloud.base.generator.resolver;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * @Description: tinyint逆向生成Integer
 * @Author: Roy Z
 * @Date: 2020-01-08 10:57
 * @Version: v1.0
 */
public class TinyintTypeResolver extends JavaTypeResolverDefaultImpl {

    public TinyintTypeResolver() {
        super();
        typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Integer.class.getName())));
    }

}
