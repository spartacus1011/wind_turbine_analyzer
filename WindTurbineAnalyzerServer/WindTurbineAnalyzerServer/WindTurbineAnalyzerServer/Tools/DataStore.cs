using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SQLite;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using WindTurbineAnalyzerServer.Models;

namespace WindTurbineAnalyzerServer.Tools
{
    public class DataStore
    {
        private SQLiteConnection connection;

        private readonly string RecordingInfoTableName = "Recording_Info";
        private readonly string RecordingInfoTableData = "";
        private readonly string RecordingInfoTableDefinition = "";

        private readonly string RecordingClassificationImageResultTableName = "Recording_Classification_Image_Result";
        private readonly string RecordingClassificationImageResultTableData = "";
        private readonly string RecordingClassificationImageResultTableDefinition = "";

        private RecordingInfo dummyRecordingInfo = new RecordingInfo();
        private RecordingClassificationImageResult dummyRecordingClassificationImageResult = new RecordingClassificationImageResult();

        public DataStore(string dbPath)
        {
            //setup db info
            foreach (PropertyInfo prop in dummyRecordingInfo.GetType().GetProperties())
            {
                RecordingInfoTableData += prop.Name + ", ";

                Type propTypeType = prop.PropertyType;
                RecordingInfoTableDefinition += prop.Name + " " + propTypeType.Name + ", ";
            }
            RecordingInfoTableData = RecordingInfoTableData.Remove(RecordingInfoTableData.Length -2,2);
            RecordingInfoTableDefinition = RecordingInfoTableDefinition.Remove(RecordingInfoTableDefinition.Length - 2, 2);


            foreach (PropertyInfo prop in dummyRecordingClassificationImageResult.GetType().GetProperties())
            {
                RecordingClassificationImageResultTableData += prop.Name + ", ";

                Type propTypeType = prop.PropertyType;
                RecordingClassificationImageResultTableDefinition += prop.Name + " " + propTypeType.Name + ", ";
            }
            RecordingClassificationImageResultTableData = RecordingClassificationImageResultTableData.Remove(RecordingClassificationImageResultTableData.Length - 2, 2);
            RecordingClassificationImageResultTableDefinition = RecordingClassificationImageResultTableDefinition.Remove(RecordingClassificationImageResultTableDefinition.Length - 2, 2);

            //setup db
            if (!File.Exists(dbPath))
            {
                connection = DatabaseHelper.ConnectToDatabase(dbPath); //should autocreate if it doesnt exist
                DatabaseHelper.CreateTable(connection, RecordingInfoTableName, RecordingInfoTableDefinition);
                DatabaseHelper.CreateTable(connection, RecordingClassificationImageResultTableName, RecordingClassificationImageResultTableDefinition);
            }
            else //should do versioning check here
            {
                connection = DatabaseHelper.ConnectToDatabase(dbPath);
            }

        }


        public void DisconnectFromDatabase()
        {
            DatabaseHelper.DisconnectFromDatabase(connection);
        }

        /// <summary>
        /// This needs to look through the rows to see if that file already exists and remove and replace it
        /// should also join recording info and results to the same function
        /// </summary>
        /// <param name="recordingInfo"></param>
        public void AddRecordingInfo(RecordingInfo recordingInfo)
        {
            List<object> recordingInfoListObj = new List<object>();

            foreach (PropertyInfo propertyInfo in recordingInfo.GetType().GetProperties())
            {
                recordingInfoListObj.Add(propertyInfo.GetValue(recordingInfo, null));
            }

            DatabaseHelper.AddItem(connection, RecordingInfoTableName, RecordingInfoTableData, recordingInfoListObj);
        }

        //This function may not work as expected. Plz Test
        public void DeleteRecordingInfo(RecordingInfo recordingInfo)
        {
            DatabaseHelper.DeleteItem(connection, RecordingInfoTableName, nameof(recordingInfo.IDGUID), recordingInfo.IDGUID.ToString());
        }

        public void AddRecordingClassificationResults(List<RecordingClassificationImageResult> rcirList)
        {
            //im sure this is bad
            List<List<object>> rcirListObj = new List<List<object>>();

            foreach (RecordingClassificationImageResult rcir in rcirList)
            {
                List<object> rcirObj = new List<object>();
                foreach (PropertyInfo propertyInfo in rcir.GetType().GetProperties())
                {
                    rcirObj.Add(propertyInfo.GetValue(rcir, null));
                }
                rcirListObj.Add(rcirObj);
            }
            DatabaseHelper.AddMultipleItems(connection, RecordingClassificationImageResultTableName, RecordingClassificationImageResultTableData, rcirListObj);
        }

        public void SyncRecordingInfo()
        {
            
        }

    }
}
