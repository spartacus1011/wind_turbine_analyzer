using System;
using System.Reflection;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using WindTurbineAnalyzerServer.Tools;
using System.Windows.Input;
using System.Windows;
using System.Collections.ObjectModel;
using System.IO;
using System.Net;

namespace WindTurbineAnalyzerServer.ViewModels
{
    public class MainWindowViewModel : ObservableObject
    {
        public ICommand StartTCPListeningCommand { get { return new DelegateCommand(startTCPListening); } }
        public ICommand CreateClassificationImagesCommand { get { return new DelegateCommand(CreateClassificationImages); } }
        public ICommand StartTrainingCommand { get { return new DelegateCommand(StartTrainingNetwork); } }
        public ICommand ClassifyCommand { get { return new DelegateCommand(Classify); } }

        private string statusText = "Good morning";
        public string StatusText { get { return statusText; } set { statusText = value; RaisePropertyChangedEvent("StatusText"); } }

        public bool HasAudioToClassify { get { return AudioFiles.Any() && SelectedAudioFile != ""; } }

        private bool tcpisInactive = true;
        public bool TCPisInactive { get { return tcpisInactive; } set { tcpisInactive = value; RaisePropertyChangedEvent("TCPisInactive"); } }

        private List<string> audioFiles = new List<string>();
        public ObservableCollection<string> AudioFiles { get { return new ObservableCollection<string>(audioFiles); } } //years later, im still not sure that this is the best approach
        private string selectedAudioFile = "";
        public string SelectedAudioFile { get { return selectedAudioFile; } set { selectedAudioFile = value; RaisePropertyChangedEvent("SelectedAudioFile"); RaisePropertyChangedEvent("HasAudioToClassify"); } }

        public string IPAddress{ get; set; } //its ok to not have the private one as the IP address wont change between sessions

        private int sessionReceivedCount = 0; 
        public string SessionReceivedCountString { get { return "Files receiced this session: " + sessionReceivedCount; } }

        private TCPSocketSyncronous tcp = new TCPSocketSyncronous(); //we only really want one TCP connection at a time
        MLApp.MLApp matlab = new MLApp.MLApp();


        public MainWindowViewModel()
        {
            SelectedAudioFile = "";
            //Consider showing a splash or something while this is happening

            IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
            IPAddress ipAddress = ipHostInfo.AddressList[1];
            IPAddress = ipAddress.ToString();
            
            //set up matlab
            matlab.Execute(@"cd D:\RMITUNI\allwindturbineanalyzer\WindTurbineAnalyser"); //everything will be done from this directory

            PopulateReceivedList();

        }

        public void UpdateStatusText(string message) {

            StatusText = message;
        }

        private void startTCPListening() //should really just make an async command
        {
            if (TCPisInactive)
            {

                Task<bool?> asyncListen = new Task<bool?>(startTCPListeningAction);
                asyncListen.Start(); //Need to google how to give the task a callback function

                asyncListen.Wait();

                bool? classify = asyncListen.Result;

                if (classify == true)
                {
                    CreateClassificationImages(pathForClassification, "Classification//" + Path.GetFileNameWithoutExtension(pathForClassification));

                    //we then need to classify images 
                    Classify("Classification//" + Path.GetFileNameWithoutExtension(pathForClassification));

                    tcp.SendClassificationResults(UpdateStatusText, "", new string[] { "", "" });
                    //and send the results back
                }
                //if false we chill the data was just for training, if null there was an error


                TCPisInactive = false;
            }
        }

        private string pathForClassification;

        private bool? startTCPListeningAction()
        { 
            bool? classify = tcp.StartListening(UpdateStatusText, out string path);
            pathForClassification = path;
            TCPisInactive = true; //this might need to be moved
            PopulateReceivedList();
            sessionReceivedCount++;
            RaisePropertyChangedEvent("SessionReceivedCountString");

            return classify;
 
        }


        private int window = 512;
        private int noverlap = 500;
        private int nfft = 1024;

        private void CreateClassificationImages() {
            string outputImageLocation = "Classification//" + Path.GetFileNameWithoutExtension(selectedAudioFile);
            CreateClassificationImages(selectedAudioFile,outputImageLocation);
        }

        private void CreateClassificationImages(string pathToAudio,string pathToOutputImages)
        {
            StatusText = "Creating classification images...";
            object result = null;
            try
            {
                Directory.CreateDirectory(pathToOutputImages);
                matlab.Feval("CreateClassificationImages", 1, out result, pathToAudio, pathToOutputImages, window, noverlap, nfft);
            }
            catch (Exception e)
            {
                Console.WriteLine("Output file location = " + pathToOutputImages);
                StatusText = "An error has occured whilst trying to classify the images: \n" + e.Message;

            }
            object[] res = result as object[];
            StatusText = "Classification images have been created in path: " + pathToOutputImages;
        }

        private void Classify(string selectedAudioPath) {

            object result = null;
            try
            {
                matlab.Feval("Classify", 4, out result, selectedAudioPath);
            }
            catch (Exception e)
            {
                StatusText = "An error has occured whilst trying to classify the images: \n" + e.Message;

            }
            object[] res = result as object[];


            CreateClassificationResultsView((string)res[0], (double)res[1], (double)res[2], (float[,])res[3]);
        }

        private void CreateClassificationResultsView(string result, double windPercent, double windTurbinePercent, float[,] confidenceScores)
        {
            //its ok to do stuff in this manner it will only be a one way interaction between the views
            ClassificationResultsView classificationResultsView = new ClassificationResultsView();
            classificationResultsView.DataContext = new ClassificationResultsViewModel(result, windPercent, windTurbinePercent, confidenceScores);

            classificationResultsView.Show(); //This is in a new thread
        }

        private void Classify() {

            Classify("Classification//" + Path.GetFileNameWithoutExtension(selectedAudioFile));
        }


        private void StartTrainingNetwork() {

            object result = null;
            try
            {
                matlab.Feval("DataTrainer", 1, out result);
            }
            catch (Exception e)
            {
                StatusText = "An error has occured whilst trying to classify the images: \n" + e.Message;

            }
            object[] res = result as object[];
        }

        private void PopulateReceivedList()
        {
            if (Directory.Exists("ReceivedAudio") && Directory.GetFiles("ReceivedAudio").Length > 0)
            {
                audioFiles = Directory.GetFiles("ReceivedAudio").ToList();
            }
            else
            {
                Directory.CreateDirectory("ReceivedAudio");
                audioFiles.Add("The received audio directory could not be found or there were no files in the directory");
            }
            RaisePropertyChangedEvent("AudioFiles");
        }
    }
}
