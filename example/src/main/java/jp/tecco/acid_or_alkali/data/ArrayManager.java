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
    private String[] alkaliArray;

    public ArrayManager(Context context, String[] acidArray,String[] alkaliArray) {
        this.context = context;
        this.acidArray = acidArray;
        this.alkaliArray = alkaliArray;
    }

    public ArrayList<String> getAcidList(){
        String[] preAcAl = this.context.getResources().getStringArray(R.array.acidArray);
        ArrayList<String> AcAl = new ArrayList<String>(Arrays.asList(preAcAl));
        return AcAl;
    }

    public ArrayList<String> getAlkaliList(){
        String[] preAlAl = this.context.getResources().getStringArray(R.array.alkaliArray);
        ArrayList<String> AlAl = new ArrayList<String>(Arrays.asList(preAlAl));
        return AlAl;
    }

    public ArrayList<String> getCombinedList(){
        ArrayList<String> CombAl = new ArrayList<>();
        CombAl.addAll(this.getAcidList());
        CombAl.addAll(this.getAlkaliList());

        return CombAl;
    }

    public ArrayList<String> randomiseList(ArrayList<String> arrayList){
        ArrayList<String> CombAl = new ArrayList<>();

        for(int i = 0 ; i < 10 ; i ++){
            Random r = new Random();
            int n = r.nextInt(arrayList.size() - 1) + 1;

            CombAl.add(arrayList.get(n));
        }
        for(int i = 0 ; i < 3 ; i ++){
            CombAl.add(arrayList.get(0));
        }
        return CombAl;
    }
}
