package app.quranhub.mushaf.model;

public class QuranPageInfo {

    private int juz;
    private int sura;


    public int getJuz() {
        return juz;
    }

    public void setJuz(int juz) {
        this.juz = juz;
    }

    public int getSura() {
        return sura;
    }

    public void setSura(int sura) {
        this.sura = sura;
    }

    @Override
    public String toString() {
        return "QuranPageInfo{" +
                "juz=" + juz +
                ", number='" + sura + '\'' +
                '}';
    }

}
