package no.nofuzz.gs1.benchmark;

import no.nofuzz.gs1.parser.Gs1Parser;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Thread)
public class Gs1ParserBenchmark {

    private static final char FNC1 = 29;

    private Gs1Parser lenientParser;
    private Gs1Parser strictParser;

    private String gs1_128;
    private String datamatrix;

    @Setup
    public void setup() {
        lenientParser = Gs1Parser.defaultParser();
        strictParser = Gs1Parser.strictParser();

        gs1_128 = "(01)09501101530003(17)251231(10)ABC123";

        datamatrix =
            "" + FNC1 +
            "10ABC123" + FNC1 +
            "17251231";
    }

    // --------------------
    // GS1-128
    // --------------------

    @Benchmark
    public void parseGs1_128_lenient() {
        lenientParser.parse(gs1_128);
    }

    @Benchmark
    public void parseGs1_128_strict() {
        strictParser.parse(gs1_128);
    }

    // --------------------
    // DataMatrix
    // --------------------

    @Benchmark
    public void parseDatamatrix_lenient() {
        lenientParser.parse(datamatrix);
    }

    @Benchmark
    public void parseDatamatrix_strict() {
        strictParser.parse(datamatrix);
    }
}
