/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.cadastros;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import control.MaintainRoom;
import exception.PatrimonyException;

/**
 * 
 * @author Parley
 */
public class ClassroomCadastre extends HeritageCadastre {


	private static final long serialVersionUID = 1L;

	public ClassroomCadastre(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setName("CadastroSala");
    }
    
 // This method is the action to the cadastre button
    @Override protected void cadastroAction() {
        try {
            // JOptionPane.showMessageDialog(this, codigoTxtField.getText() +
            // descricaoTextArea.getText() + capacidadeTxtField.getText(),
            // "teste", JOptionPane.INFORMATION_MESSAGE, null);
            MaintainRoom.getInstance().insertRooms(codigoTxtField.getText(), descricaoTextArea.getText(), capacidadeTxtField.getText());

            JOptionPane.showMessageDialog(this, "Sala Cadastrada com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null);
            this.setVisible(false);

        } catch (PatrimonyException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getSQLState() + "\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        }

    }
}
