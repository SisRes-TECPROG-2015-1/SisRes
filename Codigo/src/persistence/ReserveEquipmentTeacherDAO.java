package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import model.Equipment;
import model.Teacher;
import model.TeacherEquipmentReserve;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;


//Importing Log4J2 classes 
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ReserveEquipmentTeacherDAO extends DAO {

	// Mensagens e Alertas
	private final String NULA = "Termo nulo.";
	private final String EQUIPAMENTO_INDISPONIVEL = "O Equipamento esta reservada no mesmo dia e horario.";
	private final String PROFESSOR_INEXISTENTE = "Professor inexistente.";
	private final String EQUIPAMENTO_INEXISTENTE = "Equipamento inexistente";
	private final String RESERVA_INEXISTENTE = "Reserva inexistente";
	private final String RESERVA_EXISTENTE = "A reserva ja existe.";
	
	//logger
	static final Logger logger = LogManager.getLogger( ReserveEquipmentTeacherDAO.class.getName() );
	
	// Singleton
	private static ReserveEquipmentTeacherDAO instance;

	private ReserveEquipmentTeacherDAO() {
	}

	public static ReserveEquipmentTeacherDAO getInstance() {
		if ( instance == null ) { 
			logger.trace( "There is any instance of teacher equipment reserve DAO");
			instance = new ReserveEquipmentTeacherDAO();
			logger.trace( "A new teacher equipment reserve DAO is just instantiated" );
		}else{
			//do nothing
		}
		return instance;
	}

	//

	/**
	 * Reusable querys
	 * @param p
	 * @return
	 */
	private String select_id_professor( Teacher p ) {
		logger.trace( "Selecting teacher id." );
		String select = "SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \"" + p.getName() + "\" and "
				+ "professor.cpf = \"" + p.getCpf() + "\" and "
				+ "professor.telefone = \"" + p.getFone() + "\" and "
				+ "professor.email = \"" + p.getEmail() + "\" and "
				+ "professor.matricula = \"" + p.getRegistration() + "\"";
		logger.trace( "Teacher id has been colected." );
		return select;
	}

	private String select_id_equipamento( Equipment equipamento ) {
		logger.trace( "Selecting equipment id." );
		String select = "SELECT id_equipamento FROM equipamento WHERE "
				+ "equipamento.codigo = \"" + equipamento.getCode()
				+ "\" and " + "equipamento.descricao = \""
				+ equipamento.getDescription();
		logger.trace( "Equipment id has been selected." );
		return select;
	}

	private String where_reserva_equipamento_professor( 
			TeacherEquipmentReserve r ) {
		String select = " WHERE " + "id_professor = ( "
				+ select_id_professor( r.getProfessor() ) + " ) and "
				+ "id_equipamento = ( "
				+ select_id_equipamento( r.getEquipment() ) + " ) and "
				+ "hora = \"" + r.getHour() + "\" and " + "data = \""
				+ r.getDate();
		return select;
	}

	private String values_reserva_equipamento_professor( 
			TeacherEquipmentReserve r ) {
		String select = "( " + select_id_professor( r.getProfessor() ) + " ), " + "( "
				+ select_id_equipamento( r.getEquipment() ) + " ), " + "\""
				+ r.getHour() + "\", " + "\"" + r.getDate();
		return select;
	}

	private String atributes_value_reserva_equipamento_professor( 
			TeacherEquipmentReserve r ) {
		String select = "id_professor = ( " + select_id_professor( r.getProfessor() )
				+ " ), " + "id_equipamento = ( "
				+ select_id_equipamento( r.getEquipment() ) + " ), "
				+ "hora = \"" + r.getHour() + "\", " + "data = \""
				+ r.getDate();
		return select;
	}

	private String insert_into( TeacherEquipmentReserve r ) {
		logger.trace( "Inserting a new equipment reserve." );
		String select = "INSERT INTO "
				+ "reserva_equipamento_professor (id_professor, id_equipamento, hora, data) "
				+ "VALUES ( " + values_reserva_equipamento_professor( r ) + " );";
		logger.trace( "A new equipment reserve has been saved." );
		return select;
	}

	private String update( TeacherEquipmentReserve r,
			TeacherEquipmentReserve r2 ) {
		logger.trace( "Updating an equipment reserve." );
		String select = "UPDATE reserva_equipamento_professor SET "
				+ this.atributes_value_reserva_equipamento_professor( r2 )
				+ this.where_reserva_equipamento_professor( r ) + " ;";
		logger.trace( "An equipment reserve has been updated." );
		return select;
	}

	private String delete_from_professor( TeacherEquipmentReserve r ) {
		logger.trace( "Deleting an equipment reserve." );
		String select = "DELETE FROM reserva_equipamento_professor "
				+ this.where_reserva_equipamento_professor( r ) + " ;";
		logger.trace( "An equipment reserve has been deleted." );
		return select;
	}

	private String delete_from_aluno( TeacherEquipmentReserve r ) {
		logger.trace( "Deleting an equipment reserve." );
		String select = "DELETE FROM reserva_equipamento_aluno WHERE " + "hora = \""
				+ r.getHour() + "\" and " + "data = \"" + r.getDate() + " ;";
		logger.trace( "An equipment reserve has been deleted." );
		return select;
	}

	/**
	 * Method to include a reservation
	 * @param teacherReserve
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void includeReserve( TeacherEquipmentReserve teacherReserve ) throws ReserveException,
			SQLException {
		if ( teacherReserve == null ) {
			throw new ReserveException( NULA );
		} else if ( !this.professorinDB(teacherReserve.getProfessor() ) ) {
			throw new ReserveException( PROFESSOR_INEXISTENTE );
		} else if ( !this.equipamentoinDB( teacherReserve.getEquipment() ) ) {
			throw new ReserveException( EQUIPAMENTO_INEXISTENTE );
		} else if ( this.equipamentoinReservaDB(teacherReserve.getEquipment(), teacherReserve.getDate(),
				teacherReserve.getHour() ) ) {
			throw new ReserveException( EQUIPAMENTO_INDISPONIVEL );
		} else if ( this.professorinReservaDB( teacherReserve.getProfessor(), teacherReserve.getDate(),
				teacherReserve.getHour() ) ) {
			throw new ReserveException( RESERVA_EXISTENTE );
		} else {
			super.executeQuery( this.delete_from_aluno( teacherReserve ) );
			super.executeQuery( this.insert_into( teacherReserve ) );
		}

	}

	/**
	 * This method modifies a reserve into the database
	 * @param oldReservation
	 * @param newReservation
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void alterar( TeacherEquipmentReserve oldReservation,
			TeacherEquipmentReserve newReservation ) throws ReserveException,
			SQLException {
		if ( oldReservation == null ) {
			throw new ReserveException( NULA );
		} else if ( newReservation == null ) {
			throw new ReserveException( NULA );
		} else if ( !this.reservainDB( oldReservation ) ) {
			throw new ReserveException( RESERVA_INEXISTENTE );
		} else if ( this.reservainDB( newReservation ) ) {
			throw new ReserveException( RESERVA_EXISTENTE );
		} else if ( !oldReservation.getDate().equals( newReservation.getDate() )
				|| !oldReservation.getHour().equals( newReservation.getHour() ) ) {
			if ( this.professorinReservaDB( newReservation.getProfessor(),
					newReservation.getDate(), newReservation.getHour() ) ) {
				throw new ReserveException( RESERVA_EXISTENTE );
			} else if ( this.equipamentoinReservaDB( newReservation.getEquipment(),
					newReservation.getDate(), newReservation.getHour() ) ) {
				throw new ReserveException( EQUIPAMENTO_INDISPONIVEL );
			
			} else if ( !this.professorinDB( newReservation.getProfessor() ) ) {
				throw new ReserveException( PROFESSOR_INEXISTENTE );
			} else if (!this.equipamentoinDB( newReservation.getEquipment() ) ) {
				throw new ReserveException( EQUIPAMENTO_INEXISTENTE );
			} else {
				super.updateQuery( this.update( oldReservation, newReservation ) );
			}
		}else{
			//do nothing
		}
		
	}

	/**
	 * Method to exclude a reservation into the database
	 * @param r
	 * @throws ReserveException
	 * @throws SQLException
	 */
	public void excludeReservation( TeacherEquipmentReserve r ) throws ReserveException,
			SQLException {
		if ( r == null ) {
			throw new ReserveException( NULA );
		}
		else if ( !this.reservainDB( r ) ) {
			throw new ReserveException( RESERVA_INEXISTENTE );
		}
		else {
			super.executeQuery( this.delete_from_professor( r ) );
		}
	}

	@SuppressWarnings( "unchecked" )
	public Vector<Object> buscarTodos() throws SQLException, ClientException,
			PatrimonyException, ReserveException, ClientException {
		Vector<Object> searchSelect = super
				.search( "SELECT * FROM reserva_sala_professor "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+ "INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor;" );
		return searchSelect;
	}

	@SuppressWarnings( "unchecked" )
	public Vector<TeacherEquipmentReserve> buscarPorMes( int mes )
			throws SQLException, ClientException, PatrimonyException,
			ReserveException, ClientException {
		Vector<TeacherEquipmentReserve> reservas_prof_mes = super
				.search( "SELECT * FROM reserva_equipamento_professor "
						+ "INNER JOIN equipamento ON equipamento.id_equipamento = reserva_equipamento_professor.id_equipamento "
						+ "INNER JOIN professor ON professor.id_professor = reserva_equipamento_professor.id_professor;");
		Iterator<TeacherEquipmentReserve> it = reservas_prof_mes.iterator();
		while ( it.hasNext() ) {
			TeacherEquipmentReserve obj = it.next();
			if ( Integer.parseInt( obj.getDate().split( "[./-]" )[1] ) != mes ) {
				reservas_prof_mes.remove( obj );
			}else{
				//do nothing
			}
		}
		return reservas_prof_mes;
	}

	@SuppressWarnings( "unchecked" )
	public Vector<TeacherEquipmentReserve> buscarPorHora( String hora )
			throws SQLException, ClientException, PatrimonyException,
			ReserveException, ClientException {
		String hora_a = "", hora_b = "";
		if ( hora.length() == 4 ) {
			hora_a = "0" + hora;
		}else{
			//do nothing
		}
		
		if ( hora.charAt( 0 ) == '0' ) {
			hora_b = hora.substring( 1 );
		}else{
			//do nothing
		}
		
		Vector<TeacherEquipmentReserve> searchSelect = super
				.search( "SELECT * FROM reserva_equipamento_professor "
						+ "INNER JOIN equipamento ON equipamento.id_equipamento = reserva_equipamento_professor.id_equipamento "
						+ "INNER JOIN professor ON professor.id_professor = reserva_equipamento_professor.id_professor "
						+ " WHERE hora = \"" + hora + "\" or hora = \""
						+ hora_a + "\" or hora = \"" + hora_b + "\";" );
		return searchSelect;
	}

	@Override
	protected Object fetch( ResultSet rs ) throws SQLException, ClientException,
			PatrimonyException, ReserveException, ClientException {
		
		Teacher p = new Teacher( rs.getString( "nome" ), rs.getString( "cpf" ),
				rs.getString( "matricula" ), rs.getString( "telefone" ),
				rs.getString( "email" ) );

		Equipment s = new Equipment( rs.getString( "codigo" ),
				rs.getString( "descricao" ) );

		TeacherEquipmentReserve r = new TeacherEquipmentReserve( 
				rs.getString( "data" ), rs.getString( "hora" ), s, p );

		return r;
	}

	private boolean professorinDB( Teacher professor ) throws SQLException {
		boolean select = super.inDBGeneric( "SELECT * FROM professor WHERE "
				+ "professor.nome = \"" + professor.getName() + "\" and "
				+ "professor.cpf = \"" + professor.getCpf() + "\" and "
				+ "professor.telefone = \"" + professor.getFone()
				+ "\" and " + "professor.email = \"" + professor.getEmail()
				+ "\" and " + "professor.matricula = \""
				+ professor.getRegistration() + "\";" );
		return select;
	}

	private boolean equipamentoinDB( Equipment equipamento )
			throws SQLException {
		boolean select = super.inDBGeneric( "SELECT * FROM equipamento WHERE "
				+ "equipamento.codigo = \"" + equipamento.getCode()
				+ "\" and " + "equipamento.descricao = \""
				+ equipamento.getDescription() + "\" and " + ";" );
		return select;
	}

	private boolean professorinReservaDB( Teacher professor, String data,
			String hora ) throws SQLException {
		boolean select = super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "data = \"" + data + "\" and " + "hora = \"" + hora
				+ "\" and "
				+ "id_professor = (SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \"" + professor.getName() + "\" and "
				+ "professor.cpf = \"" + professor.getCpf() + "\" and "
				+ "professor.telefone = \"" + professor.getFone()
				+ "\" and " + "professor.email = \"" + professor.getEmail()
				+ "\" and " + "professor.matricula = \""
				+ professor.getRegistration() + "\");" );
		return select;
	}

	private boolean equipamentoinReservaDB( Equipment equipamento,
			String data, String hora ) throws SQLException {
		boolean select = super
				.inDBGeneric("SELECT * FROM reserva_equipamento_professor WHERE "
						+ "data = \""
						+ data
						+ "\" and "
						+ "hora = \""
						+ hora
						+ "\" and "
						+ "id_equipamento = (SELECT id_equipamento FROM equipamento WHERE "
						+ "equipamento.codigo = \""
						+ equipamento.getCode()
						+ "\" and "
						+ "equipamento.descricao = \""
						+ equipamento.getDescription() + "\" and " + ");");
		return select;
	}

	private boolean reservainDB( TeacherEquipmentReserve r )
			throws SQLException {
		boolean select = super
				.inDBGeneric( "SELECT * FROM reserva_equipamento_professor WHERE "
						+ "id_professor = (SELECT id_professor FROM professor WHERE "
						+ "professor.nome = \""
						+ r.getProfessor().getName()
						+ "\" and "
						+ "professor.cpf = \""
						+ r.getProfessor().getCpf()
						+ "\" and "
						+ "professor.telefone = \""
						+ r.getProfessor().getFone()
						+ "\" and "
						+ "professor.email = \""
						+ r.getProfessor().getEmail()
						+ "\" and "
						+ "professor.matricula = \""
						+ r.getProfessor().getRegistration()
						+ "\") and "
						+ "id_equipamento = (SELECT id_equipamento FROM equipamento WHERE "
						+ "equipamento.codigo = \""
						+ r.getEquipment().getCode()
						+ "\" and "
						+ "equipamento.descricao = \""
						+ r.getEquipment().getDescription()
						+ "\" and "
						+ "hora = \""
						+ r.getHour()
						+ "\" and "
						+ "data = \""
						+ r.getDate() + ";" );
		return select;
	}

}
