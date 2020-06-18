
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.net.*; 

public class Crawler{
	LinkedList<String> coada;
	HashSet<String> vizitate;
	HashSet<String> domeniuVizitate;
	RequestHTTP http;
	Crawler(){
		coada = new LinkedList<String>();
		vizitate = new HashSet<String>();
		domeniuVizitate = new HashSet<String>();
		http = new RequestHTTP();
	}
	
	public void crawler(ArrayList<String> urls, int limita)
	{
		int nrUrl = 0;
		//initializam URL Frontier, cu URL-urile initiale
		coada.addAll(urls);
		//conditiile de stop
		while((coada.isEmpty()!= true) && (nrUrl<limita)) {
			String urlCoada = coada.pop();
			URL url;
			try {
				url= new URL(urlCoada);
				String cale = url.getPath();
				String domeniu = url.getHost();
				int port = url.getPort();
				if(port == -1) //trecem pe portul 80 daca nu poate fi identificat portul
				{
					port = 80;
				}
				System.out.println(cale + "  " + domeniu + " " + port); // returnam cale, domeniue si port
				
			//daca protocolul nu e HTTP, ignoram URL-ul si il marcam ca vizitat, pentru a nu mai fi accesat
			if (!url.getProtocol().equals("http")) 
            {
                System.out.println("Nu e HTTP URL: " + urlCoada);
                vizitate.add(urlCoada);
                continue;
            }
			if(vizitate.isEmpty() ==true) //gasim doar primul link
			{
				if(vizitate.contains(urlCoada)) //daca url este vizitat il ignoram
				{
					continue;
				}
				else
				{
					if(!domeniuVizitate.contains(domeniu)) //daca gasim un domeniu neexplorat
					{
						String robot = http.cerereHttp("/robots.txt", domeniu, port); //luam fisierut robots de pe acest domeniu
						domeniuVizitate.add(domeniu);
						if(robot != null)  // si stocam regulile din fisier
						{
							String robotText = new String(robot);
							//Verificam daca avem voie sa exploram URL-ul curent
							if(!RobotParser.isAllowed(url, robotText))
							{
								System.out.print("Interzis pentru explorare\n");
								continue;
							}
						}
					}
					String save = http.cerereHttp(cale,domeniu,port);
					vizitate.add(urlCoada);
					if(save != null)
					{
						ParseFile pf = new ParseFile(save);
						String robots = pf.metaRobots(); //preia lista de robots
						if(robots.equals("")) // daca exista mentiuni cu privire la robotii ce acceseaza pagina curenta
						{
							//extragem si salvam continutul text din robots
							if(robots == "all" || robots =="index")
							{
								String text = pf.getText();
								BufferedWriter w = new BufferedWriter(new FileWriter(pf.getDoc().location()+".txt"));
								w.write(text);
								w.close();
							}
							//extragem din robots o lista noua de legaturi links
							if(robots == "all" || robots =="follow")
							{
								Set<String> links = pf.getLinks(urlCoada);
								coada.addAll(links);
							}
						}
						Set<String> links = pf.getLinks(urlCoada);
						coada.addAll(links);
						nrUrl++;
					}
					else {
						nrUrl++;
						continue;
					}
				}
			}
			//facem acelasi lucru pentru a gasi restul 99 de linkuri
			else
			{
				
				String robot = http.cerereHttp("/robots.txt", domeniu, port);
				domeniuVizitate.add(domeniu);
				System.out.println("ajung");
				if(robot != null)  
				{
					String robotText = new String(robot);
					if(!RobotParser.isAllowed(url, robotText))
					{
						System.out.print("Interzis pentru explorare\n");
						continue;
					}
				}
				String save = http.cerereHttp(cale,domeniu,port);
				vizitate.add(urlCoada);
				if(save != null)
				{
					ParseFile pf = new ParseFile(save);
					String robots = pf.metaRobots();
					if(robots.equals(""))
					{
						if(robots == "all" || robots =="index")
						{
							String text = pf.getText();
							BufferedWriter w = new BufferedWriter(new FileWriter(pf.getDoc().location()+".txt"));
							w.write(text);
							w.close();
						}
						if(robots == "all" || robots =="follow")
						{
							Set<String> links = pf.getLinks(urlCoada);
							coada.addAll(links);
							
						}
					}
					Set<String> links = pf.getLinks(urlCoada);
					coada.addAll(links);
					nrUrl++;
				}
				else {
					nrUrl++;
					continue;
				}
			}
			
			System.out.println(nrUrl);
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	

}