import Analysis.Analyse;
import Analysis.CountMetrics;
import Analysis.GetInitial;


public class Main {
	public static String url = "";

	public static void main(String[] args) {
		process("https://github.com/rhq-project/rhq.git","java");
		process("https://github.com/dropwizard/dropwizard.git","java");
		process("https://github.com/hhru/nuts-and-bolts.git","java");
		process("https://github.com/nickboldt/temp-jbosstools-javaee.git","java");
		process("https://github.com/querydsl/querydsl.git","java");
		
		process("https://github.com/TurboGears/tg2devtools.git","python");
		process("https://github.com/jhamrick/gutenbach.git","python");
		process("https://github.com/dmwm/WMCore.git","python");
		process("https://github.com/McNetic/CouchPotatoServer-de.git","python");
		process("https://github.com/mrkipling/maraschino.git","python");
		process("https://github.com/seatgeek/cronq.git","python");
		process("https://github.com/SiCKRAGETV/SickRage.git","python");
		
		process("https://github.com/litecommerce/core.git","php");
	}
	
	public static void process(String url,String language){
		String parsedURL = url.substring(url.lastIndexOf("/")+1,url.length()-4).replaceAll("[^\\w\\s\\-_]", "");
		System.out.println(parsedURL);
		new GetInitial(url,parsedURL , parsedURL).start();
		new Analyse(parsedURL, parsedURL, language).start();
		new CountMetrics(parsedURL).start();
	}
}
