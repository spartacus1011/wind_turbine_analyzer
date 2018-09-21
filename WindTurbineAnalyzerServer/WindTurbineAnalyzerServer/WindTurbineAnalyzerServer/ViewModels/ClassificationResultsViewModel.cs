using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WindTurbineAnalyzerServer.Tools;

namespace WindTurbineAnalyzerServer.ViewModels
{
    public class ClassificationResultsViewModel:ObservableObject
    {
        private string result = "";
        public string Result { get { return result; } set { result = value; RaisePropertyChangedEvent("Result"); } }
        private double windPercent = 0;
        public string WindPercentString { get { return string.Format("{0:00.00}",windPercent*100); }}
        private double windTurbinePercent = 0;
        public string WindTurbinePercentString { get { return string.Format("{0:00.00}", windTurbinePercent * 100); } }
        private double otherPercent = 0;
        public string OtherPercentString { get { return string.Format("{0:00.00}", otherPercent* 100); } }


        public ObservableCollection<GridClass> ConfidenceScores { get; set; }

        public ClassificationResultsViewModel(string result, double windPercent, double windTurbinePercent, double otherPercent, float[,] confidenceScores) {

            Result = result;
            this.windPercent = windPercent;
            this.windTurbinePercent = windTurbinePercent;
            this.otherPercent = otherPercent;

            ConfidenceScores = new ObservableCollection<GridClass>();
            for (int i = 0; i < confidenceScores.Length/3 -1; i++) { //length / number of output classes
                ConfidenceScores.Add(new GridClass(i,confidenceScores[i, 1], confidenceScores[i,2], confidenceScores[i,0]));
            }


            //RaisePropertyChanged should auto occur after constructor
        }

        public class GridClass {
            public string ImageNumber { get; set; }
            public string WindScore { get; set; }
            public string WindTurbineScore { get; set; }
            public string OtherScore { get; set; }

            public GridClass(int imageNumber, float windScore, float windTurbineScore, float otherScore) {

                WindScore = string.Format("{0:00.0000}",windScore*100);
                WindTurbineScore = string.Format("{0:00.0000}",windTurbineScore*100);
                OtherScore = string.Format("{0:00.0000}", otherScore*100);
                ImageNumber = string.Format("{0:000}",imageNumber);
            }

        }
    }
}
