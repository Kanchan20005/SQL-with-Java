// Sample program to demonstrate rudimentary database access using SQL Server in the Library example
//Name: Kanchan Bhattarai
// Date: 4/30/2023
//
//
// REQUIRES THE FOLLOWING:

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.*;

public class Library
{
	//The JDBC connection URL to allow for Windows Integrated Authentication:
	private static final String dbURL = "jdbc:sqlserver://localhost;databaseName=PublicLibrary;integratedSecurity=true;trustServerCertificate=true";
	
	private static Scanner keyboard = new Scanner(System.in);
	private static Connection databaseConnection = null;
	
	public static void main(String[] args) 
	{
		System.out.println("Program started");
    
	    //Connection databaseConnection= null;
	    try
	    {
	      //Connect to the database
	      databaseConnection = DriverManager.getConnection(dbURL);
	      System.out.println("Connected to the database");
	    
	      //declare the statement object
	      Statement sqlStatement = databaseConnection.createStatement();
	      
	      displayMenu(); // we will stay in the menu system until the user exits.	      
	      
	      System.out.println("Closing database connection");

	      //close the database connection
	      databaseConnection.close();
	    }
	    catch (SQLException err)
	    {
	       System.err.println("Error connecting to the database");
	       err.printStackTrace(System.err);
	       System.exit(0);
	    }
	    System.out.println("Program finished");
	} // End Main
	
	private static void displayMenu()
	{
		while (true)
		{
			System.out.println("\n\n");
			System.out.println("         MAIN MENU      ");
			System.out.println("------------------------");
			System.out.println("1. Look up Book         ");
			System.out.println("2. Check Out Book       ");
			System.out.println("3. Return Book          ");
			System.out.println("4. View Book History    ");
			System.out.println("5. View Customer History");
			System.out.println("6. Show Books On Loan");
			System.out.println("7. Exit                 \n");
			System.out.print  ("Enter Option (1-7): ");
			int option = keyboard.nextInt();
			if (option == 7) return;
			if (option == 1) lookUpBook();
			if (option == 2) CheckOutBook();
			if (option == 3) ReturnBook();
			if (option == 5) viewcustomerHistory();
			if (option == 6) ShowBooksOnLoan();
		}	
	}
	
	private static void lookUpBook()
	{
		String partialQuery = "SELECT T.Date_Out, A.Name, T.Title, T.ISBN " +
							  "FROM   AUTHOR A, TITLE T, TITLE_AUTHOR TA " + 
							  "WHERE  TA.Au_ID = A.Au_ID AND T.ISBN = TA.ISBN AND   ";

		System.out.println("\n\n");
		System.out.println("       BOOK LOOKUP       ");
		System.out.println("-------------------------");
		System.out.println("1. By Author             ");
		System.out.println("2. By ISBN               ");
		System.out.println("3. By Title              \n");
		System.out.println("4. Return to main menu   \n");
		System.out.print  ("Enter option (1-4): ");
		int option = keyboard.nextInt();
		switch(option)
		{
			case 4: return;
		
			case 1: {	// Look up book by Author's Name
				System.out.print("Enter Author Name (or partial): ");
				keyboard.nextLine(); 				// flush Scanner buffer
				String name = keyboard.nextLine();	// Get author name fragment
				if (name.indexOf('\'') > -1)                 // If the "name" contains a single quote,
				{                                            //
					name = name.replace("\'", "\'\'");       // escape it before sending it on to SQL Server
				}
				
				try
				{
					Statement sqlStatement = databaseConnection.createStatement();
					String queryString = partialQuery + " A.Name like '%" + name + "%' ORDER BY T.ISBN, A.Name;";
					System.out.println("Query String:\n" + queryString);
					
					ResultSet rs = sqlStatement.executeQuery(queryString);
					displayBookList(rs);
					rs.close();
				}
				catch (SQLException err)
				{
					System.out.println("SQL Exception " + err.getMessage() + " occurred during lookup\n");
				}
				break;
			}
			
			case 2: {   // Look up book by ISBN
				System.out.print("Enter ISBN (n-nnnnnnn-n-n) (or partial): ");
				keyboard.nextLine(); 				// flush Scanner buffer
				String isbn = keyboard.nextLine();	// Get author name fragment
				try
				{
					Statement sqlStatement = databaseConnection.createStatement();
					String queryString = partialQuery + " T.ISBN like '%" + isbn + "%' ORDER BY T.ISBN, A.Name;";
					ResultSet rs = sqlStatement.executeQuery(queryString);
					displayBookList(rs);
					rs.close();
				}
				catch (SQLException err)
				{
					System.out.println("SQL Exception " + err.getMessage() + " occurred during lookup\n");
				}
				break;
	}
			
			case 3: {	// Look up book by ISBN
				System.out.print("Enter Book Title () (or partial): ");
				keyboard.nextLine(); 				// flush Scanner buffer
				String title = keyboard.nextLine();	// Get author name fragment
				try
				{
					Statement sqlStatement = databaseConnection.createStatement();
					String queryString = partialQuery + " T.Title like '%" + title + "%' ORDER BY T.ISBN, A.Name;";
					ResultSet rs = sqlStatement.executeQuery(queryString);
					displayBookList(rs);
					rs.close();
				}
				catch (SQLException err)
				{
					System.out.println("SQL Exception " + err.getMessage() + " occurred during lookup\n");
				}
				break;
	        }
			
			default: System.out.println("Please use 1 - 4");
		}
	} // end look up book
	
	
	private static void CheckOutBook(){
		System.out.print("Enter Customer's Card_ID");
		String custID = keyboard.next();
		
		//Look up this Customer
		try {
			Statement sqlStatement = databaseConnection.createStatement();
			String queryString = "SELECT COUNT(*) as N FROM CUSTOMER WHERE Card_ID= '"+ custID + "';";
			ResultSet rs = sqlStatement.executeQuery(queryString);
			rs.next();
			if (rs.getInt("N") == 0)
			{
				System.out.println("Customer's Card_ID Not Found\n");
				return;
			}
		}
		catch(SQLException err) {
			System.out.println("SQL Exception " + err.getMessage()+ " occured looking up Customer's Cust_ID \n");
			
		}
		
		System.out.print("Enter book's ISBN: ");
		String isbn = keyboard.next();
		
		try {
			Statement sqlStatement = databaseConnection.createStatement();
			String queryString = "SELECT COUNT(*) as N FROM TITLE WHERE ISBN= '"+ isbn + "';";
			ResultSet rs = sqlStatement.executeQuery(queryString);
			rs.next();
			if (rs.getInt("N") == 0)
			{
				System.out.println("Book's ISBN Not Found\n");
				return;
			}
		}
		catch(SQLException err){
			System.out.println("SQL Exception " + err.getMessage()+ " occured looking up Book ISBN\n");

		}
		
		
		try {
			Statement sqlStatement = databaseConnection.createStatement();
			String update = "UPDATE TITLE SET Card_ID = " + custID + ", Date_out = GETDATE() " + "WHERE ISBN = '" +isbn + "';";
			sqlStatement.executeUpdate(update);
			
		}
		catch(SQLException err){
			System.out.println("SQL Exception " + err.getMessage()+ " occured checking out Book\n");

		}
	}
	private static void displayBookList(ResultSet rs)
	{
		// Accepts a ResultSet created from the book lookup menu.  The book lookup menu's ResultSets
		// consist of the book's ISBN, the Author's Name, and the book's Title.
		try
		{
			if (!rs.isBeforeFirst())
			{
				System.out.println("No Matches Found\n");
			}
			else
			{
				System.out.println("IN?       ISBN                Author                    TITLE");
				System.out.println("---  ----------------  -------------------------  ----------------------------------------------------------------------");
				while (rs.next())
				{				
					System.out.printf("%3s  ",(rs.getString("Date_Out")==null) ? "YES": "NO");
					System.out.printf("%13s  %-25s  %-70s\n", 
										
									  trimTo(rs.getString("ISBN"),  13), 
									  trimTo(rs.getString("Name"),  25), 
									  trimTo(rs.getString("Title"), 70));
				}
			}
		}
		catch (SQLException err)
		{
			System.out.println("SQL Exception " + err.getMessage() + " occurred during display\n");
		}
		
	} // end lookUpBook
		
	private static String trimTo(String s, int len)
	{
		// returns S, trimmed to no more than len characters
		if (s.length() < len) return s;
		return s.substring(0, len);
		
	} // end trimTo
	
	private static void ReturnBook()
	{
		//  Return a book by using the stored procedure ReturnBook in the server
		//
		System.out.print("Enter the ISBN of the book to return: ");
		String isbn = keyboard.next();
		
		try
		{
			CallableStatement cstmt = databaseConnection.prepareCall("{call ReturnBook(?,?)}");
			cstmt.setString("ISBN", isbn);
			cstmt.registerOutParameter("RETURN_VALUE", java.sql.Types.INTEGER);
			cstmt.execute();
			int retVal = cstmt.getInt("RETURN_VALUE");
			if (retVal == -1)
			{
				System.out.println("Book is either not a valid book, or it is NOT checked out\n");
				
			}
			else
			{
				System.out.print("Book successfully returned - ");
				if (retVal == 0) System.out.println("NO FINE DUE");
				else             System.out.printf("FINE DUE: $5.2f\n", retVal * 0.10);
			}
			cstmt.close();
			return;
		} // end of try block
		catch (SQLException err)
		{
			System.out.println("SQL Exception " + err.getMessage() + " occurred during return\n");
		}  
		
	} // end of returnBook

	private static void viewcustomerHistory()
	{
		//Prompts to enter the Customer's ID
		System.out.println("Enter Customer ID:");
		int customerId1 = keyboard.nextInt();
		//Setting up connection
		try(Statement statement = databaseConnection.createStatement())
		{	
		String query = "SELECT * FROM CUSTOMER WHERE Card_ID = " + customerId1;
		ResultSet customerResultSet = statement.executeQuery(query);
		if (customerResultSet.next()) {
			System.out.println("Customer #"+ customerId1 +" "+ customerResultSet.getString("LastName") + ", " + customerResultSet.getString("FirstName"));
			System.out.println(customerResultSet.getString("Address"));
			System.out.print(customerResultSet.getString("City") +" ");
			System.out.println(customerResultSet.getString("State")  + " "+ customerResultSet.getString("Zip"));
			System.out.println(customerResultSet.getString("Phone")+"\n");
		}
		else {
			System.out.println("No record found");
		}
		} catch (SQLException e) {
	
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			Statement sqlStatement = databaseConnection.createStatement();
			String queryString = "SELECT S.*, A.Date_In, A.Fine_Due, A.Fine_Paid, A.Paid_On FROM\r\n"
					+ "(SELECT H.Card_ID,T.Title, H.ISBN, H.DATE_OUT FROM HISTORY H, Title T WHERE T.ISBN = H.ISBN and H.Card_ID = " +customerId1 +"\r\n"
					+ "UNION\r\n"
					+ "SELECT Card_ID,Title, ISBN, DATE_OUT FROM Title WHERE Date_Out is not NULL and Card_ID =" +customerId1 +") S\r\n"
					+ "LEFT JOIN HISTORY A\r\n"
					+ "ON A.ISBN = S.ISBN and A.Card_ID = S.Card_ID and A.Date_Out = S.Date_Out order by S.Date_Out ";
			ResultSet rs = sqlStatement.executeQuery(queryString);
			try
			{
				if (!rs.isBeforeFirst())
				{
					System.out.println("No Matches Found\n");
				}
				else
				{
					System.out.println("   ISBN        Title                                                                                                           Date_Out    Date_In      Days_Out   Fine_Due    Fine_Paid       Paid_On     ");
					System.out.println("------------   ------------------------------------------------------------------------------------------------------------    --------    -------      -------    --------    ---------       -------     ");
					int count = 0;
					float fine = 0;
					LocalDate localDate2;
					while (rs.next())
					{		
						Date Date_in = rs.getDate("Date_In");
						Date Date_Out = rs.getDate("Date_Out");
						
						LocalDate localDate1 = Date_Out.toLocalDate();
						
						if (Date_in  != null){
							
						localDate2 = Date_in.toLocalDate();
						}
						else {
						localDate2 = LocalDate.now();
						}


				        long days = java.time.temporal.ChronoUnit.DAYS.between(localDate1, localDate2);
				        float fineDue;
				        if(days>14) {
				        	fineDue = (float) ((days-14)*0.10);
				        	
				        }
				        else {
				        	fineDue = 0;
				        }
						System.out.printf("%13s  %-110s  %-12s %-12s  %-8s  %-12s  %-12s %-12s\n", 
											
										  trimTo(rs.getString("ISBN"),  13), 
										  trimTo(rs.getString("Title"),  110), 
										  trimTo(rs.getString("Date_Out"), 12),
										  (rs.getString("Date_In")==null) ? "N/A": rs.getString("Date_In"),
										  days,
										  (rs.getString("Fine_Due")==null) ? fineDue: rs.getString("Fine_Due"),
										  (rs.getString("Fine_Paid")==null) ? "N/A": rs.getString("Fine_Paid"),
						  					(rs.getString("Paid_On")==null) ? "N/A": rs.getString("Paid_On"));

					count ++;
					fine = fine + rs.getFloat("Fine_Due");
					
					}
					System.out.println("\nTotal Books checked out is " + count + " total fine assessed is " + fine);
				}
			}
			catch (SQLException err)
			{
				System.out.println("SQL Exception " + err.getMessage() + " occurred during View History\n");
			}
			
			rs.close();
		}
		catch (SQLException err)
		{
			System.out.println("SQL Exception " + err.getMessage() + " occurred during View History\n");
		}
	}

	private static void ShowBooksOnLoan()
	{

		try
		{
			Statement sqlStatement = databaseConnection.createStatement();
			String queryString = "SELECT T.ISBN, T.Title, C.FirstName, C.LastName, T.Card_ID, T.Date_Out FROM Title T, CUSTOMER C where C.Card_ID = T.Card_ID order by Date_Out;";
			ResultSet rs = sqlStatement.executeQuery(queryString);

			try
			{
				if (!rs.isBeforeFirst())
				{
					System.out.println("No Matches Found\n");
				}
				else
				{
					System.out.println("   ISBN        Title                                                                                                           First_Name  Last_Name	 Cust_ID      Check_Out    DUE_BACK     Days_OUT");
					System.out.println("------------   ------------------------------------------------------------------------------------------------------------    --------    -------       --------     ---------    ---------    --------   ");

					while (rs.next())
					{
						//Calculating the Due date and, how many days it has already been out.
						Date Date_Out = rs.getDate("Date_Out");
						java.util.Calendar cal = java.util.Calendar.getInstance();
						cal.setTime(Date_Out);
						cal.add(Calendar.DAY_OF_MONTH,14);
						java.util.Date updatedDate = cal.getTime();
						java.text.SimpleDateFormat Date_Format = new java.text.SimpleDateFormat("yyyy-MM-dd");
						String formattedDate = Date_Format.format(updatedDate);
						java.time.LocalDate currentDate = java.time.LocalDate.now();
						LocalDate localDate1 = Date_Out.toLocalDate();
				        long days = java.time.temporal.ChronoUnit.DAYS.between(localDate1, currentDate);
						System.out.printf("%13s  %-110s  %-12s %-12s  %-10s  %-12s %-12s %-12s\n", 
											
										  trimTo(rs.getString("ISBN"),  13), 
										  trimTo(rs.getString("Title"),  110), 
										  trimTo(rs.getString("FirstName"), 12),
										  trimTo(rs.getString("LastName"), 12),
										  trimTo(rs.getString("Card_ID"), 10),
						  					trimTo(rs.getString("Date_Out"), 12),
						  					formattedDate,
						  					days
						  				
						  					);
				
					}
				}
			}
			catch (SQLException err)
			{
				System.out.println("SQL Exception " + err.getMessage() + " occurred during Show books on loan\n");
			}
			
			rs.close();
		}
		catch (SQLException err)
		{
			System.out.println("SQL Exception " + err.getMessage() + " occurred during Show books on loan\n");
		}
	}

} // end class


