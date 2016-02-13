package queries;

/**
 * Class that for saving the max frequency of each document and the LD
 */
public class Freq {

    double maxFreq;
    double ld;

    public Freq(double maxFreq, double ld) {
        this.maxFreq = maxFreq;
        this.ld = ld;
    }

    public double getMaxFreq() {
        return maxFreq;
    }

    public double getLd() {
        return ld;
    }
}
