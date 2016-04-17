/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author minli
 */
package src.main.java;
import edu.jhu.nlp.wikipedia.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.Vector;
import java.util.HashSet;
public class WikiParser {
    long pagenum=0;
    long bigPageNum=0;
    BufferedWriter bw=null;
    FileOutputStream fos=null;
    BufferedWriter bw_cat=null;
    FileOutputStream fos_cat=null;
    WikiParser(){
        pagenum=0;
        
    }
    public void openfile(String outputFilePath){
        try{
        //File fout = new File(outputFilePath);
             fos = new FileOutputStream(new File(outputFilePath));
             bw = new BufferedWriter(new OutputStreamWriter(fos));
             fos_cat = new FileOutputStream(new File(outputFilePath+"_cat"));
             bw_cat = new BufferedWriter(new OutputStreamWriter(fos_cat));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void closefile(){
        try{
        bw.close();
        fos.close();
        bw_cat.close();
        fos_cat.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String args[]) {
        try {
            if(args.length<1){
                System.out.println("usage: inputfile output");
            }
            final WikiParser wikip=new WikiParser();
            String inputFilePath=args[0];
            String outputFilePath=args[1];
                    //"c:/temp/data.txt"
         //   File file = new File(inputFilePath);
          //  FileReader fr = new FileReader(file);
           // BufferedReader br = new BufferedReader(fr);
            
           // File fout = new File(outputFilePath);
            //FileOutputStream fos = new FileOutputStream(fout);
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            wikip.openfile(outputFilePath);
            
            
            
             
         //   while ((line = br.readLine()) != null) {     
            WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(inputFilePath);
            wxsp.setPageCallback(new PageCallbackHandler() { 
                       public void process(WikiPage page) {
                           wikip.pagenum++;
                              //System.out.println("####title: "+page.getTitle());
                         //     System.out.println(page.getTitle().replace("\r", " ").replace("\n", " ")+
                           //           " " +page.getText().replace("\r", " ").replace("\n", " ").replaceAll("\\{\\{.*\\}\\}",""));
                           String pageText=page.getText().replace("\r", " ").replace("\n", " ");
                           if(pageText.length()<100){return;}
                           wikip.bigPageNum++;
                           HashSet<String> cate=page.getCategories();
                           //String category=cate.size()!=0? cate.firstElement():"";
                          String category="";
                          for(String s: cate){ category+=":"+s;}
                           
                           String doc=category
                                   +" :::: "+page.getTitle().replace("\r", " ").replace("\n", " ")
                                   +   " " +pageText+"\n";
                           try{
                             if(!category.isEmpty()){wikip.bw_cat.write(category+"\n");}
                               wikip.bw.write(doc);
                             
                           }catch(Exception e){
                               wikip.closefile();
                                e.printStackTrace();
                           }
                         
                       }

            });

            wxsp.parse();
                 
         //   }
            System.out.println("page "+wikip.pagenum+" bigpage "+wikip.bigPageNum);
            
         //   br.close();
           // fr.close();
           wikip.closefile();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }    
}
