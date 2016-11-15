import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.*;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String storeFolder = "./crawl";
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(storeFolder);
		config.setMaxPagesToFetch(10000);
		config.setMaxDepthOfCrawling(16);
		config.setIncludeBinaryContentInCrawling(true);
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstextConfig = new RobotstxtConfig();
		RobotstxtServer robotstextServer = new RobotstxtServer(robotstextConfig, pageFetcher);
		try {
			CrawlController controller = new CrawlController(config, pageFetcher, robotstextServer);
			controller.addSeed("http://www.wsj.com/");
			int numberOfCrawlers = 1000;
			controller.start(SecondCralwer.class, numberOfCrawlers);
			List<Object> localData = controller.getCrawlersLocalData();
			int totalFetch = 0;
			int totalSucceed = 0;
			int totalFailed = 0;
			int totalAborted = 0;
			
			ArrayList<String> fetch_lines = new ArrayList<>();
			ArrayList<String> visit_lines = new ArrayList<>();
			ArrayList<String> url_lines = new ArrayList<>();
			
			int url_totle = 0;
			HashSet<String> url_in = new HashSet<>();
			HashSet<String> url_out = new HashSet<>();
			HashSet<String> url_unique = new HashSet<>();
			HashMap<Integer, Integer> status_code = new HashMap<>();
			HashMap<Integer, Integer> file_size = new HashMap<>();
			HashMap<String, Integer> content_type = new HashMap<>();
			
			for(Object local: localData){
				CrawlStat stat =(CrawlStat)local;
				totalFetch += stat.getAttemps();
				totalSucceed += stat.getSucceed();
				totalFailed += stat.getFailed();
				totalAborted += stat.getAborted();
				fetch_lines.addAll(stat.get_fetchLines());
				visit_lines.addAll(stat.get_visitlines());
				url_lines.addAll(stat.get_urllines());
				url_totle += stat.url_totle;
				for(String item: stat.url_in) url_in.add(item);
				for(String item: stat.url_out) url_out.add(item);
				for(String item: stat.url_unique) url_unique.add(item);
				
				for(Integer key:stat.status_code.keySet()){
					if(status_code.containsKey(key)) status_code.put(key, stat.status_code.get(key)+status_code.get(key));
					else status_code.put(key, stat.status_code.get(key));
				}
				for(Integer key:stat.file_size.keySet()){
					if(file_size.containsKey(key)) file_size.put(key, stat.file_size.get(key)+file_size.get(key));
					else file_size.put(key, stat.file_size.get(key));
				}
				for(String key:stat.content_type.keySet()){
					if(content_type.containsKey(key)) content_type.put(key, stat.content_type.get(key)+content_type.get(key));
					else content_type.put(key, stat.content_type.get(key));
				}
			}

			CrawlStat allInOne = new CrawlStat();
			allInOne.fetch_attemps = totalFetch;
			allInOne.fetch_succeed = totalSucceed;
			allInOne.fetch_failed = totalFailed;
			allInOne.fetch_aborted = totalAborted;
			allInOne.url_totle = url_totle;
			allInOne.url_in = url_in;
			allInOne.url_out = url_out;
			allInOne.url_unique = url_unique;
			allInOne.status_code = status_code;
			allInOne.file_size = file_size;
			allInOne.content_type = content_type;
			allInOne.fetch_lines = fetch_lines;
			allInOne.visit_lines = visit_lines;
			allInOne.urls_lines = url_lines;
			statics(allInOne);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("exception from controller!");
		}
	}
	public static void statics(CrawlStat stat){
			int fetch_attemps = stat.getAttemps();
			int fetch_succeed = stat.getSucceed();
			int fetch_aborted = stat.getAborted();
			int fetch_failed = stat.getFailed();
			int url_totle = stat.url_totle;
			HashSet<String> url_in = stat.url_in;
			HashSet<String> url_out = stat.url_out;
			HashSet<String> url_unique = stat.url_unique;
			HashMap<Integer, Integer> status_code = stat.status_code;
			HashMap<Integer, Integer> file_size = stat.file_size;
			HashMap<String, Integer> content_type = stat.content_type;
			String report = stat.report;
			
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
			lines.add("# fetches aborted: "+fetch_aborted+"\n");
			lines.add("# fetches failed: "+fetch_failed+"\n");
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
			try{
				FileWriter fw = new FileWriter(stat.fetch_file,true);
				for(String line:stat.fetch_lines) fw.write(line);
				fw.close();
			} catch(Exception e){
				e.printStackTrace();
			}
			try{
				FileWriter fw = new FileWriter(stat.visit_file,true);
				for(String line:stat.visit_lines) fw.write(line);
				fw.close();
			} catch(Exception e){
				e.printStackTrace();
			}
			try{
				FileWriter fw = new FileWriter(stat.urls_file,true);
				for(String line:stat.urls_lines) fw.write(line);
				fw.close();
			} catch(Exception e){
				e.printStackTrace();
			}
			
			System.out.println("DONE!!!");
	}
}
