package com.example.BusinessHttp;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.BanBanBusiness.localStore;

public class HttpUtil {

	public static final int SUCCESS_CODE = 0x123;
	public static final int FAILURE_CODE = 0x124;

	public static void JsonGetRequest(final String uri, final Handler handler,
			final RequestQueue requestQueue) {

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET,
				uri, null, new Response.Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						Message msg = new Message();
						msg.what = SUCCESS_CODE;
						Log.v("HttpUtil", response.toString());
						msg.obj = response;
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						Log.v("HttpUtil", error.toString());
						Message msg = new Message();
						msg.what = FAILURE_CODE;
						handler.sendMessage(msg);
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {

				Map<String, String> headers = new HashMap<String, String>();
				String str = localStore.USER_NAME + ":" + localStore.PASSWORD;
			//	Log.v("HttpUtil JsonGetRequest", "str is:" + str);
				headers.put(
						"Authorization",
						"Basic "
								+ Base64.encodeToString(str.getBytes(),
										Base64.DEFAULT));
				return headers;

			}
		};
		requestQueue.add(jsonObjectRequest);
	}

	public static void NormalPostRequest(final Map<String, String> map,
			final String uri, final Handler handler,
			final RequestQueue requestQueue) {
		NormalPostRequest normalPostRequest = new NormalPostRequest(uri,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Message msg = new Message();
						msg.what = SUCCESS_CODE;
						Log.v("HttpUtil", response.toString());
						msg.obj = response;
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Message msg = new Message();
						msg.what = FAILURE_CODE;
						handler.sendMessage(msg);
					}
				}, map);
		requestQueue.add(normalPostRequest);
	}

	public static class NormalPostRequest extends Request<JSONObject> {
		private Map<String, String> mMap;
		private Listener<JSONObject> mListener;

		public NormalPostRequest(String url, Listener<JSONObject> listener,
				ErrorListener errorListener, Map<String, String> map) {
			super(Request.Method.POST, url, errorListener);

			mListener = listener;
			mMap = map;
		}

		@Override
		protected Map<String, String> getParams() throws AuthFailureError {
			return mMap;
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			Map<String, String> headers = new HashMap<String, String>();
			String str =  localStore.USER_NAME + ":" +  localStore.PASSWORD;
			Log.v("HttpUtil NormalPostRequest", "str is: " + str);
			headers.put(
					"Authorization",
					"Basic "
							+ Base64.encodeToString(str.getBytes(),
									Base64.DEFAULT));
			return headers;
		}

		@Override
		protected Response<JSONObject> parseNetworkResponse(
				NetworkResponse response) {
			try {
				String jsonString = new String(response.data,
						HttpHeaderParser.parseCharset(response.headers));

				return Response.success(new JSONObject(jsonString),
						HttpHeaderParser.parseCacheHeaders(response));
			} catch (UnsupportedEncodingException e) {
				return Response.error(new ParseError(e));
			} catch (JSONException je) {
				return Response.error(new ParseError(je));
			}
		}

		@Override
		protected void deliverResponse(JSONObject response) {
			mListener.onResponse(response);
		}
	}
}