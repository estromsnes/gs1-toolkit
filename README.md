# GS1 Toolkit

[![Build and Test](https://github.com/estromsnes/gs1-toolkit/actions/workflows/build.yml/badge.svg)](https://github.com/estromsnes/gs1-toolkit/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/License-Dual-blue.svg)](LICENSE.txt)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.java.com)

> Parse, validate and understand GS1 barcodes - without losing your sanity.

A battle-tested Java library for parsing GS1-128 and DataMatrix barcodes into structured, type-safe data.

**What is GS1?** [GS1](https://www.gs1.org/) is a global standards organization that develops and maintains barcode standards used across retail, healthcare, logistics, and supply chain industries. GS1 standards enable products to be uniquely identified and tracked worldwide through formats like GTINs (product identifiers), SSCCs (shipping containers), and Application Identifiers (structured data fields in barcodes).

**Why developers choose GS1 Toolkit:**
- **Fast**: 1.1-1.6 million parses/second (JMH verified), single-pass, zero regex
- **Safe**: No exceptions leak, handles malformed input gracefully
- **Practical**: Works with real-world scanner data, not just perfect specs
- **Zero dependencies**: Pure Java, no bloat
- **Flexible**: LENIENT mode for production, STRICT for compliance

---

## Quick Start

### Installation

**Maven:**
```xml
<dependency>
    <groupId>no.nofuzz.gs1</groupId>
    <artifactId>gs1-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

**Gradle:**
```groovy
implementation 'no.nofuzz.gs1:gs1-core:0.1.0'
```

**Requirements:** Java 17+

### Basic Usage

```java
import no.nofuzz.gs1.parser.Gs1Parser;
import no.nofuzz.gs1.model.Gs1Result;
import java.time.LocalDate;

// Parse GS1-128 barcode (parenthesis format)
String barcode = "(01)09501101530003(17)251231(10)LOT123";
Gs1Result result = Gs1Parser.defaultParser().parse(barcode);

// Extract required fields (throws if missing)
String gtin = (String) result.getOrThrow("01");        // "09501101530003"
LocalDate expiry = (LocalDate) result.getOrThrow("17"); // 2025-12-31
String lot = (String) result.getOrThrow("10");          // "LOT123"

// Extract optional fields (returns Optional)
String batch = (String) result.get("10").orElse("UNKNOWN");
LocalDate bestBefore = (LocalDate) result.get("15").orElse(null);

// Check if field exists
if (result.contains("21")) {
    String serial = (String) result.getOrThrow("21");
}

// Or get all fields
Map<String, Gs1Element> all = result.asMap();
```

**DataMatrix format (FNC1-based):**
```java
// Input with FNC1 separators (char 29)
String datamatrix = "\u001D01095011015300031725123110LOT123";
Gs1Result result = Gs1Parser.defaultParser().parse(datamatrix);
```

### CLI Tool

```bash
# Parse and display barcode contents
java -jar gs1-cli.jar parse "(01)09501101530003(17)251231(10)LOT123"

# Output:
# {01=09501101530003, 17=2025-12-31, 10=LOT123}
```

---

## Licensing

### **Free for Non-Commercial Use**

GS1 Toolkit is **free** for:
- Personal projects
- Educational use (students, teachers, academic research)
- Non-profit organizations
- Open-source projects
- Evaluation and proof-of-concept (60 days)

See [LICENSE.txt](LICENSE.txt) for complete terms.

### **Commercial License Required**

A **commercial license is required** if you use GS1 Toolkit in:
- Production systems at for-profit companies
- Internal business applications
- Products or services that generate revenue
- Commercial software (SaaS, on-premise, embedded)

### **How to Purchase**

**Contact Us**
- Email: estromsnes@gmail.com
- Subject: "GS1 Toolkit Business License"

### **License Compliance**

Using GS1 Toolkit commercially without a license violates the license agreement and may result in:
- Legal action for license violation
- Liability for damages
- Reputation damage

**Stay compliant:** If you're unsure whether you need a license, contact us at estromsnes@gmail.com - we're happy to help clarify.

**Full license terms:** See [LICENSE.txt](LICENSE.txt) for the complete legal agreement.

---

## Why GS1 Toolkit?

Built for developers tired of:
- Unreadable regex spaghetti
- Half-baked GS1 implementations that fail on real scanner data
- Cryptic "invalid barcode" errors with no position info
- Libraries that crash on malformed input
- Choosing between "works in production" and "passes compliance tests"

**GS1 Toolkit gives you both:** LENIENT mode for real-world robustness, STRICT mode for compliance.

---

## GS1 Standards & Resources

This library implements the official GS1 standards for barcode encoding and Application Identifiers.

### Official GS1 Documentation

**GS1 Organization:**
- [GS1 Homepage](https://www.gs1.org/) - Global standards organization
- [GS1 General Specifications](https://www.gs1.org/standards/barcodes-epcrfid-id-keys/gs1-general-specifications) - Complete technical documentation
- [GS1 Standards Document](https://www.gs1.org/docs/barcodes/GS1_General_Specifications.pdf) - PDF specification (registration required)

**Application Identifiers:**
- [GS1 Application Identifier Reference](https://ref.gs1.org/ai/) - Official AI registry and lookup tool
- [AI Finder Tool](https://www.gs1.org/services/check-digit-calculator) - Interactive AI search and validation

**Barcode Formats:**
- [GS1-128 Specification](https://www.gs1.org/standards/barcodes/ean-upc) - Linear barcode format (formerly UCC/EAN-128)
- [GS1 DataMatrix](https://www.gs1.org/standards/gs1-datamatrix) - 2D barcode format specification
- [GS1 Digital Link](https://www.gs1.org/standards/gs1-digital-link) - Web-based GS1 identifiers

**Key Identifier Standards:**
- [GTIN (Global Trade Item Number)](https://www.gs1.org/standards/id-keys/gtin) - Product identification
- [GLN (Global Location Number)](https://www.gs1.org/standards/id-keys/gln) - Location/party identification
- [SSCC (Serial Shipping Container Code)](https://www.gs1.org/standards/id-keys/sscc) - Logistics unit identification

### Industry Resources

**Getting Started with GS1:**
- [GS1 Company Prefix](https://www.gs1.org/standards/id-keys/company-prefix) - How to obtain your GS1 prefix
- [Barcode Basics](https://www.gs1.org/standards/barcodes/barcode-basics) - Introduction to GS1 barcodes
- [Implementation Guidelines](https://www.gs1.org/standards/implementation-guidelines) - Industry-specific guides

**Tools & Calculators:**
- [Check Digit Calculator](https://www.gs1.org/services/check-digit-calculator) - GTIN/GLN validation tool
- [Barcode Generator](https://www.gs1.org/services/barcode-generator) - Create test barcodes

**Note:** This library is an independent implementation and is not affiliated with or endorsed by GS1. All trademarks are property of their respective owners.

---

## Features

### Format Support

**[GS1-128](https://www.gs1.org/standards/barcodes/ean-upc) (AI)value Syntax**
- Linear 1D barcode format (formerly UCC/EAN-128)
- Human-readable format: `(01)09501101530003(17)251231`
- Automatic format detection
- Mixed fixed and variable-length AIs

**[GS1 DataMatrix](https://www.gs1.org/standards/gs1-datamatrix) (FNC1-based)**
- 2D matrix barcode format
- Binary FNC1 separators (char 29)
- Optional leading FNC1 (in LENIENT mode)
- Ambiguous AI detection (prevents `10ABC17` being parsed incorrectly)

### Dual Parsing Modes

| Feature | LENIENT (default) | STRICT |
|---------|-------------------|--------|
| Missing leading FNC1 | ✅ Allowed | ❌ Required |
| Variable AI without FNC1 (final) | ✅ Allowed | ✅ Allowed |
| Variable AI without FNC1 (middle) | ❌ Detected & rejected | ❌ Rejected |
| Max length validation | ⚠️ Warning only | ❌ Enforced |
| Check digit validation (GTIN, GLN) | ⚠️ Not validated | ❌ Enforced |
| Ambiguous input | ❌ Detected & rejected | ❌ Rejected |
| **Best for** | Production systems | Compliance testing |

### Safety & Reliability

- **Thread-safe:** Parser instances are immutable - create once, reuse everywhere
- **No exceptions leak:** All parse errors return `Gs1ParseException` with position info
- **Hostile input safe:** Fuzz-tested, handles truncated/malformed data
- **Ambiguity detection:** Catches `10ABC17251231` (missing FNC1 between AIs)
- **Type-safe results:** Dates parsed as `LocalDate`, counts as `Integer`
- **DoS protection:** 10,000 character input limit prevents memory exhaustion

### Performance

**Actual JMH Benchmark Results** (OpenJDK 21, single-threaded):

| Format | Mode | Throughput | Operations/Second |
|--------|------|------------|-------------------|
| GS1-128 | LENIENT | 1,626 ops/ms | **1.63 million/sec** |
| GS1-128 | STRICT | 1,243 ops/ms | **1.24 million/sec** |
| DataMatrix | LENIENT | 1,159 ops/ms | **1.16 million/sec** |
| DataMatrix | STRICT | 1,102 ops/ms | **1.10 million/sec** |

**Performance Characteristics:**
- **Single-pass parsing** - no regex, no backtracking
- **GS1-128 format 40% faster** than DataMatrix (simpler format)
- **STRICT mode overhead:** 5-24% depending on format
  - GS1-128: 24% slower (check digit validation + extra compliance)
  - DataMatrix: 5% slower (minimal validation difference)
- **Zero allocations** in hot path (almost)

**Run benchmarks yourself:**
```bash
mvn package -pl gs1-benchmark -am
java -jar gs1-benchmark/target/gs1-benchmark-0.1.0.jar
```

Suitable for:
- Real-time scanner integrations (POS, warehouse)
- High-throughput batch processing
- Backend services (ERP, WMS, supply chain)

### Supported Application Identifiers

Currently supports **172 Application Identifiers** covering retail, food, pharma, logistics, and supply chain industries.

For the complete official AI registry, see [GS1 Application Identifier Reference](https://ref.gs1.org/ai/).

**Core Identifiers:**
- **00**: SSCC - Serial Shipping Container Code (18 digits, fixed)
- **01**: GTIN - Global Trade Item Number (14 digits, fixed) - ✅ **Check digit validated in STRICT mode**
- **02**: GTIN of Contained Trade Items (14 digits, fixed)
- **03**: GTIN of Trade Items Contained in a Logistic Unit (14 digits, fixed)

**Dates:**
- **11**: Production Date (YYMMDD, parsed as `LocalDate`)
- **12**: Due Date for Payment (YYMMDD, parsed as `LocalDate`)
- **13**: Packaging Date (YYMMDD, parsed as `LocalDate`)
- **15**: Best Before Date (YYMMDD, parsed as `LocalDate`)
- **16**: Sell By Date (YYMMDD, parsed as `LocalDate`)
- **17**: Expiry Date (YYMMDD, parsed as `LocalDate`)

**Product Information:**
- **10**: Batch/Lot Number (variable, max 20)
- **20**: Product Variant (2 digits, fixed)
- **21**: Serial Number (variable, max 20)
- **22**: Consumer Product Variant (variable, max 20)
- **30**: Variable Count (variable, max 8, parsed as `Integer`)
- **37**: Count of Trade Items (variable, max 8, parsed as `Integer`)

**Custom Product Identification:**
- **235**: Third Party Controlled Extension (variable, max 28)
- **240**: Additional Product Identification (variable, max 30)
- **241**: Customer Part Number (variable, max 30)
- **242**: Made-to-Order Variation Number (variable, max 6)
- **243**: Packaging Component Number (variable, max 20)
- **250**: Secondary Serial Number (variable, max 30)
- **251**: Reference to Source Entity (variable, max 30)
- **254**: GLN Extension Component (variable, max 20)

**Locations & Parties (GLN):**
- **400**: Customer Purchase Order Number (variable, max 30)
- **410**: Ship To / Deliver To GLN (13 digits, fixed) - ✅ **Check digit validated in STRICT mode**
- **411**: Bill To / Invoice To GLN (13 digits, fixed) - ✅ **Check digit validated in STRICT mode**
- **412**: Purchased From GLN (13 digits, fixed) - ✅ **Check digit validated in STRICT mode**
- **413**: Ship For / Deliver For / Forward To GLN (13 digits, fixed) - ✅ **Check digit validated in STRICT mode**
- **414**: Identification of Physical Location GLN (13 digits, fixed) - ✅ **Check digit validated in STRICT mode**
- **415**: Invoicing Party GLN (13 digits, fixed) - ✅ **Check digit validated in STRICT mode**
- **416**: Production or Service Location GLN (13 digits, fixed) - ✅ **Check digit validated in STRICT mode**
- **420**: Ship To Postal Code (variable, max 20)
- **710**: National Healthcare Reimbursement Number (variable, max 20)

**Variable Measure Trade Items (Complete Coverage):**

*Weight (kg):*
- **3100-3105**: Net Weight in kilograms (6 digits, 0-5 decimal places)
- **3300-3305**: Gross Weight in kilograms - logistics (6 digits, 0-5 decimal places)

*Weight (lb):*
- **3200-3205**: Net Weight in pounds (6 digits, 0-5 decimal places)

*Length (m):*
- **3110-3115**: Length/Distance in meters (6 digits, 0-5 decimal places)
- **3310-3315**: Length in meters - logistics (6 digits, 0-5 decimal places)

*Length (in):*
- **3210-3215**: Length in inches (6 digits, 0-5 decimal places)

*Length (ft):*
- **3220-3225**: Length in feet (6 digits, 0-5 decimal places)

*Width (m):*
- **3120-3125**: Width in meters (6 digits, 0-5 decimal places)
- **3320-3325**: Width in meters - logistics (6 digits, 0-5 decimal places)

*Width (in):*
- **3240-3245**: Width in inches (6 digits, 0-5 decimal places)

*Width (ft):*
- **3250-3255**: Width in feet (6 digits, 0-5 decimal places)

*Width (yd):*
- **3260-3265**: Width in yards (6 digits, 0-5 decimal places)

*Height (m):*
- **3130-3135**: Height in meters (6 digits, 0-5 decimal places)
- **3330-3335**: Height in meters - logistics (6 digits, 0-5 decimal places)

*Height (in):*
- **3270-3275**: Height in inches (6 digits, 0-5 decimal places)

*Height (ft):*
- **3280-3285**: Height in feet (6 digits, 0-5 decimal places)

*Height (yd):*
- **3290-3295**: Height in yards (6 digits, 0-5 decimal places)

*Area (m²):*
- **3140-3145**: Area in square meters (6 digits, 0-5 decimal places)
- **3340-3345**: Area in square meters - logistics (6 digits, 0-5 decimal places)

*Volume (L & m³):*
- **3150-3155**: Net Volume in liters (6 digits, 0-5 decimal places)
- **3160-3165**: Net Volume in cubic meters (6 digits, 0-5 decimal places)
- **3350-3355**: Volume in liters - logistics (6 digits, 0-5 decimal places)
- **3360-3365**: Volume in cubic meters - logistics (6 digits, 0-5 decimal places)

*Variable measure AIs use implied decimal places based on the last digit of the AI code (0-5 decimals).*

**Check Digit Validation:**
- In STRICT mode, GTIN (AI 01, 02, 03) and GLN (AI 410-416) check digits are validated using GS1 modulo-10 algorithm
- In LENIENT mode, check digits are not validated (accepts scanner data as-is)

**Extensible:** Register custom AIs via builder pattern

---

## Usage Guide

### Choosing Your Mode

**Use LENIENT mode (default) when:**
- Integrating with scanners (handle real-world deviations)
- Processing data from external sources
- You need "it just works" reliability
- Example: warehouse scanning, POS integration

**Use STRICT mode when:**
- Building compliance testing tools
- Validating data before external submission
- Regulated industries requiring audit trails
- Example: pharmaceutical track & trace

### LENIENT Mode (Default)

Optimized for production systems with imperfect data:

```java
Gs1Parser parser = Gs1Parser.defaultParser();

// Accepts GS1-128 format
parser.parse("(01)12345678901231(10)LOT42");

// Accepts DataMatrix without leading FNC1
parser.parse("0112345678901231\u001D10LOT42");

// Allows final variable AI without FNC1
parser.parse("\u001D10BATCH123"); // ✅ OK - last element

// BUT: Detects ambiguous input
parser.parse("10ABC17251231"); // ❌ Error: "17" looks like AI 17
```

### STRICT Mode

Enforces GS1 specification:

```java
Gs1Parser parser = Gs1Parser.strictParser();

// Requires leading FNC1 for DataMatrix
parser.parse("\u001D0112345678901231\u001D10LOT42"); // ✅ OK
parser.parse("0112345678901231\u001D10LOT42");       // ❌ Error

// Requires FNC1 between variable AIs
parser.parse("\u001D10BATCH\u001D17251231"); // ✅ OK
parser.parse("\u001D10BATCH17251231");        // ❌ Error

// Enforces max length
parser.parse("\u001D10" + "A".repeat(25) + "\u001D"); // ❌ Error: exceeds max 20

// Validates check digits (GTIN, GLN)
parser.parse("(01)09501101530003");  // ✅ OK - valid check digit (3)
parser.parse("(01)09501101530004");  // ❌ Error: invalid check digit
parser.parse("(410)0614141123452"); // ✅ OK - valid GLN check digit
```

### Error Handling

All parse errors include position info:

```java
try {
    Gs1Parser.defaultParser().parse("(99)INVALID");
} catch (Gs1ParseException e) {
    System.err.println(e.getMessage());    // "Unknown AI 99"
    System.err.println(e.getPosition());   // 1 (position of '99')
    System.err.println(e.getCode());       // UNKNOWN_AI
}
```

Error codes:
- `INVALID_FORMAT`: Malformed structure
- `UNKNOWN_AI`: AI not in registry
- `INVALID_LENGTH`: Value length mismatch
- `VALUE_PARSE_ERROR`: Value parsing failed (e.g., invalid date)

### Custom Parser Configuration

**Minimal Parser with Specific AIs:**

This example shows how to create a parser supporting only GTIN (01), Length (3110), and Batch/Lot (10):

```java
import no.nofuzz.gs1.parser.Gs1Parser;
import no.nofuzz.gs1.model.Gs1ComplianceMode;
import no.nofuzz.gs1.ai.*;
import java.util.Map;

// Create parser with only 3 specific AIs
AiRegistry customRegistry = new AiRegistry(Map.of(
    // AI 01: GTIN - 14 digits fixed, numeric, with check digit validation
    "01", new ApplicationIdentifier(
        "01",                      // AI code
        14,                        // Fixed length: 14 digits
        14,                        // Max length: 14 digits
        false,                     // Not variable length
        CharacterSet.NUMERIC,      // Numeric only
        true,                      // Validate check digit (in STRICT mode)
        v -> v                     // Return value as-is
    ),

    // AI 3110: Length in meters (0 decimals) - 6 digits fixed, numeric
    "3110", new ApplicationIdentifier(
        "3110",                    // AI code
        6,                         // Fixed length: 6 digits
        6,                         // Max length: 6 digits
        false,                     // Not variable length
        CharacterSet.NUMERIC,      // Numeric only
        false,                     // No check digit
        v -> v.replaceFirst("^0+(?!$)", "")  // Strip leading zeros
    ),

    // AI 10: Batch/Lot Number - variable length, alphanumeric
    "10", new ApplicationIdentifier(
        "10",                      // AI code
        null,                      // No fixed length
        20,                        // Max length: 20 chars
        true,                      // Variable length
        CharacterSet.ALPHANUMERIC, // Alphanumeric
        false,                     // No check digit
        v -> v                     // Return value as-is
    )
));

// Create parser in LENIENT mode
Gs1Parser parser = new Gs1Parser(customRegistry, Gs1ComplianceMode.LENIENT);

// Use the custom parser
Gs1Result result = parser.parse("(01)09501101530003(3110)005000(10)BATCH42");
String gtin = (String) result.getOrThrow("01");      // "09501101530003"
String length = (String) result.getOrThrow("3110");  // "5000" (meters)
String batch = (String) result.getOrThrow("10");     // "BATCH42"

// This parser will reject unknown AIs
parser.parse("(17)251231");  // ❌ Throws Gs1ParseException: Unknown AI 17
```

**Using the Builder Pattern (Add to Standard AIs):**

```java
import no.nofuzz.gs1.parser.Gs1Parser;
import no.nofuzz.gs1.model.Gs1ComplianceMode;
import no.nofuzz.gs1.ai.*;

// Start with standard AIs and add custom ones
Gs1Parser parser = Gs1Parser.builder()
    .mode(Gs1ComplianceMode.STRICT)
    .registerAi("99", new ApplicationIdentifier(
        "99",                          // AI code
        null,                          // No fixed length
        10,                            // Max length
        true,                          // Variable length
        CharacterSet.ALPHANUMERIC,     // Character set
        false,                         // No check digit validation
        v -> v.toUpperCase()           // Value parser
    ))
    .build();

// Or start fresh without standard AIs
Gs1Parser minimalParser = Gs1Parser.builder()
    .withoutStandardAis()
    .registerAi("01", gtinDefinition)
    .registerAi("10", batchDefinition)
    .build();
```

**Understanding AI Specifications:**

| Parameter | Description | Example Values |
|-----------|-------------|----------------|
| `code` | AI identifier | `"01"`, `"10"`, `"3110"` |
| `fixedLength` | Exact length (or `null` if variable) | `14`, `6`, `null` |
| `maxLength` | Maximum allowed length | `14`, `20`, `30` |
| `isVariable` | Whether AI has variable length | `false` (fixed), `true` (variable) |
| `characterSet` | Allowed characters | `NUMERIC`, `ALPHANUMERIC` |
| `validateCheckDigit` | Enable check digit validation (STRICT mode) | `true`, `false` |
| `valueParser` | Function to transform value | `v -> v`, `v -> v.toUpperCase()` |

**Variable Measure AIs (3100-3365 series):**

For variable measure AIs like 3110 (length), 3102 (weight with 2 decimals), etc., the last digit of the AI code indicates decimal places:
- `3110` = 0 decimals → "005000" becomes "5000"
- `3111` = 1 decimal → "005000" becomes "500.0"
- `3112` = 2 decimals → "005000" becomes "50.00"

Use `StandardAis.parseVariableMeasure(aiCode, value)` for proper formatting, or implement custom logic.

**Thread Safety Note:**
- `Gs1Parser` instances are **immutable and thread-safe**
- Recommended: Create one parser instance and reuse across threads
- Custom AI value parsers MUST return immutable objects (String, LocalDate, Integer, etc.)
- Do NOT return mutable collections or objects that can be modified after parsing

---

## Real-World Examples

### Warehouse Scanner Integration

```java
public class ScannerProcessor {
    private final Gs1Parser parser = Gs1Parser.defaultParser();

    public void processScannedBarcode(String scannerData) {
        try {
            Gs1Result result = parser.parse(scannerData);

            String gtin = (String) result.getOrThrow("01");
            String lot = (String) result.getOrThrow("10");
            LocalDate expiry = (LocalDate) result.getOrThrow("17");

            // Update inventory system
            inventory.receive(gtin, lot, expiry);

        } catch (Gs1ParseException e) {
            logger.error("Invalid barcode at position {}: {}",
                         e.getPosition(), e.getMessage());
            notifyOperator("Scan failed - please try again");
        }
    }
}
```

### Compliance Validation Service

```java
public class ComplianceValidator {
    private final Gs1Parser parser = Gs1Parser.strictParser();

    public ValidationResult validate(String barcode) {
        try {
            Gs1Result result = parser.parse(barcode);
            return ValidationResult.success(result);
        } catch (Gs1ParseException e) {
            return ValidationResult.failure(
                e.getCode(),
                e.getMessage(),
                e.getPosition()
            );
        }
    }
}
```

---

## Architecture

**Module Structure:**
- **gs1-core**: Core parsing library (zero dependencies)
- **gs1-cli**: Command-line tool
- **gs1-test**: Comprehensive test suite
- **gs1-benchmark**: JMH performance benchmarks

**Design Principles:**
1. **Correctness over cleverness** - readable, maintainable code
2. **Robustness over completeness** - handles real-world data
3. **Developer experience over specification rigidity** - practical compliance

---

## Project Status

- **Production-ready** core parser
- **Comprehensive tests**: 120 tests including unit, integration, fuzz, and regression tests
- **Battle-tested** with real scanner data
- **172 Application Identifiers** implemented covering retail, food, pharma, and logistics industries
- **Complete variable measure support**: Weight, length, width, height, area, and volume in metric and imperial units
- **Full GLN support**: All location and party identification AIs (410-416) with check digit validation
- **Not yet published** to Maven Central (coming soon)

---

## Contributing

Contributions welcome! Areas we'd love help with:
- **Additional Application Identifiers** - Check the [official GS1 AI registry](https://ref.gs1.org/ai/) for AIs not yet implemented
- **More test cases** - Especially edge cases from real scanners and production data
- **Documentation improvements** - Examples, tutorials, industry-specific guides
- **Performance optimizations** - Profiling results and optimization PRs welcome

**Before contributing AIs:** Please verify against the [GS1 General Specifications](https://www.gs1.org/standards/barcodes-epcrfid-id-keys/gs1-general-specifications) to ensure correctness.

See issues for current priorities.

---

## License

This software is free for non-commercial use and requires a commercial license for business use.

See [LICENSE.txt](LICENSE.txt) for complete terms and conditions.

---

## Support

- **Issues**: Report bugs or request features via [GitHub Issues](https://github.com/anthropics/gs1-toolkit/issues)
- **Questions**: Open a discussion or issue

---

## Comparison with Alternatives

| Feature | GS1 Toolkit | Other Solutions |
|---------|-------------|-----------------|
| Zero dependencies | ✅ | ❌ Most require Apache Commons, etc. |
| Both GS1-128 & DataMatrix | ✅ | ⚠️ Often only one format |
| Dual mode (lenient/strict) | ✅ | ❌ All-or-nothing compliance |
| Position-aware errors | ✅ | ❌ Generic error messages |
| Ambiguity detection | ✅ | ❌ Silently misparses |
| Fuzz tested | ✅ | ⚠️ Rarely tested with hostile input |
| Performance | 1.1-1.6M ops/sec (JMH) | Varies widely |

---

## Acknowledgments

Built with frustration from dealing with poorly-implemented GS1 parsers in production systems.

**We believe:**
- Real-world data is messy - parsers should handle it
- Compliance is important - but not at the cost of usability
- Developers deserve helpful error messages
- Performance matters when you're scanning 10,000 items/hour
