

import javafx.beans.property.*;

public class StudentRecord {
    private final IntegerProperty id;
    private final StringProperty fullName;
    private final StringProperty matricNo;
    private final BooleanProperty departmentCleared;

    public StudentRecord(int id, String fullName, String matricNo) {
        this.id = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.matricNo = new SimpleStringProperty(matricNo);
        boolean deptCleared = false;
        this.departmentCleared = new SimpleBooleanProperty(deptCleared);
    }

    public int getId() { return id.get(); }
    public String getFullName() { return fullName.get(); }
    public String getMatricNo() { return matricNo.get(); }
    public boolean isDepartmentCleared() { return departmentCleared.get(); }

    public void setDepartmentCleared(boolean cleared) { departmentCleared.set(cleared); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty matricNoProperty() { return matricNo; }
    public BooleanProperty departmentClearedProperty() { return departmentCleared; }
}
