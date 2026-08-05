package com.df.pocket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
	private WebView webView;

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setStatusBarColor(Color.BLACK);
		getWindow().setNavigationBarColor(Color.BLACK);
		getWindow().getDecorView().setSystemUiVisibility(
			View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		);

		setContentView(R.layout.activity_main);
		webView = findViewById(R.id.webview);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
			WebView.setWebContentsDebuggingEnabled(true);
		}

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setMediaPlaybackRequiresUserGesture(false);
		settings.setAllowFileAccess(true);
		settings.setAllowContentAccess(true);
		settings.setAllowFileAccessFromFileURLs(true);
		settings.setAllowUniversalAccessFromFileURLs(true);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);

		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		settings.setSafeBrowsingEnabled(false);
		settings.setOffscreenPreRaster(false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		webView.setBackgroundColor(Color.BLACK);

		webView.addJavascriptInterface(new WebAppInterface(), "AndroidBridge");

		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new WebChromeClient());
		loadGame();
	}

	private void loadGame() {
		webView.loadDataWithBaseURL(
			"https://play.dragonfable.com/game/",
			readAsset("index.html"),
			"text/html",
			"UTF-8",
			"https://play.dragonfable.com/game/"
		);
	}

	private class WebAppInterface {
		@JavascriptInterface
		public void reloadGame() {
			runOnUiThread(MainActivity.this::loadGame);
		}
	}

	@Override
	public void onBackPressed() {
		if (webView != null && webView.canGoBack()) {
			webView.goBack();
			return;
		}

		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		if (webView != null) {
			webView.destroy();
			webView = null;
		}

		super.onDestroy();
	}

	private String readAsset(String fileName) {
		StringBuilder builder = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
			getAssets().open(fileName),
			StandardCharsets.UTF_8
		))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append('\n');
			}
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to load " + fileName, exception);
		}

		return builder.toString();
	}
}