using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WindTurbineAnalyzerServer.Models
{
    //I think this should be a struct?
    public class RecordingInfo
    {
        public Guid IDGUID { get; set; }
        public string RecordingName { get; set; }
        public DateTime DateRecording { get; set; }
        public bool HasImages { get; set; }
        public int NumberOfImages { get; set; }
        public bool HasBeenClassified { get; set; }
        public ClassificationResult classificationResult { get; set; }
        public double WindPercent { get; set; } //see if there is a better, less verbose way of doing thisS
        public double WindTurbinePercent { get; set; }
        public double OtherPercent { get; set; }
    }
}
