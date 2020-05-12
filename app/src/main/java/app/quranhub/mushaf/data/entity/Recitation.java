package app.quranhub.mushaf.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Recitation {

    @PrimaryKey
    private int id;

    @NonNull
    @ColumnInfo(name = "ar_name")
    private String arName;

    public Recitation(int id, @NonNull String arName) {
        this.id = id;
        this.arName = arName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getArName() {
        return arName;
    }

    public void setArName(@NonNull String arName) {
        this.arName = arName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Recitation{" +
                "id=" + id +
                ", arName='" + arName + '\'' +
                '}';
    }
}
