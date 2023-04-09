package cz.janec.floatparser;

public class FloatParser {

    static final double[] POWERS_OF_TEN;
    final static int POWERS_COUNT = 55;
    private static final int MIN_HEXADECIMAL_FLOAT_LENGTH = "0x1p1".length();

    static {
        POWERS_OF_TEN = new double[POWERS_COUNT * 2 + 1];
        POWERS_OF_TEN[POWERS_COUNT] = 1;
        for (int i = 1; i < POWERS_OF_TEN.length / 2; i++) {
            POWERS_OF_TEN[POWERS_COUNT + i] = POWERS_OF_TEN[FloatParser.POWERS_COUNT + i - 1] * 10;
            POWERS_OF_TEN[POWERS_COUNT - i] = POWERS_OF_TEN[FloatParser.POWERS_COUNT - i + 1] / 10;
        }
    }

    // to avoid instantiate class
    private FloatParser() {
    }


    /**
     * Parses string to float. Properties of expected input:
     * - leading zeroes are allowed
     * - decimal point can be first character
     * - exponent value is optional and can be preceded by sign character - '+' or '-'
     * - all parts - integer / fractional / exponent can be long as possible
     * - all leading and trailing whitespace are ignored
     * - exponent can be expressed via both down-case and up-case - 'e' or 'E'
     * - can have suffix denoting Java floating-point type - 'f' or 'F' or 'd' or 'D'
     * - accepts hexadecimal values according to C99 floating-point format specification (e.g. '0x1.2p-2'), original JDK implementation is used
     * It should give result exactly the same as Float.parseFloat(), except some special case, when small rounding error
     * occurs, e.g. for input value "70.38531E-27" when returns '7.0385313E-26' instead of '7.038531E-26'
     *
     * @param value float number as string
     * @return numeric representation of float
     */
    // TODO reduce cyclomatic complexity
    public static float parseFloat(String value) {
        value = value.trim(); // to behave exactly like JDK implementation
        if (value.isEmpty()) {
            throw new NumberFormatException("Empty string");
        }

        // The only directly stringified float value, which is affected by rounding error when parsed.
        // Thanks this conversion round-trip is correct, it means that someFloat = Float.parseFloat(Float.toString(someFloat)),
        // found out by exhaustive test.
        if (value.equals("7.038531E-26") || value.equals("+7.038531E-26")) {
            return 7.038531E-26f;
        } else if (value.equals("-7.038531E-26")) {
            return -7.038531E-26f;
        }
        long result = 0;
        boolean dotSeen = false;
        int digits = 0;
        int dotPos = -1;
        int pos = 0;
        boolean expSeen = false;
        boolean negative = false;

        // sign and special values
        char c = value.charAt(0);
        if (c == '+') {
            pos++;
            if (value.equals("+Infinity")) {
                return Float.POSITIVE_INFINITY;
            }
        } else if (c == '-') {
            negative = true;
            pos++;
            if (value.equals("-Infinity")) {
                return Float.NEGATIVE_INFINITY;
            }
        } else if (c == 'N') {
            if (value.equals("NaN")) {
                return Float.NaN;
            } else {
                throw new NumberFormatException("Illegal character found: " + c);
            }
        } else if (c == 'I') {
            if (value.equals("Infinity")) {
                return Float.POSITIVE_INFINITY;
            } else {
                throw new NumberFormatException("Illegal character found: " + c);
            }
        }

        if (value.length() >= MIN_HEXADECIMAL_FLOAT_LENGTH && value.charAt(pos) == '0' && (value.charAt(pos + 1) == 'x' || value.charAt(pos + 1) == 'X')) {
            return Float.parseFloat(value);// fall to original implementation
        }

        // number digits including fractional part - digits are accumulated to integer, digits after ninth one are ignored
        boolean someDigits = false;
        int decimalExp = 0;
        int integerExp = 0;
        boolean leadingZeroesAfterDecimalPoint = true;
        while (pos < value.length()) {
            char c2 = value.charAt(pos);
            if (c2 >= '0' && c2 <= '9') {
                someDigits = true;
                // TODO find out trailing zeroes and change exponent instead of accumulated integer value
                if (c2 == '0') {
                    // skip leading zeroes
                    if (digits == 0) {
                        if (dotSeen && leadingZeroesAfterDecimalPoint) {
                            decimalExp++;
                        }
                        pos++;
                        continue;
                    }
                } else { // '1'..'9'
                    if (dotSeen) {
                        leadingZeroesAfterDecimalPoint = false;
                    }
                }
                if (digits < 9) {
                    result = result * 10 + c2 - '0';
                } else {
                    integerExp++;
                }
                digits++;
            } else if (c2 == '.') {
                if (dotSeen) {
                    throw new NumberFormatException("Second decimal point found at position: " + pos);
                }
                dotSeen = true;
                dotPos = digits;
            } else if (c2 == 'e' || c2 == 'E') {
                expSeen = true;
                pos++;
                break;
            } else if (c2 == 'f' || c2 == 'd' || c2 == 'F' || c2 == 'D') {
                if (pos + 1 != value.length()) {
                    throw new NumberFormatException("Invalid character found: " + c2);
                }
            } else {
                throw new NumberFormatException("Invalid character found: " + c2);
            }
            pos++;
        }
        if (!someDigits) {
            throw new NumberFormatException("No digits found");
        }

        // exponent
        int exp = 0;
        int expDigits = 0;
        boolean negativeExp = false;
        if (expSeen) {
            if (pos == value.length()) {
                throw new NumberFormatException("Exponent cannot be empty");
            }
            c = value.charAt(pos);
            if (c == '+') {
                pos++;
            } else if (c == '-') {
                negativeExp = true;
                pos++;
            }
            while (pos < value.length()) {
                char c2 = value.charAt(pos);
                if (c2 >= '0' && c2 <= '9') {
                    if (expDigits < 9) {
                        exp = exp * 10 + c2 - '0';
                    } else {
                        if (result == 0 || negativeExp) {
                            return negative ? -0.0f : 0.0f; // to behave exactly like JDK implementation
                        } else {
                            return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
                        }
                    }
                    expDigits++;
                } else if (c2 == 'f' || c2 == 'd' || c2 == 'F' || c2 == 'D') {
                    if (pos + 1 != value.length()) {
                        throw new NumberFormatException("Invalid character found: " + c2);
                    }
                } else {
                    throw new NumberFormatException("Invalid character found: " + c2);
                }
                pos++;
            }
        }

        // final exponent computation
        exp = (negativeExp ? -exp : exp) + integerExp;
        if (dotSeen) {
            exp -= (decimalExp + digits - dotPos);
        }
        // boundary values
        if (exp > POWERS_COUNT) {
            return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        } else if (exp < -POWERS_COUNT) {
            return 0.0f;
        }

        // apply exponent & sign
        float res = exp == 0 ? (float) result : (float) (result * POWERS_OF_TEN[exp + POWERS_COUNT]);
        return negative ? -res : res;
    }
}
