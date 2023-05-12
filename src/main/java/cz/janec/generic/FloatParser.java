package cz.janec.generic;

import java.math.BigDecimal;

public class FloatParser implements Parser<Float> {
  /*  private static class InstanceHolder {
        public static FloatParser instance = new FloatParser();
    }

    private FloatParser(){}

    public static FloatParser getInstance() {
        return InstanceHolder.instance;
    }
*/
    // ================================
    private static FloatParser INSTANCE;

    /**
     * @return the instance of the no-op renderer
     */
    public static Parser<Float> instance() {
        if (INSTANCE == null) {
            INSTANCE = new FloatParser();
        }
        return INSTANCE;
    }

    @Override
    public Float parse(CharSequence s) {
        return null;
    }
}
