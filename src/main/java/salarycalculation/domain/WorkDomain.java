package salarycalculation.domain;

import java.math.BigDecimal;

import salarycalculation.entity.Work;

/**
 * 稼動情報ドメイン。
 *
 * @author naotake
 */
public class WorkDomain {

    /** 深夜残業手当の割増率 */
    private static final double LATE_NIGHT_OVER_RATE = 1.1;

    /** 休日勤務手当の割増率 */
    private static final double HOLIDAY_WORK_RATE = 1.2;

    /** 休日深夜勤務手当の割増率 */
    private static final double HOLIDAY_LATE_NIGHT_OVER_RATE = 1.3;

    /** 稼動情報 */
    private Work entity;

    public WorkDomain(Work entity) {
        this.entity = entity;
    }

    /**
     * 残業代を計算する。<br />
     * 対象となる値は下記の通り。
     * <ul>
     * <li>時間外手当</li>
     * <li>深夜手当</li>
     * <li>休日手当</li>
     * <li>休日深夜手当</li>
     * </ul>
     *
     * @param workOverTime1hAmount ベースとなる 1 時間当たりの時間外手当金額
     * @return 計算した残業代
     */
    public int calcOvertimeAmount(long workOverTime1hAmount) {

        // 時間外手当
        int workOverTimeAllowance = workOverTimeAllowance(workOverTime1hAmount);

        // 深夜手当
        int lateNightOverTimeAllowance = lateNightOverTimeAllowance(workOverTime1hAmount);

        // 休日手当
        int holidayWorkTimeAllowance = holidayWorkTimeAllowance(workOverTime1hAmount);

        // 休日深夜手当
        int holidayLateNightOverTimeAllowance = holidayLateNightOverTimeAllowance(workOverTime1hAmount);

        int overtimeAmount = workOverTimeAllowance + lateNightOverTimeAllowance +
                             holidayWorkTimeAllowance + holidayLateNightOverTimeAllowance;

        return overtimeAmount;
    }

    private int workOverTimeAllowance(long workOverTime1hAmount) {
        BigDecimal amount = BigDecimal.valueOf(workOverTime1hAmount)
                                      .multiply(entity.getWorkOverTime());
        return amount.intValue();
    }

    private int lateNightOverTimeAllowance(long workOverTime1hAmount) {
        BigDecimal amount = BigDecimal.valueOf(workOverTime1hAmount * LATE_NIGHT_OVER_RATE)
                                      .multiply(entity.getLateNightOverTime());
        return amount.intValue();
    }

    private int holidayWorkTimeAllowance(long workOverTime1hAmount) {
        BigDecimal amount = BigDecimal.valueOf(workOverTime1hAmount * HOLIDAY_WORK_RATE)
                                      .multiply(entity.getHolidayWorkTime());
        return amount.intValue();
    }

    private int holidayLateNightOverTimeAllowance(long workOverTime1hAmount) {
        BigDecimal amount = BigDecimal.valueOf(workOverTime1hAmount * HOLIDAY_LATE_NIGHT_OVER_RATE)
                                      .multiply(entity.getHolidayLateNightOverTime());
        return amount.intValue();
    }
}
