# What have we learned in Exampe1 - Example4

## Key idea #1: Observable and Subscriber can do anything

* Your Observable could be a database query, the Subscriber taking the results and displaying them on the screen.  
* Your Observable could be a click on the screen, the Subscriber reacting to it.
* Your Observable could be a stream of bytes read from the Internet, the Subscriber could write it to the disk.
* It's a general framework that can handle just about any problem.

## Key idea #2: The Observable and Subscriber are independent of the transformational steps in between them.

* I can stick as many map() calls as I want in between the original source Observable and its ultimate Subscriber.  
* The system is highly composable: it is easy to manipulate the data.  As long as the developers work with the correct input/output data I could make a chain that goes on forever.

## Summary of Key idea #1 and #2

Combine our two key ideas and you can see a system with a lot of potential.  At this point, though, we only have a single operator, map(), which severely limits our capabilities.  In part 2 we'll take a dip into the large pool of operators available to your when using RxJava.


