import java.util.HashMap;
import java.util.HashSet;
import java.util.*;

public class CrawlStat {

	public int fetch_attemps = 0;
	public int fetch_succeed = 0;
	public int fetch_failed = 0;
	public int fetch_aborted = 0;
	public int url_totle = 0;
	public HashSet<String> url_in = new HashSet<>();
	public HashSet<String> url_out = new HashSet<>();
	public HashSet<String> url_unique = new HashSet<>();
	public HashMap<Integer, Integer> status_code = new HashMap<>();
	public HashMap<Integer, Integer> file_size = new HashMap<>();
	public HashMap<String, Integer> content_type = new HashMap<>();
	
	public String fetch_file = "./crawl/fetch_NewsSite1.csv";
	public String visit_file = "./crawl/visit_NewsSite1.csv";
	public String urls_file = "./crawl/urls_NewsSite1.csv";
	public String report = "./crawl/CrawlReport_WallStreetJournal.txt";
	
	public ArrayList<String> fetch_lines = new ArrayList<>();
	public ArrayList<String> visit_lines = new ArrayList<>();
	public ArrayList<String> urls_lines = new ArrayList<>();
	
	public void incAttemps(){
		fetch_attemps++;
	}
	public void incSucceed(){
		fetch_succeed++;
	}
	public void incFailed(){
		fetch_failed++;
	}
	public void incAborted(){
		fetch_aborted++;
	}
	public int getAttemps(){
		return fetch_attemps;
	}
	public int getSucceed(){
		return fetch_succeed;
	}
	public int getAborted(){
		return fetch_aborted;
	}
	public int getFailed(){
		return fetch_failed;
	}
	public void add_fetch(String str){
		fetch_lines.add(str);
	}
	public void add_visit(String str){
		visit_lines.add(str);
	}
	public void add_urls(String str){
		urls_lines.add(str);
	}
	public ArrayList<String> get_fetchLines(){
		return fetch_lines;
	}
	public ArrayList<String> get_visitlines(){
		return visit_lines;
	}
	public ArrayList<String> get_urllines(){
		return urls_lines;
	}

}
