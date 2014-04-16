package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import domain.Statistics;
import domain.assembly.AssemblyStatusView;
import domain.order.CarOrder;
import domain.order.Order;

public interface UIInterface {

	void display(String string);

	int askForInteger(String string, int i);
	
	int askForInteger(String string, int i, int j);

	void showAssemblyLineStatus(AssemblyStatusView currentStatus);

	boolean askYesNoQuestion(String string);
	
	void displayPendingCarOrderInfo(Order pendingOrder, Calendar completionEstimate);
	
	void displayCompletedCarOrderInfo(Order completedOrder);

	void displayPendingCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList);

	void displayCompletedCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList);
	
	int askForCarOrder(ArrayList<Order> pendingOrders, ArrayList<Order> completedOrders, ArrayList<Calendar> completionEstimates);

	void fillIn(OrderForm order);

	int askWithPossibilities(String string, Object[] possibilities);

	void display(Object[] displayableObjects);

	String askWithPossibilities(String string, List<String> possibilities);
	
	void showStatistics(Statistics view);
	
	void printException(Exception e);

	GregorianCalendar fillInSingleTaskOrder(OrderForm orderForm);


}
