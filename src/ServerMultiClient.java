
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jdom2.Document;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.*;


public class ServerMultiClient
{
    static  String Write_Data_To_File;
    static String Directory_Name;
    static boolean debug_Messages = false;
    private final static Logger LOGGER = Logger.getLogger(ServerMultiClient.class.getName());
    public static void main(String args[]) throws IOException
    {

        //String Write_Data_To_File;
        Path path  = Paths.get(".").toAbsolutePath();
    //  System.out.println(path.normalize().toString());
       // String Directory_Name;
        //boolean debug_Messages = false;
        int port=6066;
        Directory_Name = path.normalize().toString();
   //     System.out.println(Directory_Name);
        for(int i=0;i<args.length;i++)
        {
            if(args[i].equals("-v"))
            {
                debug_Messages=true;
            }
            else if(args[i].equals("-p"))
            {
                port= Integer.parseInt(args[i+1]);
            }
            else if(args[i].equals("-d"))
            {
                Directory_Name=path.normalize().toString();
                Directory_Name+="\\"+args[i+1];
            }

        }
        //LOGGER.addHandler(new ConsoleHandler());
        LOGGER.setLevel(Level.INFO);
        System.out.println("Print Debug Messages "+debug_Messages);
        if(debug_Messages) {
            // System.out.println(Directory_Name);

            LOGGER.info(Directory_Name);
            LOGGER.info("ServerMultiClient listening at " + port);
        }
        //System.out.println("ServerMultiClient listening at "+port);
        //System.out.println("Print Debug Messages "+debug_Messages);
        ServerSocket socket = null;
        Socket s=null;
        try
        {
            socket = new ServerSocket(port);
            //socket.setSoTimeout(20000);
            //if(debug_Messages) {
               // System.out.println("waiting for client on port " + socket.getLocalPort());
              //  LOGGER.info("waiting for client on port " + socket.getLocalPort());
            }
      catch(IOException e)
      {
          e.printStackTrace();
      }
      
      while (true) {
            try {
                s = socket.accept();
            }
            catch(IOException e)
            {
                LOGGER.info("Error: "+e);
            }
            new MultipleClient(s).start();
      }


       // currentDirectory();
   }
   public static class MultipleClient extends  Thread{
        protected Socket socket;
        public MultipleClient(Socket socket)
        {
            this.socket = socket;
        }

       @Override
       public void run()
       {
           if(debug_Messages) {
               LOGGER.info("Just Connected to " + socket.getLocalPort());
           }
           //DataInputStream in = new DataInputStream(s.getInputStream());
           BufferedReader in2 = null;
           try {
               in2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           } catch (IOException e) {
               e.printStackTrace();
           }
           String temp = null;
           try {
               temp = in2.readLine();
           } catch (IOException e) {
               e.printStackTrace();
           }
           if(debug_Messages) {
               LOGGER.info(temp);
           }
           String Full_Response = temp;
           String[] Method_Path = Full_Response.split("\\s");
           String Method = Method_Path[0];
           if(debug_Messages) {
               LOGGER.info(Method_Path[1]);
           }
           String Path_Requested_File=Method_Path[1];
           //String Response_Code = "ErrorCode-: 404 File not found";
           DataOutputStream out = null;
           try {
               out = new DataOutputStream(socket.getOutputStream());
           } catch (IOException e) {
               e.printStackTrace();
           }
           if(!(Path_Requested_File.equals("/")))
           {
               File file = new File(Directory_Name+"\\"+Path_Requested_File);
               if(!(file.exists()))
               {
                   try {
                       out.writeBytes("Error\r\n");
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               else
               {
                   try {
                       out.writeBytes("HTTP/1.0 200 OK\r\n");
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
           else
           {
               try {
                   out.writeBytes("HTTP/1.0 200 OK\r\n");
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           //String request=in.readLine().trim();
           //System.out.println(temp);
           //while((temp=in2.readLine())!=null)
           //HashMap<String,String>hashMap =new HashMap<>();
           String Content_Type = "text/html";
           try {
               while(!(temp=in2.readLine()).equals(""))
               //while(!((temp=in2.readLine())==null))
               {
                   String[] Response_Content_Type = new String[2];
                   //System.out.println("-----"+temp);
                   Full_Response=temp;
                   Response_Content_Type=Full_Response.split("\\s");
                   //+= temp;
                   for(int i=0;i<2;i++)
                   {
                       //System.out.println(Response_Content_Type[i]);
                       if(Response_Content_Type[i].equals("Content-Type:"))
                       {
                           Content_Type = Response_Content_Type[i+1];
                       }
                   }
                   if(debug_Messages) {
                       System.out.println("Ful Response is here-: " + Full_Response);
                   }

               }
           } catch (IOException e) {
               e.printStackTrace();
           }
           StringBuilder payload = new StringBuilder();
           try {
               while(in2.ready())
               {
                   payload.append((char) in2.read());
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
           // System.out.println("Payload data is: "+payload.toString());
           Write_Data_To_File=payload.toString();
           if(debug_Messages) {
               LOGGER.info("Content-Type is here-: " + Content_Type);
           }
           // String[] ResponseArray = new String[Full_Response.length()];
           //System.out.println("Ful Response is here-: "+Full_Response);
           //str =in.readUTF();
           //System.out.println("Length is "+ str.length());
           //int length =str.length();
//            strArray = new String[str.length()];
           //strArray = new String[length];
           //System.out.println("Cleint Send this" + str);
           //strArray=str.split("\\s");
           //          System.out.println("Array length is " +strArray.length);
   /*         for(int i=0;i<strArray.length;i++)
            {
              if(strArray[i].equalsIgnoreCase("GET") || strArray[i].equalsIgnoreCase("post"))
               {
                method = strArray[i];
                strArray[i]=" ";
               }

            }
            for(int i=0;i<strArray.length;i++)
            {
              // System.out.println(strArray[i]);
              if(strArray[i].equalsIgnoreCase("Yes") || strArray[i].equalsIgnoreCase("no"))
               {
                debug_messages = strArray[i];
                strArray[i]=" ";
               //strArray[i]=
               }

            }
            for(int i=0;i<strArray.length;i++)
            {
                // System.out.println(strArray[i]);
                if(!strArray[i].equalsIgnoreCase(" "))
                {
                    File_Name= strArray[i];
                    strArray[i]=" ";
                    //strArray[i]=
                }

            }*/
           /* if(File_Name.equalsIgnoreCase("\\"))
            {
              File_Name="\\";
            }*/
           if(Path_Requested_File.equalsIgnoreCase("/"))
           {
               Path_Requested_File="/";
           }
           //System.out.println(File_Name);
           //System.out.println(Path_Requested_File);

           //********************getCurrentSystemDate_Time************************

           DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
           Date date = new Date();
           //System.out.println(format.format(date));

           //***************DateFinish********************************************



           //System.out.println(method +" "+debug_messages+" "+Directory_Name+"\\"+File_Name);

           //out.writeBytes(Http_Status+"\r\n");
           //out.writeBytes(Content_Disposition_Attachment_FileName+"\r\n");
           //out.writeBytes("\r\n");
           //out.writeBytes("\r\n");
           //  PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
           //out.writeBytes("HTTP/1.0 200 OK\r\n");
           //out.writeBytes("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
           try {
               out.writeBytes("Date:"+format.format(date)+"\r\n");
           } catch (IOException e) {
               e.printStackTrace();
           }
           try {
               out.writeBytes("ServerMultiClient: Navjot\r\n");
           } catch (IOException e) {
               e.printStackTrace();
           }

           //******************Content-Type****************************************************************

           // out.writeBytes("Content-Type: text/html\r\n");
           // out.writeBytes("Content-Type: application/x-download");
           if(Content_Type.equals("application/xml"))
           {
               try {
                   out.writeBytes("Content-Type: application/xml\r\n");
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           //out.writeBytes("Content-Type: text/x-json\\r\\n");
           else if(Content_Type.equals("application/json\r\n"))
           {
               try {
                   out.writeBytes("Content-Type: application/json");
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           else
           {
               try {
                   out.writeBytes("Content-Type: text/html\r\n");
               } catch (IOException e) {
                   e.printStackTrace();
               }
               //out.writeBytes("content-type: application/octet-stream\r\n");
           }
           //******************Content-Type****************************************************************

           //******************Content-Disposition****************************************************************

           //out.writeBytes("Content-Disposition: attachment; filename=Gagannav.txt\r\n");
           try {
               out.writeBytes("Content-Disposition: attachment\r\n");
           } catch (IOException e) {
               e.printStackTrace();
           }
           //out.writeBytes("Content-Disposition: inline\r\n");

           //******************Content-Disposition****************************************************************

           //******************JSON-Response****************************************************************
           JSONObject  Response = new JSONObject();
           JSONArray headers = new JSONArray();
           //headers.put("HTTP/1.0 200 OK\n");
           //  headers.put("Date:\"+format.format(date)+\"\\r\\n");
           //  headers.put("ServerMultiClient: Navjot/0.1\r\n");
           // headers.put("Expires: Sat, 01 Jan 2020 00:59:59 GMT\\r\\n");
           // headers.put("Last-modified:\"+format.format(date)+\"\\r\\n");
           // headers.put("Content-Type: text/x-json\r\n");
           //  headers.put("\r\n");
           // Response.put("Headers",headers);
           //out.writeBytes("Content-Length: 57\r\n");
           try {
               out.writeBytes("Expires: Sat, 01 Jan 2020 00:59:59 GMT\r\n");

               out.writeBytes("Last-modified:" + format.format(date) + "\r\n");
               out.writeBytes("\r\n");
           }
           catch (IOException e) {
           e.printStackTrace();
       }
           //Response.put("Status ","HTTP/1.0 200 OK");
           //Response.put("ServerMultiClient: ","Navjot/0.1");
           //Response.put("Expires: ","Sat, 01 Jan 2020 00:59:59 GMT");
           // out.write("<TITLE>Exemple</TITLE>");
           // out.write("<P>ServerMultiClient exemple.</P>");
           // out.write("\r\n");
           //Body part of http
           if(Method.equalsIgnoreCase("GET"))
           {
               if(debug_Messages) {
                   LOGGER.info("Path is here " + Path_Requested_File);
               }
               if (Path_Requested_File.equalsIgnoreCase("/"))
               {
                   // File f = new File("C:\\Users\\Navjot\\IdeaProjects\\Assignment2\\FilesALL"); // current directory
                   File f = new File(Directory_Name); // current directory
                   //File[] files = f.listFiles();
                   List<File> files_Nested_All = (List<File>)FileUtils.listFiles(f,TrueFileFilter.TRUE,TrueFileFilter.TRUE);
                   //System.out.println("hey");
                   /* try(Stream<Path> stream = Files.walk(path))
                    {
                        stream.filter(path1 -> path1.toFile().isDirectory()).forEach(System.out::println);
                    }
                    catch(IOException e)
                    {

                    }*/

                   //  Files.newDirectoryStream(Paths.get("."))
                   //         .forEach(System.out::println);
                   /* for (File file : files)
                    {
                      if (file.isDirectory())
                       {
                        list.add("Directory " + file.getName());

                       }
                      else
                       {
                        list.add("File " + file.getName());
                       }
                        System.out.println(file.getCanonicalPath());
                    }*/
                 /*   for (int i = 0; i < list.size(); i++)
                    {
                     out.writeBytes(list.get(i) + "\r\n");
                    }*/

                   //**************************JSON Response************************************************
                   if(Content_Type.equals("application/json"))
                   {
                       for (int i = 0; i <files_Nested_All.size(); i++)
                       {
                           headers.put(files_Nested_All.get(i));
                       }
                       try {
                           Response.put("Content",headers);
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
                   //**************************JSON Response************************************************


                   //**************************PlainText Response********************************************
                   if(Content_Type.equals("text/html"))
                   {
                       for (int i = 0; i <files_Nested_All.size(); i++)
                       {
                           //  out.writeBytes(files_Nested_All.get(i) + "\r\n");
                           // Response.put("Content"+i,files_Nested_All.get(i));
                          /* if(Content_Type.equals("application/json"))
                           {
                               headers.put(files_Nested_All.get(i));
                           }*/
                           try {
                               out.writeBytes(files_Nested_All.get(i) + "\r\n");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   }
                   //**************************PlainText Response********************************************


                   // **************************XML Response*************************************************
                   if(Content_Type.equals("application/xml"))
                   {
                       Element root = new Element("Response");
                       Document doc = new org.jdom2.Document();
                       Element child1=new Element("Content");
                       for (int i = 0; i <files_Nested_All.size(); i++)
                       {
                           child1.addContent(files_Nested_All.get(i).toString()+"\n");

                       }
                       root.addContent(child1);
                       doc.setRootElement(root);
                       XMLOutputter outter=new XMLOutputter();
                       outter.setFormat(Format.getPrettyFormat());
                       try {
                           outter.output(doc, new FileWriter(new File("myxml1.xml")));
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       File file = new File("myxml1.xml");
                       //FileReader fileReader = new FileReader(file);
                       BufferedReader bufferedReader = null;
                       try {
                           bufferedReader = new BufferedReader(new FileReader(file));
                       } catch (FileNotFoundException e) {
                           e.printStackTrace();
                       }
                       String line;
                       try {
                           while((line=bufferedReader.readLine())!=null)
                           {
                               out.writeBytes(line+"\n");
                           }
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   // **************************XML Response*************************************************

                   // Response.put("Content",headers);
               }
               else
               {
                   if(debug_Messages) {
                       LOGGER.info("hey in else");
                   }
                   File f1 = new File(Directory_Name + "\\" + Path_Requested_File);
                   //System.out.println(Directory_Name + Path_Requested_File);
                   if (f1.isDirectory())
                   {
                        /*  try(Stream<Path> stream = Files.walk(path))
                          {
                              stream.filter((Path path1) -> path1.toFile().isFile());
                              stream.forEach(System.out::println);
                          }
                          catch(IOException e)
                          {

                          }*/
                       //File[] filess = f1.listFiles();
                       List<File> files_Nested_All = (List<File>)FileUtils.listFiles(f1,TrueFileFilter.TRUE,TrueFileFilter.TRUE);
                      /*  for (File file : filess)
                         {
                            if (file.isDirectory())
                            {
                              list.add("Directory " + file.getName());
                            }
                            else
                            {
                             list.add("File " + file.getName());
                            }
                            System.out.println(file.getCanonicalPath());
                        }
                        for (int i = 0; i < list.size(); i++)
                        {
                            out.writeBytes(list.get(i) + "\r\n");
                        }*/
                       //**************************JSON Response************************************************
                       if(Content_Type.equals("application/json"))
                       {
                           for (int i = 0; i <files_Nested_All.size(); i++)
                           {
                               headers.put(files_Nested_All.get(i));
                           }
                           try {
                               Response.put("Content",headers);
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                       //**************************JSON Response************************************************


                       //**************************PlainText Response********************************************
                       if(Content_Type.equals("text/html"))
                       {
                           for (int i = 0; i <files_Nested_All.size(); i++)
                           {
                               //  out.writeBytes(files_Nested_All.get(i) + "\r\n");
                               // Response.put("Content"+i,files_Nested_All.get(i));
                          /* if(Content_Type.equals("application/json"))
                           {
                               headers.put(files_Nested_All.get(i));
                           }*/
                               try {
                                   out.writeBytes(files_Nested_All.get(i) + "\r\n");
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           }
                       }
                       //**************************PlainText Response********************************************



                       // **************************XML Response*************************************************
                       if(Content_Type.equals("application/xml"))
                       {
                           Element root = new Element("Response");
                           Document doc = new org.jdom2.Document();
                           Element child1=new Element("Content");
                           for (int i = 0; i <files_Nested_All.size(); i++)
                           {
                               child1.addContent(files_Nested_All.get(i).toString());

                           }
                           root.addContent(child1);
                           doc.setRootElement(root);
                           XMLOutputter outter=new XMLOutputter();
                           outter.setFormat(Format.getPrettyFormat());
                           try {
                               outter.output(doc, new FileWriter(new File("myxml1.xml")));
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                           File file = new File("myxml1.xml");
                           //FileReader fileReader = new FileReader(file);
                           BufferedReader bufferedReader = null;
                           try {
                               bufferedReader = new BufferedReader(new FileReader(file));
                           } catch (FileNotFoundException e) {
                               e.printStackTrace();
                           }
                           String line;
                           try {
                               while((line=bufferedReader.readLine())!=null)
                               {
                                   out.writeBytes(line+"\n");
                               }
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                       // **************************XML Response*************************************************



                        /*  for (int i = 0; i <files_Nested_All.size(); i++)
                          {
                             // out.writeBytes(files_Nested_All.get(i) + "\r\n");
                              headers.put(files_Nested_All.get(i));
                              System.out.println(files_Nested_All.get(i));
                          }
                          Response.put("Content",headers);*/
                   }
                   else if (f1.isFile())
                   {
                       String line = null;
                       //String ext = FilenameUtils.getExtension(f1.toString());
                       //System.out.println("Type is " + ext);
                       //  System.out.println("Pending");
                       try
                       {
                           FileReader fileReader = new FileReader(f1);
                           BufferedReader bufferedReader = new BufferedReader(fileReader);

                           //**************************JSON Response************************************************
                           if(Content_Type.equals("application/json"))
                           {
                               while ((line = bufferedReader.readLine()) != null)
                               {
                                   // out.writeBytes(line+"\r\n");
                                   headers.put(line);
                               }
                               Response.put("Content",headers);
                           }
                           //**************************JSON Response************************************************


                           //**************************PlainText Response********************************************
                           if(Content_Type.equals("text/html"))
                           {
                               while ((line = bufferedReader.readLine()) != null)
                               {
                                   out.writeBytes(line+"\r\n");
                                   //headers.put(line);
                               }
                               //Response.put("Content",headers);
                               bufferedReader.close();
                           }
                           //**************************PlainText Response********************************************


                           // **************************XML Response*************************************************
                           if(Content_Type.equals("application/xml"))
                           {
                               Element root = new Element("Response");
                               Document doc = new org.jdom2.Document();
                               Element child1=new Element("Content");
                               while ((line = bufferedReader.readLine()) != null)
                               {
                                   // out.writeBytes(line+"\r\n");
                                   //headers.put(line);
                                   child1.addContent(line+"\r\n");
                               }
                               //Response.put("Content",headers);
                               bufferedReader.close();
                               root.addContent(child1);
                               doc.setRootElement(root);
                               XMLOutputter outter=new XMLOutputter();
                               outter.setFormat(Format.getPrettyFormat());
                               outter.output(doc, new FileWriter(new File("myxml1.xml")));
                               File file = new File("myxml1.xml");
                               //FileReader fileReader = new FileReader(file);
                               BufferedReader bufferedReader1 = new BufferedReader(new FileReader(file));
                               String line1;
                               while((line1=bufferedReader1.readLine())!=null)
                               {
                                   out.writeBytes(line1+"\n");
                               }
                           }
                           // **************************XML Response*************************************************

                           /* while ((line = bufferedReader.readLine()) != null)
                            {
                               // out.writeBytes(line+"\r\n");
                                headers.put(line);
                            }
                            Response.put("Content",headers);*/
                           bufferedReader.close();
                       }
                       catch (FileNotFoundException e)
                       {
                           try {
                               out.writeBytes("ErrorCode 404 -: File Not Found\r\n");
                           } catch (IOException e1) {
                               e1.printStackTrace();
                           }
                           //headers.put("File Not Found Error 404");
                       }
                       catch (Exception e)
                       {
                           e.printStackTrace();
                       }
                   }
                   else
                   {
                       try {
                           out.writeBytes("ErrorCode 404 -: File Not Found\r\n");
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       //headers.put("File Not Found Error 404");

                   }
                   //System.out.println(File_Name);
                   //System.out.println("Pending\n");
               }
           }
           //Post*********************************************
           if((Method.equalsIgnoreCase("POST")))
           {
               File f1 = new File(Directory_Name+"\\"+Path_Requested_File);
               System.out.println(Directory_Name+Path_Requested_File);
               if(f1.exists())
               {
                   f1.setWritable(true);
                   FileWriter fileWriter = null;
                   try {
                       fileWriter = new FileWriter(f1,true);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   try {
                       fileWriter.append(Write_Data_To_File);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   try {
                       fileWriter.flush();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   try {
                       out.writeBytes("File is Already there.We append your contents\r\n");
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               else
               {
                   try {
                       f1.createNewFile();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   FileWriter fileWriter = null;
                   try {
                       fileWriter = new FileWriter(f1,true);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   try {
                       fileWriter.write(Write_Data_To_File);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   try {
                       out.writeBytes("we created file for you\r\n");
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
           // out.writeBytes("Thank you for connecting to " + s.getLocalSocketAddress() + "\nGoodbye!\r\n");
           // Response.put("Terminate","Thank you for connecting to " + s.getLocalSocketAddress() + "Goodbye!");
           //out.writeUTF(Response.toString());

           //**************************JSON Response************************************************
           if(Content_Type.equals("application/json"))
           {
               Gson gson = new GsonBuilder().setPrettyPrinting().create();
               String response = gson.toJson(Response);
               // out.writeChars(response);
               try {
                   out.writeBytes(response);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           //**************************JSON Response************************************************

           //**************************PlainText Response********************************************
           if(Content_Type.equals("text/html"))
           {
               try {
                   out.writeBytes("Thank you for connecting to " + socket.getLocalSocketAddress() + "\nGoodbye!\r\n");
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           //**************************PlainText Response********************************************
           try {
               in2.close();
               out.close();
               socket.close();
           }

            catch (IOException e) {
           e.printStackTrace();
       }
           //super.run();
       }
   }

}

