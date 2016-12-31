// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.optimization;

import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.exceptions.NullAncestorException;
import javax.swing.JDialog;
import metapiga.trees.exceptions.BranchNotFoundException;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import metapiga.parameters.Parameters;
import java.util.Set;
import metapiga.trees.Branch;
import metapiga.modelization.Charset;
import java.util.List;
import metapiga.trees.Tree;
import metapiga.RateParameter;

public class Powell implements Optimizer
{
    private static final int POWELL_ITMAX = 200;
    private static final double GOLD_NUMBER = 1.618034;
    private static final double GOLD_LIMIT = 100.0;
    private static final double GOLD_TINY = 1.0E-20;
    private static final int BRENT_ITMAX = 100;
    private static final double BRENT_CGOLD = 0.381966;
    private static final double BRENT_ZEPS = 1.0E-10;
    private static final double BRENT_TOL = 1.0E-4;
    private final Tree T;
    private final List<Charset> partitions;
    private final int nPart;
    private final List<Branch> branches;
    private final int N;
    private final int nBranch;
    private final int nParam;
    private final int nGamma;
    private final int nPinv;
    private final double[] p;
    private final double[] pcom;
    private final double[] xicom;
    private final double[][] xi;
    
    public Powell(final Tree tree, final List<Branch> branchesToOptimize, final Set<Parameters.OptimizationTarget> targetsToOptimize) throws BranchNotFoundException {
        this.branches = new ArrayList<Branch>();
        this.T = tree.clone();
        this.partitions = this.T.getPartitions();
        this.nPart = this.partitions.size();
        for (final Branch b : branchesToOptimize) {
            this.branches.add(this.T.getBranch(this.T.getNode(b.getNode().getLabel()), this.T.getNode(b.getOtherNode().getLabel())));
        }
        this.nBranch = this.branches.size();
        if (targetsToOptimize.contains(Parameters.OptimizationTarget.R)) {
            this.nParam = this.T.getEvaluationModel().getNumRateParameters() * this.nPart;
        }
        else {
            this.nParam = 0;
        }
        this.nGamma = (targetsToOptimize.contains(Parameters.OptimizationTarget.GAMMA) ? this.nPart : 0);
        this.nPinv = (targetsToOptimize.contains(Parameters.OptimizationTarget.PINV) ? this.nPart : 0);
        this.N = this.nBranch + this.nParam + this.nGamma + this.nPinv;
        this.xi = new double[this.N][this.N];
        for (int i = 0; i < this.N; ++i) {
            Arrays.fill(this.xi[i], 0.0);
            this.xi[i][i] = 1.0;
        }
        this.p = new double[this.N];
        this.pcom = new double[this.N];
        this.xicom = new double[this.N];
        int k;
        for (k = 0; k < this.nBranch; ++k) {
            this.p[k] = this.branches.get(k).getLength();
        }
        for (final Charset part : this.partitions) {
            if (this.nParam > 0) {
                final Map<RateParameter, Double> rateParam = this.T.getEvaluationRateParameters(part);
                switch (this.T.getEvaluationModel()) {
                    case GTR: {
                        this.p[k++] = rateParam.get(RateParameter.A);
                        this.p[k++] = rateParam.get(RateParameter.B);
                        this.p[k++] = rateParam.get(RateParameter.C);
                        this.p[k++] = rateParam.get(RateParameter.D);
                        this.p[k++] = rateParam.get(RateParameter.E);
                        break;
                    }
                    case TN93: {
                        this.p[k++] = rateParam.get(RateParameter.K1);
                        this.p[k++] = rateParam.get(RateParameter.K2);
                        break;
                    }
                    case HKY85: {
                        this.p[k++] = rateParam.get(RateParameter.K);
                        break;
                    }
                    case K2P: {
                        this.p[k++] = rateParam.get(RateParameter.K);
                        break;
                    }
                }
            }
            if (this.nGamma > 0) {
                this.p[k++] = this.T.getEvaluationGammaShape(part);
            }
            if (this.nPinv > 0) {
                this.p[k++] = this.T.getEvaluationPInv(part);
            }
        }
    }
    
    @Override
    public Tree getOptimizedTreeWithProgress(final JDialog owner, final String title, final int idBar, final int maxBar) throws NullAncestorException, UnrootableTreeException {
        return null;
    }
    
    @Override
    public Tree getOptimizedTreeWithProgress(final JDialog owner, final String title) throws NullAncestorException, UnrootableTreeException {
        return this.getOptimizedTreeWithProgress(owner, title, 0, 1);
    }
    
    @Override
    public Tree getOptimizedTree() throws NullAncestorException, UnrootableTreeException {
        this.powell(1.0E-10);
        this.updatedLikelihood(this.p);
        return this.T;
    }
    
    @Override
    public void stop() {
    }
    
    private double updatedLikelihood(final double[] newParameters) throws NullAncestorException, UnrootableTreeException {
        int k;
        for (k = 0; k < this.nBranch; ++k) {
            this.branches.get(k).setLength(newParameters[k]);
            this.T.markNodeToReEvaluate(this.branches.get(k).getNode());
        }
        for (final Charset part : this.partitions) {
            if (this.nParam > 0) {
                switch (this.T.getEvaluationModel()) {
                    case GTR: {
                        this.T.setEvaluationRateParameter(part, RateParameter.A, newParameters[k++]);
                        this.T.setEvaluationRateParameter(part, RateParameter.B, newParameters[k++]);
                        this.T.setEvaluationRateParameter(part, RateParameter.C, newParameters[k++]);
                        this.T.setEvaluationRateParameter(part, RateParameter.D, newParameters[k++]);
                        this.T.setEvaluationRateParameter(part, RateParameter.E, newParameters[k++]);
                        break;
                    }
                    case TN93: {
                        this.T.setEvaluationRateParameter(part, RateParameter.K1, newParameters[k++]);
                        this.T.setEvaluationRateParameter(part, RateParameter.K2, newParameters[k++]);
                        break;
                    }
                    case HKY85: {
                        this.T.setEvaluationRateParameter(part, RateParameter.K, newParameters[k++]);
                        break;
                    }
                    case K2P: {
                        this.T.setEvaluationRateParameter(part, RateParameter.K, newParameters[k++]);
                        break;
                    }
                }
            }
            if (this.nGamma > 0) {
                this.T.setEvaluationDistributionShape(part, newParameters[k++]);
            }
            if (this.nPinv > 0) {
                this.T.setEvaluationPInv(part, newParameters[k++]);
            }
        }
        return this.T.getEvaluation();
    }
    
    private void powell(final double ftol) throws NullAncestorException, UnrootableTreeException {
        final double[] pt = new double[this.N];
        final double[] ptt = new double[this.N];
        final double[] xit = new double[this.N];
        final double[] pabs = new double[this.N];
        double fret = this.T.getEvaluation();
        System.arraycopy(this.p, 0, pt, 0, this.N);
        int iter = 0;
        while (true) {
            final double fp = fret;
            int ibig = 0;
            double del = 0.0;
            for (int i = 0; i < this.N; ++i) {
                for (int j = 0; j < this.N; ++j) {
                    xit[j] = this.xi[j][i];
                }
                final double fptt = fret;
                fret = this.linmin(xit);
                if (fptt - fret > del) {
                    del = Math.abs(fptt - fret);
                    ibig = i;
                }
            }
            if (2.0 * (fp - fret) <= ftol * (Math.abs(fp) + Math.abs(fret))) {
                break;
            }
            if (iter == 200) {
                System.out.println("powell exceeding maximum iterations");
            }
            for (int k = 0; k < this.N; ++k) {
                ptt[k] = 2.0 * this.p[k] - pt[k];
                xit[k] = this.p[k] - pt[k];
                pt[k] = this.p[k];
            }
            for (int k = 0; k < this.N; ++k) {
                pabs[k] = Math.abs(ptt[k]);
            }
            final double fptt = this.updatedLikelihood(pabs);
            if (fptt < fp) {
                final double t = 2.0 * (fp - 2.0 * fret + fptt) * Math.pow(fp - fret - del, 2.0) - del * Math.pow(fp - fptt, 2.0);
                if (t < 0.0) {
                    fret = this.linmin(xit);
                    for (int k = 0; k < this.N; ++k) {
                        this.xi[k][ibig] = this.xi[k][this.N - 1];
                        this.xi[k][this.N - 1] = xit[k];
                    }
                }
            }
            ++iter;
        }
        for (int l = 0; l < this.N; ++l) {
            this.p[l] = Math.abs(this.p[l]);
        }
    }
    
    private double linmin(final double[] xit) throws NullAncestorException, UnrootableTreeException {
        System.arraycopy(this.p, 0, this.pcom, 0, this.N);
        System.arraycopy(xit, 0, this.xicom, 0, this.N);
        final double[] brent = this.brent(this.mnbrak2());
        final double xmin = brent[0];
        for (int i = 0; i < this.N; ++i) {
            xit[i] *= xmin;
            this.p[i] += xit[i];
        }
        return brent[1];
    }
    
    private double f1dim(final double x) throws NullAncestorException, UnrootableTreeException {
        final double[] xt = new double[this.N];
        final double[] pabs = new double[this.N];
        for (int j = 0; j < this.N; ++j) {
            xt[j] = this.pcom[j] + x * this.xicom[j];
        }
        for (int j = 0; j < this.N; ++j) {
            pabs[j] = Math.abs(xt[j]);
        }
        return this.updatedLikelihood(pabs);
    }
    
    private double[] mnbrak2() throws NullAncestorException, UnrootableTreeException {
        double ax = 0.0;
        double bx = 1.0;
        double fa = this.f1dim(ax);
        double fb = this.f1dim(bx);
        if (fb > fa) {
            double dum = ax;
            ax = bx;
            bx = dum;
            dum = fb;
            fb = fa;
            fa = dum;
        }
        double cx = bx + 1.618034 * (bx - ax);
        double fu;
        for (double fc = this.f1dim(cx); fb > fc; fb = fc, fc = fu) {
            final double r = (bx - ax) * (fb - fc);
            final double q = (bx - cx) * (fb - fa);
            double u = bx - ((bx - cx) * q - (bx - ax) * r) / (2.0 * this.sign(Math.max(Math.abs(q - r), 1.0E-20), q - r));
            final double ulim = bx + 100.0 * (cx - bx);
            if ((bx - u) * (u - cx) > 0.0) {
                fu = this.f1dim(u);
                if (fu < fc) {
                    ax = bx;
                    fa = fb;
                    bx = u;
                    fb = fu;
                    return new double[] { ax, bx, cx };
                }
                if (fu > fb) {
                    cx = u;
                    fc = fu;
                    return new double[] { ax, bx, cx };
                }
                u = cx + 1.618034 * (cx - bx);
                fu = this.f1dim(u);
            }
            else if ((cx - u) * (u - ulim) > 0.0) {
                fu = this.f1dim(u);
                if (fu < fc) {
                    bx = cx;
                    cx = u;
                    u = cx + 1.618034 * (cx - bx);
                    fb = fc;
                    fc = fu;
                    fu = this.f1dim(u);
                }
            }
            else if ((u - ulim) * (ulim - cx) >= 0.0) {
                u = ulim;
                fu = this.f1dim(u);
            }
            else {
                u = cx + 1.618034 * (cx - bx);
                fu = this.f1dim(u);
            }
            ax = bx;
            bx = cx;
            cx = u;
            fa = fb;
        }
        return new double[] { ax, bx, cx };
    }
    
    private double[] brent(final double[] xs) throws NullAncestorException, UnrootableTreeException {
        final double ax = xs[0];
        final double bx = xs[1];
        final double cx = xs[2];
        double a = Math.min(ax, cx);
        double b = Math.max(ax, cx);
        double w;
        double x;
        double v = x = (w = bx);
        double e = 0.0;
        double fv;
        double fw;
        double fx = fw = (fv = this.f1dim(x));
        double d = 0.0;
        for (int iter = 0; iter < 100; ++iter) {
            final double xm = 0.5 * (a + b);
            final double tol1 = 1.0E-4 * Math.abs(x) + 1.0E-10;
            final double tol2 = 2.0 * tol1;
            if (Math.abs(x - xm) <= tol2 - 0.5 * (b - a)) {
                return new double[] { x, fx };
            }
            if (Math.abs(e) > tol1) {
                final double r = (x - w) * (fx - fv);
                double q = (x - v) * (fx - fw);
                double p = (x - v) * q - (x - w) * r;
                q = 2.0 * (q - r);
                if (q > 0.0) {
                    p = -p;
                }
                q = Math.abs(q);
                final double etemp = e;
                e = d;
                if (Math.abs(p) >= Math.abs(0.5 * q * etemp) || p <= q * (a - x) || p >= q * (b - x)) {
                    if (x >= xm) {
                        e = a - x;
                    }
                    else {
                        e = b - x;
                    }
                    d = 0.381966 * e;
                }
                else {
                    d = p / q;
                    final double u = x + d;
                    if (u - a < tol2 || b - u < tol2) {
                        d = this.sign(tol1, xm - x);
                    }
                }
            }
            double u;
            if (Math.abs(d) >= tol1) {
                u = x + d;
            }
            else {
                u = x + this.sign(tol1, d);
            }
            final double fu = this.f1dim(u);
            if (fu <= fx) {
                if (u >= x) {
                    a = x;
                }
                else {
                    b = x;
                }
                v = w;
                fv = fw;
                w = x;
                fw = fx;
                x = u;
                fx = fu;
            }
            else {
                if (u < x) {
                    a = u;
                }
                else {
                    b = u;
                }
                if (fu <= fw || w == x) {
                    v = w;
                    fv = fw;
                    w = u;
                    fw = fu;
                }
                else if (fu <= fv || v == x || v == w) {
                    v = u;
                    fv = fu;
                }
            }
        }
        return new double[] { x, fx };
    }
    
    private double sign(final double x, final double y) {
        if (y >= 0.0) {
            return Math.abs(x);
        }
        return -Math.abs(x);
    }
}
