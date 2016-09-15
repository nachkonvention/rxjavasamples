package complete.database;

import rx.Observable;

import java.util.concurrent.TimeUnit;

public class DatabaseComplete {

    public String getName(int id) throws InterruptedException {
        System.out.println("Loading id: " + id);
        Thread.sleep(1000);
        return "Jan";
    }

    public Observable<String> rxGetName(int id) throws InterruptedException {
        return Observable.fromCallable(() -> this.getName(id));
    }

    public Observable rxExecuteLongRunningQuery(){
        return Observable.timer(10, TimeUnit.SECONDS);
    }
}
