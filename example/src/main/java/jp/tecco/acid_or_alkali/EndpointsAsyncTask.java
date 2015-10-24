package jp.tecco.acid_or_alkali;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.appspot.acidoralkariranking.quoteEndpoint.QuoteEndpoint;
import com.appspot.acidoralkariranking.quoteEndpoint.model.Quote;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by makotonishimoto on 2015/05/13.
 */
class EndpointsAsyncTask extends AsyncTask<Void, Void, List<Quote>> {
    private static com.appspot.acidoralkariranking.quoteEndpoint.QuoteEndpoint myApiService = null;
    private Context context;
    private int quoteMode;
    private long prefecturesId;
    private int trueAnswerNum;
    private ProgressDialog pg;

    EndpointsAsyncTask(Context context, int quoteMode, long prefecturesId, int trueAnswerNum, ProgressDialog pg) {
        this.context = context;
        this.quoteMode = quoteMode;
        this.prefecturesId = prefecturesId;
        this.trueAnswerNum = trueAnswerNum;
        this.pg = pg;
    }

    @Override
    protected List<Quote> doInBackground(Void... params) {
        if(myApiService == null) { // Only do this once
            QuoteEndpoint.Builder builder = new QuoteEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setApplicationName("acid or alkali")
                    .setRootUrl("https://acidoralkariranking.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            myApiService = builder.build();
        }


        //nullの代わりに空リストを入れておく
        List list = Collections.EMPTY_LIST;

        //取得
        if(quoteMode == 0){
            try {
                list = myApiService.listQuote().execute().getItems();
            } catch (IOException e) {
                list = Collections.EMPTY_LIST;
            }
        //更新
        }else if(quoteMode == 1) {
            Quote q = new Quote();
            //取得したいパラメータの登録
            q.setId(prefecturesId);
            q.setFalseAnswerNum(10 - trueAnswerNum);
            q.setTrueAnswerNum(trueAnswerNum);

            try{
                myApiService.updateQuote(q).execute();
            } catch (IOException e) {
                //return Collections.EMPTY_LIST;
            }
        //削除(多分使わない)
        }else if(quoteMode == 2) {

        //挿入(多分使わない)
        }else if(quoteMode == 3){
            Quote q = new Quote();
            //取得したいパラメータの登録
            //q.setPrefectureId(prefecturesId);
            q.setTrueAnswerNum(trueAnswerNum);

            /*try{
                //myApiService.insertQuote(q).execute();
            } catch (IOException e) {
                //return Collections.EMPTY_LIST;
            }*/

        }
        return list;
    }

    @Override
    protected void onPostExecute(List<Quote> result) {
        //スコア送信の完了時には処理を行わない
        if(result.size() != 0) {
            ArrayList<String> scoreList = new ArrayList<String>();
            String truepercent = "";

            String[] prefectureList = context.getResources().getStringArray(R.array.prefecturesArray);
            String[] prefectureList2 = context.getResources().getStringArray(R.array.prefecturesArray2minus);
            String[] prefectureList3 = new String[prefectureList.length + prefectureList2.length];

            System.arraycopy(prefectureList, 0, prefectureList3, 0, prefectureList.length);
            System.arraycopy(prefectureList2, 0, prefectureList3, prefectureList.length, prefectureList2.length);


            Resources res = context.getResources();
            String accuracyRate = res.getString(R.string.accuracyRate);



            //日本の場合、そのままGAEの順番通り突っ込む
            //if(Locale.JAPAN.equals(locale)) {
                for (Quote q : result) {
                    //ここで全ての地域のIDと正解数を代入する
                    //日本
                    if (q.getId() <= 47) {
                        truepercent = getTruePercent(q.getTrueAnswerNum(), q.getFalseAnswerNum());
                        scoreList.add(accuracyRate + ":" + truepercent + "%-(" + q.getTrueAnswerNum() + "/" + (q.getTrueAnswerNum() + q.getFalseAnswerNum()) + ")-" + prefectureList3[Integer.parseInt(String.valueOf(q.getId()))] + "1");
                    //アメリカ
                    }else if (q.getId() <= 97) {
                        truepercent = getTruePercent(q.getTrueAnswerNum(), q.getFalseAnswerNum());
                        scoreList.add(accuracyRate + ":" + truepercent + "%-(" + q.getTrueAnswerNum() + "/" + (q.getTrueAnswerNum() + q.getFalseAnswerNum()) + ")-" + prefectureList3[Integer.parseInt(String.valueOf(q.getId()))] + "2");
                    }
                }
            //アメリカの場合、はじめにアメリカ州を収納してから日本のリストを突っ込む
            /*}else{
                ArrayList<String> tmpScoreList = new ArrayList<String>();
                for (Quote q : result) {
                    //ここで全ての地域のIDと正解数を代入する
                    if (q.getId() <= 47) {
                        truepercent = getTruePercent(q.getTrueAnswerNum(), q.getFalseAnswerNum());
                        tmpScoreList.add(accuracyRate + ":" + truepercent + "%-(" + q.getTrueAnswerNum() + "/" + (q.getTrueAnswerNum() + q.getFalseAnswerNum()) + ")-" + prefectureList3[Integer.parseInt(String.valueOf(q.getId()))]);
                    }else if(q.getId() <= 97){
                        truepercent = getTruePercent(q.getTrueAnswerNum(), q.getFalseAnswerNum());
                        scoreList.add(accuracyRate + ":" + truepercent + "%-(" + q.getTrueAnswerNum() + "/" + (q.getTrueAnswerNum() + q.getFalseAnswerNum()) + ")-" + prefectureList3[Integer.parseInt(String.valueOf(q.getId()))]);
                    }
                }
                scoreList.addAll(tmpScoreList);
            }*/
            //Toast.makeText(context, testWord, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(context, RankingActivity.class);
            intent.putExtra("scoreList", scoreList);
            context.startActivity(intent);

            if(pg != null) {
                pg.dismiss();
            }
        }
    }

    //TODO: titleActivityのメソッドとかぶってる
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


//intentの受け渡しに使おうとしたけどきつい
class Score {
    public int trueAnswerNum;
    public int falseAnswerNum;
    public long id;

    public Score(int trueAnswerNum, int falseAnswerNum, long id){
        this.trueAnswerNum = trueAnswerNum;
        this.falseAnswerNum = falseAnswerNum;
        this.id = id;
    }
}
