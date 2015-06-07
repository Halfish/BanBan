package com.example.banban.network;

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
import com.example.banban.other.BBConfigue;

/*
 网络连接共有类
 */
public class HttpUtil {

	public static final int SUCCESS_CODE = 0x123;
	public static final int FAILURE_CODE = 0x124;

	/*
	 * Json请求 (get请求) 参数为uri和当前activity的requestqueue
	 */
	public static void JsonGetRequest(final String uri, final Handler handler,
			final RequestQueue requestQueue) {

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET,
				uri, null, new Response.Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						if (handler != null) {
							// 请求成功的相应
							Message msg = new Message();
							msg.what = SUCCESS_CODE;
							msg.obj = response;
							handler.sendMessage(msg);
						}
					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						Log.v("HttpUtil", error.toString());
						if (handler != null) {
							// 请求失败的响应
							Message msg = new Message();
							msg.what = FAILURE_CODE;
							handler.sendMessage(msg);
						}
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {

				Map<String, String> headers = new HashMap<String, String>();
				String str = BBConfigue.USER_NAME + ":" + BBConfigue.PASSWORD;
				//Log.v("HttpUtil JsonGetRequest", "str is:" + str);
				headers.put(
						"Authorization",
						"Basic "
								+ Base64.encodeToString(str.getBytes(),
										Base64.DEFAULT));
				return headers;

			}
		};
		// 将该请求加入队列中
		requestQueue.add(jsonObjectRequest);
	}

	/*
	 * Json请求 (post请求) 参数为uri,需要发送的map和当前activity的requestqueue
	 */
	public static void NormalPostRequest(final Map<String, String> map,
			final String uri, final Handler handler,
			final RequestQueue requestQueue) {
		NormalPostRequest normalPostRequest = new NormalPostRequest(uri,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (handler != null) {
							// 请求成功的相应
							Message msg = new Message();
							msg.what = SUCCESS_CODE;
							msg.obj = response;
							handler.sendMessage(msg);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(handler != null) {
							// 请求失败的响应
							Message msg = new Message();
							msg.what = FAILURE_CODE;
							handler.sendMessage(msg);
						}
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

		// mMap是已经按照前面的方式,设置了参数的实例
		@Override
		protected Map<String, String> getParams() throws AuthFailureError {
			return mMap;
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			Map<String, String> headers = new HashMap<String, String>();
			String str = BBConfigue.USER_NAME + ":" + BBConfigue.PASSWORD;
			//Log.v("HttpUtil NormalPostRequest", "str is: " + str);
			headers.put(
					"Authorization",
					"Basic "
							+ Base64.encodeToString(str.getBytes(),
									Base64.DEFAULT));
			return headers;
		}

		// 此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
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