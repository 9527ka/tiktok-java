package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
	public static String get(String url, int timeout) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setReadTimeout(timeout);
		connection.setConnectTimeout(timeout);
		connection.setRequestMethod("GET");

		connection.connect();

		InputStream is = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		return readerToString(reader);
	}

	public static String post(String url, int timeout, String in) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setReadTimeout(timeout);
		connection.setConnectTimeout(timeout);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");

		connection.setDoOutput(true);
		connection.getOutputStream().write(in.getBytes("UTF-8"));
		connection.connect();

		InputStream is = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		return readerToString(reader);
	}

	private static String readerToString(BufferedReader reader) throws IOException {
		StringBuffer buffer = new StringBuffer();
		String line;
		boolean isFirst = true;
		while ((line = reader.readLine()) != null) {
			if (isFirst) {
				isFirst = false;
			} else {
				buffer.append("\r\n");
			}
			buffer.append(line);
		}
		reader.close();
		return buffer.toString();
	}
}
