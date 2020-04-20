import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;


/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class Server {

	public static void main(String[] args) {
		if (args.length < 1) return;

		int port = Integer.parseInt(args[0]);
		String command = "";
		
		String accountName_Credential = "";
		String password_Credential = "";
		
		String accountName_Creation = "";
		String password_Creation = "";
		String email_Creation = "";
		
		boolean loginSuccess = false;
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();

				System.out.println("New client connected");

				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				OutputStream output = socket.getOutputStream();
				PrintWriter printer = new PrintWriter(output, true);
				command = reader.readLine();
				
				if(command.equals("Login")) {
					System.out.println("Login command received");
					accountName_Credential = reader.readLine();
					password_Credential = reader.readLine();
					
					if(accountName_Credential != "" && password_Credential != ""){    
						System.out.println("Account: " + accountName_Credential + " with password: " + password_Credential);
						loginSuccess = LoqinSQL(accountName_Credential, password_Credential);
						if(loginSuccess) {
							System.out.println("Success");
							printer.println("Success");

						}else {
							System.out.println("Failed");
							printer.println("Failed");
						}
					}
					command = "";
					
				}else if(command.equals("CreateAccount") ) {
					accountName_Creation = reader.readLine();
					password_Creation = reader.readLine();
					email_Creation = reader.readLine();
					
					if(CreateAccountSQL(accountName_Creation, password_Creation, email_Creation)) {
						System.out.println("Success");
						printer.println("Success");

					}else {
						System.out.println("Failed");
						printer.println("Failed");
					}
				}

				
			}
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}


	public static boolean LoqinSQL(String accountName, String password) {
		String msg = "";

		final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

		final String DB_URL = "jdbc:mysql://" + "127.0.0.1:1234" + "/" +
				"mhs_example";

		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, "root", "dettas12");

			stmt = conn.createStatement();

			String sql = "SELECT * FROM accounts WHERE AccNames = '" + accountName +"'";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){

				String passText = password;
				String pass = rs.getString("AccPass");
				rs.close();
				stmt.close();
				conn.close();

				if(passText.equals(pass)){
					msg = "Login success!";
					System.out.println(msg);
					return true;				
				}else{
					msg = "Login failed!";
					System.out.println(msg);
					return false;
				}
			}else{
				rs.close();
				stmt.close();
				conn.close();
			}


		} catch (SQLException connError){
			msg = String.valueOf(connError.getErrorCode());

			connError.printStackTrace();
		} catch (ClassNotFoundException e) {
			msg = "A Class not found exception was thrown.";
			e.printStackTrace();

		} finally{
			try{
				if(stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try{
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	public static boolean CreateAccountSQL(String accountName, String password, String emailAddress) {
		int credentialsValid = 0;
		boolean accountCreated = false;

		final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

		final String DB_URL = "jdbc:mysql://" + "127.0.0.1:1234" + "/" +
				"mhs_example";

			Connection conn = null;
			Statement stmt = null;

			try {
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, "root", "dettas12");

				stmt = conn.createStatement();

				String sql = "SELECT EXISTS(SELECT * FROM accounts WHERE AccNames = '" + accountName +"' OR AccMail = '" + emailAddress + "')";
				ResultSet rs = stmt.executeQuery(sql);
				rs.next();

				credentialsValid =  rs.getInt("EXISTS(SELECT * FROM accounts WHERE AccNames = '" + accountName +"' OR AccMail = '" + emailAddress + "')");
				rs.close();
				
				if(credentialsValid == 0) {
					String sqlCreate = "INSERT INTO `mhs_example`.`accounts` (`AccNames`, `AccPass`, `AccMail`) VALUES ('"+
							accountName + "', '" +
							password + "', '" +
							emailAddress + "')";
					stmt.executeUpdate(sqlCreate);
					accountCreated = true;
				}else {
					accountCreated = false;
				}
				
				
				stmt.close();
				conn.close();
				
				
			} catch (SQLException connError){

				connError.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();

			} finally{
				try{
					if(stmt != null){
						stmt.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
		}
			return accountCreated;
	}

}


