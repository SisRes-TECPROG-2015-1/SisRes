package control;

import java.sql.SQLException;
import java.util.Vector;
import persistence.EquipamentoDAO;
import exception.PatrimonioException;
import model.Equipamento;

/**
 * Equipment and rooms reservation system
 * ManterEquipamento class with methods to accomplish CRUD equipments
 */
public class ManterEquipamento {
	
	/**
	 * Creating a vector of equipments
	 */
	private Vector <Equipamento> Equipamento_vet = new Vector <Equipamento>();
	
	//This attribute is an instance of a ManterEquipamento type
	private static ManterEquipamento instance;
	
	/**
	 * Empty constructor
	 */
	private ManterEquipamento() {
		
	}
	
	/**
	 * Creates an instance of an equipment if it isn't already instantiated.
	 * @return Equipamento - An equipment
	 */
	public static ManterEquipamento getInstance() {
		if ( instance == null ) {
			instance = new ManterEquipamento();
		}
		return instance;
	}

	
	/**
	 * Captures all the equipments in the database
	 * @return Vector - All equipments
	 */	
	public Vector<Equipamento> getEquipamento_vet() throws SQLException, PatrimonioException {
		this.Equipamento_vet = EquipamentoDAO.getInstance().buscarTodos();
		return this.Equipamento_vet;
	}

	
	/**
	 * Inserts a new equipment and its attributes
	 * Parameters codigo and descricao of string type used to get information about the code and description from EquipamentoDao class
	 */
	public void inserir(String codigo, String descricao) throws PatrimonioException, SQLException {
		Equipamento equipamento = new Equipamento(codigo, descricao);
		EquipamentoDAO.getInstance().incluir(equipamento);
		getEquipamento_vet();
	}

	
	/**
	 * Changes an equipment's attributes
	 */
	public void alterar(String codigo, String descricao, Equipamento equipamento) throws PatrimonioException, SQLException {
		if (equipamento == null) {
			throw new PatrimonioException("Equipamento em branco");
		}
		Equipamento old_equipamento = new Equipamento(equipamento.getCodigo(), equipamento.getDescricao());
		equipamento.setCodigo(codigo);
		equipamento.setDescricao(descricao);
		EquipamentoDAO.getInstance().alterar(old_equipamento, equipamento);
		getEquipamento_vet();
	}

	
	/**
	 * Excludes an equipment from the database
	 * @return void
	 */
	public void excluir(Equipamento equipamento) throws SQLException, PatrimonioException {
		if (equipamento == null) {
			throw new PatrimonioException("Equipamento em branco");
		}
		EquipamentoDAO.getInstance().excluir(equipamento);
		getEquipamento_vet();
	}
}
