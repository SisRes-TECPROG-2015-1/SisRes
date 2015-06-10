import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Create {

	public static void main(String[] args) {
		//Deletador de banco
		reconfig("DROP DATABASE sisres_db;");
		reconfig("CREATE DATABASE sisres_db;");
		//Criador de tabelas
		uptodate("CREATE TABLE student ( id_student INT NOT NULL AUTO_INCREMENT, " +
				"name VARCHAR(100) NOT NULL, " +
				"cpf VARCHAR(14) NOT NULL, " +
				"fone VARCHAR(15), " +
				"email VARCHAR(60), " +
				"registration VARCHAR(15) NOT NULL, " +
				"PRIMARY KEY (id_student));");
		
		uptodate("CREATE TABLE equipment ( " +
				"id_equipment INT NOT NULL AUTO_INCREMENT, " +
				"code VARCHAR(15) NOT NULL, " +
				"description VARCHAR(120), " +
				"PRIMARY KEY (id_equipment));");
	
	
		uptodate("CREATE TABLE teacher (" +
				"id_teacher INT NOT NULL AUTO_INCREMENT, " +
				"name VARCHAR(100) NOT NULL, " +
				"cpf VARCHAR(14) NOT NULL, " +
				"fone VARCHAR(15), " +
				"email VARCHAR(60), " +
				"registration VARCHAR(15) NOT NULL, " +
				"PRIMARY KEY (id_teacher));");
	
	
		uptodate("CREATE TABLE reservation_equipment ( " +
				"id_reservation_equipment INT NOT NULL AUTO_INCREMENT, " +
				"id_teacher INT NOT NULL, " +
				"id_equipment INT NOT NULL, " +
				" hour VARCHAR(5) NOT NULL, " +
				"date VARCHAR(10) NOT NULL, " +
				"PRIMARY KEY (id_reservation_equipment));");
	
	
		uptodate("CREATE TABLE room ( " +
				"id_room INT NOT NULL AUTO_INCREMENT, " +
				"code VARCHAR(10) NOT NULL, " +
				"description VARCHAR(120), " +
				"capacity INT, PRIMARY KEY (id_room) );");
	
	
		uptodate("CREATE TABLE reservation_room_student ( " +
				"id_reservation_room_student INT NOT NULL AUTO_INCREMENT, " +
				"id_student INT NOT NULL, " +
				"id_room INT NOT NULL, " +
				"finality VARCHAR(150) NOT NULL, " +
				"hour VARCHAR(5) NOT NULL, " +
				"date VARCHAR(10) NOT NULL, " +
				"reserved_chairs INT NOT NULL, " +
				"PRIMARY KEY (id_reservation_room_student));");
	
	
		uptodate("CREATE TABLE reservation_room_teacher (" +
				"id_reservation_room_teacher INT NOT NULL AUTO_INCREMENT, " +
				"id_teacher INT NOT NULL, " +
				"id_room INT NOT NULL, " +
				"finality VARCHAR(150) NOT NULL, " +
				"hour VARCHAR(5) NOT NULL, " +
				"date VARCHAR(10) NOT NULL, " +
				"PRIMARY KEY (id_reservation_room_teacher));");
	
	
		uptodate("ALTER TABLE reservation_equipment ADD CONSTRAINT FK_reservation_equipment_0 FOREIGN KEY (id_teacher) REFERENCES teacher (id_teacher);");
		uptodate("ALTER TABLE reservation_equipment ADD CONSTRAINT FK_reservation_equipment_1 FOREIGN KEY (id_equipment) REFERENCES equipment (id_equipment);");
	
	
		uptodate("ALTER TABLE reservation_room_student ADD CONSTRAINT FK_reservation_room_student_0 FOREIGN KEY (id_student) REFERENCES student (id_student);");
		uptodate("ALTER TABLE reservation_room_student ADD CONSTRAINT FK_reservation_room_student_1 FOREIGN KEY (id_room) REFERENCES room (id_room);");
	
	
		uptodate("ALTER TABLE reservation_room_teacher ADD CONSTRAINT FK_reservation_room_teacher_0 FOREIGN KEY (id_teacher) REFERENCES teacher (id_teacher);");
		uptodate("ALTER TABLE reservation_room_teacher ADD CONSTRAINT FK_reservation_room_teacher_1 FOREIGN KEY (id_room) REFERENCES room (id_room);");
		

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
	public static void reconfig(String msg)
	{
		String local = "jdbc:mysql://localhost";
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
