package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Aluno;
import model.StudentRoomReserve;
import model.Sala;
import persistence.StudentRoomReserveDAO;
import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

public class MaintainClassroomReservationByStudent {

	private Vector<StudentRoomReserve> rev_sala_aluno_vet = new Vector<StudentRoomReserve>();
	//Singleton
	private static MaintainClassroomReservationByStudent instance;

	private MaintainClassroomReservationByStudent() {
	}

	
	/**
	 * Creates an instance of a classroom reserve for a student if it isn't already instantiated.
	 * @return - ManterResSalaAluno - Classroom reserve for a student
	 */
	public static MaintainClassroomReservationByStudent getInstance() {
		if ( instance == null ) {
			instance = new MaintainClassroomReservationByStudent();
	}
		return instance;
	}
	
	
	/**
     * Captures the classroom reserves for students in the date searched.
     * @return Vector - Classroom reserves
     */	
	public Vector < StudentRoomReserve > getReservasHora( String hora ) throws SQLException, PatrimonyException, ClienteException, ReserveException {
		return StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByHour(hora);
	}
	
	
	/**
     * Captures the classroom reserves for teachers in the date searched.
     * @return Vector - Classroom reserves
     */	
	public Vector < StudentRoomReserve > getReservasMes( String data ) throws SQLException, PatrimonyException, ClienteException, ReserveException {
		return StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByDay( data );
	}
	
	
	/**
     * Lists all the classroom reserves for student.
     * @return Vector - Classroom reserves for student
     */
	public Vector < StudentRoomReserve > getResAlunoSala_vet() throws SQLException, PatrimonyException, ClienteException, ReserveException {
		this.rev_sala_aluno_vet = StudentRoomReserveDAO.getInstance().getAllStudentReservedRooms();
		return this.rev_sala_aluno_vet;
	}

	/**
	 * Captures the number of chairs available in a reserved classroom in a given date and hour
	 * @return int - Available chairs in the classroom reserved 
	 */
	public int cadeirasDisponveis( Sala sala, String data, String hora ) throws SQLException, PatrimonyException, ClienteException, ReserveException {
		return StudentRoomReserveDAO.getInstance().getAvailableChairs( sala, data, hora );
	}

	
	/**
     * Reserves a classroom to a student.
     */
	public void inserir( Sala sala, Aluno aluno,
		String data, String hora, String finalidade, String cadeiras_reservadas )
		throws SQLException, ReserveException, ClienteException, PatrimonyException {

		StudentRoomReserve r = new StudentRoomReserve( data, hora, sala, finalidade, cadeiras_reservadas, aluno );
		StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(r);
		this.rev_sala_aluno_vet.add(r);
	}

	
	/**
     * Changes the classroom reserve to a new one.
     */
	public void alterar( String finalidade, String cadeiras_reservadas, StudentRoomReserve r )
		throws SQLException, ReserveException, ClienteException, PatrimonyException {

		StudentRoomReserve res_old = new StudentRoomReserve( r.getData(), r.getHora(), r.getSala(),
			r.getFinality(), r.getReservedChairs(), r.getAluno());
		r.setFinality( finalidade );
		r.setReservedChairs( cadeiras_reservadas );
		StudentRoomReserveDAO.getInstance().updateStudentRoomReserve( res_old, r );
	}

	
	/**
     * Excludes a classroom reserve for a student.
     */
	public void excluir( StudentRoomReserve r ) throws SQLException, ReserveException {
		StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom( r );
		this.rev_sala_aluno_vet.remove( r );
	}
}
