package src.main.java;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author minli
 */
public class GetSparkMetrics {
   

    public static void main(String args[]) {
        try {
            if(args.length<1){
                System.out.println("usage: inputfile ");
            }
            String inputFilePath=args[0];
           // String outputFilePath=args[1];
                    //"c:/temp/data.txt"
            File file = new File(inputFilePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            
          //  File fout = new File(outputFilePath);
          //  FileOutputStream fos = new FileOutputStream(fout);
          //  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        /*
            Disk: Hadoop:Memory: Network: 
            */
            String line;
          
            Task task=new Task();
            Stage stage=new Stage();
            App app=new App();
            JSONParser parser=new JSONParser();
            while ((line = br.readLine()) != null) {                                
                JSONObject obj=(JSONObject)parser.parse(line);       
                String event=(String)obj.get("Event");
                
                /*task metrics*/
                if(event.contains("TaskEnd")){
                    task.parseJSON(obj);
                }else if(event.contains("StageCompleted")){
                    stage.parseJSON(obj);
                }else if(event.contains("ApplicationStart")){
                    app.parseJSON(obj, Boolean.TRUE);
                }else if(event.contains("ApplicationEnd")){
                    app.parseJSON(obj, Boolean.FALSE);}
            }

            task.printVal();
            stage.printVal();
            app.printVal();
            br.close();
            fr.close();
            //bw.close();
           // fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

 
}
class App{
    
    //long stime=0;
    //long ftime=0;
    
    
    long st=0;
    long ft=0;
    public void printVal(){
        System.out.println("app totTime "+(ft-st)/1000/60 +" min ");
    }
    public void parseJSON(JSONObject obj,Boolean isStart){                               
           Long val = (Long) obj.get("Timestamp");           
           if(isStart){ st=val;}else{ft=val;}                      
    }
}
class Stage{
    
    //long stime=0;
    //long ftime=0;
    long stage_evt=0;
    long tot_time=0;
    public void printVal(){
        System.out.println("stage_evt "+this.stage_evt
        +" totTime "+tot_time/1000 +" : "
                +tot_time/1000/60 +" sec:min ");
    }
    public void parseJSON(JSONObject obj){
           this.stage_evt++;
           JSONObject info = (JSONObject) obj.get("Stage Info");
        if (info == null) {
            return;
        }
           Long id=(Long) info.get("Stage ID");
           Long st = (Long) info.get("Submission Time");
           Long ft = (Long) info.get("Completion Time");
           tot_time+=(ft-st);
           System.out.println("stage id:exe "+id+" : " +(ft-st)/1000);//second
           
    }
}
class Task{
    long stime=0;
    long ftime=0;
    //input metric, read method : Memory, Disk, Hadoop, Network
    long inputbytes = 0;//from hadoop
    long inputbytes_mem=0;
    long inputbytes_disk=0;
    long inputbytes_hadoop=0;
    long inputbytes_network=0;
    long shuffle_read = 0;
    long shuffle_write = 0;
    long taskevt = 0;
    long taskevt_nonempty = 0;
    long shuffle_task_num=0;
    long shuffle_task_time=0;
    long result_task_num=0;
    long result_task_time=0;
    long result_size=0;
    public void parseJSON(JSONObject obj) {
        taskevt++;
        String tasktype=(String) obj.get("Task Type");
        JSONObject info = (JSONObject) obj.get("Task Info");
        if (info != null) {
            Long id = (Long) info.get("Stage ID");
            Long st = (Long) info.get("Launch Time");
            Long ft = (Long) info.get("Finish Time");
            //System.out.printf("tasktime %d\n",ft-st);
            if(ft-st>0) this.taskevt_nonempty++;
          // System.out.println("stage id:exe "+id+" : " +(ft-st));
            if(tasktype.equals("ResultTask")){
                    this.result_task_time+=(ft-st);
                    this.result_task_num++;
            }else if(tasktype.equals("ShuffleMapTask")){
                this.shuffle_task_time+=(ft-st);
                this.shuffle_task_num++;
            }
        }
 
           
           
        JSONObject taskmetrics = (JSONObject) obj.get("Task Metrics");
        if (taskmetrics == null) {
            return;
        }

        JSONObject inputmetric = (JSONObject) taskmetrics.get("Input Metrics");
        if (inputmetric != null) {
            String method = (String) inputmetric.get("Data Read Method");
            if (method != null ) {
                Long val = (Long) inputmetric.get("Bytes Read");
                this.inputbytes+=val;
                if(method.equals("Hadoop")){                    
                    inputbytes_hadoop += val;
                }else if(method.equals("Disk")){
                    this.inputbytes_disk+=val;
                }else if(method.equals("Memory")){
                    this.inputbytes_mem+=val;
                }else if(method.equals("Network")){
                    this.inputbytes_network+=val;
                }
            }
        }
        JSONObject shuffleReadmetric = (JSONObject) taskmetrics.get("Shuffle Read Metrics");
        if (shuffleReadmetric != null) {
            Long val = (Long) shuffleReadmetric.get("Remote Bytes Read");
            shuffle_read += val;
        }
        JSONObject shuffleWritemetric = (JSONObject) taskmetrics.get("Shuffle Write Metrics");
        if (shuffleWritemetric != null) {
            Long val = (Long) shuffleWritemetric.get("Shuffle Bytes Written");
            shuffle_write += val;
        }
    }
    public void Task(){
        inputbytes=0;            
        shuffle_read=0;
        shuffle_write=0;            
        taskevt=0;
    }
    public void printVal(){
        double tot_task_t=this.shuffle_task_time+this.result_task_time;
        double stask_percent=(double)this.shuffle_task_time/tot_task_t;
        double rtask_percent=(double)this.result_task_time/tot_task_t;
        double tot_task_n=this.shuffle_task_num+this.result_task_num;
        double staskn_percent=(double)this.shuffle_task_num/tot_task_n;
        double rtaskn_percent=(double)this.result_task_num/tot_task_n;
        /*System.out.print("inputbyptes "+(inputbytes>>20)+" M:"+(inputbytes>>30)
                +"g, hadoop "+(inputbytes_hadoop>>20)+" M:"+(inputbytes_hadoop>>30)
                +"g, memory "+(inputbytes_mem>>20)+" M:"+(inputbytes_mem>>30)
                +"g, disk "+(inputbytes_disk>>20)+" M:"+(inputbytes_disk>>30)
                +" G,taskNum "+taskevt
                +" G,taskNoEmptyNum "+this.taskevt_nonempty
            +" shuffle read "+(shuffle_read>>20)+" M; "
            +" "+(shuffle_read>>30)+" G; "
             +" write "+(shuffle_write>>20)+" M; "
            +(shuffle_write>>30)+" G;"+"\n");*/
         System.out.printf("inputbytes_tot %d M: %.2f G, hadoop %d M: %.2f G,"
                 +"mem %d M: %.2f G,disk %d M: %.2f G,network %d M: %.2f G,"
                +" taskNum %d ,taskNoEmptyNum %d ;"
                 + "shuffle read %d M: %.2f G; write %d M; %.2f G;",
                 (this.inputbytes>>20),(double)(this.inputbytes>>20)/1024,
                 this.inputbytes_hadoop>>20,(double)(this.inputbytes_hadoop>>20)/1024,
                 this.inputbytes_mem>>20,(double)(this.inputbytes_mem>>20)/1024,
                 this.inputbytes_disk>>20,(double)(this.inputbytes_disk>>20)/1024,
                 this.inputbytes_network>>20,(double)(this.inputbytes_network>>20)/1024,
                 this.taskevt,this.taskevt_nonempty,
                 this.shuffle_read>>20,(double)(this.shuffle_read>>20)/1024,
                 this.shuffle_write>>20,(double)(this.shuffle_write>>20)/1024);
        System.out.printf(" time:shuffle:result %.2f : %.2f min; %.2f : %.2f",
                ((double)this.shuffle_task_time/1000/60),
                ((double)this.result_task_time/1000/60),
                stask_percent,rtask_percent);
        System.out.printf(" num:shuffle:result %d : %d ; %.2f : %.2f",
                (this.shuffle_task_num),
                (this.result_task_num),
                staskn_percent,rtaskn_percent);
        System.out.println();
    }
}
/*
            while ((line = br.readLine()) != null) {                                
                
                //String[] val=line.trim().split("\\s*:\\s*",2);
                //using empty line to separate the records
                if(line.isEmpty()||!line.contains("SparkListenerTask")) continue;
                taskevt++;
                line=line.replace('{', ' ');
                line=line.replace('}', ' ');
                line=line.replace('"', ' ');
                line=line.trim();
                 
                String[] vals=line.split(",");
                for(int i=0;i<vals.length;i++){
                    if(vals[i].contains("Data Read Method")){
                        read=(vals[i].split(":"))[2].trim();
                    }else if(vals[i].contains("Bytes Read")){
                        if(read!=null&&read.equals("Hadoop")){
                            inputbytes+=Integer.parseInt((vals[i].split(":"))[1].trim());
                        }
                    }
                }
                read=null;

            }
*/