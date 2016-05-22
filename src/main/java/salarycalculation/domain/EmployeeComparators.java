package salarycalculation.domain;

import java.util.Comparator;

/**
 * 社員の並び替えを行うためのユーティリティ。
 *
 * @author naotake
 */
public class EmployeeComparators {

    /**
     * 想定年収の昇順に並び替える{@link Comparator}。
     */
    public static final AnnualTotalSalaryPlanComparator ANNUAL_TOTAL_SALARY_PLAN_ASC =
            new AnnualTotalSalaryPlanComparator(true);

    /**
     * 想定年収の降順に並び替える{@link Comparator}。
     */
    public static final AnnualTotalSalaryPlanComparator ANNUAL_TOTAL_SALARY_PLAN_DESC =
            new AnnualTotalSalaryPlanComparator(false);

    /**
     * 勤続月数の昇順に並び替える{@link Comparator}。
     */
    public static final DurationMonthComparator DURATION_MONTH_ASC = new DurationMonthComparator(true);

    /**
     * 勤続月数の降順に並び替える{@link Comparator}。
     */
    public static final DurationMonthComparator DURATION_MONTH_DESC = new DurationMonthComparator(false);

    /**
     * インスタンス化を抑制。
     */
    private EmployeeComparators() {
        // NOP
    }

    private static final class AnnualTotalSalaryPlanComparator implements Comparator<EmployeeDomain> {

        private final boolean ascending;

        public AnnualTotalSalaryPlanComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(EmployeeDomain o1, EmployeeDomain o2) {
            if (ascending) {
                return (o1.getAnnualTotalSalaryPlan() - o2.getAnnualTotalSalaryPlan());
            } else {
                return (o2.getAnnualTotalSalaryPlan() - o1.getAnnualTotalSalaryPlan());
            }
        }
    }

    private static final class DurationMonthComparator implements Comparator<EmployeeDomain> {

        private final boolean ascending;

        public DurationMonthComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(EmployeeDomain o1, EmployeeDomain o2) {
            if (ascending) {
                return (o1.getDurationMonth() - o2.getDurationMonth());
            } else {
                return (o2.getDurationMonth() - o1.getDurationMonth());
            }
        }
    }
}
