/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

import org.apromore.canoniser.Canoniser;
import org.apromore.helper.Version;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveAsDialogController extends BaseController {

    private EventQueue<Event> qe = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);

    private static final BigDecimal VERSION_INCREMENT = new BigDecimal("0.1");

    private Window saveAsW;
    private Textbox modelName;
    private Textbox versionNumber;
    private Textbox branchName;

    @Wire
    private Radiogroup radiogroup;
    private Radio radio1;
    private Radio radio2;
    private Label label1;

//    private Label label2;
//    private Textbox model_to_be_evolved_1;
//    private Label label3;
//    private Textbox version_number_of_model1_to_be_evolved;
//    private Label label4;
//    private Textbox branch_name_of_model1_to_be_evolved;
//    private Label label5;
//    private Textbox new_version_number_of_model1_to_be_evolved;
//    private Label label6;
//    private Textbox new_branch_name_of_model1_to_be_evolved;

    private ProcessSummaryType process;
    private VersionSummaryType version;
    private PluginPropertiesHelper pluginPropertiesHelper;
    private EditSessionType editSession;
    private boolean save;
    private String modelData;
    private String originalVersionNumber;

    /*corresponding to radio option value*/
    private Boolean isPropagateChange = false;

    public SaveAsDialogController(ProcessSummaryType process, VersionSummaryType version, EditSessionType editSession,
                                  boolean isNormalSave, String data) throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
        this.process = process;
        this.version = version;
        this.editSession = editSession;
        this.save = isNormalSave;
        this.saveAsW = (Window) Executions.createComponents("saveAsDialog.zul", null, null);
        this.modelData = data;
        this.originalVersionNumber = this.editSession.getCurrentVersionNumber();

        if (isNormalSave) {
            this.saveAsW.setTitle("Save");
        } else {
            this.saveAsW.setTitle("Save As");
        }

        Rows rows = (Rows) this.saveAsW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();

        /*add the text boxes in 0,1,2 row*/
        Row modelNameR = (Row) rows.getChildren().get(0);
        Row versionNumberR = (Row) rows.getChildren().get(1);
        Row branchNameR = (Row) rows.getChildren().get(2);

        this.modelName = (Textbox) modelNameR.getFirstChild().getNextSibling();
        this.versionNumber = (Textbox) versionNumberR.getFirstChild().getNextSibling();
        this.branchName = (Textbox) branchNameR.getFirstChild().getNextSibling();

        /*add the label and radio group, the 3rd row*/
        this.label1 = (Label) rows.getChildren().get(3).getChildren().get(0);
        this.radiogroup = (Radiogroup) rows.getChildren().get(3).getChildren().get(1);
        this.radio1 = (Radio) radiogroup.getChildren().get(0);
        this.radio2 = (Radio) radiogroup.getChildren().get(1);

        radio2.setChecked(true);

        radio1.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        setIsPropagateChange(true);
                    }
                });

        radio2.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        setIsPropagateChange(false);
                    }
                });

//        /*add the text boxes in the 4 rows*/
//        Row model_to_be_evolved_1_R = (Row) rows.getChildren().get(4);
//        this.label2 = (Label) model_to_be_evolved_1_R.getChildren().get(0);
//        this.model_to_be_evolved_1 = (Textbox) model_to_be_evolved_1_R.getChildren().get(1);
//        this.model_to_be_evolved_1.setText("10000");
//
//        /*add the text boxes in the 5 row*/
//        Row version_number_of_model1_to_be_evolved_R = (Row) rows.getChildren().get(5);
//        this.label3 = (Label) version_number_of_model1_to_be_evolved_R.getChildren().get(0);
//        this.version_number_of_model1_to_be_evolved= (Textbox) version_number_of_model1_to_be_evolved_R.getChildren().get(1);
//
//        /*add the text box in the 6 row*/
//        Row branch_name_of_model1_to_be_evolved_R = (Row) rows.getChildren().get(6);
//        this.label4 = (Label) branch_name_of_model1_to_be_evolved_R.getChildren().get(0);
//        this.branch_name_of_model1_to_be_evolved = (Textbox) branch_name_of_model1_to_be_evolved_R.getChildren().get(1);
//
//        /*add the text boxes in the 7 row*/
//        Row new_version_number_of_model1_to_be_evolved_R = (Row) rows.getChildren().get(7);
//        this.label5 = (Label) new_version_number_of_model1_to_be_evolved_R.getChildren().get(0);
//        this.new_version_number_of_model1_to_be_evolved= (Textbox) new_version_number_of_model1_to_be_evolved_R.getChildren().get(1);
//
//        /*add the text box in the 8 row*/
//        Row new_branch_name_of_model1_to_be_evolved_R = (Row) rows.getChildren().get(8);
//        this.label6 = (Label) new_branch_name_of_model1_to_be_evolved_R.getChildren().get(0);
//        this.new_branch_name_of_model1_to_be_evolved = (Textbox) new_branch_name_of_model1_to_be_evolved_R.getChildren().get(1);

        /*add the button group in the 9th row*/
        Row buttonGroupR = (Row) rows.getChildren().get(4);
        pluginPropertiesHelper = new PluginPropertiesHelper(getService(), (Grid) this.saveAsW.getFellow("saveAsGrid"));
        Button saveB = (Button) buttonGroupR.getFirstChild().getFirstChild();
        Button cancelB = (Button) saveB.getNextSibling();
        this.modelName.setText(this.editSession.getProcessName());

        saveB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        saveModel(save);
                    }
                });
        this.saveAsW.addEventListener("onOK",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        saveModel(save);
                    }
                });
        cancelB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });

        if (isNormalSave) {
            String branchName = null;
            BigDecimal versionNumber;
            BigDecimal currentVersion = new BigDecimal(editSession.getCurrentVersionNumber());
            BigDecimal maxVersion = new BigDecimal(editSession.getMaxVersionNumber());

            versionNumber = createNewVersionNumber(currentVersion);
            if (maxVersion.compareTo(currentVersion) > 0) {
                branchName = createNewBranchName(this.editSession.getOriginalBranchName());
            } else {
                branchName = this.editSession.getOriginalBranchName();
            }


            this.modelName.setReadonly(true);
            this.branchName.setText(branchName);
            if (version.isEmpty()) {
                this.versionNumber.setText("1.0");
            } else {
                this.versionNumber.setText(String.format("%1.1f", versionNumber));
            }

        } else {
            this.branchName.setText("MAIN");
            this.branchName.setReadonly(true);
            this.versionNumber.setText("1.0");
        }

        this.saveAsW.doModal();
    }


    protected void cancel() throws Exception {
        closePopup();
    }

    private void closePopup() {
        this.saveAsW.detach();
    }


    protected void saveModel(boolean isNormalSave) throws Exception {
        String userName = UserSessionManager.getCurrentUser().getUsername();
        String nativeType = this.editSession.getNativeType();
        String versionName = this.version.getName();
        String domain = this.process.getDomain();
        String processName = this.modelName.getText();
        Integer processId = this.process.getId();
        String created = this.version.getCreationDate();
        String branch = this.branchName.getText();
        boolean makePublic = this.process.isMakePublic();
        String versionNo = versionNumber.getText();


        //get necessary parameters for  change propagation
//        Integer originalProcessId =  processId;
//        String originalBranchName  = this.editSession.getOriginalBranchName();
       // String originalVersionNumber =  this.originalVersionNumber;

//        Integer model_to_be_evolved_1 = Integer.valueOf(this.model_to_be_evolved_1.getText());
//        String version_number_of_model1_to_be_evolved = this.version_number_of_model1_to_be_evolved.getText();
//        String branch_name_of_model1_to_be_evolved = this.branch_name_of_model1_to_be_evolved.getText();

//        ProcessVersionIdType originalVariantVersion = new ProcessVersionIdType();
//        ProcessVersionIdType changedVariantVersion = new ProcessVersionIdType();
//        ProcessVersionIdType originalMergedVersion = new ProcessVersionIdType();
//        String new_version_number_of_model1_to_be_evolved = new String("");
//        if(this.new_version_number_of_model1_to_be_evolved.getText() != null){
//            new_version_number_of_model1_to_be_evolved  = this.new_version_number_of_model1_to_be_evolved.getText();
//        }
//        String new_branch_name_of_model1_to_be_evolved = new String("");
//        if(this.new_branch_name_of_model1_to_be_evolved.getText()!=null){
//            new_branch_name_of_model1_to_be_evolved = this.new_branch_name_of_model1_to_be_evolved.getText();
//        }


        if (branch == null || branch.equals("")) {
            branch = "MAIN";
        }


        // TODO: If Save As, the default branch should be MAIN and has to be handled at the server end
        InputStream is = new ByteArrayInputStream(this.modelData.getBytes());
        try {


            if (validateFields()) {
                if (!isNormalSave) {
                    Integer folderId = 0;
                    if (UserSessionManager.getCurrentFolder() != null) {
                        folderId = UserSessionManager.getCurrentFolder().getId();
                    }
                    getService().importProcess(userName, folderId, nativeType, processName, versionNo, is, domain, null, created, null,
                            makePublic, pluginPropertiesHelper.readPluginProperties(Canoniser.CANONISE_PARAMETER));
                } else {

                    /*Case1: no consider change propagation*/
                    if(!isPropagateChange){

                        getService().updateProcess(editSession.hashCode(), userName, nativeType, processId, domain, process.getName(),
                                editSession.getOriginalBranchName(), branch, versionNo, originalVersionNumber, versionName, is, 0 );
                    }

                    /*Case2: consider change propagation*/
                    else{

                            /*invoke the updated process with updating process receiving change propagation*/
                            getService().updateProcess(editSession.hashCode(), userName, nativeType, processId, domain, process.getName(),
                                    editSession.getOriginalBranchName(), branch, versionNo, originalVersionNumber, versionName, is,1);


                    }

                }
                Messagebox.show("Saved Successfully!", "Save", Messagebox.OK, Messagebox.INFORMATION);
                qe.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
                closePopup();
            }

        } catch (Exception e) {
            Messagebox.show("Unable to Save Model : Error: \n" + e.getMessage());
        }

    }


    private boolean validateFields() {
        boolean valid = true;
        String message = "";
        String title = "Missing Fields";

        Version newVersion = new Version(versionNumber.getText());
        Version curVersion = new Version(editSession.getCurrentVersionNumber());
        try {
            if (this.save) {
                if (newVersion.compareTo(curVersion) < 0) {
                    valid = false;
                    message = message + "New Version number has to be greater than " + this.editSession.getCurrentVersionNumber();
                    title = "Wrong Version Number";
                }
                if (this.branchName.getText().equals("") || this.branchName.getText() == null) {
                    valid = false;
                    message = message + "Branch Name cannot be empty";
                    title = "Branch Name Empty";
                }
            } else {
                if (this.modelName.getText().equals("") || this.modelName.getText() == null) {
                    valid = false;
                    message = message + "Model Name cannot be empty";
                    title = "Model Name Empty";
                }
                if (this.modelName.getText().equals(this.editSession.getProcessName())) {
                    valid = false;
                    message = message + "Model Name has to be different from " + this.editSession.getProcessName();
                    title = "Same Model Name";
                }
            }
            if (this.versionNumber.getText().equals("") || this.versionNumber.getText() == null) {
                valid = false;
                message = message + "Version Number cannot be empty";
                title = "Version Number Empty";
            }
            if (!message.equals("")) {
                Messagebox.show(message, title, Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

//    private boolean validateFields_propagate_1(){
//
//        /*TODO: validate the fields for the merged model 1 to be evolved*/
//        boolean valid = true;
//        String message = "";
//        String title = "Missing Fields";
//
//       try{
//
////            if (this.model_to_be_evolved_1.getText().equals("") || this.model_to_be_evolved_1.getText() == null) {
////                valid = false;
////                message = message + "Version Number of Model to be evolved cannot be empty";
////                title = "Version Number of Model to be evolved Empty";
////            }
//            if (!message.equals("")){
//
//                Messagebox.show(message, title, Messagebox.OK, Messagebox.INFORMATION);
//            }
//
//        }catch (Exception e){
//
//            valid = false;
//
//        }
//
//        return valid;
//    }

    private String createNewBranchName(String currBranchName) {
        String branchName;
        if (currBranchName.equalsIgnoreCase("Main") || !currBranchName.matches("B[0-9]+")) {
            branchName = "B1";
        } else {
            Integer branchVersionNumber = 0;
            Matcher matcher = Pattern.compile("\\d+").matcher(currBranchName);
            if (matcher.find()) {
                branchVersionNumber = Integer.valueOf(matcher.group());
            }
            branchName = "B" + branchVersionNumber + 1;
        }
        return branchName;
    }

    private BigDecimal createNewVersionNumber(BigDecimal currentVersion) {
        return (currentVersion).add(VERSION_INCREMENT);
    }

    public void setIsPropagateChange (Boolean isPropagateChange){

        this.isPropagateChange = isPropagateChange;
    }

}