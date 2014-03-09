package Main;
import java.util.ArrayList;


public class CarMechanic extends User {
	
	public CarMechanic(int id) {
		super(id);
		initializeApprovedMethods();
	}
	
	private void initializeApprovedMethods() {
		this.approvedMethods.add("selectWorkstation");
		this.approvedMethods.add("getAllPendingTasks");
		this.approvedMethods.add("getAllCompletedTasks");
		this.approvedMethods.add("selectTask");
		this.approvedMethods.add("getActiveTaskInformation");
		this.approvedMethods.add("completeTask");
	}
	
	@Override
	public boolean isCarMechanic() {
		return true;
	}
}
