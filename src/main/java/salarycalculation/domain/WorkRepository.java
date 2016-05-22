package salarycalculation.domain;

import salarycalculation.database.WorkDao;
import salarycalculation.entity.Work;

/**
 * 稼動情報リポジトリ。
 *
 * @author naotake
 */
public class WorkRepository {

    private WorkDao dao;

    public WorkRepository() {
        this.dao = new WorkDao();
    }

    /**
     * 該当社員の稼動年月の稼動情報を取得する。
     *
     * @param employeeNo 社員番号
     * @param workYearMonth 稼動年月 (e.g. 201504)
     * @return 稼動情報
     */
    public WorkDomain getByYearMonth(int employeeNo, int workYearMonth) {
        Work work = dao.getByYearMonth(employeeNo, workYearMonth);
        return new WorkDomain(work);
    }
}
