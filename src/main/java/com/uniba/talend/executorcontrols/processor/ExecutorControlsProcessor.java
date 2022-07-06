package com.uniba.talend.executorcontrols.processor;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;

import com.uniba.talend.executorcontrols.processor.control.ControlDate;
import com.uniba.talend.executorcontrols.processor.control.ControlFiscalCode;
import com.uniba.talend.executorcontrols.processor.control.ControlInt;
import com.uniba.talend.executorcontrols.processor.control.ControlVatNumber;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import com.uniba.talend.executorcontrols.service.UnibaExecutorControlsService;
import org.talend.sdk.component.api.record.Schema;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "ExecutorControls") // icon is located at src/main/resources/icons/ExecutorControls.svg
@Processor(name = "ExecutorControls")
@Documentation("TODO fill the documentation for this processor")
public class ExecutorControlsProcessor implements Serializable {
    private final ExecutorControlsProcessorConfiguration configuration;
    private final UnibaExecutorControlsService service;

    private static List<String> item=new ArrayList<String>();
    private static List<String>choises=new ArrayList<String>();
    private static List<Schema.Entry>schemaList=new ArrayList<Schema.Entry>(Arrays.asList());
    private static List<Schema.Entry>schemaList1=new ArrayList<Schema.Entry>();
    private static List<JComboBox>listCombo=new ArrayList<JComboBox>();

    private static List<Record>listRecord=new ArrayList<Record>();
    private boolean flag=false;
    private int k=0,m=0;
    private static boolean called=false;


    private static final String CODICE_FISCALE="Controllo codice fiscale";
    private static final String PARTITA_IVA="Controllo partita_iva";
    private static final String DATE="Controllo date";

    private static final String DUPLICATI_NUMERICI="Controllo duplicati numerici";

    private static final String NO_CONTROL="----";

    private static final String COMPLETEZZA_SINTATTICA="Completezza sintattica";
    private static final String COMPLETEZZA_DI_RECORD="Completezza di record";
    private static final String ACCURATEZZA_SINTATTICA="Accuratezza sintattica";

    public ExecutorControlsProcessor(@Option("configuration") final ExecutorControlsProcessorConfiguration configuration,
                          final UnibaExecutorControlsService service) {
        this.configuration = configuration;
        this.service = service;
    }

    public static boolean getCalled(){
        return called;
    }

    public static void setCalled(boolean calle){
        called =calle;
    }

    public static void readSchema(final Record record){
        final Schema schema = record.getSchema();

        schemaList=schema.getEntries();

        for(int j=0;j< schemaList.size();j++){
            schemaList1.add(schemaList.get(j));
        }

        for(int i=0;i< schemaList1.size();i++){
            if(schemaList1.get(i).getName().matches("hashCodeDirty")){
                schemaList1.remove(i);
            }
        }

        for(int i=0;i< schemaList1.size();i++){
            if(schemaList1.get(i).getName().matches("loopKey")){
                schemaList1.remove(i);
            }
        }
        schemaList=schemaList1;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it
    }

    @BeforeGroup
    public void beforeGroup() {
        // if the environment supports chunking this method is called at the beginning if a chunk
        // it can be used to start a local transaction specific to the backend you use
        // Note: if you don't need it you can delete it
    }

    @ElementListener
    public void onNext(
            @Input final Record defaultInput,
            @Output final OutputEmitter<Record> defaultOutput) {
        // this is the method allowing you to handle the input(s) and emit the output(s)
        // after some custom logic you put here, to send a value to next element you can use an
        // output parameter and call emit(value).

        if(k==0){
            readSchema(defaultInput);
            createPanel();
            k++;
        }
        flag=true;
        m++;
        listRecord.add(defaultInput);
    }

    public void createPanel(){


        //JFrame f= new JFrame("Panel Example");
        /*JPanel panel=new JPanel();
        panel.setBounds(40,80,500,500);
        panel.setBackground(Color.lightGray);*/
        /*JButton b1=new JButton("Button 1");
        b1.setBounds(50,100,80,30);
        b1.setBackground(Color.yellow);
        JButton b2=new JButton("Button 2");
        b2.setBounds(100,100,80,30);
        b2.setBackground(Color.green);
        panel.add(b1); panel.add(b2);*/
        /*f.add(panel);
        f.setSize(400,400);
        f.setLayout(null);
        f.setVisible(true);*/

        String[] optionsCommand ={"----", "Controllo codice fiscale", "Controllo partita_iva", "Controllo date", "Controllo duplicati numerici"/*,"Completezza sintattica",
        "Completezza di record","Accuratezza sintattica"*/};
        DefaultListModel model = new DefaultListModel();
        //optionsToChoose=schemaList.toString();

        JPanel panel=new JPanel();
        panel.setLayout(new GridLayout(schemaList.size(),2));
        panel.setBounds(40,80,800,800);
        panel.setBackground(Color.lightGray);


        JList<String> list = new JList<String>();
        list.setModel(model);

        for(int i=0;i<schemaList.size();i++){
            Schema.Entry sh = schemaList.get(i);
            model.addElement(sh.getName());
            JLabel column =new JLabel(sh.getName());
            panel.add(column);
            JComboBox<String> cb = new JComboBox<String>(optionsCommand);
            panel.add(cb);
            listCombo.add(cb);
        }


        JOptionPane.showMessageDialog(null, panel, "Seleziona i controlli da effettuare sulle colonne", JOptionPane.PLAIN_MESSAGE);
        int[] values=list.getSelectedIndices();
        for(int i=0;i<values.length;i++){
            item.add(list.getModel().getElementAt(values[i]));

        }

        for(int i=0;i<item.size();i++){
            for(int j=0;j<schemaList.size();j++) {
                if (item.get(i).matches(schemaList.get(j).getName())){

                }
            }
        }



    }

    public void control(){
        System.out.println("\n");
        for(int i=0;i<listCombo.size();i++){
            JComboBox jc=listCombo.get(i);
            choises.add(jc.getSelectedItem().toString());
        }

        for(int j=0;j<schemaList.size();j++){

            if(choises.get(j).matches(CODICE_FISCALE)){
                //System.out.println("Colonna: "+schemaList.get(j).getName()+" Comando: "+choises.get(j)+" count="+j);
                ControlFiscalCode cf=new ControlFiscalCode(schemaList.get(j).getName(),listRecord,schemaList);
                cf.resultControl();
            }else{
                if(choises.get(j).matches(PARTITA_IVA)){
                    // System.out.println("Colonna: "+schemaList.get(j).getName()+" Comando: "+choises.get(j));
                    ControlVatNumber cv=new ControlVatNumber(schemaList.get(j).getName(),listRecord,schemaList);
                    cv.resultControl();
                }else{
                    if(choises.get(j).matches(DATE)){
                        //System.out.println("Colonna: "+schemaList.get(j).getName()+" Comando: "+choises.get(j));
                        ControlDate cd=new ControlDate(schemaList.get(j).getName(),listRecord,schemaList);
                        cd.resultControl();
                    }else {
                        if(choises.get(j).matches(DUPLICATI_NUMERICI)){
                            //System.out.println("Colonna: "+schemaList.get(j).getName()+" Comando: "+choises.get(j));
                            ControlInt cd=new ControlInt(schemaList.get(j).getName(),listRecord,schemaList);
                            cd.verifyInt();
                        }else{
                            if(choises.get(j).matches(NO_CONTROL)){
                                System.out.println("Non è stato selezionato alcun controllo per il campo: "+schemaList.get(j).getName()+"\n");
                            }
                        }
                        /*else{
                            if(choises.get(c).matches(COMPLETEZZA_DI_RECORD)){
                                System.out.println("Colonna: "+schemaList.get(j).getName()+" Comando: "+choises.get(c));
                                c=choises.size();
                            }else{
                                if(choises.get(c).matches(COMPLETEZZA_SINTATTICA)){
                                    System.out.println("Colonna: "+schemaList.get(j).getName()+" Comando: "+choises.get(c));
                                    c=choises.size();
                                }else{
                                    if(choises.get(c).matches(ACCURATEZZA_SINTATTICA)){
                                        System.out.println("Colonna: "+schemaList.get(j).getName()+" Comando: "+choises.get(c));
                                        c=choises.size();
                                    }
                                }
                            }
                        }*/
                    }
                }
            }


        }
    }

    @AfterGroup
    public void afterGroup() {
        // symmetric method of the beforeGroup() executed after the chunk processing
        // Note: if you don't need it you can delete it

        control();
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it
    }
}