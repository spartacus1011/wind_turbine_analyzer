using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

//Not really a model, but it fits here
namespace WindTurbineAnalyzerServer.Models
{
    //Should refactor old code to use this enum. Also should add some converters in here
    public enum ClassificationResult
    {
        None, //Just for default to make sure incorrect stuff doesnt slip through
        Wind,
        [Display(Name = "Wind Turbine")]
        WindTurbine,
        Other
    }
}
