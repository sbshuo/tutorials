package grokking.rxjava.part3;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * This example is written by myself. It is not from the author
 * 
 * @author shuochen
 *
 */
public class Example6_Threading {
  
  public static int totalCount = 0;

  // Setup 1: query(String text)
  public static Observable<List<String>> query(String text) {
    if (text.equals("Hello, world!")) {
      List<String> urls = Stream.of("url1", "url2", "url3").collect(Collectors.toList());
      return Observable.just(urls);
    }

    if (text.equals("Hello, world - Six!")) {
      List<String> urls = Stream.of("url1", "url2", "url3", "url4", "url5", "url6").collect(Collectors.toList());
      return Observable.just(urls);
    }

    return Observable.just(Collections.emptyList());
  }

  public static Observable<String> getTitle(String url) {
    return Observable.just(url);
  }
  
  public static String doHeavyWork(String url) {
    
    Random random = new Random();
    try {
      Thread.sleep(random.nextInt(3) * 100);
      return url;
    }
    catch(InterruptedException e) {
      e.printStackTrace();
    }
        
    return null;
  }

  // From https://proandroiddev.com/understanding-rxjava-subscribeon-and-observeon-744b0c6a41ea
  public static void simpleExample() {
    Observable.just("long", "longer", "longest")
      .subscribeOn(Schedulers.io())
      .map(String::length)
      .observeOn(Schedulers.computation()).filter(it -> it > 2)
      .subscribe(length -> System.out.println("item length " + length));
  }
  
  public static void mainThreadNeedToWait() throws Exception {
    query("Hello, world!")
      .subscribeOn(Schedulers.io())
      .flatMap(urls -> Observable.from(urls))
      .doOnNext(url -> System.out.println(url + " -- FlatMap"))
      .subscribe(url -> {
        System.out.println(url + " ## Subscriber");
        totalCount++;
      });
    
    // Main thread need to give Observable a chance to fire emissions on the background thread
    // Otherwise, nothing will be printed by the subscriber.
    Thread.sleep(3000L);
  }

  public static void showingSubscribOnThreads() throws Exception {
    System.out.println("Main Thread: " + Thread.currentThread().getName());
    query("Hello, world - Six!")
      .flatMap(urls -> Observable.from(urls))
      .doOnNext(url -> System.out.println("SubscribeOn Thread: " + Thread.currentThread().getName()))
      .map(url -> doHeavyWork(url))
      .subscribeOn(Schedulers.io())
      .doOnNext(url -> System.out.println(url + " -- FlatMap"))
      .subscribe(url -> {
        System.out.println(url + " ## Subscriber");
        totalCount++;
      });
    
    // Main thread need to give Observable a chance to fire emissions on the background thread
    // Otherwise, nothing will be printed by the subscriber.
    Thread.sleep(3000L);
  }

  public static void showingSubscribOnAndObserveOnThreads() throws Exception {
    System.out.println("Main Thread: " + Thread.currentThread().getName());
    query("Hello, world - Six!")
      .flatMap(urls -> Observable.from(urls))
      .doOnNext(url -> System.out.println("SubscribeOn Thread: " + Thread.currentThread().getName()))
      .map(url -> doHeavyWork(url))
      .subscribeOn(Schedulers.io())
      .doOnNext(url -> System.out.println(url + " -- FlatMap -- " + Thread.currentThread().getName()))
      .observeOn(Schedulers.computation())
      .doOnNext(url -> System.out.println("ObserveOn Thread: " + Thread.currentThread().getName()))
      .subscribe(url -> {
        System.out.println(url + " ## Subscriber -- " + Thread.currentThread().getName());
        totalCount++;
      });
    
    // Main thread need to give Observable a chance to fire emissions on the background thread
    // Otherwise, nothing will be printed by the subscriber.
    Thread.sleep(3000L);
  }

  public static void main(String[] args) throws Exception {
    
//    for (int i = 0; i<10; i++) {
//      System.out.println("====== " + i);
//      mainThreadNeedToWait();
//    }
//    
//    System.out.println("1 - Total count: " + totalCount);

//    for (int i = 0; i<1; i++) {
//      System.out.println("====== " + i);
//      showingSubscribOnThreads();
//    }
//    
//    System.out.println("2 - Total count: " + totalCount);

    for (int i = 0; i<1; i++) {
      System.out.println("====== " + i);
      showingSubscribOnAndObserveOnThreads();
    }
    
    System.out.println("3 - Total count: " + totalCount);

//    simpleExample();
  }


}
