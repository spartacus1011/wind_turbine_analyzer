using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace WindTurbineAnalyzerServer.Tools
{
    public class TCPSocketSyncronous
    {
        public void StartClient()
        {
            // Data buffer for incoming data.  
            byte[] bytes = new byte[1024];

            // Connect to a remote device.  
            try
            {
                // Establish the remote endpoint for the socket.  
                // This example uses port 11000 on the local computer.  
                IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
                IPAddress ipAddress = ipHostInfo.AddressList[0];
                IPEndPoint remoteEP = new IPEndPoint(ipAddress, 11000);

                // Create a TCP/IP  socket.  
                Socket sender = new Socket(ipAddress.AddressFamily,
                    SocketType.Stream, ProtocolType.Tcp);

                // Connect the socket to the remote endpoint. Catch any errors.  
                try
                {
                    sender.Connect(remoteEP);

                    Console.WriteLine("Socket connected to {0}",
                        sender.RemoteEndPoint.ToString());

                    // Encode the data string into a byte array.  
                    byte[] msg = Encoding.ASCII.GetBytes("This is a test<EOF>");

                    // Send the data through the socket.  
                    int bytesSent = sender.Send(msg);

                    // Receive the response from the remote device.  
                    int bytesRec = sender.Receive(bytes);
                    Console.WriteLine("Echoed test = {0}",
                        Encoding.ASCII.GetString(bytes, 0, bytesRec));

                    // Release the socket.  
                    sender.Shutdown(SocketShutdown.Both);
                    sender.Close();

                }
                catch (ArgumentNullException ane)
                {
                    Console.WriteLine("ArgumentNullException : {0}", ane.ToString());
                }
                catch (SocketException se)
                {
                    Console.WriteLine("SocketException : {0}", se.ToString());
                }
                catch (Exception e)
                {
                    Console.WriteLine("Unexpected exception : {0}", e.ToString());
                }

            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        public delegate void UpdateStatusText(string message);


        //should probs rename this
        public void StartListening(UpdateStatusText updateStatusText)
        {
            updateStatusText("Setting up");
            // Data buffer for incoming data.  
            byte[] bytes = new Byte[1024];

            // Establish the local endpoint for the socket.  
            // Dns.GetHostName returns the name of the   
            // host running the application.  
            IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
            IPAddress ipAddress = ipHostInfo.AddressList[1];
            IPEndPoint localEndPoint = new IPEndPoint(ipAddress, 11000);

            // Create a TCP/IP socket.  
            Socket listener = new Socket(ipAddress.AddressFamily,
                SocketType.Stream, ProtocolType.Tcp);

            // Bind the socket to the local endpoint and   
            // listen for incoming connections.  
            try
            {
                listener.Bind(localEndPoint);
                listener.Listen(10);

                // Start listening for connections.  
                while (true)
                {
                    //Console.WriteLine("Waiting for a connection...");
                    updateStatusText("Waiting for a connection...");
                    // Program is suspended while waiting for an incoming connection.  
                    Socket handler = listener.Accept();
                    updateStatusText("Connection recieved. Waiting for data");
                    // An incoming connection needs to be processed.  

                    List<byte> AllData = new List<byte>(); //Made a list because its easier to have variable size
                    string fileInfo = "";
                    string tryString = "";

                    //Receiving part A
                    //while (true)
                    {
                        int bytesCountA = handler.Receive(bytes);
                        fileInfo += Encoding.UTF8.GetString(bytes, 0, bytesCountA);

                        //if (fileInfo.Contains("\u0004")) //this means end of transmition. I think??
                            //break;
                    }
                    fileInfo = fileInfo.Replace("\u0005",""); //removing the EOF tag
                    fileInfo = fileInfo.Replace("\0","");
                    fileInfo = fileInfo.Replace("$",""); //Not sure where this $ comes from

                    fileInfo = Path.GetInvalidFileNameChars().Aggregate(fileInfo, (current, c) => current.Replace(c.ToString(), string.Empty)); // force remove any invalid chars
                    //Receiving part B
                    handler = listener.Accept();
                    while (true)
                    {
                        int bytesRec = handler.Receive(bytes);

                        tryString += Encoding.ASCII.GetString(bytes, 0, bytesRec); //I feel like this is a bad idea
                        if (tryString.Contains("<EOF>")) { 
                            break;
                        }

                        for (int i = 0; i < bytesRec; i++) {
                            AllData.Add(bytes[i]);
                        }
                    }

                    File.WriteAllBytes("ReceivedAudio\\" + fileInfo + ".wav", AllData.ToArray());

                    handler.Shutdown(SocketShutdown.Both);
                    handler.Close();
                }

            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }



    }
}
