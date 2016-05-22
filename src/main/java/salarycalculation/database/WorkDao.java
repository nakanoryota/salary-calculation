package salarycalculation.database;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import salarycalculation.entity.Work;
import salarycalculation.exception.RecordNotFoundException;

/**
 * 稼動情報 Dao。
 *
 * @author naotake
 */
public class WorkDao extends BaseDao<Work> {

    public WorkDao() {
        super();
    }

    /**
     * 該当社員の稼動年月の稼動情報を取得する。
     *
     * @param employeeNo 社員番号
     * @param workYearMonth 稼動年月 (e.g. 201504)
     * @return 稼動情報
     */
    // @UT
    public Work getByYearMonth(int employeeNo, int workYearMonth) {
        String query = "select * from work where employeeNo = ? and workYearMonth = ?";

        Work result = getByQuery(query, employeeNo, workYearMonth);
        if (result == null) {
            throw new RecordNotFoundException(Work.class, employeeNo, workYearMonth);
        }
        return result;
    }

    @Override
    protected BeanHandler<Work> newBeanHandler() {
        return new BeanHandler<Work>(Work.class);
    }

    @Override
    protected BeanListHandler<Work> newBeanListHandler() {
        return new BeanListHandler<Work>(Work.class);
    }
}
