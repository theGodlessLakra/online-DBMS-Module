import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread implements Runnable
{

	static final int BUFSIZE = 256;

	Socket clntSock;

	byte[] data = new byte[BUFSIZE];
	byte[] cmdBuffer = new byte[BUFSIZE];

	String msg;
	String cmd;

	int login = 0;
	int userExists = 0;
	int dataDel = 0;

	static int databaseRead;
	static int noOfClients;

	String username;
	String password;

	File usersFile = new File("Users.ser");
	File databaseFile = new File("Database.ser");

	ObjectInputStream objInStream;
	ObjectOutputStream objOutStream;

	ObjectInputStream objectInStream;
	ObjectOutputStream objectOutStream;

	List<User> users = new ArrayList<User>();
	static List<Student> students = new ArrayList<Student>();

	public void handleClient()
	{

		noOfClients++;

		printClients(noOfClients);

		try {

			InputStream in = clntSock.getInputStream();
			OutputStream out = clntSock.getOutputStream();

			msg = "\nWelcome to the Student Online Database hosted on " + clntSock.getLocalSocketAddress().toString() + ".\nAuthors: M.Anuj, B.Chaitali, S.Shivani, L.Sumit, V.Sumit, R.Vinay. No rights reserved.\n\nType 'help' for help.\n\n1: Sign Up\n2: Log In\n\nPlease enter your choice";
			sendToClient(msg, out);

			if (!usersFile.exists())
			{
				objOutStream = new ObjectOutputStream(new FileOutputStream("Users.ser"));
			}

			else
			{
				objOutStream = new ObjectOutputStream(new FileOutputStream("Users.ser", true))
				{
					protected void writeStreamHeader() throws IOException
					{
                		reset();
   					}
        		};
			}

			objInStream = new ObjectInputStream(new FileInputStream("Users.ser"));

			while (true)
			{
				try
				{
					users.add((User) objInStream.readObject());
				}
				catch (Exception e)
				{
					break;
				}
			}

			objInStream.close();

			while(login != 1)
			{

				userExists = 0;

				readFromClient(in);

				if (cmd.startsWith("1"))
				{

					msg = "\n Please enter a username : ";
					sendToClient(msg, out);

					readFromClient(in);
					username = cmd.trim();

					for (int i=0; i<users.size(); i++)
					{

						if (((users.get(i)).username).equals(username))
						{

							msg = "\n Username not available.\nPlease enter '1' to sign up or '2' to login.";
							sendToClient(msg, out);

							userExists = 1;
							break;

						}

					}

					if (userExists == 0)
					{

						msg = "\n Please enter a password : ";
						sendToClient(msg, out);

						readFromClient(in);
						password = cmd.trim();

						User user = new User();

						user.username = username;
						user.password = password;

						objOutStream.writeObject(user);
						objOutStream.close();

						login = 1;

						msg = "Login Successful.";
						sendToClient(msg, out);

					}

				}

				else if (cmd.startsWith("2"))
				{

					msg = "\n Please enter your username : ";
					sendToClient(msg, out);

					readFromClient(in);
					username = cmd.trim();

					msg = "\n Please enter your password : ";
					sendToClient(msg, out);

					readFromClient(in);
					password = cmd.trim();

					for (int i=0; i<users.size(); i++)
					{

						if (((users.get(i)).username).equals(username) && ((users.get(i)).password).equals(password))
						{

							login = 1;

							msg = "Login Successful.";
							sendToClient(msg, out);

							break;

						}

					}

					if (login == 0)
					{

						msg = "\n Username or password incorrect.\nPlease enter '1' to sign up or '2' to login.";
						sendToClient(msg, out);

					}

				}

				else if (cmd.startsWith("EXIT"))
				{
					closeConnection();
				}

				else
				{

					msg = "\n Invalid Input. Please enter '1' to sign up or '2' to login.";
					sendToClient(msg, out);

				}

			}

			in.read(cmdBuffer,0,BUFSIZE-1);

			if (!databaseFile.exists())
			{

				msg = "\nThe Database does not exist.\n";
				sendToClient(msg, out);

			}

			else
			{
				if (databaseRead != 1)

				{

					objectInStream = new ObjectInputStream(new FileInputStream("Database.ser"));

					while (true)
					{
						try
						{
							students.add((Student) objectInStream.readObject());
						}
						catch (Exception e)
						{
							break;
						}
					}

					objectInStream.close();

					databaseRead = 1;

				}

				showDatabse(in, out);

			}

			while (true)
			{

				readFromClient(in);

				if (cmd.startsWith("EXIT"))
				{
					closeConnection();
				}

				else if (cmd.startsWith("help"))
				{

					msg = "\nADD\t- to add a new Student to the Databse.\nDELETE\t- to delete a Student from the Databse.\nSHOW\t- to show the Database.\nEXIT\t- to exit Database.\n";
					sendToClient(msg, out);

				}

				else if (cmd.startsWith("ADD "))
				{

					String[] input = cmd.split("\\s+");

					if (input.length < 8 || input.length > 9)
					{

						msg = "\n Usage: ADD <Roll> <Name> <Marks1> <Marks2> <Marks3> <Marks4> <Marks5> < >";
						sendToClient(msg, out);

					}

					else
					{

						Student student = new Student();

						student.roll_no = Integer.parseInt(input[1]);
						student.name = input[2];
						student.mark_sub1 = Integer.parseInt(input[3]);
						student.mark_sub2 = Integer.parseInt(input[4]);
						student.mark_sub3 = Integer.parseInt(input[5]);
						student.mark_sub4 = Integer.parseInt(input[6]);
						student.mark_sub5 = Integer.parseInt(input[7]);

						if (!databaseFile.exists())
						{
							objectOutStream = new ObjectOutputStream(new FileOutputStream("Database.ser"));
						}

						else
						{
							objectOutStream = new ObjectOutputStream(new FileOutputStream("Database.ser", true))
							{
								protected void writeStreamHeader() throws IOException
								{
                					reset();
            					}
        					};
						}

						students.add(student);

						objectOutStream.writeObject(student);
						objectOutStream.close();

						msg = "New Data entered successfully";
						sendToClient(msg, out);

					}

				}

				else if (cmd.startsWith("DELETE "))
				{

					String[] input = cmd.split("\\s+");

					if (input.length != 2)
					{

						msg = "\n Usage: DELETE <Roll no>";
						sendToClient(msg, out);

					}

					else
					{

						for (int i=0; i<students.size(); i++)
						{

							if ((students.get(i)).roll_no == Integer.parseInt(input[1]))
							{

								students.remove(i);
								
								objectOutStream = new ObjectOutputStream(new FileOutputStream("Database.ser"));

								for (int j=0; j<students.size(); j++)
								{
									objectOutStream.writeObject(students.get(j));
								}

								objectOutStream.close();

								msg = "Data deleted successfully.";
								sendToClient(msg, out);

								dataDel = 1;

								break;

							}

						}
						if (dataDel != 1)
						{

							msg = "Unable to locate data.";
							sendToClient(msg, out);

						}

					}

				}

				else if (cmd.startsWith("SHOW"))
				{
					showDatabse(in, out);
				}

				else
				{

					msg = "\nThe command you entered is not valid. Type help for help...";
					sendToClient(msg, out);

				}

			}

		} catch (Exception e) {

			noOfClients--;
			printClients(noOfClients);

			Thread.currentThread().interrupt();
			return;

		}

	}

	public void showDatabse(InputStream in, OutputStream out) throws Exception
	{

		msg = "" + students.size();
		sendToClient(msg, out);

		in.read(cmdBuffer,0,BUFSIZE-1);

		if (students.size() > 0)
		{

			msg = "+------+--------+------+------+------+------+------+";
			sendToClient(msg, out);

			in.read(cmdBuffer,0,BUFSIZE-1);

			msg = "| Roll | Name\t| Sub1 | Sub2 | Sub3 | Sub4 | Sub5 |";
			sendToClient(msg, out);

			in.read(cmdBuffer,0,BUFSIZE-1);

			msg = "+------+--------+------+------+------+------+------+";
			sendToClient(msg, out);

			in.read(cmdBuffer,0,BUFSIZE-1);

			for (int j=0; j<students.size(); j++)
			{

				Student student = students.get(j);

				msg = "|   " + ((student.roll_no > 9) ? student.roll_no : (" " + student.roll_no)) + " | " + student.name + "\t|   " + ((student.mark_sub1 > 9) ? student.mark_sub1 : (" " + student.mark_sub1)) + " |   " + ((student.mark_sub2 > 9) ? student.mark_sub2 : (" " + student.mark_sub2)) + " |   " + ((student.mark_sub3 > 9) ? student.mark_sub3 : (" " + student.mark_sub3)) + " |   " + ((student.mark_sub4 > 9) ? student.mark_sub4 : (" " + student.mark_sub4)) + " |   " + ((student.mark_sub5 > 9) ? student.mark_sub5 : (" " + student.mark_sub5)) + " |";
				sendToClient(msg, out);

				in.read(cmdBuffer,0,BUFSIZE-1);

			}

			msg = "+------+--------+------+------+------+------+------+";
			sendToClient(msg, out);

			in.read(cmdBuffer,0,BUFSIZE-1);
			sendToClient(" ", out);

		}

	}

	public void sendToClient(String message, OutputStream out) throws Exception
	{

    	msg = message;
		data = msg.getBytes();
		out.write(data,0,data.length);

    }

    public void readFromClient(InputStream in) throws Exception
    {

    	for (int i=0; i<cmdBuffer.length; cmdBuffer[i++]=' ');
		in.read(cmdBuffer,0,BUFSIZE-1);
		cmd = new String(cmdBuffer);

    }

    public void closeConnection() throws Exception
    {

        clntSock.close();
		Thread.currentThread().interrupt();
		return;

    }

    public void printClients(int n)
    {
    	System.out.println("Number of Clients connected is... " + n + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    public ServerThread(Socket clntSock)
	{
		this.clntSock = clntSock;
	}

	public void run()
	{
		handleClient();
	}

}