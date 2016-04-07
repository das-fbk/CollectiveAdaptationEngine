package eu.fbk.das.adaptation.experiment;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import eu.fbk.das.adaptation.EnsembleManager;
import eu.fbk.das.adaptation.RoleManager;
import eu.fbk.das.adaptation.ensemble.Issue;
import eu.fbk.das.adaptation.model.IssueCommunication;
import eu.fbk.das.adaptation.model.IssueResolution;
import eu.fbk.das.adaptation.presentation.CAWindow;

public class ExperimentRunner {

    private static ExperimentRunner instance;
    private static long memoryBaseline;
    // private static double cpuLoadBaseline;
    private static ThreadMXBean threadBean;
    // private static Runtime runtime;
    private static MemoryMXBean memoryBean;

    public static ExperimentRunner getInstance() {
	if (ExperimentRunner.instance == null) {
	    // System.gc();
	    ExperimentRunner.instance = new ExperimentRunner();
	    ExperimentRunner.threadBean = ManagementFactory.getThreadMXBean();// getPlatformMXBean(OperatingSystemMXBean.class);
	    ExperimentRunner.memoryBean = ManagementFactory.getMemoryMXBean();
	    // ExperimentRunner.runtime = Runtime.getRuntime();

	    System.gc();
	    // ExperimentRunner.memoryBaseline =
	    // ExperimentRunner.runtime.totalMemory() -
	    // ExperimentRunner.runtime.freeMemory();
	    // ExperimentRunner.processCpuLoadBaseline =
	    // ExperimentRunner.osMean.getProcessCpuLoad();

	    ExperimentRunner.memoryBaseline = ExperimentRunner.memoryBean.getHeapMemoryUsage().getUsed();
	    // System.gc();

	}
	return ExperimentRunner.instance;

    }

    public ExperimentResult run(Treatment treatment, List<EnsembleManager> ensembles, CAWindow window) {

	System.gc();
	try {
	    Thread.sleep(200);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	// System.gc();

	ExperimentResult result = new ExperimentResult();

	// set id
	result.setId(treatment.getId());

	// start logging the CPU usage for filling dv9
	// double startCPU = ExperimentRunner.osMean.getProcessCpuLoad();
	long startCPU = ExperimentRunner.threadBean.getCurrentThreadCpuTime();

	// start logging the time for setting dv1 of the result
	long startTime = System.nanoTime();

	for (int t = 0; t < treatment.getIssues().size(); t++) {

	    // generazione di Ivano
	    // Issue issue = treatment.getIssues().get(t);
	    // RoleManager r = Utilities.pickRoleForIssue(issue);

	    // restart the cross ensemble counter
	    // r.setCrossEnsembleIssues(0);
	    // r.setMinDepth(0);
	    // r.setMaxDepth(0);
	    // r.setMinExtent(0);
	    // r.setMaxExtent(0);

	    // [controlla]
	    // System.out.println("ROLE: " + r.getRole().getType());

	    // generazione Antonio
	    Issue issue = new Issue();
	    issue.setIssueType("IntenseTraffic");
	    RoleManager r = ensembles.get(0).getRolebyType("DriverA");
	    System.out.println("ISSUE TRIGGERED: " + issue.getIssueType());

	    IssueResolution resolution1 = new IssueResolution(t + 1, "ISSUE_TRIGGERED", r, r, issue, null);
	    resolution1.setRoot(true);
	    r.addIssueResolution(resolution1);

	    // add the issueresolution to the right Ensemble
	    for (int i = 0; i < ensembles.size(); i++) {
		for (int j = 0; j < ensembles.get(i).getRolesManagers().size(); j++) {
		    RoleManager currentManager = ensembles.get(i).getRolesManagers().get(j);
		    if (currentManager.getRole().getType().equalsIgnoreCase(r.getRole().getType())) {
			ArrayList<IssueResolution> resolutions = new ArrayList<IssueResolution>();

			EnsembleManager em = ensembles.get(i);
			if (em.getIssueCommunications() != null) {
			    em.getIssueCommunications().clear();
			}

			// update id of the issue resolution
			em.setIssueResolutionCount(1);
			resolution1.setIssueResolutionID(em.getIssueResolutionCount());

			resolutions.add(resolution1);
			em.setActiveIssueResolutions(resolutions);

			List<IssueCommunication> relatedComs = new ArrayList<IssueCommunication>();

			em.setCommunicationsRelations(resolution1, relatedComs);
			System.out.println(em.getIssueCommunications().size());

			String capID = Integer.toString(t + 1);

			System.out.println(em.getIssueCommunications().size());

			em.checkIssues(null, capID, window, ensembles, null, 0, null);

			break;
		    }
		}
	    }
	}

	// set dv1
	long endTime = System.nanoTime();
	long totalTime = endTime - startTime;

	// set dv2
	// long usedMemory = ExperimentRunner.runtime.totalMemory() -
	// ExperimentRunner.runtime.freeMemory() -
	// ExperimentRunner.memoryBaseline;
	long usedMemory = ExperimentRunner.memoryBean.getHeapMemoryUsage().getUsed() - ExperimentRunner.memoryBaseline;

	// set dv3
	double endCPU = ExperimentRunner.threadBean.getCurrentThreadCpuTime(); // in
	// nanoseconds
	double usedCPU = (endCPU - startCPU) / 1000000; // those are in
	// milliseconds

	System.gc();

	result.setDv1(totalTime);
	result.setDv2(usedMemory);
	result.setDv3(usedCPU);

	// set dv4: min depth of resolution trees
	int dv4 = 0;
	for (int i = 0; i < ensembles.size(); i++) {
	    EnsembleManager currentEnsemble = ensembles.get(i);
	    for (int j = 0; j < currentEnsemble.getRolesManagers().size(); j++) {
		RoleManager roleMng = currentEnsemble.getRolesManagers().get(j);
		dv4 = dv4 + roleMng.getMinDepth();
	    }
	}
	result.setDv4(dv4);
	// System.out.println("DV4: " + dv4);

	// System.out.println("DV4: " + dv4);

	// // set dv5: max depth of resolution trees
	int dv5 = 0;
	for (int i = 0; i < ensembles.size(); i++) {
	    EnsembleManager currentEnsemble = ensembles.get(i);
	    for (int j = 0; j < currentEnsemble.getRolesManagers().size(); j++) {
		RoleManager roleMng = currentEnsemble.getRolesManagers().get(j);
		dv5 = dv5 + roleMng.getMaxDepth();
	    }
	}
	result.setDv5(dv5);
	// System.out.println("DV5: " + dv5);

	// System.gc();

	// // set dv7: min Extend of resolution trees
	int dv7 = 0;
	for (int i = 0; i < ensembles.size(); i++) {
	    EnsembleManager currentEnsemble = ensembles.get(i);
	    for (int j = 0; j < currentEnsemble.getRolesManagers().size(); j++) {
		RoleManager roleMng = currentEnsemble.getRolesManagers().get(j);
		dv7 = dv7 + roleMng.getMinExtent();
	    }
	}
	result.setDv7(dv7);
	// System.out.println("DV7: " + dv7);

	// System.gc();

	// // set dv8: max Extend of resolution trees
	int dv8 = 0;
	for (int i = 0; i < ensembles.size(); i++) {
	    EnsembleManager currentEnsemble = ensembles.get(i);
	    for (int j = 0; j < currentEnsemble.getRolesManagers().size(); j++) {
		RoleManager roleMng = currentEnsemble.getRolesManagers().get(j);
		dv8 = dv8 + roleMng.getMaxExtent();
	    }
	}
	result.setDv8(dv8);
	// System.out.println("DV8: " + dv8);

	// System.gc();

	// // set dv10: cross-ensembles issues
	int dv10 = 0;
	for (int i = 0; i < ensembles.size(); i++) {
	    EnsembleManager currentEnsemble = ensembles.get(i);
	    for (int j = 0; j < currentEnsemble.getRolesManagers().size(); j++) {
		RoleManager roleMng = currentEnsemble.getRolesManagers().get(j);
		dv10 = dv10 + roleMng.getCrossEnsembleIssues();
	    }
	}
	result.setDv10(dv10);
	// System.out.println("DV10: " + dv10);

	System.gc();

	return result;
    }

}
