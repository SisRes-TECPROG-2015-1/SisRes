import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Create {

	public static void main(String[] args) {
		uptodate("CREATE TABLE aluno ( id_aluno INT NOT NULL AUTO_INCREMENT, " +
				"nome VARCHAR(100) NOT NULL, " +
				"cpf VARCHAR(14) NOT NULL, " +
				"telefone VARCHAR(15), " +
				"email VARCHAR(60), " +
				"matricula VARCHAR(15) NOT NULL, " +
				"PRIMARY KEY (id_aluno));");
		
		uptodate("CREATE TABLE equipamento ( " +
				"id_equipamento INT NOT NULL AUTO_INCREMENT, " +
				"codigo VARCHAR(15) NOT NULL, " +
				"descricao VARCHAR(120), " +
				"PRIMARY KEY (id_equipamento));");
	
	
		uptodate("CREATE TABLE professor (" +
				"id_professor INT NOT NULL AUTO_INCREMENT, " +
				"nome VARCHAR(100) NOT NULL, " +
				"cpf VARCHAR(14) NOT NULL, " +
				"telefone VARCHAR(15), " +
				"email VARCHAR(60), " +
				"matricula VARCHAR(15) NOT NULL, " +
				"PRIMARY KEY (id_professor));");
	
	
		uptodate("CREATE TABLE reserva_equipamento ( " +
				"id_reserva_equipamento INT NOT NULL AUTO_INCREMENT, " +
				"id_professor INT NOT NULL, " +
				"id_equipamento INT NOT NULL, " +
				" hora VARCHAR(5) NOT NULL, " +
				"data VARCHAR(10) NOT NULL, " +
				"PRIMARY KEY (id_reserva_equipamento));");
	
	
		uptodate("CREATE TABLE sala ( " +
				"id_sala INT NOT NULL AUTO_INCREMENT, " +
				"codigo VARCHAR(10) NOT NULL, " +
				"descricao VARCHAR(120), " +
				"capacidade INT, PRIMARY KEY (id_sala) );");
	
	
		uptodate("CREATE TABLE reserva_sala_aluno ( " +
				"id_reserva_sala_aluno INT NOT NULL AUTO_INCREMENT, " +
				"id_aluno INT NOT NULL, " +
				"id_sala INT NOT NULL, " +
				"finalidade VARCHAR(150) NOT NULL, " +
				"hora VARCHAR(5) NOT NULL, " +
				"data VARCHAR(10) NOT NULL, " +
				"cadeiras_reservadas INT NOT NULL, " +
				"PRIMARY KEY (id_reserva_sala_aluno));");
	
	
		uptodate("CREATE TABLE reserva_sala_professor (" +
				"id_reserva_sala_professor INT NOT NULL AUTO_INCREMENT, " +
				"id_professor INT NOT NULL, " +
				"id_sala INT NOT NULL, " +
				"finalidade VARCHAR(150) NOT NULL, " +
				"hora VARCHAR(5) NOT NULL, " +
				"data VARCHAR(10) NOT NULL, " +
				"PRIMARY KEY (id_reserva_sala_professor));");
	
	
		uptodate("ALTER TABLE reserva_equipamento ADD CONSTRAINT FK_reserva_equipamento_0 FOREIGN KEY (id_professor) REFERENCES professor (id_professor);");
		uptodate("ALTER TABLE reserva_equipamento ADD CONSTRAINT FK_reserva_equipamento_1 FOREIGN KEY (id_equipamento) REFERENCES equipamento (id_equipamento);");
	
	
		uptodate("ALTER TABLE reserva_sala_aluno ADD CONSTRAINT FK_reserva_sala_aluno_0 FOREIGN KEY (id_aluno) REFERENCES aluno (id_aluno);");
		uptodate("ALTER TABLE reserva_sala_aluno ADD CONSTRAINT FK_reserva_sala_aluno_1 FOREIGN KEY (id_sala) REFERENCES sala (id_sala);");
	
	
		uptodate("ALTER TABLE reserva_sala_professor ADD CONSTRAINT FK_reserva_sala_professor_0 FOREIGN KEY (id_professor) REFERENCES professor (id_professor);");
		uptodate("ALTER TABLE reserva_sala_professor ADD CONSTRAINT FK_reserva_sala_professor_1 FOREIGN KEY (id_sala) REFERENCES sala (id_sala);");
		

	}
	public static void uptodate(String msg)
	{
		String local = "jdbc:mysql://localhost/sisres_db";
		String user = "root";
		String password = "root";
		
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = null;
			con = DriverManager.getConnection(local, user, password);
			pst = con.prepareStatement(msg);
			pst.executeUpdate();
			pst.close();
			con.close();
			System.out.println("Ok!");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}	
	}
}
