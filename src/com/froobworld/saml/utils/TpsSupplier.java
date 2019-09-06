package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Queue;

public class TpsSupplier {
    private Saml saml;
    private final long tpsSampleSize;
    private final double tpsWeightingFactor;
    private final long tpsSmoothingSampleSize;
    private boolean useNmsTps;
    private final long standardDeviationSampleRate;
    private final long standardDeviationSampleSize;
    private int tpsTaskId;
    private int standardDeviationTaskId;
    private double lastTps;
    private double lastTpsStandardDeviation;
    private boolean startStandardDeviationCalc;

    public TpsSupplier(Saml saml) {
        this.saml = saml;
        this.lastTps = 20.0;
        this.tpsSampleSize = saml.getSamlConfig().getLong(ConfigKeys.CNF_TPS_SAMPLE_SIZE);
        this.tpsSmoothingSampleSize = saml.getSamlConfig().getLong(ConfigKeys.CNF_TPS_SMOOTHING_SAMPLE_SIZE);
        this.tpsWeightingFactor = saml.getSamlConfig().getDouble(ConfigKeys.CNF_TPS_WEIGHTING_FACTOR);
        this.useNmsTps = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_NMS_TPS);
        this.standardDeviationSampleRate = saml.getSamlConfig().getLong(ConfigKeys.CNF_TPS_DEVIATION_SAMPLE_RATE);
        this.standardDeviationSampleSize = saml.getSamlConfig().getLong(ConfigKeys.CNF_TPS_DEVIATION_SAMPLE_SIZE);
        if(!useNmsTps) {
            new CalculateTpsTask();
        } else if(NmsUtils.getTPS() == null) {
            Saml.logger().warning("Could not access NMS for TPS. We'll be using our own TPS calculator instead.");
            useNmsTps = false;
            new CalculateTpsTask();
        }
        new CalculateStandardDeviationTask();
    }


    public double getTps() {
        return Math.min(new BigDecimal(useNmsTps ? NmsUtils.getTPS() : lastTps).setScale(2, RoundingMode.HALF_UP).doubleValue(), 20.0);
    }

    public double getTpsStandardDeviation() {
        return new BigDecimal(lastTpsStandardDeviation).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public void cancelTasks() {
        Bukkit.getScheduler().cancelTask(tpsTaskId);
        Bukkit.getScheduler().cancelTask(standardDeviationTaskId);
    }

    private class CalculateTpsTask implements Runnable {
        private Queue<Long> deltas;
        private Queue<Double> rawTpsValues;
        private double totalTps;
        private long lastTimeStamp;
        private double totalWeightedDelta;
        private double totalWeightedTicks;
        private double weightingFactorFinalPower;

        private CalculateTpsTask() {
            deltas = new ArrayDeque<>((int) tpsSampleSize);
            rawTpsValues = new ArrayDeque<>((int) tpsSmoothingSampleSize);
            totalWeightedTicks = tpsWeightingFactor == 1 ? tpsSampleSize : new BigDecimal(1).subtract(new BigDecimal(tpsWeightingFactor).pow((int) (tpsSampleSize - 1))).divide(new BigDecimal(1).subtract(new BigDecimal(tpsWeightingFactor)), BigDecimal.ROUND_HALF_UP).doubleValue();
            weightingFactorFinalPower = new BigDecimal(tpsWeightingFactor).pow((int) (tpsSampleSize - 1)).doubleValue();
            tpsTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(saml, this, 0,  1);
        }


        @Override
        public void run() {
            if(lastTimeStamp == 0) {
                lastTimeStamp = System.currentTimeMillis();
                return;
            }
            if(deltas.size() != 0 && deltas.size() >= tpsSampleSize) {
                totalWeightedDelta -= weightingFactorFinalPower * deltas.remove();
            }
            long nextDelta = System.currentTimeMillis() - lastTimeStamp;
            deltas.add(nextDelta);
            totalWeightedDelta = tpsWeightingFactor * totalWeightedDelta;
            totalWeightedDelta += nextDelta;

            double nextTps = totalWeightedTicks / totalWeightedDelta * 1000;
            lastTimeStamp = System.currentTimeMillis();
            if(deltas.size() < tpsSampleSize) {
                return;
            } else {
                startStandardDeviationCalc = true;
            }

            if(rawTpsValues.size() != 0 && rawTpsValues.size() > tpsSmoothingSampleSize) {
                totalTps -= rawTpsValues.remove();
            }
            rawTpsValues.add(nextTps);
            totalTps += nextTps;
            lastTps = totalTps / rawTpsValues.size();
        }

    }

    private class CalculateStandardDeviationTask implements Runnable {
        private Queue<Double> tpsSamples;
        private double total;
        private double squareTotal;

        private CalculateStandardDeviationTask() {
            tpsSamples = new ArrayDeque<>((int) standardDeviationSampleSize);
            standardDeviationTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(saml, this, 0, standardDeviationSampleRate);
        }


        @Override
        public void run() {
            if(!startStandardDeviationCalc) {
                return;
            }
            if(tpsSamples.size() != 0 && tpsSamples.size() >= standardDeviationSampleSize) {
                double oldest = tpsSamples.remove();
                total -= oldest;
                squareTotal -= Math.pow(oldest, 2.0);
            }
            double nextTps = lastTps;
            tpsSamples.add(nextTps);
            total += nextTps;
            squareTotal += Math.pow(nextTps, 2.0);

            if(tpsSamples.size() > 1) {
                lastTpsStandardDeviation = Math.sqrt(Math.abs((squareTotal - Math.pow(total, 2.0) / tpsSamples.size()) / (tpsSamples.size() - 1)));
            } else {
                lastTpsStandardDeviation = 0;
            }
        }
    }

}
