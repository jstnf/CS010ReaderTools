package dev.justinf.zybooks2elearn.data;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import org.apache.commons.collections4.MultiValuedMap;

public class ElearnBean {

    @CsvBindByName(column = "Student")
    private String name;

    // Unused
    @CsvBindByName(column = "ID")
    private String eLearnId;

    @CsvBindByName(column = "SIS Login ID")
    private String netId;

    @CsvBindByName(column = "Section")
    private String section;

    // Map containing the current assignments and associated grades
    @CsvBindAndJoinByName(column = ".*", elementType = String.class)
    private MultiValuedMap<String, String> assignments;

    private ZybooksBean linkedZybooksBean = null;

    public String getNetIdFormatted() {
        return netId.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public String getNetId() {
        return netId;
    }

    public String getSection() {
        return section;
    }

    public MultiValuedMap<String, String> getAssignments() {
        return assignments;
    }

    public ZybooksBean getLinkedZybooksBean() {
        return linkedZybooksBean;
    }

    public void setLinkedZybooksBean(ZybooksBean linkedZybooksBean) {
        this.linkedZybooksBean = linkedZybooksBean;
    }

    // debug string
    public String allDataString() {
        return name + " " + eLearnId + " " + section + " " + netId + " " + assignments.size() + "->assignments";
    }
}