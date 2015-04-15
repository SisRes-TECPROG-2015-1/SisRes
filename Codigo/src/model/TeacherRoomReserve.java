package model;

import exception.ReserveException;

public class TeacherRoomReserve extends RoomReserve {

	private Teacher teacher;

	// Message for exception
	private final String NULL_TEACHER = "O professor esta nulo.";

	/**
	 * Constructor method for class ReservaSalaProfessor
	 * @param date
	 * @param hour
	 * @param room
	 * @param finality
	 * @param teacher
	 * @throws ReserveException
	 */
	public TeacherRoomReserve( String date, String hour, Sala room,
			String finality, Teacher teacher ) throws ReserveException {
		super( date, hour, room, finality );
		this.setProfessor( teacher );
	}

	/**
	 * Getter method for attribute 'professor'
	 * @return
	 */
	public Teacher getProfessor() {
		return this.teacher;
	}

	/**
	 * Setter method for attribute 'professor'
	 * @return
	 */
	public void setProfessor( Teacher teacher ) throws ReserveException {
		if ( teacher == null ) {
			throw new ReserveException( NULL_TEACHER );
		} else { 
			// Nothing to do
		}
		this.teacher = teacher;
	}

	/**
	 * Function to validate if an TeacherRoomReserveObjectect passed is equal to an instancied TeacherRoomReserveObjectect
	 * @param TeacherRoomReserveObject
	 * @return
	 */
	public boolean equals( TeacherRoomReserve TeacherRoomReserveObject ) {
		return ( super.equals( TeacherRoomReserveObject ) && this.getProfessor().equals(
				TeacherRoomReserveObject.getProfessor() ) );
	}

	@Override
	/**
	 * To string method for class ReservaSalaProfessor
	 */
	public String toString() {
		return "ReservaSalaProfessor [professor="
				+ this.getProfessor().toString() + ", toString()="
				+ super.toString() + "]";
	}

}
