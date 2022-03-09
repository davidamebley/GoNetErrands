package somame.amebleysystems.com.somame;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class ReadTextFromServer {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PATH_TO_SERVER = URLs.URL_TERMS_CONDITIONS;
    private Handler handler = new Handler();
    private static String textContent="";


    public static void executeReadTextFile(){
        DownloadFilesTask downloadFilesTask = new DownloadFilesTask();
        downloadFilesTask.execute();
    }

    static String getTextContent() {
        return textContent;
    }


    private static class DownloadFilesTask extends AsyncTask<URL, Void, String> {
        protected String doInBackground(URL... urls) {
            return downloadRemoteTextFileContent();
        }
        protected void onPostExecute(String result) {
            if(!TextUtils.isEmpty(result)){
                textContent = result;
            }
            super.onPostExecute(result);
        }
    }


    private static String downloadRemoteTextFileContent(){
        URL mUrl = null;
        StringBuilder content = new StringBuilder();//Used string builder because I want to use 'append' and not '+=' in loop
        try {
            mUrl = new URL(PATH_TO_SERVER);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            assert mUrl != null;
            URLConnection connection = mUrl.openConnection();
//            BufferedReader br = new BufferedReader(new UnicodeReader(new FileInputStream(fileName), "UTF-8"));
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line = "";
            content = new StringBuilder();
            while((line = br.readLine()) != null){
                content.append(line).append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(content);
    }
}
