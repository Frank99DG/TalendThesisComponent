package com.uniba.talend.executorcontrols.processor.control;

import org.talend.sdk.component.api.record.Record;

import java.util.List;

public class SyntacticAccuracyISO25024 {

    private List<Record> recordList;
    private String column;

    public SyntacticAccuracyISO25024(String column, List<Record> recordList){

        this.column=column;
        this.recordList=recordList;
    }

    public void syntacticAccuracy(int recordCount,int recordCorrect, String field) {
        float accuracy=(float)recordCorrect/recordCount;
        System.out.println("Accuratezza sintattica del campo "+field+" calcolata secondo la ISO 25024: "+accuracy+"\n");
    }
}
