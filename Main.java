import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  public static final String BAD_REGEX = ".* (.*)\\[(.*)\\]:.*";
  public static final String BETTER_REGEX = "[12]\\d{3}-[01]\\d-[0-3]\\d (.*)\\[(.*)\\]:.*";
  public static final String BEST_REGEX = "[12]\\d{3}-[01]\\d-[0-3]\\d ([^ \\[]*?)\\[([^\\]]*?)\\]:.*";
  public static final String MATCHING_INPUT = "2014-08-26 app[web.1]: 50.0.134.125 - - [26/Aug/2014 00:27:41] \"GET / HTTP/1.1\" 200 14 0.0005";
  public static final String NON_MATCHING_INPUT = "50.0.134.125 - - [26/Aug/2014 00:27:41] \\\"GET / HTTP/1.1\\\" 200 14 0.0005";
  public static final int NUM_RUNS = 1000000;
  public static final String DATA_FILE_PATH = "/Users/liz/Documents/regex_experiment_runs.txt";
  public static FileWriter dataFileWriter;

  public static void main(String[] args) throws Exception {
    dataFileWriter = new FileWriter(DATA_FILE_PATH, true);
    dataFileWriter.write("**************BEGIN EXPERIMENT *****************\n");

    runExperiment(BAD_REGEX, "bad", NON_MATCHING_INPUT);
    runExperiment(BETTER_REGEX, "better", NON_MATCHING_INPUT);
    runExperiment(BEST_REGEX, "best", NON_MATCHING_INPUT);
    runExperiment(BAD_REGEX, "bad", MATCHING_INPUT);
    runExperiment(BETTER_REGEX, "better", MATCHING_INPUT);
    runExperiment(BEST_REGEX, "best", MATCHING_INPUT);

    dataFileWriter.write("**************END EXPERIMENT*****************\n\n\n");
    dataFileWriter.close();
  }

  public static Long runExperiment(String regex, String regexDescripton, String input) throws Exception {
    Pattern p = Pattern.compile(regex);

    boolean matches = false;
    Long start = System.currentTimeMillis();
    for(int i = 0; i < NUM_RUNS; i++) {
      Matcher m = p.matcher(input);
      matches |= m.matches();
    }
    Long timeElapsed = System.currentTimeMillis() - start;

    String result = String.format(
        "Took %dms to match the %s regex with %s input",
        timeElapsed, regexDescripton, matches ? "matching" : "non matching");
    System.out.println(result);
    dataFileWriter.write(result+"\n");
    dataFileWriter.write(String.format("Pattern was: %s, Input was: %s\n\n", regex, input));

    return timeElapsed;
  }
}
