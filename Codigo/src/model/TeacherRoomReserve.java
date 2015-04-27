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
	public TeacherRoomReserve( String date, String hour, Room room,
			String finality, Teacher teacher ) throws ReserveException {
		super( date, hour, room, finality );
		this.setTeacher( teacher );
	}

	/**
	 * Getter method for attribute 'teacher'
	 * @return Teacher - An instance o a teacher
	 */
	public Teacher getTeacher() {
		return this.teacher;
	}

	/**
	 * Setter method for attribute 'teacher'
	 */
	public void setTeacher( Teacher teacher ) throws ReserveException {
		if ( teacher == null ) {
			throw new ReserveException( NULL_TEACHER );
		} else { 
			// Nothing to do
		}
		this.teacher = teacher;
	}

	/**
	 * Function to validate if an TeacherRoomReserveObject passed is equal to an instanced TeacherRoomReserveObjectect
	 * @param TeacherRoomReserveObject
	 * @return boolean = 
	 */
	public boolean equals( TeacherRoomReserve TeacherRoomReserveObject ) {
		boolean isTeacherRoomReserve = super.equals( TeacherRoomReserveObject );
		boolean isTeacher = this.getTeacher().equals(TeacherRoomReserveObject.getTeacher());
		boolean areEqualInstances = isTeacherRoomReserve &&  isTeacher;
		return areEqualInstances;
	}

	@Override
	/**
	 * To string method for class ReservaSalaProfessor
	 */
	public String toString() {
		return "TeacherRoomReserve [professor="
				+ this.getTeacher().toString() + ", toString()="
				+ super.toString() + "]";
	}

}
