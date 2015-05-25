package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;
import model.Student;
import model.StudentRoomReserve;
import model.Room;

@SuppressWarnings("unchecked")
public class StudentRoomReserveDAO extends DAO {

	/**
	 * Defining constants for error messages and alerts. It will stay like that
	 * for future analyze.
	 */
	private final String NULL_TERM = "Termo nulo.";
	private final String UNAVAILABLE_STUDENT = "O aluno possui uma reserva no mesmo dia e horario.";
	private final String UNAVAILABLE_ROOM = "A Sala esta reservada no mesmo dia e horario.";
	private final String ABSENT_STUDENT = "Aluno inexistente.";
	private final String ABSENT_ROOM = "Sala inexistente";
	private final String ABSENT_RESERV = "Reserva inexistente";
	private final String EXISTING_RESERV = "A reserva ja existe.";
	private final String UNAVAILABLE_CHAIRS = "O numero de cadeiras reservadas esta indisponivel para esta sala.";
	private final String DATE_TIME_PASSED = "A data escolhida ja passou.";
	private final String HOUR_PASSED = "A hora escolhida ja passou.";

	// Singleton
	private static StudentRoomReserveDAO instance;

	/**
	 * NULL constructor for StudentRoomReserveDAO
	 */
	private StudentRoomReserveDAO() {
	}

	/**
	 * getInstance method for external instances of StudentRoomReserveDAO
	 * 
	 * @return
	 */
	public static StudentRoomReserveDAO getInstance() {
		if ( instance == null ) {
			instance = new StudentRoomReserveDAO();
		}
		return instance;
	}

	/**
	 * Function to get the string containing a selsct clause 
	 * @param student
	 * @return String - Select Clause
	 */
	private String createStudentSelectClause( Student student ) {
		String selectClause = "SELECT id_aluno FROM aluno WHERE " + "aluno.nome = \""
				+ student.getName() + "\" and " + "aluno.cpf = \""
				+ student.getCpf() + "\" and " + "aluno.telefone = \""
				+ student.getFone() + "\" and " + "aluno.email = \""
				+ student.getEmail() + "\" and " + "aluno.matricula = \""
				+ student.getRegistration() + "\"";
		return selectClause;
	}

	/**
	 * Function to give the necessary string containing the
	 * instruction to select a room from the database 
	 * @param room
	 * @return String - The string to select a room in the database
	 */
	private String createSelectFromIdClause( Room room ) {
		String roomSelect = "SELECT id_sala FROM sala WHERE " + "sala.codigo = \""
				+ room.getCode() + "\" and " + "sala.descricao = \""
				+ room.getDescription() + "\" and " + "sala.capacidade = "
				+ room.getCapacity();
		return roomSelect;
	}

	/**
	 * Function to construct a where clause for a calling sql query 
	 * @param reservedRoom
	 * @return String - Where clause
	 */
	private String createWhereClause( StudentRoomReserve reservedRoom ) {
		String whereClause = " WHERE " + "id_aluno = ( "
				+ createStudentSelectClause( reservedRoom.getStudent() ) + " ) and "
				+ "id_sala = ( " + createSelectFromIdClause( reservedRoom.getRoom() )
				+ " ) and " + "finalidade = \"" + reservedRoom.getFinality()
				+ "\" and " + "hora = \"" + reservedRoom.getHour() + "\" and "
				+ "data = \"" + reservedRoom.getDate() + "\" and "
				+ "cadeiras_reservadas = " + reservedRoom.getReservedChairs();
		return whereClause;
	}

	/**
	 * Details the reserve
	 * @param roomReserve
	 * @return String - The details of the reserve
	 */
	private String detailStudentRoomReserve(
			StudentRoomReserve roomReserve ) {
		String details = "( " + createStudentSelectClause( roomReserve.getStudent() ) + " ), "
				+ "( " + createSelectFromIdClause( roomReserve.getRoom() ) + " ), "
				+ "\"" + roomReserve.getFinality() + "\", " + "\""
				+ roomReserve.getHour() + "\", " + "\""
				+ roomReserve.getDate() + "\", "
				+ roomReserve.getReservedChairs();
		return details;
	}

	/**
	 * Updates a reserve in the database
	 * @param roomReserve
	 * @return String - Update Clause
	 */
	private String createUpdateClause(
			StudentRoomReserve roomReserve ) {
		
		String reserve = "id_aluno = ( "
				+ createStudentSelectClause( roomReserve.getStudent() ) + " ), "
				+ "id_sala = ( "
				+ createSelectFromIdClause( roomReserve.getRoom() ) + " ), "
				+ "finalidade = \"" + roomReserve.getFinality() + "\", "
				+ "hora = \"" + roomReserve.getHour() + "\", "
				+ "data = \"" + roomReserve.getDate() + "\", "
				+ "cadeiras_reservadas = "
				+ roomReserve.getReservedChairs();
		return reserve;
	}

	/**
	 * Function to construct an insert sql clause for a new StudentRoomReserve
	 * @param newRoomReserve
	 * @return String - Insert clause
	 */
	private String createInsertClause( StudentRoomReserve newRoomReserve ) {
		String insertClause = "INSERT INTO "
				+ "reserva_sala_aluno (id_aluno, id_sala, finalidade, hora, data, cadeiras_reservadas) "
				+ "VALUES ( "
				+ detailStudentRoomReserve( newRoomReserve )
				+ " );";
		return insertClause;
	}

	/**
	 * Function to construct an update sql clause to update a student room reserve
	 * @param updatingRoomReserve
	 * @param newAttributesRoomToSave
	 * @return String - Update clause
	 */
	private String createUpdateClause( StudentRoomReserve updatingRoomReserve,
			StudentRoomReserve newAttributesRoomToSave ) {
		
		String updateClause = "UPDATE reserva_sala_aluno SET "
				+ this.createUpdateClause( newAttributesRoomToSave )
				+ this.createWhereClause( updatingRoomReserve )
				+ " ;";
		return updateClause;
	}

	/**
	 * Function to construct a delete sql clause for a student room reserve
	 * @param reservedRommToDelete
	 * @return String - Delete clause
	 */
	private String createDeleteClause( StudentRoomReserve reservedRommToDelete ) {
		String deleteClause = "DELETE FROM reserva_sala_aluno "
				+ this.createWhereClause( reservedRommToDelete )
				+ " ;";
		return deleteClause;
	}

	/**
	 * Method to save a new room reserve for a student
	 * @param roomToReserve
	 * @throws ReserveException
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ClientException 
	 * @throws NumberFormatException 
	 */
	public void saveNewStudentRoomReserve( StudentRoomReserve roomToReserve )
			throws ReserveException, SQLException, ClientException,
			PatrimonyException, NumberFormatException, ClientException {
		if ( roomToReserve == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( !this.checkExistingStudent( roomToReserve.getStudent() ) ) {
			throw new ReserveException( ABSENT_STUDENT );
		} else if ( !this.checkRoomExistence( roomToReserve.getRoom() ) ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( this.checkTeacherRoomReserveExistence( roomToReserve.getRoom(),
				roomToReserve.getDate(), roomToReserve.getHour() ) ) {
			throw new ReserveException( UNAVAILABLE_ROOM );
		} else if ( this.checkStudentRoomReserveExistence( roomToReserve.getStudent(),
				roomToReserve.getDate(), roomToReserve.getHour() ) ) {
			throw new ReserveException( UNAVAILABLE_STUDENT );
		} else if ( !this.checkIfExistsAvailableChairs(
				roomToReserve.getReservedChairs(), roomToReserve.getRoom(),
				roomToReserve.getDate(), roomToReserve.getHour() ) ) {
			throw new ReserveException( UNAVAILABLE_CHAIRS );
		}else{
        	//do nothing
        }
		
		if ( this.checkPassedDate( roomToReserve.getDate() ) ) {
			throw new ReserveException( DATE_TIME_PASSED );
		}else{
        	//do nothing
        }
		
		if ( this.checkDateWithCurrent( roomToReserve.getDate() ) ) {
			if ( this.checkHourHasPassed( roomToReserve.getHour() ) ) {
				throw new ReserveException( HOUR_PASSED );
			} else {
				super.executeQuery( this.createInsertClause( roomToReserve ) );
			}
		} else {
			super.executeQuery( this.createInsertClause( roomToReserve ) );
		}
	}

	/**
	 * Method to update a reserve made by a student
	 * 
	 * @param alreadyReservedRoom
	 * @param newReserveRoomData
	 * @throws ReserveException
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ClientException 
	 * @throws NumberFormatException 
	 */
	public void updateStudentRoomReserve(
			StudentRoomReserve alreadyReservedRoom,
			StudentRoomReserve newReserveRoomData ) throws ReserveException,
			SQLException, ClientException, PatrimonyException, NumberFormatException, ClientException {
		if ( alreadyReservedRoom == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( newReserveRoomData == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( !this.checkIfaStudentRoomReserveExistsById( alreadyReservedRoom ) ) {
			throw new ReserveException( ABSENT_RESERV );
		} else if ( this.checkIfaStudentRoomReserveExistsById( newReserveRoomData ) ) {
			throw new ReserveException( EXISTING_RESERV );
		} else if ( !this.checkExistingStudent( newReserveRoomData.getStudent() ) ) {
			throw new ReserveException( ABSENT_STUDENT );
		} else if ( !this.checkRoomExistence( newReserveRoomData.getRoom() ) ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( !alreadyReservedRoom.getDate().equals(
				newReserveRoomData.getDate() )
				|| !alreadyReservedRoom.getHour().equals(
						newReserveRoomData.getHour() ) ) {
			if ( this.checkStudentRoomReserveExistence( newReserveRoomData.getStudent(),
					newReserveRoomData.getDate(), newReserveRoomData.getHour() ) ) {
				throw new ReserveException( UNAVAILABLE_STUDENT );
			} else if ( this.checkTeacherRoomReserveExistence(
					newReserveRoomData.getRoom(), newReserveRoomData.getDate(),
					newReserveRoomData.getHour() ) ) {
				throw new ReserveException( UNAVAILABLE_ROOM );
			}
		}
		if ( !this.checkIfExistsAvailableChairs(
				""
						+ ( Integer.parseInt( newReserveRoomData
								.getReservedChairs() ) - Integer
								.parseInt( alreadyReservedRoom
										.getReservedChairs() ) ),
				newReserveRoomData.getRoom(), newReserveRoomData.getDate(),
				newReserveRoomData.getHour() ) ) {
			throw new ReserveException( UNAVAILABLE_CHAIRS );
		}else{
        	//do nothing
        }
		
		if ( this.checkPassedDate( newReserveRoomData.getDate() ) ) {
			throw new ReserveException( DATE_TIME_PASSED );
		}else{
        	//do nothing
        }
		
		if ( this.checkHourHasPassed( newReserveRoomData.getHour() )
				&& this.checkDateWithCurrent( newReserveRoomData.getDate() ) ) {
			throw new ReserveException( HOUR_PASSED );
		} else {
			super.updateQuery( this.createUpdateClause( alreadyReservedRoom,
					newReserveRoomData ) );
		}

	}

	/**
	 * Method to delete some reserved room for a student
	 * 
	 * @param reservedRoomToDelete
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void deleteStudentReservedRoom(
			StudentRoomReserve reservedRoomToDelete ) throws ReserveException,
			SQLException {
		if ( reservedRoomToDelete == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( !this.checkIfaStudentRoomReserveExistsById( reservedRoomToDelete ) ) {
			throw new ReserveException( ABSENT_RESERV );
		} else {
			super.executeQuery( this.createDeleteClause( reservedRoomToDelete ) );
		}
	}

	/**
	 * Function to get all student reserved rooms from database
	 * 
	 * @return Vector<StudentRoomReserve> - Reserves
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 * @throws ClientException 
	 */
	public Vector<StudentRoomReserve> getAllStudentReservedRooms()
			throws SQLException, ClientException, PatrimonyException,
			ReserveException, ClientException {
		 
		Vector<StudentRoomReserve> vectorOfReserves =
		 				super.search( "SELECT * FROM reserva_sala_aluno "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_aluno.id_sala "
						+ "INNER JOIN aluno ON aluno.id_aluno = reserva_sala_aluno.id_aluno;" );
		return vectorOfReserves;
	}

	/**
	 * Function to get from the database the reserved rooms for students in a given day
	 * @param date
	 * @return Vector<StudentRoomReserve> = A vector of reserves made by students
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 * @throws ClientException 
	 */
	public Vector<StudentRoomReserve> getStudentReservedRoomsByDay( String date )
			throws SQLException, ClientException, PatrimonyException,
			ReserveException, ClientException {
		
		date = this.standardizeDate( date );
		Vector<StudentRoomReserve> vectorOfReserves =
				super.search( "SELECT * FROM reserva_sala_aluno "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_aluno.id_sala "
						+ "INNER JOIN aluno ON aluno.id_aluno = reserva_sala_aluno.id_aluno "
						+ "WHERE data = \"" + date + "\";" );
		return vectorOfReserves;
	}

	/**
	 * Function to get from the database the reserved rooms for students in a given hour
	 * @param hour
	 * @return Vector<StudentRoomReserve> - A vector of reserves made by students
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 * @throws ClientException 
	 */
	public Vector<StudentRoomReserve> getStudentReservedRoomsByHour( String hour )
			throws SQLException, ClientException, PatrimonyException,
			ReserveException, ClientException {
		
		hour = this.standardizeHour( hour );
		Vector<StudentRoomReserve> vectorOfreserves = 
				super.search( "SELECT * FROM reserva_sala_aluno "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_aluno.id_sala "
						+ "INNER JOIN aluno ON aluno.id_aluno = reserva_sala_aluno.id_aluno "
						+ " WHERE hora = \"" + hour + "\";" );
		return vectorOfreserves;
	}

	/**
	 * Function to get the number of available chairs in a room
	 * @param room
	 * @param date
	 * @param hour
	 * @return int - Number of available chairs
	 * @throws SQLException
	 * @throws PatrimonyException
	 * @throws ClienteException
	 * @throws ReserveException
	 * @throws ClientException 
	 */
	public int getAvailableChairs( Room room, String date, String hour )
			throws SQLException, PatrimonyException, ClientException,
			ReserveException, ClientException {
		date = this.standardizeDate( date );
		hour = this.standardizeHour( hour );
		Vector<StudentRoomReserve> vet = this.getAllStudentReservedRooms();
		Iterator<StudentRoomReserve> iterator = vet.iterator();
		int total = Integer.parseInt( room.getCapacity() );
		while ( iterator.hasNext() ) {
			StudentRoomReserve r = iterator.next();
			if ( r.getRoom().equals( room ) && r.getDate().equals( date )
					&& r.getHour().equals( hour ) ) {
				total -= Integer.parseInt( r.getReservedChairs() );
			}
		}
		return total;
	}

	/**
	 * Method to check if any available chairs exists
	 * @param reservedChairs
	 * @param room
	 * @param date
	 * @param hour
	 * @return boolean - Existence of available chairs
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 * @throws ClientException 
	 * @throws NumberFormatException 
	 */
	private boolean checkIfExistsAvailableChairs( String reservedChairs,
			Room room, String date, String hour ) throws SQLException,
			ClientException, PatrimonyException, ReserveException, NumberFormatException, ClientException {
		
		int avaiableChairsNumber = this.getAvailableChairs( room, date, hour );
		int reservedChairsNumber = Integer.parseInt( reservedChairs );
		boolean isAvaiableChairs = avaiableChairsNumber >= reservedChairsNumber;
		return isAvaiableChairs;
	}

	@Override
	protected Object fetch( ResultSet resulSet ) throws SQLException,
			ClientException, PatrimonyException, ReserveException, ClientException {
		Student student = new Student( resulSet.getString( "nome" ),
				resulSet.getString( "cpf" ), resulSet.getString( "matricula" ),
				resulSet.getString( "telefone" ), resulSet.getString( "email" ) );

		Room room = new Room( resulSet.getString( "codigo" ),
				resulSet.getString( "descricao" ),
				resulSet.getString( "capacidade" ) );

		StudentRoomReserve reservedRoom = new StudentRoomReserve(
				resulSet.getString( "data" ), resulSet.getString( "hora" ),
				room, resulSet.getString( "finalidade" ),
				resulSet.getString( "cadeiras_reservadas" ), student );

		return reservedRoom;
	}

	/**
	 * Function to check if a student exists in database
	 * 
	 * @param student
	 * @return Boolean - Existence of a student
	 * @throws SQLException
	 */
	private boolean checkExistingStudent( Student student ) throws SQLException {
		boolean isAStudent = super.inDBGeneric( "SELECT * FROM aluno WHERE "
				+ "aluno.nome = \"" + student.getName() + "\" and "
				+ "aluno.cpf = \"" + student.getCpf() + "\" and "
				+ "aluno.telefone = \"" + student.getFone() + "\" and "
				+ "aluno.email = \"" + student.getEmail() + "\" and "
				+ "aluno.matricula = \"" + student.getRegistration() + "\";" );
		return isAStudent;
	}

	/**
	 * Function to check if a room exists in database
	 * @param room
	 * @return
	 * @throws SQLException
	 */
	private boolean checkRoomExistence( Room room ) throws SQLException {
		boolean isARoom = super.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + room.getCode() + "\" and "
				+ "sala.descricao = \"" + room.getDescription() + "\" and "
				+ "sala.capacidade = " + room.getCapacity() + ";" );
		return isARoom;
	}

	/**
	 * Function to check if a student room reserve exists in database
	 * @param student
	 * @param date
	 * @param hour
	 * @return boolean - Existence of a Student Room Reserve
	 * @throws SQLException
	 */
	private boolean checkStudentRoomReserveExistence( Student student, String date,
			String hour ) throws SQLException {
		
		boolean isARoomReserve = super.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
				+ "data = \"" + date + "\" and " + "hora = \"" + hour
				+ "\" and " + "id_aluno = (SELECT id_aluno FROM aluno WHERE "
				+ "aluno.nome = \"" + student.getName() + "\" and "
				+ "aluno.cpf = \"" + student.getCpf() + "\" and "
				+ "aluno.telefone = \"" + student.getFone() + "\" and "
				+ "aluno.email = \"" + student.getEmail() + "\" and "
				+ "aluno.matricula = \"" + student.getRegistration() + "\");" );
		
		return isARoomReserve;
	}
	
	/**
	 * Function to if a teacher room reserve exists in database
	 * @param room
	 * @param date
	 * @param hour
	 * @return boolean - Existence of a teacher room reserve
	 * @throws SQLException
	 */
	private boolean checkTeacherRoomReserveExistence( Room room, String date,
			String hour ) throws SQLException {
		
		boolean isATeacherRoomReserve = super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "data = \"" + this.standardizeDate( date ) + "\" and "
				+ "hora = \"" + this.standardizeHour( hour ) + "\" and "
				+ "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \"" + room.getCode() + "\" and "
				+ "sala.descricao = \"" + room.getDescription() + "\" and "
				+ "sala.capacidade = " + room.getCapacity() + " );" );
		
		return isATeacherRoomReserve;
	}

	/**
	 * Function to check if a student room reserve exists in database
	 * @param reservedRoom
	 * @return boolean - Existence of a student room 
	 * @throws SQLException
	 */
	private boolean checkIfaStudentRoomReserveExistsById( StudentRoomReserve reservedRoom ) throws SQLException {
		boolean isAStudentRoomReserve = super.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
				+ "id_aluno = (SELECT id_aluno FROM aluno WHERE "
				+ "aluno.nome = \""
				+ reservedRoom.getStudent().getName()
				+ "\" and "
				+ "aluno.cpf = \""
				+ reservedRoom.getStudent().getCpf()
				+ "\" and "
				+ "aluno.telefone = \""
				+ reservedRoom.getStudent().getFone()
				+ "\" and "
				+ "aluno.email = \""
				+ reservedRoom.getStudent().getEmail()
				+ "\" and "
				+ "aluno.matricula = \""
				+ reservedRoom.getStudent().getRegistration()
				+ "\") and "
				+ "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \""
				+ reservedRoom.getRoom().getCode()
				+ "\" and "
				+ "sala.descricao = \""
				+ reservedRoom.getRoom().getDescription()
				+ "\" and "
				+ "sala.capacidade = "
				+ reservedRoom.getRoom().getCapacity()
				+ " ) and "
				+ "finalidade = \""
				+ reservedRoom.getFinality()
				+ "\" and "
				+ "hora = \""
				+ reservedRoom.getHour()
				+ "\" and "
				+ "data = \""
				+ reservedRoom.getDate()
				+ "\" and "
				+ "cadeiras_reservadas = " + reservedRoom.getReservedChairs() + ";" );
		
		return isAStudentRoomReserve;
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
		dateToCheck[ 2 ] = currentDate[ 2 ].substring( 0, dif ) + dateToCheck[ 2 ];

		if ( Integer.parseInt( currentDate[ 2 ] ) > Integer.parseInt( dateToCheck[ 2 ] ) ) {
			return true;
		}else{
        	//do nothing
        }

		dif = currentDate[ 1 ].length() - dateToCheck[ 1 ].length();
		dateToCheck[ 1 ] = currentDate[ 1 ].substring( 0, dif ) + dateToCheck[ 1 ];

		if ( Integer.parseInt( currentDate[ 1 ] ) > Integer.parseInt( dateToCheck[ 1 ] ) ) {
			return true;
		} else if ( Integer.parseInt( currentDate[ 1 ] ) == Integer
				.parseInt( dateToCheck[ 1 ] ) ) {
			dif = currentDate[ 0 ].length() - dateToCheck[ 0 ].length();
			dateToCheck[ 0 ] = currentDate[ 0 ].substring( 0, dif ) + dateToCheck[ 0 ];

			if ( Integer.parseInt( currentDate[ 0 ] ) > Integer.parseInt( dateToCheck[ 0 ] ) ) {
				return true;
			}else{
            	//do nothing
            }
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
        	//do nothing
        }
		
		return false;
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
		}else{
        	//do nothing
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
				standardDatePattern += currentDate[ i ].substring( 0, currentDate[ i ].length()
						- dateSplits[ i ].length() )
						+ dateSplits[ i ];
			} else {
				standardDatePattern += "/"
						+ currentDate[ i ].substring( 0, currentDate[ i ].length()
								- dateSplits[ i ].length() ) + dateSplits[ i ];
			}

		}

		return standardDatePattern;
	}

	/**
	 * Function to standardize hour pattern
	 * @param givenHour
	 * @return String - Hour in a standard
	 */
	private String standardizeHour( String givenHour ) {
		boolean isStandardized = (givenHour.length() == 4) ;
		if (isStandardized == true){
			return "0" + givenHour;
		}else{
        	//do nothing
        }
		
		return givenHour;
	}

}
