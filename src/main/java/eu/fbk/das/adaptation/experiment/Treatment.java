package eu.fbk.das.adaptation.experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import eu.fbk.das.adaptation.ensemble.Issue;

public class Treatment extends Loggable {

    private int id;

    private int iv1 = 0; // number of issues raised during the execution of the
    // mission
    // private int[] ivIssues = { 0, 0, 0, 0, 0 }; // array containing the
    // number
    private int[] ivIssues = { 0 };
    // of Ii issues

    private String[] ivIssueNames = { "DriverNotifyRouteInterrupted" }; // array
    // containing
    // the
    // names
    // of Ii
    // issues

    private List<Issue> issues = new ArrayList<Issue>();

    public Treatment() {
	super();
    }

    public void populate() {
	Issue currentIssue;
	for (int i = 0; i < this.ivIssues.length; i++) {
	    int currentIssueTotal = this.ivIssues[i];
	    for (int j = 0; j < currentIssueTotal; j++) {
		currentIssue = new Issue();
		currentIssue.setIssueType(this.ivIssueNames[i]);
		this.issues.add(currentIssue);
	    }
	}
	this.shuffleIssues();
    }

    private void shuffleIssues() {
	long seed = System.nanoTime();
	Collections.shuffle(this.issues, new Random(seed));
    }

    public Treatment(int id, int v1Value) {
	this.id = id;
	this.iv1 = v1Value;
	Random random = new Random();
	if (v1Value == 1) {
	    // [attiva con 4 tipi di issue] this.ivIssues[random.nextInt(4)] =
	    // 1;

	    this.ivIssues[random.nextInt(1)] = 1;
	    //// System.out.println(this.toString());
	    return;
	}
	List<Integer> randomValues = Utilities.generateRandomValues((int) ((100) * ((double) v1Value / 100)), 5);
	int currentIndexRandomValues = 0;
	for (int i = 0; i < this.ivIssues.length; i++) {
	    this.ivIssues[i] = randomValues.get(currentIndexRandomValues);
	    currentIndexRandomValues++;
	}
	//// System.out.println(this.toString());
    }

    public Treatment(int id, int v1Value, int issueIndex, int othersValue) {
	this.id = id;
	this.iv1 = v1Value;
	this.ivIssues[issueIndex] = (int) (((double) v1Value / 100) * othersValue);
	if (v1Value == 1) {
	    this.ivIssues[issueIndex] = 1;
	    //// System.out.println(this.toString());
	    return;
	}
	if (othersValue != 100) {
	    List<Integer> randomValues = Utilities
		    .generateRandomValues((int) ((100 - othersValue) * ((double) v1Value / 100)), 4);
	    int currentIndexRandomValues = 0;
	    for (int i = 0; i < this.ivIssues.length; i++) {
		if (i != issueIndex) {
		    this.ivIssues[i] = randomValues.get(currentIndexRandomValues);
		    currentIndexRandomValues++;
		}
	    }
	}
	//// System.out.println(this.toString());
    }

    public List<Issue> getIssues() {
	return issues;
    }

    @Override
    public String toString() {
	return "Treatment [id=" + this.id + ", iv1=" + iv1 + ", ivIssues=" + Arrays.toString(ivIssues) + "]";
    }

    public String getCsvFileHeader(String commaDelimiter) {
	String result = "id" + commaDelimiter + "iv1" + commaDelimiter + "iv2" + commaDelimiter + "iv3" + commaDelimiter
		+ "iv4" + commaDelimiter + "iv5" + commaDelimiter + "iv6";
	return result;
    }

    public String toCsv(String commaDelimiter) {
	String result = "";
	result += this.id + commaDelimiter;
	result += this.iv1 + commaDelimiter;
	result += Arrays.toString(ivIssues).replace(" ", "").replace("[", "").replace("]", "").replace(",",
		commaDelimiter);
	return result;
    }

    public int getIv1() {
	return iv1;
    }

    public void setIv1(int iv1) {
	this.iv1 = iv1;
    }

    public int[] getIvIssues() {
	return this.ivIssues;
    }

    public void setIvIssues(int[] ivIssues) {
	this.ivIssues = ivIssues;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public Treatment clone() {
	Treatment result = new Treatment();
	result.id = this.id;
	result.iv1 = this.iv1;
	result.ivIssues = this.ivIssues;
	result.ivIssueNames = this.ivIssueNames;
	result.issues = this.issues;
	return result;
    }

}
