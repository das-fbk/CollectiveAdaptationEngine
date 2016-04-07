package eu.fbk.das.adaptation.experiment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Random;

import javax.naming.ConfigurationException;

import de.dfki.ek.EKConfiguration;
import de.dfki.ek.EvoKnowledge;
import eu.fbk.das.adaptation.DemoManagementSystem;
import eu.fbk.das.adaptation.EnsembleManager;
import eu.fbk.das.adaptation.ensemble.Ensemble;
import eu.fbk.das.adaptation.presentation.CAWindow;

/**
 * this is the main file of the experiments
 * 
 * @author Antonio Bucchiarone
 * 
 */
public class ExperimentMain {
    private final static String PROP_PATH = "adaptation.properties";

    private final static String PreferencesDir = "scenario/ALLOWEnsembles/Preferences/";

    public static void main(String[] args) throws ConfigurationException, FileNotFoundException {

	System.gc();
	// reading property file
	String propPath = PROP_PATH;
	if (args.length > 0) {
	    propPath = args[0];
	}
	Properties props = new Properties();
	try {
	    props.load(new FileInputStream(propPath));
	} catch (IOException e) {
	    System.err.println("Cannot read configuration file: " + e.getMessage());
	}

	// ///// Evoknowledge SETUP/////////
	// String modelPath = "jdbc:postgresql://localhost:5432/";
	String modelPath = "jdbc:mysql://localhost:3306/";
	String modelName = "allowek";
	String userName = "root";
	String password = "";

	// Create EKConfiguration or read from file
	EKConfiguration config = new EKConfiguration(modelPath, modelName, userName, password);

	// Initialize EvoKnowledge
	EvoKnowledge.initialize(config, "ek", true);

	// Create a new instance of EvoKnowledge
	EvoKnowledge ek = EvoKnowledge.createEvoKnowledge();
	// ////////////////////////////

	// demo management system construction

	DemoManagementSystem dms = DemoManagementSystem.initializeSystem("scenario/ALLOWEnsembles/Review/");

	List<Treatment> treatments = ExperimentMain.createTreatments();

	// Ensemble Creation - Instance of Ensemble Route A
	Ensemble routeA = dms.getEnsemble("RouteA", null);
	EnsembleManager routeAManager = new EnsembleManager(routeA);

	// Ensemble Creation - Instance of Ensemble Route B
	Ensemble routeB = dms.getEnsemble("RouteB", null);
	EnsembleManager routeBManager = new EnsembleManager(routeB);

	// Ensemble Creation - Instance of Ensemble FlexiBus Management
	Ensemble flexiBusMngmt = dms.getEnsemble("FlexiBusMngmt", null);
	EnsembleManager flexiBusMngmtBManager = new EnsembleManager(flexiBusMngmt);

	// set the Evoknowledge of the Ensemble
	routeAManager.setEk(ek);
	/*
	 * // add the preference to each roleManager of the Ensemble from file
	 * for (int i = 0; i < routeAManager.getRolesManagers().size(); i++) {
	 * RoleManager currentRM = routeAManager.getRolesManagers().get(i);
	 * String fileName = currentRM.getRole().getType() + ".xml"; Preferences
	 * pref = ParserUtil.parse(PreferencesDir, fileName);
	 * routeAManager.addRolePreferences(currentRM, pref); }
	 */
	// Ensemble Creation - Instance of Ensemble 2
	// Ensemble e2 = dms.getEnsemble("E2");
	// EnsembleManager e2Manager = new EnsembleManager(e2);

	// Ensemble Creation - Instance of Ensemble 3
	// Ensemble e3 = dms.getEnsemble("E3");
	// EnsembleManager e3Manager = new EnsembleManager(e3);

	// Ensemble Creation - Instance of Ensemble 3
	// Ensemble e4 = dms.getEnsemble("E4");
	// EnsembleManager e4Manager = new EnsembleManager(e4);

	List<EnsembleManager> ensembles = new ArrayList<EnsembleManager>();
	ensembles.add(routeAManager);
	ensembles.add(routeBManager);
	ensembles.add(flexiBusMngmtBManager);

	Utilities.buildSolversMap(ensembles);
	// System.out.println(Utilities.getSolversMap());

	System.gc();
	// try {
	// System.out.println("Experiment starting in 5 seconds...");
	// Thread.sleep(5000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	ExperimentMain.runTreatments(treatments, ensembles);

	// remove the first element of the treatments list
	treatments.remove(0);
	// Utilities.genericWriteFile(treatments, "treatments.csv");
	System.out.println("END SIMULATION");
	// System.exit(1);

    }

    /*
     * static void launchWindow(EnsembleManager ensembleManager) { CAWindow
     * window = new CAWindow();
     * window.loadActiveIssueResolutionsTable(ensembleManager.
     * getActiveIssueResolutions(), ensembleManager); window.loadTreeFrame();
     * ensembleManager.checkRoles(window);
     * 
     * //System.out.println("END SIMULATION"); }
     */
    static List<Treatment> createTreatments() {

	List<Treatment> result = new ArrayList<Treatment>();

	// this variable defines the number of Issue Triggered in sequence
	int treatmentsForSubject = 1;
	// int treatmentsForSubject = 1000;

	// int[] v1Values = { 1, 250, 500, 750, 1000 };
	int[] v1Values = { 1 };
	// int[] othersValues = { 0, 20, 40, 60, 80, 100 };
	int[] othersValues = { 0 };
	boolean fullyRandom = true;

	int currentTreatmentId = 1;

	if (fullyRandom) {
	    for (int i = 0; i < v1Values.length; i++) {
		for (int t = 1; t <= treatmentsForSubject; t++) {
		    result.add(createRandomTreatment(currentTreatmentId++, v1Values[i]));
		}
	    }
	} else {
	    // othersValues.length
	    for (int i = 0; i < v1Values.length; i++) {
		for (int issueIndex = 0; issueIndex <= 4; issueIndex++) {
		    for (int j = 0; j < othersValues.length; j++) {
			for (int t = 1; t <= treatmentsForSubject; t++) {
			    result.add(createTreatment(currentTreatmentId++, v1Values[i], issueIndex, othersValues[j]));
			}
		    }
		}
	    }
	}
	Collections.shuffle(result, new Random(System.nanoTime()));
	// add a copy of the first element, it will be always at the beginning
	// and at the end of the list of treatments
	// Treatment treatmentToAdd = ((Treatment) result.get(0)).clone();
	// result.add(treatmentToAdd);
	return result;
    }

    static Treatment createTreatment(int id, int v1Value, int issueIndex, int othersValue) {
	Treatment result = new Treatment(id, v1Value, issueIndex, othersValue);
	result.populate();
	return result;
    }

    static Treatment createRandomTreatment(int id, int v1Value) {
	Treatment result = new Treatment(id, v1Value);
	result.populate();
	return result;
    }

    static void runTreatments(List<Treatment> treatments, List<EnsembleManager> ensembles) {
	ListIterator<Treatment> iterator = treatments.listIterator();
	System.out.println("Numero di Treatments: " + treatments.size());
	List<ExperimentResult> results = new ArrayList<ExperimentResult>();
	// CAWindow window = null;
	CAWindow window = new CAWindow(ensembles, null, 0, null);
	// CAWindow window = new CAWindow(ensembles);
	while (iterator.hasNext()) {

	    results.add(ExperimentMain.runTreatment(iterator.next(), ensembles, window));
	}
	// remove the first result
	results.remove(0);
	// Utilities.genericWriteFile(results, "results.csv");

    }

    static ExperimentResult runTreatment(Treatment treatment, List<EnsembleManager> ensembles, CAWindow window) {
	System.out.println("" + treatment.getId() + " - " + treatment.toString());
	return ExperimentRunner.getInstance().run(treatment, ensembles, window);

    }
}
