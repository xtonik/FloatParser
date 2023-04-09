package cz.janec.floatparser;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 10, timeUnit = TimeUnit.MILLISECONDS, time = 100)
@Measurement(iterations = 30, timeUnit = TimeUnit.MILLISECONDS, time = 100)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class FloatParserBenchmark {

    private static final int REPEATS = 1000;
    @Param({ "1", "23233", "102120.22", "12345678.2E-22", "70385310000E-36", "+0x12.ABp-1"})
    private String value;

    @Benchmark
    public void jdk(Blackhole blackhole) {
        for (int i = 0; i < REPEATS; i++) {
            blackhole.consume(Float.parseFloat(value));
        }
    }

    @Benchmark
    public void custom(Blackhole blackhole) {
        for (int i = 0; i < REPEATS; i++) {
            blackhole.consume(FloatParser.parseFloat(value));
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FloatParserBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
