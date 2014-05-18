package domain.configuration;


public class OptionType extends TaskType{


	/**
	 * Constructor of OptionType.
	 * 
	 * @param name
	 * 		The name of this optionType.
	 * @param singleTaskPossible
	 * 		True if this option is available for single task orders, otherwise false.
	 * @param mandatory
	 * 		True if this option is mandatory for vehicle orders, otherwise false.
	 */
	OptionType(String name, boolean singleTaskPossible, boolean mandatory){
		super.name = name;
		super.singleTaskPossible = singleTaskPossible;
		super.mandatory =mandatory;
	}

	/**
	 * Constructor of OptionType.
	 * The option type won't be available for single task orders and won't be mandatory for vehicle orders.
	 */
	OptionType(String name){
		super.name = name;
		super.singleTaskPossible = false;
		super.mandatory = true;
	}
}