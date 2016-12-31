// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import java.util.concurrent.CountedCompleter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Spliterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Contended;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntBiFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongBiFunction;
import java.util.function.DoubleBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import sun.misc.Unsafe;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;
import java.awt.Dimension;
import java.util.Collection;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Collections;
import java.util.TreeMap;
import metapiga.utilities.Tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;
import java.util.SortedMap;
import java.awt.Color;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;

public class Graph extends JPanel
{
    private static final int space = 1;
    private ConcurrentHashMap<String, List<Double>> points;
    private Map<String, Color> colors;
    private SortedMap<Integer, Integer> replicatesPositions;
    private Map<Integer, Double> replicatesMinima;
    private Map<Integer, Double> replicatesMaxima;
    private int currentReplicateId;
    private SortedMap<Integer, Integer> restartsPositions;
    private Map<Integer, Double> restartsMinima;
    private Map<Integer, Double> restartsMaxima;
    private int numRestart;
    private int currentRestartId;
    private String mainCurve;
    private double maxValue;
    private double maxValueCurrentRep;
    private double minValueCurrentRep;
    private double maxValueCurrentRestart;
    private double minValueCurrentRestart;
    private double minValue;
    private int numPoints;
    private final boolean maxLine;
    private final boolean minLine;
    private final boolean negative;
    private final int xStep;
    private final GraphY yAxis;
    private Point viewPosition;
    
    public Graph(final String[] curves, final int mainCurveIndex, final boolean maxLine, final boolean minLine, final boolean negative, final int xStep, final GraphY yAxis) {
        this.currentReplicateId = -1;
        this.numRestart = Integer.MAX_VALUE;
        this.currentRestartId = -1;
        this.mainCurve = curves[mainCurveIndex];
        this.maxLine = maxLine;
        this.minLine = minLine;
        this.negative = negative;
        this.xStep = xStep;
        this.yAxis = yAxis;
        this.points = new ConcurrentHashMap<String, List<Double>>();
        this.colors = new HashMap<String, Color>();
        final List<Color> availableColors = new ArrayList<Color>();
        availableColors.add(Color.green);
        availableColors.add(Color.yellow);
        availableColors.add(Color.magenta);
        availableColors.add(Color.blue);
        availableColors.add(Color.white);
        availableColors.add(Color.orange);
        availableColors.add(Color.pink);
        for (final String curve : curves) {
            this.points.put(curve, new ArrayList<Double>());
            final Color color = availableColors.isEmpty() ? new Color(Tools.randInt(255), Tools.randInt(255), Tools.randInt(255)) : availableColors.remove(0);
            this.colors.put(curve, color);
        }
        this.replicatesPositions = Collections.synchronizedSortedMap(new TreeMap<Integer, Integer>());
        this.replicatesMinima = new TreeMap<Integer, Double>();
        this.replicatesMaxima = new TreeMap<Integer, Double>();
        this.restartsPositions = Collections.synchronizedSortedMap(new TreeMap<Integer, Integer>());
        this.restartsMinima = new TreeMap<Integer, Double>();
        this.restartsMaxima = new TreeMap<Integer, Double>();
        this.maxValue = 0.0;
        this.minValue = Double.MAX_VALUE;
        this.maxValueCurrentRep = 0.0;
        this.minValueCurrentRep = Double.MAX_VALUE;
        this.maxValueCurrentRestart = 0.0;
        this.minValueCurrentRestart = Double.MAX_VALUE;
        this.numPoints = 1;
    }
    
    public boolean addPoints(final Map<String, Double> nextPoints) {
        for (final Map.Entry<String, Double> e : nextPoints.entrySet()) {
            this.points.get(e.getKey()).add(e.getValue());
        }
        ++this.numPoints;
        this.viewPosition = new Point(this.numPoints * 1, 0);
        final double point = nextPoints.get(this.mainCurve);
        if (point > this.maxValue) {
            this.maxValue = point;
        }
        if (point < this.minValue) {
            this.minValue = point;
        }
        if (point > this.maxValueCurrentRep) {
            this.maxValueCurrentRep = point;
        }
        if (point < this.minValueCurrentRep) {
            this.minValueCurrentRep = point;
        }
        if (point > this.maxValueCurrentRestart) {
            this.maxValueCurrentRestart = point;
        }
        if (point < this.minValueCurrentRestart) {
            this.minValueCurrentRestart = point;
        }
        if (this.getWidth() <= this.numPoints * 1) {
            return false;
        }
        this.repaint();
        return true;
    }
    
    public Color getColor(final String curve) {
        return this.colors.get(curve);
    }
    
    public void setColor(final String curve, final Color color) {
        this.colors.put(curve, color);
    }
    
    public void newReplicate(final int replicateId) {
        if (this.currentReplicateId >= 0) {
            this.replicatesMaxima.put(this.currentReplicateId, this.maxValueCurrentRep);
            this.replicatesMinima.put(this.currentReplicateId, this.minValueCurrentRep);
        }
        this.maxValueCurrentRep = 0.0;
        this.minValueCurrentRep = Double.MAX_VALUE;
        this.replicatesPositions.put(replicateId, this.numPoints);
        this.currentReplicateId = replicateId;
        if (this.numRestart == Integer.MAX_VALUE && this.currentRestartId > -1) {
            this.numRestart = this.currentRestartId + 1;
        }
    }
    
    public void restart() {
        if (this.currentRestartId >= 0) {
            this.restartsMaxima.put(this.currentRestartId, this.maxValueCurrentRestart);
            this.restartsMinima.put(this.currentRestartId, this.minValueCurrentRestart);
        }
        this.maxValueCurrentRestart = 0.0;
        this.minValueCurrentRestart = Double.MAX_VALUE;
        ++this.currentRestartId;
        this.restartsPositions.put(this.currentRestartId, this.numPoints);
    }
    
    public Point getViewPosition() {
        return this.viewPosition;
    }
    
    public void paintComponent(final Graphics g) {
        g.setColor(new Color(0, 0, 0));
        final Dimension dim = this.getSize();
        g.fillRect(0, 0, (int)dim.getWidth(), (int)dim.getHeight());
        if (this.numPoints > 1) {
            g.setColor(Color.green);
            final double scale = (this.maxValue - this.minValue) / (this.getHeight() * 80.0 / 100.0);
            try {
                final Map<String, Double> lastPoint = new HashMap<String, Double>();
                for (final Map.Entry<String, List<Double>> e : this.points.entrySet()) {
                    lastPoint.put(e.getKey(), e.getValue().get(0));
                }
                final Iterator<Map.Entry<Integer, Integer>> rIt = this.replicatesPositions.entrySet().iterator();
                Map.Entry<Integer, Integer> e2 = rIt.hasNext() ? rIt.next() : null;
                int rPos = (e2 != null) ? e2.getValue() : -1;
                int rNum = (e2 != null) ? e2.getKey() : 1;
                final Iterator<Map.Entry<Integer, Integer>> rsIt = this.restartsPositions.entrySet().iterator();
                Map.Entry<Integer, Integer> ers = rsIt.hasNext() ? rsIt.next() : null;
                int rsPos = (ers != null) ? ers.getValue() : -1;
                int rsNum = (ers != null) ? (ers.getKey() % this.numRestart) : 1;
                int x = 1;
                for (int i = 1; i < this.numPoints - 1; ++i) {
                    final int x2 = 1 * (x - 1);
                    final int x3 = 1 * x;
                    final List<String> curves = new ArrayList<String>(this.points.keySet());
                    if (curves.remove("Best solution")) {
                        curves.add("Best solution");
                    }
                    for (final String curve : curves) {
                        if (this.points.get(curve).size() > i) {
                            final double thisPoint = this.points.get(curve).get(i);
                            int y1 = this.getHeight() - (int)((lastPoint.get(curve) - this.minValue) / scale) - this.getHeight() / 10;
                            int y2 = this.getHeight() - (int)((thisPoint - this.minValue) / scale) - this.getHeight() / 10;
                            if (this.negative) {
                                y1 = (int)((lastPoint.get(curve) - this.minValue) / scale) + this.getHeight() / 10;
                                y2 = (int)((thisPoint - this.minValue) / scale) + this.getHeight() / 10;
                            }
                            g.setColor(this.colors.get(curve));
                            if (i != rsPos - 1 && i != rPos - 1) {
                                g.drawLine(x2, y1, x3, y2);
                            }
                            lastPoint.put(curve, thisPoint);
                        }
                    }
                    if (i == rsPos) {
                        if (rsNum > 0) {
                            g.setColor(Color.magenta);
                            g.drawLine(x3, 0, x3, this.getHeight());
                            g.drawString(" restart " + rsNum, x3, 10);
                        }
                        if (rsIt.hasNext()) {
                            ers = rsIt.next();
                            rsPos = ers.getValue();
                            rsNum = ers.getKey() % this.numRestart;
                        }
                    }
                    if (i == rPos) {
                        g.setColor(Color.cyan);
                        g.drawLine(x3, 0, x3, this.getHeight());
                        g.drawString(" rep " + rNum, x3, 10);
                        if (rIt.hasNext()) {
                            e2 = rIt.next();
                            rPos = e2.getValue();
                            rNum = e2.getKey();
                        }
                    }
                    ++x;
                }
                if (this.maxLine || this.minLine) {
                    g.setColor(Color.red);
                    final SortedMap<Integer, Integer> positions = this.restartsPositions.isEmpty() ? this.replicatesPositions : this.restartsPositions;
                    final double maxValueCurrent = this.restartsPositions.isEmpty() ? this.maxValueCurrentRep : this.maxValueCurrentRestart;
                    final Map<Integer, Double> rMaxima = this.restartsPositions.isEmpty() ? this.replicatesMaxima : this.restartsMaxima;
                    final double minValueCurrent = this.restartsPositions.isEmpty() ? this.minValueCurrentRep : this.minValueCurrentRestart;
                    final Map<Integer, Double> rMinima = this.restartsPositions.isEmpty() ? this.replicatesMinima : this.restartsMinima;
                    final Iterator<Integer> rid = positions.keySet().iterator();
                    int currentId = rid.hasNext() ? rid.next() : -1;
                    int x4 = (currentId < 0) ? 0 : positions.get(currentId);
                    for (int j = 0; j < positions.size(); ++j) {
                        final int nextId = rid.hasNext() ? rid.next() : -1;
                        final int x5 = (nextId < 0) ? (this.numPoints * 1) : positions.get(nextId);
                        if (this.maxLine) {
                            final double thisMax = (nextId < 0) ? maxValueCurrent : rMaxima.get(currentId);
                            int y3 = this.getHeight() - (int)((thisMax - this.minValue) / scale) - this.getHeight() / 10;
                            if (this.negative) {
                                y3 = (int)((thisMax - this.minValue) / scale) + this.getHeight() / 10;
                            }
                            final int y4 = y3;
                            g.drawLine(x4, y3, x5, y4);
                        }
                        if (this.minLine) {
                            final double thisMin = (nextId < 0) ? minValueCurrent : rMinima.get(currentId);
                            int y3 = this.getHeight() - (int)((thisMin - this.minValue) / scale) - this.getHeight() / 10;
                            if (this.negative) {
                                y3 = (int)((thisMin - this.minValue) / scale) + this.getHeight() / 10;
                            }
                            final int y4 = y3;
                            g.drawLine(x4, y3, x5, y4);
                        }
                        x4 = x5;
                        currentId = nextId;
                    }
                }
                if (this.xStep > 0) {
                    g.setColor(Color.cyan);
                    final SortedMap<Integer, Integer> positions = this.restartsPositions.isEmpty() ? this.replicatesPositions : this.restartsPositions;
                    final Iterator<Integer> rid2 = positions.keySet().iterator();
                    int currentId2 = rid2.hasNext() ? rid2.next() : -1;
                    int x6 = (currentId2 < 0) ? 0 : positions.get(currentId2);
                    final int yAxis = this.getHeight() - 20;
                    for (int k = 0; k < positions.size(); ++k) {
                        final int nextId2 = rid2.hasNext() ? rid2.next() : -1;
                        final int x7 = (nextId2 < 0) ? (this.numPoints * 1) : positions.get(nextId2);
                        for (int offset = this.xStep; x6 + offset < x7 - 1; offset += this.xStep) {
                            g.drawLine(x6 + offset, yAxis - 1, x6 + offset, yAxis + 1);
                            final String s = new StringBuilder().append(offset).toString();
                            final int sl = (int)g.getFontMetrics().getStringBounds(s, g).getWidth() / 2;
                            final int sh = (int)g.getFontMetrics().getStringBounds(s, g).getHeight();
                            g.drawString(s, x6 + offset - sl, yAxis + 2 + sh);
                        }
                        x6 = x7;
                        currentId2 = nextId2;
                    }
                    g.drawLine(0, yAxis, x6, yAxis);
                }
                if (this.yAxis != null) {
                    this.yAxis.setValues(this.minValue, scale);
                }
            }
            catch (Exception ex) {}
        }
    }
}
