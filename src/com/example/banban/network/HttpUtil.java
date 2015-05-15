package com.example.banban.network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(uri, null,
				new Response.Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						// 请求成功的相应
						Message msg = new Message();
						msg.what = SUCCESS_CODE;
						msg.obj = response;
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						// 请求失败的响应
						Message msg = new Message();
						msg.what = FAILURE_CODE;
						handler.sendMessage(msg);
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {

				Map<String, String> headers = new HashMap<String, String>();
				String str = BBConfigue.USER_NAME + ":" + BBConfigue.PASSWORD;
				Log.v("HttpUtil JsonGetRequest", "str is:" + str);
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
	public static void JsonPostRequest(final Map<String, String> map,
			final String uri, final Handler handler,
			final RequestQueue requestQueue) {
		JSONObject params = new JSONObject(map);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Method.POST, uri, params, new Response.Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						// 请求成功的相应
						Log.v("HttpUtil JsonPostRequest", response + "");
						Message msg = new Message();
						msg.what = SUCCESS_CODE;
						msg.obj = response;
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						// 请求失败的响应
						Log.v("haha", error + "");
						Message msg = new Message();
						msg.what = FAILURE_CODE;
						handler.sendMessage(msg);
					}
				}) {
		};
		requestQueue.add(jsonObjectRequest);

	}

	/*
	 * String请求(获取字符串资源，get)
	 */
	public static void StringGetRequest(final String uri,
			final Handler handler, final RequestQueue requestQueue,
			final TextView view) {
		StringRequest stringRequest = new StringRequest(uri,
				new Response.Listener<String>() {
					public void onResponse(String response) {
						// 请求成功
						Message msg = new Message();
						msg.what = SUCCESS_CODE;
						msg.obj = response;
						handler.sendMessage(msg);

					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						// 请求失败
						Message msg = new Message();
						msg.what = FAILURE_CODE;
						handler.sendMessage(msg);
					}
				});
		// 将该请求加入队列中
		requestQueue.add(stringRequest);
	}

	/*
	 * String(post)
	 */
	public static void StringPostRequest(final String uri,
			final Handler handler, final RequestQueue requestQueue,
			final Map<String, String> map) {
		// 将信息发送出去
		StringRequest stringRequest = new StringRequest(Method.POST, uri,
				new Response.Listener<String>() {
					public void onResponse(String response) {
						Log.v("HttpUtil StringPost", response);
						// 请求成功的
						Message msg = new Message();
						msg.what = SUCCESS_CODE;
						msg.obj = response;
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						// 请求失败
						Message msg = new Message();
						msg.what = FAILURE_CODE;
						handler.sendMessage(msg);
					}
				}) {
			// 重载getparams函数
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params = map;
				return params;
			}

		};
		requestQueue.add(stringRequest);
	}

	// ******************JsonArray get请求（获取json资源）*********//
	public static void JsonArraygetRequest(final String uri,
			final Handler handler, final RequestQueue requestQueue) {
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(uri,
				new Response.Listener<JSONArray>() {
					public void onResponse(JSONArray response) {
						// 请求成功的相应
						Message msg = new Message();
						msg.what = SUCCESS_CODE;
						msg.obj = response;
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						// 请求失败的响应
						Message msg = new Message();
						msg.what = FAILURE_CODE;
						handler.sendMessage(msg);
					}
				});
		requestQueue.add(jsonArrayRequest);
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
						// 请求成功的相应
						Message msg = new Message();
						msg.what = SUCCESS_CODE;
						msg.obj = response;
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// 请求失败的响应
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

		// mMap是已经按照前面的方式,设置了参数的实例
		@Override
		protected Map<String, String> getParams() throws AuthFailureError {
			return mMap;
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			Map<String, String> headers = new HashMap<String, String>();
			String str = BBConfigue.USER_NAME + ":" + BBConfigue.PASSWORD;
			Log.v("HttpUtil NormalPostRequest", "str is: " + str);
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