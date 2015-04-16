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
	public Student getAluno() {
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
	 * Setter method for attribute 'student'
	 * @return
	 */
	public void setAluno( Student student ) throws ReserveException {
		if ( student == null ) { 
			throw new ReserveException( NULL_STUDENT );
		} else {
			// Nothing to do
		}
		this.student = student;
	}

	/**
	 * Setter method for attribute 'cadeira_reservada'
	 * @return
	 */
	public void setReservedChairs( String reserved_chairs )
			throws ReserveException {
		String chairs_control = reserved_chairs;
		if ( chairs_control == null ) { 
			throw new ReserveException( NULL_CHAIRS );
		} else { 
			// Nothing to do
		}
		chairs_control = chairs_control.trim();
		if ( chairs_control.equals( "" ) ) { 
			throw new ReserveException( BLANK_CHAIRS );
		} else if ( chairs_control.matches( CHAIRS_PATTERN ) ) {
			if ( Integer.parseInt( super.getSala().getCapacity() ) < Integer
					.parseInt( reserved_chairs ) ) { 
				throw new ReserveException( OVER_LIMIT_CHAIRS );
			} else {
				this.reserved_chairs = reserved_chairs;
			}
		} else {
			throw new ReserveException( INVALID_CHAIRS );
		}
	}

	/**
	 * Function to validate if an StudentRoomReserveObjectect passed is equal to an instancied StudentRoomReserveObjectect
	 * @param StudentRoomReserveObject
	 * @return
	 */
	public boolean equals( StudentRoomReserve StudentRoomReserveObject ) {
		return ( super.equals( StudentRoomReserveObject ) && this.getAluno().equals( StudentRoomReserveObject.getAluno() ) && this
				.getReservedChairs().equals( StudentRoomReserveObject.getReservedChairs() ) );
	}

	@Override
	/**
	 * To String method for class ReservaSalaAluno
	 */
	public String toString() {
		return "Aluno: " + this.getAluno().toString()
				+ "\nCadeiras Reservadas: " + this.getReservedChairs()
				+ super.toString();
	}

}
