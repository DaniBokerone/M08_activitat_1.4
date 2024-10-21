package com.dani_juarez.a14threads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Button actionButton = findViewById(R.id.callButton);
        actionButton.setOnClickListener(v -> {
            // URL Exercici
            String url = "https://api.myip.com";

            executor.execute(new Runnable() {
                @Override
                public void run() {

                    String result = getDataFromUrl(url);
                    String resultatFinal = "";
                    if (result != null) {
                        resultatFinal = result;
                    } else {
                        resultatFinal = "Ha hagut un error a la peticio";
                    }

                    String finalResultatFinal = resultatFinal;
                    runOnUiThread(() -> {

                        Log.i("request", finalResultatFinal);
                    });

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            // Tasques a la interfície gràfica (GUI)

                            TextView resultatPeticio = findViewById(R.id.resultatPeticio);
                            StringBuilder resultatPeticioText = new StringBuilder(finalResultatFinal);

                            resultatPeticio.setText(resultatPeticioText.toString());


                        }
                    });
                }
            });
        });
    }

    String error = "";

    private String getDataFromUrl(String demoIdUrl) {
        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
