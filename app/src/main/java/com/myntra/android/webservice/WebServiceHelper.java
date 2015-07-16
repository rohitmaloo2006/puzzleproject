package com.myntra.android.webservice;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * 
 * @author Rohit
 * 
 */
public class WebServiceHelper {

	private static final int TIMEOUT_SOCKET = 30000;
	private static ClientConnectionManager connMgr;

	private static final String TAG = "WebServiceHelper";

	public WebServiceHelper() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		connMgr = new ThreadSafeClientConnManager(params, registry);
	}

	/**
	 * used for making Http POST call
	 * 
	 * @param url
	 * @param hm
	 * @return
	 */

	public String doPost(String url, HashMap<String, String> hm) {
		String response = "failed";
		DefaultHttpClient httpClient = null;
		HttpPost postMethod = new HttpPost(url);

		if (hm == null)
			return null;

		postMethod.setHeader("Content-Type",
				"application/x-www-form-urlencoded");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Iterator<String> it = hm.keySet().iterator();
			String k, v;

			while (it.hasNext()) {
				k = it.next();
				v = hm.get(k);
				// v = URLEncoder.encode(v, "UTF-8");
				nameValuePairs.add(new BasicNameValuePair(k, v));
			}

			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
			httpClient = new DefaultHttpClient(connMgr, httpParameters);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpClient.execute(postMethod, responseHandler);
			System.err.println("RESPONSE from Server==>" + response.toString());
		} catch (Exception e) {
			response = "failed";
			e.printStackTrace();
		} finally {

		}
		return response;
	}

	/**
	 * used for making http GET calls
	 * 
	 * @param mUrl
	 * @return
	 */
	public String doGet(String mUrl, Map<String, String> extrasMap) {

		String response = null;
		DefaultHttpClient httpClient = null;

		String params = "?";

		if (extrasMap != null) {
			for (Entry<String, String> entry : extrasMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				try {
					value = URLEncoder.encode(value, "UTF-8");
				} catch (Exception e) {
					e.printStackTrace();
				}

				params = params + key + "=" + value + "&";
			}
			params = params.substring(0, params.length() - 1);
			mUrl = mUrl + params;
		}

		System.err.println(mUrl);

		HttpGet httpGet = new HttpGet(mUrl);
		try {
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
			httpClient = new DefaultHttpClient(connMgr, httpParameters);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httpGet, responseHandler);
			System.err.println(response.toString());
			//android.util.Log.d("TAG", response.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return response;
	}
}
