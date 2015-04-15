package model;

import exception.ReserveException;

public class TeacherEquipmentReserve extends EquipmentReserve {

	private Teacher teacher;

	// Message for exception
	private final String NULL_TEACHER = "O professor esta nulo.";

	/**
	 * Constructor method for model TeacherEquipmentReserve
	 * @param date
	 * @param hour
	 * @param equipamento
	 * @param professor
	 * @throws ReserveException
	 */
	public TeacherEquipmentReserve( String date, String hour,
			Equipment equipamento, Teacher teacher )
			throws ReserveException {
		super( date, hour, equipamento );
		this.setProfessor( teacher );
	}

	/**
	 * Getter method for attribute 'teacher'
	 * @return
	 */
	public Teacher getProfessor() {
		return teacher;
	}

	/**
	 * Setter method for attribute 'teacher'
	 * @param teacher
	 * @throws ReserveException
	 */
	public void setProfessor( Teacher teacher ) throws ReserveException {
		if ( teacher == null ) {
			throw new ReserveException( NULL_TEACHER );
		} 
		this.teacher = teacher;
	}

	/**
	 * Function to validate if an TeacherEquipmentReserveObjectect passed is equal to an instancied TeacherEquipmentReserveObjectect
	 * @param TeacherEquipmentReserveObject
	 * @return
	 */
	public boolean equals( TeacherEquipmentReserve TeacherEquipmentReserveObject ) {
		return ( super.equals( TeacherEquipmentReserveObject ) && this.getEquipment().equals(
				TeacherEquipmentReserveObject.getEquipment() ) );
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
