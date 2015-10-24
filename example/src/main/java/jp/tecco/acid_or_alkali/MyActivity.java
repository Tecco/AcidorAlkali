package jp.tecco.acid_or_alkali;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MyActivity extends Activity {

    private ArrayList<String> AcAl;
    private ArrayList<String> AlAl;

    private ArrayList<String> AllAl = new ArrayList<String>();
    private ArrayList<String> QAl = new ArrayList<String>();

    private ArrayAdapter<String> arrayAdapter;
    private int i;

    private CountDownTimer countDownTimer;

    public InterstitialAd interstitial;

    @InjectView(R.id.frame) SwipeFlingAdapterView flingContainer;
    @InjectView(R.id.timerText)TextView timerText;
    @InjectView(R.id.answerMark)TextView answerMark;

    //cardTextが最初のタイミングで生成されていないため
    private TextView cardText;

    private int trueAnswerNum = 0;
    private long prefecturesId = 0;

    //スコアが2回でないようにするフラグ
    private boolean scoreFlag = true;

    AlphaAnimation fadeIn;
    AlphaAnimation fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);

        createTimer();

        //効果音の作成 lollipopがバグるので却下

        // インタースティシャルを作成する。
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-7287685278318574/1629053445");

        // 広告リクエストを作成する。
        AdRequest adRequestInter = new AdRequest.Builder().build();

        // インタースティシャルの読み込みを開始する。
        interstitial.loadAd(adRequestInter);

        fadeIn = new AlphaAnimation( 1.0f , 0.0f );
        //fadeOut = new AlphaAnimation(0.0f , 1.0f );

        fadeIn.setDuration(600);
        fadeIn.setFillAfter(true);
        //fadeOut.setStartOffset(4200+fadeIn.getStartOffset());

        //TODO: ちょっと動かして連打すると落ちる

        //酸性とアルカリ性を分ける　スコア表出す　国別統計とかならおもろいかも

        //酸性のリスト
        String[] preAcAl = getResources().getStringArray(R.array.acidArray);
        AcAl = new ArrayList<String>(Arrays.asList(preAcAl));

        //アルカリ性のリスト
        String[] preAlAl = getResources().getStringArray(R.array.alkaliArray);
        AlAl = new ArrayList<String>(Arrays.asList(preAlAl));

        //allListに結合
        AllAl.addAll(AcAl);
        AllAl.addAll(AlAl);

        for(int i = 0 ; i < 10 ; i ++){

            Random r = new Random();
            int n = r.nextInt(AllAl.size() - 1) + 1;

            QAl.add(AllAl.get(n));
        }
        for(int i = 0 ; i < 3 ; i ++){
            QAl.add(AllAl.get(0));
        }


        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, QAl );


        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                //最初のタイミングではcardTextが生成されていない
                cardText = (TextView) findViewById(R.id.helloText);

                //めくったタイミングで1回ずつ飛んでいるイベント
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");

                try {
                    QAl.remove(0);
                    arrayAdapter.notifyDataSetChanged();

                    countDownTimer.cancel();
                    countDownTimer.start();
                } catch (IndexOutOfBoundsException e) {
                    Log.d("LIST", "removed object Nothing!");
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                String a = dataObject.toString();
                //makeToast(MyActivity.this, a);

                if (pHJudge(a) == 1) {
                    //makeToast(MyActivity.this, "True");
                    answerMark.setText("○");
                    answerMark.setTextColor(Color.RED);
                    answerMark.startAnimation(fadeIn);

                    trueAnswerNum++;
                } else {
                    answerMark.setText("×");
                    answerMark.setTextColor(Color.BLACK);
                    answerMark.startAnimation(fadeIn);
                    //makeToast(MyActivity.this, "False");
                }

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                String a = dataObject.toString();
                //makeToast(MyActivity.this, a);

                if (pHJudge(a) == 2) {
                    answerMark.setText("○");
                    answerMark.setTextColor(Color.RED);
                    answerMark.startAnimation(fadeIn);
                    //makeToast(MyActivity.this, "True");
                    trueAnswerNum++;
                } else {
                    answerMark.setText("×");
                    answerMark.setTextColor(Color.BLACK);
                    answerMark.startAnimation(fadeIn);
                    //makeToast(MyActivity.this, "False");
                }


                //new EndpointsAsyncTask().execute(new Pair<Context, String>(MyActivity.this, "Manfred"));
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // リストが0になったあとの処理
                if (scoreFlag) {

                    final SharedPreferences pref = MyActivity.this.getSharedPreferences("pref", MODE_PRIVATE);
                    //現在の正解数、不正解数を取得
                    int preTrueAnswerNum = pref.getInt("trueAnswerNum", 0);
                    int preFalseAnswerNum = pref.getInt("falseAnswerNum", 0);

                    prefecturesId = pref.getLong("prefectureId", 0);
                    /*
                    * 　context
                    * 　mode(0: 取得, 1: 更新)
                    * 　地方id
                    * 　正解数
                    * */
                    new EndpointsAsyncTask(MyActivity.this, 1, prefecturesId, trueAnswerNum, null).execute();

                    Editor editor = pref.edit();
                    editor.putInt("trueAnswerNum", preTrueAnswerNum + trueAnswerNum);
                    editor.putInt("falseAnswerNum", preFalseAnswerNum + (10 - trueAnswerNum));
                    editor.commit();

                    finish();

                    Intent intent = new Intent(MyActivity.this, ScoreActivity.class);
                    intent.putExtra("trueAnswerNum", trueAnswerNum);
                    startActivity(intent);

                    scoreFlag = false;

                    displayInterstitial();


                /*QAl.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;*/

                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                //TODO: 一旦コメントアウトしておく。端にある判定部分のことだと思う
                //View view = flingContainer.getSelectedView();
                //view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                //view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }

            // インタースティシャルを表示する準備ができたら、displayInterstitial() を呼び出す。
            public void displayInterstitial() {
                Random r = new Random();
                int n = r.nextInt(5);

                //5回に1回の確率でインタースティシャル
                if(n == 0) {
                    if (interstitial.isLoaded()) {
                        interstitial.show();
                    }
                }
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //Timer処理のとき使いそう
                //String a = dataObject.toString();
                //makeToast(MyActivity.this, dataObject.toString());
            }
        });




    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    private int pHJudge(String val){
        int pH = 0;
        if(AcAl.contains(val)) {
            pH = 1;
        }else if(AlAl.contains(val)){
            pH = 2;
        }
        return pH;
    }

    @OnClick(R.id.right)
    public void right() {
        /**
         * Trigger the right event manually.
         */
        flingContainer.getTopCardListener().selectRight();
    }

    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }

    public void createTimer(){


        //タイマー時間と更新感覚
        countDownTimer = new CountDownTimer(2000, 20) {

            // カウントダウン処理
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 1000) {
                    timerText.setTextColor(Color.RED);
                } else {
                    timerText.setTextColor(Color.BLACK);
                }
                timerText.setText(String.valueOf(millisUntilFinished));
            }

            // カウントが0になった時の処理
            public void onFinish() {


                //タイムアップ時に0を代入
                timerText.setText(String.valueOf(0));

                String cardText = flingContainer.getTopCardListener().getDataobject();


                if(pHJudge(cardText) == 1){
                    flingContainer.getTopCardListener().selectRight();
                }else{
                    flingContainer.getTopCardListener().selectLeft();
                }

            }
        };
    }


}