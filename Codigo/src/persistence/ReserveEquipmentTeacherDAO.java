package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import model.Equipment;
import model.Teacher;
import model.TeacherEquipmentReserve;
import exception.ClientException;
import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ReserveEquipmentTeacherDAO extends DAO {

	// Mensagens e Alertas
	private final String NULA = "Termo nulo.";
	private final String EQUIPAMENTO_INDISPONIVEL = "O Equipamento esta reservada no mesmo dia e horario.";
	private final String PROFESSOR_INEXISTENTE = "Professor inexistente.";
	private final String EQUIPAMENTO_INEXISTENTE = "Equipamento inexistente";
	private final String RESERVA_INEXISTENTE = "Reserva inexistente";
	private final String RESERVA_EXISTENTE = "A reserva ja existe.";

	// Singleton
	private static ReserveEquipmentTeacherDAO instance;

	private ReserveEquipmentTeacherDAO() {
	}

	public static ReserveEquipmentTeacherDAO getInstance() {
		if ( instance == null ) { 
			instance = new ReserveEquipmentTeacherDAO();
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
		return "SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \"" + p.getName() + "\" and "
				+ "professor.cpf = \"" + p.getCpf() + "\" and "
				+ "professor.telefone = \"" + p.getFone() + "\" and "
				+ "professor.email = \"" + p.getEmail() + "\" and "
				+ "professor.matricula = \"" + p.getRegistration() + "\"";
	}

	private String select_id_equipamento( Equipment equipamento ) {
		return "SELECT id_equipamento FROM equipamento WHERE "
				+ "equipamento.codigo = \"" + equipamento.getCode()
				+ "\" and " + "equipamento.descricao = \""
				+ equipamento.getDescription();
	}

	private String where_reserva_equipamento_professor( 
			TeacherEquipmentReserve r ) {
		return " WHERE " + "id_professor = ( "
				+ select_id_professor( r.getProfessor() ) + " ) and "
				+ "id_equipamento = ( "
				+ select_id_equipamento( r.getEquipment() ) + " ) and "
				+ "hora = \"" + r.getHour() + "\" and " + "data = \""
				+ r.getDate();
	}

	private String values_reserva_equipamento_professor( 
			TeacherEquipmentReserve r ) {
		return "( " + select_id_professor( r.getProfessor() ) + " ), " + "( "
				+ select_id_equipamento( r.getEquipment() ) + " ), " + "\""
				+ r.getHour() + "\", " + "\"" + r.getDate();
	}

	private String atributes_value_reserva_equipamento_professor( 
			TeacherEquipmentReserve r ) {
		return "id_professor = ( " + select_id_professor( r.getProfessor() )
				+ " ), " + "id_equipamento = ( "
				+ select_id_equipamento( r.getEquipment() ) + " ), "
				+ "hora = \"" + r.getHour() + "\", " + "data = \""
				+ r.getDate();
	}

	private String insert_into( TeacherEquipmentReserve r ) {
		return "INSERT INTO "
				+ "reserva_equipamento_professor (id_professor, id_equipamento, hora, data) "
				+ "VALUES ( " + values_reserva_equipamento_professor( r ) + " );";
	}

	private String update( TeacherEquipmentReserve r,
			TeacherEquipmentReserve r2 ) {
		return "UPDATE reserva_equipamento_professor SET "
				+ this.atributes_value_reserva_equipamento_professor( r2 )
				+ this.where_reserva_equipamento_professor( r ) + " ;";
	}

	private String delete_from_professor( TeacherEquipmentReserve r ) {
		return "DELETE FROM reserva_equipamento_professor "
				+ this.where_reserva_equipamento_professor( r ) + " ;";
	}

	private String delete_from_aluno( TeacherEquipmentReserve r ) {
		return "DELETE FROM reserva_equipamento_aluno WHERE " + "hora = \""
				+ r.getHour() + "\" and " + "data = \"" + r.getDate() + " ;";
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
		}
		else if ( this.professorinReservaDB( teacherReserve.getProfessor(), teacherReserve.getDate(),
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
	public Vector<Object> buscarTodos() throws SQLException, ClienteException,
			PatrimonyException, ReserveException, ClientException {
		return super
				.search( "SELECT * FROM reserva_sala_professor "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+ "INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor;" );
	}

	@SuppressWarnings( "unchecked" )
	public Vector<TeacherEquipmentReserve> buscarPorMes( int mes )
			throws SQLException, ClienteException, PatrimonyException,
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
			}
		}
		return reservas_prof_mes;
	}

	@SuppressWarnings( "unchecked" )
	public Vector<TeacherEquipmentReserve> buscarPorHora( String hora )
			throws SQLException, ClienteException, PatrimonyException,
			ReserveException, ClientException {
		String hora_a = "", hora_b = "";
		if ( hora.length() == 4 ) {
			hora_a = "0" + hora;
		}
		if ( hora.charAt( 0 ) == '0' ) {
			hora_b = hora.substring( 1 );
		}
		return super
				.search( "SELECT * FROM reserva_equipamento_professor "
						+ "INNER JOIN equipamento ON equipamento.id_equipamento = reserva_equipamento_professor.id_equipamento "
						+ "INNER JOIN professor ON professor.id_professor = reserva_equipamento_professor.id_professor "
						+ " WHERE hora = \"" + hora + "\" or hora = \""
						+ hora_a + "\" or hora = \"" + hora_b + "\";" );
	}

	@Override
	protected Object fetch( ResultSet rs ) throws SQLException, ClienteException,
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
		return super.inDBGeneric( "SELECT * FROM professor WHERE "
				+ "professor.nome = \"" + professor.getName() + "\" and "
				+ "professor.cpf = \"" + professor.getCpf() + "\" and "
				+ "professor.telefone = \"" + professor.getFone()
				+ "\" and " + "professor.email = \"" + professor.getEmail()
				+ "\" and " + "professor.matricula = \""
				+ professor.getRegistration() + "\";" );
	}

	private boolean equipamentoinDB( Equipment equipamento )
			throws SQLException {
		return super.inDBGeneric( "SELECT * FROM equipamento WHERE "
				+ "equipamento.codigo = \"" + equipamento.getCode()
				+ "\" and " + "equipamento.descricao = \""
				+ equipamento.getDescription() + "\" and " + ";" );
	}

	private boolean professorinReservaDB( Teacher professor, String data,
			String hora ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "data = \"" + data + "\" and " + "hora = \"" + hora
				+ "\" and "
				+ "id_professor = (SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \"" + professor.getName() + "\" and "
				+ "professor.cpf = \"" + professor.getCpf() + "\" and "
				+ "professor.telefone = \"" + professor.getFone()
				+ "\" and " + "professor.email = \"" + professor.getEmail()
				+ "\" and " + "professor.matricula = \""
				+ professor.getRegistration() + "\");" );
	}

	private boolean equipamentoinReservaDB( Equipment equipamento,
			String data, String hora ) throws SQLException {
		return super
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
	}

	private boolean reservainDB( TeacherEquipmentReserve r )
			throws SQLException {
		return super
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
	}

}
