
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import objects.DirectIndexJsonTemplate;

public class DirectIndex {
	private static DirectIndexJsonTemplate getFileTemplate(String path){
		DirectIndexJsonTemplate di = new DirectIndexJsonTemplate();
		Writer inWriter = null;
		File file = new File(path);
		String filename = file.getName();

		String[] aux = filename.split("\\.");

		String absolutePath = file.getAbsolutePath();

		String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
		filename = aux[0];
		if(aux[1].equals("html")){
			di.setFileName(absolutePath);
			try {
				inWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath+"/"+filename+".txt"), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Parser.parseInformations(inWriter, file);
			try {
				
				inWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File f = new File(filePath+"/"+filename+".txt");
			HashMap<String, Integer> w = Parser.parseText(f);
			di.setWords(w);
		}
		return di;
	}
	private static List<DirectIndexJsonTemplate> getListOfFileTemplates(List<String> paths){
		List<DirectIndexJsonTemplate> listOfFileTemplates = new ArrayList<>();
		for (String path : paths) {
			DirectIndexJsonTemplate di = DirectIndex.getFileTemplate(path);
			if(di.getFileName() != null && di.getWords() != null){
				listOfFileTemplates.add(di);
			}
		}
		return listOfFileTemplates;
	}
	private static void directIndexToFile(List<DirectIndexJsonTemplate> list){
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		String strJson = g.toJson(list);
		Writer writer = null;
		try {
			writer = new FileWriter("indexDirect.json");
			writer.write(strJson);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void directIndex(String dirName){
		List<String> paths = Parser.getFiles(new File(dirName));
		
		List<DirectIndexJsonTemplate> listOfFileTemplates = DirectIndex.getListOfFileTemplates(paths);
		directIndexToFile(listOfFileTemplates);
		
	}
}
