package control;

import java.sql.SQLException;
import java.util.Vector;
import persistence.EquipmentDAO;
import exception.PatrimonyException;
import model.Equipment;

//Importing Log4J2 classes 
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MaintainEquipment {

	private Vector <Equipment> Equipment_vet = new Vector <Equipment>();
	
	static final Logger logger = LogManager.getLogger( MaintainTeacher.class.getName() );
	
	//Singleton
	private static MaintainEquipment instance;
	private MaintainEquipment() {
		
	}
	
	/**
	 * Creates an instance of an equipment if it isn't already instantiated.
	 * @return Equipment - An equipment
	 */
	public static MaintainEquipment getInstance() {
		if ( instance == null ) {
			logger.trace( "There is any instance of an equipment" );
			instance = new MaintainEquipment();
			logger.trace( "A new equipment is just instantiated" );
		}else{
			//do nothing
		}
		
		return instance;
	}


	/**
	 * Captures all the equipments in the database
	 * @return Vector - All equipments
	 */	
	public Vector <Equipment> getEquipments() throws SQLException, PatrimonyException {
		this.Equipment_vet = EquipmentDAO.getInstance().searchForAll();
		return this.Equipment_vet;
	}
	
	/**
	 * Inserts a new equipment and its attributes
	 */
	public void insertNewEquipment (String code, String description) throws PatrimonyException, SQLException {
		Equipment equipment = new Equipment ( code, description );
		EquipmentDAO.getInstance().includeEquipment( equipment );
		getEquipments();
		logger.trace( "A new student was inserted");
	}

	
	/**
	 * Changes an equipment's attributes
	 */
	public void changeEquipmentsAttributes(String code, String description, Equipment equipment) throws PatrimonyException, SQLException {
		
		if (equipment == null) {
			throw new PatrimonyException("Equipamento em branco");
		}else{
			//do nothing
		}
		
		Equipment old_equipment = new Equipment (equipment.getCode(), equipment.getDescription());
		equipment.setCode(code);
		equipment.setDescription(description);
		EquipmentDAO.getInstance().modifyEquipment(old_equipment, equipment);
		logger.trace( "The equipment" + equipment.getCode()+" had its atribbutes changed succesfully");
		getEquipments();
	}

	
	/**
	 * Excludes an equipment from the database
	 * @return void
	 */
	public void excludeEquipment(Equipment equipment) throws SQLException, PatrimonyException {
		logger.trace( "Asking for an equipment exclusion");
		
		if (equipment == null) {
			throw new PatrimonyException("Equipamento em branco");
		}else{
			//do nothing
		}
		
		EquipmentDAO.getInstance().excludeEquipment(equipment);
		getEquipments();
	}
}
