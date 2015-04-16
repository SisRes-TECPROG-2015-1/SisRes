package control;

import java.sql.SQLException;
import java.util.Vector;
import persistence.EquipamentoDAO;
import exception.PatrimonyException;
import model.Equipment;

public class MaintainEquipment {

	private Vector <Equipment> Equipment_vet = new Vector <Equipment>();
	
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
			instance = new MaintainEquipment();
		}
		return instance;
	}


	/**
	 * Captures all the equipments in the database
	 * @return Vector - All equipments
	 */	
	public Vector <Equipment> getEquipments() throws SQLException, PatrimonyException {
		this.Equipment_vet = EquipamentoDAO.getInstance().searchForAll();
		return this.Equipment_vet;
	}
	
	/**
	 * Inserts a new equipment and its attributes
	 */
	public void insertNewEquipment (String code, String description) throws PatrimonyException, SQLException {
		Equipment equipment = new Equipment ( code, description );
		EquipamentoDAO.getInstance().includeReserve( equipment );
		getEquipamento_vet();
	}

	
	/**
	 * Changes an equipment's attributes
	 */
	public void changeEquipmentsAttributes(String code, String description, Equipment equipment) throws PatrimonyException, SQLException {
		if (equipment == null) {
			throw new PatrimonyException("Equipamento em branco");
		}
		Equipment old_equipment = new Equipment (equipment.getCode(), equipment.getDescription());
		equipment.setCode(code);
		equipment.setDescription(description);
		EquipamentoDAO.getInstance().changeClassroomReserve(old_equipment, equipment);
		getEquipamento_vet();
	}

	
	/**
	 * Excludes an equipment from the database
	 * @return void
	 */
	public void excludeEquipment(Equipment equipment) throws SQLException, PatrimonyException {
		if (equipment == null) {
			throw new PatrimonyException("Equipamento em branco");
		}
		EquipamentoDAO.getInstance().excludeEquipment(equipment);
		getEquipamento_vet();
	}
}
