package Project.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import Project.model.CourseDetails;
import Project.model.RegisteredCourses;
import Project.model.User;
import Project.util.DbUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class StudentRepository
{
	//Simple JDBC
	private Connection dbConnection;

	//Hibernate Libraries
	private Configuration cfg;
	private SessionFactory factory;
	private Session session;
	private Transaction transaction;

	private static int RollNumber = 114000;

	public StudentRepository()
	{
		//JDBC
		dbConnection = DbUtil.getConnection();

		//Hibernate
		cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");
		factory = cfg.buildSessionFactory();
		session = factory.openSession();
	}

	public void save(String firstName, String lastName, String dateOfBirth, String Address , String PhoneNumber , String Username, String Password, String emailAddress)
	{
		if (dbConnection != null)
		{
			try
			{
				PreparedStatement prepStatement = dbConnection
						.prepareStatement("INSERT INTO User( id , Name , DOB , Address , Phone_Number , Roll_Number , Username , Password , EmailAddress  )  VALUES (DEFAULT , ? , ? , ? , ? , ? , ? , ? , ? )");
				prepStatement.setString(1, firstName + lastName);
				prepStatement.setDate(2, new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy").parse(dateOfBirth.substring(0, 10)).getTime()));
                prepStatement.setString(3, Address);
                prepStatement.setString(4, PhoneNumber);
				prepStatement.setString(5, String.valueOf(RollNumber++));
				prepStatement.setString(6, Username);
				prepStatement.setString(7, Password);
				prepStatement.setString(8, emailAddress);

				prepStatement.executeUpdate();

			}
            catch (SQLException | ParseException e)
            {
				e.printStackTrace();
			}
		}
	}

	public boolean findByUserName(String userName) {
		if (dbConnection != null) {
			try {
				PreparedStatement prepStatement = dbConnection
						.prepareStatement("select count(*) from User where UserName = ?");
				prepStatement.setString(1, userName);

				ResultSet result = prepStatement.executeQuery();
				if (result != null) {
					while (result.next()) {
						if (result.getInt(1) == 1) {
							return true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean findByLogin(String userName, String password) {
		if (dbConnection != null) {
			try {
				PreparedStatement prepStatement = dbConnection
						.prepareStatement("select Password from User where UserName = ?");
				prepStatement.setString(1, userName);

				ResultSet result = prepStatement.executeQuery();
				if (result != null) {
					while (result.next()) {
						if (result.getString(1).equals(password)) {
							return true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public List<CourseDetails> showAllAvailableCourses( )
	{
		Query query1 = session.createQuery("from CourseDetails ");

		List <CourseDetails> courseDetails = query1.list();

		for(CourseDetails course : courseDetails)
			System.out.println(course.getName());

		return courseDetails;
	}

	public List<CourseDetails> showAllCourses( String username )
	{
		Query query1 = session.createQuery("from User where username = :Username");
		query1.setParameter( "Username" , username);

		List <User> users = query1.list();

		String rollNumber = users.get(0).getRollNumber();

		Query query2 = session.createQuery("from RegisteredCourses where rollNumber = :RollNumber");
		query2.setParameter( "RollNumber" , rollNumber);
		List <RegisteredCourses> courses = query2.list();

		String values = "courseId LIKE '%";

		for( int i = 0; i < courses.size(); i++ )
		{
			values += courses.get(i).getCourseId();
			if(i != courses.size() - 1)
				values += "%' OR courseId LIKE '%";
		}
		values += "%'";

		Query query3 = session.createQuery("from CourseDetails where :CourseID");
		query3.setParameter( "CourseID" , values);
		List <CourseDetails> courseDetails = query3.list();

		return courseDetails;
	}

	public List<CourseDetails> showRegisteredCourses( String username )
	{
		return null;
	}

}
