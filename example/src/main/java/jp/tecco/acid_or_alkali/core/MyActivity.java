package jp.tecco.acid_or_alkali.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import jp.tecco.acid_or_alkali.R;
import jp.tecco.acid_or_alkali.data.ArrayManager;
import jp.tecco.acid_or_alkali.endpoints.EndpointsAsyncTask;


public class MyActivity extends Activity {

    private ArrayAdapter<String> arrayAdapter;
    private CountDownTimer countDownTimer;
    private ArrayManager mArrayManager;

    public InterstitialAd interstitial;

    @InjectView(R.id.frame) SwipeFlingAdapterView flingContainer;
    @InjectView(R.id.timerText)TextView timerText;
    @InjectView(R.id.answerMark)TextView answerMark;

    private int trueAnswerNum = 0;
    private long prefecturesId = 0;

    //スコアが2回でないようにするフラグ
    private boolean scoreShowed = false;

    private MyPreferences mPref;

    public enum PH { ACID, ALKALI };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);

        mPref = new MyPreferences(this);
        mArrayManager = new ArrayManager(this, getResources().getStringArray(R.array.acidArray), getResources().getStringArray(R.array.alkaliArray));

        createTimer();

        //効果音の作成 lollipopがバグるので却下

        // インタースティシャルを作成する。
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-7287685278318574/1629053445");

        // 広告リクエストを作成する。
        AdRequest adRequestInter = new AdRequest.Builder().build();

        // インタースティシャルの読み込みを開始する。
        interstitial.loadAd(adRequestInter);

        final Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);

        //TODO: ちょっと動かして連打すると落ちる

        final ArrayList<String> combAl = mArrayManager.getCombinedList();
        final ArrayList<String> randomCombAl = mArrayManager.randomiseList(combAl);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.card_text, randomCombAl);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                //めくったタイミングで1回ずつ飛んでいるイベント
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");

                try {
                    randomCombAl.remove(0);
                    arrayAdapter.notifyDataSetChanged();

                    countDownTimer.cancel();
                    countDownTimer.start();
                } catch (IndexOutOfBoundsException e) {
                    Log.d("LIST", "removed object Nothing!");
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                String data = dataObject.toString();

                if (pHJudge(data) == PH.ACID) {
                    answerMark.setText("○");
                    answerMark.setTextColor(getResources().getColor(R.color.true_answer_color));
                    trueAnswerNum++;
                } else {
                    answerMark.setText("×");
                    answerMark.setTextColor(getResources().getColor(R.color.false_answer_color));
                }
                answerMark.startAnimation(fadeIn);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                String data = dataObject.toString();

                if (pHJudge(data) == PH.ALKALI) {
                    answerMark.setText("○");
                    answerMark.setTextColor(getResources().getColor(R.color.true_answer_color));
                    trueAnswerNum++;
                } else {
                    answerMark.setText("×");
                    answerMark.setTextColor(getResources().getColor(R.color.false_answer_color));
                }
                answerMark.startAnimation(fadeIn);

                //new EndpointsAsyncTask().execute(new Pair<Context, String>(MyActivity.this, "Manfred"));
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // リストが0になったあとの処理
                if (!scoreShowed) {

                    //現在の正解数、不正解数を取得
                    int preTrueAnswerNum = mPref.getTrueAnswerNum();
                    int preFalseAnswerNum = mPref.getFalseAnswerNum();

                    prefecturesId = mPref.getPrefecturesId();
                    /*
                    * 　context
                    * 　mode(0: 取得, 1: 更新)
                    * 　地方id
                    * 　正解数
                    * */
                    new EndpointsAsyncTask(MyActivity.this, 1, prefecturesId, trueAnswerNum, null).execute();

                    mPref.edit().putTrueAnswerNum(preTrueAnswerNum + trueAnswerNum).apply();
                    mPref.edit().putFalseAnswerNum(preFalseAnswerNum + (10 - trueAnswerNum)).apply();

                    finish();

                    Intent intent = new Intent(MyActivity.this, ScoreActivity.class);
                    intent.putExtra("trueAnswerNum", trueAnswerNum);
                    startActivity(intent);

                    scoreShowed = true;

                    displayInterstitial();

                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

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
            }
        });

    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    private PH pHJudge(String val){
        PH pH = null;
        if(mArrayManager.getAcidList().contains(val)) {
            pH = PH.ACID;
        }else if(mArrayManager.getAlkaliList().contains(val)){
            pH = PH.ALKALI;
        }
        return pH;
    }

    @OnClick(R.id.right)
    public void right() { flingContainer.getTopCardListener().selectRight(); }

    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }

    private void createTimer(){
        //タイマー時間と更新感覚
        countDownTimer = new CountDownTimer(2000, 20) {
            // カウントダウン処理
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 1000) {
                    timerText.setTextColor(getResources().getColor(R.color.true_answer_color));
                } else {
                    timerText.setTextColor(getResources().getColor(R.color.false_answer_color));
                }
                timerText.setText(String.valueOf(millisUntilFinished));
            }

            // カウントが0になった時の処理
            public void onFinish() {
                //タイムアップ時に0を代入
                timerText.setText(String.valueOf(0));

                String cardText = flingContainer.getTopCardListener().getDataobject();
                if(pHJudge(cardText) == PH.ACID){
                    flingContainer.getTopCardListener().selectRight();
                }else{
                    flingContainer.getTopCardListener().selectLeft();
                }
            }
        };
    }
}