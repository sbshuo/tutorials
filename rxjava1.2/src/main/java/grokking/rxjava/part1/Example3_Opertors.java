package grokking.rxjava.part1;

import rx.Observable;
import rx.functions.Func1;

public class Example3_Opertors {
	
	public static void simpleMapOperator() {
		Observable.just("Hello, world! -1")
	    .map(new Func1<String, String>() {
	        @Override
	        public String call(String s) {
	            return s + " -Dan";
	        }
	    })
	    .subscribe(s -> System.out.println(s));
	}

	public static void lamdaMapOperator() {
		Observable.just("Hello, world! -2")
	    .map(s -> s + " -Dan")
	    .subscribe(s -> System.out.println(s));
	}

	public static void main(String[] args) {
		simpleMapOperator();
		lamdaMapOperator();
	}
}
