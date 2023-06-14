// Sample program to demonstrate rudimentary database access using SQL Server in the Library example
//
// Date: 4/11/2023
//
// Author: Larry Thomas, University of Toledo.  (C) 2020, 2022, 2023, LarryThomas.  All Rights Reserved.
//
// REQUIRES THE FOLLOWING:

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class SQL_Test_2
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
			System.out.println("6. Exit                 \n");
			System.out.print  ("Enter Option (1-6): ");
			int option = keyboard.nextInt();
			if (option == 6) return;
			if (option == 1) lookUpBook();
			//if (option == 2) CheckOutBook();
		}	
	}
	
	private static void lookUpBook()
	{
		String partialQuery = "SELECT A.Name, T.Title, T.ISBN " +
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
				System.out.println("    ISBN                Author                    TITLE");
				System.out.println("-------------  -------------------------  ----------------------------------------------------------------------");
				while (rs.next())
				{						
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
} // end class




