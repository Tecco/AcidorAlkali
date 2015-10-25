package jp.tecco.acid_or_alkali.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import jp.tecco.acid_or_alkali.R;
import jp.tecco.acid_or_alkali.endpoints.EndpointsAsyncTask;

/**
 * Created by makotonishimoto on 2015/05/10.
 */
public class TitleActivity extends Activity {

    @InjectView(R.id.title_ranking_button)BootstrapButton rankingButton;
    @InjectView(R.id.spinner)Spinner spinner;
    @InjectView(R.id.textView)TextView prefectureText;

    private Resources res = null;
    private Locale locale;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);

        //TODO: Butterknife古くない？
        ButterKnife.inject(this);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //Google Analyticsコード
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(R.xml.app_tracker);
        tracker.setScreenName("タイトル画面");

        //端末の言語設定を取得(てす)
        locale = Locale.getDefault();

        final SharedPreferences pref = TitleActivity.this.getSharedPreferences("pref", MODE_PRIVATE);
        int prefectureId = pref.getInt("prefectureId", 0);

        //言語リソースの取得
        res = getResources();

        if(Locale.JAPAN.equals(locale)){

        }else if (Locale.US.equals(locale)){
            //英語の場合日本の数だけずらす
            prefectureId = prefectureId - 47;
        }else{
            rankingButton.setVisibility(View.INVISIBLE);
            prefectureText.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
        }

        //レアケースだが言語設定を変更したときに地域を初期化する
        try {
            spinner.setSelection(prefectureId, false);
        }catch(IndexOutOfBoundsException e){
            //地域、点数の初期化
            Editor editor = pref.edit();
            editor.putInt("prefectureId", 0);
            editor.putInt("trueAnswerNum", 0);
            editor.putInt("falseAnswerNum", 0);

            editor.commit();

            Toast.makeText(TitleActivity.this, "Initialize by change language", Toast.LENGTH_LONG).show();
            finish();
        }
        spinner.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                int nowTrueAnswerNum = pref.getInt("trueAnswerNum", 0);
                int nowFalseAnswerNum = pref.getInt("falseAnswerNum", 0);

                String warning = res.getString(R.string.warning);
                String warningMessage = res.getString(R.string.warningMessage);

                if(nowTrueAnswerNum != 0 || nowFalseAnswerNum != 0){
                    createDialog(warning, warningMessage, "OK", null, 0);
                }
            }
            return false;
            }
        });


        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                //画面起動時に1回、同じものを選択した時には発火しない、違うものを選んだ時は発火する
                //Toast.makeText(TitleActivity.this, "テスト", Toast.LENGTH_LONG).show();
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();
                //preferenceに登録 int positionを保存
                Editor editor = pref.edit();
                //地域idの変更
                if (Locale.JAPAN.equals(locale)) {
                    editor.putInt("prefectureId", position);
                } else {
                    if (position != 0) {
                        editor.putInt("prefectureId", position + 47);
                    } else {
                        editor.putInt("prefectureId", position);
                    }
                }

                //正解数、不正解数の初期化
                editor.putInt("trueAnswerNum", 0);
                editor.putInt("falseAnswerNum", 0);

                editor.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @OnClick(R.id.title_start_button)
     void moveQuiz() {
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.title_twitter_button)
    void tweet() {
        SharedPreferences pref = TitleActivity.this.getSharedPreferences("pref", MODE_PRIVATE);
        float trueAnswerNum = pref.getInt("trueAnswerNum", 0);
        float falseAnswerNum = pref.getInt("falseAnswerNum", 0);
        //地域の取得
        int prefectureId = pref.getInt("prefectureId", 0);
        String[] prefectureList = getResources().getStringArray(R.array.prefecturesArray);
        String[] prefectureList2 = getResources().getStringArray(R.array.prefecturesArray2minus);
        String[] prefectureList3 = new String[prefectureList.length + prefectureList2.length];

        System.arraycopy(prefectureList, 0, prefectureList3, 0, prefectureList.length);
        System.arraycopy(prefectureList2, 0, prefectureList3, prefectureList.length, prefectureList2.length);

        String prefecture = prefectureList3[prefectureId];

        String truePercent = "";
        String tweetBody = "";

        //TODO: ランダムで褒め言葉とかけなし言葉をつぶやかせる

        try{
            truePercent = getTruePercent(trueAnswerNum, falseAnswerNum);
            if(Locale.JAPAN.equals(locale)){
                int truePercentInt = (int)((trueAnswerNum * 100 )/ (trueAnswerNum + falseAnswerNum));
                String oneComment = "";

                Random r = new Random();
                int n = r.nextInt(3);

                //ランダムにツイッターの一言コメントを変える
                String[] comment80 = {"天才かよ！！", "あなたが神か。", "イケメン！！"};
                String[] comment60 = {"なかなかやるね！", "優秀だな。", "あと一歩だぞ！"};
                String[] comment40 = {"まぁまぁだな。", "一般人です。", "THE 普通！"};
                String[] comment20 = {"もうちょっとがんばろう。", "まだまだやれるはずだ", "そんな成績で大丈夫か？"};
                String[] comment0 = {"バカでごめんなさい。", "生まれてきてごめんなさい。", "日本の恥でごめんなさい。"};

                String rank = "";

                if(truePercentInt >= 80){
                    oneComment = comment80[n];
                    rank = "S";
                }else if(truePercentInt >= 60){
                    oneComment = comment60[n];
                    rank = "A";
                }else if(truePercentInt >= 40){
                    oneComment = comment40[n];
                    rank = "B";
                }else if(truePercentInt >= 20){
                    oneComment = comment20[n];
                    rank = "C";
                }else{
                    oneComment = comment0[n];
                    rank = "F";
                }

                if((int)trueAnswerNum == 0 && (int)falseAnswerNum == 0 && truePercentInt == 0){
                    oneComment = "さぁ、正解を積んで地域に貢献だ！";
                }
                tweetBody = "【ランク" + rank
                        + "】キミの正解数は" + String.valueOf((int)trueAnswerNum)
                        + " 不正解数は" + String.valueOf((int)falseAnswerNum)
                        + " 正解率は" + String.valueOf(truePercent) + "％だ。"
                        + prefecture + "に貢献しているぞ！ " + oneComment + " %23acidoralkali "
                        + "https://goo.gl/MckdNF";
            }else {
                int truePercentInt = (int)((trueAnswerNum * 100 )/ (trueAnswerNum + falseAnswerNum));

                String rank = "";

                if(truePercentInt >= 80){
                    rank = "S";
                }else if(truePercentInt >= 60){
                    rank = "A";
                }else if(truePercentInt >= 40){
                    rank = "B";
                }else if(truePercentInt >= 20){
                    rank = "C";
                }else{
                    rank = "F";
                }

                tweetBody = "RANK:" + rank + "(" + String.valueOf(truePercent) + "%25) - "
                        + "Correct:" + String.valueOf((int)trueAnswerNum)
                        + ",Incorrect:" + String.valueOf((int)falseAnswerNum)
                        + ".The state where you are contributing is " + prefecture + ". %23acidoralkali "
                        + "https://goo.gl/MckdNF";
            }

        }catch(ArithmeticException e){
            tweetBody = "NO DATA... #acidoralkali";
        }

        String url = "http://twitter.com/share?text=" + tweetBody;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @OnClick(R.id.title_score_button)
    void score() {
        SharedPreferences pref = TitleActivity.this.getSharedPreferences("pref", MODE_PRIVATE);
        float trueAnswerNum = pref.getInt("trueAnswerNum", 0);
        float falseAnswerNum = pref.getInt("falseAnswerNum", 0);
        String truePercent = "";
        String scoreMessage = "";
        //地域の取得
        int prefectureId = pref.getInt("prefectureId", 0);
        String[] prefectureList = getResources().getStringArray(R.array.prefecturesArray);
        String[] prefectureList2 = getResources().getStringArray(R.array.prefecturesArray2minus);
        String[] prefectureList3 = new String[prefectureList.length + prefectureList2.length];

        System.arraycopy(prefectureList, 0, prefectureList3, 0, prefectureList.length);
        System.arraycopy(prefectureList2, 0, prefectureList3, prefectureList.length, prefectureList2.length);

        String prefecture = prefectureList3[prefectureId];

        try{
            truePercent = getTruePercent(trueAnswerNum, falseAnswerNum);
        }catch(ArithmeticException e){
            scoreMessage = "NO DATA";
        }

        if(Locale.JAPAN.equals(locale)) {
            scoreMessage = "キミの貢献してる地域は" + prefecture + "\n"
                    + "正解数: " + String.valueOf((int) trueAnswerNum) + "回だよ" + "\n"
                    + "不正解数: " + String.valueOf((int) falseAnswerNum) + "回だよ" + "\n"
                    + "正解率: " + truePercent + "%だよ";
        }else{
            scoreMessage = "The state where you are contributing is " + prefecture + "\n"
                    + "Correct: " + String.valueOf((int) trueAnswerNum) + "\n"
                    + "Incorrect: " + String.valueOf((int) falseAnswerNum) + "\n"
                    + "Accuracy rate: " + truePercent + "%";
        }

        String score = res.getString(R.string.score);
        String tweet = res.getString(R.string.tweetMessage);

        createDialog(score, scoreMessage, "OK", tweet, 1);


    }

    @OnClick(R.id.title_ranking_button)
    void ranking() {

        String ranking = res.getString(R.string.ranking);
        String nowLoading = res.getString(R.string.nowLoading);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(ranking);
        progressDialog.setMessage(nowLoading);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
        //Intent intent = new Intent(this, RankingActivity.class);
        //startActivity(intent);
        /*
        * 　context
        * 　mode(0: 取得, 1: 更新)
        * 　地方id
        * 　正解数
        * */
        new EndpointsAsyncTask(this, 0, 0, 0, progressDialog).execute();

    }

    public void createDialog(String title, String message, String OK, String NO, final int dialogTask){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        if(NO != null) {
            alertDialogBuilder.setNegativeButton(NO,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(dialogTask == 1){
                                tweet();
                            }
                        }
                    });
        }
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getTruePercent(float trueAnswerNum, float falseAnswerNum){
        String truePercent = "";
        //小数点第二位を四捨五入するために予め10倍ずらしておく
        float preTruePercent = trueAnswerNum / (trueAnswerNum + falseAnswerNum) * 1000;

        if(trueAnswerNum == 0 && falseAnswerNum == 0){
            truePercent = "0";
        }else {
            //ここでfloatにキャストしないとdoubleになってしまう
            truePercent = String.valueOf((float) (Math.floor(preTruePercent) * 0.1));
        }
        return truePercent;
    }
}
