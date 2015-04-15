package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Equipamento;
import model.Teacher;
import model.TeacherEquipmentReserve;
import persistence.ResEquipamentoProfessorDAO;
import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

public class MaintainEquipmentReservationByTeacher {
	
    private Vector < Object > rev_equipamento_professor_vet = new Vector < Object >();

    // Singleton
    private static MaintainEquipmentReservationByTeacher instance;

    private MaintainEquipmentReservationByTeacher() {
    }

    
    /**
	 * Creates an instance of an equipment reserve for a teacher if it isn't already instantiated.
	 * @return - ManterResEquipamentoProfessor - Equipment reserve
	 */
    public static MaintainEquipmentReservationByTeacher getInstance() {
        if ( instance == null )
            instance = new MaintainEquipmentReservationByTeacher();
        return instance;
    }


    /**
     * Captures the classroom reserves for teachers in the hour searched.
     * @return Vector - Classroom reserves for teachers
     */
    public Vector < TeacherEquipmentReserve > getReservasHora( String hora ) throws SQLException, PatrimonyException,
            ClienteException, ReserveException {
        return ResEquipamentoProfessorDAO.getInstance().buscarPorHora( hora );
    }

    /**
     * Captures the classroom reserves for teachers which had reserved a equipment in the month searched.
     * @return Vector - Classroom reserves for teachers
     */
    public Vector < TeacherEquipmentReserve > getReservasMes( int mes ) throws SQLException, PatrimonyException, ClienteException,
            ReserveException {
        return ResEquipamentoProfessorDAO.getInstance().buscarPorMes( mes );
    }

    
    /**
     * Lists all the classroom reserves for teachers.
     * @return Vector - Classroom reserves for teachers
     */
    public Vector < Object > getResEquipamentoProfessor_vet() throws SQLException, ClienteException, PatrimonyException,
            ReserveException {
        this.rev_equipamento_professor_vet = ResEquipamentoProfessorDAO.getInstance().buscarTodos();
        return this.rev_equipamento_professor_vet;
    }

    
    /**
     * Reserves an equipment to a teacher to a date and hour.
     */
    public void inserir( Equipamento equipamento, Teacher prof, String data, String hora ) throws SQLException, ReserveException {
        TeacherEquipmentReserve reserva = new TeacherEquipmentReserve( data, hora, equipamento, prof );
        ResEquipamentoProfessorDAO.getInstance().incluir( reserva );
        this.rev_equipamento_professor_vet.add( reserva );
    }

    
    /**
     * Changes the classroom reserve to a new one.
     */
    public void alterar( String finalidade, TeacherEquipmentReserve reserva ) throws SQLException, ReserveException {

        TeacherEquipmentReserve reserva_old = new TeacherEquipmentReserve( reserva.getData(), reserva.getHora(),
                reserva.getEquipment(), reserva.getProfessor());
        ResEquipamentoProfessorDAO.getInstance().alterar( reserva_old, reserva );

    }

    
    /**
     * Excludes a classroom reserve.
     */
    public void excluir( TeacherEquipmentReserve reserva ) throws SQLException, ReserveException {
        ResEquipamentoProfessorDAO.getInstance().excluir( reserva );
        this.rev_equipamento_professor_vet.remove( reserva );
    }

}
