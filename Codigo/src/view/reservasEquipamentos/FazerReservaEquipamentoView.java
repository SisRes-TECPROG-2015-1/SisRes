/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.reservasEquipamentos;

import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import model.Equipment;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

/**
 * 
 * @author Parley
 */
public class FazerReservaEquipamentoView extends ReservaEquipamentoView {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Equipment equipamento;

    public FazerReservaEquipamentoView( Frame parent, boolean modal, Equipment e, String data ) throws SQLException,
            PatrimonyException, PatrimonyException, ClientException, ReserveException {
        super( parent, modal );
        this.equipamento = e;
        this.dataTextField.setText( data );
        this.equipamentoTextArea.setText( e.toString() );
    }

    /**
     * This method books an equipment by a teacher
     */
    @Override protected void reservarProfessor() {
        try {

            instanceProf.insertNewEquipment( equipamento, prof, this.dataTextField.getText(), this.horaTextField.getText() );

            JOptionPane.showMessageDialog( this, "Reserva feita com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null );

            this.setVisible( false );
        } catch ( ReserveException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getLocalizedMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

}
