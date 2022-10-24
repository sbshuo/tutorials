package grokking.rxjava.part2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import rx.Observable;
import rx.functions.Func1;

public class Example5_FlatMap {

  // Setup 1: query(String text)
  public static Observable<List<String>> query(String text) {
    if (text.equals("Hello, world!")) {
      List<String> urls = Stream.of("url1", "url2", "url3").collect(Collectors.toList());
      return Observable.just(urls);
    }

    return Observable.just(Collections.emptyList());
  }

  public static Observable<String> getTitle(String url) {
    return Observable.just(url);
  }

  // I can't transform the data stream in the Observer
  public static void noMap() {
    query("Hello, world!").subscribe(urls -> {
      for (String url : urls) {
        System.out.println(url);
      }
    });
  }

  public static void uglyMap() {
    query("Hello, world!").map(urls -> {
      List<String> mappedUrls = new ArrayList<>();
      for (String url : urls) {
        mappedUrls.add(url + " -mapped ");
      }
      return mappedUrls;
    }).subscribe(urls -> {
      for (String url : urls) {
        System.out.println(url);
      }
    });
  }

  public static void simpleFlatMap() {
    query("Hello, world!").flatMap(new Func1<List<String>, Observable<String>>() {
      @Override
      public Observable<String> call(List<String> urls) {
        return Observable
            .from(urls.stream().map(url -> url + " -flatMapped").collect(Collectors.toList()));
      }
    }).subscribe(url -> System.out.println(url));
  }

  public static void lamdaFlatMap() {
    query("Hello, world!")
        .flatMap(urls -> Observable
            .from(urls.stream().map(url -> url + " -flatMapped").collect(Collectors.toList())))
        .subscribe(url -> System.out.println(url));
  }

  public static void simpleTwoFlatMaps() {
    query("Hello, world!")
        .flatMap(urls -> Observable
            .from(urls.stream().map(url -> url + " -twoFlatMapped").collect(Collectors.toList())))
        .flatMap(new Func1<String, Observable<String>>() {
          @Override
          public Observable<String> call(String url) {
            return getTitle(url);
          }
        }).subscribe(title -> System.out.println(title));
  }

  public static void lamdaTwoFlatMaps() {
    query("Hello, world!")
        .flatMap(urls -> Observable
            .from(urls.stream().map(url -> url + " -twoFlatMapped").collect(Collectors.toList())))
        .flatMap(url -> getTitle(url)).subscribe(title -> System.out.println(title));
  }

  public static void filterWithTwoFlatMaps() {
    query("Hello, world!")
        .flatMap(urls -> Observable
            .from(urls.stream().map(url -> url + " -twoFlatMapped").collect(Collectors.toList())))
        .flatMap(url -> getTitle(url)).filter(title -> title != null)
        .subscribe(title -> System.out.println(title));
  }

  public static void take2WithTwoFlatMaps() {
    query("Hello, world!")
        .flatMap(urls -> Observable
            .from(urls.stream().map(url -> url + " -twoFlatMapped").collect(Collectors.toList())))
        .flatMap(url -> getTitle(url)).filter(title -> title != null).take(2)
        .subscribe(title -> System.out.println(title));
  }

  public static void saveTitleWithTwoFlatMaps() {
    query("Hello, world!")
        .flatMap(urls -> Observable
            .from(urls.stream().map(url -> url + " -twoFlatMapped").collect(Collectors.toList())))
        .flatMap(url -> getTitle(url)).filter(title -> title != null).take(2).doOnNext(title -> saveTitle(title))
        .subscribe(title -> System.out.println(title));
  }

  public static void saveTitle(String title) {
    System.out.println("Saving title: " + title);
  }
  
  public static void main(String[] args) {
    noMap();
    uglyMap();
    simpleFlatMap();
    lamdaFlatMap();
    simpleTwoFlatMaps();
    lamdaTwoFlatMaps();
    filterWithTwoFlatMaps();
    take2WithTwoFlatMaps();
    saveTitleWithTwoFlatMaps();
  }
}
