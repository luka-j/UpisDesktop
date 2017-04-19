package rs.luka.upisstats.desktop.io;

import rs.luka.upisstats.desktop.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luka on 1.2.17..
 */
public class Network {
    public interface Callbacks {
        void onRequestCompleted(int id, String result);
        void onRequestError(int id, int code, String message);
        void onExceptionThrown(int id, Throwable throwable);
    }

    private static final int CACHE_SIZE = 15;
    private static final LinkedHashMap<String, String> cache
            = new LinkedHashMap<String, String>(CACHE_SIZE+1, 0.1f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > CACHE_SIZE;
        }
    };

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String URL_BASE = "http://upis.ml/get?q=";


    /**
     * Executes in background, in order (first start - first end)
     * @param id
     * @param query
     * @param callbacks
     */
    public static void submitQuery(int id, String query, Callbacks callbacks) {
        executor.execute(() -> {
            if(cache.containsKey(query)) { callbacks.onRequestCompleted(id, cache.get(query)); return; }


            HttpURLConnection conn;
            try {
                URL url = new URL(URL_BASE + URLEncoder.encode(query, "UTF-8"));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 100.0; Win64; x64; rv:10.0) Gecko/20100101 Firefox/10.0");
                //cloudflare refuses no-useragent requests, so we're just going to lie. like, a lot
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode >= 400) {
                    char[] errorMsg = new char[8192];
                    Arrays.fill(errorMsg, '\0');
                    Reader errorReader = new InputStreamReader(conn.getErrorStream());
                    errorReader.read(errorMsg);
                    errorReader.close();
                    conn.disconnect();
                    int end = 0;
                    while (end < 8192 && errorMsg[end] != '\0') end++;
                    callbacks.onRequestError(id, responseCode, String.valueOf(errorMsg).substring(0, end));

                } else {
                    String encoding = conn.getContentEncoding();
                    InputStream stream = Utils.wrapStream(encoding, conn.getInputStream());
                    StringBuilder response = new StringBuilder(256);
                    BufferedReader reader      = new BufferedReader(new InputStreamReader(stream));
                    reader.lines().forEachOrdered(response::append);
                    reader.close();
                    String result = response.toString();
                    callbacks.onRequestCompleted(id, result);

                    conn.disconnect();
                    cache.put(query, result);
                }
            } catch (final Throwable ex) {
                callbacks.onExceptionThrown(id, ex);
            }
        });
    }
}
