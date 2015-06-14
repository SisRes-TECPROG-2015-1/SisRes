package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import model.Teacher;
import model.TeacherRoomReserve;
import model.Room;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

public class TeacherRoomReserveDAO extends DAO {

	/**
	 * Defining constants for error messages and alerts. It will stay like that
	 * for future analyse.
	 */
	private final String NULL_TERM = "Termo nulo.";
	private final String UNAVAILABLE_ROOM = "A room esta reservada no mesmo dia e hourrio.";
	private final String ABSENT_TEACHER = "teacher inexistente.";
	private final String ABSENT_ROOM = "room inexistente";
	private final String ABSENT_RESERVE = "Reserva inexistente";
	private final String EXISTING_RESERV = "A reserva ja existe.";
	private final String DATE_TIME_PASSED = "A date escolhida ja passou.";
	private final String HOUR_PASSED = "A hour escolhida ja passou.";

	// Singleton
	private static TeacherRoomReserveDAO instance;

	/**
	 * NULL constructor for TeacherRoomReserveDAO
	 */
	private TeacherRoomReserveDAO() {
	}

	/**
	 * getInstance method for external instances of TeacherRoomReserveDAO
	 * 
	 * @return
	 */
	public static TeacherRoomReserveDAO getInstance() {
		if ( instance == null ) {
			instance = new TeacherRoomReserveDAO();
		}
		return instance;
	}

	/**
	 * Function to get the string containing a select clause to a teacher
	 * @param teacher
	 * @return String - Select Clause
	 */	
	private String createTeacherSelectClause( Teacher teacher ) {
		String selectClause = "SELECT id_teacher FROM teacher WHERE "
				+ "teacher.name = \"" + teacher.getName() + "\" and "
				+ "teacher.cpf = \"" + teacher.getCpf() + "\" and "
				+ "teacher.fone = \"" + teacher.getFone() + "\" and "
				+ "teacher.email = \"" + teacher.getEmail() + "\" and "
				+ "teacher.registration = \"" + teacher.getRegistration() + "\"";
		return selectClause;
	}

	/**
	 * Function to give the necessary string containing the
	 * instruction to select a room from the datebase 
	 * @param room
	 * @return String - The string to select a room in the datebase
	 */
	private String createRoomSelectFromIdClause( Room room ) {
		String roomSelect = "SELECT id_room FROM room WHERE " + "room.code = \""
				+ room.getCode() + "\" and " + "room.description = \""
				+ room.getDescription() + "\" and " + "room.capacity = "
				+ room.getCapacity();
		return roomSelect;
	}

	/**
	 * Function to construct a where clause for a calling sql query 
	 * @param reservedRoom
	 * @return String - Where clause
	 */
	private String createWhereClause(
			TeacherRoomReserve reservedRoom ) {
		String whereClause = " WHERE " + "id_teacher = ( "
				+ createTeacherSelectClause( reservedRoom.getTeacher() ) + " ) and "
				+ "id_room = ( " + createRoomSelectFromIdClause( reservedRoom.getRoom() )
				+ " ) and " + "finality = \"" + reservedRoom.getFinality()
				+ "\" and " + "hour = \"" + reservedRoom.getHour() + "\" and "
				+ "date = \"" + reservedRoom.getDate() + "\"";
		return whereClause;
	}

	/**
	 * Details the reserve
	 * @param roomReserve
	 * @return String - The details of the reserve
	 */
	private String detailsTeacherRoomReserve(
			TeacherRoomReserve reservingRoom ) {
		String details = "( " + createTeacherSelectClause( reservingRoom.getTeacher() )
				+ " ), " + "( " + createRoomSelectFromIdClause( reservingRoom.getRoom() )
				+ " ), " + "\"" + reservingRoom.getFinality() + "\", " + "\""
				+ reservingRoom.getHour() + "\", " + "\""
				+ reservingRoom.getDate() + "\"";
		return details;
	}

	/**
	 * Updates a reserve in the datebase
	 * @param roomReserve
	 * @return String - Update Clause
	 */
	private String createUpdateClause(
			TeacherRoomReserve updateReservedRoom ) {
		String reserve = "id_teacher = ( "
				+ createTeacherSelectClause( updateReservedRoom.getTeacher() )
				+ " ), " + "id_room = ( "
				+ createRoomSelectFromIdClause( updateReservedRoom.getRoom() ) + " ), "
				+ "finality = \"" + updateReservedRoom.getFinality() + "\", "
				+ "hour = \"" + updateReservedRoom.getHour() + "\", "
				+ "date = \"" + updateReservedRoom.getDate() + "\"";
		return reserve;
	}

	/**
	 * Function to construct an insert sql clause for a new StudentRoomReserve
	 * @param newRoomReserve
	 * @return String - Insert clause
	 */
	private String createInsertClause( TeacherRoomReserve newRoomReserve ) {
		String insertClause = "INSERT INTO "
				+ "reservation_room_teacher (id_teacher, id_room, finality, hour, date) "
				+ "VALUES ( "
				+ detailsTeacherRoomReserve( newRoomReserve )
				+ " );";
		return insertClause;
	}

	/**
	 * Function to construct a delete sql for a TeacherRoomReserve
	 * 
	 * @param reservedRommToDelete
	 * @return String - Teacher reserve delete clause
	 */
	private String createTeacherReserveDeleteClause( TeacherRoomReserve reservedRommToDelete ) {
		String deleteClause = "DELETE FROM reservation_room_teacher "
				+ this.createWhereClause( reservedRommToDelete )
				+ " ;";
		return deleteClause;
	}

	/**
	 * Function to construct an update sql clause to update a teacher room reserve
	 * @param updatingRoomReserve
	 * @param newAttributesRoomToSave
	 * @return String - Update clause
	 */
	private String update( TeacherRoomReserve updatingRoomReserve,
			TeacherRoomReserve newAttributesRoomToSave ) {
		String updateClause = "UPDATE reservation_room_teacher SET "
				+ this.createUpdateClause( newAttributesRoomToSave )
				+ this.createWhereClause( updatingRoomReserve )
				+ " ;";
		return updateClause;
	}
	
	/**
	 * Function to construct a delete sql clause for a teacher room reserve
	 * @param reservedRoomToDelete
	 * @return String - Delete clause
	 */
	private String createDeleteClause( TeacherRoomReserve reservedRoomToDelete ) {
		String deleteClause = "DELETE FROM reservation_room_student WHERE " + "hour = \""
				+ reservedRoomToDelete.getHour() + "\" and " + "date = \""
				+ reservedRoomToDelete.getDate() + "\" ;";
		return deleteClause;
	}
	

	/**
	 * Method to save a new room reserve for a teacher
	 * 
	 * @param roomToReserve
	 * @throws ReserveException
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	public void saveNewTeacherRoomReserve( TeacherRoomReserve roomToReserve )
			throws ReserveException, SQLException {
		
		boolean isATeacher = this.checkTeacherExistence( roomToReserve.getTeacher() );
		
		boolean isARoom = this.checkRoomExistence( roomToReserve.getRoom() );
		
		boolean isARoomReserve = this.checkTeacherRoomReserveExistence( roomToReserve.getRoom(),	
								roomToReserve.getDate(), roomToReserve.getHour() );
		
		boolean isATeacherRoomReserve = this.checkExistingTeacherRoomReserve( roomToReserve ); 
		
		boolean isAStudentRoomReserve = this.checkExistingStudentRoomReserve( roomToReserve.getDate(), 
										roomToReserve.getHour() );
		
		boolean isPassedDate = this.checkPassedDate( roomToReserve.getDate() );
		
		boolean isPassedHour = this.checkDateWithCurrent( roomToReserve.getDate() ) ;
		
		boolean isPassedSeconds =  this.checkHourHasPassed( roomToReserve.getHour() );		
				
		if ( roomToReserve == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( isATeacher == false ) {
			throw new ReserveException( ABSENT_TEACHER );
		} else if ( isARoom == false ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( isARoomReserve ) {
			throw new ReserveException( UNAVAILABLE_ROOM );
		} else if ( isATeacherRoomReserve ) {
			throw new ReserveException( EXISTING_RESERV );
		} else if ( isAStudentRoomReserve ) {
			super.executeQuery( this.createDeleteClause( roomToReserve ) );
		}else{
        	//do nothing
        }
		
		if ( isPassedDate ) {
			throw new ReserveException( DATE_TIME_PASSED );
		}else{
        	//do nothing
        }
		
		if ( isPassedHour ) {
			if ( isPassedSeconds ) {
				throw new ReserveException( HOUR_PASSED );
			} else {
				super.executeQuery( this.createInsertClause( roomToReserve ) );
			}
		} else {
			super.executeQuery( this.createInsertClause( roomToReserve ) );
		}
	}

	/**
	 * Method to update a reserve made by a teacher
	 * 
	 * @param alreadyReservedRoom
	 * @param newReserveRoomdate
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void updateTeacherRoomReserve(
			TeacherRoomReserve alreadyReservedRoom,
			TeacherRoomReserve newReserveRoomdate ) throws ReserveException,
			SQLException {
		
		if(alreadyReservedRoom == null){
			throw new ReserveException( NULL_TERM );
		}
		else{
			//Nothing to do.
		}
		
		if(newReserveRoomdate == null){
			throw new ReserveException( NULL_TERM );
		}
		else{
			//Nothing to do.
		}
		

		boolean isTeacherRoomReserve = this.checkExistingTeacherRoomReserve( alreadyReservedRoom );
		
		boolean isAReserveInTheGivenDay = this.checkExistingTeacherRoomReserve( newReserveRoomdate );
		
		boolean isATeacher = this.checkTeacherExistence( newReserveRoomdate.getTeacher() );
		
		boolean isARoom = this.checkRoomExistence( newReserveRoomdate.getRoom() );
		
		boolean isReservedInTheGivenDate = alreadyReservedRoom.getDate().equals(newReserveRoomdate.getDate() );
		
		boolean isReservedInTheGivenHour = alreadyReservedRoom.getHour().equals(newReserveRoomdate.getHour() );
		
		boolean isReservedInTheGivendateOrTime = isReservedInTheGivenDate || isReservedInTheGivenHour;
		
		boolean isRoomReserve = this.checkTeacherRoomReserveExistence( newReserveRoomdate.getRoom(),
				newReserveRoomdate.getDate(), newReserveRoomdate.getHour() );
		
		boolean isPassedDate = this.checkPassedDate( newReserveRoomdate.getDate() );
		
		boolean isPassedHour = this.checkHourHasPassed( newReserveRoomdate.getHour() );
		
		boolean isPassedDateWithCurrent = this.checkDateWithCurrent( newReserveRoomdate.getDate() );
		
		boolean isPassedDateAndTime = ( ( isPassedHour ) && ( isPassedDateWithCurrent ) );
		
		if ( isTeacherRoomReserve == false ) {
			throw new ReserveException( ABSENT_RESERVE );
		} else if ( isAReserveInTheGivenDay ) {
			throw new ReserveException( EXISTING_RESERV );
		} else if ( isATeacher == false ) {
			throw new ReserveException( ABSENT_TEACHER );
		} else if ( isARoom == false ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( isReservedInTheGivendateOrTime == false) {
			if ( isRoomReserve ) {
				throw new ReserveException( UNAVAILABLE_ROOM );
			}
		}else{
        	//do nothing
        }
		
		if ( isPassedDate ) {
			throw new ReserveException( DATE_TIME_PASSED );
		}else{
        	//do nothing
        }
		
		if ( isPassedDateAndTime ) {
			throw new ReserveException( HOUR_PASSED );
		} else {
			super.updateQuery( this.update( alreadyReservedRoom,
					newReserveRoomdate ) );
		}
	}

	/**
	 * Method to delete some reserved room for a teacher
	 * 
	 * @param reservedRoomToDelete
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void deleteTeacherReservedRoom(
			TeacherRoomReserve reservedRoomToDelete ) throws ReserveException,
			SQLException {
		
		boolean isARoomReserve = this.checkExistingTeacherRoomReserve( reservedRoomToDelete );
		
		if ( reservedRoomToDelete == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( isARoomReserve == false ) {
			throw new ReserveException( ABSENT_RESERVE );
		} else {
			super.executeQuery( this.createTeacherReserveDeleteClause( reservedRoomToDelete ) );
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Function to get all the teachers reserved rooms from datebase
	 * 
	 * @return Vector<TeacherRoomReserve> - Reserves
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 * @throws ClientException 
	 */
	public Vector<TeacherRoomReserve> getAllTeacherReservedRooms()
			throws SQLException, ClientException, PatrimonyException,
			ReserveException, ClientException {
		
		Vector<TeacherRoomReserve> vectorOfReserves = 
				super.search( "SELECT * FROM reservation_room_teacher "
						+ "INNER JOIN room ON room.id_room = reservation_room_teacher.id_room "
						+ "INNER JOIN teacher ON teacher.id_teacher = reservation_room_teacher.id_teacher;" );
		return vectorOfReserves;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Function to get from the datebase the reserved rooms for students in a given day
	 * @param date
	 * @return Vector<TeacherRoomReserve> = A vector of reserves made by teacher
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 * @throws ClientException 
	 */
	public Vector<TeacherRoomReserve> getTeacherReservedRoomsByDay(
			String date ) throws SQLException, ClientException,
			PatrimonyException, ReserveException, ClientException {
		
		Vector<TeacherRoomReserve> vectorOfReserves =
				super.search( "SELECT * FROM reservation_room_teacher "
						+ "INNER JOIN room ON room.id_room = reservation_room_teacher.id_room "
						+ "INNER JOIN teacher ON teacher.id_teacher = reservation_room_teacher.id_teacher"
						+ " WHERE date = \"" + this.standardizeDate( date )
						+ "\";" );
		return vectorOfReserves;
	}

	@Override
	protected Object fetch( ResultSet rs ) throws SQLException,
			ClientException, PatrimonyException, ReserveException, ClientException {
		Teacher p = new Teacher( rs.getString( "name" ),
				rs.getString( "cpf" ), rs.getString( "registration" ),
				rs.getString( "fone" ), rs.getString( "email" ) );

		Room s = new Room( rs.getString( "code" ),
				rs.getString( "description" ), rs.getString( "capacity" ) );

		TeacherRoomReserve r = new TeacherRoomReserve( rs.getString( "date" ),
				rs.getString( "hour" ), s, rs.getString( "finality" ), p );

		return r;
	}

	/**
	 * Function to check if a teacher exists in datebase
	 * 
	 * @param teacher
	 * @return Boolean - Existence of a teacher
	 * @throws SQLException
	 */
	private boolean checkTeacherExistence( Teacher teacher )
			throws SQLException {
		boolean isATeacher =  super.inDBGeneric( "SELECT * FROM teacher WHERE "
				+ "teacher.name = \"" + teacher.getName() + "\" and "
				+ "teacher.cpf = \"" + teacher.getCpf() + "\" and "
				+ "teacher.fone = \"" + teacher.getFone() + "\" and "
				+ "teacher.email = \"" + teacher.getEmail() + "\" and "
				+ "teacher.registration = \"" + teacher.getRegistration() + "\";" );
		return isATeacher;
	}

	/**
	 * Function to check if a room exists in datebase
	 * @param room
	 * @return
	 * @throws SQLException
	 */
	private boolean checkRoomExistence( Room room ) throws SQLException {
		boolean isARoom = super.inDBGeneric( "SELECT * FROM room WHERE "
				+ "room.code = \"" + room.getCode() + "\" and "
				+ "room.description = \"" + room.getDescription() + "\" and "
				+ "room.capacity = " + room.getCapacity() + ";" );
		return isARoom;
	}

	/**
	 * Function to check if a teacher room reserve exists in datebase
	 * @param student
	 * @param date
	 * @param hour
	 * @return boolean - Existence of a teacher room reserve
	 * @throws SQLException
	 */
	private boolean checkTeacherRoomReserveExistence( Room room, String date,
			String hour ) throws SQLException {
		boolean isAReserve = super.inDBGeneric( "SELECT * FROM reservation_room_teacher WHERE "
				+ "date = \"" + date + "\" and " + "hour = \"" + hour
				+ "\" and " + "id_room = (SELECT id_room FROM room WHERE "
				+ "room.code = \"" + room.getCode() + "\" and "
				+ "room.description = \"" + room.getDescription() + "\" and "
				+ "room.capacity = " + room.getCapacity() + " );" );
		return isAReserve;
	}

	/**
	 * Function to if a teacher room reserve exists in datebase
	 * @param roomReserve
	 * @return boolean - Existence of a teacher room reserve
	 * @throws SQLException
	 */
	private boolean checkExistingTeacherRoomReserve(
			TeacherRoomReserve roomReserve ) throws SQLException {
		boolean isAReserve = super.inDBGeneric( "SELECT * FROM reservation_room_teacher WHERE "
				+ "id_teacher = (SELECT id_teacher FROM teacher WHERE "
				+ "teacher.name = \""
				+ roomReserve.getTeacher().getName()
				+ "\" and "
				+ "teacher.cpf = \""
				+ roomReserve.getTeacher().getCpf()
				+ "\" and "
				+ "teacher.fone = \""
				+ roomReserve.getTeacher().getFone()
				+ "\" and "
				+ "teacher.email = \""
				+ roomReserve.getTeacher().getEmail()
				+ "\" and "
				+ "teacher.registration = \""
				+ roomReserve.getTeacher().getRegistration()
				+ "\") and "
				+ "id_room = (SELECT id_room FROM room WHERE "
				+ "room.code = \""
				+ roomReserve.getRoom().getCode()
				+ "\" and "
				+ "room.description = \""
				+ roomReserve.getRoom().getDescription()
				+ "\" and "
				+ "room.capacity = "
				+ roomReserve.getRoom().getCapacity()
				+ " ) and "
				+ "finality = \""
				+ roomReserve.getFinality()
				+ "\" and "
				+ "hour = \""
				+ roomReserve.getHour()
				+ "\" and "
				+ "date = \"" + roomReserve.getDate() + "\";" );
		
		return isAReserve;
	}

	/**
	 * Function to check if a student room reserve exists in datebase
	 * @param date
	 * @param hour
	 * @return boolean - Existence of a Student Room Reserve
	 * @throws SQLException
	 */
	private boolean checkExistingStudentRoomReserve( String date, String hour )
			throws SQLException {
		boolean isAReserve = super.inDBGeneric( "SELECT * FROM reservation_room_student WHERE "
				+ "date = \"" + date + "\" and " + "hour = \"" + hour + "\";" );
		return isAReserve;
	}

	/**
	 * Function to get the current date
	 * @return String - Current date
	 */
	private String getCurrentDate() {
		Date date = new Date( System.currentTimeMillis() );
		SimpleDateFormat formatedDate = new SimpleDateFormat( "dd/MM/yyyy" );
		String currentDate = formatedDate.format( date );
		return currentDate;
	}

	/**
	 * Function to get the current hour
	 * @return String - Current hour
	 */
	private String getCurrentHour() {
		Date date = new Date( System.currentTimeMillis() );
		String currentHour = date.toString().substring( 11, 16 );
		return currentHour;
	}

	
	/**
	 * Function to validate if a given date already passed
	 * @param givenDate
	 * @return boolean - Is true if the current date didn't passes
	 */
	private boolean checkPassedDate( String givenDate ) {
		String currentDate[] = this.getCurrentDate().split( "[./-]" );
		String dateToCheck[] = givenDate.split( "[./-]" );

		int dif = currentDate[ 2 ].length() - dateToCheck[ 2 ].length();
		dateToCheck[ 2 ] = currentDate[ 2 ].substring( 0, dif )
				+ dateToCheck[ 2 ];

		if ( Integer.parseInt( currentDate[ 2 ] ) > Integer
				.parseInt( dateToCheck[ 2 ] ) ) {
			return true;
		}else{
        	//do nothing
        }

		dif = currentDate[ 1 ].length() - dateToCheck[ 1 ].length();
		dateToCheck[ 1 ] = currentDate[ 1 ].substring( 0, dif )
				+ dateToCheck[ 1 ];

		if ( Integer.parseInt( currentDate[ 1 ] ) > Integer
				.parseInt( dateToCheck[ 1 ] ) ) {
			return true;
		} else if ( Integer.parseInt( currentDate[ 1 ] ) == Integer
				.parseInt( dateToCheck[ 1 ] ) ) {
			dif = currentDate[ 0 ].length() - dateToCheck[ 0 ].length();
			dateToCheck[ 0 ] = currentDate[ 0 ].substring( 0, dif )
					+ dateToCheck[ 0 ];

			if ( Integer.parseInt( currentDate[ 0 ] ) > Integer
					.parseInt( dateToCheck[ 0 ] ) ) {
				return true;
			}
		}else{
        	//do nothing
        }
		
		return false;
	}

	
	/**
	 * Function to check if the given date is equal to current date
	 * @param givenDate
	 * @return boolean 
	 */
	public boolean checkDateWithCurrent( String givenDate ) {
		givenDate = this.standardizeDate( givenDate );
		String currentDate[] = this.getCurrentDate().split( "[./-]" );
		String dateToCheck[] = givenDate.split( "[./-]" );

		if ( currentDate[ 0 ].equals( dateToCheck[ 0 ] ) && currentDate[ 1 ].equals( dateToCheck[ 1 ] )
				&& currentDate[ 2 ].equals( dateToCheck[ 2 ] ) ) {
			return true;
		}else{
        	return false;
		}
	}

	
	

	/**
	 * Function to check if the given hour has already passed
	 * @param givenHour
	 * @return boolean - True if the given hour has passed
	 */
	private boolean checkHourHasPassed( String givenHour ) {
		
		String currentHour = this.getCurrentHour();
		
		boolean hasLengthFour = givenHour.length() == 4;
		
		if(hasLengthFour == true){
			givenHour = "0" + givenHour;
		}
		
		int currentHourFirstAlgarisms = Integer.parseInt( currentHour.substring( 0, 2 ));
		int givenHourFirstAlgarisms = Integer.parseInt( givenHour.substring( 0, 2 ));
		
		boolean isPassedHour = currentHourFirstAlgarisms >  givenHourFirstAlgarisms ;
		boolean isTheSameHour = currentHourFirstAlgarisms == givenHourFirstAlgarisms ;
		boolean isTheSameSeconds =  Integer.parseInt( currentHour.substring( 3, 5 ) ) > Integer
													.parseInt( givenHour.substring( 3, 5 ) );
		
		
		
		if ( isPassedHour == true ) {
			return true;
		} else if ( isTheSameHour == true) {
			if ( isTheSameSeconds == true ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Function to standardize date pattern
	 * @param givenDate
	 * @return String - Date in a standard
	 */
	private String standardizeDate( String givenDate ) {
		String currentDate[] = getCurrentDate().split( "[./-]" );
		String dateSplits[] = givenDate.split( "[./-]" );
		String standardDatePattern = "";

		for ( int i = 0; i < 3; i++ ) {
			if ( i == 0 ) {
				standardDatePattern += currentDate[ i ].substring( 0,
						currentDate[ i ].length() - dateSplits[ i ].length() )
						+ dateSplits[ i ];
			} else {
				standardDatePattern += "/"
						+ currentDate[ i ].substring(
								0,
								currentDate[ i ].length()
										- dateSplits[ i ].length() )
						+ dateSplits[ i ];
			}

		}

		return standardDatePattern;
	}

	/*
	 * private String padronizarhour(String hour){ if(hour.length() == 4) return
	 * "0" + hour; return hour; }
	 */
}