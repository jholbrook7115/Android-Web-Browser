package com.holbrookj.androidwebbrowser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	// UI variables
	EditText urlInput;
	Button goButton;
	WebView webBrowser;

	// Other Variables
	String websiteURL;

	Handler showContent = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			webBrowser.loadData((String) msg.obj, "text/html", "UTF-8");
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		urlInput = (EditText) findViewById(R.id.http_edittext);
		goButton = (Button) findViewById(R.id.go_button);
		webBrowser = (WebView) findViewById(R.id.web_browser);
		webBrowser.getSettings().setJavaScriptEnabled(true);
		//webBrowser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		goButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				websiteURL = urlInput.getText().toString();
				System.out.println(websiteURL);
				// code to add "http://" if missing
				if (!(websiteURL.startsWith("http://"))) {
					websiteURL = "http://" + websiteURL;
					urlInput.setText(websiteURL);
					System.out.println(websiteURL);
				}
				if (isNetworkActive()) {
					new URLDownload().execute(websiteURL);
				}
			}

		});
	}

	private class URLDownload extends AsyncTask<String, String, String> {
		String url = "";

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			url = websiteURL;
		}

		@Override
		protected String doInBackground(String... params) {
			String htmlData = htmlDownloader(url);
			//System.out.println("htmlData:  " + htmlData);
			return htmlDownloader(url);
		}

		@Override
		protected void onPostExecute(String result) {
			//super.onPostExecute(result);
			Message htmlCallbackMsg = Message.obtain();
			htmlCallbackMsg.obj = result;
			System.out.println("htmlCallbackMsg:  " + (String)htmlCallbackMsg.obj);
			//webBrowser.loadDataWithBaseURL(url, result, mimeType, encoding, historyUrl)
			showContent.handleMessage(htmlCallbackMsg);
			//
		}
	}

	private String htmlDownloader(String url) {
		String htmlString="";
		InputStream inStream = null;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity httpEntity = response.getEntity();

			inStream = httpEntity.getContent();
			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(inStream, "iso-8859-1"));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			while ((line = buffReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
				// Print to check the String Building
				// System.out.println(line);
			}

			htmlString = stringBuilder.toString();
			// print to check String being passed back is correct
			// System.out.println(jsonText);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (Exception ex) {
			}
		}
		return htmlString;
	}

	private boolean isNetworkActive() {
		ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMan.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		} else {
			System.out.println("ERROR: Network Inactive");
			return false;
		}
	}
}
