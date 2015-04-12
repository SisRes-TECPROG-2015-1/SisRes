package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.ResSalaProfessorDAO;

import model.Professor;
import model.TeacherRoomReserve;
import model.Sala;
import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ManterResSalaProfessor {
	private Vector<TeacherRoomReserve> rev_sala_professor_vet = new Vector<TeacherRoomReserve>();
	
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
	public Vector < TeacherRoomReserve > buscarPorData( String data ) throws SQLException, ClienteException, PatrimonyException, ReserveException {
		return ResSalaProfessorDAO.getInstance().buscarPorData( data );
	} 
	    	
		
	 /**
     * Lists all the classroom reserves for teachers.
     * @return Vector - Classroom reserves for teachers
     */
	public Vector < TeacherRoomReserve > getResProfessorSala_vet() throws SQLException, ClienteException, PatrimonyException, ReserveException {
		this.rev_sala_professor_vet = ResSalaProfessorDAO.getInstance().buscarTodos();
		return this.rev_sala_professor_vet;
	}

	
	/**
     * Reserves an classroom to a teacher.
     */
	public void inserir( Sala sala, Professor prof,
						String data, String hora, String finalidade ) 
					throws SQLException, ReserveException {

		TeacherRoomReserve reserva = new TeacherRoomReserve( data, hora, sala , finalidade, prof );
		ResSalaProfessorDAO.getInstance().incluir( reserva );
		this.rev_sala_professor_vet.add( reserva );
	}

	
	/**
     * Changes the classroom reserve to a new one.
     */
	public void alterar( String finalidade, TeacherRoomReserve reserva ) 
				throws SQLException, ReserveException {
		
		TeacherRoomReserve reserva_old = new TeacherRoomReserve( reserva.getData(), reserva.getHora(), reserva.getSala() , 
				reserva.getFinality(), reserva.getProfessor() );
		
		reserva.setFinality( finalidade );
		ResSalaProfessorDAO.getInstance().alterar( reserva_old, reserva );
		
	}

	/**
     * Excludes a classroom reserve for a teacher.
     */
	public void excluir( TeacherRoomReserve reserva ) throws SQLException, ReserveException {
		ResSalaProfessorDAO.getInstance().excluir( reserva );
		this.rev_sala_professor_vet.remove( reserva );
	}

}