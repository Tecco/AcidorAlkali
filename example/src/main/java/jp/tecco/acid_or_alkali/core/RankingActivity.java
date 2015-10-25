package jp.tecco.acid_or_alkali.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;

import jp.tecco.acid_or_alkali.data.MySimpleArrayAdapter;
import jp.tecco.acid_or_alkali.R;

/**
 * Created by makotonishimoto on 2015/05/23.
 */
public class RankingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        ArrayList<String> scoreList = intent.getStringArrayListExtra("scoreList");

        //スコアのソート
        Collections.sort(scoreList);
        Collections.reverse(scoreList);

        //String[] prefectureList = getResources().getStringArray(R.array.prefecturesArray);
        String[] hogeList = (String[])scoreList.toArray(new String[0]);


        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, hogeList);

        // アイテムを追加します
        /*for(int i = 0 ; i < scoreList.size() ; i ++){
            adapter.add(scoreList.get(i));
        }*/


        //TODO: ランキング用のソート

        ListView listView = (ListView) findViewById(R.id.rankingList);
        // アダプターを設定します
        listView.setAdapter(adapter);

        // リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                String item = (String) listView.getItemAtPosition(position);
            }
        });
        // リストビューのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ListView listView = (ListView) parent;
                // 選択されたアイテムを取得します
                String item = (String) listView.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });





    }
}
