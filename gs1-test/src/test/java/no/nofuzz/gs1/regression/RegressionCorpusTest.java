package no.nofuzz.gs1.regression;

import no.nofuzz.gs1.parser.Gs1Parser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class RegressionCorpusTest {

    private final Gs1Parser parser = Gs1Parser.defaultParser();

    private final List<String> validBarcodes = List.of(
        "(01)09501101530003(17)251231(10)ABC123",
        "(01)12345678901231(10)LOT42",
        "(01)00012345678905(17)240101"
    );

    @Test
    void knownValidBarcodesShouldAlwaysParse() {
        for (String barcode : validBarcodes) {
            assertThatCode(() -> parser.parse(barcode))
                .doesNotThrowAnyException();
        }
    }
}