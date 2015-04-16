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
	private final String UNAVAILABLE_ROOM = "A Sala esta reservada no mesmo dia e horario.";
	private final String ABSENT_TEACHER = "Professor inexistente.";
	private final String ABSENT_ROOM = "Sala inexistente";
	private final String ABSENT_RESERVE = "Reserva inexistente";
	private final String EXISTING_RESERV = "A reserva ja existe.";
	private final String DATE_TIME_PASSED = "A data escolhida ja passou.";
	private final String HOUR_PASSED = "A hora escolhida ja passou.";

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
	 * Function to get the teacher id from database
	 * 
	 * @param teacher
	 * @return
	 */
	private String select_teacher_id( Teacher teacher ) {
		return "SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \"" + teacher.getName() + "\" and "
				+ "professor.cpf = \"" + teacher.getCpf() + "\" and "
				+ "professor.telefone = \"" + teacher.getFone() + "\" and "
				+ "professor.email = \"" + teacher.getEmail() + "\" and "
				+ "professor.matricula = \"" + teacher.getRegistration() + "\"";
	}

	/**
	 * Function to get the room id in the databse
	 * 
	 * @param room
	 * @return
	 */
	private String select_id_sala( Room room ) {
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
	private String teacher_room_reserve_where_clausule_construct(
			TeacherRoomReserve reservedRoom ) {
		return " WHERE " + "id_professor = ( "
				+ select_teacher_id( reservedRoom.getProfessor() ) + " ) and "
				+ "id_sala = ( " + select_id_sala( reservedRoom.getSala() )
				+ " ) and " + "finalidade = \"" + reservedRoom.getFinality()
				+ "\" and " + "hora = \"" + reservedRoom.getHour() + "\" and "
				+ "data = \"" + reservedRoom.getDate() + "\"";
	}

	/**
	 * Function to construct the values cases for an 'insert'sql query
	 * 
	 * @param reservingRoom
	 * @return
	 */
	private String teacher_room_reserve_insert_values_construct(
			TeacherRoomReserve reservingRoom ) {
		return "( " + select_teacher_id( reservingRoom.getProfessor() )
				+ " ), " + "( " + select_id_sala( reservingRoom.getSala() )
				+ " ), " + "\"" + reservingRoom.getFinality() + "\", " + "\""
				+ reservingRoom.getHour() + "\", " + "\""
				+ reservingRoom.getDate() + "\"";
	}

	/**
	 * Function to construct the attributes of an update sql value cases
	 * 
	 * @param updateReservedRoom
	 * @return
	 */
	private String teacher_room_reserve_update_values_construct(
			TeacherRoomReserve updateReservedRoom ) {
		return "id_professor = ( "
				+ select_teacher_id( updateReservedRoom.getProfessor() )
				+ " ), " + "id_sala = ( "
				+ select_id_sala( updateReservedRoom.getSala() ) + " ), "
				+ "finalidade = \"" + updateReservedRoom.getFinality() + "\", "
				+ "hora = \"" + updateReservedRoom.getHour() + "\", "
				+ "data = \"" + updateReservedRoom.getDate() + "\"";
	}

	/**
	 * Function to construct an insert sql for a new TeacherRoomReserve
	 * 
	 * @param newRoomReserve
	 * @return
	 */
	private String insert_into( TeacherRoomReserve newRoomReserve ) {
		return "INSERT INTO "
				+ "reserva_sala_professor (id_professor, id_sala, finalidade, hora, data) "
				+ "VALUES ( "
				+ teacher_room_reserve_insert_values_construct( newRoomReserve )
				+ " );";
	}

	/**
	 * Function to construct a delete sql for a TeacherRoomReserve
	 * 
	 * @param reservedRommToDelete
	 * @return
	 */
	private String delete_from_professor(
			TeacherRoomReserve reservedRommToDelete ) {
		return "DELETE FROM reserva_sala_professor "
				+ this.teacher_room_reserve_where_clausule_construct( reservedRommToDelete )
				+ " ;";
	}

	/**
	 * Function to construct a delete sql for a student reserved room
	 * 
	 * @param reservedRommToDelete
	 * @return
	 */
	private String delete_from_aluno( TeacherRoomReserve reservedRommToDelete ) {
		return "DELETE FROM reserva_sala_aluno WHERE " + "hora = \""
				+ reservedRommToDelete.getHour() + "\" and " + "data = \""
				+ reservedRommToDelete.getDate() + "\" ;";
	}

	/**
	 * Function to construct an update sql to update a TeacherRoomReserve
	 * 
	 * @param updatingRoomReserve
	 * @param newAttributesRoomToSave
	 * @return
	 */
	private String update( TeacherRoomReserve updatingRoomReserve,
			TeacherRoomReserve newAttributesRoomToSave ) {
		return "UPDATE reserva_sala_professor SET "
				+ this.teacher_room_reserve_update_values_construct( newAttributesRoomToSave )
				+ this.teacher_room_reserve_where_clausule_construct( updatingRoomReserve )
				+ " ;";
	}

	/**
	 * Method to save a new room reserve for a teacher
	 * 
	 * @param roomToReserve
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void saveNewTeacherRoomReserve( TeacherRoomReserve roomToReserve )
			throws ReserveException, SQLException {
		if ( roomToReserve == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( !this.checkExistingTeacher( roomToReserve.getProfessor() ) ) {
			throw new ReserveException( ABSENT_TEACHER );
		} else if ( !this.checkExistingRoom( roomToReserve.getSala() ) ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( this.checkExistingRoomReserve( roomToReserve.getSala(),
				roomToReserve.getDate(), roomToReserve.getHour() ) ) {
			throw new ReserveException( UNAVAILABLE_ROOM );
		} else if ( this.checkExistingTeacherRoomReserve( roomToReserve ) ) {
			throw new ReserveException( EXISTING_RESERV );
		} else if ( this.checkExistingStudentRoomReserve(
				roomToReserve.getDate(), roomToReserve.getHour() ) ) {
			super.executeQuery( this.delete_from_aluno( roomToReserve ) );
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
	 * Method to update a reserve made by a teacher
	 * 
	 * @param alreadyReservedRoom
	 * @param newReserveRoomData
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void updateTeacherRoomReserve(
			TeacherRoomReserve alreadyReservedRoom,
			TeacherRoomReserve newReserveRoomData ) throws ReserveException,
			SQLException {
		if ( alreadyReservedRoom == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( newReserveRoomData == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( !this.checkExistingTeacherRoomReserve( alreadyReservedRoom ) ) {
			throw new ReserveException( ABSENT_RESERVE );
		} else if ( this.checkExistingTeacherRoomReserve( newReserveRoomData ) ) {
			throw new ReserveException( EXISTING_RESERV );
		} else if ( !this.checkExistingTeacher( newReserveRoomData
				.getProfessor() ) ) {
			throw new ReserveException( ABSENT_TEACHER );
		} else if ( !this.checkExistingRoom( newReserveRoomData.getSala() ) ) {
			throw new ReserveException( ABSENT_ROOM );
		} else if ( !alreadyReservedRoom.getDate().equals(
				newReserveRoomData.getDate() )
				|| !alreadyReservedRoom.getHour().equals(
						newReserveRoomData.getHour() ) ) {
			if ( this.checkExistingRoomReserve( newReserveRoomData.getSala(),
					newReserveRoomData.getDate(), newReserveRoomData.getHour() ) ) {
				throw new ReserveException( UNAVAILABLE_ROOM );
			}
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
	 * Method to delete some reserved room for a teacher
	 * 
	 * @param reservedRoomToDelete
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void deleteTeacherReservedRoom(
			TeacherRoomReserve reservedRoomToDelete ) throws ReserveException,
			SQLException {
		if ( reservedRoomToDelete == null ) {
			throw new ReserveException( NULL_TERM );
		} else if ( !this
				.checkExistingTeacherRoomReserve( reservedRoomToDelete ) ) {
			throw new ReserveException( ABSENT_RESERVE );
		} else {
			super.executeQuery( this
					.delete_from_professor( reservedRoomToDelete ) );
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Function to get all teacher reserved rooms from database
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 */
	public Vector<TeacherRoomReserve> getAllTeacherReservedRooms()
			throws SQLException, ClientException, PatrimonyException,
			ReserveException, ClientException {
		return super
				.search( "SELECT * FROM reserva_sala_professor "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+ "INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor;" );
	}

	@SuppressWarnings("unchecked")
	/**
	 * Function to get from the database the reserved rooms for teachers in a given day
	 * 
	 * @param date
	 * @return
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonyException
	 * @throws ReserveException
	 */
	public Vector<TeacherRoomReserve> buscagetTeacherReservedRoomsByDayrPorData(
			String date ) throws SQLException, ClientException,
			PatrimonyException, ReserveException, ClientException {
		return super
				.search( "SELECT * FROM reserva_sala_professor "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+ "INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor"
						+ " WHERE data = \"" + this.standardizeDate( date )
						+ "\";" );
	}

	@Override
	protected Object fetch( ResultSet rs ) throws SQLException,
			ClientException, PatrimonyException, ReserveException, ClientException {
		Teacher p = new Teacher( rs.getString( "nome" ),
				rs.getString( "cpf" ), rs.getString( "matricula" ),
				rs.getString( "telefone" ), rs.getString( "email" ) );

		Room s = new Room( rs.getString( "codigo" ),
				rs.getString( "descricao" ), rs.getString( "capacidade" ) );

		TeacherRoomReserve r = new TeacherRoomReserve( rs.getString( "data" ),
				rs.getString( "hora" ), s, rs.getString( "finalidade" ), p );

		return r;
	}

	/**
	 * Function to check if a teacher is already registered in the database
	 * 
	 * @param teacher
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingTeacher( Teacher teacher )
			throws SQLException {
		return super.inDBGeneric( "SELECT * FROM professor WHERE "
				+ "professor.nome = \"" + teacher.getName() + "\" and "
				+ "professor.cpf = \"" + teacher.getCpf() + "\" and "
				+ "professor.telefone = \"" + teacher.getFone() + "\" and "
				+ "professor.email = \"" + teacher.getEmail() + "\" and "
				+ "professor.matricula = \"" + teacher.getRegistration() + "\";" );
	}

	/**
	 * Function to check if a room is already registered in the database
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
	 * Function to check if a room reserve is already registered in the database
	 * 
	 * @param room
	 * @param date
	 * @param hour
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingRoomReserve( Room room, String date,
			String hour ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "data = \"" + date + "\" and " + "hora = \"" + hour
				+ "\" and " + "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \"" + room.getCode() + "\" and "
				+ "sala.descricao = \"" + room.getDescription() + "\" and "
				+ "sala.capacidade = " + room.getCapacity() + " );" );
	}

	/**
	 * Function to check if a room reserve made by a teacher is already
	 * registered in the database
	 * 
	 * @param roomReserve
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingTeacherRoomReserve(
			TeacherRoomReserve roomReserve ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "id_professor = (SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \""
				+ roomReserve.getProfessor().getName()
				+ "\" and "
				+ "professor.cpf = \""
				+ roomReserve.getProfessor().getCpf()
				+ "\" and "
				+ "professor.telefone = \""
				+ roomReserve.getProfessor().getFone()
				+ "\" and "
				+ "professor.email = \""
				+ roomReserve.getProfessor().getEmail()
				+ "\" and "
				+ "professor.matricula = \""
				+ roomReserve.getProfessor().getRegistration()
				+ "\") and "
				+ "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \""
				+ roomReserve.getSala().getCode()
				+ "\" and "
				+ "sala.descricao = \""
				+ roomReserve.getSala().getDescription()
				+ "\" and "
				+ "sala.capacidade = "
				+ roomReserve.getSala().getCapacity()
				+ " ) and "
				+ "finalidade = \""
				+ roomReserve.getFinality()
				+ "\" and "
				+ "hora = \""
				+ roomReserve.getHour()
				+ "\" and "
				+ "data = \"" + roomReserve.getDate() + "\";" );
	}

	/**
	 * Function to check if a student room reserve exist in db
	 * 
	 * @param date
	 * @param hour
	 * @return
	 * @throws SQLException
	 */
	private boolean checkExistingStudentRoomReserve( String date, String hour )
			throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
				+ "data = \"" + date + "\" and " + "hora = \"" + hour + "\";" );
	}

	/**
	 * Function to get the current date
	 * 
	 * @return
	 */
	private String getCurrentDate() {
		Date date = new Date( System.currentTimeMillis() );
		SimpleDateFormat formatador = new SimpleDateFormat( "dd/MM/yyyy" );
		return formatador.format( date );
	}

	/**
	 * Function to get the current hour
	 * 
	 * @return
	 */
	private String getCurrentHour() {
		Date date = new Date( System.currentTimeMillis() );
		return date.toString().substring( 11, 16 );
	}

	/**
	 * Function to validate if a given date already passed
	 * 
	 * @param givenDate
	 * @return
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
		}
		return false;
	}

	/**
	 * Function to check if the given date is equal to current date
	 * 
	 * @param givenDate
	 * @return
	 */
	public boolean checkDateWithCurrent( String givenDate ) {
		givenDate = this.standardizeDate( givenDate );
		String currentDate[] = this.getCurrentDate().split( "[./-]" );
		String dateToCheck[] = givenDate.split( "[./-]" );

		if ( currentDate[ 0 ].equals( dateToCheck[ 0 ] )
				&& currentDate[ 1 ].equals( dateToCheck[ 1 ] )
				&& currentDate[ 2 ].equals( dateToCheck[ 2 ] ) ) {
			return true;
		}
		return false;
	}

	/**
	 * Function to check if the given hour has already passed
	 * 
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
	 * 
	 * @param givenDate
	 * @return
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
	 * private String padronizarHora(String hora){ if(hora.length() == 4) return
	 * "0" + hora; return hora; }
	 */
}