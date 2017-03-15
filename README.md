# online-DBMS-Module
An Online Database Management System Module, to store the basic student details on a server, and few MySQL like commands to manipulate the data. This is just a small college project I was working on to demonstrate OOP. It includes TCP/IP socket programming, multi-threading to handle multiple clients simultaneous, serialization, and some other Java features. Please note that this is not to be used as a real world application.

# To Compile the Server
javac Server.java

# To Compile the Client
javac Client.java

# To Start the Server
java Server [Port number]

# To Connect the Client to the Server
java Client <Server IP> [Port number]

# Note
Entering the Port numbers in both the Server and the Client is optional. The default port number is 3500. If you type in a port yourself in the Server, you must also specify the same port in the Client. If both the Server and the Client are run on the same machine, the Server's IP can be substituted with 'localhost' or '127.0.0.1'
