package jp.tecco.acidbackend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by makotonishimoto on 2015/05/13.
 */

@Entity
public class Quote {
    @Id
    long id;
    int falseAnswerNum;
    int trueAnswerNum;

    public Quote() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFalseAnswerNum() {
        return falseAnswerNum;
    }

    public void setFalseAnswerNum(int falseAnswerNum) {
        this.falseAnswerNum = falseAnswerNum;
    }


    public int getTrueAnswerNum() {
        return trueAnswerNum;
    }

    public void setTrueAnswerNum(int trueAnswerNum) {
        this.trueAnswerNum = trueAnswerNum;
    }
}
