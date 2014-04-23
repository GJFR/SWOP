package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.order.SingleTaskOrder;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;
import domain.assembly.CarAssemblyProcess;
import domain.assembly.Workstation;

public class CarAssemblyProcessTest {

	Workstation w1;
	Workstation w2;
	Workstation w3;
	
	ArrayList<OptionType> taskTypes1;
	ArrayList<OptionType> taskTypes2;
	ArrayList<OptionType> taskTypes3;
	
	CarAssemblyProcess process;

	
	@Before
	public void testCreate() throws IOException, CarModelCatalogException, InvalidConfigurationException{
		
		// MAAK EEN AUTO MET OPTIONS EN MODEL AAN
		
		
		process = createCar().getAssemblyprocess();
	
		taskTypes1 = new ArrayList<OptionType>();
		taskTypes1.add(OptionType.Body);
		taskTypes1.add(OptionType.Color);
		Workstation workStation1 = new Workstation(null, 1, taskTypes1);
		
		taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Engine);
		taskTypes2.add(OptionType.Gearbox);
		Workstation workStation2 = new Workstation(null, 2, taskTypes2);
		
		taskTypes3 = new ArrayList<OptionType>();
		taskTypes3.add(OptionType.Seats);
		taskTypes3.add(OptionType.Airco);
		taskTypes3.add(OptionType.Wheels);
		Workstation workStation3 = new Workstation(null, 3, taskTypes3);
		
		w1 = workStation1;
		w2 = workStation2;
		w3 = workStation3;
	}
	
	@Test
	public void testMatching(){
		for(int i = 0; i<process.compatibleWith(w1).size(); i++){
			assertTrue(taskTypes1.contains(process.compatibleWith(w1).get(i).getType()));
		}
		
		for(int i = 0; i<process.compatibleWith(w2).size(); i++){
			assertTrue(taskTypes2.contains(process.compatibleWith(w2).get(i).getType()));
		}
		
		for(int i = 0; i<process.compatibleWith(w3).size(); i++){
			assertTrue(taskTypes3.contains(process.compatibleWith(w3).get(i).getType()));
		}
	}
	
	@Test
	public void testFilterWorkstation() throws InvalidConfigurationException, CarModelCatalogException{
		ArrayList<Workstation> stations = new ArrayList<Workstation>();
		stations.add(w1);
		stations.add(w2);
		stations.add(w3);
		
		ArrayList<Workstation> filtered = process.filterWorkstations(stations);
		assertEquals(3,filtered.size());
		assertTrue(filtered.contains(w1));
		assertTrue(filtered.contains(w2));
		assertTrue(filtered.contains(w3));
		
		CarAssemblyProcess carProcess = createSingleTask().getAssemblyprocess();
		
		ArrayList<Workstation> filtered2 = carProcess.filterWorkstations(stations);
		assertEquals(1,filtered2.size());
		assertTrue(filtered2.contains(w1));
	}
	
	@Test
	public void testTimeWorked(){
		assertEquals(0, process.getTotalTimeSpend());
		process.addTimeWorked(10);
		assertEquals(10, process.getTotalTimeSpend());
		process.addTimeWorked(1000);
		assertEquals(1010, process.getTotalTimeSpend());
		process.addTimeWorked(10);
		assertEquals(1020, process.getTotalTimeSpend());
	}
	
	private SingleTaskOrder createSingleTask() throws InvalidConfigurationException, CarModelCatalogException{
		
		Policy singleTaskPolicy = new SingleTaskOrderNumbersOfTasksPolicy(null);
		Configuration config = new Configuration(null, singleTaskPolicy);
		config.addOption(new Option("test", OptionType.Color));
		config.complete();
		CustomShopManager customShop = new CustomShopManager(1);
		
		GregorianCalendar now = new GregorianCalendar();
		
		return new SingleTaskOrder(0, customShop, config, now, now);
	}
	
	private CarOrder createCar() throws InvalidConfigurationException, IOException, CarModelCatalogException{
		
		Policy pol1 = new CompletionPolicy(null,OptionType.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		Policy carOrderPolicy= pol4;
		
		
		CarModelCatalog catalog = new CarModelCatalog();
		CarModel carModel = null;
		for(CarModel m : catalog.getAllModels()){
			if(m.getName().equals("Model A")){
				carModel = m;
				continue;
			}
		}
		
		Configuration config = new Configuration(carModel, carOrderPolicy);
		
		for(Option option : catalog.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("comfort")
					)
				config.addOption(option);
		}
		config.complete();
		GarageHolder garageHolder = new GarageHolder(1);
		
		GregorianCalendar now = new GregorianCalendar();
		CarOrder carOrder = new CarOrder(1, garageHolder, config, now);
		return carOrder;
	}
	
	
}
