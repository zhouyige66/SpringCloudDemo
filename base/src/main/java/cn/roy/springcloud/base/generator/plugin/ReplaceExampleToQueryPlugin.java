package cn.roy.springcloud.base.generator.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

/**
 * @Description: 替换插件
 * @Author: Roy Z
 * @Date: 2020-01-03 16:39
 * @Version: v1.0
 */
public class ReplaceExampleToQueryPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String exampleWhereClauseId = introspectedTable.getExampleWhereClauseId();
        String myBatis3UpdateByExampleWhereClauseId = introspectedTable.getMyBatis3UpdateByExampleWhereClauseId();
        String deleteByExampleStatementId = introspectedTable.getDeleteByExampleStatementId();
        String updateByExampleStatementId = introspectedTable.getUpdateByExampleStatementId();
        String updateByExampleSelectiveStatementId = introspectedTable.getUpdateByExampleSelectiveStatementId();
        String selectByExampleStatementId = introspectedTable.getSelectByExampleStatementId();
        String countByExampleStatementId = introspectedTable.getCountByExampleStatementId();

        exampleWhereClauseId = exampleWhereClauseId.replace("Example","Model");
        myBatis3UpdateByExampleWhereClauseId = myBatis3UpdateByExampleWhereClauseId.replace("Example","Model");
        deleteByExampleStatementId = deleteByExampleStatementId.replace("Example","Query");
        updateByExampleStatementId = updateByExampleStatementId.replace("Example","Query");
        updateByExampleSelectiveStatementId = updateByExampleSelectiveStatementId.replace("Example","Query");
        selectByExampleStatementId = selectByExampleStatementId.replace("Example","Query");
        countByExampleStatementId = countByExampleStatementId.replace("Example","Query");

        introspectedTable.setExampleWhereClauseId(exampleWhereClauseId);
        introspectedTable.setMyBatis3UpdateByExampleWhereClauseId(myBatis3UpdateByExampleWhereClauseId);
        introspectedTable.setDeleteByExampleStatementId(deleteByExampleStatementId);
        introspectedTable.setUpdateByExampleStatementId(updateByExampleStatementId);
        introspectedTable.setUpdateByExampleSelectiveStatementId(updateByExampleSelectiveStatementId);
        introspectedTable.setSelectByExampleStatementId(selectByExampleStatementId);
        introspectedTable.setCountByExampleStatementId(countByExampleStatementId);
    }

}
