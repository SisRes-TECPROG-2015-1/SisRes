/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.alteracoes;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import view.cadastros.HeritageCadastre;
import control.MaintainRoom;
import exception.PatrimonyException;

/**
 * 
 * @author Parley
 */
public class ModifyRoom extends HeritageCadastre {

    
	private static final long serialVersionUID = 1L;
	private int index2 = 0;

    public ModifyRoom( java.awt.Frame parent, boolean modal, int index ) {
        super( parent, modal );
        this.setTitle( "Alterar" );
        this.setName( "AlterarSala" );
        this.cadastroBtn.setText( "Alterar" );
        this.cadastroBtn.setName( "Alterar" );
        index2 = index;

        try {

            this.codigoTxtField.setText( MaintainRoom.getInstance().getRooms().get(index).getCode() );
            this.capacidadeTxtField.setText( MaintainRoom.getInstance().getRooms().get( index ).getCapacity() );
            this.descricaoTextArea.setText( MaintainRoom.getInstance().getRooms().get( index ).getDescription() );
            this.index2 = index;

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( NullPointerException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }

    }

    /**
     * This method is the action of the cadastre button
     */
    @Override protected void cadastroAction() {
        try {

            MaintainRoom.getInstance().changeRoom( codigoTxtField.getText(), descricaoTextArea.getText(), capacidadeTxtField.getText(),
                    MaintainRoom.getInstance().getRooms().get( index2 ) );

            JOptionPane.showMessageDialog( this, "Sala Alterada com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null );
            this.setVisible( false );

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

}
