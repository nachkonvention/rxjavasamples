package template.database;

import rx.Observable;

public class Database {

    public String getName(int id) throws InterruptedException {
        System.out.println("Loading id: " + id);
        Thread.sleep(1000);
        return "Jan";
    }

}
