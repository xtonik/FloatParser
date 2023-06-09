package cz.janec.floatparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

// TODO parametrize parsers
public class FloatParserTest {

    @ParameterizedTest(name = "{index}.  {0}")
    @ValueSource(strings = {
            "Infinity",
            "0x1p1", "0x1.2p3", "0x1.2P3", "0xAA.BCDp-3", "0xAA.BC0DP+3", // C99 floating-point hexadecimal
            "1", "1.", "1.0", "1e0", "1e-0", // ones
            "1f", "1F", "1D", "1d", // ones with Java data type literal suffixes
            "1e2f", "1e2F", "1e2D", "1e2d",
            ".1e2f", ".1e2F", ".1e2D", ".1e2d",
            "1.1e2f", "1.1e2F", "1.1e2D", "1.1e2d",
            "1.e2f", "1.e2F", "1.e2D", "1.e2d",
            "0", "0.", "0.0", "0.0e38", "0.0e-38", "0e999", "1.111111e-500", // zeroes
            "10", "15", "111", "123", "1234", "9999999", // ordinary integers
            // integers around 24 bits - float mantissa bit length:
            "16777217", "16777216", "16777215", "999999999", // 2^24+1, 2^24, 2^24-1, >> 2^24
            "1.23", "12.3", "123.0", "0.3", "0.123", "1645.34", // some ordinary decimal values
            ".0", ".1", ".01", ".0e2", ".1e2", ".01e2", // leading dot
            "1e+1", "0e+1", ".1e+2", "1.1e+2", "0.0e+2", "1.0e+2", "0.1e+2",
            "1e50", "1e100", "1e200", "123.456e50", "123.456e100", "123.456e200",
            "12345678901234567890", "1234567890.1234567890",
            "1e1000000000000", "0e1000000000000", "1e-1000000000000", "0e-1000000000000", // huge exponent
            "00000000000004E0", "00000000000004E-0", "00000000000004E+0",
            "000000000000012345678999", "000000000000012345678999E0", "000000000000012345678999E-0", "000000000000012345678999E+0",
            "00000000000004E2", "00000000000004E-2", "00000000000004E+2",
            "000000000000012345678999E2", "000000000000012345678999E-2", "000000000000012345678999E+2",
            "0.0000000000000001e15", "0.0000000000000001e-15",
            "1000000000000000e-15", "1000000000000000e+15",

            "1000000000000001e-10", "1000000000000001e+10", // digit under precision
            "1.00000000000001E0", "1.00000000000001",
            "1.10000000000001E0", "1.10000000000001",
            "1.4e-45", "14e-46", "140e-47", // Float.MIN_VALUE
            "2e-45", "20e-46", // above Float.MIN_VALUE
            "7.038531E-26", // rounding error suppressed by exact match string
            // values very similar to those, which are affected by rounding error
            "70385310001E-36", "7038531000001E-38", "7038531000000001E-41",
            "70385311E-33", "703853111E-34", "70.35531E-30", "703.5531E-30",
            "0.70385307E-25", "7.0385307E-26", "7.0385308E-26", "7.0385309E-26", "7.0385311E-26", "7.0385312E-26", "7.0385313E-26",

            "0.12345678912345677", "0.1234567891234567768576901869437278946862", "0.1234567891234567768576901869437278946862"
    })
    public void valid(String s) {
        assertAll(
                () -> assertEquals(Float.parseFloat(s), FloatParser.parseFloat(s)),
                () -> assertEquals(Float.parseFloat("-" + s), FloatParser.parseFloat("-" + s)),
                () -> assertEquals(Float.parseFloat("+" + s), FloatParser.parseFloat("+" + s))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = { // values are parsed inaccurately - results is the smallest greater value than correct one
            "70.38531E-27", "703.8531E-28", "7038531E-32",
            "70385310E-33", "703853100E-34", "7038531000E-35", "70385310000E-36", "703853100000000E-40",
            "70385310000000001E-42",
            // TODO add another rounding error cases
            "1.9146878E-6", "1241481705", "1.5714405E26","906001943","1.066626E29"
    })
    public void validRoundError(String s) {
        assertAll(
                () -> assertEquals(Float.floatToIntBits(Float.parseFloat(s)) + 1, Float.floatToIntBits(FloatParser.parseFloat(s))),
                () -> assertEquals(Float.floatToIntBits(Float.parseFloat("-" + s)) + 1, Float.floatToIntBits(FloatParser.parseFloat("-" + s))),
                () -> assertEquals(Float.floatToIntBits(Float.parseFloat("+" + s)) + 1, Float.floatToIntBits(FloatParser.parseFloat("+" + s)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"NaN", "+NaN", "-NaN"})
    public void notANumber(String notANumber) {
        assertAll(
                () -> assertEquals(Float.NaN, Float.parseFloat(notANumber)),
                () -> assertEquals(Float.NaN, FloatParser.parseFloat(notANumber))
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @ParameterizedTest
    @ValueSource(strings = {
            "NaN2", "Infinity2", // falsify seems to be special value
            "", " ", "  ", // empty strings
            "e", ".", ".0e", "0e", ".e", "0.0e", // some number parts are missing
            "0.0.0", "0.0.1", "0.1e1.", "0.1e.1", // double dot
            "1 1", // whitespace in the middle
            "0x0", "0xFF", // hexadecimal
            "1dd", "1ff", "1ll", "1DD", "1FF", "1LL", "1df", "1fd", // suffixes for C99 floating-point data types
            "z", // invalid character
            "1e6fz", "1e6dz", // java types suffix is not last character
    })
    public void invalid(String s) {
        assertAll(
                () -> assertThrows(NumberFormatException.class, () -> Float.parseFloat(s)),
                () -> assertThrows(NumberFormatException.class, () -> FloatParser.parseFloat(s)),

                () -> assertThrows(NumberFormatException.class, () -> Float.parseFloat("-" + s)),
                () -> assertThrows(NumberFormatException.class, () -> FloatParser.parseFloat("-" + s)),

                () -> assertThrows(NumberFormatException.class, () -> Float.parseFloat("+" + s)),
                () -> assertThrows(NumberFormatException.class, () -> FloatParser.parseFloat("+" + s))
        );
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "DataFlowIssue"})
    @Test
    public void nullValue() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> Float.parseFloat(null)),
                () -> assertThrows(NullPointerException.class, () -> FloatParser.parseFloat(null))
        );
    }

    // Conversion round-trip from float to String and vice versa, i.e. float_value = Float.parseFloat(Float.toString(float_value)).
    @Disabled // exhaustive test - takes too much time ~ 1 hour, it should be verified at least once for each platform
    @Test
    // TODO test opposite direction? String -> float -> String
    public void allStringifiedFloats() {
        long start = System.currentTimeMillis();
        int from = Integer.MIN_VALUE;
        int to = Integer.MAX_VALUE;
        for (int i = from; i < to; i++) {
            Float f = Float.intBitsToFloat(i);
            String s = f.toString();
            Float f2 = Float.parseFloat(s);
            Float f3 = FloatParser.parseFloat(s);
            if (!f.equals(f3) || !f2.equals(f3)) {
                // the only values are:  +/- 7.038531E-26, but those are treated by exact if condition within FloatParser.parseFloat()
                System.out.println("original " + i);
                System.out.println("parsed " + s);
                System.out.println(f2 + " " + Integer.toHexString(Float.floatToIntBits(f2)));
                System.out.println(f3 + " " + Integer.toHexString((Float.floatToIntBits(f3))));
                System.out.println("====");
                // go through all values even some incorrect ones has been already found
            }
            if ((i & 0xFFFFFF) == 0xFFFFFF) { // print progress info each 0.4% percent done work
                long duration = System.currentTimeMillis() - start;
                long elapsed = (long) i - from;
                long total = (long) to - from;
                float percent = (float) elapsed / total * 100;
                int remaining = (int) (duration / percent * (100 - percent)) / 1000;
                System.out.println("last value: " + i + " , elapsed " + percent + "%, estimated remaining time: " + remaining + " s");
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"1 ", " 1 ", " 1",})
    public void whitespacesAroundAreIgnored(String s) {
        assertAll(
                () -> assertEquals(1, Float.parseFloat(s)),
                () -> assertEquals(1, FloatParser.parseFloat(s))
        );
    }

    @Test
    public void randomDataAbovePrecision() {
        Random r = new Random();
        for (int i = 0; i < 100_000; i++) {
            String integral = Long.toString(r.nextLong() % 200);
            String fractional = Long.toString(Math.abs(r.nextLong()));
            String exponent = Long.toString(r.nextLong() % 254 - Float.MIN_EXPONENT);
            String s = integral + "." + fractional + "E" + exponent;
            assertEquals(Float.parseFloat(s), FloatParser.parseFloat(s));
        }
    }
}