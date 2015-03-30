package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.ResSalaProfessorDAO;

import model.Professor;
import model.ReservaSalaProfessor;
import model.Sala;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

public class ManterResSalaProfessor {
	private Vector<ReservaSalaProfessor> rev_sala_professor_vet = new Vector<ReservaSalaProfessor>();
	
	//Singleton
	private static ManterResSalaProfessor instance;
	
	private ManterResSalaProfessor() {
	}
	
	/**
	 * Creates an instance of a classroom reserve for a teacher if it isn't already instantiated.
	 * @return - ManterResSalaProfessor - Classroom reserve
	 */
	public static ManterResSalaProfessor getInstance() {
		if ( instance == null ) {
			instance = new ManterResSalaProfessor();
		}
			return instance;
	}
	
	
	/**
     * Captures the classroom reserves for teachers in the date searched.
     * @return Vector - Classroom reserves
     */	
	public Vector < ReservaSalaProfessor > buscarPorData( String data ) throws SQLException, ClienteException, PatrimonioException, ReservaException {
		return ResSalaProfessorDAO.getInstance().buscarPorData( data );
	} 
	    	
		
	 /**
     * Lists all the classroom reserves for teachers.
     * @return Vector - Classroom reserves for teachers
     */
	public Vector < ReservaSalaProfessor > getResProfessorSala_vet() throws SQLException, ClienteException, PatrimonioException, ReservaException {
		this.rev_sala_professor_vet = ResSalaProfessorDAO.getInstance().buscarTodos();
		return this.rev_sala_professor_vet;
	}

	
	/**
     * Reserves an classroom to a teacher.
     */
	public void inserir( Sala sala, Professor prof,
						String data, String hora, String finalidade ) 
					throws SQLException, ReservaException {

		ReservaSalaProfessor reserva = new ReservaSalaProfessor( data, hora, sala , finalidade, prof );
		ResSalaProfessorDAO.getInstance().incluir( reserva );
		this.rev_sala_professor_vet.add( reserva );
	}

	
	/**
     * Changes the classroom reserve to a new one.
     */
	public void alterar( String finalidade, ReservaSalaProfessor reserva ) 
				throws SQLException, ReservaException {
		
		ReservaSalaProfessor reserva_old = new ReservaSalaProfessor( reserva.getData(), reserva.getHora(), reserva.getSala() , 
				reserva.getFinalidade(), reserva.getProfessor() );
		
		reserva.setFinalidade( finalidade );
		ResSalaProfessorDAO.getInstance().alterar( reserva_old, reserva );
		
	}

	/**
     * Excludes a classroom reserve for a teacher.
     */
	public void excluir( ReservaSalaProfessor reserva ) throws SQLException, ReservaException {
		ResSalaProfessorDAO.getInstance().excluir( reserva );
		this.rev_sala_professor_vet.remove( reserva );
	}

}