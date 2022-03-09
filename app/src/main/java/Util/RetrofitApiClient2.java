package Util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import somame.amebleysystems.com.somame.URLs;

/**
 * Created by Akwasi on ${9/27/2018}.
 */
public class RetrofitApiClient2 {
        public static final String BASE_URL= URLs.ROOT_URL;
//        public static final String BASE_URL="http://192.168.56.1:80/retrofit_test/";
        public static Retrofit retrofit = null;

        public static Retrofit getApiClient(){
            //Api less than 20 SSL Internet Connection stuff
//            OkHttpClient client=new OkHttpClient();
//            try {
//                client = new OkHttpClient.Builder()
//                        .sslSocketFactory(new TLSSocketFactory())
//                        .build();
//            } catch (KeyManagementException e) {
//                e.printStackTrace();
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }

            if(retrofit==null){
//                retrofit =new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
//                retrofit =new Retrofit.Builder().client(getUnsafeOkHttpClient()).baseUrl(BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
                retrofit =new Retrofit.Builder().client(getUnsafeOkHttpClient()).baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            }
            return retrofit;
        }


        //SSL Stuff
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder
                                        .connectTimeout(60, TimeUnit.SECONDS)
                                        .callTimeout(60, TimeUnit.SECONDS)
                                        .readTimeout(15, TimeUnit.SECONDS)
                                        .writeTimeout(10, TimeUnit.SECONDS)
                                        .retryOnConnectionFailure(true)
                                        .build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
