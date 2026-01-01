package no.nofuzz.gs1.cli;

import no.nofuzz.gs1.parser.Gs1Parser;

public class Gs1Cli {

    private static final String VERSION = "0.1.0";

    public static void main(String[] args) {
        printBanner();

        if (args.length < 2 || !"parse".equals(args[0])) {
            System.out.println("Usage: gs1 parse \"<barcode>\"");
            System.exit(2);
        }

        try {
            var result = Gs1Parser.defaultParser().parse(args[1]);
            System.out.println(result.asMap());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void printBanner() {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                   GS1 Toolkit v" + VERSION + "                        ║");
        System.out.println("║                                                                ║");
        System.out.println("║  FREE for personal, educational, and evaluation use            ║");
        System.out.println("║                                                                ║");
        System.out.println("║  ⚠️  COMMERCIAL LICENSE REQUIRED for business use              ║");
        System.out.println("║                                                                ║");
        System.out.println("║  Commercial use includes:                                      ║");
        System.out.println("║    • Production systems in for-profit organizations            ║");
        System.out.println("║    • Internal business applications                            ║");
        System.out.println("║    • Revenue-generating products or services                   ║");
        System.out.println("║                                                                ║");
        System.out.println("║  License pricing: $1000/year per legal entity                  ║");
        System.out.println("║  60-day free trial available                                   ║");
        System.out.println("║                                                                ║");
        System.out.println("║  Contact: estromsnes@gmail.com                                 ║");
        System.out.println("║  Website: https://nofuzz.no/gs1-toolkit                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
}