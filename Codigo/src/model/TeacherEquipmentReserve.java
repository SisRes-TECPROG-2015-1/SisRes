package model;

import exception.ReserveException;

public class TeacherEquipmentReserve extends EquipmentReserve {

	private Professor teacher;

	// Message for exception
	private final String PROFESSOR_NULO = "O professor esta nulo.";

	/**
	 * Constructor method for model TeacherEquipmentReserve
	 * @param date
	 * @param hour
	 * @param equipamento
	 * @param professor
	 * @throws ReserveException
	 */
	public TeacherEquipmentReserve( String date, String hour,
			Equipamento equipamento, Professor teacher )
			throws ReserveException {
		super( date, hour, equipamento );
		this.setProfessor( teacher );
	}

	/**
	 * Getter method for attribute 'teacher'
	 * @return
	 */
	public Professor getProfessor() {
		return teacher;
	}

	/**
	 * Setter method for attribute 'teacher'
	 * @param teacher
	 * @throws ReserveException
	 */
	public void setProfessor( Professor teacher ) throws ReserveException {
		if ( teacher == null ) {
			throw new ReserveException( PROFESSOR_NULO );
		} 
		this.teacher = teacher;
	}

	/**
	 * Function to validate if an object passed is equal to an instancied object
	 * @param obj
	 * @return
	 */
	public boolean equals( TeacherEquipmentReserve obj ) {
		return ( super.equals( obj ) && this.getEquipment().equals(
				obj.getEquipment() ) );
	}

	@Override
	/**
	 * To String method for the class TeacherEquipmentReserve
	 */
	public String toString() {
		return "ReservaEquipamentoProfessor [professor="
				+ this.getEquipment().toString() + ", toString()="
				+ super.toString() + "]";
	}

}
