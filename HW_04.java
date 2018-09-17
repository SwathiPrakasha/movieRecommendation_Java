import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.Serializable;

public class HW_04 implements Serializable {

	private static int totalNonAscii = 0;
	private static int totalNonAsciiLines = 0;
	private static int lineCount = 0;
	private static final String outFile = "movie_names2.txt";
	private static ObjectOutputStream output;

	static int[][] matrix;
	static int matrixRows = 0, matrixCol = 0;

	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			System.out.printf("Need two files, received \n", args.length);
			System.exit(-1);
		}

		// creating an object of FileWriter
		Scanner inputFile1 = null;
		Scanner inputFile2 = null;
		inputFile1 = new Scanner(Paths.get(args[0]), "ISO-8859-1"); // Reading
		processFile1(inputFile1);

		inputFile2 = new Scanner(Paths.get(args[1]), "ISO-8859-1");
		processFile2(inputFile2);

	}

	private static String fetchMap(String line) {
		Map<Character, Character> myMap = new HashMap<>();

		myMap.put('\u00e9', '\u0065');
		myMap.put('\u00c1', '\u0041');
		myMap.put('\u00f6', '\u006f');
		myMap.put('\u00e8', '\u0065');

		totalNonAsciiLines++;

		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);

			if (myMap.containsKey(ch)) {
				System.out.println("non-Ascii char: " + ch + " in line " + lineCount + " char " + (i + 1));
				System.out.println(line + "\n");
				line = line.substring(0, i) + myMap.get(ch) + line.substring(i + 1);
				totalNonAscii++;
			}
		}
		return line;
	}

	public static void processFile1(Scanner inputFile) {

		BufferedWriter bw = null; // creating an object of BufferedWriter
		FileWriter fw = null;
		String movieNum = null;
		String pattern = "(^[1-9][\\d]*)\\|([\u0000-\u007F]+[\\h]*[\\:]*)( [\\(][1-9]\\d{3}[\\)])*";
		Pattern r = Pattern.compile(pattern);

		try {
			fw = new FileWriter(outFile);
			bw = new BufferedWriter(fw);
			while (inputFile.hasNextLine()) {
				lineCount++;
				String input = inputFile.nextLine();

				// matcher m is used to find high order chars
				Matcher m = r.matcher(input);
				if (!m.matches()) {
		        // fetchmap function replaces high ordered by relative letter
					input = fetchMap(input);
				}

				// Matcher pat to print only group1 & group2 inthe outputfile
				Matcher pat = r.matcher(input);
				if (pat.matches()) {
					movieNum = pat.group(1);
					movieNum = String.format("%04d", Integer.parseInt(movieNum));
					bw.write(movieNum + "\t");
					bw.write(pat.group(2) + "\n");
				}
			}
			System.out.println("NO. of high order chars:          " + totalNonAscii);
			System.out.println("Lines with the high order chars:  " + totalNonAsciiLines);
			System.out.println("Total Record Count :              " + lineCount);
		}

		catch (IOException ioException) {
			System.err.println("Error opening file. Terminating.");
			System.exit(1); // terminate the program
		}

		catch (NoSuchElementException elementException) {
			System.out.println("File improperly formed. Terminating.");
			System.exit(1);
		} catch (IllegalStateException stateException) {
			System.out.println("Error reading from file. Terminating.");
			System.exit(1);
		} finally {
			try {
				inputFile.close();
				if (bw != null)
					bw.close();
				if (bw != null)
					fw.close();
			}

			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void processFile2(Scanner input) {

		String matrixLine = null;
		String[] items = null;
		matrix = new int[2000][1000];
		while (input.hasNextLine()) {

			matrixLine = input.nextLine();

			items = matrixLine.split(";", -1); // pass -1 as the second
												// argument to split
												// otherwise it removes
												// empty strings.
			matrixCol = items.length;

			for (int i = 0; i < items.length; i++) {
				if (items[i].isEmpty()) {
					items[i] = "0";
				}

				    matrix[matrixRows][i] = Integer.parseInt(items[i]);
				}
			
			matrixRows++;

		}
		input.close();

		System.out.println("*** No. of rows(movies) in matrix       =  " + matrixRows);
		System.out.println("*** No. of columns(reviewers) in matrix =  " + matrixCol);

            fileSerilize();
	}


  public static void fileSerilize()
           {
              try
              {
                 output = new ObjectOutputStream(
                 Files.newOutputStream(Paths.get("movie-matrix2.ser")));
                  output.writeObject(matrixRows-1);
                  output.writeObject(matrixCol-1);
                  output.writeObject(matrix);

                  if (output != null)
                     output.close();

              }
              catch (IOException ioException)
              {
                 System.err.println("Error opening file. Terminating.");
                 System.exit(1); // terminate the program
              }
              catch (NoSuchElementException elementException)
              {
                    System.err.println("Invalid input. Please try again.");

              }
           }


}
