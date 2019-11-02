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
package org.liqr.poc;

import com.codahale.metrics.Meter;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.google.common.util.concurrent.RateLimiter;
import org.liqr.core.LiveQueryManager;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.websockets.WebsocketBundle;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.server.JSONP;
import java.time.LocalDateTime;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.liqr.core.LiveQueryManager.overrideWith;

import org.liqr.stores.h2.H2Query;
import org.liqr.utils.GeneralUtils;

import java.util.function.Consumer;
import org.h2.jdbcx.JdbcConnectionPool;

import static io.dropwizard.websockets.GeneralUtils.rethrow;
import static java.time.LocalDateTime.now;
import java.time.ZoneOffset;

public class LiveQueryApp extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new LiveQueryApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/static"));
        bootstrap.addBundle(new WebsocketBundle(StreamingQuery.class));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws InvalidKeySpecException, NoSuchAlgorithmException {
        myH2Store = new MyStore(JdbcConnectionPool.create("jdbc:h2:/tmp/db:test", "username", "password"),
                environment.metrics());
        myH2Store.init();

        LQM = new LiveQueryManager<>(myH2Store);
        final Meter meter = environment.metrics().meter("incoming");
        startProducerThread("", "", LQM, meter);
        environment.jersey().register(new RestQuery());
    }

    @Path(value = "/query")
    public static class RestQuery {
        @GET
        @JSONP(queryParam = "callback")
        @Produces({"application/javascript"})
        public Object validate(
                @QueryParam("val") Optional<Integer> minutes) {
            final LocalDateTime now = now();
            return myH2Store.values(H2Query.of("due > :min and due <= :max order by due limit 200",
                    q->q.bind("min", now.toEpochSecond(ZoneOffset.UTC))
                            .bind("max", now.plusMinutes(minutes.orElse(5)).toEpochSecond(ZoneOffset.UTC)))).toArray();
        }
    }

    @Metered
    @Timed
    @ServerEndpoint("/ws")
    public static class StreamingQuery {
        @OnOpen
        public void myOnOpen(final Session session) throws IOException {
            LocalDateTime min = GeneralUtils.getParam(session, "val")
                    .map(ldt -> LocalDateTime.parse(ldt, ISO_LOCAL_DATE_TIME))
                    .orElse(now());
            final LocalDateTime max = myH2Store.maxDue(min, 50);
            sub = LQM.subscribe(MyStore.BetweenDates.of(min, max), sendTo(session));
        }

        @OnClose
        public void myOnClose(final Session session, CloseReason cr) {
            Optional.ofNullable(sub).ifPresent(s -> LQM.unsubscribe(s));
        }

        private static <T> Consumer<T> sendTo(final Session session) {
            return rethrow(val -> session.getAsyncRemote().sendText(MyObj.OM.writeValueAsString(val)));
        }
        private Object sub;
    }

    private void startProducerThread(final String bootstrap, final String topic, LiveQueryManager<String, MyObj, ?> liveQueryManager, Meter meter) {
        Thread t = new Thread(() -> {
            RateLimiter rl = RateLimiter.create(Integer.parseInt(RATE));

            while (true) {
                rl.acquire();
                liveQueryManager.getStore().update(
                        "c-" + Integer.toString(ThreadLocalRandom.current().nextInt(DATASET_SIZE)),
                        overrideWith(MyObj.random()));
                meter.mark();

            }    });
        t.setDaemon(true);
        t.start();
    }

    public static LiveQueryManager<String, MyObj, H2Query> LQM;
    private static MyStore myH2Store;
    private static final int DATASET_SIZE = Integer.parseInt(Optional.ofNullable(System.getenv("DATASET_SIZE")).orElse("2000"));
    private static final String RATE = Optional.ofNullable(System.getenv("RATE")).orElse("100");


}
