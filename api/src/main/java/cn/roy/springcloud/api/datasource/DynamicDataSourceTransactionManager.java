package cn.roy.springcloud.api.datasource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import javax.sql.DataSource;

/**
 * @Description: 事务管理库选择（多数据源）
 * @Author: Roy Z
 * @Date: 2019-08-05 15:57
 * @Version: v1.0
 */
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {

    public DynamicDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 只读事务到读库，读写事务到写库
     *
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        //设置数据源
        boolean readOnly = definition.isReadOnly();
        if (readOnly) {
            // 默认使用第一个Slave源
            DynamicDataSourceHolder.putDynamicDataSourceType(DynamicDataSource.DynamicDataSourceType.getSlaveType(0));
        } else {
            DynamicDataSourceHolder.putDynamicDataSourceType(DynamicDataSource.DynamicDataSourceType.getMasterType());
        }
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     *
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DynamicDataSourceHolder.clearDataSource();
    }

}
