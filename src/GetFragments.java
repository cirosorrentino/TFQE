/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ciro
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class TimeComponents { // contiene le coppie tipo - tipoConcreto   es. Use - Religious , oppure Material - Wood dopo tipo è la classe di livello superiore considerata
	  private long BeforeConnTime;
	  private long AfterConnTime;
	  private long AfterResponseTime;
	  private long AfterRenderingTime;
	  
	  long getBeforeConnTime() {
		  return this.BeforeConnTime;
	  }
	  long getAfterConnTime() {
		  return this.AfterConnTime;
	  }
	  long getAfterResponseTime() {
		  return this.AfterResponseTime;
	  }
	  long getAfterRenderingTime() {
		  return this.AfterRenderingTime;
	  }
	  
	  void setBeforeConnTime(long bct) {
		 this.BeforeConnTime=bct;
	  }
	  void setAfterConnTime(long act) {
		  this.AfterConnTime=act;
	  }
	  void setAfterResponseTime(long arest) {
		  this.AfterResponseTime=arest;
	  }
	  void setAfterRenderingTime(long arent) {
		  this.AfterRenderingTime=arent;
	  }
	}

class Conf { // contiene i parametri di configurazione
	
	  static String baseUriStardog ="http://localhost:5820/c4t4_0_1releaseBETA_v1/";
	  static String baseUriMarmotta ="http://localhost:8080/marmotta/sparql/";
	  static String prefissi_standard = "";
//			  "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX crm:<http://www.cidoc-crm.org/cidoc-crm/> PREFIX c4t:<http://www.temotec.it/cidoc4temotec#> "+
//			 	"PREFIX cat:<http://www.temotec.it/CHthesauri/category#> PREFIX temp:<http://www.temotec.it/CHthesauri/temporalEntity#> "+
//			 	"PREFIX agent:<http://www.temotec.it/CHthesauri/agent#> PREFIX analysis:<http://www.temotec.it/CHthesauri/analysis#> "+
//			 	"PREFIX func:<http://www.temotec.it/CHthesauri/functionalType#> PREFIX haz:<http://www.temotec.it/CHthesauri/hazard#> "+
//			 	"PREFIX int:<http://www.temotec.it/CHthesauri/intervention#> PREFIX loc: <http://www.temotec.it/CHthesauri/locationCharacteristic#> "+
//			 	"PREFIX locChange: <http://www.temotec.it/CHthesauri/locationCharacteristicChange#> PREFIX mat: <http://www.temotec.it/CHthesauri/material#> "+
//			 	"PREFIX mU: <http://www.temotec.it/CHthesauri/measurementUnit#> PREFIX mec: <http://www.temotec.it/CHthesauri/mechanism#> "+ 
//			 	"PREFIX per: <http://www.temotec.it/CHthesauri/period#> PREFIX property: <http://www.temotec.it/CHthesauri/propertyType#> "+
//			 	"PREFIX struct: <http://www.temotec.it/CHthesauri/structuralType#> PREFIX requirement: <http://www.temotec.it/CHthesauri/requirement#> "+
//			 	"PREFIX resource: <http://www.temotec.it/CHthesauri/resource#> PREFIX risk: <http://www.temotec.it/CHthesauri/riskFactor#> "+
//			 	"PREFIX style: <http://www.temotec.it/CHthesauri/style#> PREFIX technique: <http://www.temotec.it/CHthesauri/technique#> "+
//			 	"PREFIX test: <http://www.temotec.it/CHthesauri/test#> PREFIX use: <http://www.temotec.it/CHthesauri/use#> "+
//			 	"PREFIX vuln: <http://www.temotec.it/CHthesauri/vulnerability#> PREFIX testSuite: <http://suite#> ";
//	  
	  public String nomefile;
	  public String nomefileOutput;
	  public String nomefileResultsOutput;
	  public StringBuffer elencoquery;
	  public int numeroMisure;
	  public String prefissiFile;
	  public StringBuffer prefissi;
	  public String baseUri;
	  public String selectUri;
	  public String updateUri;
	  public String method; // GET o POST
	  public String type;
	  public String server; // marmotta o stardog
	  public String reasoning;  // utile per stardog: SL, RL, EL, RDFS, None etc..
	  public boolean reasoningOn;
	  public int Cont;
	  public int soglia_output;
	  public int verbose; //  -v 0  ; -v 1 ; -v 2     verbose low, middle e high
	  public int numPairs; // numero massimo di coppie nome-valore prelevabili e caricabili attraverso una singola query sparql dall'ontologia
	  public String timeType; // valori: ms o ns (sta per millisecondi o nanosecondi)
	  public int delay; // ritardo in ms tra due interrogazioni successive
	  
	  Conf() {
		  baseUri=baseUriMarmotta; //  impostatato su un installazione di default di marmotta altrimenti modificabile trmite opzioni da riga di comando
		  selectUri=baseUri+"select";
		  updateUri=baseUri+"update";
	 	  method = "POST";
		  type = "html";
		  numeroMisure=1;
		  server = "marmotta";
		  reasoning="none";
		  reasoningOn=false;
		  this.prefissi= new StringBuffer(); //prefissi_standard);
		  this.elencoquery= new StringBuffer();
		  Cont = 0;
		  soglia_output=Integer.MAX_VALUE;
		  verbose = 2;
		  numPairs = Integer.MAX_VALUE;
		  timeType = "ns";
		  delay = 500;
		  nomefileOutput="defaultOutput.csv";
	  }
	  
	  Conf(String bu, String su, String uu, String m, String t, String p, int n, String s, int cont, String r, int so, int v, int np, String tt, int d, String w) {
		  baseUri=bu; // è impostatato su un installazione di defualt di marmotta altrimenti modificabile trmite opzioni da riga di comando
		  selectUri=su;
		  updateUri=uu;
		  method = m;
		  type = t;
		  this.prefissi= new StringBuffer(p);
		  numeroMisure=n;
		  server = s;
		  Cont = cont;
		  reasoning =r;
		  if (!r.equalsIgnoreCase("none")) reasoningOn = true;
		  soglia_output=so;
		  verbose =v;
		  numPairs = np;
		  timeType = tt;
		  delay = d;
		  nomefileOutput= w;
	  }
}

public class GetFragments {
	
	private static String executeQuery(String query, String surl) { // serve alla GET ; esegue "query" sulla specificata "surl"
		   String html2="";
		   StringBuffer sb = new StringBuffer();
		 //  StringBuffer htmlBuffer = new StringBuffer();
		 try {
		   String encodedQuery = URLEncoder.encode(query, "UTF-8");
		   StringBuffer eqsb = new StringBuffer(encodedQuery);
		   int x=eqsb.indexOf("+");  
		   while (x > 0) {
			   eqsb.replace(x,x+1,"%20");
			   x=eqsb.indexOf("+",x); 
		   }   
		   x=eqsb.indexOf("_");  
		   while (x > 0) {
			   eqsb.replace(x,x+1,"%5F");
			   x=eqsb.indexOf("_",x); 
		   }   
		   
		   encodedQuery = eqsb.toString();
		  // System.out.println(encodedQuery);
		 //  System.out.format("'%s'\n", surl+encodedQuery);
		   URL url = new URL(surl+encodedQuery); //+"&writer=html");
		//********System.out.println(url);
		   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		   
		 //  BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
		      
		   String line;
		   
		   //!!!verificare lettura con connection.getContent())
		    int length = 0x8FFFFFF; // 128 Mb
		   
		 // MappedByteBuffer out = new RandomAccessFile("virtual.dat", "rw").getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);
		  Scanner rd = new Scanner(new BufferedReader(new InputStreamReader(connection.getInputStream())));;
			 while (rd.hasNextLine()) {
				 sb.append(rd.nextLine());
			 }
			 rd.close();
			 
			 connection.disconnect();
		//   while ( ((line=rd.readLine()) !=null) ) {	  
//			   html2 += line;
		 //  } 
		 //  rd.close();
		  }   
		  catch(MalformedURLException ex) {
		         ex.printStackTrace();
		         System.out.println("**ECCEZIONE MalformedURL sulla query **"+query+"**  "+ex.toString());
		 } catch(IOException ioex) {
		         ioex.printStackTrace();
		         System.out.println("**ECCEZIONE I/O sulla query **"+query+"**  "+ioex.toString());
		 }
		 //html2=htmlBuffer.toString();
		// if (html2.length()>1000) System.out.println("\n"+html2.substring(0,999)+"\n");
		 	// if (sb.length()>1000) System.out.println("\n"+sb.substring(0,999)+"\n");
		 	// else System.out.println("\n"+sb+"\n");
		 System.out.flush();
		 return sb.toString();
		 }
	
	private static TimeComponents executeQuery(String query, String surl, Conf c, boolean append) { // esegue "query" sulla specificata "surl"
		   StringBuffer sb = new StringBuffer();
		   HttpURLConnection conn;
		   String urlParameters ="";
		   byte[] postData = null;
		   TimeComponents tc = new TimeComponents();
		   
		 //  StringBuffer htmlBuffer = new StringBuffer();
		  if (c.method.equalsIgnoreCase("POST")) {
			  System.out.println("METODO: private static TimeComponents executeQuery, CONFIGURING POST REQUEST PARAMETERS");
		   		if (c.server.equals("stardog")) urlParameters  = "query="+c.prefissi+" "+query+"&reasoning="+c.reasoningOn+"&reasoning="+c.reasoning; // STARDOG
		   		else if (c.server.equals("marmotta")) urlParameters  = ""+c.prefissi+" "+query;       //MARMOTTA
		   		else {System.out.println("Server "+c.server+" non supportato.");System.exit(0);}
		   		if (c.verbose>=2) System.out.println("urlParameters: "+urlParameters.replaceAll(" {2,}", " ")+"\n");
				try { postData       = urlParameters.getBytes( StandardCharsets.UTF_8);} catch ( Exception e) {System.out.println(e);}
				//	   char[]  postDataString = String.  postData.;
				int    postDataLength = postData.length;
				String request        = surl; 
				try { 
					   URL    url            = new URL( request );
					   // take the start time
					   tc.setBeforeConnTime(System.nanoTime());
					   conn= (HttpURLConnection) url.openConnection();
					   tc.setAfterConnTime(System.nanoTime());
					   conn.setDoOutput( true );
					   conn.setInstanceFollowRedirects( false );
					   conn.setRequestMethod( "POST" );
					   if (c.server.equals("stardog")) {
						  // if (surl.contains("update"))
						//	   conn.setRequestProperty( "Content-Type", "application/sparql-update");
						//  else
							  conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); //application/x-www-form-urlencoded,");  // STARDOG
						   conn.setRequestProperty("Accept", "text/tab-separated-values"); //csv"); //"text/boolean");
					   }				   
					   if (c.server.equals("marmotta")) {
						   conn.setRequestProperty( "Content-Type", "application/sparql-update");   // MARMOTTA
					   }
					   conn.setRequestProperty( "charset", "utf-8");
					   conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
					   conn.setUseCaches( false );
					    DataOutputStream wr = new DataOutputStream( conn.getOutputStream()) ;
					      wr.write( postData );
					   // take the end time without rendering
					   tc.setAfterResponseTime(System.nanoTime());
					 	   
					  Scanner rd = new Scanner(new BufferedReader(new InputStreamReader(conn.getInputStream())));;
					 while (rd.hasNextLine()) {
						 sb.append("<inizio>"+rd.nextLine()+"<fine>    ");
					 }
					 tc.setAfterRenderingTime(System.nanoTime());
					 rd.close();
					 conn.disconnect();
				 }   
				 catch(MalformedURLException ex) {
				         ex.printStackTrace();
				         System.out.println("**ECCEZIONE MalformedURL sulla query **"+query+"**  "+ex.toString());
				 } 
				 catch(IOException ioex) {
				         ioex.printStackTrace();
				         System.out.println("**ECCEZIONE I/O sulla query **"+query+"**  "+ioex.toString());
				 }
				 
				 if (sb.length()>c.soglia_output) {
					 if (c.verbose>=1) System.out.println("\n***POST TRUNCATED OUTPUT: "+(sb.substring(0,c.soglia_output-1)).toString().replaceAll("\t{1,}", " ")+"\n");
				 } else 
			     if (c.verbose>=1) 
			    	 System.out.println("\n***POST OUTPUT: "+sb.toString().replaceAll("\t{1,}", " ")+"\n");
				 System.out.flush();
		  } else if (c.method.equalsIgnoreCase("GET")) {  // if POST else...
			  if (c.verbose>=2) System.out.println("\n***QUERY:  "+c.selectUri+query);
			  sb.append(executeQuery(c.prefissi+" "+query, c.selectUri)); 
			  if (c.verbose>=1) System.out.println("\n***GET OUTPUT: "+sb.toString().replaceAll("\t{1,}", " "));
		  } else {
			  System.out.println("\n***Method "+c.method+" non supportato (solo GET o POST)");
			  sb.append("ERROREMETHOD");
		  }
		  String tipoQuery = (query.substring(0, 6)).toUpperCase();
		  if (tipoQuery.equals("INSERT") || tipoQuery.equals("DELETE")) c.Cont++; 
		 //return sb.toString();
		  try {  // scrive i risultati in un file
				 FileWriter fileout = new FileWriter("./"+c.nomefileResultsOutput, append);
				 fileout.append(sb.toString());
				 fileout.flush();
				 fileout.close();
				 } catch (IOException e) {
			            System.out.println(e);
			     }
		  return tc;
		 }
	
	static Query getIRIs(String nomef) throws IOException {
		ArrayList<Query> queries = new ArrayList<Query>();
		Query q = null;
		try {
			Scanner sc = new Scanner(new File(nomef));
			q = Query.leggi(sc);
			sc.close();
		} catch (FileNotFoundException e) 
		{	System.err.println("Il file non è stato trovato");}
		return q;
	}
	
	/****************************************/
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Conf c = new Conf();
                
               //// String[] database_list = {"b4"};
		//int[] database_subj_num = {5};
		String[] database_list = {"b1","b2","b3","b4","b5","b6","b7","b10","b12"};
		int[] database_subj_num = {10,10,10,10,10,10,10,10,10};
                int[] database_triple_num = {0,0,0,0,0,0,0,0,0,0};
                c.method = "POST";
		c.server="stardog";
		c.reasoningOn = false;
		c.verbose=0;
                
                long startTime = System.nanoTime();
                
	for (int k=0; k<database_list.length; k++) { 
		String database = database_list[k]; // nome del database che si intende indicizzare
		int subjects_number = database_subj_num[k]; // numero di soggetti per ogni tipo
				
		
		c.nomefileResultsOutput = ""+database+"types.txt";
		//types.reportCP(System.out);
		
		c.soglia_output = Integer.MAX_VALUE;
                c.selectUri = "PIPPOURI";
		String querytype = "select distinct ?type where {?s2 a ?type}";
                String surl = "http://156.54.166.83:8443/"+database+"/query";
		TimeComponents ts = executeQuery(querytype, surl, c,false);
		
		Query types = getIRIs(c.nomefileResultsOutput);
		
		c.nomefileResultsOutput = ""+database+"subjects.txt";
		ArrayList <String> types_list = types.getConcepts_and_predicates();
                
                try {  // azzera il contenuto del file dei soggetti
				FileWriter fileout = new FileWriter("./"+c.nomefileResultsOutput, false);
                                fileout.close();
                }  catch (IOException e) {
	            System.out.println(e);
	        }
                
                while (types_list.remove("?type")) {
                  //  System.out.println("Elemento rimosso.");
                };
		for (String type:types_list) {
			String querysubjects = "select distinct ?s  where {?s a "+type+"} limit "+subjects_number;
			TimeComponents ts2 = executeQuery(querysubjects, surl, c, true);
	        }
		
		Query subjects = getIRIs(c.nomefileResultsOutput);
		
		int all_percentage = 0;
		c.nomefileResultsOutput = ""+database+"triplesfragment.txt";
		ArrayList <String> subjects_list = subjects.getConcepts_and_predicates();
                while (subjects_list.remove("?s")) {
                  //  System.out.println("Elemento rimosso.");
                };
                while (subjects_list.removeIf(n -> (n.startsWith("_:")) )) {
                  //  System.out.println("Elemento rimosso.");
                };
                
		int cont = 1;
                String f = ""+database+"_cut.nt";
                try {  // azzera il contenuto del file delle triple
				FileWriter fileout = new FileWriter("./"+f, false);
                                fileout.close();
                }  catch (IOException e) {
	            System.out.println(e);
	        }
                
                int num_triple = 0;
                int num_triple_db = 0;
		for (String subject:subjects_list) {
			//System.out.println("Cerco tutte le triple per il "+cont+"° soggetto: "+subject);
			String querytriples = "select  ?p ?o where {"+subject+" ?p ?o}";
                        num_triple_db = 0;
			TimeComponents ts3 = executeQuery(querytriples, surl, c, false );
			
			ArrayList<String> predicates_and_objects =new ArrayList<String>();
			try {
				FileReader fil = new FileReader(c.nomefileResultsOutput);
				Scanner sc = new Scanner(fil);
				while (sc.hasNextLine()) {
					String linea = sc.nextLine();
					Pattern pattern = Pattern.compile("<inizio>(.*?)<fine>");
					Matcher matcher = pattern.matcher(linea);
			        // check all occurance
			        while (matcher.find()) {
			            String m = matcher.group();
			            //System.out.println(m);
                                    if (!(m.equalsIgnoreCase("<inizio>?p	?o<fine>"))) {
                                        m = m.substring(8, m.length()-6);
			                predicates_and_objects.add(m);
                                    }
			        }
				}
				//System.out.println("Numero di elementi parserizzati dalla linea: "+predicates_and_objects.size());
				fil.close();
				sc.close();
			} catch (IOException e) {
	            System.out.println(e);
	        }
						
			//System.out.println("Predicates_and_objects = "+predicates_and_objects.toString());
			
			c.nomefileResultsOutput = ""+database+"insert.txt";
			
			String predicate = "";
			String object = "";
			try {  // scrive i risultati in un file e sul server Stardog
				FileWriter fileout = new FileWriter("./"+f, true);
				int elements = predicates_and_objects.size();
				//System.out.println("Inserisco "+elements/2+" elementi (?p ?o) nel file del taglio relativi al subject ?s = "+subject);
				double percentage = 0;
				int i = 0;
				while (i<elements) {
				//for (int i=0; i <elements; i=i+2) { 
					//predicate = predicates_and_objects.get(i);
					//object = predicates_and_objects.get(i+1);
					fileout.append(subject+" "+predicates_and_objects.get(i)+" . \n");
                                        num_triple++;
                                        num_triple_db++;
                                        /** Percentuale di  completamento Type
					/*double old_percentage = percentage;
					percentage = (double)i/(double)elements*100;
					if ((percentage-old_percentage)>10) System.out.println("Type  completato al "+percentage+"%"); */
					i = i+1;
				}
				fileout.flush();
				fileout.close();
			} catch (IOException e) {
	            System.out.println(e);
	     }
                        //** Percentuale di completamento dell'indicizzazione
			all_percentage = (int)Math.ceil((double)cont/(double)subjects_list.size()*60);
			System.out.print("Database fragment extraction %completed: "+database+"  ");
			System.out.print(new String(new char[all_percentage]).replace("\0", "|"));
			System.out.println(new String(new char[60-all_percentage]).replace("\0", "-"));
                        database_triple_num[k] =num_triple_db;
			cont++; // conta i soggetti letti 
	    }
			
		System.out.println("Database "+database+" fragment extraction completed.");
                System.out.println("Soggetti estratti: "+(cont-1));
                System.out.println("Numero triple estratte: "+ num_triple);

	}
	
        long endTime =  System.nanoTime();
        long totTime = startTime - endTime;
        System.out.println("All database fragment extraction completed in "+totTime+" ns.");
        for (int k=0; k<database_list.length; k++) { 
             System.out.println("Dataset fragment di "+database_list[k]+ " numero triple "+database_triple_num[k]+" .");
        }
	}
       
	
}
