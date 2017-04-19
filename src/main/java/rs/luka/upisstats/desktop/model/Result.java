package rs.luka.upisstats.desktop.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static rs.luka.upisstats.desktop.model.ApiConstants.*;

/**
 * Created by luka on 24.1.17..
 */
public class Result {

    private Color color;
    private double scaleFactor;
    private String xName, yName, zName;
    private int counts[];

    private int distinctCount;
    private int totalCount;
    private float sizes[];
    private float x[], y[];


    private int xMax, xMin, yMax, yMin;
    private double xAvg, yAvg;
    private boolean hasResults = true;

    public Result(JsonObject json) {
        //System.out.println("result: " + jsonResult);

        if (json.has(KEY_COLOR)) color = Color.decode("#"+json.get(KEY_COLOR).getAsString().substring(2));
        if (json.has(KEY_SCALE_FACTOR)) scaleFactor = json.get(KEY_SCALE_FACTOR).getAsDouble();
        if (json.has(KEY_X_AXIS_NAME) && !json.get(KEY_X_AXIS_NAME).isJsonNull())
            xName = json.get(KEY_X_AXIS_NAME).getAsString();
        if (json.has(KEY_Y_AXIS_NAME) && !json.get(KEY_Y_AXIS_NAME).isJsonNull())
            yName = json.get(KEY_Y_AXIS_NAME).getAsString();
        if (json.has(KEY_Z_AXIS_NAME) && !json.get(KEY_Z_AXIS_NAME).isJsonNull())
            zName = json.get(KEY_Z_AXIS_NAME).getAsString();

        JsonArray data = json.get(KEY_RESULT_DATA).getAsJsonArray();
        if (data.get(0).getAsJsonArray().size() == 0) {
            hasResults = false;
            return;
        }

        JsonArray counts = data.get(0).getAsJsonArray();
        distinctCount = counts.size();
        this.counts = new int[distinctCount];
        sizes = new float[distinctCount];
        for (int i = 0; i < distinctCount; i++) {
            this.counts[i] = counts.get(i).getAsInt();
            totalCount += this.counts[i];
            sizes[i] = (float)Math.max(2, this.counts[i] * scaleFactor);
        }
        x = new float[distinctCount];
        double[] stats = parseAxisArrays(data, 1, x);
        xAvg = stats[0]; xMin = (int)stats[1]; xMax = (int)stats[2];
        if (data.size() > 1) {
            y = new float[distinctCount];
            stats = parseAxisArrays(data, 2, y);
            yAvg = stats[0]; yMin = (int) stats[1]; yMax = (int) stats[2];
        }

        if(xName == null || yName == null) {
            Map<Float, Integer> countMap;
            if(xName != null) countMap = countData(x);
            else              countMap = countData(y);

            distinctCount = countMap.size();
            x = new float[distinctCount];
            y = new float[distinctCount];

            if(xName != null) {countsToAxes(countMap, x, y); yAvg = 0;}
            else              {countsToAxes(countMap, y, x); xAvg = 0;}

            int[] xStats = getExtremeIndices(x);
            xMin = xStats[0]; xMax = xStats[1];
            int[] yStats = getExtremeIndices(y);
            yMin = yStats[0]; yMax = yStats[1];

            sizes = new float[distinctCount];
            Arrays.fill(sizes, 6);
        }
    }

    private static int[] getExtremeIndices(float[] data) {
        float max = Float.NEGATIVE_INFINITY, min = Float.POSITIVE_INFINITY; int maxi = 0, mini=0;
        for (int i = 0; i < data.length; i++) {
            if(data[i] > max) {max = data[i]; maxi=i;}
            if(data[i] < min) {min = data[i]; mini=i;}
        }
        return new int[] {mini, maxi};
    }
    private static Map<Float, Integer> countData(float[] data) {
        Map<Float, Integer> counts = new HashMap<>();
        for(float f : data)
            counts.put(f, counts.getOrDefault(f, 0)+1);
        return counts;
    }
    private static void countsToAxes(Map<Float, Integer> counts, float[] keys, float[] values) {
        int i=0;
        for(Map.Entry<Float, Integer> e : counts.entrySet()) {
            keys[i] = e.getKey();
            values[i] = e.getValue();
            i++;
        }
    }

    private static double[] parseAxisArrays(JsonArray data, int index, float[] parsed) {
        JsonArray axis = data.get(index).getAsJsonArray();
        double sum = 0, max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY, maxi = 0, mini=0;
        for (int i = 0; i < parsed.length; i++) {
            parsed[i] = axis.get(i).getAsFloat();
            sum += parsed[i];
            if(parsed[i] > max) {max = parsed[i]; maxi=i;}
            if(parsed[i] < min) {min = parsed[i]; mini=i;}
        }
        return new double[] {sum / parsed.length, mini, maxi};
    }


    public void setColor(Color color) {
        this.color = color;
    }

    public float getMinX() {
        if(!hasResults()) return 0;
        return x[xMin];
    }

    public float getMaxX() {
        if(!hasResults()) return 0;
        return x[xMax];
    }

    public float getMinY() {
        if(!hasResults()) return 0;
        return y[yMin];
    }

    public float getMaxY() {
        if(!hasResults()) return 0;
        return y[yMax];
    }

    public int getMaxXIndex() {
        return xMax;
    }

    public int getMinXIndex() {
        return xMin;
    }

    public int getMaxYIndex() {
        return yMax;
    }

    public int getMinYIndex() {
        return yMin;
    }

    public float getX(int i) {
        return x[i];
    }

    public float getY(int i) {
        return y[i];
    }

    public Color getColor() {
        return color;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public String getxName() {
        return xName;
    }

    public String getyName() {
        return yName;
    }

    public String getzName() {
        return zName;
    }

    public int getCount(int i) {
        return counts[i];
    }

    public float getSize(int i) {
        return sizes[i];
    }

    public boolean hasResults() {
        return hasResults;
    }

    public int getDistinct() {
        return distinctCount;
    }

    public int getCount() { return totalCount; }

    public double getxAvg() {
        return xAvg;
    }

    public double getyAvg() {
        return yAvg;
    }
}
