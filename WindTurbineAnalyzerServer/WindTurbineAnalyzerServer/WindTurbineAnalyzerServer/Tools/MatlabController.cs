using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WindTurbineAnalyzerServer.Tools
{
    /// <summary>
    /// Controls the IO between C# and Matlab
    /// NYI
    /// </summary>
    public class MatlabController
    {
        private MLApp.MLApp matlab = new MLApp.MLApp();

        public MatlabController(string directory)
        {
            matlab.Execute(@"cd " + directory); //everything will be done from this directory
        }
    }
}
