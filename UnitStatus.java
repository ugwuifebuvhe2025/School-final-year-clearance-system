

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UnitStatus {
    private final StringProperty unitName;
    private final StringProperty status;

    public UnitStatus(String unitName, boolean cleared) {
        this.unitName = new SimpleStringProperty(unitName);
        this.status = new SimpleStringProperty(cleared ? "Cleared ✅" : "Pending ❌");
    }

    public String getUnitName() { return unitName.get(); }
    public String getStatus() { return status.get(); }

    public StringProperty unitNameProperty() { return unitName; }
    public StringProperty statusProperty() { return status; }
}
