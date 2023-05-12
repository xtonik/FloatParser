# FloatParser
Fast decimal string parser to float primitive data type at the expense of being not so accurate.

#### It is up to 10x faster than original JDK implementation, results generated by JMH:

|Benchmark|                      (float value)|  Mode|  Cnt|    Score|    Error|  Units|
|-|-:|-|-|-:|-:|-|
|FloatParserBenchmark.custom|                1|  avgt|   30|   12.369| ±  1.523|  us/op|
|FloatParserBenchmark.custom|            23233|  avgt|   30|   21.378| ±  2.618|  us/op|
|FloatParserBenchmark.custom|        102120.22|  avgt|   30|   38.733| ±  3.512|  us/op|
|FloatParserBenchmark.custom|   12345678.2E-22|  avgt|   30|   47.181| ±  1.388|  us/op|
|FloatParserBenchmark.custom|  70385310000E-36|  avgt|   30|   43.564| ±  0.718|  us/op|
|FloatParserBenchmark.custom|      +0x12.ABp-1|  avgt|   30|  681.804| ± 89.975|  us/op|
|FloatParserBenchmark.jdk|                   1|  avgt|   30|   51.741| ± 33.642|  us/op|
|FloatParserBenchmark.jdk|               23233|  avgt|   30|   52.446| ±  7.062|  us/op|
|FloatParserBenchmark.jdk|           102120.22|  avgt|   30|  159.708| ± 24.383|  us/op|
|FloatParserBenchmark.jdk|      12345678.2E-22|  avgt|   30|  232.579| ± 22.520|  us/op|
|FloatParserBenchmark.jdk|     70385310000E-36|  avgt|   30|  351.372| ± 39.676|  us/op|
|FloatParserBenchmark.jdk|         +0x12.ABp-1|  avgt|   30|  589.403| ± 32.466|  us/op|

#### Note: There is much more comprehensive and more performant implementation of the same task in project [FastDoubleParser](https://github.com/wrandelshofer/FastDoubleParser).
