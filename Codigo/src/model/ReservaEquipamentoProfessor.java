package model;

import exception.ReserveException;

public class ReservaEquipamentoProfessor extends EquipmentReserve {

	private Professor professor;

	// Message for exception
	private final String PROFESSOR_NULO = "O professor esta nulo.";

	/**
	 * Constructor method for model ReservaEquipamentoProfessor
	 * @param data
	 * @param hora
	 * @param equipamento
	 * @param professor
	 * @throws ReserveException
	 */
	public ReservaEquipamentoProfessor( String data, String hora,
			Equipamento equipamento, Professor professor )
			throws ReserveException {
		super( data, hora, equipamento );
		this.setProfessor( professor );
	}

	/**
	 * Getter method for attribute 'professor'
	 * @return
	 */
	public Professor getProfessor() {
		return professor;
	}

	/**
	 * Setter method for attribute 'professor'
	 * @param professor
	 * @throws ReserveException
	 */
	public void setProfessor( Professor professor ) throws ReserveException {
		if ( professor == null ) {
			throw new ReserveException( PROFESSOR_NULO );
		} 
		this.professor = professor;
	}

	/**
	 * Function to validate if an object passed is equal to an instancied object
	 * @param obj
	 * @return
	 */
	public boolean equals( ReservaEquipamentoProfessor obj ) {
		return ( super.equals( obj ) && this.getEquipamento().equals(
				obj.getEquipamento() ) );
	}

	@Override
	/**
	 * To String method for the class ReservaEquipamentoProfessor
	 */
	public String toString() {
		return "ReservaEquipamentoProfessor [professor="
				+ this.getEquipamento().toString() + ", toString()="
				+ super.toString() + "]";
	}

}
