package lms;

// Buat class baru: SoalKuis.java
public class SoalKuis {
    public String pertanyaan;
    public String opsiA, opsiB, opsiC, opsiD;
    public String jawaban; // 'A', 'B', 'C', atau 'D'
 
    public SoalKuis(String p, String a, String b, String c, String d, String j) {
        this.pertanyaan = p;
        this.opsiA = a; this.opsiB = b;
        this.opsiC = c; this.opsiD = d;
        this.jawaban = j;
    }
}
