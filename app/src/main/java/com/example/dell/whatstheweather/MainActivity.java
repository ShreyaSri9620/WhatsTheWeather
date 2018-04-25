package com.example.dell.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    Button b1;
    EditText cityName;
    String encodedCityName="";
    TextView weatherFinalReport;

    public void findPlace(View v){

        //code to hide the keyboard when are button is tapped
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//this ges the input method service that our app is corruntly using
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);//cityName.getWindowToken() gives the current window that we coding in context of


        try {
            encodedCityName = URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&APPID=4c7ba19e30c0c9333e434fdce00006b7");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(),"Cannot Find the Weather",Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.weatherButton);
        cityName = (EditText) findViewById(R.id.cityET);
        weatherFinalReport = (TextView) findViewById(R.id.text);


    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                //creating data variable to read the content of our reader
                int data = reader.read();
                //creating while loop which will run and read the data until it reaches to index -1
                while (data != -1) {
                    char current = (char) data;//casting data to a character
                    result += current;//adding the character to the result string
                    data = reader.read();//read the next character
                }

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Cannot Find the Wearther",Toast.LENGTH_SHORT).show();//that will pop up if the user does not enter some valid place for url
            }

            return result;
        }

        //it is called when the doInBackground task is completed and pass the result to it in this case it will pass result string
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String weatherInfo="",mainInfo="";
            //Double m_temp=0.0,m_humidity=0.0,m_pressure=0.0;
            //String s_temp="",s_humidity="",s_pressure="";
            String w_description="",finalWeatherReport="",w_main="";
            //converting the resulting string into JSON format
            try {
                JSONObject jsonObject = new JSONObject(result);//creates JSON object for us from a string
                weatherInfo = jsonObject.getString("weather");//this will extract the weather part of the jsonObject which we created using the result string
                //Log.i("Weather Info:", weatherInfo);
                //mainInfo = jsonObject.getString("main");

                //converting weatherinfo into a JSON array because there can be more than one type of weather for some cases
                JSONArray arrW = new JSONArray(weatherInfo);
                //loop through the content of the weather info that I extracted
                for (int i = 0; i < arrW.length(); i++) {
                    //creating an array for the different part of weatherInfo
                    JSONObject jsonPart = arrW.getJSONObject(i);
                    w_main = jsonPart.getString("main");
                    w_description = jsonPart.getString("description");

                    //converting mainInfo into a JSon Array
               /* JSONArray arrM = new JSONArray(mainInfo);
                for(int j = 0; j < arrM.length(); j++){

                    JSONObject jsonPart2 = arrM.getJSONObject(j);
                    m_temp = jsonPart2.getDouble("temp");
                    m_humidity = jsonPart2.getDouble("humidity");
                    m_pressure = jsonPart2.getDouble("pressure");

                    s_temp=Double.toString(m_temp);
                    s_humidity=Double.toString(m_humidity);
                    s_pressure=Double.toString(m_pressure);

                }
*/
                    if (w_main != "" && w_description != "") {
                        finalWeatherReport = "Weather:\n" + w_main + "\nDescription: " + w_description ;
                                //"\nTemperature: " + s_temp +"\nHumidity: " + s_humidity + "\nPressure: " + s_pressure;
                    }
                }
                if(finalWeatherReport != "")
                {
                    weatherFinalReport.setText(finalWeatherReport);
                    weatherFinalReport.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot Update the Weather",Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Cannot Update or Find the Weather",Toast.LENGTH_SHORT).show();
            }


        }
    }
}