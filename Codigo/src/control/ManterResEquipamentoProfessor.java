package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Equipamento;
import model.Professor;
import model.ReservaEquipamentoProfessor;
import persistence.ResEquipamentoProfessorDAO;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

public class ManterResEquipamentoProfessor {
	
    private Vector < Object > rev_equipamento_professor_vet = new Vector < Object >();

    // Singleton
    private static ManterResEquipamentoProfessor instance;

    private ManterResEquipamentoProfessor() {
    }

    
    /**
	 * Creates an instance of a reserve if it isn't already instantiated.
	 * @return - ManterResEquipamentoProfessor - Equipment reserve
	 */
    public static ManterResEquipamentoProfessor getInstance() {
        if ( instance == null )
            instance = new ManterResEquipamentoProfessor();
        return instance;
    }


    /**
     * Captures the teacher which had reserved a equipment in the hour searched.
     * @return Vector - Teachers
     */
    public Vector < ReservaEquipamentoProfessor > getReservasHora( String hora ) throws SQLException, PatrimonioException,
            ClienteException, ReservaException {
        return ResEquipamentoProfessorDAO.getInstance().buscarPorHora( hora );
    }

    /**
     * Captures the teachers which had reserved a equipment in the month searched.
     * @return Vector - Teachers
     */
    public Vector < ReservaEquipamentoProfessor > getReservasMes( int mes ) throws SQLException, PatrimonioException, ClienteException,
            ReservaException {
        return ResEquipamentoProfessorDAO.getInstance().buscarPorMes( mes );
    }

    
    /**
     * Lists all the equipment reserves for teachers.
     * @return Vector - Equipment
     */
    public Vector < Object > getResEquipamentoProfessor_vet() throws SQLException, ClienteException, PatrimonioException,
            ReservaException {
        this.rev_equipamento_professor_vet = ResEquipamentoProfessorDAO.getInstance().buscarTodos();
        return this.rev_equipamento_professor_vet;
    }

    
    /**
     * Reserves an equipment to a teacher to a date and hour.
     */
    public void inserir( Equipamento equipamento, Professor prof, String data, String hora ) throws SQLException, ReservaException {
        ReservaEquipamentoProfessor reserva = new ReservaEquipamentoProfessor( data, hora, equipamento, prof );
        ResEquipamentoProfessorDAO.getInstance().incluir( reserva );
        this.rev_equipamento_professor_vet.add( reserva );
    }

    
    /**
     * Changes the reserve to a new one.
     */
    public void alterar( String finalidade, ReservaEquipamentoProfessor reserva ) throws SQLException, ReservaException {

        ReservaEquipamentoProfessor reserva_old = new ReservaEquipamentoProfessor( reserva.getData(), reserva.getHora(),
                reserva.getEquipamento(), reserva.getProfessor());
        ResEquipamentoProfessorDAO.getInstance().alterar( reserva_old, reserva );

    }

    
    /**
     * Excludes a reserve.
     */
    public void excluir( ReservaEquipamentoProfessor reserva ) throws SQLException, ReservaException {
        ResEquipamentoProfessorDAO.getInstance().excluir( reserva );
        this.rev_equipamento_professor_vet.remove( reserva );
    }

}
