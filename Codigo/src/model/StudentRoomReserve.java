package model;

import exception.ReserveException;

public class StudentRoomReserve extends RoomReserve {

	private Student student;
	private String reserved_chairs;

	// Messages for exception
	private final String NULL_STUDENT = "O aluno esta nulo.";
	private final String NULL_CHAIRS = "O numero de cadeiras esta nulo.";
	private final String BLANK_CHAIRS = "O numero de cadeiras esta em branco.";
	private final String INVALID_CHAIRS = "O numero de cadeira eh invalido.";
	private final String OVER_LIMIT_CHAIRS = "A sala nao possui este numero de cadeiras para reservar.";
	private final String CHAIRS_PATTERN = "^[\\d]+$";

	/**
	 * Constructor method for class ReservaSalaAluno
	 * @param date
	 * @param hour
	 * @param sala
	 * @param finality
	 * @param reserved_chairs
	 * @param student
	 * @throws ReserveException
	 */
	public StudentRoomReserve( String date, String hour, Room sala,
			String finality, String reserved_chairs, Student student )
			throws ReserveException {
		super( date, hour, sala, finality );
		this.setAluno( student );
		this.setReservedChairs( reserved_chairs );
	}

	/**
	 * Getter method for attribute 'student'
	 * @return
	 */
	public Student getStudent() {
		return this.student;
	}
	
	/**
	 * Getter method for attribute 'reserved_chairs'
	 * @return
	 */
	public String getReservedChairs() {
		return this.reserved_chairs;
	}

	/**
	 * Launches an exception message
	 * @param message
	 * @throws ReserveException
	 */
	public void launchException (String message)throws ReserveException {
		throw new ReserveException( message );
	}

	/**
	 * Checks if the object is null
	 * @param student
	 * @param exceptionMessage
	 * @throws ReserveException
	 * @return Boolean - Nullity of the instance student
	 */
	public boolean checkNullity (Student student, String exceptionMessage)throws ReserveException{
		if ( student == null ){
			launchException( exceptionMessage );
			return true;
		} else return false;
	}

	
	/**
	 * Setter method for attribute 'student'
	 * @return
	 */
	public void setAluno( Student student ) throws ReserveException {
		boolean isNull = checkNullity(student, NULL_STUDENT);
		if ( isNull == false) { 
			this.student = student;
		} else;
	}

		
	
	/**
	 * Setter method for attribute 'cadeira_reservada'
	 * @return
	 */
	public void setReservedChairs( String reserved_chairs )	throws ReserveException {
		String chairs_control = reserved_chairs;
		
		if ( chairs_control == null ) { 
			launchException( NULL_CHAIRS );
		} else; 
		
		chairs_control = chairs_control.trim(); // trim returns a copy of this string with leading and trailing white space removed
		
		if ( chairs_control.equals( "" ) ) { 
			launchException( BLANK_CHAIRS );
		} else {	
			try{
				Integer value_control = new Integer(chairs_control);
				value_control.toString();
			}
			catch(NumberFormatException nfe){
				launchException( INVALID_CHAIRS );
			}
			boolean hasCapacity = roomHasCapacity( chairs_control );
			if ( hasCapacity == true){
				this.reserved_chairs = reserved_chairs;
				
			} else {				
				launchException( INVALID_CHAIRS );
			}
		}
	}

	
	public boolean roomHasCapacity 	( String reserved_chairs )throws ReserveException{
		boolean hasCapacity = true;
		if ( reserved_chairs.matches( CHAIRS_PATTERN ) ) {
			if ( Integer.parseInt( super.getRoom().getCapacity() ) < Integer.parseInt( reserved_chairs ) ) { 
				hasCapacity = false;
				throw new ReserveException( OVER_LIMIT_CHAIRS );
			}else return hasCapacity;
		}else return hasCapacity;	
	}
	
	/**
	 * Function to validate if an StudentRoomReserveObjectect passed is equal to an instancied StudentRoomReserveObjectect
	 * @param StudentRoomReserveObject
	 * @return
	 */
	public boolean equals( StudentRoomReserve StudentRoomReserveObject ) {
		return ( super.equals( StudentRoomReserveObject ) && this.getStudent().equals( StudentRoomReserveObject.getStudent() ) && this
				.getReservedChairs().equals( StudentRoomReserveObject.getReservedChairs() ) );
	}

	@Override
	/**
	 * To String method for class ReservaSalaAluno
	 */
	public String toString() {
		return "Aluno: " + this.getStudent().toString()
				+ "\nCadeiras Reservadas: " + this.getReservedChairs()
				+ super.toString();
	}

}
