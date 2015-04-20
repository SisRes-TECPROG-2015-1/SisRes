package model;

import exception.ReserveException;

public class RoomReserve extends Reserve {

	private Room room;
	private String finality;

	// Message for exception
	private final String NULL_ROOM = "A sala esta nula.";
	private final String NULL_FINALITY = "A finalidade esta nula.";
	private final String BLANK_FINALITY = "A finalidade esta em branco.";

	/**
	 * Constructor method for ReservaSala model class
	 * @param date
	 * @param hour
	 * @param room
	 * @param finality
	 * @throws ReserveException
	 */
	public RoomReserve( String date, String hour, Room room, String finality )
			throws ReserveException {
		super( date, hour );
		this.setSala( room );
		this.setFinality( finality );
	}

	/**
	 * Getter method for attribute 'room'
	 * @return
	 */
	public Room getSala() {
		return this.room;
	}

	/**
	 * Getter method for attribute 'finality'
	 * @return
	 */
	public String getFinality() {
		return this.finality;
	}

	/**
	 * Setter method for attribute 'room'
	 */
	public void setSala( Room room ) throws ReserveException {
		if ( room == null ){
			launchException( NULL_ROOM );
		} else this.room = room;
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
	 * @param finality
	 * @param exceptionMessage
	 * @throws ReserveException
	 * @return Boolean - Nullity of the instance room
	 */
	public boolean checkRoomNullity (String finality, String exceptionMessage)throws ReserveException{
		if ( finality == null ){
			launchException( exceptionMessage );
			return true;
		} else return false;
	}
	
	/**
	 * Checks for blank spaces
	 * @param name
	 * @param exceptionMessage
	 * @return
	 * @throws ReserveException
	 */
	public boolean checkForBlankSpace (String name, String exceptionMessage) throws ReserveException{
		if ( name.equals( "" ) ) {
			launchException( exceptionMessage );
			return true;
		} 
		return false;
	}
	
	
	/**
	 * Setter method for attribute 'finality'
	 * @return
	 */
	public void setFinality( String finality ) throws ReserveException {
		boolean isNull = checkRoomNullity( finality, NULL_FINALITY );
		if (isNull == false){
			finality = finality.trim();
			boolean hasBlankSpace = checkForBlankSpace ( finality, BLANK_FINALITY );
			if ( hasBlankSpace == false ){
				this.finality = finality;
			}else;
		}
	}
	
	

	/**
	 * Function to validate if an roomReserveObjectect passed is equal to an instancied roomReserveObjectect
	 * @param roomReserveObject
	 * @return
	 */
	public boolean equals( RoomReserve roomReserveObject ) {
		return ( super.equals( roomReserveObject ) && this.getSala().equals( roomReserveObject.getSala() ) && this
				.getFinality().equals( roomReserveObject.getFinality() ) );
	}

	@Override
	/**
	 * To string method for ReservaSala class
	 */
	public String toString() {
		return "\n" + this.getSala().toString() + "\nFinalidade="
				+ this.getFinality() + super.toString();
	}

}
