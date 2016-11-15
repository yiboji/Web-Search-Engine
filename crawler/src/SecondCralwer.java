import java.util.Set;
import java.util.regex.Pattern;
import java.util.*;
import javax.sound.sampled.AudioFormat.Encoding;
import org.apache.http.HttpStatus;
import com.google.common.io.Files;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SecondCralwer  extends WebCrawler{
	CrawlStat myStat;
	public SecondCralwer(){
		myStat = new CrawlStat();
	}
	@Override
	public Object getMyLocalData(){
		return myStat;
	}
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{ 
		String href = url.getURL().toLowerCase();
		return href.startsWith("http://www.wsj.com/"); 
	}
	@Override
	public void visit(Page page) {
		myStat.incSucceed();
		String url = page.getWebURL().getURL(); 
		int size = page.getContentData().length;
		if(size<1024){
			if(myStat.file_size.containsKey(1)) myStat.file_size.put(1, myStat.file_size.get(1024)+1);
			else myStat.file_size.put(1, 1);
		}
		else if(size<10*1024){
			if(myStat.file_size.containsKey(10)) myStat.file_size.put(10, myStat.file_size.get(10)+1);
			else myStat.file_size.put(10, 1);
		}
		else if(size<100*1024){
			if(myStat.file_size.containsKey(100)) myStat.file_size.put(100, myStat.file_size.get(100)+1);
			else myStat.file_size.put(100, 1);
			
		}
		else if(size<1000*1024){
			if(myStat.file_size.containsKey(1000)) myStat.file_size.put(1000, myStat.file_size.get(1000)+1);
			else myStat.file_size.put(1000, 1);
		}
		else{
			if(myStat.file_size.containsKey(10000)) myStat.file_size.put(10000, myStat.file_size.get(10000)+1);
			else myStat.file_size.put(10000, 1);
		}
		int outgoinglinks = 0;
		if(page.getParseData() instanceof HtmlParseData){
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			for(WebURL outurl:links){
				myStat.url_totle++;
				myStat.url_unique.add(outurl.getURL().toLowerCase());
				myStat.add_urls(checkAllURLS(outurl));
			}
			outgoinglinks = links.size();
		}
		String contentType = page.getContentType().split(";")[0];
		String contentKey = contentType;
		if(myStat.content_type.containsKey(contentKey)) myStat.content_type.put(contentKey,myStat.content_type.get(contentKey)+1);
		else myStat.content_type.put(contentKey, 1);
		String rowOfVisit = url+","+size+","+outgoinglinks+","+contentType+"\n";
		myStat.add_visit(rowOfVisit);
	}
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription){
		myStat.incAttemps();
		if(myStat.status_code.containsKey(statusCode)) myStat.status_code.put(statusCode,myStat.status_code.get(statusCode)+1);
		else myStat.status_code.put(statusCode,1);
		
		if(statusCode>=300&&statusCode<=399) //fetch_aborted
			myStat.incAborted();
		else if(statusCode>=200&&statusCode<=299)// fetch_succeed
			System.out.println("success");
		else// fetch_failed
			myStat.incFailed();
		
		String rowFetch = "";
		rowFetch+= (webUrl.getURL()+","+statusCode)+"\n";
		myStat.add_fetch(rowFetch);
	}
	private String checkAllURLS(WebURL weburl){
		String href = weburl.getURL();
		String resides = "";
		if(href.startsWith("http://www.wsj.com/")){
			resides = "OK";
			myStat.url_in.add(href.toLowerCase());
		}
		else{
			resides = "N_OK";
			myStat.url_out.add(href.toLowerCase());
		}
		return href+","+resides+"\n";
	}
}

