# gs1-toolkit
Parse, validate and understand GS1 barcodes - without losing your sanity.

GS1 Toolkit is a small, focused Java library for parsing and validating GS1-128 / GS1 formatted barcodes into structured, type-safe data.

Built for developers who are tired of:
 * unreadable regexes
 * half-baked GS1 implementations
 * cryptic “invalid barcode” errors


## GS1 Compliance & Design Philosophy

GS1 Toolkit is designed to be **robust, developer-friendly, and production-safe**, while covering the vast majority of real-world GS1 use cases.

It aims for **practical GS1 compliance**, not a rigid line-by-line implementation of the full GS1 specification.


### ✔️ What is supported
**GS1-128**
 * Parsing of GS1-128 barcodes using (AI)value syntax
 * Support for common fixed- and variable-length Application Identifiers
 * Clear separation between AI parsing and value interpretation
 * Precise, position-aware error reporting

**GS1 DataMatrix**
 * Sequential parsing of GS1 DataMatrix–formatted data
 * Explicit handling of the FNC1 separator
 * Correct handling of:
   * fixed-length AIs (no separator required)
   * variable-length AIs (terminated by FNC1 or end-of-input)
 * Robust detection of truncated or malformed input
 * Safe handling of scanner noise and invalid characters

### Parsing Modes: Lenient vs Strict

GS1 Toolkit supports two parsing modes to balance **real-world robustness** with **formal GS1 compliance**.

🔹 LENIENT mode (default)

**LENIENT mode** is optimized for production systems and imperfect real-world data.

This is the default mode used by `Gs1Parser.defaultParser()`.

**Characteristics:**
 * Tolerates missing leading FNC1 in GS1 DataMatrix
 * Allows variable-length AIs to omit FNC1 if they are the final element
 * Accepts common scanner deviations and legacy data formats
 * Focuses on robustness and operational stability

**Recommended for:**
 * Backend services
 * Batch processing
 * Scanner integrations
 * POS / ERP / WMS systems
 * Systems ingesting external or uncontrolled data

Example:
```
Gs1Parser parser = Gs1Parser.defaultParser();
Gs1Result result = parser.parse(barcode);
```

🔒 STRICT mode

**STRICT mode** enforces GS1 rules more rigorously and aims to be as close to the GS1 specification as possible.

STRICT mode must be enabled explicitly.

**Characteristics:**
 * Requires leading FNC1 for GS1 DataMatrix
 * Enforces FNC1 termination for variable-length AIs (unless final field)
 * Uses longest-match AI resolution (2–4 digits)
 * Enforces per-AI maximum length constraints
 * Rejects malformed or ambiguous input early

**Recommended for:**
 * Compliance-sensitive environments
 * Regulated industries
 * Validation pipelines
 * Certification and conformance testing
 * Controlled input sources

**Example:**
```
Gs1Parser parser = Gs1Parser.strictParser();
Gs1Result result = parser.parse(barcode);
```

🧭 Design philosophy

GS1 Toolkit intentionally separates **parsing correctness** from **operational robustness**.

* **LENIENT mode** reflects how GS1 data appears in real systems
* **STRICT mode** reflects how GS1 data is defined in the specification

This allows consumers to explicitly choose the level of strictness required for their use case, without compromising safety or performance.

### Performance considerations

STRICT mode performs additional validation and introduces a small performance overhead.

Benchmarks show:
 * LENIENT mode: highest throughput
 * STRICT mode: ~10–20% overhead due to additional checks

Both modes are suitable for high-throughput backend processing.

### 🔒 Robustness & Safety Guarantees

Regardless of compliance mode, GS1 Toolkit guarantees:
 * No JVM runtime exceptions leak from parsing
 * All parse failures result in a Gs1ParseException
 * Truncated, malformed, or hostile input is handled safely
 * Extensive test coverage including:
   * negative test cases
   * fuzz/property-based testing
   * regression corpus tests

This makes GS1 Toolkit suitable for:
 * production batch jobs
 * scanner integrations
 * backend services
 * support and debugging tools


### Performance

GS1 Toolkit is optimized for high-throughput backend processing.

Benchmarks (JMH):
 *  ~1–2 million parses per second
 * STRICT mode adds ~10–20% overhead due to additional validation
 *  No regex usage
 *  Single-pass parsing

The library is suitable for:
 * batch processing
 * scanner ingestion pipelines
 *  real-time backend services

We benchmarked it. Strict GS1 compliance costs ~15%. You choose.

### Summary

GS1 Toolkit prioritizes:
 * **Correctness over clevernes**
 * **Stability over theoretical completeness**
 * **Developer experience over specification rigidity**

It is designed to handle GS1 data **as it actually appears in real systems**, not only as it exists on paper.