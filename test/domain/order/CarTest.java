package domain.order;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Test;

import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.user.GarageHolder;

public class CarTest {


	private static Configuration config;
	private static ArrayList<Option> allOptions;

	@Test
	public void testCreate() throws IOException, CarModelCatalogException {

		Policy pol1 = new CompletionPolicy(null,OptionType.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);


		GarageHolder holder = new GarageHolder(1);
		CarModelCatalog catalog = new CarModelCatalog();
		CarModel carModel = null;
		for(CarModel m : catalog.getAllModels()){
			if(m.getName().equals("Model A")){
				carModel = m;
				continue;
			}
		}

		config = new Configuration(carModel, pol4);


		allOptions = carModel.getPossibleOptions();
		try {
			config.addOption(allOptions.get(0));
			config.addOption(allOptions.get(2));
			config.addOption(allOptions.get(6));
			for(Option option : allOptions){
				if(option.getDescription().equals("6 speed manual")
						||option.getDescription().equals("leather white")
						||option.getDescription().equals("sports")
						){
					config.addOption(option);
				}
			}
			config.complete();
		}catch (InvalidConfigurationException e) {
			System.out.println(e.getMessage());
			fail();
		}

		CarOrder car = new CarOrder(1, holder, config, new GregorianCalendar());
		assertEquals(false, car.isCompleted());
	}


	@Test(expected=InvalidConfigurationException.class)
	public void testPoliciesMessages() throws InvalidConfigurationException{
		config.addOption(allOptions.get(1));
		config.addOption(allOptions.get(3));
		config.addOption(allOptions.get(4));
		config.addOption(allOptions.get(5));
	}

}
