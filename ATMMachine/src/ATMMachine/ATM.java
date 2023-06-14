package ATMMachine;

import java.text.SimpleDateFormat;
import java.io.*;
import java.time.Instant;
import java.text.DateFormat;  
import java.util.Scanner;
import java.util.Date;
import java.sql.*;

public class ATM {
	
	public static Instant getDateFromString(String string)
    {
        Instant timestamp = null;
 
        timestamp = Instant.parse(string);
 
        return timestamp;
    }

	public static void main(String[] args) throws Exception{
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ATMMachine","root","");
			Statement stmt = con.createStatement();
			PreparedStatement ps = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Scanner sc = new Scanner(System.in);
			System.out.println("Welcome to All in One ATM");
			System.out.println("DO You Have Account IN THE BANK?");
			System.out.println("1. YES");
			System.out.println("2. No");
			System.out.println("Enter Your choice:");
			int ch1 = sc.nextInt();
			if(ch1==1) {
			    System.out.println("Enter the Account Number: ");
				int acc = sc.nextInt();
				System.out.println("Enter Your Pin Number: ");
				int pin = sc.nextInt();
				ResultSet rs = stmt.executeQuery("select * from account_holder_list where Account_Number="+acc+" "
						+ "and Account_Pin="+pin);
				String name = "";
				int balance = 0;
				int count = 0;
				while(rs.next()){
					name = rs.getString(4);
					balance = rs.getInt(5);
					count++;
				}
				int dep_amount = 0;
				int wit_amount = 0;
				String st ;
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String newdate = format.format(date);
				if(count>0){
					System.out.println();
					System.out.println("Hello "+ name +" Welcome to ATM");
					System.out.println();
					while(true){
						System.out.println();
						System.out.println("***************MENU*******************");
						System.out.println();
						System.out.println("Press 1 to Check Balanace");
						System.out.println("Press 2 to Deposit Amount");
						System.out.println("Press 3 to Withdraw Amount");
						System.out.println("Press 4 to check transaction on particular date: ");
						System.out.println("Press 5 to Print the Statement");
						System.out.println("Press 6 to Exit / Quit");
	
						System.out.println();
						System.out.println("Enter Your Choice: ");
						int ch = sc.nextInt();
	
						switch (ch) {
							case 1:
								System.out.println();
								System.out.println("Your Account Balance is :" +balance);
								break;
							
							case 2:
								System.out.println();
							    System.out.println("How much Amount did you want to Deposit:");
								dep_amount = sc.nextInt();
							    balance=balance+dep_amount;
							    ps = con.prepareStatement("insert into transaction_list(Account_Number,Deposit,Balance,Time) "
							    		+ "values ("+acc+","+dep_amount+","+balance+",'"+newdate+"')");
							    ps.execute();
								int add = stmt.executeUpdate("update account_holder_list set Balance ="+balance+" "
										+ "where Account_Number="+acc);
								System.out.println();
							    System.out.println("Successfully Deposited "+ dep_amount +" Rupees into Your Account");
								System.out.println("Now Your Current Balance is : "+balance);
								break;
	
							case 3:
								System.out.println("How much Amount did you want to Withdraw:");
								wit_amount = sc.nextInt();
								if(wit_amount>balance){
									System.out.println();
									System.out.println("Your balance is insufficient");
									wit_amount = 0;
								}
								else {
									balance=balance-wit_amount;
									ps = con.prepareStatement("insert into transaction_list(Account_Number,Withdraw,Time,Balance) "
											+ "values ("+acc+","+wit_amount+",'"+newdate+"',"+balance+")");
									ps.execute();
									int sub = stmt.executeUpdate("update account_holder_list set Balance ="+balance+" "
											+ "where Account_Number="+acc);
									System.out.println();
									System.out.println("Successfully Withdrew "+ wit_amount +" Rupees from Your Account");
									System.out.println("Now Your Current Balance is : "+balance);
								}
								break;
								
							case 4: 
								System.out.println("Enter the date in yyyy-mm-dd format:");
								st = br.readLine();
								boolean transactionFound = false;
								ResultSet rs2 = stmt.executeQuery("select * from transaction_list where Account_Number="+acc);
								while (rs2.next()) {
									Date date1 = rs2.getDate(6);  
					                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
					                String strDate = dateFormat.format(date1);
									if(st.equalsIgnoreCase(strDate)) {
									System.out.println("Date - "+rs2.getDate(6)+" / Time - "+rs2.getTime(6));
									System.out.println("Current balance: "+rs2.getInt(5));
									System.out.println("Deposited Amount: "+rs2.getInt(3));
									System.out.println("Withdrew Amount: "+rs2.getInt(4));
									System.out.println();
									transactionFound = true;
									}
								}
								if(!transactionFound) {
									System.out.println();
									System.out.println("transaction did not take place on that particular date");
								}
								break;
							
							case 5:
								System.out.println();
								ResultSet rs1 = stmt.executeQuery("select * from transaction_list where Account_Number="+acc);
							    while (rs1.next()) {
							    	System.out.println("Date - "+rs1.getDate(6)+" / Time - "+rs1.getTime(6));
									System.out.println("Current balance: "+rs1.getInt(5));
									System.out.println("Deposited Amount: "+rs1.getInt(3));
									System.out.println("Withdrew Amount: "+rs1.getInt(4));
									System.out.println();
							     }
							    System.out.println();
								System.out.println("Thanks for Coming");
								System.out.println();
								break;
	
							case 6: 
							    System.exit(0);
								break;
	
							default:
								System.out.println();
							    System.out.println("Enter the correct choice");
								break;
						}
					}
				}
				else{
					System.out.println("Wrong Account Number or Pin Number");
				}
			}
			else if(ch1==2) {
				System.out.println();
				System.out.println("Create Bank Account in the Bank");
			}
			else {
				System.out.println();
				System.out.println("Enter the correct choice");
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}
