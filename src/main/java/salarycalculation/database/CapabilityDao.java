package salarycalculation.database;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import salarycalculation.entity.Capability;
import salarycalculation.exception.RecordNotFoundException;

/**
 * 能力等級 Dao。
 *
 * @author naotake
 */
public class CapabilityDao extends BaseDao<Capability> {

    public CapabilityDao() {
        super();
    }

    /**
     * 等級を基に能力等級を取得する。
     *
     * @param rank 等級
     * @return 能力等力
     */
    public Capability get(String rank) {
        String query = "select * from capability where rank = ?";

        Capability result = getByQuery(query, rank);
        if (result == null) {
            throw new RecordNotFoundException(Capability.class, rank);
        }
        return result;
    }

    @Override
    protected BeanHandler<Capability> newBeanHandler() {
        return new BeanHandler<Capability>(Capability.class);
    }

    @Override
    protected BeanListHandler<Capability> newBeanListHandler() {
        return new BeanListHandler<Capability>(Capability.class);
    }
}
