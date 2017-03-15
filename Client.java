import java.io.*;
import java.net.*;

public class Client
{

	static final int BUFSIZE = 256;

	static String server_IP;
	static int servPort;

	static byte[] data = new byte[BUFSIZE];
	static byte[] cmdBuffer = new byte[BUFSIZE];

	static String cmd, num;
	static int login = 0;
	
	public static void main(String[] args) throws Exception
	{

		if (args.length < 1)
		{

			System.out.println("Usage: java Client <Server IP> [<Port>]");
			System.exit(0);

		}

		servPort = (args.length == 2) ? Integer.parseInt(args[1]) : 3500;
		server_IP = args[0];

		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Socket clntSock = new Socket(server_IP,servPort);

			InputStream in = clntSock.getInputStream();
			OutputStream out = clntSock.getOutputStream();

			readFromServer(in, 0);

			while (login != 1)
			{

				cmd = br.readLine();
				sendToServer(cmd, out);

				if (cmd.startsWith("EXIT"))
				{

					clntSock.close();
					System.exit(0);

				}

				readFromServer(in, 1);

				if ((new String(data)).startsWith("Login Successful"))
				{

					login = 1;
					break;

				}


			}

			sendToServer(" ", out);

			printDatabase(in, out);

			while (true)
			{

				readFromServer(in, 1);

				cmd = br.readLine();
				sendToServer(cmd, out);

				if (cmd.startsWith("EXIT"))
				{

					clntSock.close();
					System.exit(0);

				}

				if (cmd.startsWith("SHOW"))
				{

					printDatabase(in, out);

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public static void printDatabase(InputStream in, OutputStream out) throws Exception
	{

		for (int i=0; i<data.length; data[i++]=' ');
		in.read(data,0,BUFSIZE-1);

		num = (new String(data)).trim();
		int numOfStudents = Integer.parseInt(num);

		sendToServer(" ", out);

		if (numOfStudents > 0)
		{

			for (int j=0; j<numOfStudents+4; j++)
			{

				readFromServer(in, 1);
				sendToServer(" ", out);

			}

		}

	}

	public static void sendToServer(String command, OutputStream out) throws Exception
	{

    	cmd = command;
		cmdBuffer = cmd.getBytes();
		out.write(cmdBuffer,0,cmdBuffer.length);

    }

    public static void readFromServer(InputStream in, int n) throws Exception
    {

    	for (int i=0; i<data.length; data[i++]=' ');
		in.read(data,0,BUFSIZE-1);

		if (n == 1)
		{
			System.out.println((new String(data)).trim());
		} else
		{
			System.out.println(new String(data));
		}

    }

}