package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.StudentDAO;
import exception.ClientException;
import model.Student;

public class MaintainStudent {
	
	private Vector <Student> student_vet = new Vector <Student> ();
	
	//Singlenton
	private static MaintainStudent instance;
	
	private MaintainStudent () {
	}
	
		
	/**
	 * Creates an instance of a student if it isn't already instantiated.
	 * @return - Student - A student
	 */
	public static MaintainStudent getInstance () {
		if ( instance == null )
		instance = new MaintainStudent();
		return instance;
	}
	
	/**
	 * Searches for students by its name
	 * @return Vector - Students
	 */
	public Vector <Student> searchStudentByName ( String studantsName ) throws SQLException, ClientException {
		return StudentDAO.getInstance().searchByName(studantsName);
	}
	
	/**
	 * Searches for students by its CPF
	 * @return Vector - Students
	 */
	public Vector <Student> getStudentsByCpf ( String studentsCpf ) throws SQLException, ClientException {
		return StudentDAO.getInstance().searchByCpf( studentsCpf );
	}
	
	
	/**
	 * Searches for students by its enrollment
	 * @return Vector - Students
	 */
	public Vector <Student> getStudentsByRegistration ( String studentRegistration ) throws SQLException, ClientException {
		return StudentDAO.getInstance().searchByRegistration( studentRegistration );
	}
	
	
	/**
	 * Searches for a student by its e-mail
	 * @return Vector - Students
	 */
	public Vector <Student> getStudentsByEmail ( String studentEmail ) throws SQLException, ClientException {
		return StudentDAO.getInstance().searchByEmail( studentEmail );
	}
	
	/**
	 * Searches for students by its telephone number
	 * @return Vector - Students
	 */
	public Vector <Student> getStudentByTelephone ( String studentTelephone ) throws SQLException, ClientException {
		return StudentDAO.getInstance().searchByPhoneNumber( studentTelephone );
	}
	
	
	/**
	 * Captures all the students in database
	 * @return Vector - students
	 */	
	public Vector <Student> getAluno_vet() throws SQLException, ClientException {
		this.student_vet = StudentDAO.getInstance().captureStudents();
		return this.student_vet;
	}
	
	
	/**
	 * Inserts a new student in the database and its attributes
	 * @return void
	 */
	public void inserir ( String nome, String cpf, String matricula,
						String telefone, String email ) 
						throws ClientException, SQLException {
		Student aluno = new Student ( nome, cpf, matricula, telefone, email );
		StudentDAO.getInstance().includeNewStudent(aluno);
		this.student_vet.add(aluno);
	}

	
	/**
	 * Changes a student attributes like name, CPF, enrollment and others
	 * @return void
	 */
	public void alterar ( String nome, String cpf, String registration,
						String fone, String email, Student student ) 
						throws ClientException, SQLException {
		Student old_student = new Student (
						student.getName(),
						student.getCpf(),
						student.getRegistration(),
						student.getFone(),
						student.getEmail());
						student.setNome(nome);
						student.setCpf(cpf);
						student.setRegistration(registration);
						student.setTelefone(fone);
						student.setEmail(email);
						StudentDAO.getInstance().modifyStudent(old_student, student);
	}
	
	
	/**
	 * Excludes a student from the database by its instance
	 * @return void
	 */
	public void excluir ( Student student ) throws SQLException, ClientException {
		StudentDAO.getInstance().deleteStudent(student);
		this.student_vet.remove(student);
	}

}
