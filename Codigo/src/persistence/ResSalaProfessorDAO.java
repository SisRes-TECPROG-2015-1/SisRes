package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import model.Professor;
import model.ReservaSalaProfessor;
import model.Sala;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

public class ResSalaProfessorDAO extends DAO {

	private final String NULA = "Termo nulo."; // Attribute indicates a null term
	private final String SALA_INDISPONIVEL = "A Sala esta reservada no mesmo dia e horario."; // Attribute indicates room is unavailable
	private final String PROFESSOR_INEXISTENTE = "Professor inexistente."; // Attribute indicates teacher doesnt exist.
	private final String SALA_INEXISTENTE = "Sala inexistente"; // Attribute indicates room doesnt exist.
	private final String RESERVA_INEXISTENTE = "Reserva inexistente"; // Attribute indicates booking doesnt exists.
	private final String RESERVA_EXISTENTE = "A reserva ja existe."; // Attribute indicates booking already exists.
	private final String DATA_JA_PASSOU = "A data escolhida ja passou."; // Attribute that indicates the date choosen already passed
	private final String HORA_JA_PASSOU = "A hora escolhida ja passou."; // Atribute that indicates the time choosen already passed

	private static ResSalaProfessorDAO instance; // This attribute is an instance of ResSalaProfessorDAO

	/**
	 *  Empty Constructor
	 */
	private ResSalaProfessorDAO() {
	}

	/**
	 * This method is the getter for the attribute instance
	 * @return: a ResSalaProfessorDAO instance
	 */
	public static ResSalaProfessorDAO getInstance() {
		if ( instance == null ) {
			instance = new ResSalaProfessorDAO();
		}
		return instance;
	}

	/**
	 * This method is a procedure to select a teacher by id 
	 * @param p: indicates an object professor to be searched
	 * @return: query to the search
	 */
	private String select_id_professor( Professor p ) {
		return "SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \"" + p.getNome() + "\" and "
				+ "professor.cpf = \"" + p.getCpf() + "\" and "
				+ "professor.telefone = \"" + p.getTelefone() + "\" and "
				+ "professor.email = \"" + p.getEmail() + "\" and "
				+ "professor.matricula = \"" + p.getMatricula() + "\"";
	}

	/**
	 * This method is a procedure to select a room by id
	 * @param sala: indicates an object room to be searched
	 * @return: query to the search
	 */
	private String select_id_sala( Sala sala ) {
		return "SELECT id_sala FROM sala WHERE " + "sala.codigo = \""
				+ sala.getCodigo() + "\" and " + "sala.descricao = \""
				+ sala.getDescricao() + "\" and " + "sala.capacidade = "
				+ sala.getCapacidade();
	}

	/**
	 * This method is a query to select bookings from the table reserva_sala_professor
	 * into the database
	 * @param r: indicates a booking
	 * @return: a query string
	 */
	private String where_reserva_sala_professor( ReservaSalaProfessor r ) {
		return " WHERE " + "id_professor = ( "
				+ select_id_professor( r.getProfessor() ) + " ) and "
				+ "id_sala = ( " + select_id_sala( r.getSala() ) + " ) and "
				+ "finalidade = \"" + r.getFinalidade() + "\" and "
				+ "hora = \"" + r.getHora() + "\" and " + "data = \""
				+ r.getData() + "\"";
	}

	/**
	 * This method is query to pass values to the teacher_room_reservation
	 * @param r: indicates a booking
	 * @return: a query string 
	 */
	private String values_reserva_sala_professor( ReservaSalaProfessor r ) {
		return "( " + select_id_professor( r.getProfessor() ) + " ), " + "( "
				+ select_id_sala( r.getSala() ) + " ), " + "\""
				+ r.getFinalidade() + "\", " + "\"" + r.getHora() + "\", "
				+ "\"" + r.getData() + "\"";
	}

	/**
	 * 
	 * @param r
	 * @return
	 */
	private String atibutes_value_reserva_sala_professor( ReservaSalaProfessor r ) {
		return "id_professor = ( " + select_id_professor( r.getProfessor() )
				+ " ), " + "id_sala = ( " + select_id_sala( r.getSala() )
				+ " ), " + "finalidade = \"" + r.getFinalidade() + "\", "
				+ "hora = \"" + r.getHora() + "\", " + "data = \""
				+ r.getData() + "\"";
	}

	/**
	 * This method is a procedure to insert into the table reserva_sala_professor
	 * @param r: a ReservaSalaProfessor object
	 * @return: query string
	 */
	private String insert_into( ReservaSalaProfessor r ) {
		return "INSERT INTO "
				+ "reserva_sala_professor (id_professor, id_sala, finalidade, hora, data) "
				+ "VALUES ( " + values_reserva_sala_professor( r ) + " );";
	}

	/**
	 * This method is a procedure to delete from the table reserva_sala_professor
	 * @param r: a ReservaSalaProfessor object
	 * @return a query string
	 */
	private String delete_from_professor( ReservaSalaProfessor r ) {
		return "DELETE FROM reserva_sala_professor "
				+ this.where_reserva_sala_professor( r ) + " ;";
	}

	/**
	 * This method is a procedure to delete from the table reserva_sala_aluno
	 * @param r: a ReservaSalaProfessor object
	 * @return: a query 
	 */
	private String delete_from_aluno( ReservaSalaProfessor r ) {
		return "DELETE FROM reserva_sala_aluno WHERE " + "hora = \""
				+ r.getHora() + "\" and " + "data = \"" + r.getData() + "\" ;";
	}

	/**
	 * This method is a procedure to update the table reserva_sala_professor
	 * @param r: a ReservaSalaProfessor object
	 * @return: a query 
	 */
	private String update( ReservaSalaProfessor r, ReservaSalaProfessor r2 ) {
		return "UPDATE reserva_sala_professor SET "
				+ this.atibutes_value_reserva_sala_professor( r2 )
				+ this.where_reserva_sala_professor( r ) + " ;";
	}

	/**
	 * This method includes a new booking into the database
	 * @param r
	 * @throws ReservaException
	 * @throws SQLException
	 */
	public void incluir( ReservaSalaProfessor r ) throws ReservaException,
			SQLException {
		if ( r == null ) {
			throw new ReservaException( NULA );
		} else if ( !this.professorinDB( r.getProfessor() ) ) {
			throw new ReservaException( PROFESSOR_INEXISTENTE );
		} else if ( !this.salainDB( r.getSala() ) ) {
			throw new ReservaException( SALA_INEXISTENTE );
		} else if ( this.salainReservaDB( r.getSala(), r.getData(), r.getHora() ) ) {
			throw new ReservaException( SALA_INDISPONIVEL );
		} else if ( this.reservainDB( r ) ) {
			throw new ReservaException( RESERVA_EXISTENTE );
		} else if ( this.alunoinReservaDB( r.getData(), r.getHora() ) ) {
			super.executeQuery( this.delete_from_aluno( r ) );
		} 
		if ( this.dataPassou( r.getData() ) ) {
			throw new ReservaException( DATA_JA_PASSOU );
		}
		if ( this.dataIgual( r.getData() ) ) {
			if ( this.horaPassou( r.getHora() ) ) {
				throw new ReservaException( HORA_JA_PASSOU );
			} else {
				super.executeQuery( this.insert_into( r ) );
			}
		} else {
			super.executeQuery( this.insert_into( r ) );
		}
	}
	
	/**
	 * This method modifies a booking into the database
	 * @param r: booking to be modified
	 * @param r_new: the new booking will be set.
	 * @throws ReservaException
	 * @throws SQLException
	 */
	public void alterar( ReservaSalaProfessor r, ReservaSalaProfessor r_new )
			throws ReservaException, SQLException {
		if ( r == null ) {
			throw new ReservaException( NULA );
		} else if ( r_new == null ) {
			throw new ReservaException( NULA );
		} else if ( !this.reservainDB( r ) ) {
			throw new ReservaException( RESERVA_INEXISTENTE );
		} else if ( this.reservainDB( r_new ) ) {
			throw new ReservaException( RESERVA_EXISTENTE );
		} else if ( !this.professorinDB( r_new.getProfessor() ) ) {
			throw new ReservaException( PROFESSOR_INEXISTENTE );
		} else if ( !this.salainDB( r_new.getSala() ) ) {
			throw new ReservaException( SALA_INEXISTENTE );
		} else if ( !r.getData().equals( r_new.getData() )
				|| !r.getHora().equals( r_new.getHora() ) ) {
			if ( this.salainReservaDB( r_new.getSala(), r_new.getData(),
					r_new.getHora() ) ) {
				throw new ReservaException( SALA_INDISPONIVEL );
			}
		}
		if ( this.dataPassou( r_new.getData() ) ) {
			throw new ReservaException( DATA_JA_PASSOU );
		}
		if ( this.horaPassou( r_new.getHora() ) && this.dataIgual( r_new.getData() ) ) {
			throw new ReservaException( HORA_JA_PASSOU );
		} else {
			super.updateQuery( this.update( r,r_new ) );
		}
	}

	/**
	 * This methods excludes a booking from the database
	 * @param r: booking object
	 * @throws ReservaException
	 * @throws SQLException
	 */
	public void excluir( ReservaSalaProfessor r ) throws ReservaException,
			SQLException {
		if ( r == null ) {
			throw new ReservaException( NULA );
		} else if ( !this.reservainDB( r ) ) {
			throw new ReservaException( RESERVA_INEXISTENTE );
		} else {
			super.executeQuery( this.delete_from_professor( r ) );
		}
	}

	/**
	 * This method searches a teachers reservation into the database
	 * @return: arraylist with the bookings found
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonioException
	 * @throws ReservaException
	 */
	@SuppressWarnings( "unchecked" )
	public Vector<ReservaSalaProfessor> buscarTodos() throws SQLException,
			ClienteException, PatrimonioException, ReservaException {
		return super
				.buscar( "SELECT * FROM reserva_sala_professor "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+ "INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor;" );
	}

	/**
	 * This method searches a teacher booking by date
	 * @param data: date
	 * @return: arraylist with the bookings found
	 * @throws SQLException
	 * @throws ClienteException
	 * @throws PatrimonioException
	 * @throws ReservaException
	 */
	@SuppressWarnings( "unchecked" )
	public Vector<ReservaSalaProfessor> buscarPorData( String data )
			throws SQLException, ClienteException, PatrimonioException,
			ReservaException {
		return super
				.buscar( "SELECT * FROM reserva_sala_professor "
						+ "INNER JOIN sala ON sala.id_sala = reserva_sala_professor.id_sala "
						+ "INNER JOIN professor ON professor.id_professor = reserva_sala_professor.id_professor"
						+ " WHERE data = \"" + this.padronizarData(data)
						+ "\";" );
	}

	/**
	 * This method fetches a teacher into the database and inserts the information collected
	 * into an object
	 */
	@Override
	protected Object fetch( ResultSet rs ) throws SQLException, ClienteException,
			PatrimonioException, ReservaException {
		Professor p = new Professor( rs.getString( "nome" ), rs.getString( "cpf" ),
				rs.getString( "matricula" ), rs.getString( "telefone" ),
				rs.getString( "email" ) );

		Sala s = new Sala(rs.getString( "codigo" ), rs.getString( "descricao" ),
				rs.getString( "capacidade" ) );

		ReservaSalaProfessor r = new ReservaSalaProfessor( rs.getString( "data" ),
				rs.getString( "hora" ), s, rs.getString( "finalidade" ), p );

		return r;
	}

	/**
	 * This method verifies the complete status of the tearcher search into the database
	 * @param professor: teacher to be searched
	 * @return: boolean status
	 * @throws SQLException
	 */
	private boolean professorinDB( Professor professor ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM professor WHERE "
				+ "professor.nome = \"" + professor.getNome() + "\" and "
				+ "professor.cpf = \"" + professor.getCpf() + "\" and "
				+ "professor.telefone = \"" + professor.getTelefone()
				+ "\" and " + "professor.email = \"" + professor.getEmail()
				+ "\" and " + "professor.matricula = \""
				+ professor.getMatricula() + "\";" );
	}

	/**
	 * This method verifies the complete status of the room search into the database
	 * @param sala
	 * @return boolean status
	 * @throws SQLException
	 */
	private boolean salainDB( Sala sala ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + sala.getCodigo() + "\" and "
				+ "sala.descricao = \"" + sala.getDescricao() + "\" and "
				+ "sala.capacidade = " + sala.getCapacidade() + ";" );
	}

	/**
	 * This method verifies the complete status of the booking search into the database
	 * @param sala: indicates a room for the booking
	 * @param data: indicates a date for the booking
	 * @param hora: indicates a time for the booking
	 * @return: boolean with the status (exists or not)
	 * @throws SQLException
	 */
	private boolean salainReservaDB( Sala sala, String data, String hora )
			throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "data = \"" + data + "\" and " + "hora = \"" + hora
				+ "\" and " + "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \"" + sala.getCodigo() + "\" and "
				+ "sala.descricao = \"" + sala.getDescricao() + "\" and "
				+ "sala.capacidade = " + sala.getCapacidade() + " );" );
	}

	/**
	 * This method verifies the complete status of the teacher room booking search into the database
	 * @param r: indicates a booking
	 * @return boolean with the status (exists or not)
	 * @throws SQLException
	 */
	private boolean reservainDB( ReservaSalaProfessor r ) throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "id_professor = (SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \""
				+ r.getProfessor().getNome()
				+ "\" and "
				+ "professor.cpf = \""
				+ r.getProfessor().getCpf()
				+ "\" and "
				+ "professor.telefone = \""
				+ r.getProfessor().getTelefone()
				+ "\" and "
				+ "professor.email = \""
				+ r.getProfessor().getEmail()
				+ "\" and "
				+ "professor.matricula = \""
				+ r.getProfessor().getMatricula()
				+ "\") and "
				+ "id_sala = (SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \""
				+ r.getSala().getCodigo()
				+ "\" and "
				+ "sala.descricao = \""
				+ r.getSala().getDescricao()
				+ "\" and "
				+ "sala.capacidade = "
				+ r.getSala().getCapacidade()
				+ " ) and "
				+ "finalidade = \""
				+ r.getFinalidade()
				+ "\" and "
				+ "hora = \""
				+ r.getHora()
				+ "\" and " + "data = \"" + r.getData() + "\";" );
	}

	/**
	 * This method verifies the complete status of the  search into the database
	 * @param data
	 * @param hora
	 * @return
	 * @throws SQLException
	 */
	private boolean alunoinReservaDB( String data, String hora )
			throws SQLException {
		return super.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
				+ "data = \"" + data + "\" and " + "hora = \"" + hora + "\";" );
	}

	/**
	 * This method returns the actual date
	 * @return a date
	 */
	private String dataAtual() {
		Date date = new Date( System.currentTimeMillis() );
		SimpleDateFormat formatador = new SimpleDateFormat( "dd/MM/yyyy" );
		return formatador.format( date );
	}

	/**
	 * This method returns the actual time
	 * @return a time
	 */
	private String horaAtual() {
		Date date = new Date( System.currentTimeMillis() );
		return date.toString().substring( 11, 16 );
	}

	/**
	 * This method indicates if the actual date already passed
	 * @param d
	 * @return
	 */
	private boolean dataPassou( String d ) {
		String agora[] = this.dataAtual().split( "[./-]" );
		String data[] = d.split( "[./-]" );

		int dif = agora[2].length() - data[2].length();
		data[2] = agora[2].substring(0, dif) + data[2];

		if ( Integer.parseInt( agora[2] ) > Integer.parseInt( data[2] ) ) {
			return true;
		}

		dif = agora[1].length() - data[1].length();
		data[1] = agora[1].substring( 0, dif ) + data[1];

		if ( Integer.parseInt( agora[1] ) > Integer.parseInt( data[1] ) ) {
			return true;
		} else if ( Integer.parseInt( agora[1] ) == Integer.parseInt( data[1] ) ) {
			dif = agora[0].length() - data[0].length();
			data[0] = agora[0].substring( 0, dif ) + data[0];

			if ( Integer.parseInt(agora[0] ) > Integer.parseInt( data[0] ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method indicates if the informed dates are equal
	 * @param d
	 * @return
	 */
	public boolean dataIgual( String d ) {
		d = this.padronizarData( d );
		String agora[] = this.dataAtual().split( "[./-]" );
		String data[] = d.split( "[./-]" );

		if ( agora[0].equals( data[0] ) && agora[1].equals( data[1] )
				&& agora[2].equals( data[2] ) ) {
			return true;
		}
		return false;
	}

	/**
	 * This method indicates if the time already passed
	 * @param hora
	 * @return
	 */
	private boolean horaPassou( String hora ) {
		String agora = this.horaAtual();
		if ( hora.length() == 4 ) {
			hora = "0" + hora;
		}
		if ( Integer.parseInt( agora.substring( 0, 2 ) ) > Integer.parseInt( hora
				.substring( 0, 2 ) ) ) {
			return true;
		} else if ( Integer.parseInt( agora.substring( 0, 2 ) ) == Integer
				.parseInt( hora.substring( 0, 2 ) ) ) {
			if ( Integer.parseInt( agora.substring( 3, 5 ) ) > Integer.parseInt(hora
					.substring( 3, 5 ) ) ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * This method puts the date into the pattern
	 * @param data
	 * @return
	 */
	private String padronizarData( String data ) {
		String agora[] = dataAtual().split( "[./-]" );
		String partes[] = data.split( "[./-]" );
		String dataNoPadrao = "";

		for ( int i = 0; i < 3; i++ ) {
			if ( i == 0 ) {
				dataNoPadrao += agora[i].substring(0, agora[i].length()
						- partes[i].length())
						+ partes[i];
			} else {
				dataNoPadrao += "/"
						+ agora[i].substring(0,
								agora[i].length() - partes[i].length())
						+ partes[i];
			}

		}

		return dataNoPadrao;
	}
	/*
	 * private String padronizarHora(String hora){ if(hora.length() == 4) return
	 * "0" + hora; return hora; }
	 */
}