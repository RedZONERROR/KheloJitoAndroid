package com.app.khelojito;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "khelojito_config";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String DEFAULT_SERVER_URL = "https://khelojito.top/";

    // GitHub repository raw endpoints (README and docs/link.txt)
    private static final String GITHUB_LINK_TXT_MAIN = "https://raw.githubusercontent.com/RedZONERROR/KheloJitoAndroid/main/docs/link.txt";
    private static final String GITHUB_LINK_TXT_MASTER = "https://raw.githubusercontent.com/RedZONERROR/KheloJitoAndroid/master/docs/link.txt";
    private static final String GITHUB_README_MAIN = "https://raw.githubusercontent.com/RedZONERROR/KheloJitoAndroid/main/README.md";
    private static final String GITHUB_README_MASTER = "https://raw.githubusercontent.com/RedZONERROR/KheloJitoAndroid/master/README.md";

    private String currentServerUrl;
    private String currentDomain;
    private SharedPreferences sharedPreferences;

    private WebView webView;
    private FrameLayout customViewContainer;
    private View loadingLayout;
    private ProgressBar progressBarHorizontal;
    private View offlineLayout;
    private Button btnRetry;

    // HTML5 Fullscreen state
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    // File Chooser state for support attachments
    private ValueCallback<Uri[]> fileUploadCallback;
    private ActivityResultLauncher<Intent> fileChooserLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable hardware acceleration at window level
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );

        setImmersiveFullscreen();
        setContentView(R.layout.activity_main);

        // Load saved server URL from SharedPreferences or fallback to default
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentServerUrl = sharedPreferences.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL);
        currentDomain = extractDomain(currentServerUrl);

        initViews();
        setupFileChooser();
        setupWebView();

        loadGameUrl();
        syncServerUrlFromGitHub();
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        customViewContainer = findViewById(R.id.customViewContainer);
        loadingLayout = findViewById(R.id.loadingLayout);
        progressBarHorizontal = findViewById(R.id.progressBarHorizontal);
        offlineLayout = findViewById(R.id.offlineLayout);
        btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> {
            offlineLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);
            syncServerUrlFromGitHub();
            loadGameUrl();
        });
    }

    private void setImmersiveFullscreen() {
        Window window = getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            WindowCompat.setDecorFitsSystemWindows(window, false);

            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                window.setAttributes(lp);
            }
        }
    }

    private void setupFileChooser() {
        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (fileUploadCallback == null) return;

                    Uri[] results = null;
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getData() != null) {
                            results = new Uri[]{data.getData()};
                        } else if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            results = new Uri[count];
                            for (int i = 0; i < count; i++) {
                                results[i] = data.getClipData().getItemAt(i).getUri();
                            }
                        }
                    }
                    fileUploadCallback.onReceiveValue(results);
                    fileUploadCallback = null;
                }
        );
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();

        // 1. Core JS, DOM Storage & Database (Essential for React SPA & Socket.io)
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        // 2. Audio & Media (Autoplay card sounds, chips, game effects without tap blocks)
        settings.setMediaPlaybackRequiresUserGesture(false);

        // 3. Smart Cache Optimization
        File cacheDir = new File(getCacheDir(), "khelojito_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if (isNetworkAvailable()) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        // 4. Viewport & 0ms Touch Latency (No double-tap zoom delay on game buttons)
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        // 5. File & Content Access
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        // 6. Security & Cookies
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        CookieManager.getInstance().setAcceptCookie(true);

        // 7. Hardware Layer Rendering & Background Color
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setBackgroundColor(0xFF020617);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        settings.setLoadsImagesAutomatically(true);

        // 8. Custom WebViewClient
        webView.setWebViewClient(new CustomWebViewClient());

        // 9. Custom WebChromeClient
        webView.setWebChromeClient(new CustomWebChromeClient());
    }

    private void loadGameUrl() {
        if (!isNetworkAvailable()) {
            offlineLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            return;
        }
        webView.loadUrl(currentServerUrl);
    }

    private void syncServerUrlFromGitHub() {
        new Thread(() -> {
            String extractedUrl = fetchUrlFromGitHub();
            if (extractedUrl != null && !extractedUrl.isEmpty()) {
                boolean isDifferent = !extractedUrl.equalsIgnoreCase(currentServerUrl);
                currentServerUrl = extractedUrl;
                currentDomain = extractDomain(currentServerUrl);
                sharedPreferences.edit().putString(KEY_SERVER_URL, currentServerUrl).apply();

                if (isDifferent) {
                    runOnUiThread(() -> {
                        if (webView != null) {
                            webView.loadUrl(currentServerUrl);
                        }
                    });
                }
            }
        }).start();
    }

    private String fetchUrlFromGitHub() {
        // Priority 1: Check docs/link.txt (dedicated URL file)
        String linkTxt = downloadUrl(GITHUB_LINK_TXT_MAIN);
        if (linkTxt == null || linkTxt.trim().isEmpty()) {
            linkTxt = downloadUrl(GITHUB_LINK_TXT_MASTER);
        }
        if (linkTxt != null && !linkTxt.trim().isEmpty()) {
            String parsed = parseServerUrlFromMarkdown(linkTxt);
            if (parsed != null) return parsed;
        }

        // Priority 2: Check README.md
        String content = downloadUrl(GITHUB_README_MAIN);
        if (content == null || content.trim().isEmpty()) {
            content = downloadUrl(GITHUB_README_MASTER);
        }
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        return parseServerUrlFromMarkdown(content);
    }

    private String downloadUrl(String urlString) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 KheloJitoAndroid");
            conn.setUseCaches(false);

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                return sb.toString();
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    private String parseServerUrlFromMarkdown(String markdown) {
        // Look for markdown links: [KheloJito](https://khelojito.top/) or any [Text](https://...)
        Pattern markdownPattern = Pattern.compile("\\[.*?\\]\\((https?://[^\\s\\)]+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = markdownPattern.matcher(markdown);
        while (matcher.find()) {
            String candidate = matcher.group(1);
            if (isValidCandidateUrl(candidate)) {
                return sanitizeUrl(candidate);
            }
        }

        // Fallback: look for raw https?:// URLs
        Pattern rawPattern = Pattern.compile("https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:/[^\\s\\)\\]]*)?", Pattern.CASE_INSENSITIVE);
        Matcher rawMatcher = rawPattern.matcher(markdown);
        while (rawMatcher.find()) {
            String candidate = rawMatcher.group(0);
            if (isValidCandidateUrl(candidate)) {
                return sanitizeUrl(candidate);
            }
        }

        return null;
    }

    private boolean isValidCandidateUrl(String candidate) {
        if (candidate == null) return false;
        String lower = candidate.toLowerCase();
        // Ignore GitHub's own URLs
        if (lower.contains("github.com") || lower.contains("githubusercontent.com")) {
            return false;
        }
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    private String sanitizeUrl(String url) {
        url = url.trim();
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }

    private String extractDomain(String url) {
        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            return host != null ? host : "khelojito.top";
        } catch (Exception e) {
            return "khelojito.top";
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        return caps != null && (
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }

    // ── Custom WebViewClient ─────────────────────────────────────────
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            return handleUriNavigation(view, uri);
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            return handleUriNavigation(view, uri);
        }

        private boolean handleUriNavigation(WebView view, Uri uri) {
            if (uri == null) return false;

            String scheme = uri.getScheme();
            if (scheme != null && !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                // Intercept UPI, WhatsApp, Telegram, PhonePe, Paytm, tel, mailto
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    return true; // Avoid ERR_UNKNOWN_URL_SCHEME crash
                }
            }

            // Keep internal game pages inside this WebView (supporting dynamic domain)
            String host = uri.getHost();
            if (host != null && (host.contains(currentDomain) || host.contains("khelojito.top"))) {
                return false;
            }

            // External website links open in external browser
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBarHorizontal.setVisibility(View.VISIBLE);
            offlineLayout.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBarHorizontal.setVisibility(View.GONE);
            if (loadingLayout.getVisibility() == View.VISIBLE) {
                loadingLayout.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> {
                            loadingLayout.setVisibility(View.GONE);
                            loadingLayout.setAlpha(1f);
                        });
            }
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                offlineLayout.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
            }
        }
    }

    // ── Custom WebChromeClient ───────────────────────────────────────
    private class CustomWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBarHorizontal.setProgress(newProgress);
            if (newProgress == 100) {
                progressBarHorizontal.setVisibility(View.GONE);
            }
        }

        // HTML5 Fullscreen Support (For Teen Patti requestFullscreen)
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (customView != null) {
                callback.onCustomViewHidden();
                return;
            }
            customView = view;
            customViewCallback = callback;
            customViewContainer.addView(view);
            customViewContainer.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            setImmersiveFullscreen();
        }

        @Override
        public void onHideCustomView() {
            if (customView == null) return;

            customViewContainer.removeView(customView);
            customView = null;
            customViewContainer.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);

            if (customViewCallback != null) {
                customViewCallback.onCustomViewHidden();
                customViewCallback = null;
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            setImmersiveFullscreen();
        }

        // File Chooser Support (<input type="file"> for Support Chat)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (fileUploadCallback != null) {
                fileUploadCallback.onReceiveValue(null);
            }
            fileUploadCallback = filePathCallback;

            try {
                Intent intent = fileChooserParams.createIntent();
                fileChooserLauncher.launch(intent);
                return true;
            } catch (Exception e) {
                fileUploadCallback = null;
                return false;
            }
        }
    }

    // ── Lifecycle Handling ───────────────────────────────────────────
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setImmersiveFullscreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
        setImmersiveFullscreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
        CookieManager.getInstance().flush();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            // Exit HTML5 fullscreen if active
            if (webView.getWebChromeClient() != null) {
                ((CustomWebChromeClient) webView.getWebChromeClient()).onHideCustomView();
            }
            return;
        }

        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
