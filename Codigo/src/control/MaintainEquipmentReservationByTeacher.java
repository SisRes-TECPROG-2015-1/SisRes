package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Equipment;
import model.Teacher;
import model.TeacherEquipmentReserve;
import persistence.ReserveEquipmentTeacherDAO;
import exception.ClientException;
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
	 * @return - MaintainEquipmentReservationByTeacher - Equipment reserve
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
    public Vector < TeacherEquipmentReserve > getClassroomReserves( String time ) throws SQLException, PatrimonyException,
            ClientException, ReserveException {
        return ReserveEquipmentTeacherDAO.getInstance().buscarPorHora( time );
    }

    /**
     * Captures the classroom reserves for teachers which had reserved a equipment in the month searched.
     * @return Vector - Classroom reserves for teachers
     */
    public Vector < TeacherEquipmentReserve > getClassroomReservesByMonth( int month ) throws SQLException, PatrimonyException, ClientException,
            ReserveException {
        return ReserveEquipmentTeacherDAO.getInstance().buscarPorMes( month );
    }

    
    /**
     * Lists all the classroom reserves for teachers.
     * @return Vector - Classroom reserves for teachers
     */
    public Vector < Object > getReserves () throws SQLException, ClientException, PatrimonyException,
            ReserveException {
        this.rev_equipamento_professor_vet = ReserveEquipmentTeacherDAO.getInstance().buscarTodos();
        return this.rev_equipamento_professor_vet;
    }

    
    /**
     * Reserves an equipment to a teacher to a date and hour.
     */
    public void insertNewEquipment( Equipment equipment, Teacher teacher, String date, String time ) throws SQLException, ReserveException {
        TeacherEquipmentReserve reserve = new TeacherEquipmentReserve( date, time, equipment, teacher );
        ReserveEquipmentTeacherDAO.getInstance().includeReserve( reserve );
        this.rev_equipamento_professor_vet.add( reserve );
    }

    
    /**
     * Changes the classroom reserve to a new one.
     */
    public void changeClassroomReserve( String finality, TeacherEquipmentReserve reserve ) throws SQLException, ReserveException {

        TeacherEquipmentReserve reserva_old = new TeacherEquipmentReserve( reserve.getDate(), reserve.getHour(),
                reserve.getEquipment(), reserve.getProfessor());
        ReserveEquipmentTeacherDAO.getInstance().alterar( reserva_old, reserve );

    }

    
    /**
     * Excludes a classroom reserve.
     */
    public void excludeClassroomReserve( TeacherEquipmentReserve reserve ) throws SQLException, ReserveException {
        ReserveEquipmentTeacherDAO.getInstance().excludeReservation( reserve );
        this.rev_equipamento_professor_vet.remove( reserve );
    }

}
