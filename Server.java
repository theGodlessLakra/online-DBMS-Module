import java.io.*;
import java.net.*;

public class Server
{

	public static void main(String[] args) throws IOException
	{

		int servPort = (args.length == 1) ? Integer.parseInt(args[0]) : 3500;

		ServerSocket servSock = new ServerSocket(servPort);

		System.out.println("Server started on port " + servPort + "...\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		while (true)
		{

			Socket clntSock = servSock.accept();
			Thread t = new Thread(new ServerThread(clntSock));
			t.start();

		}

	}

}