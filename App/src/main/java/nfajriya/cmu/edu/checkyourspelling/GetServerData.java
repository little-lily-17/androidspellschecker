package nfajriya.cmu.edu.checkyourspelling;

/**
 * Created by nurlailifajriyah on 4/6/17.
 */

import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/*class Result to record result from web service
*/
class Result {
    static String value; //record result value, as a string with JSON format

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

public class GetServerData {

    MainActivity ma = null;
    String userInput;
    //method to be called by main activity to do the fetching process
    public void getData(String input, MainActivity ma) {
        this.ma = ma;
        //execute with asynchronous process, will call doInBackground implementation
        new AsyncSearch().execute(input);
    }

    private class AsyncSearch extends AsyncTask<String, Void, String> {
        //doInBackgroud will try to call gerServerData, running in different thread
        protected String doInBackground(String... urls) {
            userInput = urls[0];
            return getServerData(userInput);
        }
        //onPostExecute will be executed after all process regarding to getting result from server is done
        //back to the main thread
        //ready to show the result to the user
        protected void onPostExecute(String input) {
            Result r = new Result();
            //everything has done, call resultReady in the MainActivity
            ma.resultReady(r.getValue());
        }
        //to call doGet and getResult value after get all result from the server
        private String getServerData(String input) {
            Result r = new Result();
            doGet(input, r);
            return r.getValue();
        }

        public int doGet(String input, Result r) {

            // Make an HTTP GET passing the name on the URL line
            r.setValue("");
            String response = "";
            HttpURLConnection conn;

            int status = 0;

            try {
                //get model, manufacture, and release version information from the user device
                String model = Build.MODEL;
                String manufacture = Build.MANUFACTURER;
                String osversion = Build.VERSION.RELEASE;

                //create url object with heroku url, it is url for Task 2, for Task 1 it should be murmuring-crag-90848.herokuapp.com
                //it includes some informations such as user input and user device information as parameter in the URL
                URL url = new URL("https://mysterious-mountain-92638.herokuapp.com/?input=" +
                        URLEncoder.encode(input, "UTF-8") + "&model=" + URLEncoder.encode(model, "UTF-8") + "&manufacture="
                        + URLEncoder.encode(manufacture, "UTF-8") + "&osversion=" + URLEncoder.encode(osversion, "UTF-8") + "&os=android");
                //open http connection
                conn = (HttpURLConnection) url.openConnection();
                //set GET method
                conn.setRequestMethod("GET");
                //tell the server what format we want back
                conn.setRequestProperty("Accept", "text/plain");
                //add request header
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                //get response status
                status = conn.getResponseCode();
                //if status is no 200, something wrong happened, just return.
                if (status != 200) {
                    String msg = conn.getResponseMessage();
                    return conn.getResponseCode();
                }
                String output = "";
                //if status is 200, process success, try to read the result output
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                //read each line from the output
                while ((output = br.readLine()) != null) {
                    response += output;
                }
                //disconnect the connection
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //set value with the server response,
            // after parse it from json format to normal string format
            r.setValue(parseJSON(response));
            // return HTTP status to caller
            return status;
        }
        //parseJSON to parse output from webservice
        public String parseJSON(String text) {
            try {
                JSONObject jo = new JSONObject(text);
                //result is key attribute name in webservice result
                //if not null, return result to be set in Result value
                if (!(jo.isNull("result")))
                    return jo.getString("result");
                //if null, return empty string
                return "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
