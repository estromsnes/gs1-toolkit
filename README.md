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

### ⚠️ Intentional deviations from strict GS1 specification

GS1 Toolkit intentionally applies a **lenient parsing strategy** in certain areas to better match real-world data and reduce operational friction.

These deviations are documented and tested.

**Lenient DataMatrix handling**
 * The first character is not required to be FNC1 (many scanners omit this in practice)
 * Variable-length fields are allowed to omit FNC1 **if they are the final element**
 * AI codes are currently parsed as **2-digit identifiers** (extended AI support is planned)

These choices significantly improve interoperability with:
 * legacy scanners
 * batch-imported barcode data
 * production systems with imperfect input sources

### ❌ What is not currently enforced

The following GS1 rules are **not strictly enforced** in the current version:
 * Per-AI maximum length constraints
 * Full numeric-only validation for all numeric AIs
 * Mandatory leading FNC1 for DataMatrix input
 * Longest-match parsing for 3- and 4-digit AIs

These constraints can be added in future versions or enabled via a **strict parsing mode**.

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


### Summary

GS1 Toolkit prioritizes:
 * **Correctness over clevernes**
 * **Stability over theoretical completeness**
 * **Developer experience over specification rigidity**

It is designed to handle GS1 data **as it actually appears in real systems**, not only as it exists on paper.