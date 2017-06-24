package com.wscubetech.seovideotutorials.model;

import java.io.Serializable;

/**
 * Created by wscubetech on 13/9/16.
 */
public class SubCategoryModel implements Serializable{
    String subCatId="",subCatTitle="",subCatImage="";
    String subCatFlag=""; //1->Interview Ques   2->Technical Terms
    String subCatQuizTime="";

    String subCatHindiCount="0",subCatEnglishCount="0",total="0";

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubCatHindiCount() {
        return subCatHindiCount;
    }

    public void setSubCatHindiCount(String subCatHindiCount) {
        this.subCatHindiCount = subCatHindiCount;
    }

    public String getSubCatEnglishCount() {
        return subCatEnglishCount;
    }

    public void setSubCatEnglishCount(String subCatEnglishCount) {
        this.subCatEnglishCount = subCatEnglishCount;
    }

    public String getSubCatQuizTime() {
        return subCatQuizTime;
    }

    public void setSubCatQuizTime(String subCatQuizTime) {
        this.subCatQuizTime = subCatQuizTime;
    }

    public String getSubCatFlag() {
        return subCatFlag;
    }

    public void setSubCatFlag(String subCatFlag) {
        this.subCatFlag = subCatFlag;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getSubCatTitle() {
        return subCatTitle;
    }

    public void setSubCatTitle(String subCatTitle) {
        this.subCatTitle = subCatTitle;
    }

    public String getSubCatImage() {
        return subCatImage;
    }

    public void setSubCatImage(String subCatImage) {
        this.subCatImage = subCatImage;
    }
}
