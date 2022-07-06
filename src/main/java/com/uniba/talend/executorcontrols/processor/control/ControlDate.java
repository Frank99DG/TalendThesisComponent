package com.uniba.talend.executorcontrols.processor.control;


import com.uniba.talend.executorcontrols.processor.ExecutorControlsProcessor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ControlDate {

    final static String DATE_FORMAT = "dd/MM/yyyy";

    private List<Record>recordList;
    private String column;

    private List<Schema.Entry> schemaList;
    private int correct=0, recordNull=0;

    public ControlDate(String column, List<Record> recordList, List<Schema.Entry> schemaList){

        this.column=column;
        this.recordList=recordList;
        this.schemaList=schemaList;
    }

    public static boolean isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
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


    public void resultControl() {
        boolean res;

        Schema.Type type = schemaList.get(0).getType();
        for (int i = 0; i < schemaList.size(); i++) {
            if (schemaList.get(i).getName().matches(column)) {
                type = schemaList.get(i).getType();
            }
        }
        if (type.toString().matches("STRING") || type.toString().matches("String")) {
            System.out.println("**** Date errate " + "nella colonna " + column + " **** \n");
            for (int i = 0; i < recordList.size(); i++) {
                String p = "ciao";
                p = recordList.get(i).getString(column);
                p = p + "";
                if (p.contains("null")) {
                    System.out.println("null" + " al record" + i + "\n");
                    recordNull++;
                } else {
                    res = isDateValid(p);
                    if (!res) {
                        System.out.println(recordList.get(i).getString(column) + " al record" + i + "\n");
                    }else{
                        correct++;
                    }
                }
            }
            System.out.println("/**** INFO COMPLETEZZA E ACCURATEZZA ISO 25024 ****/ "+"\n");
            syntacticAccuracy(recordList.size(), correct, column);
            attributeCompleteness(recordList.size(),recordNull,column);
            if(!ExecutorControlsProcessor.getCalled()) {
                recordCompleteness(schemaList.size());
                ExecutorControlsProcessor.setCalled(true);
            }
        }else{

            System.out.println("il campo "+column+"non contiene date valide"+"\n");
        }
    }
}
