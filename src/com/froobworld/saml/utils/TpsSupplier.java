package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class TpsSupplier {
    private Saml saml;
    private final long tpsSampleSize;
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
        return new BigDecimal(useNmsTps ? NmsUtils.getTPS() : lastTps).setScale(2, RoundingMode.HALF_UP).doubleValue();
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
        private long totalDelta;
        private long ticksSinceStart;

        private CalculateTpsTask() {
            Long[] initialValues = new Long[((Long) tpsSampleSize).intValue()];
            Arrays.fill(initialValues, 50L);
            deltas = new LinkedList<Long>(Arrays.asList(initialValues));
            rawTpsValues = new LinkedList<>();
            totalDelta = 50 * tpsSampleSize;
            tpsTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(saml, this, 0,  1);
        }


        @Override
        public void run() {
            if(ticksSinceStart < 200) {
                ticksSinceStart++;
            } else {
                startStandardDeviationCalc = true;
            }
            if(lastTimeStamp == 0) {
                lastTimeStamp = System.currentTimeMillis();
                return;
            }
            if(deltas.size() != 0 && deltas.size() > tpsSampleSize) {
                totalDelta -= deltas.remove();
            }
            long nextDelta = System.currentTimeMillis() - lastTimeStamp;
            deltas.add(nextDelta);
            totalDelta += nextDelta;

            double nextTps = (double) deltas.size() / (double) totalDelta * 1000;
            lastTimeStamp = System.currentTimeMillis();

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

        private CalculateStandardDeviationTask() {
            tpsSamples = new LinkedList<>();
            standardDeviationTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(saml, this, 0, standardDeviationSampleRate);
        }


        @Override
        public void run() {
            if(!startStandardDeviationCalc) {
                return;
            }
            if(tpsSamples.size() != 0 && tpsSamples.size() >= standardDeviationSampleSize) {
                total -= tpsSamples.remove();
            }
            double nextTps = getTps();
            tpsSamples.add(nextTps);
            total += nextTps;

            if(tpsSamples.size() > 1) {
                double average = total / tpsSamples.size();

                double squareDifferenceSum = 0;
                for (double sample : tpsSamples) {
                    squareDifferenceSum += Math.pow(sample - average, 2.0);
                }
                lastTpsStandardDeviation = Math.sqrt(squareDifferenceSum / (tpsSamples.size() - 1));
            } else {
                lastTpsStandardDeviation = 0;
            }
        }
    }

}
