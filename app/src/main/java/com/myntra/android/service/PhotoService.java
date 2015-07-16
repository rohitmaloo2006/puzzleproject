package com.myntra.android.service;

import java.util.ArrayList;

import com.myntra.android.model.Item;
import com.myntra.android.model.JsonFlickrFeed;
import com.myntra.android.webservice.WebServiceHelper;

public class PhotoService {
	private WebServiceHelper webServiceHelper = new WebServiceHelper();

	public ArrayList<String> getPhotoFromServer() {

		ArrayList<String> lImageStrings = new ArrayList<String>();

		String response = webServiceHelper
				.doGet("http://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1",
						null);

		try {
			JsonFlickrFeed webserviceResponse = (JsonFlickrFeed) POJOToJSON
					.fromJson(response, JsonFlickrFeed.class);

			for (int i = 0; i < 9; i++) {
				lImageStrings.add(webserviceResponse.getItems().get(i)
						.getMedia().getM());
			}
			System.err.println("r" + lImageStrings.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lImageStrings;

	}
}