package korotaeva.ru.mathtips;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LOG_TAG = "myLogs";
    private FTPClient ftp = null;
    private String serverName, username, password;
    private Boolean btnClickable = true;
    Button bConnect, bDownload;
    TextView tView;
    EditText etFileName;
    FTPFile[] ftpFiles = null;
    String fileName;
    List<String> filesAvailable = new LinkedList<>();
    DataReader dataReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        bConnect = (Button) findViewById(R.id.bConnect);
        etFileName = (EditText) findViewById(R.id.etFileName);
        bConnect.setOnClickListener(this);
        bDownload = (Button) findViewById(R.id.bDownload);
        bDownload.setOnClickListener(this);
        tView = (TextView) findViewById(R.id.tViewFiles);
        dataReader = new DataReader("settings/ftpsettings.txt", getApplicationContext());
        try {
            String [] parameters = dataReader.ReadFile();
            serverName = parameters[0];
            username = parameters[1];
            password = parameters[2];
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "End of onCreate");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bConnect:
                Log.d(LOG_TAG, "Зашли в bConnect");
                if (btnClickable == true) {
                    FtpConnectTask ftpTask = new FtpConnectTask();
                    ftpTask.execute();
                    bConnect.setText("Disconnect");
                    btnClickable = !btnClickable;
                } else {
                    FtpDisconnectTask ftpTask = new FtpDisconnectTask();
                    ftpTask.execute();
                    bConnect.setText("Connect");
                    tView.setText("Server is not available anymore");
                    btnClickable = !btnClickable;
                }
                break;

            case R.id.bDownload:
                Log.d(LOG_TAG, "Зашли в bDownload");
                fileName = etFileName.getText().toString();
                if (!btnClickable) {
                    if (filesAvailable.contains(fileName)) {
                        Log.d(LOG_TAG, "Здесь надо запускать download");
                        DownloadFromServerAsyncTask download = new DownloadFromServerAsyncTask();
                        download.execute();
                        Toast.makeText(this, "Загружено", LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, "Такого файла не существует", LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Сперва подключитесь к серверу", LENGTH_SHORT).show();
                }
                break;
        }
    }

    class FtpConnectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ftp = new FTPClient();
            try {
                ftp.connect(serverName);
                ftp.enterLocalPassiveMode();
                Log.d(LOG_TAG, "CONNECT TRY");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "CONNECT Catch");
            }
            try {
                ftp.login(username, password);
                Log.d(LOG_TAG, "Login TRY");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "Login Catch");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            FilesAsyncTask filesTask = new FilesAsyncTask();
            filesTask.execute();
        }
    }

    class FilesAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ftpFiles = ftp.listFiles("documents");
                filesAvailable.addAll(Arrays.asList(ftp.listNames("documents")));
                int b = ftpFiles.length;
                Log.d(LOG_TAG, "TRY FTP FILE");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (status == true) {
                tView.setText("Available files: \n");
                Log.d(LOG_TAG, "TRY STATUS IS TRUE");
                for (FTPFile x : ftpFiles) {
                    if (x.getType() != 1) {

                        Log.d(LOG_TAG, "Дошло");
                        filesAvailable.add(x.getName());
                        tView.append(x.getName() + "\n");
                        Log.d(LOG_TAG, "Вышло");
                        //Log.d(LOG_TAG, x.getName().toString());
                    }
                }
                for (String fileName : filesAvailable
                        ) {
                    Log.d(LOG_TAG, fileName + "\n");

                }
            }
        }
    }

    class DownloadFromServerAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {


            String[] fileNames;
            try {
                fileNames = ftp.listNames("documents/");

                for (String x :
                        fileNames) {
                    Log.d(LOG_TAG, x);

                }
                return Arrays.asList(fileNames).contains(fileName);

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean status) {
            if (status) {
                new downloadFileAsyncTask().execute();
            }
        }
    }

    class downloadFileAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            String output = getApplicationInfo().dataDir;

            String path = output + "/files/" + fileName;

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                Log.d(LOG_TAG, "FileNotFoundException");
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "doInBack download");
            Log.d(LOG_TAG, "Output = " + output);
            try {
                ftp.retrieveFile(fileName, fileOutputStream);
                Log.d(LOG_TAG, "Try to retriever");

            } catch (IOException e) {
                e.printStackTrace();
            }

            String sdState = android.os.Environment.getExternalStorageState();
            if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
                File sdDir = android.os.Environment.getExternalStorageDirectory();
                Log.d(LOG_TAG, "sdDir = " + sdDir.getAbsolutePath());
                if (sdDir.canWrite()) {
                    File source = new File(path);
                    new File(sdDir.getAbsolutePath() + "/math_tips").mkdir();
                    File dest = new File(sdDir.getAbsolutePath() + "/math_tips/" + fileName);
                    Log.d(LOG_TAG, dest.getPath().toString());
                    if (!dest.exists()) {
                        try {
                            dest.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (source.exists()) {
                        InputStream src = null;
                        try {
                            src = new FileInputStream(source);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        OutputStream dst = null;
                        try {
                            dst = new FileOutputStream(dest);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        // Copy the bits from instream to outstream
                        byte[] buf = new byte[1024];
                        int len;
                        try {
                            while ((len = src.read(buf)) > 0) {
                                dst.write(buf, 0, len);

                            }
                            Log.d(LOG_TAG, "Download Success");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(LOG_TAG, "Download Failed");
                        }
                        try {
                            src.close();
                            Log.d(LOG_TAG, "Src.Close Try Success");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(LOG_TAG, "Src.Close Catch Exception");
                        }
                        try {
                            dst.close();
                            Log.d(LOG_TAG, "DST.Close Try Success");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(LOG_TAG, "DST.Close Catch Exception");
                        }
                    }
                }

            }

            return null;
        }
    }

    class FtpDisconnectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ftp.logout();
                ftp.disconnect();
                Log.d(LOG_TAG, "TRY FTPFILE");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
