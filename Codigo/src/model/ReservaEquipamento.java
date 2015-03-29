package model;

import exception.ReservaException;

public class ReservaEquipamento extends Reserva {

	private Equipamento equipamento;

	// Message for exception
	private final String EQUIPAMENTO_NULO = "O equipamneto esta nulo.";

	/**
	 * Constructor method for ReservaEquipamento class
	 * @param data
	 * @param hora
	 * @param equipamento
	 * @throws ReservaException
	 */
	public ReservaEquipamento( String data, String hora, Equipamento equipamento )
			throws ReservaException {
		super( data, hora );
		this.setEquipamento( equipamento );
	}

	/**
	 * Getter method for attribute 'equipamento'
	 * @return
	 */
	public Equipamento getEquipamento() {
		return this.equipamento;
	}

	/**
	 * Setter method for attribute 'equipamento'
	 * @param equipamento
	 * @throws ReservaException
	 */
	public void setEquipamento( Equipamento equipamento ) throws ReservaException {
		if ( equipamento == null )
			throw new ReservaException( EQUIPAMENTO_NULO );
		this.equipamento = equipamento;
	}

	/**
	 * Function to validate if an object passed is equal to an instancied object
	 * @param obj
	 * @return
	 */
	public boolean equals( ReservaEquipamento obj ) {
		return ( super.equals( obj ) && this.getEquipamento().equals( 
				obj.getEquipamento() ) );
	}

	@Override
	/**
	 * To string method for class ReservaEquipamento
	 */
	public String toString() {
		return "ReservaEquipamento [equipamento=" + this.getEquipamento()
				+ ", toString()=" + super.toString() + "]";
	}

}
