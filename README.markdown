<h1>configurable-parallel-computer</h1>

Provides a concurrent "Computer" for junit 4.7 that supports multiple modes of execution.

To install: Build from source using mvn install

<h2>RELEASES</h2>

1.6

Fixed problems with missing test results when some tests failed.

1.5

- Re-instated BOTH style executions and added test cases to prove that they work.

1.4

- Fixed a problem with failed tests and failed assumptions in the demultiplexer.

1.3

- Fixed problem with parallel=classes and surefire-reports not being generated.
- Modified CPC to be more like junit's ParallelComputer.
   I still believe junit's ParallelComputer model has the potential for deadlocks with parallel=both and limited threads, and we now also
    have this capability ;) If you use both, it should basically be because you're using a lot of threads ;) (You should have /more/
    threads than test-methods in any single test class to be safe).

1.2

Fixed concurrency (or lack thereof) when running parallel=classes. 

1.1

Supports a better demultiplexer of test-run output

1.0 First released version


