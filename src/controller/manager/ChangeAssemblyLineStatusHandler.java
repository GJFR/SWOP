package controller.manager;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineStatus;

public class ChangeAssemblyLineStatusHandler {

	public void run(UIInterface ui, Company company) {
		ArrayList<AssemblyLineStatus> statuses = new ArrayList<AssemblyLineStatus>();
		for (AssemblyLine line : company.getAssemblyLines())
			statuses.add(line.getCurrentStatus());
		while(true) {
			int answer1 = ui.askWithPossibilitiesWithCancel("Which assembly line do you want to change the status of?", statuses.toArray());
			if (answer1 == -1)
				return;
			AssemblyLine assemblyLine = company.getAssemblyLines().get(answer1);
			int answer2 = ui.askWithPossibilitiesWithCancel("Which state do you want to choose?", assemblyLine.getPossibleStatuses().toArray());
			if (answer2 != -1) {
				assemblyLine.setCurrentStatus(assemblyLine.getPossibleStatuses().get(answer2));
				break;
			}
		}
	}
}