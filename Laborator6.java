import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class RequestHTTP {
	static int incercari = 0;
	public String cerereHttp(String cale, String domeniu, int port) throws UnknownHostException, IOException {
		String cerereHTTP = "GET /"+cale+ " HTTP/1.1\r\nHost: "+domeniu+"\r\nUser-Agent: RIWEB_CRAWLER\r\nConnection: close\r\n\r\n";
		InetAddress intAddress = InetAddress.getByName(domeniu);
		String ip = intAddress.getHostAddress();
		Socket client = new Socket(ip,port);
		DataOutputStream request = new DataOutputStream(client.getOutputStream()); // buffer de iesire (pt cerere)
		BufferedReader response = new BufferedReader(new InputStreamReader(client.getInputStream())); // buffer de intrare (pt raspuns)
		request.writeBytes(cerereHTTP); //trimite cererea
		String responseLine = response.readLine(); //
		System.out.println(responseLine);
		
		boolean is200 = false;
		boolean is301 = false;
		boolean is302 = false;
		
		// prima linie este linia de stare, ce contine codul de raspuns
		if(responseLine.contains("200 OK")) 
		{
			is200 = true;
		}
		else if(responseLine.contains("301")) //Mutat permanent
		{
			is301 = true;
		}
		else if(responseLine.contains("302")) //Gasit
		{
			is302 = true;
		}
		else
		{
			client.close();
			return null;
		}
		// preia o resursa web folosind protocolul HTTP 1.1
		String newLocation = ""; // noua locatie in caz de raspuns 301
		while ((responseLine = response.readLine()) != null)
        {
			StringBuilder ss = new StringBuilder();
			ss.append(responseLine);
            if (responseLine.equals(""))
            	break;
            if (responseLine.startsWith("Location:")) //avem raspuns 301 sau 302
            {
                newLocation = responseLine.replace("Location: ", "");
            }
            ss.toString();
        }
		
		//salvam continutul paginii
		String htmlPath;
		if(is200){   //raspuns 200 ok
			StringBuilder sb = new StringBuilder();  // se construieste pagina trimisa de server
			while((responseLine = response.readLine()) != null){
				sb.append(responseLine + "\n");
			}
			
			//contruim calea de salvare a resursei
			htmlPath =  domeniu + "/" +cale;
			if(!(htmlPath.endsWith(".html") || htmlPath.endsWith(".htm")) && !htmlPath.equals("/robots.txt")){
				if(!htmlPath.endsWith("/")){
					htmlPath += "/";
				}
				htmlPath += "index.html";
			}
			File file = new File(htmlPath);
			//System.out.println(htmlPath);
            File dir = file.getParentFile();
            //System.out.println(dir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            
            // salvam resursa
            BufferedWriter writer = new BufferedWriter(new FileWriter(htmlPath));
            
            writer.write(sb.toString());
            writer.close();
            client.close();
            return sb.toString();
		}
		
		//se reface cererea pentru noua locatie si se actualizeaza datele deja salvate
		else if(is301)
		{
			if(incercari < 3)
			{
				System.out.println(incercari);
				incercari++;
				client.close();
				URL urlNou = new URL(newLocation);
				String caleNoua = urlNou.getPath();
				String domeniuNou = urlNou.getHost();
				int portNou = urlNou.getPort();
				if(portNou == -1)
				{
					portNou = 80;
				}
				return cerereHttp(caleNoua,domeniuNou,portNou);
			}
			else
			{
				StringBuilder s = new StringBuilder();
				while((responseLine = response.readLine()) != null){
					s.append(responseLine + "\n");
				}
				BufferedWriter w = new BufferedWriter(new FileWriter("codEroare.txt"));
				w.write(s.toString());
				w.close();
				client.close();
				System.out.println("Cod 301. Numar de incercari epuizate.\n");
				return null;
			}
		}
		else if(is302)
		{
			if(incercari < 3)
			{
				incercari++;
				client.close();
				URL urlNou = new URL(newLocation);
				System.out.println(newLocation);
				String caleNoua = urlNou.getPath();
				String domeniuNou = urlNou.getHost();
				int portNou = urlNou.getPort();
				if(portNou == -1)
				{
					portNou = 80;
				}
				return cerereHttp(caleNoua,domeniuNou,portNou);
			}
			else
			{
				StringBuilder s = new StringBuilder();
				while((responseLine = response.readLine()) != null){
					s.append(responseLine + "\n");
				}
				BufferedWriter w = new BufferedWriter(new FileWriter("codEroare.txt"));
				w.write(s.toString());
				w.close();
				client.close();
				System.out.println("Cod 302. Numar de incercari epuizate.\n");
				return null;
			}
			
		}
		//Daca nu primeste 200, 301 sau 302 se incarca eroarea in fisierul text codEroare.txt
		else
		{
			StringBuilder s = new StringBuilder();
			while((responseLine = response.readLine()) != null){
				s.append(responseLine + "\n");
			}
			BufferedWriter w = new BufferedWriter(new FileWriter("codEroare.txt"));
			w.write(s.toString());
			w.close();
			client.close();
			System.out.println("Cod 4xx\n");
			return null;
		}
	}
}
