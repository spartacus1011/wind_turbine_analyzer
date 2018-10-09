using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WindTurbineAnalyzerServer.Models
{
    public class RecordingClassificationImageResult
    {
        public Guid IDGUID { get; set; }
        public int ImageNumber { get; set; }
        public double WindScore { get; set; }
        public double WindTurbineScore { get; set; }
        public double OtherScore { get; set; }
    }
}
