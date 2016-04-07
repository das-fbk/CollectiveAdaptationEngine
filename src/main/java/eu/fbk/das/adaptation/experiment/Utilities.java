package eu.fbk.das.adaptation.experiment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.fbk.das.adaptation.EnsembleManager;
import eu.fbk.das.adaptation.RoleManager;
import eu.fbk.das.adaptation.ensemble.Issue;
import eu.fbk.das.adaptation.ensemble.Solution;
import eu.fbk.das.adaptation.ensemble.Solver;

/**
 * @author Antonio Bucchiarone
 * 
 */
public class Utilities {

    private static HashMap<String, ArrayList<RoleManager>> solversMap;

    public static HashMap<String, ArrayList<RoleManager>> getSolversMap() {
	return solversMap;
    }

    public static void setSolversMap(HashMap<String, ArrayList<RoleManager>> solversMap) {
	Utilities.solversMap = solversMap;
    }

    /**
     *
     * Thanks to assylias - https://assylias.wordpress.com/
     * 
     * http://stackoverflow.com/questions/22380890/generate-n-random-numbers-
     * whose-sum-is-m-and-all-numbers-should-be-greater-than
     *
     */
    public static List<Integer> generateRandomValues(int targetSum, int numberOfDraws) {
	Random r = new Random();
	List<Integer> load = new ArrayList<>();

	// random numbers
	int sum = 0;
	for (int i = 0; i < numberOfDraws; i++) {
	    int next = r.nextInt(targetSum) + 1;
	    load.add(next);
	    sum += next;
	}

	// scale to the desired target sum
	double scale = 1d * targetSum / sum;
	sum = 0;
	for (int i = 0; i < numberOfDraws; i++) {
	    load.set(i, (int) (load.get(i) * scale));
	    sum += load.get(i);
	}

	// take rounding issues into account
	while (sum++ < targetSum) {
	    int i = r.nextInt(numberOfDraws);
	    load.set(i, load.get(i) + 1);
	}

	return load;
    }

    public static void writeFile(List<Treatment> treatments, String fileName) {

	// Delimiter used in CSV file
	String commaDelimiter = ",";
	String newLineSeparator = "\n";

	FileWriter fileWriter = null;

	try {
	    fileWriter = new FileWriter(fileName);

	    // Write the CSV file header
	    fileWriter.append(treatments.get(0).getCsvFileHeader(commaDelimiter));
	    // Add a new line separator after the header
	    fileWriter.append(newLineSeparator);

	    // Write a new treatment object list to the CSV file
	    for (Treatment treatment : treatments) {
		fileWriter.append(treatment.toCsv(commaDelimiter));
		fileWriter.append(newLineSeparator);
	    }
	} catch (Exception e) {
	    // System.out.println("Error in CsvFileWriter.");
	    e.printStackTrace();
	} finally {

	    try {
		fileWriter.flush();
		fileWriter.close();
	    } catch (IOException e) {
		// System.out.println("Error while flushing/closing
		// fileWriter.");
		e.printStackTrace();
	    }

	}
    }

    public static void genericWriteFile(List<? extends Loggable> loggables, String fileName) {

	// Delimiter used in CSV file
	String commaDelimiter = ",";
	String newLineSeparator = "\n";

	FileWriter fileWriter = null;

	try {
	    fileWriter = new FileWriter(fileName);

	    // Write the CSV file header
	    fileWriter.append(loggables.get(0).getCsvFileHeader(commaDelimiter));
	    // Add a new line separator after the header
	    fileWriter.append(newLineSeparator);

	    // Write a new treatment object list to the CSV file
	    for (Loggable currentLoggable : loggables) {
		fileWriter.append(currentLoggable.toCsv(commaDelimiter));
		fileWriter.append(newLineSeparator);
	    }
	} catch (Exception e) {
	    // System.out.println("Error in CsvFileWriter.");
	    e.printStackTrace();
	} finally {

	    try {
		fileWriter.flush();
		fileWriter.close();
	    } catch (IOException e) {
		// System.out.println("Error while flushing/closing
		// fileWriter.");
		e.printStackTrace();
	    }

	}
    }

    public static RoleManager pickRoleForIssue(Issue issue) {
	RoleManager result = null;

	int numElements = Utilities.solversMap.get(issue.getIssueType()).size();
	if (numElements == 1) {
	    result = Utilities.solversMap.get(issue.getIssueType()).get(0);
	}
	if (numElements > 1) {
	    int index = Utilities.generateRandomValues(numElements - 1, 1).get(0);
	    result = Utilities.solversMap.get(issue.getIssueType()).get(index);
	}
	return result;
    }

    public static void buildSolversMap(List<EnsembleManager> ensembles) {
	HashMap<String, String[]> staticMap = new HashMap<String, String[]>();
	staticMap.put("DriverNotifyRouteInterrupted", new String[] { "RM1" });
	// staticMap.put("I2", new String[] { "H1", "D1", "D2", "D5", "H3",
	// "H2", "D3", "D4" });

	Utilities.solversMap = new HashMap<String, ArrayList<RoleManager>>();
	Utilities.solversMap.put("DriverNotifyRouteInterrupted", new ArrayList<RoleManager>());
	/*
	 * Utilities.solversMap.put("I2", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I3", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I4", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I5", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I6", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I7", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I8", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I9", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I10", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I11", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I12", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I13", new ArrayList<RoleManager>());
	 * Utilities.solversMap.put("I14", new ArrayList<RoleManager>());
	 */

	Iterator<EnsembleManager> ensemblesIterator = ensembles.iterator();
	Iterator<RoleManager> rolesIterator;
	Iterator<Solver> solversIterator;
	Iterator<Solution> solutionsIterator;
	Iterator<Issue> issuesIterator;
	EnsembleManager currentEnsemble;
	RoleManager currentRole;
	Solver currentSolver;
	Solution currentSolution;
	String currentIssueName;
	List<RoleManager> roleManagers = new ArrayList<RoleManager>();
	while (ensemblesIterator.hasNext()) {
	    currentEnsemble = (EnsembleManager) ensemblesIterator.next();
	    rolesIterator = currentEnsemble.getRolesManagers().iterator();
	    while (rolesIterator.hasNext()) {
		currentRole = (RoleManager) rolesIterator.next();
		roleManagers.add(currentRole);
		// solversIterator =
		// currentRole.getRole().getSolver().iterator();
		// while (solversIterator.hasNext()) {
		// currentSolver = solversIterator.next();
		// solutionsIterator = currentSolver.getSolution().iterator();
		// while (solutionsIterator.hasNext()) {
		// currentSolution = solutionsIterator.next();
		// issuesIterator = currentSolution.getIssue().iterator();
		// while (issuesIterator.hasNext()) {
		// currentIssueName = issuesIterator.next().getIssueType();
		// if (Utilities.solversMap.get(currentIssueName) != null) {
		// Utilities.solversMap.get(currentIssueName).add(currentRole);
		// }
		// }
		// }
		// }
	    }
	}
	Utilities.assignStaticallyRoles(staticMap, roleManagers);
	// Utilities.printSolversMap();
    }

    private static void assignStaticallyRoles(HashMap<String, String[]> staticMap, List<RoleManager> roleManagers) {
	Iterator iterator = staticMap.entrySet().iterator();
	while (iterator.hasNext()) {
	    Map.Entry pair = (Map.Entry) iterator.next();
	    Utilities.solversMap.put((String) pair.getKey(),
		    Utilities.getManagersByIssueNames((String[]) pair.getValue(), roleManagers));
	}
    }

    private static ArrayList<RoleManager> getManagersByIssueNames(String[] names, List<RoleManager> roleManagers) {
	ArrayList<RoleManager> result = new ArrayList<RoleManager>();
	for (int i = 0; i < roleManagers.size(); i++) {
	    if (Arrays.asList(names).contains(roleManagers.get(i).getRole().getType())) {
		result.add(roleManagers.get(i));
	    }
	}
	return result;
    }

    private static void printSolversMap() {
	ArrayList<RoleManager> currentRolesList;
	Iterator iterator = Utilities.solversMap.entrySet().iterator();
	String currentIssue;
	String currentRolesNames;
	while (iterator.hasNext()) {
	    currentRolesNames = "";
	    Map.Entry pair = (Map.Entry) iterator.next();
	    currentIssue = (String) pair.getKey();
	    currentRolesList = (ArrayList<RoleManager>) pair.getValue();
	    for (int i = 0; i < currentRolesList.size(); i++) {
		currentRolesNames += currentRolesList.get(i).getRole().getType() + ", ";
	    }
	    // System.out.println(currentIssue + " - [" + currentRolesNames +
	    // "]");
	}
    }

}
