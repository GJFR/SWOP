package domain.assembly;

import java.util.ArrayList;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.order.Order;



public abstract class Status implements AssemblyLineStatus {
	protected void standardAdvanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException{
		// Check of alle tasks klaar zijn, zoniet laat aan de user weten welke nog niet klaar zijn (zie exception message).
		if (!this.canAdvanceLine(assemblyLine))
			throw new CannotAdvanceException(assemblyLine.getBlockingWorkstations());


		// Zoek de tijd die nodig was om alle tasks uit te voeren.
		int timeSpendForTasks = 0;
		for(Workstation workstation : assemblyLine.getAllWorkstations()){
			if(workstation.getTimeSpend() > timeSpendForTasks)
				timeSpendForTasks = workstation.getTimeSpend();
		}
		assemblyLine.getAssemblyLineScheduler().addCurrentTime(timeSpendForTasks);
		// Vraag nieuwe order op.
		Order newOrder = notifyOrderAsked(assemblyLine);
		try{		

			// move huidige vehicles 1 plek
			//neem vehicle van WorkStation 3
			Workstation workstationLast = assemblyLine.selectWorkstationById(assemblyLine.getNumberOfWorkstations());
			ArrayList<Order> finishedOrders = new ArrayList<Order>();
			if(workstationLast.getVehicleAssemblyProcess() != null){
				// zoek welke order klaar is, wacht met het zetten van de deliveryTime omdat de tijd van het schedule nog moet worden geupdate.
				finishedOrders.add(workstationLast.getVehicleAssemblyProcess().getOrder());
			}


			for(int i = assemblyLine.getAllWorkstations().size(); i>1; i--){
				Workstation workstationNext = assemblyLine.selectWorkstationById(i);
				workstationNext.clear();
				Workstation workstationPrev = assemblyLine.selectWorkstationById(i-1);
				workstationNext.setVehicleAssemblyProcess(workstationPrev.getVehicleAssemblyProcess());
			}

			VehicleAssemblyProcess newAssemblyProcess = null;
			if(newOrder != null){
				newAssemblyProcess = newOrder.getAssemblyprocess();
			}

			Workstation workstation1 = assemblyLine.selectWorkstationById(1);
			workstation1.clear();
			workstation1.setVehicleAssemblyProcess(newAssemblyProcess);

			// Er wordt gecheckt of er workstations geskipt kunnen worden.
			for (int i = assemblyLine.getAllWorkstations().size(); i > 0; i--) {
				for (int id = i; id <= assemblyLine.getAllWorkstations().size(); id++) {
					// als de eerste plaats van de workstation leeg is plaats nieuw order
					if(id ==1 && assemblyLine.getAllOrders().get(0) ==null){
						Order nextOrder = this.notifyOrderAsked(assemblyLine);
						if(nextOrder != null){
							VehicleAssemblyProcess nextAssemblyProcess = newOrder.getAssemblyprocess();
							workstation1.setVehicleAssemblyProcess(nextAssemblyProcess);
							i++;
						}
					}
					// Er wordt gecheckt welke workstation geen taken uit te voeren heeft.
					if (assemblyLine.selectWorkstationById(id).getAllPendingTasks().isEmpty()) {					

						// Als dit niet de laatste workstation is en de volgende is vrij, dan wordt het proces verschoven naar de volgende.
						if (id < assemblyLine.getAllWorkstations().size() && assemblyLine.selectWorkstationById(id + 1).getVehicleAssemblyProcess() == null) {
							assemblyLine.selectWorkstationById(id + 1).setVehicleAssemblyProcess(assemblyLine.selectWorkstationById(id).getVehicleAssemblyProcess());
							assemblyLine.selectWorkstationById(id).clear();
						}
						// Als dit de laatste workstation is, wordt het assembly process van de band gehaald.
						else if (assemblyLine.selectWorkstationById(id).getVehicleAssemblyProcess() == null) {
							finishedOrders.add(assemblyLine.selectWorkstationById(id).getVehicleAssemblyProcess().getOrder());
						}
					}
				}
			}

			for (Order finishedOrder : finishedOrders){
				finishedOrder.getAssemblyprocess().setDeliveredTime(assemblyLine.getAssemblyLineScheduler().getCurrentTime());
				finishedOrder.getAssemblyprocess().registerDelay(assemblyLine);
			}
		}
		catch(DoesNotExistException e){
			throw new InternalFailureException("Suddenly a Workstation disappeared while that should not be possible.");
		}
	}

	protected abstract Order notifyOrderAsked(AssemblyLine assemblyLine);
	@Override
	public abstract void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException;

	public int calculateTimeTillEmptyFor(AssemblyLine assemblyLine, LinkedList<Order> assembly) {
		@SuppressWarnings("unchecked")
		LinkedList<Order> simulAssembly = (LinkedList<Order>) assembly.clone();
		int time = 0;
		for(int i = 0; i < assemblyLine.getNumberOfWorkstations(); i++){
			time += assemblyLine.calculateTimeTillAdvanceFor(simulAssembly);
			simulAssembly.removeLast();
			simulAssembly.addFirst(null);
			for(int j= assemblyLine.getNumberOfWorkstations()-1; j>=0;j--){
				try {
					if(simulAssembly.get(j) != null && !assemblyLine.filterWorkstations(simulAssembly.get(j).getAssemblyprocess()).contains(assemblyLine.selectWorkstationById(j))){
						if(j==assemblyLine.getNumberOfWorkstations()-1){
							simulAssembly.removeLast();
							simulAssembly.addLast(null);
						}
						else{
							if(simulAssembly.get(j+1)==null){
								simulAssembly.remove(j+1);
								simulAssembly.add(j+1, simulAssembly.get(j));
								simulAssembly.remove(j);
								simulAssembly.add(j, null);
								j+=2;
							}
						}
					}
				} catch (DoesNotExistException e) {
					// onmogelijk
				}
			}
		}
		return time;
	}
}