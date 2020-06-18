package parser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import objects.Metadata;

public final class Parser {
	public static void parseInformations(Writer writer,File input){
		try {
			//titlu
			Document doc = Jsoup.parse(input, "UTF-8");
			if(Data.getTitle(doc)!=null){
				String title = Data.getTitle(doc);
				writer.write(title);
			}
			
			//metadate
			List<Metadata> metadatas = Data.getMetadatas(doc);
			for (Metadata metadata : metadatas) {
				if(metadata.getName()!="robots"){
					writer.write(metadata.getContent());
				}
			}
			String text = Data.getText(doc);
			writer.write(text);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void parseLinks(Writer writer, File input){
		Document doc;
		try {
			doc = Jsoup.parse(input, null);
			HashSet<String> urls = Data.getLinks(doc);
			//writer.write("Link-uri: ");
	        for (String element : urls) {
				writer.write(element+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String readFromFile(String filename){
		String doc = "";
		BufferedReader br = null;
		FileReader fr = null;

		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(new File(filename));
			br = new BufferedReader(fr);

			String  sCurrentLine ;

			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				doc += sCurrentLine;
			}
		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
		return doc;
	}
	public static HashMap<String, Integer>  parseText(File file){
		String doc = readFromFile(file.getAbsolutePath());
		HashMap <String,Integer> words = new HashMap<String,Integer>();
		Data.getWords(doc, words);
		return words;
	}
	public static List<String> getFiles(File dir) {
		List<String> paths = new ArrayList<>();
		Stack<File> stack = new Stack<File>();
		stack.push(dir);
		while(!stack.isEmpty()) {
			File child = stack.pop();
			if (child.isDirectory()) {
				for(File f : child.listFiles()) stack.push(f);
			} else if (child.isFile()) {
				paths.add(child.getPath());
			}
		}
		return paths;
	}
	
}
