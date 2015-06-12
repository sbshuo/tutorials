package java8.examples;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 
 * Examples from http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/
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
	
	public static void main(String[] args) {
		StreamAPIExamples sut = new StreamAPIExamples();
		//sut.sortedOperation();
		//sut.streamSupplier();
		//sut.listToSet();
		//sut.groupBy();
		sut.mergeDuringMapping();
	}
}
