package com.uniba.talend.executorcontrols.processor.control;

import com.uniba.talend.executorcontrols.processor.ExecutorControlsProcessor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import java.util.List;

public class ControlInt {

    private List<Record> recordList;
    private String column;

    private boolean duplicated=false;

    private List<Schema.Entry> schemaList;
    private int correct=0, recordNull=0;

    public ControlInt(String column, List<Record> recordList, List<Schema.Entry> schemaList){

        this.column=column;
        this.recordList=recordList;
        this.schemaList=schemaList;
    }

    public void verifyInt(){

            for(int j=0;j<schemaList.size();j++){

                if(column.matches(schemaList.get(j).getName())) {
                    Schema.Type type = schemaList.get(j).getType();
                    if(type.toString().matches("int") || type.toString().matches("INT")  ){

                        verifyDuplicate();

                    }else{
                        System.out.println("il campo "+column+" non contiene vaori interi validi"+"\n");
                    }
                }
            }

    }



    public void syntacticAccuracy(int recordCount,int recordCorrect, String field) {
        float accuracy=(float)recordCorrect/recordCount;
        System.out.println("Accuratezza sintattica del campo "+field+" calcolata secondo la ISO 25024: "+accuracy+"\n");
    }

    public void attributeCompleteness(int recordCount,int recordNull, String field) {

        float completeness=(float)recordNull/recordCount;

        System.out.println("Completezza di attributo del campo "+field+" calcolata secondo la ISO 25024: "+completeness+"\n");
    }

    /**
     * {Category} User Defined
     *
     */
    public void recordCompleteness(int numberOfComumn) {
        recordNull=0;
        for(int i=0;i<recordList.size();i++){

            for(int j=0;j<schemaList.size();j++){
                Schema.Type type = schemaList.get(j).getType();
                if(type.toString().matches("STRING") || type.toString().matches("String")  ){

                    String p = "ciao";
                    p = recordList.get(i).getString(schemaList.get(j).getName());
                    p=p+"";
                    if (p.contains("null")) {
                        recordNull++;
                    }
                    /*if (recordList.get(i).getString(schemaList.get(j).getName()).matches("null")) {
                        recordNull++;

                    }*/
                }else{
                    if(type.name().matches("int")){
                        if(recordList.get(i).getInt(schemaList.get(j).getName())==0){
                            recordNull++;
                        }

                    }
                }
            }
            float completeness=(float)(numberOfComumn-recordNull)/numberOfComumn;
            System.out.println("Completezza del record "+i+" calcolata secondo la ISO 25024: "+completeness+"\n");
            recordNull=0;
        }

    }


    public void verifyDuplicate(){
        System.out.println("**** Controllo duplicati "+"nella colonna "+column+" **** \n");
        for(int i=0;i<recordList.size();i++){
            for(int j=1;j<recordList.size();j++){
                if(recordList.get(i).getInt(column)==recordList.get(j).getInt(column) && i!=j){
                    System.out.println("Valori interi duplicati: "+recordList.get(i).getInt(column)+" al record: "+i+"\n");
                    duplicated=true;
                }
            }
            if(!duplicated){
                correct++;
            }else{
                duplicated=false;
            }
        }
        System.out.println("/**** INFO COMPLETEZZA E ACCURATEZZA ISO 25024 ****/ "+"\n");
        syntacticAccuracy(recordList.size(), correct, column);
        attributeCompleteness(recordList.size(),recordNull,column);
        if(!ExecutorControlsProcessor.getCalled()) {
            recordCompleteness(schemaList.size());
            ExecutorControlsProcessor.setCalled(true);
        }
    }
}
