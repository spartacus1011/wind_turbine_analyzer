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
    //not even syncronous anymore
    public class TCPSocketSyncronous
    {
        public delegate void UpdateStatusText(string message);

        //should probs rename this
        public bool? StartListening(UpdateStatusText updateStatusText, out string imagePath, out string ipAddressToSendBackTo)
        {
            imagePath = "";
            ipAddressToSendBackTo = "";
            updateStatusText("Setting up for listening");

            byte[] bytes = new Byte[1024];

            IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
            IPAddress ipAddress = ipHostInfo.AddressList[2];
            IPEndPoint localEndPoint = new IPEndPoint(ipAddress, 11000);

            Socket listener = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                listener.Bind(localEndPoint);
                listener.Listen(10);

                // Start listening for connections.  
                //while (true) //use this for final server when you want it to constantly be waiting for a new connection
                {
                    updateStatusText("Waiting for a connection...");
                    // Program is suspended while waiting for an incoming connection.  
                    Socket handler = listener.Accept();
                    updateStatusText("Connection recieved. Waiting for data");
                    // An incoming connection needs to be processed.  

                    List<byte> AllData = new List<byte>(); //Made a list because its easier to have variable size
                    string purposeAndLayer = "";
                    string tryString = "";

                    //Receiving part A
                    int bytesCountA = handler.Receive(bytes);
                    purposeAndLayer += Encoding.UTF8.GetString(bytes, 0, bytesCountA);

                    purposeAndLayer = purposeAndLayer.Replace("\u0005", ""); //removing the EOF tag
                    purposeAndLayer = purposeAndLayer.Replace("\0", ""); //removing padding bytes
                    purposeAndLayer = purposeAndLayer.Replace("$", ""); //Not sure where this $ comes from

                    string[] info = purposeAndLayer.Split('|');
                    ipAddressToSendBackTo = info[0];
                    string purpose = info[1];
                    string layer = "";
                    if (purpose == "Training")
                        layer = info[2];
                    string fileInfo = info[3];
                    fileInfo = Path.GetInvalidFileNameChars().Aggregate(fileInfo, (current, c) => current.Replace(c.ToString(), string.Empty)); // force remove any invalid chars

                    //should do some kind of error check on all the info received
                    updateStatusText("Received info. Audio file now being received");

                    handler = listener.Accept();
                    while (true)
                    {
                        int bytesRec = handler.Receive(bytes);

                        tryString += Encoding.ASCII.GetString(bytes, 0, bytesRec); //I feel like this is a bad idea
                        if (tryString.Contains("<EOF>"))
                        {
                            break;
                        }

                        for (int i = 0; i < bytesRec; i++)
                        {
                            AllData.Add(bytes[i]);
                        }
                    }

                    if (purpose == "Classification")
                    {
                        imagePath = "ReceivedAudio\\" + fileInfo + ".wav";
                        File.WriteAllBytes(imagePath, AllData.ToArray());
                    }
                    else if (purpose == "Training")
                    {
                        string dir = "DownloadedAudio\\" + layer;
                        if (!Directory.Exists(dir))
                            Directory.CreateDirectory(dir);
                        File.WriteAllBytes(String.Format("{0}\\{1}.wav", dir, fileInfo), AllData.ToArray());
                    }
                    else
                    {
                        //We have a problem
                        throw new Exception("Error: incorrect data purpose");
                    }

                    handler.Shutdown(SocketShutdown.Both);
                    handler.Close();
                    listener.Close();

                    updateStatusText(string.Format("A new {0} file has been downloaded", purpose));
                    return purpose == "Classification";
                }
            }
            catch (Exception e)
            {
                updateStatusText(e.ToString());
                listener.Close();
                return null;
            }
        }


        public bool SendClassificationResults(UpdateStatusText updateStatusText, IPAddress addressToSend ,string classificationResult, string[] classificationPercentages)
        {
            updateStatusText("Setting up for sending");

            byte[] bytes = new Byte[1024];

            //IPAddress ipAddress = new IPAddress(new byte[] { 192,168,0,4});

            IPEndPoint localEndPoint = new IPEndPoint(addressToSend, 11000);

            Socket sender = new Socket(addressToSend.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                //sender.Bind(localEndPoint);

                updateStatusText("Sending data");

                sender.Connect(localEndPoint); //will wait here until phone accepts socket connection


                //data should be sent in parts (or all as one string). Im sure that there are better ways to do this, but this will do
                // *Classification result*|*Wind percentage*, Wind turbine percentage|*Any other future data*
                string percentagesToSend = "";
                foreach (string percent in classificationPercentages)
                {
                    percentagesToSend += percent + ",";
                }

                percentagesToSend.Remove(percentagesToSend.Length -1); //nasty but efficent way to get rid of the final comma

                string toSend = String.Format("{0}|{1}|{2}",classificationResult,percentagesToSend, "<EOF>");

                sender.Send(Encoding.UTF8.GetBytes(toSend));

                sender.Shutdown(SocketShutdown.Both);
                sender.Close();
            }

            catch (Exception e)
            {
                updateStatusText(e.ToString());
                sender.Close();
                return false;
            }

            return true;
        }

    }
}
