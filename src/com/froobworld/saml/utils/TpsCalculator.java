package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TpsCalculator {
    private int sampleTime;
    private long lastTick;

    private List<Long> deltas;

    public TpsCalculator(int sampleTime, Saml saml) {
        this.sampleTime = sampleTime;
        lastTick = System.currentTimeMillis();
        deltas = new ArrayList<Long>();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(saml, new Runnable() {
            @Override
            public void run() {
                task();
            }
        }, 1, 1);
    }

    private void task() {
        if (deltas.size() == sampleTime) {
            deltas.remove(0);
        }
        deltas.add(System.currentTimeMillis() - lastTick);
        lastTick = System.currentTimeMillis();
    }

    public double getTPS() {
        if(deltas.size() == 0) {
            return 20;
        }

        long total = 0;
        for (Long l : deltas) {
            total += l;
        }

        return round((double) deltas.size() / ((double) total) * 1000);
    }

    private double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

}
