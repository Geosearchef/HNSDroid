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
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpTemplate {

	private final Bundle bundle = new Bundle();
	@Getter
	private final Gson gson = new Gson();

	private final RequestQueue queue;

	public HttpTemplate(Context context) {
		queue = Volley.newRequestQueue(context);
	}

	//Method: Request.Method
	public <T> void sendAsyncRequest(String route, int method, Object object, final Class reponseClass, Map<String, String> params, final Callback<T> callback) {
		String url = buildURL(route, params);
		JSONObject json = new JSONObject();
		if(object != null) {
			try {
				json = new JSONObject(gson.toJson(object));
			} catch (JSONException e) {
				Logger.error("Error while serializing", e);
				callback.onFailure(e);
				return;
			}
		}

		JsonObjectRequest request = new JsonObjectRequest(method, url, json,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						callback.onSuccess((T) gson.fromJson(response.toString(), reponseClass));
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

	public <T> T sendSyncRequest(String route, int method, Object object, Class responseClass, Map<String, String> params) {
		final Object[] result = new Object[2];
		sendAsyncRequest(route, method, object, responseClass, params, new Callback<T>() {
			@Override
			public void onSuccess(T o) {
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
			return (T) result[0];
		} else {
			throw new HttpException((String) result[1]);
		}
	}

	public String buildURL(String route, Map<String, String> params) {
		if(! route.startsWith("/")) {
			route = "/" + route;
		}
		StringBuilder paramsString = new StringBuilder();
		if(! params.isEmpty()) {
			paramsString.append("?");
		}

		for(Map.Entry<String, String> param : params.entrySet()) {
			paramsString.append(param.getKey());
			paramsString.append("=");
			paramsString.append(param.getValue());
			paramsString.append("&");
		}

		paramsString.deleteCharAt(paramsString.length() - 1);

		return String.format("http://%s:%d%s%s", WebService.getConnectedServerAddress(), WebService.getConnectedServerPort(), route, paramsString.toString());
	}

	public class HttpException extends RuntimeException {
		public HttpException(String message) {
			super(message);
		}
	}

	public Map<String, String> params(String... s) {
		if(s.length % 2 == 1) {
			throw new RuntimeException("Could not parse params");
		}
		Map<String, String> res = new HashMap<>();
		for(int i = 0;i < s.length / 2;i++) {
			//TODO: sanatize
			res.put(s[i*2], s[i*2+1].replace("&", "").replace(" ", "%20").replace("=", ""));
		}
		return res;
	}
}
