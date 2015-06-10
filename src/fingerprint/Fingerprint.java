package fingerprint;

/**
 * Created by Sebastian on 2015-06-10.
 */
public class Fingerprint {
    private String name;
    private int[] verticalData = new int[5];
    private int[] horizontalData = new int[5];


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getVerticalData() {
        return verticalData;
    }

    public int getVerticalData(int index) {
        return verticalData[index];
    }

    public void setVerticalData(int[] verticalData) {
        this.verticalData = verticalData;
    }

    public void setVerticalData(int index, int value) {
        this.verticalData[index] = value;
    }

    public int[] getHorizontalData() {
        return horizontalData;
    }

    public int getHorizontalData(int index) {
        return horizontalData[index];
    }

    public void setHorizontalData(int[] horizontalData) {
        this.horizontalData = horizontalData;
    }

    public void setHorizontalData(int index, int value) {
        this.horizontalData[index] = value;
    }
}
