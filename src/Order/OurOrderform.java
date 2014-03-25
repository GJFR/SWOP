package Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Car.CarModel;
import Car.Option;
import Order.OptionSubTypes.OptionType;
import User.User;

public class OurOrderform implements OrderForm{
	private HashMap<OptionType,Option> options;
	private CarModel model;
	private User user;
	private CarModelCatalog catalog;
	public OurOrderform(User user, CarModel model, CarModelCatalog catalog) {
		options= new HashMap<OptionType,Option>();
		this.user= user;
		this.catalog = catalog;
		this.model = model;
	}
		
	@Override
	public ArrayList<Option> getOptions() {
		return  new ArrayList<Option>(options.values());
	}

	@Override
	public CarModel getModel() {
		return model;
	}
	@Override
	public User getUser() {
		return user;
	}
	
	@Override
	public List<String> getPossibleOptionsOfType(String type) {
		List<String> result = new ArrayList<String>();
			for(Option i: model.getOptions()){
				if(i.getType().toString().equals(type)){
					Boolean incompatible = false;
					for(Option j: options.values()){
						incompatible=	incompatible || j.conflictsWith(i);
					}
					if(!incompatible) result.add(i.getDescription());
				}
			}
			return result;
	}


	@Override
	public boolean setOption(String description) {
		Option option = catalog.getOption(description);
		if(option == null) return false;
		if(model.getOptions().contains(option) && !this.options.containsKey(option.getType())){
			this.options.put(option.getType(), option);
			return true;
		}
		return false;
	}
	@Override
	public boolean canPlaceType(String Type) {
		return options.containsKey(Type);
	}
	
	public List<String> getOptionTypes() {
		ArrayList<String> result = new ArrayList<String>();
		for(OptionType i:OptionType.values()) result.add(i.toString());
				return result;
	}
}
