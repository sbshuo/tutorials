package grokking.rxjava.part1;

import rx.Observable;
import rx.functions.Action1;

public class Example2_SimplerCode {

	/**
	 * Simplification 1: creation method <code>Observable.just</code>
	 */
	public static void simplerCode_1() {
		Observable<String> myObservable =
			    Observable.just("Hello, world! -1");
		
		Action1<String> onNextAction = new Action1<String>() {
		    @Override
		    public void call(String s) {
		        System.out.println(s);
		    }
		};
		
		myObservable.subscribe(onNextAction);
	}
	
	/**
	 * Simplification 2: omit OnErrorAction and onCompleteAction
	 */	
	public static void simplerCode_2() {
		Observable.just("Hello, world! -2")
	    .subscribe(new Action1<String>() {
	        @Override
	        public void call(String s) {
	      	    System.out.println(s);
	    	}
	    });
	}
	
	/**
	 * Simplification 3: use lamda to get rid of Action1
	 */	
	public static void simplerCode_3() {
		Observable.just("Hello, world! -3")
	    .subscribe(s -> System.out.println(s));
	}
	
	public static void main(String[] args) {
		simplerCode_1();
		simplerCode_2();
		simplerCode_3();
	}
}
