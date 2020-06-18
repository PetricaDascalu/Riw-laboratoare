import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Laborator1 {
	public void parse(String path)
	{
		File file = new File(path);
		try {
			Document doc = Jsoup.parse(file, "UTF-8");
			String title  = doc.title();
			Elements metaTags = doc.getElementsByTag("meta");
			Elements aTags = doc.getElementsByTag("a");
			String text =  doc.body().text();
			
			for(Element tag : metaTags)
			{
				String name = tag.attr("name");
				if(name.equals("description") || name.equals("keyword") || name.equals("robots"))
				{
					System.out.println("Numele: " + name);
				}
			}
			
			for(Element atag : aTags)
			{
				String href = atag.attr("href");
				System.out.println("Href: " + href);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Laborator1 l = new Laborator1();
		l.parse("");
	}
}
