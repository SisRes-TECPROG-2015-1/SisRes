package model;

import exception.ReserveException;

public class EquipmentReserve extends Reserve {

	private Equipment equipment;

	// Message for exception
	private final String EQUIPAMENTO_NULO = "O equipamneto esta nulo.";

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
	 * Setter method for attribute 'equipment'
	 * @param equipamento
	 * @throws ReserveException
	 */
	public void setEquipment( Equipment equipment ) throws ReserveException {
		if ( equipment == null ) { 
			throw new ReserveException( EQUIPAMENTO_NULO );
		}
		this.equipment = equipment;
	}

	/**
	 * Function to validate if an object passed is equal to an instancied object
	 * @param obj
	 * @return
	 */
	public boolean equals( EquipmentReserve obj ) {
		return ( super.equals( obj ) && this.getEquipment().equals( 
				obj.getEquipment() ) );
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
