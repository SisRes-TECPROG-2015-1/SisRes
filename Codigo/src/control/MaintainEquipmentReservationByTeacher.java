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

//Importing Log4J2 classes 
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MaintainEquipmentReservationByTeacher {
	
    private Vector < Object > rev_equipamento_professor_vet = new Vector < Object >();

    static final Logger logger = LogManager.getLogger( MaintainTeacher.class.getName() );
    
    // Singleton
    private static MaintainEquipmentReservationByTeacher instance;

    private MaintainEquipmentReservationByTeacher() {
    }

    
    /**
	 * Creates an instance of an equipment reserve for a teacher if it isn't already instantiated.
	 * @return - MaintainEquipmentReservationByTeacher - Equipment reserve
	 */
    public static MaintainEquipmentReservationByTeacher getInstance() {
        if ( instance == null ){
        	logger.trace( "There is any instance of equipment reserve for a teacher");
            instance = new MaintainEquipmentReservationByTeacher();
            logger.trace( "A new equipment reserve for a teacher is just instantiated" );
    	}
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
        logger.trace( "A new equipment to a teacher was inserted");
    }

    
    /**
     * Changes the classroom reserve to a new one.
     */
    public void changeClassroomReserve( String finality, TeacherEquipmentReserve reserve ) throws SQLException, ReserveException {

        TeacherEquipmentReserve reserva_old = new TeacherEquipmentReserve( reserve.getDate(), reserve.getHour(),
                reserve.getEquipment(), reserve.getProfessor());
        ReserveEquipmentTeacherDAO.getInstance().alterar( reserva_old, reserve );
        logger.trace( "The reserve had its atribbutes changed succesfully");

    }

    
    /**
     * Excludes a classroom reserve.
     */
    public void excludeClassroomReserve( TeacherEquipmentReserve reserve ) throws SQLException, ReserveException {
    	logger.trace( "Asking for a reserve exclusion");
        ReserveEquipmentTeacherDAO.getInstance().excludeReservation( reserve );
        this.rev_equipamento_professor_vet.remove( reserve );
    }

}
