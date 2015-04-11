package control;

import java.sql.SQLException;
import java.util.Vector;
import persistence.EquipamentoDAO;
import exception.PatrimonyException;
import model.Equipamento;

public class ManterEquipamento {

	private Vector <Equipamento> Equipamento_vet = new Vector <Equipamento>();
	
	//Singleton
	private static ManterEquipamento instance;
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
	public Vector<Equipamento> getEquipamento_vet() throws SQLException, PatrimonyException {
		this.Equipamento_vet = EquipamentoDAO.getInstance().buscarTodos();
		return this.Equipamento_vet;
	}

	
	/**
	 * Inserts a new equipment and its attributes
	 */
	public void inserir(String codigo, String descricao) throws PatrimonyException, SQLException {
		Equipamento equipamento = new Equipamento(codigo, descricao);
		EquipamentoDAO.getInstance().incluir(equipamento);
		getEquipamento_vet();
	}

	
	/**
	 * Changes an equipment's attributes
	 */
	public void alterar(String codigo, String descricao, Equipamento equipamento) throws PatrimonyException, SQLException {
		if (equipamento == null) {
			throw new PatrimonyException("Equipamento em branco");
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
	public void excluir(Equipamento equipamento) throws SQLException, PatrimonyException {
		if (equipamento == null) {
			throw new PatrimonyException("Equipamento em branco");
		}
		EquipamentoDAO.getInstance().excluir(equipamento);
		getEquipamento_vet();
	}
}
