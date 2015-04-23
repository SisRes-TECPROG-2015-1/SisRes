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
	 * for future analyse.
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
	 * Function to get the student id from database
	 * 
	 * @param student
	 * @return
	 */
	private String select_student_id( Student student ) {
		return "SELECT id_aluno FROM aluno WHERE " + "aluno.nome = \""
				+ student.getName() + "\" and " + "aluno.cpf = \""
				+ student.getCpf() + "\" and " + "aluno.telefone = \""
				+ student.getFone() + "\" and " + "aluno.email = \""
				+ student.getEmail() + "\" and " + "aluno.matricula = \""
				+ student.getRegistration() + "\"";
	}

	/**
	 * Function to get the room id in the databse
	 * 
	 * @param room
	 * @return
	 */
	private String select_room_id( Room room ) {
		return "SELECT id_sala FROM sala WHERE " + "sala.codigo = \""
				+ room.getCode() + "\" and " + "sala.descricao = \""
				+ room.getDescription() + "\" and " + "sala.capacidade = "
				+ room.getCapacity();
	}

	/**
	 * Function to construct an where clause for a calling sql query
	 * 
	 * @param reservedRoom
	 * @return
	 */
	private String student_room_reserve_where_clausule_construct(
			StudentRoomReserve reservedRoom ) {
		return " WHERE " + "id_aluno = ( "
				+ select_student_id( reservedRoom.getStudent() ) + " ) and "
				+ "id_sala = ( " + select_room_id( reservedRoom.getRoom() )
				+ " ) and " + "finalidade = \"" + reservedRoom.getFinality()
				+ "\" and " + "hora = \"" + reservedRoom.getHour() + "\" and "
				+ "data = \"" + reservedRoom.getDate() + "\" and "
				+ "cadeiras_reservadas = " + reservedRoom.getReservedChairs();
	}

	/**
	 * Function to construct the values cases for an 'insert'sql query
	 * 
	 * @param reservingRoom
	 * @return
	 */
	private String student_room_reserve_insert_values_construct(
			StudentRoomReserve reservingRoom ) {
		return "( " + select_student_id( reservingRoom.getStudent() ) + " ), "
				+ "( " + select_room_id( reservingRoom.getRoom() ) + " ), "
				+ "\"" + reservingRoom.getFinality() + "\", " + "\""
				+ reservingRoom.getHour() + "\", " + "\""
				+ reservingRoom.getDate() + "\", "
				+ reservingRoom.getReservedChairs();
	}

	/**
	 * Function to construct the attributes of an update sql value cases
	 * 
	 * @param updateReservedRoom
	 * @return
	 */
	private String student_room_reserve_update_values_construct(
			StudentRoomReserve updateReservedRoom ) {
		return "id_aluno = ( "
				+ select_student_id( updateReservedRoom.getStudent() ) + " ), "
				+ "id_sala = ( "
				+ select_room_id( updateReservedRoom.getRoom() ) + " ), "
				+ "finalidade = \"" + updateReservedRoom.getFinality() + "\", "
				+ "hora = \"" + updateReservedRoom.getHour() + "\", "
				+ "data = \"" + updateReservedRoom.getDate() + "\", "
				+ "cadeiras_reservadas = "
				+ updateReservedRoom.getReservedChairs();
	}

	/**
	 * Function to construct an insert sql for a new StudentRoomReserve
	 * 
	 * @param newRoomReserve
	 * @return
	 */
	private String insert_into( StudentRoomReserve newRoomReserve ) {
		return "INSERT INTO "
				+ "reserva_sala_aluno (id_aluno, id_sala, finalidade, hora, data, cadeiras_reservadas) "
				+ "VALUES ( "
				+ student_room_reserve_insert_values_construct( newRoomReserve )
				+ " );";
	}

	/**
	 * Function to construct an update sql to update a StudentRoomReserve
	 * 
	 * @param updatingRoomReserve
	 * @param newAttributesRoomToSave
	 * @return
	 */
	private String update( StudentRoomReserve updatingRoomReserve,
			StudentRoomReserve newAttributesRoomToSave ) {
		return "UPDATE reserva_sala_aluno SET "
				+ this.student_room_reserve_update_values_construct( newAttributesRoomToSave )
				+ this.student_room_reserve_where_clausule_construct( updatingRoomReserve )
				+ " ;";
	}

	/**
	 * Function to construct a delete sql for a StudentRoomReserve
	 * 
	 * @param reservedRommToDelete
	 * @return
	 */
	private String delete_from( StudentRoomReserve reservedRommToDelete ) {
		return "DELETE FROM reserva_sala_aluno "
				+ this.student_room_reserve_where_clausule_construct( reservedRommToDelete )
				+ " ;";
	}

	/**
	 * Method to save a new room reserve for a student
	 * 
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
		} else if ( !this.checkExistingRoom( roomToReserve.getRoom() ) ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( this.checkExistingTeacherRoomReserve( roomToReserve.getRoom(),
				roomToReserve.getDate(), roomToReserve.getHour() ) ) {
			throw new ReserveException( UNAVAILABLE_ROOM );
		} else if ( this.checkExistingStudentRoomReserve( roomToReserve.getStudent(),
				roomToReserve.getDate(), roomToReserve.getHour() ) ) {
			throw new ReserveException( UNAVAILABLE_STUDENT );
		} else if ( !this.checkIfExistsAvailableChairs(
				roomToReserve.getReservedChairs(), roomToReserve.getRoom(),
				roomToReserve.getDate(), roomToReserve.getHour() ) ) {
			throw new ReserveException( UNAVAILABLE_CHAIRS );
		}
		if ( this.checkPassedDate( roomToReserve.getDate() ) ) {
			throw new ReserveException( DATE_TIME_PASSED );
		}
		if ( this.checkDateWithCurrent( roomToReserve.getDate() ) ) {
			if ( this.checkHourHasPassed( roomToReserve.getHour() ) ) {
				throw new ReserveException( HOUR_PASSED );
			} else {
				super.executeQuery( this.insert_into( roomToReserve ) );
			}
		} else {
			super.executeQuery( this.insert_into( roomToReserve ) );
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
		} else if ( !this.checkIfaStudentRoomReserveExists( alreadyReservedRoom ) ) {
			throw new ReserveException( ABSENT_RESERV );
		} else if ( this.checkIfaStudentRoomReserveExists( newReserveRoomData ) ) {
			throw new ReserveException( EXISTING_RESERV );
		} else if ( !this.checkExistingStudent( newReserveRoomData.getStudent() ) ) {
			throw new ReserveException( ABSENT_STUDENT );
		} else if ( !this.checkExistingRoom( newReserveRoomData.getRoom() ) ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( !alreadyReservedRoom.getDate().equals(
				newReserveRoomData.getDate() )
				|| !alreadyReservedRoom.getHour().equals(
						newReserveRoomData.getHour() ) ) {
			if ( this.checkExistingStudentRoomReserve( newReserveRoomData.getStudent(),
					newReserveRoomData.getDate(), newReserveRoomData.getHour() ) ) {
				throw new ReserveException( UNAVAILABLE_STUDENT );
			} else if ( this.checkExistingTeacherRoomReserve(
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
		}
		if ( this.checkPassedDate( newReserveRoomData.getDate() ) ) {
			throw new ReserveException( DATE_TIME_PASSED );
		}
		if ( this.checkHourHasPassed( newReserveRoomData.getHour() )
				&& this.checkDateWithCurrent( newReserveRoomData.getDate() ) ) {
			throw new ReserveException( HOUR_PASSED );
		} else {
			super.updateQuery( this.update( alreadyReservedRoom,
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
		} else if ( !this.checkIfaStudentRoomReserveExists( reservedRoomToDelete ) ) {
			throw new ReserveException( ABSENT_RESERV );
		} else {
			super.executeQuery( this.delete_from( reservedRoomToDelete ) );
		}
	}

	/**
	 * Function to get all student reserved rooms from database
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 * @throws ClientException 
	 */
	public Vector<StudentRoomReserve> getAllStudentReservedRooms()
			throws SQLException, ClientException, PatrimonyException,
			ReserveException, ClientException {
		return super
				.search( "SELECT * FROM reserva_sala_aluno "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_aluno.id_sala "
						+ "INNER JOIN aluno ON aluno.id_aluno = reserva_sala_aluno.id_aluno;" );
	}

	/**
	 * Function to get from the database the reserved rooms for students in a
	 * given day
	 * 
	 * @param date
	 * @return
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
		return super
				.search( "SELECT * FROM reserva_sala_aluno "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_aluno.id_sala "
						+ "INNER JOIN aluno ON aluno.id_aluno = reserva_sala_aluno.id_aluno "
						+ "WHERE data = \"" + date + "\";" );
	}

	/**
	 * Function to get from the database the reserved rooms for students in a
	 * given hour
	 * 
	 * @param hour
	 * @return
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
		return super
				.search( "SELECT * FROM reserva_sala_aluno "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_aluno.id_sala "
						+ "INNER JOIN aluno ON aluno.id_aluno = reserva_sala_aluno.id_aluno "
						+ " WHERE hora = \"" + hour + "\";" );
	}

	/**
	 * Function to get the number of available chairs in a room
	 * 
	 * @param room
	 * @param date
	 * @param hour
	 * @return
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
		Iterator<StudentRoomReserve> it = vet.iterator();
		int total = Integer.parseInt( room.getCapacity() );
		while ( it.hasNext() ) {
			StudentRoomReserve r = it.next();
			if ( r.getRoom().equals( room ) && r.getDate().equals( date )
					&& r.getHour().equals( hour ) ) {
				total -= Integer.parseInt( r.getReservedChairs() );
			}
		}
		return total;
	}

	/**
	 * Method to check if any available chairs exist
	 * 
	 * @param reservedChairs
	 * @param room
	 * @param date
	 * @param hour
	 * @return
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
		if ( this.getAvailableChairs( room, date, hour ) >= Integer
				.parseInt( reservedChairs ) ) {
			return true;
		}
		return false;
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
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingStudent( Student student ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM aluno WHERE "
				+ "aluno.nome = \"" + student.getName() + "\" and "
				+ "aluno.cpf = \"" + student.getCpf() + "\" and "
				+ "aluno.telefone = \"" + student.getFone() + "\" and "
				+ "aluno.email = \"" + student.getEmail() + "\" and "
				+ "aluno.matricula = \"" + student.getRegistration() + "\";" );
	}

	/**
	 * Function to check if a room exists in database
	 * 
	 * @param room
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingRoom( Room room ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + room.getCode() + "\" and "
				+ "sala.descricao = \"" + room.getDescription() + "\" and "
				+ "sala.capacidade = " + room.getCapacity() + ";" );
	}

	/**
	 * Function to check if a student room reserve exists in database
	 * 
	 * @param student
	 * @param date
	 * @param hour
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingStudentRoomReserve( Student student, String date,
			String hour ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
				+ "data = \"" + date + "\" and " + "hora = \"" + hour
				+ "\" and " + "id_aluno = (SELECT id_aluno FROM aluno WHERE "
				+ "aluno.nome = \"" + student.getName() + "\" and "
				+ "aluno.cpf = \"" + student.getCpf() + "\" and "
				+ "aluno.telefone = \"" + student.getFone() + "\" and "
				+ "aluno.email = \"" + student.getEmail() + "\" and "
				+ "aluno.matricula = \"" + student.getRegistration() + "\");" );
	}
	
	/**
	 * Function to if a teacher room reserve exists in database
	 * 
	 * @param room
	 * @param date
	 * @param hour
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingTeacherRoomReserve( Room room, String date,
			String hour ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "data = \"" + this.standardizeDate( date ) + "\" and "
				+ "hora = \"" + this.standardizeHour( hour ) + "\" and "
				+ "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \"" + room.getCode() + "\" and "
				+ "sala.descricao = \"" + room.getDescription() + "\" and "
				+ "sala.capacidade = " + room.getCapacity() + " );" );
	}

	/**
	 * Function to check if a student room reserve exists in database
	 * @param reservedRoom
	 * @return
	 * @throws SQLException
	 */
	private boolean checkIfaStudentRoomReserveExists( StudentRoomReserve reservedRoom ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
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
	}

	/**
	 * Function to get the current date
	 * @return
	 */
	private String getCurrentDate() {
		Date date = new Date( System.currentTimeMillis() );
		SimpleDateFormat formatador = new SimpleDateFormat( "dd/MM/yyyy" );
		return formatador.format( date );
	}

	/**
	 * Function to get the current hour
	 * @return
	 */
	private String getCurrentHour() {
		Date date = new Date( System.currentTimeMillis() );
		return date.toString().substring( 11, 16 );
	}

	/**
	 * Function to validate if a given date already passed
	 * @param givenDate
	 * @return
	 */
	private boolean checkPassedDate( String givenDate ) {
		String currentDate[] = this.getCurrentDate().split( "[./-]" );
		String dateToCheck[] = givenDate.split( "[./-]" );

		int dif = currentDate[ 2 ].length() - dateToCheck[ 2 ].length();
		dateToCheck[ 2 ] = currentDate[ 2 ].substring( 0, dif ) + dateToCheck[ 2 ];

		if ( Integer.parseInt( currentDate[ 2 ] ) > Integer.parseInt( dateToCheck[ 2 ] ) ) {
			return true;
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
			}
		}
		return false;
	}

	/**
	 * Function to check if the given date is equal to current date
	 * @param givenDate
	 * @return
	 */
	public boolean checkDateWithCurrent( String givenDate ) {
		givenDate = this.standardizeDate( givenDate );
		String currentDate[] = this.getCurrentDate().split( "[./-]" );
		String dateToCheck[] = givenDate.split( "[./-]" );

		if ( currentDate[ 0 ].equals( dateToCheck[ 0 ] ) && currentDate[ 1 ].equals( dateToCheck[ 1 ] )
				&& currentDate[ 2 ].equals( dateToCheck[ 2 ] ) ) {
			return true;
		}
		return false;
	}

	/**
	 * Function to check if the given hour has already passed
	 * @param givenHour
	 * @return
	 */
	private boolean checkHourHasPassed( String givenHour ) {
		String currentHour = this.getCurrentHour();
		if ( givenHour.length() == 4 ) {
			givenHour = "0" + givenHour;
		}
		if ( Integer.parseInt( currentHour.substring( 0, 2 ) ) > Integer
				.parseInt( givenHour.substring( 0, 2 ) ) ) {
			return true;
		} else if ( Integer.parseInt( currentHour.substring( 0, 2 ) ) == Integer
				.parseInt( givenHour.substring( 0, 2 ) ) ) {
			if ( Integer.parseInt( currentHour.substring( 3, 5 ) ) > Integer
					.parseInt( givenHour.substring( 3, 5 ) ) ) {
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
	 * @return
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
	 * @return
	 */
	private String standardizeHour( String givenHour ) {
		if ( givenHour.length() == 4 ) {
			return "0" + givenHour;
		}
		return givenHour;
	}

}
