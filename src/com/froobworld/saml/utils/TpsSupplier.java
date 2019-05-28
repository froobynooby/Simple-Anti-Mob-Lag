package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Supplier;

public class TpsSupplier implements Supplier<Double> {
    private boolean useNms;
    private TpsCalculator tpsCalculator;

    public TpsSupplier(Saml saml) {
        useNms = NmsUtils.getTPS() != null;
        if(!useNms) {
            Saml.logger().info("Could not access NMS for TPS. We will have to use a less accurate approximation.");
            tpsCalculator = new TpsCalculator(1200, saml);
        }
    }

    @Override
    public Double get() {
        BigDecimal bd = new BigDecimal(useNms ? NmsUtils.getTPS() : tpsCalculator.getTPS());
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }
}
