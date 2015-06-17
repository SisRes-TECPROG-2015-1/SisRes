package view.alteracoes;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import view.cadastros.HeritageCadastre;
import control.MaintainEquipment;
import exception.PatrimonyException;

/**
 * 
 * @author Parley
 */
public class AlterarEquipamento extends HeritageCadastre {
    
	private static final long serialVersionUID = 5154835153930983108L;
	
	private int index2 = 0;

    public AlterarEquipamento( java.awt.Frame parent, boolean modal, int index ) {
        super(parent, modal);
        this.setTitle( "Alterar" );
        this.setName( "AlterarEquipamento" );
        this.cadastroBtn.setText( "Alterar" );
        this.cadastroBtn.setName( "Alterar" );
        this.capacidadeLbl.setVisible( false );
        this.capacidadeTxtField.setVisible( false );
        index2 = index;

        try {

            this.codigoTxtField.setText( MaintainEquipment.getInstance().getEquipments().get( index ).getCode() );
            this.descricaoTextArea.setText(MaintainEquipment.getInstance().getEquipments().get( index ).getDescription() );
            this.index2 = index;

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }

    }

    // This method is the action to the cadastre button
    @Override protected void cadastroAction() {
        try {

            MaintainEquipment.getInstance().changeEquipmentsAttributes( codigoTxtField.getText(), descricaoTextArea.getText(),
                    MaintainEquipment.getInstance().getEquipments().get( index2 ) );

            JOptionPane.showMessageDialog( this, "Equipamento alterado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE,
                    null );
            this.setVisible( false );

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }
}
