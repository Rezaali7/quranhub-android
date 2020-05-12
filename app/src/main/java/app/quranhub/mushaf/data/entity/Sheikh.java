package app.quranhub.mushaf.data.entity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import app.quranhub.R;

@Entity
public class Sheikh {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @SerializedName("ar_name")
    @ColumnInfo(name = "ar_name")
    private String arName;

    private Sheikh() {

    }

    public Sheikh(@NonNull String id, @NonNull String arName) {
        this.id = id;
        this.arName = arName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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
    public String getLocalizedName(@NonNull Context context) {
        switch (id) {
            case "afasy":
                return context.getString(R.string.reciter_afasy);
            case "ayman":
                return context.getString(R.string.reciter_ayman);
            case "basset":
                return context.getString(R.string.reciter_basset);
            case "ghamdy":
                return context.getString(R.string.reciter_ghamdy);
            case "hussary":
                return context.getString(R.string.reciter_hussary);
            case "huzaify":
                return context.getString(R.string.reciter_huzaify);
            case "maher":
                return context.getString(R.string.reciter_maher);
            case "minshawy":
                return context.getString(R.string.reciter_minshawy);
            case "sudais":
                return context.getString(R.string.reciter_sudais);
            case "dossary":
                return context.getString(R.string.reciter_dossary);
            case "yassin":
                return context.getString(R.string.reciter_yassin);
            default:
                return arName;
        }
    }

    public static String getLocalizedName(@NonNull Context context, @NonNull String sheikhId) {
        Sheikh tempSheikh = new Sheikh();
        tempSheikh.id = sheikhId;
        return tempSheikh.getLocalizedName(context);
    }

    @NonNull
    @Override
    public String toString() {
        return "Sheikh{" +
                "id=" + id +
                ", arName='" + arName + '\'' +
                '}';
    }
}
