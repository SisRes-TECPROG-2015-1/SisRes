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

public class ResEquipamentoProfessorDAO extends DAO {

	// Mensagens e Alertas
	private final String NULA = "Termo nulo.";
	private final String EQUIPAMENTO_INDISPONIVEL = "O Equipamento esta reservada no mesmo dia e horario.";
	private final String PROFESSOR_INEXISTENTE = "Professor inexistente.";
	private final String EQUIPAMENTO_INEXISTENTE = "Equipamento inexistente";
	private final String RESERVA_INEXISTENTE = "Reserva inexistente";
	private final String RESERVA_EXISTENTE = "A reserva ja existe.";

	// Singleton
	private static ResEquipamentoProfessorDAO instance;

	private ResEquipamentoProfessorDAO() {
	}

	public static ResEquipamentoProfessorDAO getInstance() {
		if ( instance == null ) { 
			instance = new ResEquipamentoProfessorDAO();
		}
		return instance;
	}

	//

	//Querys de reuso
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

	//Metodo para incluir reserva
	public void incluir( TeacherEquipmentReserve r ) throws ReserveException,
			SQLException {
		if ( r == null ) {
			throw new ReserveException( NULA );
		} else if ( !this.professorinDB(r.getProfessor() ) ) {
			throw new ReserveException( PROFESSOR_INEXISTENTE );
		} else if ( !this.equipamentoinDB( r.getEquipment() ) ) {
			throw new ReserveException( EQUIPAMENTO_INEXISTENTE );
		} else if ( this.equipamentoinReservaDB(r.getEquipment(), r.getDate(),
				r.getHour() ) ) {
			throw new ReserveException( EQUIPAMENTO_INDISPONIVEL );
		}
		else if ( this.professorinReservaDB( r.getProfessor(), r.getDate(),
				r.getHour() ) ) {
			throw new ReserveException( RESERVA_EXISTENTE );
		} else {
			super.executeQuery( this.delete_from_aluno( r ) );
			super.executeQuery( this.insert_into( r ) );
		}

	}

	//Metodo para altera reserva
	public void alterar( TeacherEquipmentReserve r,
			TeacherEquipmentReserve r_new ) throws ReserveException,
			SQLException {
		if ( r == null ) {
			throw new ReserveException( NULA );
		} else if ( r_new == null ) {
			throw new ReserveException( NULA );
		} else if ( !this.reservainDB( r ) ) {
			throw new ReserveException( RESERVA_INEXISTENTE );
		} else if ( this.reservainDB( r_new ) ) {
			throw new ReserveException( RESERVA_EXISTENTE );
		} else if ( !r.getDate().equals( r_new.getDate() )
				|| !r.getHour().equals( r_new.getHour() ) ) {
			if ( this.professorinReservaDB( r_new.getProfessor(),
					r_new.getDate(), r_new.getHour() ) ) {
				throw new ReserveException( RESERVA_EXISTENTE );
			} else if ( this.equipamentoinReservaDB( r_new.getEquipment(),
					r_new.getDate(), r_new.getHour() ) ) {
				throw new ReserveException( EQUIPAMENTO_INDISPONIVEL );
			
			} else if ( !this.professorinDB( r_new.getProfessor() ) ) {
				throw new ReserveException( PROFESSOR_INEXISTENTE );
			} else if (!this.equipamentoinDB( r_new.getEquipment() ) ) {
				throw new ReserveException( EQUIPAMENTO_INEXISTENTE );
			} else {
				super.updateQuery( this.update( r, r_new ) );
			}
		}
	}

	//Metodo para excluir reserva
	public void excluir( TeacherEquipmentReserve r ) throws ReserveException,
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
				.buscar( "SELECT * FROM reserva_sala_professor "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+ "INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor;" );
	}

	@SuppressWarnings( "unchecked" )
	public Vector<TeacherEquipmentReserve> buscarPorMes( int mes )
			throws SQLException, ClienteException, PatrimonyException,
			ReserveException, ClientException {
		Vector<TeacherEquipmentReserve> reservas_prof_mes = super
				.buscar( "SELECT * FROM reserva_equipamento_professor "
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
				.buscar( "SELECT * FROM reserva_equipamento_professor "
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
