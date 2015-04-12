/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.reservasEquipamentos;

import java.awt.Color;
import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import model.ReservaEquipamentoProfessor;
import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

/**
 * 
 * @author Parley
 */
public class AlterarReservaEquipamentoView extends ReservaEquipamentoView {

    int index;
    ReservaEquipamentoProfessor reserva;

    private void resetComponents() {
        this.reservarButton.setText( "Alterar" );
        this.reservarButton.setName( "AlterarButton" );
        this.cpfLabel.setEnabled( false );
        this.cpfTextField.setBackground( new Color( 200, 208, 254 ) );
        this.cpfTextField.setEditable( false );
        this.horaTextField.setText( reserva.getHora() );
        this.dataTextField.setText( reserva.getData() );
        this.professorTextArea.setText( reserva.getProfessor().toString() );
    }

    public AlterarReservaEquipamentoView( Frame parent, boolean modal, int index, int mes ) throws SQLException, PatrimonyException,
            PatrimonyException, ClienteException, ReserveException {
        super( parent, modal );
        this.index = index;
        reserva = this.instanceProf.getReservasMes( mes ).get( index );
        resetComponents();
    }

    /**
     * This method changes an equipment booking by a teacher
     */
    @Override protected void reservarProfessor() {
        try {

            instanceProf.alterar( null, reserva );
            JOptionPane.showMessageDialog( this, "Reserva alterada com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null );
            this.setVisible( false );
        } catch ( ReserveException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getLocalizedMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

}
