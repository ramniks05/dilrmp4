package in.gov.dilrmp.utils;

public class NumberFormatterUtil {

    public static String formatWithCommas(Integer number) {
        if (number == null) {
            return null;
        }

        String amount = String.valueOf(number);
        StringBuilder stringBuilder = new StringBuilder();
        char amountArray[] = amount.toCharArray();
        int a = 0, b = 0;

        for (int i = amountArray.length - 1; i >= 0; i--) {
            if (a < 3) {
                stringBuilder.append(amountArray[i]);
                a++;
            } else if (b < 2) {
                if (b == 0) {
                    stringBuilder.append(",");
                    stringBuilder.append(amountArray[i]);
                    b++;
                } else {
                    stringBuilder.append(amountArray[i]);
                    b = 0;
                }
            }
        }

        return stringBuilder.reverse().toString();
    }

}
