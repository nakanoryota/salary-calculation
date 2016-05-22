package salarycalculation.database;

import java.sql.Connection;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;

import salarycalculation.entity.Role;
import salarycalculation.exception.RecordNotFoundException;

/**
 * 役割等級 Dao。
 *
 * @author naotake
 */
public class RoleDao extends BaseDao<Role> {

    public RoleDao() {
        super();
    }

    /**
     * 等級を基に役割等級を取得する。
     *
     * @param rank 等級
     * @return 役割等級
     */
    public Role get(String rank) {
        verify(rank);
        String query = "select * from role where rank = ?";

        Role result = getByQuery(query, rank);
        if (result == null) {
            throw new RecordNotFoundException(Role.class, rank);
        }
        return result;
    }

    private void verify(String rank) {
        if (StringUtils.isBlank(rank)) {
            throw new NullPointerException("等級は必須です");
        }
        if (StringUtils.length(rank) != 2) {
            throw new IllegalArgumentException(String.format("等級は 2 桁で指定してください[%s]",
                                                             StringUtils.length(rank)));
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected BeanHandler<Role> newBeanHandler() {
        return new BeanHandler<Role>(Role.class);
    }

    @Override
    protected BeanListHandler<Role> newBeanListHandler() {
        return new BeanListHandler<Role>(Role.class);
    }
}
