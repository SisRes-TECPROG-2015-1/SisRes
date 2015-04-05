package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Aluno;
import model.ReservaSalaAluno;
import model.Sala;
import persistence.ResSalaAlunoDAO;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

/**
 * Equipment and rooms reservation system
 * ManterResSalaAluno class with methods to accomplish CRUD reserves of classroom by a student
 */
public class ManterResSalaAluno {

	/**
	 * Creating a vector of reserves of classroom by a student
	 */
	private Vector<ReservaSalaAluno> rev_sala_aluno_vet = new Vector<ReservaSalaAluno>();

	//This attribute is an instance of a ManterResSalaAluno type
	private static ManterResSalaAluno instance;

	/**
	 * Empty constructor
	 */
	private ManterResSalaAluno() {
	}

	
	/**
	 * Creates an instance of a classroom reserve for a student if it isn't already instantiated.
	 * @return - ManterResSalaAluno - Classroom reserve for a student
	 */
	public static ManterResSalaAluno getInstance() {
		if ( instance == null ) {
			instance = new ManterResSalaAluno();
	}
		return instance;
	}
	
	
	/**
     * Captures the classroom reserves for students in the date searched.
     * Parameter hora used to bring information about reserve's hour
     * @return Vector - Classroom reserves
     */	
	public Vector < ReservaSalaAluno > getReservasHora( String hora ) throws SQLException, PatrimonioException, ClienteException, ReservaException {
		return ResSalaAlunoDAO.getInstance().buscarPorHora(hora);
	}
	
	
	/**
     * Captures the classroom reserves for teachers in the date searched.
     * Parameter data used to bring information about reserve's data
     * @return Vector - Classroom reserves
     */	
	public Vector < ReservaSalaAluno > getReservasMes( String data ) throws SQLException, PatrimonioException, ClienteException, ReservaException {
		return ResSalaAlunoDAO.getInstance().buscarPorDia( data );
	}
	
	
	/**
     * Lists all the classroom reserves for student.
     * @return Vector - Classroom reserves for student
     */
	public Vector < ReservaSalaAluno > getResAlunoSala_vet() throws SQLException, PatrimonioException, ClienteException, ReservaException {
		this.rev_sala_aluno_vet = ResSalaAlunoDAO.getInstance().buscarTodos();
		return this.rev_sala_aluno_vet;
	}

	/**
	 * Captures the number of chairs available in a reserved classroom in a given date and hour
	 * Parameter sala, data and hora used to bring information about available chairs
	 * @return int - Available chairs in the classroom reserved 
	 */
	public int cadeirasDisponveis( Sala sala, String data, String hora ) throws SQLException, PatrimonioException, ClienteException, ReservaException {
		return ResSalaAlunoDAO.getInstance().cadeirasDisponiveis( sala, data, hora );
	}

	
	/**
     * Reserves a classroom to a student.
     * Parameter sala, aluno, data, hora, finalidade and cadeiras_reservadas used to bring information for 
     * create reserves of classroom by a student
     */
	public void inserir( Sala sala, Aluno aluno,
		String data, String hora, String finalidade, String cadeiras_reservadas )
		throws SQLException, ReservaException, ClienteException, PatrimonioException {

		ReservaSalaAluno r = new ReservaSalaAluno( data, hora, sala, finalidade, cadeiras_reservadas, aluno );
		ResSalaAlunoDAO.getInstance().incluir(r);
		this.rev_sala_aluno_vet.add(r);
	}

	
	/**
     * Changes the classroom reserve to a new one.
     * Parameter finalidade, cadeiras_reservadas used to update the reserve
     */
	public void alterar( String finalidade, String cadeiras_reservadas, ReservaSalaAluno r )
		throws SQLException, ReservaException, ClienteException, PatrimonioException {

		ReservaSalaAluno res_old = new ReservaSalaAluno( r.getData(), r.getHora(), r.getSala(),
			r.getFinalidade(), r.getCadeiras_reservadas(), r.getAluno());
		r.setFinalidade( finalidade );
		r.setCadeiras_reservadas( cadeiras_reservadas );
		ResSalaAlunoDAO.getInstance().alterar( res_old, r );
	}

	
	/**
     * Excludes a classroom reserve for a student.
     */
	public void excluir( ReservaSalaAluno r ) throws SQLException, ReservaException {
		ResSalaAlunoDAO.getInstance().excluir( r );
		this.rev_sala_aluno_vet.remove( r );
	}
}
