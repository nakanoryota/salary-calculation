package salarycalculation.domain;

import static salarycalculation.domain.EmployeeComparators.ANNUAL_TOTAL_SALARY_PLAN_ASC;
import static salarycalculation.domain.EmployeeComparators.ANNUAL_TOTAL_SALARY_PLAN_DESC;
import static salarycalculation.domain.EmployeeComparators.DURATION_MONTH_ASC;
import static salarycalculation.domain.EmployeeComparators.DURATION_MONTH_DESC;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import salarycalculation.database.CapabilityDao;
import salarycalculation.database.EmployeeDao;
import salarycalculation.database.OrganizationDao;
import salarycalculation.database.RoleDao;
import salarycalculation.entity.Capability;
import salarycalculation.entity.Employee;
import salarycalculation.entity.Organization;
import salarycalculation.entity.Role;

/**
 * 社員情報リポジトリ。
 *
 * @author naotake
 */
public class EmployeeRepository {

    private EmployeeDao dao;
    private OrganizationDao organizationDao;
    private RoleDao roleDao;
    private CapabilityDao capabilityDao;

    public EmployeeRepository() {
        this.dao = new EmployeeDao();
        this.organizationDao = new OrganizationDao();
        this.roleDao = new RoleDao();
        this.capabilityDao = new CapabilityDao();
    }

    /**
     * 社員番号を基に社員情報を全て取得する。
     *
     * @param no 社員番号
     * @return 社員情報
     */
    // @UT
    public EmployeeDomain get(String no) {
        Employee employee = dao.get(no);

        // Domain を準備
        EmployeeDomain domain = buildDomainHasRelation(employee);

        return domain;
    }

    /**
     * 社員番号を基に組織と社員情報を取得する。
     *
     * @param no 社員番号
     * @return 社員情報
     */
    public EmployeeDomain getSimple(String no) {
        Employee employee = dao.get(no);

        // 所属する組織情報を取得
        Organization organization = organizationDao.get(employee.getOrganization());

        // Domain を準備
        EmployeeDomain domain = new EmployeeDomain(employee);
        domain.setOrganization(organization);

        return domain;
    }

    /**
     * 社員情報の一覧を取得する。<br />
     * 社員番号の昇順に一覧を取得する。
     *
     * @return 社員情報一覧
     */
    // @UT
    public List<EmployeeDomain> findAll() {
        List<Employee> employees = dao.findAll(true);
        return employees.stream().map(t -> buildDomainHasRelation(t)).collect(Collectors.toList());
    }

    /**
     * 想定年収順に社員情報の一覧を取得する。
     *
     * @param ascending 想定年収の昇順（低い順）なら true
     * @return 社員情報一覧
     */
    // @UT
    public List<EmployeeDomain> findAllOrderByAnnualSalary(final boolean ascending) {
        List<EmployeeDomain> domains = findAll();

        // 並び替え
        Collections.sort(domains, (ascending ? ANNUAL_TOTAL_SALARY_PLAN_ASC : ANNUAL_TOTAL_SALARY_PLAN_DESC));

        return domains;
    }

    private EmployeeDomain buildDomainHasRelation(Employee employee) {

        // 所属する組織情報を取得
        Organization organization = organizationDao.get(employee.getOrganization());

        // 各等級情報を取得
        Role role = roleDao.get(employee.getRoleRank());
        Capability capability = capabilityDao.get(employee.getCapabilityRank());

        // Domain を準備
        EmployeeDomain domain = new EmployeeDomain(employee);
        domain.setOrganization(organization);
        domain.setRole(role);
        domain.setCapability(capability);

        return domain;
    }

    /**
     * 指定年月の全社員の総支給額の合計を取得する。
     *
     * @param yearMonth 算出対象の年月 (e.g. 201504)
     * @return 全社員の総支給額合計
     */
    // @UT
    public int sumTotalSalaries(int yearMonth) {
        List<EmployeeDomain> domains = findAll();
        return domains.stream().mapToInt(t -> t.getTotalSalary(yearMonth)).sum();
    }

    /**
     * 指定年月の全社員の手取り額の平均を取得する。
     *
     * @param yearMonth 算出対象の年月 (e.g. 201504)
     * @return 全社員の手取り額平均
     */
    // @UT
    public int averageTakeHome(int yearMonth) {
        List<EmployeeDomain> domains = findAll();
        return (int) domains.stream().mapToInt(t -> t.getTakeHomeAmount(yearMonth)).average().getAsDouble();
    }

    /**
     * 想定年収が指定額を超えている社員数を取得する。
     *
     * @param condition 視定額
     * @return 該当する社員数
     */
    // @UT
    public int countByOverAnnualSalary(int condition) {
        List<EmployeeDomain> domains = findAll();
        return (int) domains.stream().filter(t -> t.overAnnualTotalSalaryPlan(condition)).count();
    }

    /**
     * 勤続月数の最大 or 最小の社員情報を取得する。
     *
     * @param selectMax 最大を求める場合は true
     * @return 社員情報。社員が一人も存在しない場合は null
     */
    // @UT
    public EmployeeDomain getByDurationMonth(boolean selectMax) {
        List<EmployeeDomain> domains = findAll();
        Optional<EmployeeDomain> result = domains.stream().max((selectMax ? DURATION_MONTH_ASC : DURATION_MONTH_DESC));
        return (result.isPresent() ? result.get() : null);
    }

    /**
     * 指定された組織コードに所属する社員数を取得する。
     *
     * @param organizationCode 組織コード
     * @return 所属する社員数
     */
    public long countByOrganization(String organizationCode) {
        return dao.countByOrganization(organizationCode);
    }

    public void setDao(EmployeeDao dao) {
        this.dao = dao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setCapabilityDao(CapabilityDao capabilityDao) {
        this.capabilityDao = capabilityDao;
    }
}
