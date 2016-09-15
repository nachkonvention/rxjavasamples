package complete;

import complete.database.DatabaseComplete;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ReactiveBedConTestComplete {
    @Test
    public void testbedcon_1() throws Exception {
        CompletableFuture<String> stringFuture = CompletableFuture.completedFuture("foo");

        CompletableFuture<Integer> intFuture = stringFuture.thenApply((String s) -> s.length());

        System.out.println(intFuture.get());
    }

    @Test
    public void testbedcon_2() throws Exception {
        Subscriber s = new Subscriber() {
            @Override
            public void onCompleted() {
                // do sth
            }

            @Override
            public void onError(Throwable throwable) {
                // do sth
            }

            @Override
            public void onNext(Object o) {
                // do sth
            }
        };
    }

    @Test
    public void testbedcon_3() throws Exception {
        Observable<String> observable = Observable.just("bar");

        /*
         * subscribe(Action1<? super T> onNext, Action1<java.lang.Throwable> onError, Action0 onCompleted)
         * Subscribes to an Observable and provides callbacks to handle the items it emits and any error or completion notification it issues.
         */

        Subscription subscriber = observable.subscribe(
        (String s) -> System.out.println(s),
        throwable -> System.out.println(throwable.toString())
        );
    }

    public DatabaseComplete db = new DatabaseComplete();

    @Test
    public void testbedcon_4() throws Exception {
        Observable<String> name = db
                .rxGetName(1337)
                .timeout(100, TimeUnit.MILLISECONDS);

        name.subscribe(
                (String s) -> System.out.println(s),
                throwable -> System.out.println("Damn!")
        );
    }

    @Test
    public void testbedcon_5() throws Exception {
        Observable.interval(1, TimeUnit.SECONDS)
                .toBlocking()
                .subscribe(System.out::println);
    }

    @Test
    public void testbedcon_6() throws Exception {
        Observable o1 = Observable.interval(1, TimeUnit.SECONDS);
        Observable o2 = Observable.interval(2, TimeUnit.SECONDS);

        Observable<String> o3 = o1.withLatestFrom(o2, (x, y) -> "o1: " + x + "o2: " + y);

        o3.toBlocking()
                .subscribe(s -> System.out.println(s));
    }

    @Test
    public void testbedcon_7() throws Exception {
        Observable<String> query = db.rxExecuteLongRunningQuery();

        TestScheduler sched = Schedulers.test();
        TestSubscriber<String> sub = new TestSubscriber<>();

        query.timeout(2, TimeUnit.SECONDS, sched)
                .doOnError(e -> System.out.println("Timeout!"))
                .retry(4)
                .onErrorReturn(e -> "Damn!")
                .subscribe(sub);

        sub.assertNoValues();
        sub.assertNoErrors();

        sched.advanceTimeBy(9, TimeUnit.SECONDS);

        sub.assertNoValues();
        sub.assertNoErrors();

        sched.advanceTimeBy(2, TimeUnit.SECONDS);

        sub.assertNoErrors();
        sub.assertValue("Damn!");
    }
}
