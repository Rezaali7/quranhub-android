package app.quranhub.downloads_manager.network.model;

import androidx.annotation.NonNull;

import java.util.List;

import app.quranhub.mushaf.data.entity.Sheikh;

public class RecitersResponse {

    @NonNull
    private List<Sheikh> reciters;

    public RecitersResponse(@NonNull List<Sheikh> reciters) {
        this.reciters = reciters;
    }

    @NonNull
    public List<Sheikh> getReciters() {
        return reciters;
    }

    public void setReciters(@NonNull List<Sheikh> reciters) {
        this.reciters = reciters;
    }

    @Override
    public String toString() {
        return "RecitersResponse{" +
                "reciters=" + reciters +
                '}';
    }
}
