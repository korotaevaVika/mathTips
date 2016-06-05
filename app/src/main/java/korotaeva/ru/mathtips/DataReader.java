package korotaeva.ru.mathtips;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataReader {
    private String path;
    private Context context;
    public DataReader(String path, Context context){
        this.path = path;
        this.context = context;
    }

    public String[] listFileInAssets() throws  IOException{
        AssetManager assetManager = context.getAssets();
        return assetManager.list("settings");
    }

    public String[] ReadFile() throws IOException{
        //FileReader fileReader = new FileReader(path);
        AssetManager assetManager = context.getAssets();
        InputStreamReader istream = new InputStreamReader(assetManager.open(path));

        BufferedReader textReader = new BufferedReader(istream);
        int numberLines = readLines();
        String [] textData = new String[numberLines];
        int i =0;
        for (; i<numberLines; i++) {
            textData[i]=textReader.readLine();
        }
        textReader.close();
        return  textData;
    }
    int readLines() throws  IOException{
        int numberLines=0;
        AssetManager assetManager = context.getAssets();
        InputStreamReader istream = new InputStreamReader(assetManager.open(path));
        BufferedReader textReader = new BufferedReader(istream);
        while (textReader.readLine()!= null) {
            numberLines++;
        }
        textReader.close();
        return numberLines;
    }

}
