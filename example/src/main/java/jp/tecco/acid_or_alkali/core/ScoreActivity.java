package jp.tecco.acid_or_alkali.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import jp.tecco.acid_or_alkali.R;

/**
 * Created by makotonishimoto on 2015/05/18.
 */



public class ScoreActivity extends Activity{

    @InjectView(R.id.scoreText)TextView scoreText;
    @InjectView(R.id.scoreButton1)com.beardedhen.androidbootstrap.BootstrapButton scoreButton1;
    @InjectView(R.id.scoreButton2)com.beardedhen.androidbootstrap.BootstrapButton scoreButton2;

    Animation fadein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        ButterKnife.inject(this);

        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);

        scoreButton1 = (com.beardedhen.androidbootstrap.BootstrapButton) findViewById(R.id.scoreButton1);
        scoreButton2 = (com.beardedhen.androidbootstrap.BootstrapButton) findViewById(R.id.scoreButton2);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        int trueAnswerNum = intent.getIntExtra("trueAnswerNum", 0);

        Resources res = getResources();
        String result = res.getString(R.string.result);
        String correct = res.getString(R.string.correct);
        String incorrect = res.getString(R.string.incorrect);

        scoreText.setText(result + "\n" + correct + ":" + trueAnswerNum + " " + incorrect + ":" + (10 - trueAnswerNum));

        new Handler().postDelayed(scoreShow, 200);
        new Handler().postDelayed(buttonShow, 1600);

        final SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        int countCreate = pref.getInt("countCreate", 0);

        if (countCreate <= 100){
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("countCreate", countCreate + 1);
            editor.commit();

            if(countCreate == 100){
                //端末の言語設定を取得(てす)
                Locale locale = Locale.getDefault();
                if(Locale.JAPAN.equals(locale)){
                    createDialog("これが100回目のプレイです！！", "いつもプレイありがとうございます！評価☆5をつけていただけないでしょうか？", "OK", "キャンセル");
                }else {
                    createDialog("Congratulations! 100th play!", "Please evaluate in google play.", "OK", "Cancel");
                }
            }
        }

    }

    void jumpGooglePlay(){
        try{
            //Google Playを開く
            Uri uri = Uri.parse("market://details?id=jp.tecco.acid_or_alkali");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }catch(Exception e){

        }
    }

    @OnClick(R.id.scoreButton1)
    void oneMore() {
        finish();
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.scoreButton2)
    void goTitle() {
        finish();
    }

    private final Runnable scoreShow = new Runnable() {
        @Override
        public void run() {
            scoreText.setVisibility(View.VISIBLE);
            scoreText.startAnimation(fadein);
        }
    };

    private final Runnable buttonShow = new Runnable() {
        @Override
        public void run() {
            scoreButton1.setVisibility(View.VISIBLE);
            scoreButton2.setVisibility(View.VISIBLE);
            scoreButton1.startAnimation(fadein);
            scoreButton2.startAnimation(fadein);
        }
    };

    public void createDialog(String title, String message, String OK, String NO){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jumpGooglePlay();
                    }
                });

        alertDialogBuilder.setNegativeButton(NO,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
