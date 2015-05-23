package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Room;
import model.Teacher;
import model.TeacherRoomReserve;
import persistence.TeacherRoomReserveDAO;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;


//Importing Log4J2 classes 
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MaintainClassroomReservationByTeacher {
	private Vector<TeacherRoomReserve> roomReserve = new Vector<TeacherRoomReserve>();
	
	static final Logger logger = LogManager.getLogger( MaintainTeacher.class.getName() );
	
	//Singleton
	private static MaintainClassroomReservationByTeacher instance;
	
	private MaintainClassroomReservationByTeacher() {
	}
	
	/**
	 * Creates an instance of a classroom reserve for a teacher if it isn't already instantiated.
	 * @return - ManterResSalaProfessor - Classroom reserve
	 */
	public static MaintainClassroomReservationByTeacher getInstance() {
		if ( instance == null ) {
			logger.trace( "There is any instance of classroom reserve for a teacher");
			instance = new MaintainClassroomReservationByTeacher();
			logger.trace( "A new classroom reserve is just instantiated" );
		}
			return instance;
	}
	
	
	/**
     * Captures the classroom reserves for teachers in the date searched.
     * @return Vector - Classroom reserves
     */	
	public Vector < TeacherRoomReserve > getRoomByDate( String date ) throws SQLException, ClientException, PatrimonyException, ReserveException {
		return TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay( date );
	} 
	    	
		
	 /**
     * Lists all the classroom reserves for teachers.
     * @return Vector - Classroom reserves for teachers
     */
	public Vector < TeacherRoomReserve > getRoomsReserves() throws SQLException, ClientException, PatrimonyException, ReserveException {
		this.roomReserve = TeacherRoomReserveDAO.getInstance().getAllTeacherReservedRooms();
		return this.roomReserve;
	}

	
	/**
     * Reserves an classroom to a teacher.
     */
	public void insertReserve( Room room, Teacher teacher,
						String date, String hour, String finality ) 
					throws SQLException, ReserveException {

		TeacherRoomReserve reserva = new TeacherRoomReserve( date, hour, room , finality, teacher );
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve( reserva );
		this.roomReserve.add( reserva );
		logger.trace( "A new reserve was inserted");
	}

	
	/**
     * Changes the classroom reserve to a new one.
     */
	public void changeRoomReserve( String finality, TeacherRoomReserve reserve ) 
				throws SQLException, ReserveException {
		
		TeacherRoomReserve old_reserve = new TeacherRoomReserve( reserve.getDate(), reserve.getHour(), reserve.getRoom() , 
				reserve.getFinality(), reserve.getTeacher() );
		
		reserve.setFinality( finality );
		TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve( old_reserve, reserve );
		logger.trace( "The reserve had its atribbutes changed succesfully");
		
	}

	/**
     * Excludes a classroom reserve for a teacher.
     */
	public void excludeReserve( TeacherRoomReserve reserve ) throws SQLException, ReserveException {
		logger.trace( "Asking for a reserve exclusion");
		TeacherRoomReserveDAO.getInstance().deleteTeacherReservedRoom( reserve );
		this.roomReserve.remove( reserve );
	}

}