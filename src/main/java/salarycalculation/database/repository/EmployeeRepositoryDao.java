package salarycalculation.database.repository;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import salarycalculation.database.EmployeeDao;
import salarycalculation.database.model.EmployeeRecord;
import salarycalculation.domain.employee.BusinessDate;
import salarycalculation.domain.employee.Employee;
import salarycalculation.domain.employee.EmployeeRepository;
import salarycalculation.domain.employee.Employees;
import salarycalculation.domain.organization.Organization;
import salarycalculation.domain.organization.OrganizationRepository;

/**
 * 社員情報リポジトリ。
 *
 * @author naotake
 */
public class EmployeeRepositoryDao implements EmployeeRepository {

    private OrganizationRepository organizationRepository;
    private EmployeeDao dao;
    private EmployeeTransformer transformer;

    public EmployeeRepositoryDao() {
        this.dao = new EmployeeDao();
        this.organizationRepository = new OrganizationRepositoryDao();
        this.transformer = new EmployeeTransformer();
    }

    /**
     * 社員番号を基に社員情報を全て取得する。
     *
     * @param no 社員番号
     * @return 社員情報
     */
    @Override
    public Employee get(String no) {
        EmployeeRecord employee = dao.get(no);

        // Domain を準備
        Employee entity = transformer.transformToEntity(employee);

        return entity;
    }

    /**
     * 社員番号を基に組織と社員情報を取得する。
     *
     * ※不完全なEntityが生成されるため非推奨
     *
     * @param no 社員番号
     * @return 社員情報
     */
    @Override
    @Deprecated
    public Employee getSimple(String no) {
        EmployeeRecord employee = dao.get(no);

        // 所属する組織情報を取得
        Organization organization = organizationRepository.find(employee.getOrganization());

        // Domain を準備
        Employee entity = transformer.createFromRecord(employee, organization,
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        return entity;
    }

    /**
     * 社員情報の一覧を取得する。<br />
     * 社員番号の昇順に一覧を取得する。
     *
     * @return 社員情報一覧
     */
    @Override
    public Employees findAll() {
        return new Employees(findAllAsStream()
                .collect(toList()));

    }

    /**
     * 想定年収順に社員情報の一覧を取得する。
     *
     * @param ascending 想定年収の昇順（低い順）なら true
     * @return 社員情報一覧
     */
    @Override
    public Employees findAllOrderByAnnualSalary(final boolean ascending) {
        // 並び替え
        return new Employees(findAllAsStream().sorted(this.compareAnnualTotalSalaryPlan(ascending)).collect(toList()));
    }

    /**
     * 全Entityをストリームで取得する。
     *
     * @return 全Entity
     */
    private Stream<Employee> findAllAsStream() {

        List<EmployeeRecord> employees = dao.findAll(true);
        return employees.stream()
                .map(transformer::transformToEntity);

    }

    private Comparator<Employee> compareAnnualTotalSalaryPlan(boolean ascending) {

        Comparator<Employee> comparator = (o1, o2) -> o1.getAnnualTotalSalaryPlan()
                .minus(o2.getAnnualTotalSalaryPlan()).value().intValue();

        if (ascending) {
            return comparator;
        }
        return comparator.reversed();

    }

    /**
     * 勤続月数の最大 or 最小の社員情報を取得する。
     *
     * // TODO 戻り値はリスト？
     *
     * @param selectMax 最大を求める場合は true
     * @return 社員情報
     */
    @Override
    public Employee getByDurationMonth(boolean selectMax) {

        BusinessDate now = BusinessDate.now();

        BinaryOperator<Employee> accumulator = selectMax
                ? (a, b) -> a.calculateAttendanceMonth(now) >= b.calculateAttendanceMonth(now) ? a : b
                : (a, b) -> a.calculateAttendanceMonth(now) <= b.calculateAttendanceMonth(now) ? a : b;

        return findAllAsStream().reduce(accumulator).get();

    }

    @Override
    public long countByOrganization(String organizationCode) {
        return dao.countByOrganization(organizationCode);
    }

    public void setDao(EmployeeDao dao) {
        this.dao = dao;
    }

    public void setOrganizationDao(OrganizationRepository organizationDao) {
        this.organizationRepository = organizationDao;
    }

    public void setTransFormer(EmployeeTransformer transformer) {
        this.transformer = transformer;
    }

}
