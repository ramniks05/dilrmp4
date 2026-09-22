package in.gov.dilrmp.utils;

import org.springframework.stereotype.Component;

@Component
public class IndianNumberConverter {
    private static final String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    private static final String[] teens = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

    public static String convertToWords(double number) {
        long integralPart = (long) number;
        double fractionalPart = number - integralPart;

        StringBuilder result = new StringBuilder();

        // Convert the integral part to words
        result.append(convertToWords((int) integralPart)).append(" ");

        // Convert the fractional part to words
        String fractionalWords = convertFractionalToWords(fractionalPart);
        result.append(fractionalWords).append(" Rupees.");

        return result.toString().trim();
    }

    private static String convertToWords(int number) {
        StringBuilder result = new StringBuilder();
        int index, digit, next;
        String[] words = new String[10];

        String numberStr = String.valueOf(number);
        if (numberStr.length() > 0 && numberStr.length() <= 10) {
            for (index = numberStr.length() - 1; index >= 0; index--) {
                digit = Character.getNumericValue(numberStr.charAt(index));
                next = index > 0 ? Character.getNumericValue(numberStr.charAt(index - 1)) : 0;

                switch (numberStr.length() - index - 1) {
                    case 0:
                        words[0] = formatOther(digit, next, "");
                        break;
                    case 1:
                        words[1] = formatTenth(digit, Character.getNumericValue(numberStr.charAt(index + 1)));
                        break;
                    case 2:
                        words[2] = 0 != digit ? " " + ones[digit] + " Hundred" + (0 != Character.getNumericValue(numberStr.charAt(index + 1)) && 0 != Character.getNumericValue(numberStr.charAt(index + 2)) ? " and" : "") : "";
                        break;
                    case 3:
                        words[3] = formatOther(digit, next, "Thousand");
                        break;
                    case 4:
                        words[4] = formatTenth(digit, Character.getNumericValue(numberStr.charAt(index + 1)));
                        break;
                    case 5:
                        words[5] = formatOther(digit, next, "Lakh");
                        break;
                    case 6:
                        words[6] = formatTenth(digit, Character.getNumericValue(numberStr.charAt(index + 1)));
                        break;
                    case 7:
                        words[7] = formatOther(digit, next, "Crore");
                        break;
                    case 8:
                        words[8] = formatTenth(digit, Character.getNumericValue(numberStr.charAt(index + 1)));
                        break;
                    case 9:
                        words[9] = 0 != digit ? " " + ones[digit] + " Hundred" + (0 != Character.getNumericValue(numberStr.charAt(index + 1)) || 0 != Character.getNumericValue(numberStr.charAt(index + 2)) ? " and" : " Crore") : "";
                }
            }

            for (int i = words.length - 1; i >= 0; i--) {
                if (words[i] != null) {
                    result.append(words[i]);
                }
            }
        }

        return result.toString().trim();
    }

    private static String convertFractionalToWords(double fractionalPart) {
        // Convert the fractional part to words based on your requirements
        // For simplicity, this example converts each digit separately
        String fractionalStr = String.valueOf(fractionalPart).substring(2); // Remove "0." from the decimal
        StringBuilder fractionalWords = new StringBuilder();
        for (char digitChar : fractionalStr.toCharArray()) {
            int digit = Character.getNumericValue(digitChar);
            fractionalWords.append(ones[digit]);
        }
        return fractionalWords.toString().trim();
    }

    private static String formatOther(int digit, int next, String denom) {
        return (0 != digit && 1 != next ? " " + ones[digit] : "") + (0 != next || digit > 0 ? " " + denom : "");
    }

    private static String formatTenth(int digit, int next) {
        return 0 == digit ? "" : " " + (1 == digit ? teens[next] : tens[digit]);
    }
}
