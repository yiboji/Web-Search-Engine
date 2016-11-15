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

public class MyCrawler  extends WebCrawler{
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");
	private static int fetch_attemps = 0;
	private static int fetch_succeed = 0;
	private static int fetch_failed = 0;
	private static int url_totle = 0;
	private static HashSet<String> url_in = new HashSet<>();
	private static HashSet<String> url_out = new HashSet<>();
	private static HashSet<String> url_unique = new HashSet<>();
	private static HashMap<Integer, Integer> status_code = new HashMap<>();
	private static HashMap<Integer, Integer> file_size = new HashMap<>();
	private static HashMap<String, Integer> content_type = new HashMap<>();
	
	private static String fetch_file = "./crawl/fetch_NewsSite1.csv";
	private static String visit_file = "./crawl/visit_NewsSite1.csv";
	private static String urls_file = "./crawl/urls_NewsSite1.csv";
	private static String report = "./crawl/CrawlReport_WallStreetJournal.txt";
 
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{ 
		String href = url.getURL().toLowerCase();
		return href.startsWith("http://www.wsj.com/"); 
	}
	@Override
	public void visit(Page page) {
		fetch_succeed++;
		String url = page.getWebURL().getURL(); 
		int size = page.getContentData().length;
		if(size<1024){
			if(file_size.containsKey(1)) file_size.put(1, file_size.get(1024)+1);
			else file_size.put(1, 1);
		}
		else if(size<10*1024){
			if(file_size.containsKey(10)) file_size.put(10, file_size.get(10)+1);
			else file_size.put(10, 1);
		}
		else if(size<100*1024){
			if(file_size.containsKey(100)) file_size.put(100, file_size.get(100)+1);
			else file_size.put(100, 1);
			
		}
		else if(size<1000*1024){
			if(file_size.containsKey(1000)) file_size.put(1000, file_size.get(1000)+1);
			else file_size.put(1000, 1);
		}
		else{
			if(file_size.containsKey(10000)) file_size.put(10000, file_size.get(10000)+1);
			else file_size.put(10000, 1);
		}
		int outgoinglinks = 0;
		if(page.getParseData() instanceof HtmlParseData){
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			for(WebURL outurl:links){
				url_totle++;
				url_unique.add(outurl.getURL().toLowerCase());
				try {
					FileWriter all_fw = new FileWriter(urls_file,true);
					all_fw.write(checkAllURLS(outurl));
					all_fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			outgoinglinks = links.size();
		}
		String contentType = page.getContentType();
		String contentKey = contentType;
		if(content_type.containsKey(contentKey)) content_type.put(contentKey,content_type.get(contentKey)+1);
		else content_type.put(contentKey, 1);
		String rowOfVisit = url+","+size+","+outgoinglinks+","+contentType+"\n";
		try {
			FileWriter fw = new FileWriter(visit_file,true);
			fw.write(rowOfVisit);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription){
		fetch_attemps++;
		if(status_code.containsKey(statusCode)) status_code.put(statusCode,status_code.get(statusCode)+1);
		else status_code.put(statusCode,1);
		if(statusCode!=HttpStatus.SC_OK) fetch_failed++;
		String rowFetch = "";
		rowFetch+= (webUrl.getURL()+","+statusCode)+"\n";
		FileWriter fw;
		try {
			fw = new FileWriter(fetch_file,true);
			fw.write(rowFetch);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String checkAllURLS(WebURL weburl){
		String href = weburl.getURL();
		String resides = "";
		if(href.startsWith("http://www.wsj.com")){
			resides = "OK";
			url_in.add(href.toLowerCase());
		}
		else{
			resides = "N_OK";
			url_out.add(href.toLowerCase());
		}
		return href+","+resides+"\n";
	}
	@Override
	public void onBeforeExit(){
		String name = "Yibo Ji\n";
		String USCID = "8317398887\n";
		String site = "http://www.wsj.com/\n";
		String fetch_title = "Fetch Statistics:\n================\n";
		String outgoing_title = "Outgoing URLs:\n================\n";
		String status_title = "Status Codes:\n================\n";
		String file_title = "File Sizes:\n================\n";
		String content_title = "Content Types:\n================\n";
		ArrayList<String> lines = new ArrayList<>();
		lines.add("Name: "+name);
		lines.add("USC ID: "+USCID);
		lines.add("News site crawled: "+site);
		lines.add("\n");
		lines.add(fetch_title);
		lines.add("# fetches attempted: "+fetch_attemps+"\n");
		lines.add("# fetches succeeded: "+fetch_succeed+"\n");
		lines.add("# fetches aborted: "+fetch_failed+"\n");
		lines.add("\n");
		lines.add(outgoing_title);
		lines.add("Total URLs extracted: "+url_totle+"\n");
		lines.add("# unique URLs extracted: "+url_unique.size()+"\n");
		lines.add("# unique URLs within News Site: "+url_in.size()+"\n");
		lines.add("# unique URLs outside News Site: "+url_out.size()+"\n");
		lines.add("\n");
		lines.add(status_title);
		for(Integer key: status_code.keySet()){
			lines.add(key+": "+status_code.get(key)+"\n");
		}
		lines.add("\n");
		lines.add(file_title);
		lines.add("< 1KB: "+(file_size.containsKey(1)?file_size.get(1):0)+"\n");
		lines.add("1KB ~ <10KB: "+(file_size.containsKey(10)?file_size.get(10):0)+"\n");
		lines.add("10KB ~ <100KB: "+(file_size.containsKey(100)?file_size.get(100):0)+"\n");
		lines.add("100KB ~ <1MB: "+(file_size.containsKey(1000)?file_size.get(1000):0)+"\n");
		lines.add(">= 1MB: "+(file_size.containsKey(10000)?file_size.get(10000):0)+"\n");
		lines.add("\n");
		lines.add(content_title);
		for(String key: content_type.keySet()){
			lines.add(key+": "+content_type.get(key)+"\n");
		}
		try{
			FileWriter fw = new FileWriter(report,true);
			for(String line:lines) fw.write(line);
			fw.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("DONE!!!");
	}
}

