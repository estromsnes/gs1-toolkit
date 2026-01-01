package no.nofuzz.gs1.cli;

import no.nofuzz.gs1.parser.Gs1Parser;

public class Gs1Cli {

    public static void main(String[] args) {
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
}