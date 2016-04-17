package src.main.java;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author minli
 * convert amazon movie review to uid::productID::score::time_stamp
 */
public class MovieReviewConverter {

    public static void main(String args[]) {
        try {
            if(args.length<1){
                System.out.println("usage: inputfile outputfile");
            }
            String inputFilePath=args[0];
            String outputFilePath=args[1];
                    //"c:/temp/data.txt"
            File file = new File(inputFilePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            
            File fout = new File(outputFilePath);
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        
            String line;
            int i=0;
            String[] movie=new String[9];
            int lineNum=0;
            while ((line = br.readLine()) != null) {                                
                lineNum++;
                line=line.trim();
                //using empty line to separate the records
                 if(!line.isEmpty()){                    
                    String[] val=line.trim().split("\\s*:\\s*",2);
                    if(val.length!=2||val[1]==null){ 
                        System.out.println(line+" error line:"+Integer.toString(lineNum));
                        continue;
                    }
                    movie[i++]=val[1];
                }else{
                    if(i==8){
                        //user id, product id; score; time;
                        String str=String.valueOf(movie[0].hashCode())+"::"
                                +String.valueOf(movie[1].hashCode())
                                +"::"+movie[4]
                                +"::"+movie[5];
                        
                        bw.write(str);
                        bw.newLine();  
                        //bw.flush();
                    }
                    i=0;
                 }
            }
            br.close();
            fr.close();
            bw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
