/**
 * The MIT License
 * Copyright (c) 2019 Eitan Yarden
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//package com.liveperson.infra.liqr.stores;
// 
//import com.codahale.metrics.Histogram;
//import com.codahale.metrics.Meter;
//import com.codahale.metrics.MetricRegistry;
////import com.liveperson.test.common.SlowTests;
//import jnt.scimark2.Constants;
//import jnt.scimark2.Random;
//import org.junit.Test;
//import org.junit.experimental.categories.Category;
// 
//import java.io.File;
//import java.util.concurrent.Phaser;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
// 
//import static org.junit.Assert.assertThat;
// 
///**
// *
// * @author eitanya
// */
//public class TestThroughput {
// 
////    @Category(SlowTests.class)
//    @Test
//    /**
//     * Finds the throughput of the service. First, calculates normalizer using
//     * some FFT CPU benchmark. Then, sets the rate limit and tests the service
//     * with this rate request. If the responses rate is similar to the requests
//     * rate, it increases the requests rate and try again. Else, it stops. The
//     * results are written to a file.
//     */
//    public void testPerf() throws Exception {
//        final int INITIAL_RATIO = 5;
//        final double STEP_RATIO = 0.1;
//        final double FAIL_UNDER_RATIO = 5.1;
// 
//        double normalizer = jnt.scimark2.kernel.measureFFT(Constants.FFT_SIZE, 3, new Random(Constants.RANDOM_SEED));
//        double perf = normalizer * INITIAL_RATIO;
//        double ratio;
////        ArrayNode jsonRes = JSON.array();
//        try {
//            do {
//                perf += normalizer * STEP_RATIO;
//                System.out.print("Testing: " + perf + "(" + perf / normalizer + ")");
//                System.gc();
//                PerformanceResult res = testIt(perf);
//                ratio = res.outputRate / perf;
//                System.out.println(" res: " + ratio + ", " + res.latency);
////                jsonRes.add(JSON.object().put("rate", perf).put("normalized", perf / normalizer).put("ratio", ratio).put("latancy98micro", res.latency));
//            } while (Math.abs(1 - ratio) < 0.05);
//        } catch (TimeoutException ex) {
//            System.out.println("***** Iter "+perf+" exited abnormally.");
//        }
//        final File file = new File("target/perf/throuput/message.json");
//        file.getParentFile().mkdirs();
////        JSON.om.writeValue(file, jsonRes);
//        System.out.println(String.format(TEAMCITY_STATISTIC_VALUE, "ThrouputRatio", perf - STEP_RATIO));
//        assertThat(perf / normalizer, greaterThan(FAIL_UNDER_RATIO));
//    }
//    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();
// 
//    /**
//     *
//     * @param rate The rate of service input requests
//     * @return The rate of the service output responses
//     * @throws Exception
//     */
//    private PerformanceResult testIt(double rate) throws Exception {
//        RateLimiter rl = RateLimiter.create(rate);
//        final Meter recv = METRIC_REGISTRY.meter("recv" + rate);
//        final Histogram hist = METRIC_REGISTRY.histogram("hist" + rate);
// 
//        WsTester<JsonNode> client = JsonWsTester.connect(BROADCAST_SERVER_WS_URI);
//        Phaser phaser = new Phaser();
//        client.register(any(), p -> {
//            recv.mark();
//            phaser.arriveAndDeregister();
//            hist.update(TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - p.path("time").asLong()));
//            return false;
//        });
//        long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
//        while (System.nanoTime() < end) {
//            rl.acquire();
//            phaser.register();
////            client.send(JSON.object().put("time", System.nanoTime()));
//        }
//        phaser.awaitAdvanceInterruptibly(0, 3, TimeUnit.SECONDS);
//        return new PerformanceResult(recv.getMeanRate(), hist.getSnapshot().get98thPercentile());
//    }
// 
//    private class PerformanceResult {
//        final double outputRate;
//        final double latency;
// 
//        public PerformanceResult(double outputRate, double latency) {
//            this.outputRate = outputRate;
//            this.latency = latency;
//        }
//    }
//   protected static final String TEAMCITY_STATISTIC_VALUE = "##teamcity[buildStatisticValue key='%s' value='%s']";
//}