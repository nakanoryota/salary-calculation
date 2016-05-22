package salarycalculation.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 能力等級を表す列挙型。
 *
 * @author naotake
 */
public enum CapabilityRank {

    PL("PL", 10000),

    PM("PM", 30000),

    OTHER("", 0);

    private final String code;
    private int allowance;

    private CapabilityRank(final String code, int allowance) {
        this.code = code;
        this.allowance = allowance;
    }

    private static final Map<String, CapabilityRank> CODE_MAP;
    static {
        CODE_MAP = new HashMap<String, CapabilityRank>(2);
        CODE_MAP.put(PL.code, PL);
        CODE_MAP.put(PM.code, PM);
    }

    public static CapabilityRank codeOf(String code) {
        if (CODE_MAP.containsKey(code)) {
            return CODE_MAP.get(code);
        }
        return OTHER;
    }

    public int getAllowance() {
        return allowance;
    }
}
