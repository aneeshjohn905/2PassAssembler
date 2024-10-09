import java.io.*;
import java.util.HashMap;
import java.util.Map;

// The Assembler class that will run in the background
public class TwoPassAssembler {

    private String inputFile;
    private String optabFile;
    private Map<String, String> optab = new HashMap<>();
    private Map<String, Integer> symtab = new HashMap<>();
    private int locctr = 0;
    private int start = 0;
    private int length = 0;

    public TwoPassAssembler(String inputFile, String optabFile) {
        this.inputFile = inputFile;
        this.optabFile = optabFile;
    }

    // Load the opcode table
    public void loadOptab() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(optabFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            optab.put(parts[0], parts[1]);
        }
        reader.close();
    }

    // First pass: generate symbol table and intermediate file
    public void passOne() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter intermediateWriter = new BufferedWriter(new FileWriter("intermediate.txt"));
        BufferedWriter symtabWriter = new BufferedWriter(new FileWriter("symtab.txt"));

        String line = reader.readLine();
        String[] parts = line.split("\\s+");

        // Check if the first line has "START"
        if (parts[1].equals("START")) {
            start = Integer.parseInt(parts[2]);
            locctr = start;
            intermediateWriter.write("\t" + parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\n");
            line = reader.readLine();
        } else {
            locctr = 0;
        }

        while (line != null) {
            parts = line.split("\\s+");
            if (parts[1].equals("END")) break;

            intermediateWriter.write(locctr + "\t" + parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\n");

            if (!parts[0].equals("-")) {
                symtab.put(parts[0], locctr);
                symtabWriter.write(parts[0] + "\t" + locctr + "\n");
            }

            if (optab.containsKey(parts[1])) {
                locctr += 3;
            } else if (parts[1].equals("WORD")) {
                locctr += 3;
            } else if (parts[1].equals("RESW")) {
                locctr += 3 * Integer.parseInt(parts[2]);
            } else if (parts[1].equals("RESB")) {
                locctr += Integer.parseInt(parts[2]);
            } else if (parts[1].equals("BYTE")) {
                locctr += parts[2].length() - 3;
            }

            line = reader.readLine();
        }

        intermediateWriter.write(locctr + "\t" + parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\n");
        length = locctr - start;

        reader.close();
        intermediateWriter.close();
        symtabWriter.close();
    }

    // Second pass: generate object code
    public void passTwo() throws IOException {
        BufferedReader intermediateReader = new BufferedReader(new FileReader("intermediate.txt"));
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter("output.txt"));

        String line = intermediateReader.readLine();
        String[] parts = line.split("\\s+");

        // Handle "START" directive
        if (parts[1].equals("START")) {
            outputWriter.write("H^ " + parts[0] + "^ " + parts[2] + "^ " + String.format("%06X", length) + "\n");
            line = intermediateReader.readLine();
        } else {
            outputWriter.write("H^ " + parts[0] + "^ 0000^ " + String.format("%06X", length) + "\n");
        }

        outputWriter.write("T^ ");
        while (line != null) {
            parts = line.split("\\s+");
            if (parts[2].equals("END")) break;

            if (optab.containsKey(parts[2])) {
                String machineCode = optab.get(parts[2]);
                int address = symtab.getOrDefault(parts[3], 0);
                outputWriter.write(String.format("%06X", locctr) + "^ " + machineCode + String.format("%04X", address) + "^ ");
            } else if (parts[2].equals("WORD")) {
                outputWriter.write(String.format("%06X", locctr) + "^ " + String.format("%06X", Integer.parseInt(parts[3])) + "^ ");
            } else if (parts[2].equals("BYTE")) {
                outputWriter.write(String.format("%06X", locctr) + "^ " + parts[3].substring(2, parts[3].length() - 1) + "^ ");
            }

            line = intermediateReader.readLine();
        }

        outputWriter.write("\nE^ " + String.format("%06X", start) + "\n");

        intermediateReader.close();
        outputWriter.close();
    }
}