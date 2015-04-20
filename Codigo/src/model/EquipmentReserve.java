package model;

import exception.ReserveException;

public class EquipmentReserve extends Reserve {

	private Equipment equipment;

	// Message for exception
	private final String NULL_EQUIPMENT = "O equipamento esta nulo.";

	/**
	 * Constructor method for ReservaEquipamento class
	 * @param date
	 * @param hour
	 * @param equipment
	 * @throws ReserveException
	 */
	public EquipmentReserve( String date, String hour, Equipment equipment )
							throws ReserveException {
		super( date, hour );
		this.setEquipment( equipment );
	}

	/**
	 * Getter method for attribute 'equipment'
	 * @return
	 */
	public Equipment getEquipment() {
		return this.equipment;
	}

	/**
	 * Checks if the object is null
	 * @param teacher
	 * @param exceptionMessage
	 * @throws ReserveException
	 * @return Boolean - Nullity of the instance teacher
	 */
	public boolean checkEquipmentNullity (Equipment teacher, String exceptionMessage)throws ReserveException{
		if ( teacher == null ){
			launchException( exceptionMessage );
			return true;
		} else return false;
	}
	
	/**
	 * Setter method for attribute 'equipment'
	 * @param equipment
	 * @throws ReserveException
	 */
	public void setEquipment( Equipment equipment ) throws ReserveException {
		boolean isNull = checkEquipmentNullity(equipment, NULL_EQUIPMENT);
		
		if ( isNull == false ) { 
			this.equipment = equipment;
		}
	}

	public void launchException (String message)throws ReserveException {
		throw new ReserveException( message );
	}
	
	
	/**
	 * Function to validate if an object passed is equal to an instanced object
	 * @param reserve
	 * @return Boolean - Equality between two objects
	 */
	public boolean equals( EquipmentReserve reserve ) {
		return ( super.equals( reserve ) && this.getEquipment().equals( 
				reserve.getEquipment() ) );
	}

	@Override
	/**
	 * To string method for class ReservaEquipamento
	 */
	public String toString() {
		return "ReservaEquipamento [equipamento=" + this.getEquipment()
				+ ", toString()=" + super.toString() + "]";
	}

}
