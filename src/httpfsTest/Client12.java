package httpfsTest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class Client12
{
    public static String str,method;
    // public static String method= args[0];
    public static  void main(String args[])
    {
        String str,Directory_Name="\\";
        String debug_messages= "false";
        String serverName="localhost";
        int port=6066;
       for(int i=0;i< args.length;i++)
       {
            if(args[i].equals("GET"))
            {
                method = "GET";
            }
           if(args[i].equals("POST"))
           {
               method = "POST";
           }
            if (args[i].equalsIgnoreCase("-v"))
            {
                debug_messages = "yes";
                System.out.println("Server will print you errors "+ debug_messages);
            }
            if (args[i].equals("-d")) {
                Directory_Name = args[i + 1];
                System.out.println(Directory_Name);
            }

        }
        try
        {
            // Socket s = new Socket("http://httpbin.org/get?course=networking&assignment=1",80);
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket("localhost",6066);
           // Socket client = new Socket("localhost",6066);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
           // out.writeUTF("hello from "+ client.getLocalSocketAddress()+"\n"+method+"\n"+debug_messages+"\n"+Directory_Name);
            out.writeUTF(method+" "+debug_messages+" "+Directory_Name);
           // out.writeUTF(method + " "+ debug_messages + " "+Directory_Name);
           // out.writeUTF(Directory_Name);
            //out.writeUTF(debug_messages);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

                while ((str=in.readLine())!= null)
                {
                  //  str = in.readUTF();
                    System.out.print(str + "\n");
                }

                    in.close();
            client.close();
            // PrintWriter writer = new PrintWriter(client.getOutputStream());
            // writer.println("GET /get?course=networking&assignment=1 HTTP/1.1");
            //  writer.println("Host: httpbin.org");
            // writer.println("");
            // writer.flush();
            // BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            // while((str=in.readLine())!=null)
            // {
            // str = in.readUTF().toString();
            //      System.out.println("" + str);
            //  }
            //s.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
