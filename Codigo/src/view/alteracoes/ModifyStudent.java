/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.alteracoes;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import view.cadastros.ClientCadastre;
import control.MaintainStudent;
import exception.ClientException;

/**
 * 
 * @author Parley
 */
public class ModifyStudent extends ClientCadastre {

    
	private static final long serialVersionUID = 1269596966446202752L;
	
	int index2 = 0;

    //Constructor
    public ModifyStudent( java.awt.Frame parent, boolean modal, int index ) {
        super( parent, modal );
        this.setTitle( "Alterar" );
        this.setName( "AlterarAluno" );
        this.cadastroBtn.setText( "Alterar" );
        this.cadastroBtn.setName( "Alterar" );
        this.index2 = index;

        try {
            this.nomeTxtField.setText( MaintainStudent.getInstance().getStudents().get( index ).getName() );
            this.emailTxtField.setText( MaintainStudent.getInstance().getStudents().get( index ).getEmail() );
            this.telefoneTxtField.setText( MaintainStudent.getInstance().getStudents().get( index ).getFone() );
            this.matriculaTxtField.setText( MaintainStudent.getInstance().getStudents().get( index ).getRegistration() );
            this.cpfTxtField.setText( MaintainStudent.getInstance().getStudents().get( index ).getCpf() );

        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

    // This method is the action to the button of cadastre
    @Override public void cadastroAction() {
        try {
            MaintainStudent.getInstance().changeStudent( nomeTxtField.getText(), cpfTxtField.getText(), matriculaTxtField.getText(),
                    telefoneTxtField.getText(), emailTxtField.getText(), MaintainStudent.getInstance().getStudents().get( index2 ) );

            JOptionPane.showMessageDialog( this, "Aluno alterado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null );
            this.setVisible( false );
        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }
}
