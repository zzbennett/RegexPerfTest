import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTricksExperiments {
  public static final int NUM_RUNS = 1000000;
  public static final int NUM_WARM_UP_RUNS = 4;

  public static final String DATA_FILE_PATH = "/Users/liz/Documents/regex_tricks_experiment_runs.txt";
  public static FileWriter dataFileWriter;

  public static void main(String[] args) throws Exception {
    dataFileWriter = new FileWriter(DATA_FILE_PATH, true);

    lazyQuantifierExperiment();
    atomicGroupExperiment();
    anchorExperiment();
    characterClassExperiment();
    orderMattersExperiment();

    dataFileWriter.close();
  }

  public static void characterClassExperiment() throws Exception {
    write("**************BEGIN CHARACTER CLASS EXPERIMENT *****************");
    String vagueRegex = "field1=(.*) field2=(.*) field3=(.*) field4=(.*).*";
    String characterClassRegex = "field1=([^ ]*) field2=([^ ]*) field3=([^ ]*) field4=([^ ]*).*";

    String matchingInput = "field1=cat field2=dog field3=parrot field4=mouse field5=hamster";
    String nonMatchingInput = "field1=cat dog parrot mouse";
    String nonMatchingInput2 = "field1=cat field2=dog field3=parrot field5=mouse";

    for(int i = 0; i < NUM_WARM_UP_RUNS; i++) {
      runExperiment(vagueRegex, "VAGUE REGEX", matchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(characterClassRegex, "CHARACTER CLASS REGEX", matchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(vagueRegex, "VAGUE REGEX", nonMatchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(characterClassRegex, "CHARACTER CLASS REGEX", nonMatchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(vagueRegex, "VAGUE REGEX (on slightly non-matching input)", nonMatchingInput2, i==NUM_WARM_UP_RUNS-1);
      runExperiment(characterClassRegex, "CHARACTER CLASS REGEX (on slightly non-matching input)", nonMatchingInput2, i==NUM_WARM_UP_RUNS-1);
    }
    write("**************END EXPERIMENT*****************\n\n");
  }

  public static void lazyQuantifierExperiment() throws Exception {
    write("**************BEGIN LAZY QUANTIFIER EXPERIMENT *****************");
    String greedyRegex = ".*Lock_time: (\\d\\.\\d+).*";
    String lazyRegex = ".*?Lock_time: (\\d\\.\\d+).*";
    String greedyDoubleRegex = ".*Lock_time: (\\d\\.\\d+).*Rows_examined: (\\d+).*";
    String lazyDoubleRegex = ".*?Lock_time: (\\d\\.\\d+).*?Rows_examined: (\\d+).*";

    String matchingInput = "# Query_time: 0.304 Lock_time: 0.81 Rows_sent: 1 Rows_read: 4505295 Rows_affected: 0 Rows_examined: 1";
    String matchingAtTheEndInput = "# Query_time: 0.304 Rows_sent: 1 Rows_read: 4505295 Rows_affected: 0 Lock_time: 0.81 Rows_examined: 1";
    String nonMatchingInput = "# Query_time: 0.304 Rows_sent: 1 Query_time: 0.304 Rows_sent: 1 Query_time: 0.304 Rows_sent: 1 Rows_examined: 1";

    for(int i = 0; i < NUM_WARM_UP_RUNS; i++) {
      runExperiment(greedyRegex, "GREEDY", matchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(lazyRegex, "LAZY", matchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(greedyRegex, "GREEDY (matches at end)", matchingAtTheEndInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(lazyRegex, "LAZY (matches at end)", matchingAtTheEndInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(greedyRegex, "GREEDY", nonMatchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(lazyRegex, "LAZY", nonMatchingInput, i == NUM_WARM_UP_RUNS - 1);

      if(i==NUM_WARM_UP_RUNS-1) write("***************");

      runExperiment(greedyDoubleRegex, "GREEDY DOUBLE", matchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(lazyDoubleRegex, "LAZY DOUBLE", matchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(greedyDoubleRegex, "GREEDY DOUBLE (matches at end)", matchingAtTheEndInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(lazyDoubleRegex, "LAZY DOUBLE (matches at end)", matchingAtTheEndInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(greedyDoubleRegex, "GREEDY DOUBLE ", nonMatchingInput, i==NUM_WARM_UP_RUNS-1);
      runExperiment(lazyDoubleRegex, "LAZY DOUBLE", nonMatchingInput, i == NUM_WARM_UP_RUNS - 1);
    }
    write("**************END EXPERIMENT*****************\n\n");
  }


  public static void atomicGroupExperiment() throws Exception {
    write("**************BEGIN ATOMIC GROUP EXPERIMENT *****************");
    String greedyRegex = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*";
    String atomicRegex = "^(\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}+).*";
    String matchingInput = "107.21.20.1 - - [07/Dec/2012:18:55:53 -0500] \"GET /\" 200 2144";
    String nonMatchingInput = "9.21.2014 non matching text that kind of matches";

    for(int i = 0; i < NUM_WARM_UP_RUNS; i++) {
      runExperiment(greedyRegex, "GREEDY", matchingInput, i == NUM_WARM_UP_RUNS - 1);
      runExperiment(atomicRegex, "ATOMIC", matchingInput, i == NUM_WARM_UP_RUNS - 1);
      runExperiment(greedyRegex, "GREEDY", nonMatchingInput, i == NUM_WARM_UP_RUNS - 1);
      runExperiment(atomicRegex, "ATOMIC", nonMatchingInput, i == NUM_WARM_UP_RUNS - 1);
    }
    write("**************END EXPERIMENT*****************\n\n");
  }

  public static void anchorExperiment() throws Exception {
    write("**************BEGIN ANCHOR EXPERIMENT *****************");
    String unAnchoredRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    String anchoredRegex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    String matchingInput = "107.21.20.1 - - [07/Dec/2012:18:55:53 -0500] \"GET /extension/bsupport/design/cl/images/btn_letschat.png HTTP/1.1\" 200 2144";
    String nonMatchingInput = "[07/Dec/2012:23:57:13 +0000] 1354924633 GET \"/favicon.ico\" \"\" HTTP/1.1 200 82726 \"-\" \"ELB-HealthChecker/1.0\"";

    for(int i = 0; i < NUM_WARM_UP_RUNS; i++) {
      runExperiment(unAnchoredRegex, "UN-ANCHORED", matchingInput, i == NUM_WARM_UP_RUNS - 1, true);
      runExperiment(anchoredRegex, "ANCHORED", matchingInput, i == NUM_WARM_UP_RUNS - 1, true);
      runExperiment(unAnchoredRegex, "UN-ANCHORED", nonMatchingInput, i == NUM_WARM_UP_RUNS - 1, true);
      runExperiment(anchoredRegex, "ANCHORED", nonMatchingInput, i == NUM_WARM_UP_RUNS - 1, true);
    }
    write("**************END EXPERIMENT*****************\n\n");
  }

  public static void orderMattersExperiment() throws Exception {
    write("**************BEGIN ORDERING EXPERIMENT *****************");
    String goodOrderedRegex = ".*(?<=\"field5\" : \"|'field5' : ')([^\"']*).*";
    String badOrderedRegex = ".*(?<='field5' : '|\"field5\" : \")([^'\"]*).*";
    String matching = "{\"field1\" : \"wool\", \"field2\" : \"silk\", \"field3\" : \"linen\", \"field4\" : \"merino\", \"field5\" : \"alpaca\"}";

    for(int i = 0; i < NUM_WARM_UP_RUNS; i++) {
      runExperiment(goodOrderedRegex, "WELL ORDERED", matching, i==NUM_WARM_UP_RUNS-1);
      runExperiment(badOrderedRegex, "BADLY ORDERED", matching, i==NUM_WARM_UP_RUNS-1);
    }
    write("**************END EXPERIMENT *****************\n\n");
  }
  public static void runExperiment(String regex, String regexDescripton, String input, boolean printOutput) throws Exception {
    runExperiment(regex, regexDescripton, input, printOutput, false);
  }

  public static void runExperiment(String regex, String regexDescripton, String input, boolean printOutput, boolean find) throws Exception {
    Pattern p = Pattern.compile(regex);

    boolean matches = false;
    Long start = System.currentTimeMillis();
    for (int i = 0; i < NUM_RUNS; i++) {
      Matcher m = p.matcher(input);
      if(find) {
        matches |= m.find();
      } else {
        matches |= m.matches();
      }
    }
    Long timeElapsed = System.currentTimeMillis() - start;

    if(printOutput) {
      String result = String.format(
          "Took %dms to match the %s regex with %s input",
          timeElapsed, regexDescripton, matches ? "matching" : "non matching");
      write(result);
      dataFileWriter.write(String.format("Pattern was: %s, Input was: %s\n\n", regex, input));
    }
  }

  private static void write(String s) throws Exception {
    System.out.println(s);
    dataFileWriter.write(s);
  }
}
