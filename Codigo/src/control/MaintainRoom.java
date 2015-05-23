package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.ClassRoomDAO;
import exception.PatrimonyException;
import model.Room;

//Importing Log4J2 classes 
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MaintainRoom {

	private Vector < Room > room = new Vector < Room >();
	
	static final Logger logger = LogManager.getLogger( MaintainTeacher.class.getName() );
	
	//Singleton
	private static MaintainRoom instance;
	
	private MaintainRoom() {
	}
	
	
	/**
	 * Creates an instance of a classroom if it isn't already instantiated.
	 * @return ManterSala - A classroom
	 */	
	public static MaintainRoom getInstance() {
		if ( instance == null ){
			logger.trace( "There is any instance of classrom");
			instance = new MaintainRoom();
			logger.trace( "A new classroom is just instantiated" );
		}
		return instance;
	}
	
	
	/**
	 * Captures all the classrooms in the database.
	 * @return Vector - All classrooms
	 */	
	public Vector < Room > getRooms() throws SQLException, PatrimonyException {
		this.room = ClassRoomDAO.getInstance().searchAll();
		return this.room;
	}

	
	/**
	 * Inserts a new classroom and its attributes.
	 */
	public void insertRooms( String code, String description, String capacity ) throws PatrimonyException, SQLException {
		Room room = new Room( code, description, capacity );
		ClassRoomDAO.getInstance().includeARoom( room );
		this.room.add( room );
		logger.trace( "A new room was inserted");
	}


	/**
	 * Changes a classroom attributes.
	 */
	public void changeRoom( String code, String description, String capacity, Room room) throws PatrimonyException, SQLException {
		Room old_room = new Room( room.getCode(), room.getDescription(),
								room.getCapacity());
		room.setCode( code );
		room.setDescription( description );
		room.setCapacity( capacity );
		ClassRoomDAO.getInstance().modifyRoom( old_room, room );
		logger.trace( "The room" + room.getCode() + " had its atribbutes changed succesfully");
	}

	
	/**
	 * Excludes a classroom from the database.
	 */
	public void excludeRoom( Room room ) throws SQLException, PatrimonyException {
		logger.trace( "Asking for a room exclusion");
		ClassRoomDAO.getInstance().excludeRoom( room );
		this.room.remove( room );
	}

}
