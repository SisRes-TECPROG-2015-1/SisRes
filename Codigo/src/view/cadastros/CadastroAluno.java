/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.cadastros;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import control.MaintainStudent;
import exception.ClientException;

/**
 * 
 * @author Parley
 */
public class CadastroAluno extends CadastroCliente {

    public CadastroAluno( java.awt.Frame parent, boolean modal ) {
        super( parent, modal );
        this.setName( "CadastroAluno" );
    }

    // This method is the action to the cadastre button
    @Override public void cadastroAction() {
        try {
            if ( cadastroBtn.getText().equals( "Cadastrar" ) ) {
                MaintainStudent.getInstance().insertStudents( nomeTxtField.getText(), cpfTxtField.getText(), matriculaTxtField.getText(),
                        telefoneTxtField.getText(), emailTxtField.getText() );

                JOptionPane.showMessageDialog( this, "Aluno Cadastrado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE,
                        null );

                this.setVisible( false );
            }else{
            	//do nothing
            }
            
        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog( this, ex.getLocalizedMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }
}
