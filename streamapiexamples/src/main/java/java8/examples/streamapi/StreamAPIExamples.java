package java8.examples.streamapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** 
 * Examples from http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/
 * 
 * Further reading: http://martinfowler.com/articles/collection-pipeline/
 * 
 * Good questions for Stream API:
 * 1. flatMapInPipeline()
 * 
 * @author shuochen
 *
 */
public class StreamAPIExamples {

	List<Person> persons =
		    Arrays.asList(
		        new Person("Max", 18),
		        new Person("Peter", 23),
		        new Person("Pamela", 23),
		        new Person("David", 12));
	
	List<Foo> foos = new ArrayList<>();

	//
	//  sortedOperation()
	//
	//	sort: a2; d2
	//	sort: b1; a2
	//	sort: b1; d2
	//	sort: b1; a2
	//	sort: b3; b1
	//	sort: b3; d2
	//	sort: c; b3
	//	sort: c; d2
	//	filter: a2
	//	map: a2
	//	forEach: A2
	//	filter: b1
	//	filter: b3
	//	filter: c
	//	filter: d2
	public void sortedOperation() {
		Stream.of("d2", "a2", "b1", "b3", "c")
		.sorted((s1,s2) -> {
			System.out.printf("sort: %s; %s\n", s1, s2);
			return s1.compareTo(s2);
		})
		.filter(s -> {
			System.out.println("filter: " + s);
			return s.startsWith("a");
		})
		.map(s -> {
			System.out.println("map: " + s);
			return s.toUpperCase();
		})
		.forEach(s -> System.out.println("forEach: " + s));
	}
	
	public void streamSupplier() {
		Supplier<Stream<String>> streamSupplier =
			    () -> Stream.of("d2", "a2", "b1", "b3", "c")
			            .filter(s -> s.startsWith("a"));

			streamSupplier.get().anyMatch(s -> true);   // ok
			streamSupplier.get().noneMatch(s -> true);  // ok		
	}
	
	public void listToSet() {
		Set<Person> filtered =
			    persons
			        .stream()
			        .filter(p -> p.name.startsWith("P"))
			        .collect(Collectors.toSet());

			System.out.println(filtered);    // [Peter, Pamela]		
	}
	
	public void groupBy() {
		Map<Integer, List<Person>> personsByAge = persons
			    .stream()
			    .collect(Collectors.groupingBy(p -> p.age));

			personsByAge
			    .forEach((age, p) -> System.out.format("age %s: %s\n", age, p));

			// age 18: [Max]
			// age 23: [Peter, Pamela]
			// age 12: [David]		
	}
		
	public void mergeDuringMapping() {
		Map<Integer, String> map = persons
			    .stream()
			    .collect(Collectors.toMap(
			        p -> p.age,
			        p -> p.name,
			        (name1, name2) -> name1 + ";" + name2));

			System.out.println(map);
	}
	
	public void customCollector() {
		Collector<Person, StringJoiner, String> personNameCollector =
			    Collector.of(
			        () -> new StringJoiner(" | "),          // supplier
			        (j, p) -> j.add(p.name.toUpperCase()),  // accumulator
			        (j1, j2) -> j1.merge(j2),               // combiner
			        StringJoiner::toString);                // finisher

			String names = persons
			    .stream()
			    .collect(personNameCollector);

			System.out.println(names);  // MAX | PETER | PAMELA | DAVID		
	}
	
	public void flatMap() {
		List<Foo> foos = new ArrayList<>();

		// create foos
		IntStream
		    .range(1, 4)
		    .forEach(i -> foos.add(new Foo("Foo" + i)));

		// create bars
		foos.forEach(f ->
		    IntStream
		        .range(1, 4)
		        .forEach(i -> f.bars.add(new Bar("Bar" + i + " <- " + f.name))));

		foos.stream()
	    .flatMap(f -> f.bars.stream())
	    .forEach(b -> System.out.println(b.name));

	// Bar1 <- Foo1
	// Bar2 <- Foo1
	// Bar3 <- Foo1
	// Bar1 <- Foo2
	// Bar2 <- Foo2
	// Bar3 <- Foo2
	// Bar1 <- Foo3
	// Bar2 <- Foo3
	// Bar3 <- Foo3
	}
	
	public void flatMapInPipeline() {
		IntStream.range(1, 4)
	    .mapToObj(i -> new Foo("Foo" + i))
	    .peek(f -> IntStream.range(1, 4)
	        .mapToObj(i -> new Bar("Bar" + i + " <- " + f.name))
	        .forEach(f.bars::add))
	    .flatMap(f -> f.bars.stream())
	    .forEach(b -> System.out.println(b.name));		
	}
	
	/**
	 * Without using FlatMap, we have to do something like this:
	 * 
	 * Outer outer = new Outer();
	 * if (outer != null && outer.nested != null && outer.nested.inner != null) {
	 * System.out.println(outer.nested.inner.foo);
	 * }
	 */
	public void flatMapForHierarchicalCheck() {
		Outer outer = new Outer();
		Nested nested = new Nested();
		Inner inner = new Inner();
		inner.foo = "foo1";
		nested.inner = inner;
		outer.nested = nested;

		//no print-out
		Optional.of(new Outer())
	    .flatMap(o -> Optional.ofNullable(o.nested))
	    .flatMap(n -> Optional.ofNullable(n.inner))
	    .flatMap(i -> Optional.ofNullable(i.foo))
	    .ifPresent(System.out::println);

		//foo1
		Optional.of(outer)
	    .flatMap(o -> Optional.ofNullable(o.nested))
	    .flatMap(n -> Optional.ofNullable(n.inner))
	    .flatMap(i -> Optional.ofNullable(i.foo))
	    .ifPresent(System.out::println);
	}
	
	/** 
	 * Basically, find the oldest one
	 */
	public void reduce_1_BiFunction() {
		persons
	    .stream()
	    .reduce((p1, p2) -> p1.age > p2.age ? p1 : p2)
	    .ifPresent(System.out::println);    // Pamela
	}
	
	public void reduce_2_IndentityAndBiFunction() {
		Person result =
			    persons
			        .stream()
			        .reduce(new Person("", 0), (p1, p2) -> {
			            p1.age += p2.age;
			            p1.name += p2.name;
			            return p1;
			        });

			System.out.format("name=%s; age=%s", result.name, result.age);
			// name=MaxPeterPamelaDavid; age=76
	}
	
	public void reduce_3_IndenityAndAccumulatorAndCombinator() {
		Integer ageSum = persons
			    .stream()
			    .reduce(0, (sum, p) -> sum += p.age, (sum1, sum2) -> sum1 + sum2);

			System.out.println(ageSum);  // 76
	}
	
	// When NOT parallel, the combiner is not called
	public void reduce_3_Explain() {
		Integer ageSum = persons
			    .stream()
			    .reduce(0,
			        (sum, p) -> {
			            System.out.format("accumulator: sum=%s; person=%s\n", sum, p);
			            return sum += p.age;
			        },
			        (sum1, sum2) -> {
			            System.out.format("combiner: sum1=%s; sum2=%s\n", sum1, sum2);
			            return sum1 + sum2;
			        });

			// accumulator: sum=0; person=Max
			// accumulator: sum=18; person=Peter
			// accumulator: sum=41; person=Pamela
			// accumulator: sum=64; person=David
	}
	
	public void reduce_3_Explain_parallel() {
		Integer ageSum = persons
			    .parallelStream()
			    .reduce(0,
			        (sum, p) -> {
			            System.out.format("accumulator: sum=%s; person=%s\n", sum, p);
			            return sum += p.age;
			        },
			        (sum1, sum2) -> {
			            System.out.format("combiner: sum1=%s; sum2=%s\n", sum1, sum2);
			            return sum1 + sum2;
			        });

			// accumulator: sum=0; person=Pamela
			// accumulator: sum=0; person=David
			// accumulator: sum=0; person=Max
			// accumulator: sum=0; person=Peter
			// combiner: sum1=18; sum2=23
			// combiner: sum1=23; sum2=12
			// combiner: sum1=41; sum2=35
	}
	
	/**
	 * On my machine the common pool is initialized with a parallelism of 3 per default. 
	 * This value can be decreased or increased by setting the following JVM parameter:
	 * 
	 * -Djava.util.concurrent.ForkJoinPool.common.parallelism=5
	 * 
	 * The size of the underlying thread-pool uses up to five threads - depending on 
	 * the amount of available physical CPU cores:
	 */
	public void forkJoinPools() {
		ForkJoinPool commonPool = ForkJoinPool.commonPool();
		System.out.println(commonPool.getParallelism());    // 3
		//Ehm, prints out 11 for me -shuo
	}
	
	public void parallelBehavior() {
		Arrays.asList("a1", "a2", "b1", "c2", "c1")
	    .parallelStream()
	    .filter(s -> {
	        System.out.format("filter: %s [%s]\n",
	            s, Thread.currentThread().getName());
	        return true;
	    })
	    .map(s -> {
	        System.out.format("map: %s [%s]\n",
	            s, Thread.currentThread().getName());
	        return s.toUpperCase();
	    })
	    .forEach(s -> System.out.format("forEach: %s [%s]\n",
	        s, Thread.currentThread().getName()));
	}
	
	public void parallelSort() {
		Arrays.asList("a1", "a2", "b1", "c2", "c1")
	    .parallelStream()
	    .filter(s -> {
	        System.out.format("filter: %s [%s]\n",
	            s, Thread.currentThread().getName());
	        return true;
	    })
	    .map(s -> {
	        System.out.format("map: %s [%s]\n",
	            s, Thread.currentThread().getName());
	        return s.toUpperCase();
	    })
	    .sorted((s1, s2) -> {
	        System.out.format("sort: %s <> %s [%s]\n",
	            s1, s2, Thread.currentThread().getName());
	        return s1.compareTo(s2);
	    })
	    .forEach(s -> System.out.format("forEach: %s [%s]\n",
	        s, Thread.currentThread().getName()));
	}
	
	public void reduce_3_Explain_parallel_more_detail() {
		List<Person> persons = Arrays.asList(
			    new Person("Max", 18),
			    new Person("Peter", 23),
			    new Person("Pamela", 23),
			    new Person("David", 12));

			persons
			    .parallelStream()
			    .reduce(0,
			        (sum, p) -> {
			            System.out.format("accumulator: sum=%s; person=%s [%s]\n",
			                sum, p, Thread.currentThread().getName());
			            return sum += p.age;
			        },
			        (sum1, sum2) -> {
			            System.out.format("combiner: sum1=%s; sum2=%s [%s]\n",
			                sum1, sum2, Thread.currentThread().getName());
			            return sum1 + sum2;
			        });
	}
	
	public static void main(String[] args) {
		StreamAPIExamples sut = new StreamAPIExamples();
		//sut.sortedOperation();
		//sut.streamSupplier();
		//sut.listToSet();
		//sut.groupBy();
		//sut.mergeDuringMapping();
		//sut.customCollector();
		//sut.flatMap();
		//sut.flatMapInPipeline();
		//sut.flatMapForHierarchicalCheck();
		//sut.reduce_1_BiFunction();
		//sut.reduce_2_IndentityAndBiFunction();
		//sut.reduce_3_IndenityAndAccumulatorAndCombinator();
		//sut.reduce_3_Explain();
		//sut.reduce_3_Explain_parallel();
		//sut.forkJoinPools();
		//sut.parallelBehavior();
		//sut.parallelSort();
		sut.reduce_3_Explain_parallel_more_detail();
	}
}
