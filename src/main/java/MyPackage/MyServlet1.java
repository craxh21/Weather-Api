package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet1
 */
public class MyServlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet1() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
			
		//api setup
		String apiKey= "1eb0fa84cacc611b6905474937f57790";
		//get city name from user input
		String city= request.getParameter("city");
		//System.out.println(city);
		
		//creating url for open whether api
		String apiUrl="https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
		
		//api integration
		URL myUrl = new URL(apiUrl);//creating url and passing the string to it
		HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
		conn.setRequestMethod("GET");
		
		//reading the data from network
		InputStream inputStream = conn.getInputStream();
		InputStreamReader isr = new InputStreamReader(inputStream);//to read the stream we are getting from connection
		
		//To take input from reader
		Scanner sc = new Scanner(isr);
		
		//to store the data in string
		StringBuilder responseContent = new StringBuilder();
		while(sc.hasNext()) {
			responseContent.append(sc.nextLine());
		}
		sc.close();//closing the scanner object once job is done
		//System.out.println(responseContent); //this data is in string format. And we want to it in json format
		
		//parsing data into json format
		Gson gson =new Gson();
		JsonObject jo = gson.fromJson(responseContent.toString(), JsonObject.class);
		//System.out.println(jo); //this is in json format.It look same as earlier
		//now we can get seperated data
		
		//Date and time
		long dateTimestamp = jo.get("dt").getAsLong()*1000;
		String date = new Date(dateTimestamp).toString();
		/*
		 * jo.get("dt"): This retrieves a JSON element with the key "dt" from a JSON object represented by the variable jo.

			.getAsLong(): This method converts the JSON element retrieved into a long primitive data type.

		 * 1000: Multiplying the retrieved value by 1000 is a common practice when working with Unix timestamps. This is because Unix timestamps are typically measured in seconds, while Java's Date class expects timestamps to be in milliseconds. Multiplying by 1000 converts the timestamp from seconds to milliseconds.

		long dateTimestamp: The result of this expression is stored in the dateTimestamp variable.
		*/
		
		//Temparature
		double tempKelvin = jo.getAsJsonObject("main").get("temp").getAsDouble();//key  is "main"
		int tempCelcius = (int) (tempKelvin - 273.15);
		
		//Humidity
		int humidity = jo.getAsJsonObject("main").get("humidity").getAsInt();
		
		//wind speed
		double windSpeed = jo.getAsJsonObject("wind").get("speed").getAsDouble();
		
		//weather condition
		String weatherCondition = jo.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		
		//set the data as request attributes for sending to jsp page
		request.setAttribute("date", date);
		request.setAttribute("city",city);
		request.setAttribute("temp", tempCelcius);
		request.setAttribute("weatherCondition", weatherCondition);
		request.setAttribute("humidity",humidity);
		request.setAttribute("windSpeed",windSpeed);
		request.setAttribute("weatherData", responseContent.toString());
		
		conn.disconnect();
		
		//forword the request to index.jsp for rendering
		request.getRequestDispatcher("index.jsp").forward(request, response);
		
		
	}

}
