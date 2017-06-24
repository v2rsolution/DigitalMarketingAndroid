package com.wscubetech.seovideotutorials.model;

import java.io.Serializable;

/**
 * Created by wscubetech on 25/3/17.
 */

public class StudyMaterialModel implements Serializable {
    String studyId="",studyQues="",studyAns="",studyImage="";
    String subCatIconImage="";

    public String getSubCatIconImage() {
        return subCatIconImage;
    }

    public void setSubCatIconImage(String subCatIconImage) {
        this.subCatIconImage = subCatIconImage;
    }

    public String getStudyImage() {
        return studyImage;
    }

    public void setStudyImage(String studyImage) {
        this.studyImage = studyImage;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getStudyQues() {
        return studyQues;
    }

    public void setStudyQues(String studyQues) {
        this.studyQues = studyQues;
    }

    public String getStudyAns() {
        return studyAns;
    }

    public void setStudyAns(String studyAns) {
        this.studyAns = studyAns;
    }
}
