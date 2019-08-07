package cn.roy.springcloud.api.intercepter;

import cn.roy.springcloud.api.datasource.DynamicDataSource;
import cn.roy.springcloud.api.datasource.DynamicDataSourceHolder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.Random;

/**
 * @Description: SQL拦截器
 * @Author: Roy Z
 * @Date: 2019-08-05 14:26
 * @Version: v1.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SQLInterceptor implements Interceptor {
    protected static final Logger logger = LoggerFactory.getLogger(SQLInterceptor.class);
    private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";

    private int slaveDatasourceCount = 0;

    public SQLInterceptor(int slaveDatasourceCount) {
        this.slaveDatasourceCount = slaveDatasourceCount;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (!synchronizationActive) {
            Object[] objects = invocation.getArgs();
            MappedStatement ms = (MappedStatement) objects[0];

            String dynamicDataSourceType = null;

            //读方法
            if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                //!selectKey 为自增id查询主键(SELECT LAST_INSERT_ID() )方法，使用主库
                if (ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                    dynamicDataSourceType = DynamicDataSource.DynamicDataSourceType.getMasterType();
                } else {
                    BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
                    String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
                    if (sql.matches(REGEX)) {
                        dynamicDataSourceType = DynamicDataSource.DynamicDataSourceType.getMasterType();
                    } else {
                        dynamicDataSourceType = DynamicDataSource.DynamicDataSourceType.getSlaveType(slaveLoadBalance());
                    }
                }
            } else {
                dynamicDataSourceType = DynamicDataSource.DynamicDataSourceType.getMasterType();
            }
            logger.warn("设置方法[{}] use [{}] Strategy, SqlCommandType [{}]..", ms.getId(), dynamicDataSourceType,
                    ms.getSqlCommandType().name());

            DynamicDataSourceHolder.putDynamicDataSourceType(dynamicDataSourceType);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    private int slaveLoadBalance() {
        System.out.println("调用：" + this);
        //通过随机获取数组中数据库的名称来随机分配要使用的数据库
        if (slaveDatasourceCount == 0) {
            return 0;
        }
        int num = new Random().nextInt(slaveDatasourceCount);
        return num;
    }

}
