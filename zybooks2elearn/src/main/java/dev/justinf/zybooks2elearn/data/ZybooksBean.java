package dev.justinf.zybooks2elearn.data;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import org.apache.commons.collections4.MultiValuedMap;

public class ZybooksBean {

    @CsvBindByName(column = "Last name")
    private String lastName;

    @CsvBindByName(column = "First name")
    private String firstName;

    // Unused in favor of school email
    @CsvBindByName(column = "Primary email")
    private String primaryEmail;

    @CsvBindByName(column = "Class section")
    private String section;

    @CsvBindByName(column = "Student ID")
    private String studentId;

    @CsvBindByName(column = "School email")
    private String schoolEmail;

    @CsvBindAndJoinByName(column = ".*", elementType = String.class)
    private MultiValuedMap<String, String> assignments;

    public String getNetIdFormatted() {
        return schoolEmail.substring(0, Math.max(0, schoolEmail.indexOf("@"))).toLowerCase(); // Return all contents before the @
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSection() {
        return section;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public MultiValuedMap<String, String> getAssignments() {
        return assignments;
    }

    // Debug string
    public String allDataString() {
        return lastName + " " + firstName + " " + primaryEmail + " " + section + " " + studentId + " " + schoolEmail + " " + assignments.size() + "->assignments";
    }
}