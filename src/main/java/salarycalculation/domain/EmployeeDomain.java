package salarycalculation.domain;

import static salarycalculation.domain.CapabilityRank.PL;
import static salarycalculation.domain.CapabilityRank.PM;

import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;

import salarycalculation.entity.Capability;
import salarycalculation.entity.Employee;
import salarycalculation.entity.Organization;
import salarycalculation.entity.Role;

/**
 * 社員情報ドメイン。
 *
 * @author naotake
 */
public class EmployeeDomain {

    /** 社員情報 */
    private Employee entity;

    /** 組織情報 */
    private Organization organization;
    /** 役割等級 */
    private Role role;
    /** 能力等級 */
    private Capability capability;
    private CapabilityRank capabilityRank;

    private WorkRepository workRepository;

    /** 業務日付ドメイン */
    private BusinessDateDomain businessDateDomain;

    public EmployeeDomain(Employee entity) {
        this.entity = entity;
        this.capabilityRank = CapabilityRank.codeOf(entity.getCapabilityRank());
        this.workRepository = new WorkRepository();
        this.businessDateDomain = new BusinessDateDomain();
    }

    /**
     * 社員番号を取得する。
     *
     * @return 社員番号
     */
    public int getNo() {
        return entity.getNo();
    }

    /**
     * 入社年月日を基に勤続年数を取得する。<br />
     * 勤続年数が 1年未満の場合は 0 を返す。
     *
     * @return 勤続年数
     */
    // @UT
    public int getDurationYear() {
        int durationMonth = getDurationMonth();
        return (durationMonth / 12);
    }

    /**
     * 入社年月日を基に勤続月数を取得する。<br />
     * ここでいう勤続月数とは、現在日時点で勤続<b>何ヶ月目</b>かを表す。
     *
     * <pre>
     * 例えば
     * ・入社年月日: 2013/04/01
     * ・現在日時: 2014/04/01
     *
     * この場合、勤続月数は 13ヶ月目となる。
     *
     * ・現在日時: 2014/03/01 や 2014/03/31
     *
     * この場合、勤続月数は 12ヶ月目となる。
     * </pre>
     *
     * @return 勤続月数
     */
    // @UT
    public int getDurationMonth() {
        Calendar joinDateCal = Calendar.getInstance();
        joinDateCal.setTime(entity.getJoinDate());
        joinDateCal = DateUtils.truncate(joinDateCal, Calendar.HOUR);

        Calendar now = businessDateDomain.getNowAsCalendar();
        now = DateUtils.truncate(now, Calendar.HOUR);

        int count = 0;
        while (joinDateCal.before(now) || DateUtils.isSameDay(joinDateCal, now)) {
            joinDateCal.add(Calendar.MONTH, 1);
            count++;
        }

        if (count < 0) {
            count = 0;
        }

        return count;
    }

    /**
     * 指定年月の給料の手取り額を取得する。
     *
     * @param workYearMonth 稼動年月 (e.g. 201504)
     * @return 給料の手取り額
     */
    // @UT
    public int getTakeHomeAmount(int workYearMonth) {
        // 総支給額
        int totalSalary = getTotalSalary(workYearMonth);

        // 控除額
        int deduction = deduction();

        // 差引給与額を求める
        int takeHome = totalSalary - deduction;

        return takeHome;
    }

    /**
     * 指定年月の給料の総支給額を取得する。<br />
     * 総支給額の内訳は下記の通り。
     * <p />
     * 総支給額 = 基準内給与 (基本給 + 諸手当) + 基準外給与
     *
     * @param workYearMonth 稼動年月 (e.g. 201504)
     * @return 給料の総支給額
     */
    // @UT
    public int getTotalSalary(int workYearMonth) {
        // 基準内給与
        int standardSalary = standardSalary(getAllowance());

        // 基準外給与
        int nonStandardSalary = getOvertimeAmount(workYearMonth);

        // 総支給額
        int totalSalary = standardSalary + nonStandardSalary;

        return totalSalary;
    }

    /**
     * 現在時点での諸手当を取得する。
     *
     * @return 諸手当
     */
    public int getAllowance() {
        // 諸手当を求める
        int allowance = entity.getCommuteAmount() + entity.getRentAmount();

        // 能力等級が 'PL' or 'PM' の場合、別途手当が出る
        allowance += capabilityRank.getAllowance();

        // 勤続年数が丸 3年目、5年目、10年目, 20年目の場合、別途手当が出る
        if ((getDurationMonth() % 12) == 0) {
            switch (getDurationYear()) {
            case 3:
                allowance += 3000;
                break;
            case 5:
                allowance += 5000;
                break;
            case 10:
                allowance += 10000;
                break;
            case 20:
                allowance += 20000;
                break;
            default:
                break;
            }
        }

        return allowance;
    }

    /**
     * 指定年月の残業代を取得する。
     *
     * @param workYearMonth 稼動年月 (e.g. 201504)
     * @return 残業代
     */
    // @UT
    public int getOvertimeAmount(int workYearMonth) {

        // 能力等級が 'PL' or 'PM' の場合、残業代は出ない
        if (capabilityRank == PL || capabilityRank == PM) {
            return 0;
        }

        // 稼動情報を取得
        WorkDomain work = workRepository.getByYearMonth(entity.getNo(), workYearMonth);

        return work.calcOvertimeAmount(entity.getWorkOverTime1hAmount());
    }

    /**
     * 現在の各等級を基に想定年収を取得する。<br />
     * 想定年収の内訳は下記の通り。
     * <p />
     * 想定年収 = 基準内給与 (基本給 + 諸手当) * 12
     * <p />
     * ただし、以下の諸手当は年収には含まれない。
     * <ul>
     * <li>通勤手当</li>
     * <li>住宅手当</li>
     * <li>勤続手当</li>
     * </ul>
     *
     * @return 想定年収
     */
    // @UT
    public int getAnnualTotalSalaryPlan() {
        // 基準内給与
        int standardSalary = standardSalary(capabilityRank.getAllowance());

        // 想定年収
        int annualTotalSalaryPlan = (standardSalary) * 12;

        return annualTotalSalaryPlan;
    }

    /**
     * 想定年収が指定額以上かどうかを判定する。
     *
     * @param condition 判定対象額
     * @return 指定額以上の場合は true
     */
    public boolean overAnnualTotalSalaryPlan(int condition) {
        return getAnnualTotalSalaryPlan() >= condition;
    }

    private int baseSalary() {
        return role.getAmount() + capability.getAmount();
    }

    private int standardSalary(int allowance) {
        return baseSalary() + allowance;
    }

    private int deduction() {
        return entity.getHealthInsuranceAmount() + entity.getEmployeePensionAmount()
               + entity.getIncomeTaxAmount() + entity.getInhabitantTaxAmount();
    }

    public Employee getEntity() {
        return entity;
    }

    public void setEntity(Employee entity) {
        this.entity = entity;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Capability getCapability() {
        return capability;
    }

    public void setCapability(Capability capability) {
        this.capability = capability;
    }

    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public void setBusinessDateDomain(BusinessDateDomain businessDateDomain) {
        this.businessDateDomain = businessDateDomain;
    }
}
