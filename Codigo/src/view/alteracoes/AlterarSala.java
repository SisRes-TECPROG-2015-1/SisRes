/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.alteracoes;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import view.cadastros.CadastroPatrimonio;
import control.MaintainRoom;
import exception.PatrimonyException;

/**
 * 
 * @author Parley
 */
public class AlterarSala extends CadastroPatrimonio {

    private int index2 = 0;

    public AlterarSala( java.awt.Frame parent, boolean modal, int index ) {
        super( parent, modal );
        this.setTitle( "Alterar" );
        this.setName( "AlterarSala" );
        this.cadastroBtn.setText( "Alterar" );
        this.cadastroBtn.setName( "Alterar" );
        index2 = index;

        try {

            this.codigoTxtField.setText( MaintainRoom.getInstance().getSalas_vet().get(index).getCodigo() );
            this.capacidadeTxtField.setText( MaintainRoom.getInstance().getSalas_vet().get( index ).getCapacidade() );
            this.descricaoTextArea.setText( MaintainRoom.getInstance().getSalas_vet().get( index ).getDescricao() );
            this.index2 = index;

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( NullPointerException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }

    }

    // This method is the action to the cadastre button
    @Override protected void cadastroAction() {
        try {

            MaintainRoom.getInstance().alterar( codigoTxtField.getText(), descricaoTextArea.getText(), capacidadeTxtField.getText(),
                    MaintainRoom.getInstance().getSalas_vet().get( index2 ) );

            JOptionPane.showMessageDialog( this, "Sala Alterada com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null );
            this.setVisible( false );

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

}
