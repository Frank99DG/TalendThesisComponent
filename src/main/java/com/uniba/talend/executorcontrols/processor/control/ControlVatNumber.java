package com.uniba.talend.executorcontrols.processor.control;

import com.uniba.talend.executorcontrols.processor.ExecutorControlsProcessor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import java.util.List;

public class ControlVatNumber {

    private List<Record>recordList;
    private String column;

    private List<Schema.Entry> schemaList;
    private int correct=0, recordNull=0;

    public ControlVatNumber(String column, List<Record> recordList, List<Schema.Entry> schemaList){

        this.column=column;
        this.recordList=recordList;
        this.schemaList=schemaList;
    }

    /**
     * Verifies the basic syntax, length and control code of the given PI.
     * {Category} User Defined
     * @param pi Raw PI, possibly with spaces.
     * @return Null if valid, or string describing why this PI must be
     * rejected.
     */
    public static String validate(String pi)
    {

        if( pi.length() == 0 )
            return "Empty.";
        else if( pi.length() != 11 )
            return "Invalid length.";
        if( ! pi.matches("^[0-9]{11}$") )
            return "Invalid characters.";
        int s = 0;
        for(int i = 0; i < 11; i++){
            int n = pi.charAt(i) - '0';
            if( (i & 1) == 1 ){
                n *= 2;
                if( n > 9 )
                    n -= 9;
            }
            s += n;
        }
        if( s % 10 != 0 )
            return "Invalid checksum.";
        return null;
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
        String res;

        Schema.Type type = schemaList.get(0).getType();
        for (int i = 0; i < schemaList.size(); i++) {
            if (schemaList.get(i).getName().matches(column)) {
                type = schemaList.get(i).getType();
            }
        }

        if (type.toString().matches("STRING") || type.toString().matches("String")) {
            System.out.println("**** Partite Iva errate " + "nella colonna " + column + " **** \n");
            for (int i = 0; i < recordList.size(); i++) {
                String p = "ciao";
                p = recordList.get(i).getString(column);
                p = p + "";
                if (p.contains("null")) {
                    System.out.println("null" + " al record" + i + "\n");
                    recordNull++;
                } else {
                    res = validate(p);
                    if (res != null) {
                        System.out.println(recordList.get(i).getString(column) + " al record" + i + "\n");
                    }else{
                        correct++;
                    }
                }
            }
            System.out.println("/**** INFO COMPLETEZZA E ACCURATEZZA ISO 25024 ****/ "+"\n");

                syntacticAccuracy(recordList.size(), correct, column);
                attributeCompleteness(recordList.size(), recordNull, column);
            if(!ExecutorControlsProcessor.getCalled()) {
                recordCompleteness(schemaList.size());
                ExecutorControlsProcessor.setCalled(true);
            }
        }else{
            System.out.println("il campo "+column+"non contiene partite iva valide "+"\n");
        }
    }
}
