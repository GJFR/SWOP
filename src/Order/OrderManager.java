package Order;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import Assembly.ProductionSchedule;
import Car.CarModel;
import Car.CarOrder;
import Car.Option;
import User.User;
import User.UserAccessException;



public class OrderManager {

	private ProductionSchedule productionSchedule;
	private final HashMap<Integer,ArrayList<CarOrder>> carOrdersPerId;
	private int highestCarOrderID;

	/**
	 * Constructor for the OrderManager class.
	 * This constructor is also responsible for creating objects for all the placed carOrders.
	 * This constructor is also responsible for creating a ProductionSchedule and feeding it the unfinished carOrders.
	 * 
	 * @param	dataFilePath
	 * @param 	catalog
	 * 			The CarModelCatalog necessary for finding the Options and CarModel Objects of all CarOrders
	 * @param	currentTime TODO
	 */
	public OrderManager(String dataFilePath, CarModelCatalog catalog, GregorianCalendar currentTime) {
		ArrayList<CarOrder> allCarOrders = this.createOrderList(dataFilePath,catalog);
		this.carOrdersPerId = new HashMap<Integer,ArrayList<CarOrder>>();
		for(CarOrder order : allCarOrders) {
			this.addCarOrder(order);
		}

		ArrayList<CarOrder> allUnfinishedCarOrders = this.createOrderList(dataFilePath,catalog);
		for(CarOrder order : allCarOrders) {
			if(!order.IsCompleted()){
				allUnfinishedCarOrders.add(order);
			}
		}
		this.createProductionSchedule(allCarOrders, currentTime);
	}
	
	/**
	 * TODO
	 * @param catalog
	 * @param currentTime
	 */
	public OrderManager(CarModelCatalog catalog, GregorianCalendar currentTime){
		this("carOrderData.txt", catalog, currentTime);
	}
	
	/**
	 * Give a list of all the CarOrders placed by a given user.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @return	A copy of the list of all CarOrders made by the given user. Empty if there are none.
	 * @throws	UserAccessException 
	 * 			If the user is not authorized to call the given method.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<CarOrder> getOrders(User user) throws UserAccessException{
		this.checkUser(user, "getOrders");

		ArrayList<CarOrder> ordersOfUser = this.getCarOrdersPerId().get(user.getId());
		if(ordersOfUser == null)
			return new ArrayList<CarOrder>();
		else
			return (ArrayList<CarOrder>) ordersOfUser.clone();
	}
	
	/**
	 * Create a carOrder based on the user,model and options and put it in a ProductionSchedule.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * 			The User that wants to place a CarOrder.
	 * @param 	model
	 * 			The chosen CarModel of the ordered Car.
	 * @param 	options
	 * 			The chosen Options of the ordered Car.
	 * @return	A gregorian calender which contains the estimate moment of completion.
	 * @throws UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	public CarOrder placeOrder(OrderForm order) throws UserAccessException{
		User user = order.getUser();
		this.checkUser(user, "placeOrder");

		int carOrderId = this.getUniqueCarOrderId();
		CarOrder newOrder = new CarOrder(carOrderId, user,order.getModel(),order.getOptions());
		this.addCarOrder(newOrder);
		this.getProductionSchedule().addOrder(newOrder);
		return newOrder;

	}


	/**
	 * Calculates an estimated completion date for a specific CarOrder and returns it.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * @param 	order
	 * 			The CarOrder whose estimated completion date is requested.
	 * @return	A GregorianCalendar representing the estimated completion date of order.
	 * 			Or the actual delivery date if it was already completed.
	 * @throws 	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	//TODO controleer ofdat de order al klaar is?
	public GregorianCalendar completionEstimate(User user, CarOrder order) throws UserAccessException{
		this.checkUser(user, "completionEstimate");
		try{
		return order.getDeliveredTime();
		} catch(IllegalStateException e){
			return this.getProductionSchedule().completionEstimateCarOrder(order);
		}
		
			
	}

	/**
	 * Creates a ProductionSchedule which is initialised with the given CarOrders.
	 * 
	 * @param orderList
	 * 			The list of orders that has to be scheduled on the create ProductionSchedule.
	 * @param currentTime TODO
	 */
	private void createProductionSchedule(ArrayList<CarOrder> orderList, GregorianCalendar currentTime){
		ProductionSchedule newProductionSchedule = new ProductionSchedule(orderList, currentTime);
		this.setProductionSchedule(newProductionSchedule);
	}
	
	/**
	 * Creates all the placed CarOrders.
	 * @param 	catalog
	 * 			The CarModelCatalog used to convert Strings to Option and CarModel objects.
	 * @return	A list of all the placed CarOrders.
	 */
	private ArrayList<CarOrder> createOrderList(String dataFile, CarModelCatalog catalog){
		ArrayList<CarOrder> allCarOrders = new ArrayList<CarOrder>();
		ArrayList<String> allCarOrderInfo = new ArrayList<String>();
		try {
			FileInputStream fStream = new FileInputStream(dataFile);
			DataInputStream dinStream = new DataInputStream(fStream);
			InputStreamReader insReader = new InputStreamReader(dinStream);
			BufferedReader bReader = new BufferedReader(insReader);
			bReader.readLine();
			String otherLine = bReader.readLine();
			while(!otherLine.startsWith("End")){
				allCarOrderInfo.add(otherLine);
				otherLine = bReader.readLine();
			}
			bReader.close();
			//TODO is multiple exceptions in 1 catcher possible?
			// Yes, but in this case IOException will also catch filenotfoundexception as it is a subclass
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		int highestID = 0;
		for(String orderStr: allCarOrderInfo){
			String[] orderPieces = orderStr.split(",,,,");
			// String omvormen naar objecten
		// 0 : carOrderId
			int carOrderId = Integer.parseInt(orderPieces[0]);
			if(carOrderId > highestID)
				highestID = carOrderId;
		// 1 : garageHolderId
			int garageHolderId = Integer.parseInt(orderPieces[1]);
		// 2 : isDelivered -> Boolean
			boolean isDelivered = false;
			if(orderPieces[2] == "1"){
				isDelivered = true;
			}
			
		// 3 : orderedTime -> GregorianCalendar
			GregorianCalendar orderedCalendar = this.createCalendarFor(orderPieces[3]);
		// 4 : deliveryTime -> GregorianCalendar
			GregorianCalendar deliveredCalendar = null;
			if(isDelivered){
				deliveredCalendar = this.createCalendarFor(orderPieces[4]);
			}
		// 5 : modelId -> CarModel (we hebben hiervoor de Catalog nodig, hoe komen we daar aan?)
			CarModel model = catalog.getCarModel(orderPieces[5]);
		// 6 : options -> ArrayList<Option> (ook Catalog nodig)
			ArrayList<Option> optionsList = new ArrayList<Option>();
			String[] optionStr = orderPieces[6].split(";-;");
			for(String optionDescr: optionStr){
				optionsList.add(catalog.getOption(optionDescr));
			}
			allCarOrders.add(new CarOrder(carOrderId, garageHolderId, orderedCalendar, deliveredCalendar, model, optionsList));
		}

		this.highestCarOrderID = highestID;
		
		return allCarOrders;
	}

	/**
	 * Create a GregorianCalendar based on the given time and date.
	 * 
	 * @param	info
	 * 			The String that has to be converted to a GregorianCalendar object; format=DD-MM-YYYY*HH:MM:SS
	 * @return	A GregorianCalendar
	 */
	private GregorianCalendar createCalendarFor(String info) {
		String[] dateTime = info.split("==");
		String[] dateStr = dateTime[0].split("-");
		String[] timeStr = dateTime[0].split("-");
		int[] dateInt = new int[3];
		int[] timeInt = new int[3];
		for(int i = 0; i < 3;i++){
			dateInt[i] = Integer.parseInt(dateStr[i]);
			timeInt[i] = Integer.parseInt(timeStr[i]);
		}
		return new GregorianCalendar(dateInt[0],dateInt[1],dateInt[2],timeInt[0],timeInt[1],timeInt[2]);
	}

	
	/**
	 * Add a new CarOrder to the OrderManager 
	 * @param 	newOrder
	 * 			The CarOrder which will be added.
	 */
	private void addCarOrder(CarOrder newOrder) {
		if(!this.getCarOrdersPerId().containsKey(newOrder.getUserId()))
		{
			this.getCarOrdersPerId().put(newOrder.getUserId(), new ArrayList<CarOrder>());
		}
		this.getCarOrdersPerId().get(newOrder.getUserId()).add(newOrder);
	}

	private int getUniqueCarOrderId() {
		this.highestCarOrderID += 1;
		return this.highestCarOrderID;
	}
	
	public ProductionSchedule getProductionSchedule() {
		return productionSchedule;
	}

	private void setProductionSchedule(ProductionSchedule productionSchedule) {
		this.productionSchedule = productionSchedule;
	}

	private HashMap<Integer, ArrayList<CarOrder>> getCarOrdersPerId() {
		return carOrdersPerId;
	}

	public ArrayList<String> getPendingOrders(User user) throws UserAccessException {
		this.checkUser(user, "getPendingOrders");
		return GetOrdersWithStatus(user,false);
	}

	public ArrayList<String> getCompletedOrders(User user) throws UserAccessException {
		this.checkUser(user, "getCompletedOrders");
		return GetOrdersWithStatus(user,true);
	}

	private ArrayList<String> GetOrdersWithStatus(User user, boolean b) throws UserAccessException {
		ArrayList<String> result = new ArrayList<String>();
		for(CarOrder i : this.getOrders(user)){
			if(i.IsCompleted().equals("b")) result.add(i.toString());
		}
		return result;
	}
	
	/**
	 * Checks if the give user can perform the given method (defined by a string). 
	 * 
	 * @param	user
	 * 			The user that wants to call the given method.
	 * @param	methodString
	 * 			The string that defines the method.
	 * @throws	UserAccessException
	 *			If the user is not authorized to call the given method.
	 */
	private void checkUser(User user, String methodString) throws UserAccessException {
		if (!user.canPerform(methodString))
			throw new UserAccessException(user, methodString);
	}
}