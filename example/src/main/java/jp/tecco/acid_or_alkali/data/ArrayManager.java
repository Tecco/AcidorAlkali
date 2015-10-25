package jp.tecco.acid_or_alkali.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import jp.tecco.acid_or_alkali.R;

/**
 * Created by makotonishimoto on 2015/10/25.
 */
public class ArrayManager {

    private Context context;
    private String[] acidArray;
    private String[] alcaliArray;

    public ArrayManager(Context context, String[] acidArray,String[] alcaliArray) {
        this.context = context;
        this.acidArray = acidArray;
        this.alcaliArray = alcaliArray;
    }

    public ArrayList<String> getAcidList(){
        String[] preAcAl = this.context.getResources().getStringArray(R.array.acidArray);
        ArrayList<String> AcAl = new ArrayList<String>(Arrays.asList(preAcAl));
        return AcAl;
    }

    public ArrayList<String> getAlcaliList(){
        String[] preAlAl = this.context.getResources().getStringArray(R.array.alkaliArray);
        ArrayList<String> AlAl = new ArrayList<String>(Arrays.asList(preAlAl));
        return AlAl;
    }

    public ArrayList<String> getCombinedList(){
        ArrayList<String> allAl = new ArrayList<>();
        allAl.addAll(this.getAcidList());
        allAl.addAll(this.getAlcaliList());
        ArrayList<String> QAl = new ArrayList<>();

        for(int i = 0 ; i < 10 ; i ++){
            Random r = new Random();
            int n = r.nextInt(allAl.size() - 1) + 1;

            QAl.add(allAl.get(n));
        }
        for(int i = 0 ; i < 3 ; i ++){
            QAl.add(allAl.get(0));
        }
        return QAl;
    }


    //allListに結合
    /*AllAl.addAll(AcAl);
    AllAl.addAll(AlAl);

    for(int i = 0 ; i < 10 ; i ++){

        Random r = new Random();
        int n = r.nextInt(AllAl.size() - 1) + 1;

        QAl.add(AllAl.get(n));
    }
    for(int i = 0 ; i < 3 ; i ++){
        QAl.add(AllAl.get(0));
    }

    arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, QAl );*/
}
