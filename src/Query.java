import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {
	
	private static final CharSequence PREFIX = "PREFIX";
	private static final String COMMENT = "#";
	
	private ArrayList<String> comments;
	private ArrayList<String> prefixs;
	
	//private ArrayList<Concepts> concepts;
	//private ArrayList<Predicates> predicates;
	
	private ArrayList<String> concepts_and_predicates;
	
	
	public ArrayList<String> getComments() {
		return comments;
	}


	public void setComments(ArrayList<String> comments) {
		this.comments = comments;
	}


	public ArrayList<String> getPrefixs() {
		return prefixs;
	}


	public void setPrefixs(ArrayList<String> prefixs) {
		this.prefixs = prefixs;
	}


	public ArrayList<String> getConcepts_and_predicates() {
		return concepts_and_predicates;
	}


	public void setConcepts_and_predicates(ArrayList<String> concepts_and_predicates) {
		this.concepts_and_predicates = concepts_and_predicates;
	}


	public Query(ArrayList<String> cp) {
		
		this.concepts_and_predicates=cp;
	}


	public static Query leggi(Scanner sc){		
		
		if(!sc.hasNextLine()) return null;
		
		ArrayList<String> cp= new ArrayList<String>();
		
		String buffQueryLine = sc.nextLine();
		
		String startS="#";
		
		while (buffQueryLine.contains(PREFIX) || buffQueryLine.startsWith(COMMENT) || buffQueryLine.isEmpty()) sc.nextLine();
		
		Pattern pattern = Pattern.compile("<inizio>(.*?)<fine>");
		Matcher matcher = pattern.matcher(buffQueryLine);
		
		//if una sola occorrenza
		while (matcher.find()) //pi� occorrenze
		{
				//System.out.println(matcher.group(1));
			   /* String s= "<";
			    String s2="";
			    s2= s.concat(matcher.group(1));	
				cp.add(s2.concat(">")); */
				cp.add(matcher.group(1));
		    
		}
		return new Query(cp);
		}
		


public void reportCP(PrintStream ps){
	for(String q:concepts_and_predicates)
		ps.println(q.toString());
}
}
