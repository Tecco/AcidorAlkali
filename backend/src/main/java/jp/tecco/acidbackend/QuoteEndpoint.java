package jp.tecco.acidbackend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;

import static jp.tecco.acidbackend.OfyService.ofy;


/**
 * Created by makotonishimoto on 2015/05/13.
 */
@Api(name = "quoteEndpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "acidoralkariranking.appspot.com", ownerName = "acidoralkariranking.appspot.com", packagePath=""))
public class QuoteEndpoint {

// Make sure to add this endpoint to your web.xml file if this is a web application.

    public QuoteEndpoint() {

    }

    /**
     * Return a collection of quotes
     *
     * @param count The number of quotes
     * @return a list of Quotes
     */
    @ApiMethod(name = "listQuote")
    public CollectionResponse<Quote> listQuote(@Nullable @Named("cursor") String cursorString,
                                               @Nullable @Named("count") Integer count) {


        Query<Quote> query = ofy().load().type(Quote.class);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<Quote> records = new ArrayList<Quote>();
        QueryResultIterator<Quote> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }

        //Find the next cursor
        if (cursorString != null && cursorString != "") {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }
        return CollectionResponse.<Quote>builder().setItems(records).setNextPageToken(cursorString).build();
    }

    /**
     * This inserts a new <code>Quote</code> object.
     * @param quote The object to be added.
     * @return The object to be added.
     */
    //使わないのでコメントアウト
    //@ApiMethod(name = "insertQuote")
    /*public Quote insertQuote(Quote quote) throws ConflictException {
    //If if is not null, then check if it exists. If yes, throw an Exception
    //that it is already present
        if (quote.getId() != 0) {
            if (findRecord(quote.getId()) != null) {
                throw new ConflictException("Object already exists");
            }
        }
    //Since our @Id field is a Long, Objectify will generate a unique value for us
    //when we use put
        ofy().save().entity(quote).now();
        return quote;
    }*/

    /**
     * This updates an existing <code>Quote</code> object.
     * @param quote The object to be added.
     * @return The object to be updated.
     */
    @ApiMethod(name = "updateQuote")
    public Quote updateQuote(Quote quote)throws NotFoundException {
        if (findRecord(quote.getId()) == null) {
            throw new NotFoundException("Quote Record does not exist");
        }

        int nowTrueAnswerNum = quote.getTrueAnswerNum();
        int nowFalseAnswerNum = quote.getFalseAnswerNum();

        //10以下の場合で制限をかけておく
        if(nowTrueAnswerNum <= 10 && nowFalseAnswerNum <= 10) {
            //現在のレコードの取得
            Quote record = findRecord(quote.getId());
            //現在の正解数の取得
            int trueAnswerNum = record.getTrueAnswerNum();
            int falseAnswerNum = record.getFalseAnswerNum();
            //レコードの正解数に今の成績を加算する
            record.setTrueAnswerNum(trueAnswerNum + nowTrueAnswerNum);
            record.setFalseAnswerNum(falseAnswerNum + nowFalseAnswerNum);

            //DBで永続化する
            ofy().save().entity(record).now();
        }else{
            quote = null;
        }
        return quote;
    }

    /**
     * This deletes an existing <code>Quote</code> object.
     * @param id The id of the object to be deleted.
     */
    //使わないのでコメントアウト
    //@ApiMethod(name = "removeQuote")
    /*public void removeQuote(@Named("id") long id) throws NotFoundException {
        Quote record = findRecord(id);
        if(record == null) {
            throw new NotFoundException("Quote Record does not exist");
        }
        ofy().delete().entity(record).now();
    }*/

    //Private method to retrieve a <code>Quote</code> record
    private Quote findRecord(long id) {
        return ofy().load().type(Quote.class).id(id).now();
    //or return ofy().load().type(Quote.class).filter("id",id).first.now();
    }

}