package in.gov.dilrmp.models.unspentBalance;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
public class UnspentBalanceRow {

    public static final String TYPE_GROUP = "GROUP";
    public static final String TYPE_ITEM = "ITEM";
    public static final String TYPE_SUB_ITEM = "SUB_ITEM";
    public static final String TYPE_SUBTOTAL = "SUBTOTAL";
    public static final String TYPE_TOTAL = "TOTAL";

    private final String rowType;
    private final String code;
    private final String groupCode;
    private final String parentCode;
    private final String serialNo;
    private final String label;
    private final BigDecimal amount;

    public boolean isEditable() {
        return TYPE_ITEM.equals(rowType) || TYPE_SUB_ITEM.equals(rowType);
    }

    public boolean isGroup() {
        return TYPE_GROUP.equals(rowType);
    }

    public boolean isTotal() {
        return TYPE_SUBTOTAL.equals(rowType) || TYPE_TOTAL.equals(rowType);
    }

    public String getAmountText() {
        return amount == null ? "" : amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
