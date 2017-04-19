package rs.luka.upisstats.desktop.ui;

/**
 * Created by luka on 25.1.17..
 */
public class QuickSpatialMap {
    private static final int RESOLUTION = 200;

    private QueryPoints.PlotPoint[][] data;
    private double scale;

    public QuickSpatialMap(int x, int y) {
        if (x == 0 || y == 0) throw new IllegalArgumentException("Map size can't be 0");
        scale = (double) RESOLUTION / x;
        data = new QueryPoints.PlotPoint[RESOLUTION][(int) (y * scale)];
    }

    public void addAll(QueryPoints points) {
        points.getPoints().forEachRemaining(this::addPoint);
    }

    //keeping this for reference
    private void addPointBroken(QueryPoints.PlotPoint point) {
        if (Double.isInfinite(point.getX()) || Double.isInfinite(point.getY()))
            throw new ArithmeticException("point is infinitely far away!");
        //if(true) { drawCircleProper(point); return; }

        int radius = (int) (point.getRadius() * scale) + 1;
        int f = 1 - radius;
        int ddF_x = 0;
        int ddF_y = -2 * radius;
        int x = 0;
        int y = radius;
        int x0 = (int) (point.getCenterX() * scale);
        int y0 = (int) (point.getCenterY() * scale);

        putPoint(x0, y0 + radius, point);
        putPoint(x0, y0 - radius, point);
        putLine(x0 - radius, x0 + radius, y0, point);

        while (x < y) {
            if (f >= 0) {
                y--;
                ddF_y += 2;
                f += ddF_y;
            }
            x++;
            ddF_x += 2;
            f += ddF_x + 1;
            putLine(x0 - x, x0 + x, y0 + y, point);
            putLine(x0 - x, x0 + x, y0 - y, point);
            putLine(x0 - x, x0 + x, y0 + x, point);
            putLine(x0 - x, x0 + x, y0 - x, point);
        }
    }

    /**
     * Thank you kind stranger http://stackoverflow.com/a/24527943/2363015
     *
     * @param point
     */
    public void addPoint(QueryPoints.PlotPoint point) {
        int radius = (int) (point.getRadius() * scale) + 1;
        int centerX = (int) (point.getCenterX() * scale);
        int centerY = (int) (point.getCenterY() * scale);

        int x = radius;
        int radiusError = 1 - x;
        int y = 0;
        while (x >= y) {
            int startX = -x + centerX;
            int endX = x + centerX;
            putLine(startX, endX, y + centerY, point);
            if (y != 0) {
                putLine(startX, endX, -y + centerY, point);
            }

            y++;

            if (radiusError < 0) {
                radiusError += 2 * y + 1;
            } else {
                if (x >= y) {
                    startX = -y + 1 + centerX;
                    endX = y - 1 + centerX;
                    putLine(startX, endX, x + centerY, point);
                    putLine(startX, endX, -x + centerY, point);
                }
                x--;
                radiusError += 2 * (y - x + 1);
            }

        }
    }

    private void putLine(int x0, int x1, int y, QueryPoints.PlotPoint object) {
        if (x0 > x1) {
            int t = x0;
            x0 = x1;
            x1 = t;
        }
        for (int x = x0; x <= x1; x++)
            putPoint(x, y, object);
    }

    private void putPoint(int x, int y, QueryPoints.PlotPoint object) {
        if (x < 0 || y < 0 || x >= data.length || y >= data[0].length) return;
        if (data[x][y] == object) return;
        if (data[x][y] == null || data[x][y].getRadius() > object.getRadius()) data[x][y] = object;
    }

    public void clear() {
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                data[i][j] = null;
    }

    public QueryPoints.PlotPoint getPoint(int x, int y) {
        x = Math.min((int) (x * scale), data.length - 1);
        y = Math.min((int) (y * scale), data[0].length - 1);
        printMap();
        return data[x][y];
    }

    private boolean alreadyPrinted = true;
    private void printMap() {
        if (!alreadyPrinted) {
            for (QueryPoints.PlotPoint[] row : data) {
                for (QueryPoints.PlotPoint point : row)
                    System.out.print(point == null ? "0" : "1");
                System.out.println();
            }
            alreadyPrinted = true;
        }
    }

    public boolean hasPointOn(int x, int y) {
        x = Math.min((int) (x * scale), data.length - 1);
        y = Math.min((int) (y * scale), data[0].length - 1);
        return data[x][y] != null;
    }
}
