package control;

import java.sql.SQLException;
import java.util.Vector;
import persistence.EquipamentDAO;
import exception.PatrimonyException;
import model.Equipamento;

public class MaintainEquipment {

	private Vector <Equipamento> Equipamento_vet = new Vector <Equipamento>();
	
	//Singleton
	private static MaintainEquipment instance;
	private MaintainEquipment() {
		
	}
	
	/**
	 * Creates an instance of an equipment if it isn't already instantiated.
	 * @return Equipamento - An equipment
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
	public Vector<Equipamento> getEquipments() throws SQLException, PatrimonyException {
		this.Equipamento_vet = EquipamentDAO.getInstance().buscarTodos();
		return this.Equipamento_vet;
	}

	
	/**
	 * Inserts a new equipment and its attributes
	 */
	public void insertEquipment(String codigo, String descricao) throws PatrimonyException, SQLException {
		Equipamento equipamento = new Equipamento(codigo, descricao);
		EquipamentDAO.getInstance().incluir(equipamento);
		getEquipments();
	}

	
	/**
	 * Changes an equipment's attributes
	 */
	public void changeEquipment(String codigo, String descricao, Equipamento equipamento) throws PatrimonyException, SQLException {
		if (equipamento == null) {
			throw new PatrimonyException("Equipamento em branco");
		}
		Equipamento old_equipamento = new Equipamento(equipamento.getCodigo(), equipamento.getDescricao());
		equipamento.setCodigo(codigo);
		equipamento.setDescricao(descricao);
		EquipamentDAO.getInstance().changeRoomReserve(old_equipamento, equipamento);
		getEquipments();
	}

	
	/**
	 * Excludes an equipment from the database
	 * @return void
	 */
	public void excludeEquipment(Equipamento equipamento) throws SQLException, PatrimonyException {
		if (equipamento == null) {
			throw new PatrimonyException("Equipamento em branco");
		}
		EquipamentDAO.getInstance().excludeRoom(equipamento);
		getEquipments();
	}
}
