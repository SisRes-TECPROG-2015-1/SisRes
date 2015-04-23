/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.alteracoes;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import view.cadastros.CadastroCliente;
import control.MaintainTeacher;
import exception.ClientException;

/**
 * 
 * @author Parley
 */
public class AlterarProfessor extends CadastroCliente {

    int index2 = 0;

    public AlterarProfessor(java.awt.Frame parent, boolean modal, int index) {
        super(parent, modal);
        this.setName("AlterarProfessor");
        this.cadastroBtn.setText("Alterar");
        this.cadastroBtn.setName("Alterar");
        this.index2 = index;

        try {
            this.nomeTxtField.setText(MaintainTeacher.getInstance().getTeachers().get(index).getName());
            this.emailTxtField.setText(MaintainTeacher.getInstance().getTeachers().get(index).getEmail());
            this.telefoneTxtField.setText(MaintainTeacher.getInstance().getTeachers().get(index).getFone());
            this.matriculaTxtField.setText(MaintainTeacher.getInstance().getTeachers().get(index).getRegistration());
            this.cpfTxtField.setText(MaintainTeacher.getInstance().getTeachers().get(index).getCpf());

        } catch (ClientException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        }
    }

    // This method is the action to the cadastre button
    @Override public void cadastroAction() {
        try {
            MaintainTeacher.getInstance().changeTeacher(nomeTxtField.getText(), cpfTxtField.getText(), matriculaTxtField.getText(),
                    telefoneTxtField.getText(), emailTxtField.getText(),
                    MaintainTeacher.getInstance().getTeachers().get(index2));

            JOptionPane.showMessageDialog(this, "Cadastro alterado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null);
            this.setVisible(false);
        } catch (ClientException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        }
    }
}
