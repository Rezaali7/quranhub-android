package app.quranhub.mushaf.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Recitation.class, parentColumns = "id", childColumns = "recitation_id"
                , onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
        @ForeignKey(entity = Sheikh.class, parentColumns = "id", childColumns = "sheikh_id"
                , onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)})
public class SheikhRecitation {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "recitation_id")
    private int recitationId;

    @NonNull
    @ColumnInfo(name = "sheikh_id")
    private String sheikhId;

    public SheikhRecitation(int recitationId, @NonNull String sheikhId) {
        this.recitationId = recitationId;
        this.sheikhId = sheikhId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecitationId() {
        return recitationId;
    }

    public void setRecitationId(int recitationId) {
        this.recitationId = recitationId;
    }

    @NonNull
    public String getSheikhId() {
        return sheikhId;
    }

    public void setSheikhId(@NonNull String sheikhId) {
        this.sheikhId = sheikhId;
    }

    @NonNull
    @Override
    public String toString() {
        return "SheikhRecitation{" +
                "id=" + id +
                ", recitationId=" + recitationId +
                ", sheikhId=" + sheikhId +
                '}';
    }
}
