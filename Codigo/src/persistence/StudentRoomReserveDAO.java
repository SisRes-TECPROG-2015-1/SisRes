package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

import model.Aluno;
import model.StudentRoomReserve;
import model.Sala;

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
	private String select_student_id( Aluno student ) {
		return "SELECT id_aluno FROM aluno WHERE " + "aluno.nome = \""
				+ student.getNome() + "\" and " + "aluno.cpf = \""
				+ student.getCpf() + "\" and " + "aluno.telefone = \""
				+ student.getTelefone() + "\" and " + "aluno.email = \""
				+ student.getEmail() + "\" and " + "aluno.matricula = \""
				+ student.getMatricula() + "\"";
	}

	/**
	 * Function to get the room id in the databse
	 * 
	 * @param room
	 * @return
	 */
	private String select_room_id( Sala room ) {
		return "SELECT id_sala FROM sala WHERE " + "sala.codigo = \""
				+ room.getCodigo() + "\" and " + "sala.descricao = \""
				+ room.getDescricao() + "\" and " + "sala.capacidade = "
				+ room.getCapacidade();
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
				+ select_student_id( reservedRoom.getAluno() ) + " ) and "
				+ "id_sala = ( " + select_room_id( reservedRoom.getSala() )
				+ " ) and " + "finalidade = \"" + reservedRoom.getFinality()
				+ "\" and " + "hora = \"" + reservedRoom.getHora() + "\" and "
				+ "data = \"" + reservedRoom.getData() + "\" and "
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
		return "( " + select_student_id( reservingRoom.getAluno() ) + " ), "
				+ "( " + select_room_id( reservingRoom.getSala() ) + " ), "
				+ "\"" + reservingRoom.getFinality() + "\", " + "\""
				+ reservingRoom.getHora() + "\", " + "\""
				+ reservingRoom.getData() + "\", "
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
				+ select_student_id( updateReservedRoom.getAluno() ) + " ), "
				+ "id_sala = ( "
				+ select_room_id( updateReservedRoom.getSala() ) + " ), "
				+ "finalidade = \"" + updateReservedRoom.getFinality() + "\", "
				+ "hora = \"" + updateReservedRoom.getHora() + "\", "
				+ "data = \"" + updateReservedRoom.getData() + "\", "
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
	 */
	public void saveNewStudentRoomReserve( StudentRoomReserve roomToReserve )
			throws ReserveException, SQLException, ClienteException,
			PatrimonyException {
		if ( roomToReserve == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( !this.checkExistingStudent( roomToReserve.getAluno() ) ) {
			throw new ReserveException( ABSENT_STUDENT );
		} else if ( !this.checkExistingRoom( roomToReserve.getSala() ) ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( this.checkExistingTeacherRoomReserve( roomToReserve.getSala(),
				roomToReserve.getData(), roomToReserve.getHora() ) ) {
			throw new ReserveException( UNAVAILABLE_ROOM );
		} else if ( this.checkExistingStudentRoomReserve( roomToReserve.getAluno(),
				roomToReserve.getData(), roomToReserve.getHora() ) ) {
			throw new ReserveException( UNAVAILABLE_STUDENT );
		} else if ( !this.checkIfExistsAvailableChairs(
				roomToReserve.getReservedChairs(), roomToReserve.getSala(),
				roomToReserve.getData(), roomToReserve.getHora() ) ) {
			throw new ReserveException( UNAVAILABLE_CHAIRS );
		}
		if ( this.checkPassedDate( roomToReserve.getData() ) ) {
			throw new ReserveException( DATE_TIME_PASSED );
		}
		if ( this.checkDateWithCurrent( roomToReserve.getData() ) ) {
			if ( this.checkHourHasPassed( roomToReserve.getHora() ) ) {
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
	 */
	public void updateStudentRoomReserve(
			StudentRoomReserve alreadyReservedRoom,
			StudentRoomReserve newReserveRoomData ) throws ReserveException,
			SQLException, ClienteException, PatrimonyException {
		if ( alreadyReservedRoom == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( newReserveRoomData == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( !this.checkIfaStudentRoomReserveExists( alreadyReservedRoom ) ) {
			throw new ReserveException( ABSENT_RESERV );
		} else if ( this.checkIfaStudentRoomReserveExists( newReserveRoomData ) ) {
			throw new ReserveException( EXISTING_RESERV );
		} else if ( !this.checkExistingStudent( newReserveRoomData.getAluno() ) ) {
			throw new ReserveException( ABSENT_STUDENT );
		} else if ( !this.checkExistingRoom( newReserveRoomData.getSala() ) ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( !alreadyReservedRoom.getData().equals(
				newReserveRoomData.getData() )
				|| !alreadyReservedRoom.getHora().equals(
						newReserveRoomData.getHora() ) ) {
			if ( this.checkExistingStudentRoomReserve( newReserveRoomData.getAluno(),
					newReserveRoomData.getData(), newReserveRoomData.getHora() ) ) {
				throw new ReserveException( UNAVAILABLE_STUDENT );
			} else if ( this.checkExistingTeacherRoomReserve(
					newReserveRoomData.getSala(), newReserveRoomData.getData(),
					newReserveRoomData.getHora() ) ) {
				throw new ReserveException( UNAVAILABLE_ROOM );
			}
		}
		if ( !this.checkIfExistsAvailableChairs(
				""
						+ ( Integer.parseInt( newReserveRoomData
								.getReservedChairs() ) - Integer
								.parseInt( alreadyReservedRoom
										.getReservedChairs() ) ),
				newReserveRoomData.getSala(), newReserveRoomData.getData(),
				newReserveRoomData.getHora() ) ) {
			throw new ReserveException( UNAVAILABLE_CHAIRS );
		}
		if ( this.checkPassedDate( newReserveRoomData.getData() ) ) {
			throw new ReserveException( DATE_TIME_PASSED );
		}
		if ( this.checkHourHasPassed( newReserveRoomData.getHora() )
				&& this.checkDateWithCurrent( newReserveRoomData.getData() ) ) {
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
	 */
	public Vector<StudentRoomReserve> getAllStudentReservedRooms()
			throws SQLException, ClienteException, PatrimonyException,
			ReserveException {
		return super
				.buscar( "SELECT * FROM reserva_sala_aluno "
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
	 */
	public Vector<StudentRoomReserve> getStudentReservedRoomsByDay( String date )
			throws SQLException, ClienteException, PatrimonyException,
			ReserveException {
		date = this.standardizeDate( date );
		return super
				.buscar( "SELECT * FROM reserva_sala_aluno "
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
	 */
	public Vector<StudentRoomReserve> getStudentReservedRoomsByHour( String hour )
			throws SQLException, ClienteException, PatrimonyException,
			ReserveException {
		hour = this.standardizeHour( hour );
		return super
				.buscar( "SELECT * FROM reserva_sala_aluno "
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
	 */
	public int getAvailableChairs( Sala room, String date, String hour )
			throws SQLException, PatrimonyException, ClienteException,
			ReserveException {
		date = this.standardizeDate( date );
		hour = this.standardizeHour( hour );
		Vector<StudentRoomReserve> vet = this.getAllStudentReservedRooms();
		Iterator<StudentRoomReserve> it = vet.iterator();
		int total = Integer.parseInt( room.getCapacidade() );
		while ( it.hasNext() ) {
			StudentRoomReserve r = it.next();
			if ( r.getSala().equals( room ) && r.getData().equals( date )
					&& r.getHora().equals( hour ) ) {
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
	 */
	private boolean checkIfExistsAvailableChairs( String reservedChairs,
			Sala room, String date, String hour ) throws SQLException,
			ClienteException, PatrimonyException, ReserveException {
		if ( this.getAvailableChairs( room, date, hour ) >= Integer
				.parseInt( reservedChairs ) ) {
			return true;
		}
		return false;
	}

	@Override
	protected Object fetch( ResultSet resulSet ) throws SQLException,
			ClienteException, PatrimonyException, ReserveException {
		Aluno student = new Aluno( resulSet.getString( "nome" ),
				resulSet.getString( "cpf" ), resulSet.getString( "matricula" ),
				resulSet.getString( "telefone" ), resulSet.getString( "email" ) );

		Sala room = new Sala( resulSet.getString( "codigo" ),
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
	private boolean checkExistingStudent( Aluno student ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM aluno WHERE "
				+ "aluno.nome = \"" + student.getNome() + "\" and "
				+ "aluno.cpf = \"" + student.getCpf() + "\" and "
				+ "aluno.telefone = \"" + student.getTelefone() + "\" and "
				+ "aluno.email = \"" + student.getEmail() + "\" and "
				+ "aluno.matricula = \"" + student.getMatricula() + "\";" );
	}

	/**
	 * Function to check if a room exists in database
	 * 
	 * @param room
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingRoom( Sala room ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + room.getCodigo() + "\" and "
				+ "sala.descricao = \"" + room.getDescricao() + "\" and "
				+ "sala.capacidade = " + room.getCapacidade() + ";" );
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
	private boolean checkExistingStudentRoomReserve( Aluno student, String date,
			String hour ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
				+ "data = \"" + date + "\" and " + "hora = \"" + hour
				+ "\" and " + "id_aluno = (SELECT id_aluno FROM aluno WHERE "
				+ "aluno.nome = \"" + student.getNome() + "\" and "
				+ "aluno.cpf = \"" + student.getCpf() + "\" and "
				+ "aluno.telefone = \"" + student.getTelefone() + "\" and "
				+ "aluno.email = \"" + student.getEmail() + "\" and "
				+ "aluno.matricula = \"" + student.getMatricula() + "\");" );
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
	private boolean checkExistingTeacherRoomReserve( Sala room, String date,
			String hour ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "data = \"" + this.standardizeDate( date ) + "\" and "
				+ "hora = \"" + this.standardizeHour( hour ) + "\" and "
				+ "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \"" + room.getCodigo() + "\" and "
				+ "sala.descricao = \"" + room.getDescricao() + "\" and "
				+ "sala.capacidade = " + room.getCapacidade() + " );" );
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
				+ reservedRoom.getAluno().getNome()
				+ "\" and "
				+ "aluno.cpf = \""
				+ reservedRoom.getAluno().getCpf()
				+ "\" and "
				+ "aluno.telefone = \""
				+ reservedRoom.getAluno().getTelefone()
				+ "\" and "
				+ "aluno.email = \""
				+ reservedRoom.getAluno().getEmail()
				+ "\" and "
				+ "aluno.matricula = \""
				+ reservedRoom.getAluno().getMatricula()
				+ "\") and "
				+ "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \""
				+ reservedRoom.getSala().getCodigo()
				+ "\" and "
				+ "sala.descricao = \""
				+ reservedRoom.getSala().getDescricao()
				+ "\" and "
				+ "sala.capacidade = "
				+ reservedRoom.getSala().getCapacidade()
				+ " ) and "
				+ "finalidade = \""
				+ reservedRoom.getFinality()
				+ "\" and "
				+ "hora = \""
				+ reservedRoom.getHora()
				+ "\" and "
				+ "data = \""
				+ reservedRoom.getData()
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
