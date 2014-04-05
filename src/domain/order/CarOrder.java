package domain.order;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.configuration.CarModel;
import domain.configuration.Option;
import domain.user.User;

public class CarOrder {
	
	private final int carOrderID;
	private final int userID;
	private final GregorianCalendar orderedTime;
	private GregorianCalendar deliveredTime;

	private final Car car;
	
	/**
	 * Constructor of CarOrder.
	 * 
	 * @param	carOrderId
	 * 			The id of this car order
	 * @param	user TODO
	 * 			The user of that has placed this order
	 * @param	model
	 * 			The model of the car to be ordered
	 * @param	options
	 * 			The options of the car to be ordered
	 */
	public CarOrder(int carOrderId, User user, CarModel model, ArrayList<Option> options) {
		this.carOrderID = carOrderId;
		this.car = new Car(this, model, options);
		this.userID = user.getId();
		this.orderedTime = new GregorianCalendar(); // dit geeft de tijd op het moment van constructie.
	}

	/**
	 * Constructor of CarOrder.
	 * Creates a car using model and options
	 * 
	 * @param carOrderId
	 * 			The id of this car order
	 * @param userId
	 * 			The id of the garageHolder that has placed this order
	 * @param orderedCalendar
	 * 			The time when it was ordered
	 * @param deliveredCalendar
	 * 			The time when it was delivered; the car is already completed if deliveredCalendar is not null
	 * @param model
	 * 			The model of the car that has been ordered
	 * @param optionsList
	 * 			The options of the car that has been ordered
	 */
	public CarOrder(
			int carOrderId, 
			int garageHolderId,
			GregorianCalendar orderedCalendar,
			GregorianCalendar deliveredCalendar, 
			CarModel model,
			ArrayList<Option> optionsList) {
		this.carOrderID = carOrderId;
		this.userID = garageHolderId;
		this.orderedTime = (GregorianCalendar) orderedCalendar.clone();
		boolean isDelivered;
		if(deliveredCalendar != null){
			this.deliveredTime = (GregorianCalendar) deliveredCalendar.clone();
			isDelivered = true;
		}
		else{
			this.deliveredTime = null;
			isDelivered = false;
		}

		this.car = new Car(this, model, optionsList, isDelivered);
	}

	/**
	 * Sets the time this car was delivered.
	 * 
	 * @param	user
	 * 			The user that has ordered the delivery
	 * @param 	deliveredTime
	 * 			The time this car was delivered
	 */
	public void setDeliveredTime(GregorianCalendar deliveredTime) {
			if(!this.isCompleted())
				throw new IllegalStateException("Can't set deliveredTime because this CarOrder is not completed yet.");
			if(this.deliveredTime!=null)
				throw new IllegalStateException("DeliveredTime already set");
			this.deliveredTime = deliveredTime;
	}

	/**
	 * Returns the id of this car order.
	 * 
	 * @return the id of this car order
	 */
	public int getCarOrderID() {
		return carOrderID;
	}

	/**
	 * Returns the time the car was delivered.
	 * 
	 * @return	the time the car was delivered
	 * @throws	IllegalStateException
	 * 			If this car hasn't been delivered yet
	 */
	public GregorianCalendar getDeliveredTime() throws IllegalStateException{
		if (deliveredTime == null)
			throw new IllegalStateException("This car hasn't been delivered yet");
		return (GregorianCalendar) deliveredTime.clone();
	}

	/**
	 * Returns the car that has been ordered.
	 * 
	 * @return	the car that has been ordered
	 */
	public Car getCar() {
		return car;
	}

	/**
	 * Returns the user id of the user that has placed the order.
	 * 
	 * @return	the user id of the user that has placed the order
	 */
	public int getUserId() {
		return this.userID;
	}

	/**
	 * Returns the time the order was placed.
	 * 
	 * @return	the time the order was placed
	 */
	public GregorianCalendar getOrderedTime() {
		return (GregorianCalendar) this.orderedTime.clone();
	}
	
	/**
	 * Returns if this car is already completed or not.
	 * 
	 * @return true if the car is already completed, else false
	 */
	public Boolean isCompleted() {
		return this.getCar().isCompleted();
	}
	
	/**
	 * Returns a string representation of the CarOrder
	 */
	@Override
	public String toString(){
		String ordered = "  Ordered on: " + this.orderedTime.get(GregorianCalendar.DAY_OF_MONTH) 
				+ "-" + this.orderedTime.get(GregorianCalendar.MONTH)
				+ "-" + this.orderedTime.get(GregorianCalendar.YEAR)
				+ " " + this.orderedTime.get(GregorianCalendar.HOUR_OF_DAY)
				+ ":" + this.orderedTime.get(GregorianCalendar.MINUTE)
				+ ":" + this.orderedTime.get(GregorianCalendar.SECOND);
		String delivered ="";
		if(this.deliveredTime != null){
			delivered = "  Delivered on: " + this.deliveredTime.get(GregorianCalendar.DAY_OF_MONTH) 
					+ "-" + this.deliveredTime.get(GregorianCalendar.MONTH)
					+ "-" + this.deliveredTime.get(GregorianCalendar.YEAR)
					+ " " + this.deliveredTime.get(GregorianCalendar.HOUR_OF_DAY)
					+ ":" + this.deliveredTime.get(GregorianCalendar.MINUTE)
					+ ":" + this.deliveredTime.get(GregorianCalendar.SECOND);
		}
		return "CarOrder: " + this.carOrderID + "  User: " + this.userID + ordered + delivered;
		
	}

}