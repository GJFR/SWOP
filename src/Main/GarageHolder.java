package Main;

public class GarageHolder extends User {

	public GarageHolder(int id) {
		super(id);
		initializeApprovedMethods();
	}

	private void initializeApprovedMethods() {
		this.approvedMethods.add("getOrders");
		this.approvedMethods.add("getAllModels");
		this.approvedMethods.add("placeOrder");
		this.approvedMethods.add("getCompletionEstimate");
		this.approvedMethods.add("getOrderManager");
		this.approvedMethods.add("getPendingOrders");
		this.approvedMethods.add("getCompletedOrders");
		this.approvedMethods.add("getCatalog");
		this.approvedMethods.add("completionEstimate");
	}
}
