package de.geosearchef.hnsdroid.web;

import android.content.Context;
import android.os.Bundle;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import de.geosearchef.hnsdroid.toolbox.Callback;
import de.geosearchef.hnsdroid.toolbox.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpTemplate {

	private final Bundle bundle = new Bundle();
	private final Gson gson = new Gson();

	private final RequestQueue queue;

	public HttpTemplate(Context context) {
		queue = Volley.newRequestQueue(context);
	}

	//Method: Request.Method
	public void sendAsyncRequest(String route, int method, Object object, final Callback<String> callback) {
		String url = buildURL(route);
		JSONObject json;
		try {
			json = new JSONObject(gson.toJson(object));
		} catch (JSONException e) {
			Logger.error("Error while serializing", e);
			callback.onFailure(e);
			return;
		}

		JsonObjectRequest request = new JsonObjectRequest(method, url, json,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						callback.onSuccess(response.toString());
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.error("Error while sending json request", error);
						callback.onFailure(error);
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json; charset=utf-8");
				return headers;
			}
		};

		request.setTag(object);
		queue.add(request);
	}

	public String sendSyncRequest(String route, int method, Object object) {
		final String[] result = new String[2];
		sendAsyncRequest(route, method, object, new Callback<String>() {
			@Override
			public void onSuccess(String o) {
				synchronized (result) {
					result[0] = o;
					result.notifyAll();
				}
			}
			@Override
			public void onFailure(Exception e) {
				synchronized (result) {
					result[1] = e.toString();
					result.notifyAll();
				}
			}
		});

		synchronized (result) {
			while(result[0] == null && result[1] == null) {
				try {
					result.wait();
				} catch (InterruptedException e) {
					Logger.error(e);
				}
			}
		}

		if(result[0] != null) {
			return result[0];
		} else {
			throw new HttpException(result[1]);
		}
	}

	public String buildURL(String route) {
		if(! route.startsWith("/")) {
			route = "/" + route;
		}
		return String.format("http://%s:%d%s", WebService.getConnectedServerAddress(), WebService.getConnectedServerPort(), route);
	}

	public class HttpException extends RuntimeException {
		public HttpException(String message) {
			super(message);
		}
	}
}
