package grokking.rxjava.part1;

import rx.Observable;
import rx.functions.Func1;

public class Example4_MoreMaps {

	public static void simpleMapStringToInteger() {
		Observable.just("Hello, world! -1")
	    .map(new Func1<String, Integer>() {
	        @Override
	        public Integer call(String s) {
	            return s.hashCode();
	        }
	    })
	    .subscribe(i -> System.out.println(Integer.toString(i)));
	}
	
	public static void lamdaMapStringToInteger() {
		Observable.just("Hello, world! -1")
	    .map(s -> s.hashCode())
	    .subscribe(i -> System.out.println(Integer.toString(i)));
	}
	
	// Do more in map() and do less in subscribe()
	public static void simpleMapStringToIntegerAndBackToString() {
		Observable.just("Hello, world! -1")
	    .map(s -> s.hashCode())
	    .map(i -> Integer.toString(i))
	    .subscribe(s -> System.out.println(s));
	}
	
	public static void main(String[] args) {
		simpleMapStringToInteger();
		lamdaMapStringToInteger();
		simpleMapStringToIntegerAndBackToString();
	}
}
