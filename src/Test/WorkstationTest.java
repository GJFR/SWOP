package Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import Assembly.AssemblyTask;
import Assembly.CarAssemblyProcess;
import Assembly.Workstation;
import Car.Car;
import Car.CarModel;
import Car.CarOrder;
import Car.Option;
import Car.OptionType;
import Order.CarModelCatalog;
import Order.CarModelCatalogException;
import User.CarMechanic;
import User.GarageHolder;
import User.Manager;
import User.UserAccessException;

public class WorkstationTest {

	private CarMechanic carMechanic;
	private GarageHolder garageHolder;
	private Manager manager;
	private Workstation workstation;
	private AssemblyTask validTask;
	private AssemblyTask invalidTask;
	
	@Before
	public void testCreate() throws UserAccessException {
		carMechanic = new CarMechanic(1);
		garageHolder = new GarageHolder(2);
		manager = new Manager(3);
		ArrayList<OptionType> taskTypes = new ArrayList<OptionType>();
		taskTypes.add(OptionType.Airco);
		taskTypes.add(OptionType.Body);
		taskTypes.add(OptionType.Color);
		workstation = new Workstation(1, taskTypes);
		assertEquals(1, workstation.getId());
		assertEquals(taskTypes, workstation.getTaskTypes());
		assertTrue(workstation.getAllCompletedTasks(carMechanic).isEmpty());
		assertTrue(workstation.getAllPendingTasks(carMechanic).isEmpty());
		
		ArrayList<String> actions1 = new ArrayList<String>();
		actions1.add("action1");
		actions1.add("action2");
		OptionType type1 = OptionType.Body;
		validTask = new AssemblyTask(actions1, type1);
		
		ArrayList<String> actions2 = new ArrayList<String>();
		actions2.add("action1");
		actions2.add("action2");
		OptionType type2 = OptionType.Gearbox;
		invalidTask = new AssemblyTask(actions2, type2);
	}
	
	@Test
	public void testCar() throws UserAccessException, IOException, CarModelCatalogException {
		GarageHolder holder = new GarageHolder(1);
		CarModelCatalog catalog = new CarModelCatalog();
		CarModel model= catalog.getCarModel("Ford");
		ArrayList<Option> allOptions = model.getOptions();
		ArrayList<Option> selectedOptions = new ArrayList<Option>();
		selectedOptions.add(allOptions.get(0));
		selectedOptions.add(allOptions.get(1));
		selectedOptions.add(allOptions.get(2));
		selectedOptions.add(allOptions.get(3));
		selectedOptions.add(allOptions.get(4));
		selectedOptions.add(allOptions.get(5));
		selectedOptions.add(allOptions.get(6));
		CarOrder order = new CarOrder(1, holder, model, selectedOptions);
		Car car = order.getCar();
		CarAssemblyProcess process = car.getAssemblyprocess();
		workstation.setCurrentCar(process);
		assertEquals(process, workstation.getCurrentCar());
		workstation.clearCar();
		assertEquals(null, workstation.getCurrentCar());
		try {
			workstation.getActiveTaskInformation(carMechanic);
			fail("No IllegalStateException was thrown");
		}
		catch (IllegalStateException e) {
		}
		
	}
	
	@Test
	public void testCompleteTask() throws IllegalStateException, UserAccessException {
		workstation.addAssemblyTask(manager, validTask);
		workstation.addCarMechanic(carMechanic);
		workstation.selectTask(carMechanic, validTask);
		CarMechanic otherCarMechanic = new CarMechanic(4);
		try {
			workstation.completeTask(garageHolder);
			assertTrue("UserAccessException was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		try {
			workstation.completeTask(otherCarMechanic);
			assertTrue("UserAccessException was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		workstation.completeTask(carMechanic);
		assertTrue(workstation.getAllCompletedTasks(carMechanic).contains(validTask));
		assertTrue(workstation.getAllPendingTasks(carMechanic).isEmpty());
		assertTrue(workstation.hasAllTasksCompleted(manager));
	}
	
	@Test
	public void testSelectTask() throws IllegalStateException, UserAccessException, IllegalArgumentException {
		workstation.addAssemblyTask(manager, validTask);
		workstation.addCarMechanic(carMechanic);
		try {
			workstation.selectTask(carMechanic, invalidTask);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		workstation.selectTask(carMechanic, validTask);
		assertEquals(validTask.getType().toString(), workstation.getActiveTaskInformation(carMechanic).get(0));
		try {
			workstation.selectTask(carMechanic, validTask);
			assertTrue("IllegalStateException was not thrown", false);
		}
		catch (IllegalStateException e) {
		}
		ArrayList<String> taskInformation = workstation.getActiveTaskInformation(carMechanic);
		assertEquals(validTask.getType().toString(), taskInformation.get(0));
		assertEquals(validTask.getActions().get(0), taskInformation.get(1));
		assertEquals(validTask.getActions().get(1), taskInformation.get(2));
	}
	
	
	@Test
	public void testAddTask() throws IllegalArgumentException, UserAccessException {
		try {
			workstation.addAssemblyTask(garageHolder, validTask);
			assertTrue("UserAccessException was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		try {
			workstation.addAssemblyTask(carMechanic, validTask);
			assertTrue("UserAccessException was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		try {
			workstation.addAssemblyTask(manager, invalidTask);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		workstation.addAssemblyTask(manager, validTask);
		assertTrue(workstation.getAllCompletedTasks(carMechanic).isEmpty());
		assertTrue(workstation.getAllPendingTasks(carMechanic).contains(validTask));
	}
	
	@Test
	public void testAddCarMechanic() {
		try {
			workstation.addCarMechanic(garageHolder);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		try {
			workstation.addCarMechanic(manager);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		workstation.addCarMechanic(carMechanic);
	}
	
	@Test
	public void testCompleteTaskWithoutCarMechanic() throws IllegalStateException, UserAccessException {
		try {
			workstation.completeTask(manager);
			assertTrue("IllegalArgumentExcpetion was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		try {
			workstation.completeTask(carMechanic);
			assertTrue("IllegalArgumentExcpetion was not thrown", false);
		}
		catch (IllegalStateException e) {
		}
	}
}
