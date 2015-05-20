package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Student;
import model.StudentRoomReserve;
import model.Room;
import persistence.StudentRoomReserveDAO;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

//Importing Log4J2 classes 
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MaintainClassroomReservationByStudent {

	private Vector < StudentRoomReserve > reserve_vet = new Vector < StudentRoomReserve >();

	static final Logger logger = LogManager.getLogger( MaintainTeacher.class.getName() );
	
	
	//Singleton
	private static MaintainClassroomReservationByStudent instance;

	private MaintainClassroomReservationByStudent() {
	}

	
	/**
	 * Creates an instance of a classroom reserve for a student if it isn't already instantiated.
	 * @return - MaintainClassroomReservationByStudent - Classroom reserve for a student
	 */
	public static MaintainClassroomReservationByStudent getInstance() {
		if ( instance == null ) {
			logger.trace( "There is any classroom reserve for a student");
			instance = new MaintainClassroomReservationByStudent();
			logger.trace( "A new classroom reserve for a student is instantiated" );
		}
		return instance;
	}
	
	
	/**
     * Captures the classroom reserves for students in the date searched.
     * @return Vector - Classroom reserves
     */	
	public Vector < StudentRoomReserve > getRoomReservesByHour( String hour ) throws SQLException, PatrimonyException, ClientException, ReserveException {
		return StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByHour(hour);
	}
	
	
	/**
     * Captures the classroom reserves for teachers in the date searched.
     * @return Vector - Classroom reserves
     */	
	public Vector < StudentRoomReserve > getRoomReservesByDate( String date ) throws SQLException, PatrimonyException, ClientException, ReserveException {
		return StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByDay( date );
	}
	
	
	/**
     * Lists all the classroom reserves for student.
     * @return Vector - Classroom reserves for student
     */
	public Vector < StudentRoomReserve > getRoomReserves() throws SQLException, PatrimonyException, ClientException, ReserveException {
		this.reserve_vet = StudentRoomReserveDAO.getInstance().getAllStudentReservedRooms();
		return this.reserve_vet;
	}

	/**
	 * Captures the number of chairs available in a reserved classroom in a given date and hour
	 * @return int - Available chairs in the classroom reserved 
	 */
	public int captureAvailableChairs( Room room, String date, String hour ) throws SQLException, PatrimonyException, ClientException, ReserveException {
		return StudentRoomReserveDAO.getInstance().getAvailableChairs( room, date, hour );
	}

	
	/**
     * Reserves a classroom to a student.
     */
	public void insertReserve( Room room, Student student,
		String date, String hour, String finality, String reservedChairs )
		throws SQLException, ReserveException, ClientException, PatrimonyException {

		StudentRoomReserve r = new StudentRoomReserve( date, hour, room, finality, reservedChairs, student );
		StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(r);
		this.reserve_vet.add(r);
	}

	
	/**
     * Changes the classroom reserve to a new one.
     */
	public void changeReserve( String finality, String reservedChairs, StudentRoomReserve r )
		throws SQLException, ReserveException, ClientException, PatrimonyException {

		StudentRoomReserve res_old = new StudentRoomReserve( r.getDate(), r.getHour(), r.getRoom(),
			r.getFinality(), r.getReservedChairs(), r.getStudent());
		r.setFinality( finality );
		r.setReservedChairs( reservedChairs );
		StudentRoomReserveDAO.getInstance().updateStudentRoomReserve( res_old, r );
	}

	
	/**
     * Excludes a classroom reserve for a student.
     */
	public void excludeRoom( StudentRoomReserve r ) throws SQLException, ReserveException {
		StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom( r );
		this.reserve_vet.remove( r );
	}
}
