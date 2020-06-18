
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import objects.WordsTemplate;
import parser.Data;
import parser.Parser;
import stemmer.Stemmer;

public class BooleanSearch {

	private static HashSet<WordsTemplate> indirectIndex;

	private static void loadIndirectIndex() {
		Gson gson = new Gson();
		String json = Parser.readFromFile("indexIndirect.json");
		indirectIndex = gson.fromJson(json, new TypeToken<HashSet<WordsTemplate>>() {
		}.getType());
	}

	public	 static String readQueryFromUser() {
		String query;
		Scanner scanner = new Scanner(System.in);
		System.out.print("Introduceti cuvintele: ");
		query = scanner.nextLine();
		scanner.close();
		return query;
	}

	private static List<String> splitIntoOperands(String query) {
		List<String> list = new ArrayList<String>();
		String[] words = query.split("[-|+|\\s]");
		for (String string : words) {

			if (Data.isException(string)) {
				string = string.toLowerCase();
				list.add(string);
			} else {
				string = string.toLowerCase();
				if (Data.isStopWord(string)) {
					continue;
				} else {
					Stemmer s = new Stemmer();
					char[] chs = string.toCharArray();
					s.add(chs, chs.length);
					s.stem();
					string = s.toString();
					list.add(string);
				}
			}
		}
		return list;
	}

	private static List<String> splitIntoOperators(String query) {
		List<String> list = new ArrayList<String>();
		char c;
		int len = query.length();
		for (int i = 0; i < len; i++) {
			c = query.charAt(i);
			if ((c == '+') || (c == '-') || (c == ' ')) {
				list.add(c + "");
			}
		}
		return list;
	}

	private static HashSet<String> doAND(HashSet<String> op1, HashSet<String> op2) {
		HashSet<String> rez = new HashSet<>();
		int index1 = op1.size();
		int index2 = op2.size();
		if (index1 < index2) {
			for (String string : op1) {
				if (op2.contains(string)) {
					rez.add(string);
				}
			}
		} else {
			for (String string : op2) {
				if (op1.contains(string)) {
					rez.add(string);
				}
			}
		}
		return rez;
	}

	private static HashSet<String> doOR(HashSet<String> op1, HashSet<String> op2) {
		HashSet<String> rez = new HashSet<>();
		int index1 = op1.size();
		int index2 = op2.size();
		if (index1 > index2) {
			rez.addAll(op1);
			for (String string : op2) {
				rez.add(string);
			}
		} else {
			rez.addAll(op2);
			for (String string : op1) {
				rez.add(string);
			}
		}
		return rez;
	}

	private static HashSet<String> doNOT(HashSet<String> op1, HashSet<String> op2) {
		HashSet<String> rez = new HashSet<>();
		for (String string : op1) {
			if (!op2.contains(string)) {
				rez.add(string);
			}
		}
		return rez;
	}

	private static HashSet<String> getDocsForWord(String word) {
		for (WordsTemplate wordsTemplate : indirectIndex) {
			if (wordsTemplate.getWord().equals(word)) {
				return wordsTemplate.getOnlyDocs();
			}
		}
		return null;
	}

	public static HashSet<String> booleanSearch(String query) {
		HashSet<String> rez = new HashSet<>();
		loadIndirectIndex();
		List<String> operands = splitIntoOperands(query);
		List<String> operators = splitIntoOperators(query);
		
		if(operators.size() == 0){
			if(operands.size() == 1){
				rez.addAll(getDocsForWord(operands.get(0)));
			}
		}
		
		// rez.addAll(getDocsForWord(operands.get(0)));
		for (int i = 0; i < operators.size(); ++i) {
			switch (operators.get(i)) {
			case "+":
				rez.addAll(doAND(getDocsForWord(operands.get(i)), getDocsForWord(operands.get(i + 1))));
				break;
			case "-":
				rez.addAll(doNOT(getDocsForWord(operands.get(i)), getDocsForWord(operands.get(i + 1))));
				break;
			case " ":
				rez.addAll(doOR(getDocsForWord(operands.get(i)), getDocsForWord(operands.get(i + 1))));
				break;
			}
		}
		return rez;
	}
}
