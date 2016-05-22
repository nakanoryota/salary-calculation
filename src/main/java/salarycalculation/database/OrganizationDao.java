package salarycalculation.database;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import salarycalculation.entity.Organization;
import salarycalculation.exception.RecordNotFoundException;

/**
 * 組織 Dao。
 *
 * @author naotake
 */
public class OrganizationDao extends BaseDao<Organization> {

    public OrganizationDao() {
        super();
    }

    /**
     * 組織コードを基に組織情報を取得する。
     *
     * @param code 組織コード
     * @return 組織情報
     */
    public Organization get(String code) {
        String query = "select * from organization where code = ?";

        Organization result = getByQuery(query, code);
        if (result == null) {
            throw new RecordNotFoundException(Organization.class, code);
        }
        return result;
    }

    @Override
    protected BeanHandler<Organization> newBeanHandler() {
        return new BeanHandler<Organization>(Organization.class);
    }

    @Override
    protected BeanListHandler<Organization> newBeanListHandler() {
        return new BeanListHandler<Organization>(Organization.class);
    }
}
