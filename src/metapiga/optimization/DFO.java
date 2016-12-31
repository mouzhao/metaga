// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.optimization;

import java.awt.Point;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.Tree;
import javax.swing.JDialog;

public class DFO implements Optimizer
{
    private final int MXSPIT = 10;
    private final int GQTIT = 100;
    private final double ZERO = 0.0;
    private final double ONE = 1.0;
    private final double EPS = 1.0E-4;
    private final double TEN = 10.0;
    private final double TENTH = 0.1;
    private final double HUGE = 1.0E20;
    private final double THRVLD = 1.5;
    private final double ETA0 = 0.01;
    private final double ETA1 = 0.95;
    private final double GAMMathExp = 2.0;
    private final double GAMDIV = 1.75;
    private double MINRD;
    private double SMPIV;
    private double NFPMAX;
    private double GFDMAX;
    private double ERRMAX;
    private int NARCH;
    private int MXARCH;
    private int NF;
    private int MAXNF;
    private int NP;
    private int NIT;
    private int NFEVAL;
    
    public DFO() {
        this.ERRMAX = -1.0;
        final int NMAX = 100;
        final int N = 2;
        final int NBX = 0;
        final double[] X = new double[100];
        X[0] = 1.0;
        X[1] = 2.0;
        final double[] PAR = { -1.0, -1.0, 0.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0 };
        final int[] IPAR = { 1000, 1000, 0, 100, 0 };
        final int[] HESSTR = { 0 };
        final int LW = 160000;
        final double[] W = new double[160000];
        final double FX = this.UFN(N, X, 0);
        try {
            this.UDFO(N, NBX, X, FX, PAR, IPAR, HESSTR, 160000, W);
            System.out.println("Optimum  = " + this.UFN(N, X, 0) + " in (" + X[0] + "," + X[1] + ") found in " + this.NIT + " iteration and " + this.NFEVAL + " function evaluations");
        }
        catch (OptimizationError oe) {
            oe.printStackTrace();
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
    public void stop() {
    }
    
    @Override
    public Tree getOptimizedTree() throws NullAncestorException, UnrootableTreeException {
        return null;
    }
    
    private void UDFO(final int N, final int NBX, final double[] X, double FX, final double[] PAR, final int[] IPAR, final int[] HESSTR, final int LW, final double[] W) throws OptimizationError {
        int LYS = -1;
        final char[] ITINFO = new char[3];
        double DELTA = PAR[0];
        if (DELTA < 0.0) {
            DELTA = 1.0;
        }
        double GXTOL = PAR[1];
        if (GXTOL < 0.0) {
            GXTOL = 0.001;
        }
        this.MINRD = 0.010000000000000002 * GXTOL;
        double SNTOL = PAR[2];
        if (SNTOL < 0.0) {
            SNTOL = 1.0E-12;
        }
        double RTOL = PAR[6];
        double ATOL = PAR[8];
        if (RTOL < 0.0) {
            RTOL = 0.1;
        }
        if (ATOL < 0.0) {
            ATOL = 0.1;
        }
        final double LWBND = PAR[8];
        double LWBTOL = Math.min(0.9999, PAR[9]);
        if (LWBTOL < 0.0) {
            LWBTOL = 1.0E-8;
        }
        double FXLOW;
        if (LWBND == 0.0) {
            FXLOW = LWBTOL;
        }
        else if (LWBND < 0.0) {
            FXLOW = (1.0 - LWBTOL) * LWBND;
        }
        else {
            FXLOW = (1.0 + LWBTOL) * LWBND;
        }
        this.MAXNF = IPAR[0];
        if (this.MAXNF < 0) {
            this.MAXNF = 1000;
        }
        int ITMAX = IPAR[1];
        if (ITMAX < 0) {
            ITMAX = 1000;
        }
        final int NP1 = N + 1;
        int NH = IPAR[2];
        char HTYPE;
        int LP;
        if (NH > 0) {
            if (NH > N * (N - 1) / 2) {
                throw new OptimizationError("Error: IPAR[2] = " + IPAR[2] + " EXCEEDS N!. \nThe number of nonzero entries specified by the user in the strictly lower triangular part of the objective Hessian (i.e. the number of pairs\tof indices in HESSTR) exceeds the size of this part (only possible if IPAR[2] > 0).");
            }
            HTYPE = 'S';
            LP = N + N + NH;
            for (int I = 0; I < NH + NH; I += 2) {
                final int IH = HESSTR[I];
                final int IS = HESSTR[I + 1];
                if (IH < 0 || IH > N || IS < 0 || IS > N) {
                    throw new OptimizationError("Error: THE " + I + "-TH PAIR IN HESSTR IS OUT OF RANGE!. \nThe position (in HESSTR) of a nonzero entry in the strictly lower triangular part of the Hessian has indices that are out of range (from 0 to N-1) (only possible if IPAR[2] > 0).");
                }
                if (IH < IS) {
                    throw new OptimizationError("Error: THE " + I + "-TH PAIR IN HESSTR IS IN THE UPPER TRIANGLE!. \nThe position of a nonzero entry in the strict lower triangular part of the Hessian corresponds to an entry in the upper triangular part (only possible if IPAR[2] > 0).");
                }
                for (int IW1 = 0; IW1 < I - 1; IW1 += 2) {
                    if (IH == HESSTR[IW1] && IS == HESSTR[IW1 + 1]) {
                        throw new OptimizationError("Error: THE " + I + "-TH PAIR IN HESSTR ALREADY OCCURS AS THE " + IW1 + "-TH PAIR!. \nThe position of a nonzero entry in the strict lower triangular part occurs more than once in HESSTR (only possible if IPAR[2] > 0).");
                    }
                }
            }
        }
        else if (NH < 0) {
            HTYPE = 'B';
            NH = -NH;
            LP = N + NH * N - NH * (NH - 1) / 2;
        }
        else {
            HTYPE = 'D';
            LP = NP1 * (N + 2) / 2 - 1;
        }
        this.NARCH = NBX;
        final double GAMLIM = Math.max(10, LP) * 1.75;
        final int MXNUNS = 3 * N;
        this.NARCH = Math.max(this.NARCH, 0);
        this.NF = 0;
        if (this.NARCH >= LW) {
            throw new OptimizationError("Error: LW TOO SHORT OF " + (LW - this.NARCH) + "!\nThe value of NBX exceeds LW.");
        }
        double DELREF = DELTA;
        final int IXSET = 1;
        final int IYSET = IXSET + LP * N;
        final int IMODEL = IYSET + LP;
        final int INFP = IMODEL + LP;
        final int IDIST = INFP + N * N + LP * (LP - N);
        final int IS = IDIST + LP;
        final int IH = IS + N;
        int IW1 = IH + N * N;
        final int IW2 = IW1 + N;
        final int IW3 = IW2 + N;
        final int IXARCH = IW3 + N * (N + 6);
        this.MXARCH = IXARCH + Math.max(this.NARCH, 20) * NP1;
        if (LW < this.MXARCH) {
            throw new OptimizationError("Error: LW TOO SHORT OF " + (this.MXARCH - LW) + "!\nThe double precision workspace is too small");
        }
        this.MXARCH = (LW - IXARCH) / NP1;
        final int IYARCH = IXARCH + this.MXARCH * N;
        if (this.NARCH > 0) {
            LYS = this.NARCH * N;
            double FXMIN = FX;
            int IXMIN = 0;
            for (int I = LYS; I < LYS + this.NARCH; ++I) {
                if (W[I] < FX) {
                    FXMIN = W[I];
                    IXMIN = I - LYS + 1;
                }
            }
            if (IXMIN > 0) {
                this.DSWAP(N, W, (IXMIN - 1) * N, 1, X, 0, 1);
                W[LYS + IXMIN - 1] = FX;
                FX = FXMIN;
            }
        }
        if (this.NARCH > LP) {
            this.NP = LP;
            double FXMAX = -1.0E20;
            int IXMAX = 0;
            if (LYS < 0) {
                throw new OptimizationError("LYS has not been initialized !");
            }
            for (int I = LYS; I < LYS + LP; ++I) {
                if (W[I] > FXMAX) {
                    FXMAX = W[I];
                    IXMAX = I - LYS + 1;
                }
            }
            double FXMIN = FXMAX;
            int IXMIN = 0;
            for (int I = LYS + LP; I < LYS + this.NARCH; ++I) {
                if (W[I] < FXMIN) {
                    FXMIN = W[I];
                    IXMIN = I - LYS + 1;
                }
            }
            while (IXMIN > 0) {
                this.DSWAP(N, W, (IXMAX - 1) * N, 1, W, (IXMIN - 1) * N, 1);
                W[LYS + IXMAX - 1] = FXMIN;
                W[LYS + IXMIN - 1] = FXMAX;
                FXMAX = -1.0E20;
                IXMAX = 0;
                for (int I = LYS; I < LYS + LP; ++I) {
                    if (W[I] > FXMAX) {
                        FXMAX = W[I];
                        IXMAX = I - LYS + 1;
                    }
                }
                FXMIN = FXMAX;
                IXMIN = 0;
                for (int I = LYS + LP; I < LYS + this.NARCH; ++I) {
                    if (W[I] < FXMIN) {
                        FXMIN = W[I];
                        IXMIN = I - LYS + 1;
                    }
                }
            }
            final int IXS = this.NARCH;
            this.NARCH = IXARCH / NP1;
            if (this.NARCH < IXS) {
                System.arraycopy(W, N * IXS, W, N * this.NARCH, this.NARCH);
            }
        }
        else {
            this.NP = this.NARCH;
        }
        if (this.NP <= 0) {
            LYS = N;
            double TMP = N;
            final double RHO = DELTA / Math.sqrt(TMP);
            for (int i = 0; i < N; ++i) {
                W[i] = X[i] + RHO;
            }
            ++this.NF;
            if (this.NF > this.MAXNF) {
                throw new OptimizationError("Error: TOO MANY FUNCTION CALLS! NF = " + this.MAXNF + "\nThe maximum number of function calls has been reached and optimization stopped");
            }
            W[NP1 - 1] = this.UFN(N, W, 0);
            this.NP = 1;
            this.NARCH = 1;
            if (W[NP1 - 1] < FX) {
                this.DSWAP(N, X, 0, 1, W, 0, 1);
                TMP = W[NP1 - 1];
                W[NP1 - 1] = FX;
                FX = TMP;
            }
        }
        int IXN = IXARCH;
        int IXS = 1 + this.NP * N;
        if (LYS < 0) {
            throw new OptimizationError("LYS has not been initialized !");
        }
        for (int j = 0; j < this.NARCH - this.NP; ++j) {
            System.arraycopy(W, IXS - 1, W, IXN - 1, N);
            W[IYARCH + j - 1] = W[LYS + j];
            IXN += N;
            IXS += N;
        }
        this.NARCH -= this.NP;
        if (this.NP == LP) {
            for (int j = 0; j < this.NP; ++j) {
                W[IYSET + j - 1] = W[LYS + j];
            }
        }
        else {
            for (int j = this.NP; j > 0; --j) {
                W[IYSET + j - 1] = W[LYS + j];
            }
        }
        double DMAX = this.UPDIST(N, W, IXSET - 1, X, W, IDIST - 1);
        boolean SUCCES = true;
        int NUNS = 0;
        double SNORM = DELTA;
        boolean VALID = false;
        double RADOK = 1.0E20;
        for (int IT = 1; IT <= ITMAX; ++IT) {
            if (FX <= FXLOW) {
                this.NIT = IT - 1;
                this.NFEVAL = this.NF;
                return;
            }
            ITINFO[0] = ' ';
            ITINFO[2] = (ITINFO[1] = ' ');
            if (SUCCES && this.NP >= N) {
                this.GETNFP(N, LP, W, IXSET - 1, W, IYSET - 1, 0, X, FX, W, IDIST - 1, DELTA, -1.0, W, INFP - 1, W, IXARCH - 1, W, IYARCH - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
            }
            else {
                this.GETNFP(N, LP, W, IXSET - 1, W, IYSET - 1, N - 1, X, FX, W, IDIST - 1, DELTA, -1.0, W, INFP - 1, W, IXARCH - 1, W, IYARCH - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
            }
            boolean doitagain;
            double PRERED;
            do {
                this.INTERP(N, LP, W, IXSET - 1, W, IYSET - 1, X, FX, W, IMODEL - 1, W, INFP - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
                double GNORM = this.DNRM2(N, W, IMODEL - 1, 1);
                double VRAD = Math.max(this.MINRD, Math.min(GXTOL, DELTA));
                if (GNORM <= GXTOL || (!VALID && NUNS > MXNUNS)) {
                    if (GNORM <= GXTOL) {
                        ITINFO[0] = 'V';
                    }
                    else {
                        ITINFO[0] = 'U';
                    }
                    do {
                        doitagain = false;
                        this.GETNFP(N, LP, W, IXSET - 1, W, IYSET - 1, LP, X, FX, W, IDIST - 1, VRAD, 1.5, W, INFP - 1, W, IXARCH - 1, W, IYARCH - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
                        this.INTERP(N, LP, W, IXSET - 1, W, IYSET - 1, X, FX, W, IMODEL - 1, W, INFP - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
                        VALID = true;
                        RADOK = VRAD;
                        GNORM = this.DNRM2(N, W, IMODEL - 1, 1);
                        if (GNORM <= GXTOL) {
                            this.NIT = IT - 1;
                            this.NFEVAL = this.NF;
                            return;
                        }
                        VRAD *= 0.1;
                        if (GNORM > VRAD) {
                            continue;
                        }
                        doitagain = true;
                    } while (doitagain);
                }
                this.MKHESS(N, W, IMODEL - 1, LP, W, IH - 1, HTYPE, NH, HESSTR);
                double LAMBDA = 0.0;
                final DGQToutput dgqt = this.DGQT(N, W, IH - 1, N, W, IMODEL - 1, DELTA, RTOL, ATOL, 10, LAMBDA, W, IS - 1, 100, W, IW1 - 1, W, IW2 - 1, W, IW3 - 1);
                LAMBDA = dgqt.PAR;
                PRERED = dgqt.F;
                PRERED = -PRERED;
                SNORM = this.DNRM2(N, W, IS - 1, 1);
                doitagain = false;
                if (this.ERRMAX < 0.0) {
                    throw new OptimizationError("ERRMAX has not been initialized !");
                }
                if ((this.ERRMAX < 0.1 * PRERED && SNORM > SNTOL) || VALID) {
                    continue;
                }
                doitagain = true;
                this.GETNFP(N, LP, W, IXSET - 1, W, IYSET - 1, LP, X, FX, W, IDIST - 1, DELTA, 0.0, W, INFP - 1, W, IXARCH - 1, W, IYARCH - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
                VALID = true;
                RADOK = DELTA;
                ITINFO[0] = 'R';
            } while (doitagain);
            if (SNORM <= SNTOL) {
                this.NIT = IT - 1;
                this.NFEVAL = this.NF;
                throw new OptimizationError("Error: STEP TOO SHORT: " + SNORM + "!\nThe length of the predicted step is too short, indicating that the algorithm stalls. Optimization has been terminated.");
            }
            System.arraycopy(X, 0, W, IW1 - 1, N);
            this.DAXPY(N, 1.0, W, IS - 1, 1, W, IW1 - 1, 1);
            System.arraycopy(X, 0, W, IS - 1, N);
            final double FXOLD = FX;
            ++this.NF;
            if (this.NF > this.MAXNF) {
                throw new OptimizationError("Error: TOO MANY FUNCTION CALLS! NF = " + this.MAXNF + "\nThe maximum number of function calls has been reached and optimization stopped");
            }
            double FXT = this.UFN(N, W, IW1 - 1);
            final double ARED = FXOLD - FXT;
            final double RHO = ARED / PRERED;
            SUCCES = (RHO >= 0.01);
            double RADIUS;
            if (SUCCES) {
                NUNS = 0;
                this.DSWAP(N, W, IW1 - 1, 1, X, 0, 1);
                final double TMP = FXT;
                FXT = FX;
                FX = TMP;
                DMAX = this.UPDIST(N, W, IXSET - 1, X, W, IDIST - 1);
                RADIUS = Math.min(SNORM, DELTA);
            }
            else {
                ++NUNS;
                if (VALID) {
                    RADIUS = Math.min(DELTA, SNORM) / 1.75;
                }
                else {
                    RADIUS = Math.min(SNORM, DELREF / GAMLIM);
                }
            }
            ITINFO[1] = this.TRYRPL(N, LP, W, IXSET - 1, W, IYSET - 1, W, IW1 - 1, FXT, X, FX, W, IDIST - 1, RADIUS, W, INFP - 1, W, IXARCH - 1, W, IYARCH - 1, HTYPE, NH, HESSTR, W, IW2 - 1);
            if (ITINFO[1] != '-') {
                this.GETNFP(N, LP, W, IXSET - 1, W, IYSET - 1, 0, X, FX, W, IDIST - 1, RADIUS, -1.0, W, INFP - 1, W, IXARCH - 1, W, IYARCH - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
            }
            else {
                this.ARCHIV(N, W, IW1 - 1, FXT, W, IXARCH - 1, W, IYARCH - 1);
                if (!VALID) {
                    ITINFO[1] = this.IMPRVE(N, LP, W, IXSET - 1, W, IYSET - 1, X, FX, W, IDIST - 1, RADIUS, W, INFP - 1, W, IXARCH - 1, W, IYARCH - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
                    if (this.NF > this.MAXNF) {
                        throw new OptimizationError("Error: TOO MANY FUNCTION CALLS! NF = " + this.MAXNF + "\nThe maximum number of function calls has been reached and optimization stopped");
                    }
                    if (ITINFO[1] != '-') {
                        this.GETNFP(N, LP, W, IXSET - 1, W, IYSET - 1, 0, X, FX, W, IDIST - 1, DELTA, -1.0, W, INFP - 1, W, IXARCH - 1, W, IYARCH - 1, HTYPE, NH, HESSTR, W, IW1 - 1);
                    }
                    else {
                        VALID = true;
                        RADOK = RADIUS;
                    }
                }
            }
            double FXMIN = FX;
            int IXMIN = 0;
            for (int I2 = 0; I2 < this.NP; ++I2) {
                final double TMP = W[IYSET + I2 - 1];
                if (TMP < FXMIN) {
                    FXMIN = TMP;
                    IXMIN = I2 + 1;
                }
            }
            double SNORMT = SNORM;
            if (FXMIN < FX) {
                final double AREDHT = FXOLD - FXMIN;
                final double RHOHAT = AREDHT / PRERED;
                SUCCES = (RHOHAT > 0.01);
                if (SUCCES) {
                    final int IM1 = IXMIN - 1;
                    this.DSWAP(N, W, IXSET + IM1 * N - 1, 1, X, 0, 1);
                    W[IYSET + IM1 - 1] = FX;
                    FX = FXMIN;
                    ITINFO[2] = 'I';
                    DMAX = this.UPDIST(N, W, IXSET - 1, X, W, IDIST - 1);
                    this.DAXPY(N, -1.0, X, 0, 1, W, IS - 1, 1);
                    SNORMT = this.DNRM2(N, W, IS - 1, 1);
                }
            }
            if (SUCCES || VALID) {
                DELREF = DELTA;
            }
            if (RHO >= 0.01) {
                if (RHO > 0.95) {
                    if (Math.abs(RHO - 1.0) <= 1.0E-5) {
                        DELTA = Math.max(SNORM * 100.0, DELTA);
                    }
                    else {
                        DELTA = Math.max(SNORM * 2.0, DELTA);
                    }
                }
            }
            else if (VALID) {
                DELTA = Math.min(DELTA, Math.max(SNORM, DMAX)) / 1.75;
            }
            else {
                DELTA = Math.max(Math.min(SNORM, DELTA), DELREF / GAMLIM);
            }
            VALID = (VALID && !SUCCES && RADOK <= DELTA);
            SNORM = SNORMT;
        }
        throw new OptimizationError("Error: TOO MANY ITERATIONS!\nThe maximum number of iterations has been reached and optimization stopped");
    }
    
    private double UFN(final int N, final double[] X, final int startX) {
        return Math.pow(1.0 - X[startX], 2.0) + 100.0 * Math.pow(X[1 + startX] - Math.pow(X[startX], 2.0), 2.0);
    }
    
    private void DSWAP(final int N, final double[] DX, final int STARTX, final int INCX, final double[] DY, final int STARTY, final int INCY) {
        if (N <= 0) {
            return;
        }
        if (INCX == 1 && INCY == 1) {
            final int M = N % 3;
            if (M != 0) {
                for (int I = 1; I <= M; ++I) {
                    final double DTEMP = DX[I - 1 + STARTX];
                    DX[I - 1 + STARTX] = DY[I - 1 + STARTX];
                    DY[I - 1 + STARTX] = DTEMP;
                }
                if (N < 3) {
                    return;
                }
            }
            int I;
            for (int MP1 = I = M + 1; I <= N; I += 3) {
                double DTEMP = DX[I - 1 + STARTX];
                DX[I - 1 + STARTX] = DY[I - 1 + STARTX];
                DY[I - 1 + STARTX] = DTEMP;
                DTEMP = DX[I + STARTX];
                DX[I + STARTX] = DY[I + STARTX];
                DY[I + STARTX] = DTEMP;
                DTEMP = DX[I + 1 + STARTX];
                DX[I + 1 + STARTX] = DY[I + 1 + STARTX];
                DY[I + 1 + STARTX] = DTEMP;
            }
        }
        else {
            int IX = 1;
            int IY = 1;
            if (INCX < 0) {
                IX = (-N + 1) * INCX + 1;
            }
            if (INCY < 0) {
                IY = (-N + 1) * INCY + 1;
            }
            for (int I = 1; I <= N; ++I) {
                final double DTEMP = DX[IX - 1 + STARTX];
                DX[IX - 1 + STARTX] = DY[IY - 1 + STARTY];
                DY[IY - 1 + STARTY] = DTEMP;
                IX += INCX;
                IY += INCY;
            }
        }
    }
    
    private double UPDIST(final int N, final double[] XSET, final int startXSET, final double[] XBASE, final double[] DIST, final int startDist) {
        double DMAX = 0.0;
        for (int i = 0; i < this.NP; ++i) {
            final int IM1 = i;
            double NRM2 = 0.0;
            for (int j = 0; j < N; ++j) {
                NRM2 += Math.pow(XSET[startXSET + IM1 * N + j] - XBASE[j], 2.0);
            }
            NRM2 = Math.sqrt(NRM2);
            DIST[startDist + i] = NRM2;
            DMAX = Math.max(DMAX, NRM2);
        }
        return DMAX;
    }
    
    private void GETNFP(final int N, final int LP, final double[] XSET, final int startXSET, final double[] YSET, final int startYSET, final int ADDPTS, final double[] XBASE, final double FXBASE, final double[] DIST, final int startDIST, final double RADIUS, final double CUTOFF, final double[] NFP, final int startNFP, final double[] XARCH, final int startXARCH, final double[] YARCH, final int startYARCH, final char HTYPE, final int NH, final int[] HESSTR, final double[] W, final int startW) {
        final int[] FIRST = new int[4];
        final double BIGPIV = 1.0E20;
        final double THRESH = 1.0E-15;
        final int NP1 = N + 1;
        final int NSQ = N * N;
        if (this.NP < 0) {
            System.out.println("ERROR (GETNFP): NEGATIVE NUMBER OF INTERPOLATION POINTS!");
            return;
        }
        if (LP < this.NP) {
            System.out.println("ERROR (GETNFP): DECLARED LENGTH OF A POLYNOMIAL TOO SMALL!");
            return;
        }
        FIRST[0] = 1;
        if (ADDPTS > 0) {
            FIRST[1] = NP1;
            FIRST[2] = LP + 1;
        }
        else {
            FIRST[1] = Math.min(this.NP, N) + 1;
            FIRST[2] = this.NP + 1;
        }
        int NXT = 1;
        int NPOLD = this.NP;
        if (CUTOFF >= 0.0) {
            final double TMP = CUTOFF * RADIUS;
            this.NP = 0;
            for (int IP = 1; IP <= NPOLD; ++IP) {
                final int IX = 1 + (IP - 1) * N;
                if (DIST[IP - 1 + startDIST] > TMP) {
                    this.ARCHIV(N, XSET, IX - 1 + startXSET, YSET[IP - 1 + startYSET], XARCH, startXARCH, YARCH, startYARCH);
                }
                else {
                    ++this.NP;
                    System.arraycopy(XSET, IX - 1 + startXSET, XSET, NXT - 1 + startXSET, N);
                    YSET[this.NP - 1 + startYSET] = YSET[IP - 1 + startYSET];
                    DIST[this.NP - 1 + startDIST] = DIST[IP - 1 + startDIST];
                    NXT += N;
                }
            }
            NPOLD = this.NP;
        }
        NXT = LP * LP - N * (LP - N);
        for (int IP = 1; IP <= NXT; ++IP) {
            NFP[IP - 1 + startNFP] = 0.0;
        }
        for (int IP = 1; IP <= NSQ; IP += NP1) {
            NFP[IP - 1 + startNFP] = 1.0;
        }
        for (int IP = NSQ + N + 1; IP <= NXT; IP += LP + 1) {
            NFP[IP - 1 + startNFP] = 1.0;
        }
        int NEW = 0;
        this.NP = 0;
        NXT = 1;
        int NXTX = 1;
        this.SMPIV = 1.0E20;
        for (int IB = 1; IB <= 2; ++IB) {
            for (int IP = FIRST[IB - 1]; IP <= FIRST[IB] - 1; ++IP) {
                final Point FL = this.NFPDAT(N, IP, LP);
                final int JP = FL.x;
                final int LIP = FL.y;
                double TOLPIV = 0.0;
                int IPIV = NXT;
                double VALPIV = 0.0;
                for (int IX = NXT; IX <= NPOLD; ++IX) {
                    final int JX = 1 + (IX - 1) * N;
                    System.arraycopy(XSET, JX - 1 + startXSET, W, startW, N);
                    this.DAXPY(N, -1.0, XBASE, 0, 1, W, startW, 1);
                    final double VAL = this.VALP(N, W, startW, NFP, startNFP + JP - 1, LIP, HTYPE, NH, HESSTR);
                    final double ABSVAL = Math.abs(VAL);
                    if (ABSVAL > TOLPIV) {
                        IPIV = IX;
                        TOLPIV = ABSVAL;
                        VALPIV = VAL;
                    }
                }
                if (TOLPIV >= 1.0E-15) {
                    if (IPIV > NXT) {
                        this.DSWAP(N, XSET, startXSET + (IPIV - 1) * N, 1, XSET, NXTX - 1 + startXSET, 1);
                        final double VAL = YSET[NXT - 1 + startYSET];
                        YSET[NXT - 1 + startYSET] = YSET[IPIV - 1 + startYSET];
                        YSET[IPIV - 1 + startYSET] = VAL;
                    }
                    System.arraycopy(XSET, NXTX - 1 + startXSET, W, startW, N);
                    this.DAXPY(N, -1.0, XBASE, 0, 1, W, startW, 1);
                }
                else {
                    if (NEW == 0) {
                        for (int IX = NXT; IX <= this.NP; ++IX) {
                            this.ARCHIV(N, XSET, startXSET + (IX - 1) * N, YSET[IX - 1 + startYSET], XARCH, startXARCH, YARCH, startYARCH);
                        }
                    }
                    if (NEW >= ADDPTS) {
                        return;
                    }
                    ++NEW;
                    VALPIV = this.MAXABS(N, RADIUS, NFP, JP - 1, LIP, XSET, NXTX - 1, HTYPE, NH, HESSTR, W, 0);
                    TOLPIV = Math.abs(VALPIV);
                    if (TOLPIV < 1.0E-15) {
                        return;
                    }
                    System.arraycopy(XSET, NXTX - 1 + startXSET, W, startW, N);
                    this.DAXPY(N, 1.0, XBASE, 0, 1, XSET, NXTX - 1 + startXSET, 1);
                    DIST[NXT - 1 + startDIST] = this.DNRM2(N, W, startW, 1);
                    ++this.NF;
                    if (this.NF > this.MAXNF) {
                        return;
                    }
                    YSET[NXT - 1 + startYSET] = this.UFN(N, XSET, NXTX - 1 + startXSET);
                }
                if (TOLPIV < this.SMPIV) {
                    this.SMPIV = TOLPIV;
                }
                this.DSCAL(LIP, 1.0 / VALPIV, NFP, JP - 1 + startNFP, 1);
                for (int KP = FIRST[IB - 1]; KP <= LP; ++KP) {
                    if (KP != IP) {
                        final Point point = this.NFPDAT(N, KP, LP);
                        final int KX = point.x;
                        final int LKP = point.y;
                        final double TMP = this.VALP(N, W, startW, NFP, KX - 1 + startNFP, LKP, HTYPE, NH, HESSTR);
                        this.DAXPY(LIP, -TMP, NFP, JP - 1 + startNFP, 1, NFP, KX - 1 + startNFP, 1);
                    }
                }
                this.NP = NXT;
                ++NXT;
                NXTX += N;
            }
        }
    }
    
    private void INTERP(final int N, final int LP, final double[] XSET, final int startXSET, final double[] YSET, final int startYSET, final double[] XBASE, final double FXBASE, final double[] POL, final int startPOL, final double[] NFP, final int startNFP, final char HTYPE, final int NH, final int[] HESSTR, final double[] W, final int startW) {
        final int[] FIRST = new int[4];
        final int NPP1 = this.NP + 1;
        FIRST[FIRST[0] = 1] = N + 1;
        FIRST[2] = NPP1;
        int NB;
        if (this.NP >= FIRST[1]) {
            NB = 2;
        }
        else {
            NB = 1;
        }
        for (int IP = 1; IP <= this.NP; ++IP) {
            W[IP - 1 + startW] = YSET[IP - 1 + startW] - FXBASE;
        }
        this.NFPMAX = 0.0;
        this.GFDMAX = 0.0;
        for (int IB = 2; IB <= NB; ++IB) {
            final int IBP1 = IB + 1;
            final int ISTR = FIRST[IB];
            final int IEND = FIRST[IB - 1] - 1;
            int NXTI = 1 + IEND * N;
            for (int IP = FIRST[IB - 1]; IP <= this.NP; ++IP) {
                System.arraycopy(XSET, NXTI - 1 + startXSET, W, NPP1 - 1 + startW, N);
                this.DAXPY(N, -1.0, XBASE, 0, 1, W, NPP1 - 1 + startW, 1);
                NXTI += N;
                for (int KP = ISTR; KP <= IEND; ++KP) {
                    final Point point = this.NFPDAT(N, KP, LP);
                    final int NXTK = point.x;
                    final int LKP = point.y;
                    final double VAL = this.VALP(N, W, NPP1 - 1 + startW, NFP, NXTK - 1 + startNFP, LKP, HTYPE, NH, HESSTR);
                    W[IP - 1 + startW] -= W[KP - 1 + startW] * VAL;
                    if (IP >= FIRST[IB - 1] && IP < FIRST[IBP1 - 1]) {
                        this.NFPMAX = Math.max(this.NFPMAX, Math.abs(VAL));
                    }
                }
                this.GFDMAX = Math.max(Math.abs(W[IP - 1 + startW]), this.GFDMAX);
            }
        }
        for (int IP = 1; IP <= LP; ++IP) {
            POL[IP - 1 + startPOL] = 0.0;
        }
        for (int IP = 1; IP <= this.NP; ++IP) {
            final Point p = this.NFPDAT(N, IP, LP);
            final int NXTI = p.x;
            final int LIP = p.y;
            this.DAXPY(LIP, W[IP - 1 + startW], NFP, NXTI - 1 + startNFP, 1, POL, startPOL, 1);
        }
        this.ERRMAX = 0.0;
        for (int IP = 1; IP <= this.NP; ++IP) {
            System.arraycopy(XSET, startXSET + (IP - 1) * N, W, NPP1 - 1 + startW, N);
            this.DAXPY(N, -1.0, XBASE, 0, 1, W, NPP1 - 1 + startW, 1);
            this.ERRMAX = Math.max(this.ERRMAX, Math.abs(YSET[IP - 1] - FXBASE - this.VALP(N, W, NPP1 - 1 + startW, POL, startPOL, LP, HTYPE, NH, HESSTR)));
        }
    }
    
    private double DNRM2(final int N, final double[] X, final int startX, final int INCX) {
        double NORM;
        if (N < 1 || INCX < 1) {
            NORM = 0.0;
        }
        else if (N == 1) {
            NORM = Math.abs(X[startX]);
        }
        else {
            double SCALE = 0.0;
            double SSQ = 1.0;
            for (int IX = 1; IX <= 1 + (N - 1) * INCX; IX += INCX) {
                if (X[IX - 1 + startX] != 0.0) {
                    final double ABSXI = Math.abs(X[IX - 1 + startX]);
                    if (SCALE < ABSXI) {
                        SSQ = 1.0 + SSQ * Math.pow(SCALE / ABSXI, 2.0);
                        SCALE = ABSXI;
                    }
                    else {
                        SSQ += Math.pow(ABSXI / SCALE, 2.0);
                    }
                }
            }
            NORM = SCALE * Math.sqrt(SSQ);
        }
        return NORM;
    }
    
    private void MKHESS(final int N, final double[] MODEL, final int startMODEL, final int LENGTH, final double[] HSSIAN, final int startHSSIAN, final char HTYPE, final int NH, final int[] HESSTR) {
        if (LENGTH > N) {
            final int NP1 = N + 1;
            int NXTR = 1;
            int NXTM = NP1;
            for (int I = 1; I <= N; ++I) {
                HSSIAN[NXTR - 1 + startHSSIAN] = MODEL[NXTM - 1 + startMODEL];
                ++NXTM;
                NXTR += NP1;
            }
            if (HTYPE == 'D') {
                for (int I = 2; I <= N; ++I) {
                    int NXTC = I;
                    NXTR = 1 + (I - 1) * N;
                    for (int J = I; J <= N; ++J) {
                        final double TMP = MODEL[NXTM - 1 + startMODEL];
                        HSSIAN[NXTC - 1 + startHSSIAN] = (HSSIAN[NXTR - 1 + startHSSIAN] = TMP);
                        ++NXTM;
                        NXTC += NP1;
                        NXTR += NP1;
                    }
                }
            }
            else if (HTYPE == 'B') {
                for (int I = 2; I <= NH; ++I) {
                    int NXTC = I;
                    NXTR = 1 + (I - 1) * N;
                    for (int J = I; J <= N; ++J) {
                        final double TMP = MODEL[NXTM - 1 + startMODEL];
                        HSSIAN[NXTC - 1 + startHSSIAN] = (HSSIAN[NXTR - 1 + startHSSIAN] = TMP);
                        ++NXTM;
                        NXTC += NP1;
                        NXTR += NP1;
                    }
                }
                for (int I = NH + 1; I <= N; ++I) {
                    int NXTC = I;
                    NXTR = 1 + (I - 1) * N;
                    for (int J = I; J <= N; ++J) {
                        HSSIAN[NXTC - 1 + startHSSIAN] = (HSSIAN[NXTR - 1 + startHSSIAN] = 0.0);
                        NXTC += NP1;
                        NXTR += NP1;
                    }
                }
            }
            else {
                NXTR = 1;
                for (int I = 1; I <= N; ++I) {
                    for (int J = 1; J <= N; ++J) {
                        if (I != J) {
                            HSSIAN[NXTR - 1 + startHSSIAN] = 0.0;
                        }
                        ++NXTR;
                    }
                }
                for (int I = 1; I <= NH + NH; I += 2) {
                    NXTR = HESSTR[I - 1];
                    final int NXTC = HESSTR[I];
                    final double TMP = MODEL[NXTM - 1 + startMODEL];
                    HSSIAN[N * (NXTR - 1) + NXTC - 1 + startHSSIAN] = (HSSIAN[N * (NXTC - 1) + NXTR - 1 + startHSSIAN] = TMP);
                    ++NXTM;
                }
            }
        }
        else {
            for (int I = 1; I <= N * N; ++I) {
                HSSIAN[I - 1 + startHSSIAN] = 0.0;
            }
        }
    }
    
    private void DCOPY(final int N, final double[] DX, final int startDX, final int INCX, final double[] DY, final int startDY, final int INCY) {
        if (N <= 0) {
            return;
        }
        if (INCX == 1 && INCY == 1) {
            System.arraycopy(DX, startDX, DY, startDY, N);
        }
        else {
            int IX = 1;
            int IY = 1;
            if (INCX < 0) {
                IX = (-N + 1) * INCX + 1;
            }
            if (INCY < 0) {
                IY = (-N + 1) * INCY + 1;
            }
            for (int I = 1; I <= N; ++I) {
                DY[IY - 1 + startDY] = DX[IX - 1 + startDX];
                IX += INCX;
                IY += INCY;
            }
        }
    }
    
    private DGQToutput DGQT(final int N, final double[] A, final int startA, final int LDA, final double[] B, final int startB, final double DELTA, final double RTOL, final double ATOL, final int ITMAX, double PAR, final double[] X, final int startX, int ITER, final double[] Z, final int startZ, final double[] WA1, final int startWA1, final double[] WA2, final int startWA2) {
        final double P001 = 0.001;
        final double P2 = 0.5;
        double ALPHA = 0.0;
        double RZNORM = 0.0;
        double F = 0.0;
        double PARF = 0.0;
        double XNORM = 0.0;
        double RXNORM = 0.0;
        boolean REDNC = false;
        final DGQToutput output = new DGQToutput();
        for (int J = 1; J <= N; ++J) {
            Z[J - 1 + startZ] = (X[J - 1 + startX] = 0.0);
        }
        this.DCOPY(N, A, startA, LDA + 1, WA1, startWA1, 1);
        for (int J = 1; J <= N - 1; ++J) {
            this.DCOPY(N - J, A, J * LDA + J - 1 + startA, LDA, A, (J - 1) * LDA + J + startA, 1);
        }
        double ANORM = 0.0;
        for (int J = 1; J <= N; ++J) {
            WA2[J - 1 + startWA2] = this.DASUM(N, A, (J - 1) * LDA + startA, 1);
            ANORM = Math.max(ANORM, WA2[J - 1 + startWA2]);
        }
        for (int J = 1; J <= N; ++J) {
            WA2[J - 1 + startWA2] -= Math.abs(WA1[J - 1 + startWA1]);
        }
        final double BNORM = this.DNRM2(N, B, startB, 1);
        double PARS = -ANORM;
        double PARL = -ANORM;
        double PARU = -ANORM;
        for (int J = 1; J <= N; ++J) {
            PARS = Math.max(PARS, -WA1[J - 1 + startWA1]);
            PARL = Math.max(PARL, WA1[J - 1 + startWA1] + WA2[J - 1 + startWA2]);
            PARU = Math.max(PARU, -WA1[J - 1 + startWA1] + WA2[J - 1 + startWA2]);
        }
        PARL = Math.max(Math.max(0.0, BNORM / DELTA - PARL), PARS);
        PARU = Math.max(0.0, BNORM / DELTA + PARU);
        PAR = Math.max(PAR, PARL);
        PAR = Math.min(PAR, PARU);
        PARU = Math.max(PARU, (1.0 + RTOL) * PARL);
        int INFO = 0;
        int J;
        int INDEF;
        double PROD;
        double TEMP;
        double PARC;
        for (ITER = 1; ITER <= ITMAX; ++ITER) {
            if (PAR <= PARS && PARU > 0.0) {
                PAR = Math.max(0.001, Math.sqrt(PARL / PARU)) * PARU;
            }
            for (J = 1; J <= N - 1; ++J) {
                this.DCOPY(N - J, A, (J - 1) * LDA + J + startA, 1, A, J * LDA + J - 1 + startA, LDA);
            }
            for (J = 1; J <= N; ++J) {
                A[(J - 1) * LDA + J - 1 + startA] = WA1[J - 1 + startWA1] + PAR;
            }
            INDEF = this.DPOTRF('U', N, A, startA, LDA);
            if (INDEF == 0) {
                PARF = PAR;
                this.DCOPY(N, B, startB, 1, WA2, startWA2, 1);
                this.DTRSV('U', 'T', 'N', N, A, startA, LDA, WA2, startWA2, 1);
                RXNORM = this.DNRM2(N, WA2, startWA2, 1);
                this.DTRSV('U', 'N', 'N', N, A, startA, LDA, WA2, startWA2, 1);
                this.DCOPY(N, WA2, startWA2, 1, X, startX, 1);
                this.DSCAL(N, -1.0, X, startX, 1);
                XNORM = this.DNRM2(N, X, startX, 1);
                if (Math.abs(XNORM - DELTA) <= RTOL * DELTA || (PAR == 0.0 && XNORM <= (1.0 + RTOL) * DELTA)) {
                    INFO = 1;
                }
                RZNORM = this.DESTSV(N, A, startA, LDA, Z, startZ);
                PARS = Math.max(PARS, PAR - Math.pow(RZNORM, 2.0));
                REDNC = false;
                if (XNORM < DELTA) {
                    PROD = this.DDOT(N, Z, startZ, 1, X, startX, 1) / DELTA;
                    TEMP = (DELTA - XNORM) * ((DELTA + XNORM) / DELTA);
                    ALPHA = TEMP / (Math.abs(PROD) + Math.sqrt(Math.pow(PROD, 2.0) + TEMP / DELTA));
                    ALPHA = this.SIGN(ALPHA, PROD);
                    RZNORM *= Math.abs(ALPHA);
                    if (Math.pow(RZNORM / DELTA, 2.0) + PAR * Math.pow(XNORM / DELTA, 2.0) <= PAR) {
                        REDNC = true;
                    }
                    if (0.5 * Math.pow(RZNORM / DELTA, 2.0) <= RTOL * (1.0 - 0.5 * RTOL) * (PAR + Math.pow(RXNORM / DELTA, 2.0))) {
                        INFO = 1;
                    }
                    else if (0.5 * (PAR + Math.pow(RXNORM / DELTA, 2.0)) <= ATOL / DELTA / DELTA && INFO == 0) {
                        INFO = 2;
                    }
                    else if (XNORM == 0.0) {
                        INFO = 1;
                    }
                }
                if (XNORM == 0.0) {
                    PARC = -PAR;
                }
                else {
                    this.DCOPY(N, X, startX, 1, WA2, startWA2, 1);
                    TEMP = 1.0 / XNORM;
                    this.DSCAL(N, TEMP, WA2, startWA2, 1);
                    this.DTRSV('U', 'T', 'N', N, A, startA, LDA, WA2, startWA2, 1);
                    TEMP = this.DNRM2(N, WA2, startWA2, 1);
                    PARC = (XNORM - DELTA) / DELTA / TEMP / TEMP;
                }
                if (XNORM > DELTA) {
                    PARL = Math.max(PARL, PAR);
                }
                if (XNORM < DELTA) {
                    PARU = Math.min(PARU, PAR);
                }
            }
            else {
                if (INDEF > 1) {
                    this.DCOPY(INDEF - 1, A, INDEF - 1 + startA, LDA, A, (INDEF - 1) * LDA + startA, 1);
                    A[(INDEF - 1) * LDA + INDEF - 1 + startA] = WA1[INDEF - 1 + startWA1] + PAR;
                    this.DCOPY(INDEF - 1, A, INDEF - 1 + startA, 1, WA2, startWA2, 1);
                    this.DTRSV('U', 'T', 'N', INDEF - 1, A, startA, LDA, WA2, startWA2, 1);
                    A[(INDEF - 1) * LDA + INDEF - 1 + startA] -= Math.pow(this.DNRM2(INDEF - 1, WA2, startWA2, 1), 2.0);
                    this.DTRSV('U', 'N', 'N', INDEF - 1, A, startA, LDA, WA2, startWA2, 1);
                }
                WA2[INDEF - 1 + startWA2] = -1.0;
                TEMP = this.DNRM2(INDEF, WA2, startWA2, 1);
                PARC = -(A[(INDEF - 1) * LDA + INDEF - 1 + startA] / TEMP) / TEMP;
                PARS = Math.max(PARS, Math.max(PAR, PAR + PARC));
                PARU = Math.max(PARU, (1.0 + RTOL) * PARS);
            }
            PARL = Math.max(PARL, PARS);
            if (INFO == 0) {
                if (ITER == ITMAX) {
                    INFO = 4;
                }
                if (PARU <= (1.0 + 0.5 * RTOL) * PARS) {
                    INFO = 3;
                }
                if (PARU == 0.0) {
                    INFO = 2;
                }
            }
            if (INFO != 0) {
                PAR = PARF;
                F = -0.5 * (Math.pow(RXNORM, 2.0) + PAR * Math.pow(XNORM, 2.0));
                if (REDNC) {
                    F = -0.5 * (Math.pow(RXNORM, 2.0) + PAR * Math.pow(DELTA, 2.0) - Math.pow(RZNORM, 2.0));
                    this.DAXPY(N, ALPHA, Z, startZ, 1, X, startX, 1);
                }
                for (J = 1; J <= N - 1; ++J) {
                    this.DCOPY(N - J, A, (J - 1) * LDA + J + startA, 1, A, J * LDA + J - 1 + startA, LDA);
                }
                this.DCOPY(N, WA1, startWA1, 1, A, startA, LDA + 1);
                output.PAR = PAR;
                output.F = F;
                output.INFO = INFO;
                return output;
            }
            PAR = Math.max(PARL, PAR + PARC);
        }
        output.PAR = PAR;
        output.F = F;
        output.INFO = INFO;
        return output;
    }
    
    private void DTRSV(final char UPLO, final char TRANS, final char DIAG, final int N, final double[] A, final int startA, final int LDA, final double[] X, final int startX, final int INCX) {
        int KX = 0;
        int INFO = 0;
        if (UPLO != 'U' && UPLO != 'L') {
            INFO = 1;
        }
        else if (TRANS != 'N' && TRANS != 'T' && TRANS != 'C') {
            INFO = 2;
        }
        else if (DIAG != 'U' && DIAG != 'N') {
            INFO = 3;
        }
        else if (N < 0) {
            INFO = 4;
        }
        else if (LDA < Math.max(1, N)) {
            INFO = 6;
        }
        else if (INCX == 0) {
            INFO = 8;
        }
        if (INFO != 0) {
            return;
        }
        if (N == 0) {
            return;
        }
        final boolean NOUNIT = DIAG == 'N';
        if (INCX <= 0) {
            KX = 1 - (N - 1) * INCX;
        }
        else if (INCX != 1) {
            KX = 1;
        }
        if (TRANS == 'N') {
            if (UPLO == 'U') {
                if (INCX == 1) {
                    for (int J = N; J <= 1; --J) {
                        if (X[J - 1 + startX] != 0.0) {
                            if (NOUNIT) {
                                X[J - 1 + startX] /= A[(J - 1) * LDA + J - 1 + startA];
                            }
                            final double TEMP = X[J - 1 + startX];
                            for (int I = J - 1; I <= 1; --I) {
                                X[I - 1 + startX] -= TEMP * A[(J - 1) * LDA + I - 1 + startA];
                            }
                        }
                    }
                }
                else {
                    int JX = KX + (N - 1) * INCX;
                    for (int J = N; J <= 1; --J) {
                        if (X[JX - 1 + startX] != 0.0) {
                            if (NOUNIT) {
                                X[JX - 1 + startX] /= A[(J - 1) * LDA + J - 1 + startA];
                            }
                            final double TEMP = X[JX - 1 + startX];
                            int IX = JX;
                            for (int I = J - 1; I <= 1; --I) {
                                IX -= INCX;
                                X[IX - 1 + startX] -= TEMP * A[(J - 1) * LDA + I - 1 + startA];
                            }
                        }
                        JX -= INCX;
                    }
                }
            }
            else if (INCX == 1) {
                for (int J = 1; J <= N; ++J) {
                    if (X[J - 1 + startX] != 0.0) {
                        if (NOUNIT) {
                            X[J - 1 + startX] /= A[(J - 1) * LDA + J - 1 + startA];
                        }
                        final double TEMP = X[J - 1 + startX];
                        for (int I = J + 1; I <= N; ++I) {
                            X[I - 1 + startX] -= TEMP * A[(J - 1) * LDA + I - 1 + startA];
                        }
                    }
                }
            }
            else {
                int JX = KX;
                for (int J = 1; J <= N; ++J) {
                    if (X[JX - 1 + startX] != 0.0) {
                        if (NOUNIT) {
                            X[JX - 1 + startX] /= A[(J - 1) * LDA + J - 1 + startA];
                        }
                        final double TEMP = X[JX - 1 + startX];
                        int IX = JX;
                        for (int I = J + 1; I <= N; ++I) {
                            IX += INCX;
                            X[IX - 1 + startX] -= TEMP * A[(J - 1) * LDA + I - 1 + startA];
                        }
                    }
                    JX += INCX;
                }
            }
        }
        else if (UPLO == 'U') {
            if (INCX == 1) {
                for (int J = 1; J <= N; ++J) {
                    double TEMP = X[J - 1 + startX];
                    for (int I = 1; I <= J - 1; ++I) {
                        TEMP -= A[(J - 1) * LDA + I - 1 + startA] * X[I - 1 + startX];
                    }
                    if (NOUNIT) {
                        TEMP /= A[(J - 1) * LDA + J - 1 + startA];
                    }
                    X[J - 1 + startX] = TEMP;
                }
            }
            else {
                int JX = KX;
                for (int J = 1; J <= N; ++J) {
                    double TEMP = X[JX - 1 + startX];
                    int IX = KX;
                    for (int I = 1; I <= J - 1; ++I) {
                        TEMP -= A[(J - 1) * LDA + I - 1 + startA] * X[IX - 1 + startX];
                        IX += INCX;
                    }
                    if (NOUNIT) {
                        TEMP /= A[(J - 1) * LDA + J - 1 + startA];
                    }
                    X[JX - 1 + startX] = TEMP;
                    JX += INCX;
                }
            }
        }
        else if (INCX == 1) {
            for (int J = N; J <= 1; --J) {
                double TEMP = X[J - 1 + startX];
                for (int I = N; I <= J + 1; --I) {
                    TEMP -= A[(J - 1) * LDA + I - 1 + startA] * X[I - 1 + startX];
                }
                if (NOUNIT) {
                    TEMP /= A[(J - 1) * LDA + J - 1 + startA];
                }
                X[J - 1 + startX] = TEMP;
            }
        }
        else {
            int JX;
            KX = (JX = KX + (N - 1) * INCX);
            for (int J = N; J <= 1; --J) {
                double TEMP = X[JX - 1 + startX];
                int IX = KX;
                for (int I = N; I <= J + 1; --I) {
                    TEMP -= A[(J - 1) * LDA + I - 1 + startA] * X[IX - 1 + startX];
                    IX -= INCX;
                }
                if (NOUNIT) {
                    TEMP /= A[(J - 1) * LDA + J - 1 + startA];
                }
                X[JX - 1 + startX] = TEMP;
                JX -= INCX;
            }
        }
    }
    
    private int DPOTRF(final char UPLO, final int N, final double[] A, final int startA, final int LDA) {
        int INFO = 0;
        final boolean UPPER = UPLO == 'U';
        if (!UPPER && UPLO != 'L') {
            INFO = -1;
        }
        else if (N < 0) {
            INFO = -2;
        }
        else if (LDA < Math.max(1, N)) {
            INFO = -4;
        }
        if (INFO != 0) {
            return INFO;
        }
        if (N == 0) {
            return INFO;
        }
        if (UPPER) {
            for (int J = 1; J <= N; ++J) {
                double AJJ = A[(J - 1) * LDA + J - 1 + startA] - this.DDOT(J - 1, A, (J - 1) * LDA + startA, 1, A, (J - 1) * LDA + startA, 1);
                if (AJJ <= 0.0) {
                    A[(J - 1) * LDA + J - 1 + startA] = AJJ;
                    return J;
                }
                AJJ = Math.sqrt(AJJ);
                A[(J - 1) * LDA + J - 1 + startA] = AJJ;
                if (J < N) {
                    this.DGEMV('T', J - 1, N - J, -1.0, A, J * LDA + startA, LDA, A, (J - 1) * LDA + startA, 1, 1.0, A, J * LDA + J - 1 + startA, LDA);
                    this.DSCAL(N - J, 1.0 / AJJ, A, J * LDA + J - 1 + startA, LDA);
                }
            }
        }
        else {
            for (int J = 1; J <= N; ++J) {
                double AJJ = A[(J - 1) * LDA + J - 1 + startA] - this.DDOT(J - 1, A, J - 1 + startA, LDA, A, J - 1 + startA, LDA);
                if (AJJ <= 0.0) {
                    A[(J - 1) * LDA + J - 1 + startA] = AJJ;
                    return J;
                }
                AJJ = Math.sqrt(AJJ);
                A[(J - 1) * LDA + J - 1 + startA] = AJJ;
                if (J < N) {
                    this.DGEMV('N', N - J, J - 1, -1.0, A, J + startA, LDA, A, J - 1 + startA, LDA, 1.0, A, (J - 1) * LDA + J + startA, 1);
                    this.DSCAL(N - J, 1.0 / AJJ, A, (J - 1) * LDA + J + startA, 1);
                }
            }
        }
        return INFO;
    }
    
    private void DGEMV(final char TRANS, final int M, final int N, final double ALPHA, final double[] A, final int startA, final int LDA, final double[] X, final int startX, final int INCX, final double BETA, final double[] Y, final int startY, final int INCY) {
        int INFO = 0;
        if (TRANS != 'N' && TRANS != 'T' && TRANS != 'C') {
            INFO = 1;
        }
        else if (M < 0) {
            INFO = 2;
        }
        else if (N < 0) {
            INFO = 3;
        }
        else if (LDA < Math.max(1, M)) {
            INFO = 6;
        }
        else if (INCX == 0) {
            INFO = 8;
        }
        else if (INCY == 0) {
            INFO = 11;
        }
        if (INFO != 0) {
            return;
        }
        if (M == 0 || N == 0 || (ALPHA == 0.0 && BETA == 1.0)) {
            return;
        }
        int LENX;
        int LENY;
        if (TRANS == 'N') {
            LENX = N;
            LENY = M;
        }
        else {
            LENX = M;
            LENY = N;
        }
        int KX;
        if (INCX > 0) {
            KX = 1;
        }
        else {
            KX = 1 - (LENX - 1) * INCX;
        }
        int KY;
        if (INCY > 0) {
            KY = 1;
        }
        else {
            KY = 1 - (LENY - 1) * INCY;
        }
        if (BETA != 1.0) {
            if (INCY == 1) {
                if (BETA == 0.0) {
                    for (int I = 1; I <= LENY; ++I) {
                        Y[I - 1 + startY] = 0.0;
                    }
                }
                else {
                    for (int I = 1; I <= LENY; ++I) {
                        Y[I - 1 + startY] *= BETA;
                    }
                }
            }
            else {
                int IY = KY;
                if (BETA == 0.0) {
                    for (int I = 1; I <= LENY; ++I) {
                        Y[IY - 1 + startY] = 0.0;
                        IY += INCY;
                    }
                }
                else {
                    for (int I = 1; I <= LENY; ++I) {
                        Y[IY - 1 + startY] *= BETA;
                        IY += INCY;
                    }
                }
            }
        }
        if (ALPHA == 0.0) {
            return;
        }
        if (TRANS == 'N') {
            int JX = KX;
            if (INCY == 1) {
                for (int J = 1; J <= N; ++J) {
                    if (X[JX - 1 + startX] != 0.0) {
                        final double TEMP = ALPHA * X[JX - 1 + startX];
                        for (int I = 1; I <= M; ++I) {
                            Y[I - 1 + startY] += TEMP * A[(J - 1) * LDA + I - 1 + startA];
                        }
                    }
                    JX += INCX;
                }
            }
            else {
                for (int J = 1; J <= N; ++J) {
                    if (X[JX - 1 + startX] != 0.0) {
                        final double TEMP = ALPHA * X[JX - 1 + startX];
                        int IY = KY;
                        for (int I = 1; I <= M; ++I) {
                            Y[IY - 1 + startY] += TEMP * A[(J - 1) * LDA + I - 1 + startA];
                            IY += INCY;
                        }
                    }
                    JX += INCX;
                }
            }
        }
        else {
            int JY = KY;
            if (INCX == 1) {
                for (int J = 1; J <= N; ++J) {
                    double TEMP = 0.0;
                    for (int I = 1; I <= M; ++I) {
                        TEMP += A[(J - 1) * LDA + I - 1 + startA] * X[I - 1 + startX];
                    }
                    Y[JY - 1 + startY] += ALPHA * TEMP;
                    JY += INCY;
                }
            }
            else {
                for (int J = 1; J <= N; ++J) {
                    double TEMP = 0.0;
                    int IX = KX;
                    for (int I = 1; I <= M; ++I) {
                        TEMP += A[(J - 1) * LDA + I - 1 + startA] * X[IX - 1 + startX];
                        IX += INCX;
                    }
                    Y[JY - 1 + startY] += ALPHA * TEMP;
                    JY += INCY;
                }
            }
        }
    }
    
    private void DAXPY(final int N, final double DA, final double[] DX, final int startX, final int INCX, final double[] DY, final int startY, final int INCY) {
        if (N <= 0) {
            return;
        }
        if (DA == 0.0) {
            return;
        }
        if (INCX == 1 && INCY == 1) {
            final int M = N % 4;
            if (M != 0) {
                for (int I = 1; I <= M; ++I) {
                    DY[I - 1 + startY] += DA * DX[I - 1 + startX];
                }
                if (N < 4) {
                    return;
                }
            }
            int I;
            for (int MP1 = I = M + 1; I <= N; I += 4) {
                DY[I - 1 + startY] += DA * DX[I - 1 + startX];
                DY[I + 1 - 1 + startY] += DA * DX[I + 1 - 1 + startX];
                DY[I + 2 - 1 + startY] += DA * DX[I + 2 - 1 + startX];
                DY[I + 3 - 1 + startY] += DA * DX[I + 3 - 1 + startX];
            }
        }
        else {
            int IX = 1;
            int IY = 1;
            if (INCX < 0) {
                IX = (-N + 1) * INCX + 1;
            }
            if (INCY < 0) {
                IY = (-N + 1) * INCY + 1;
            }
            for (int I = 1; I <= N; ++I) {
                DY[IY - 1 + startY] += DA * DX[IX - 1 + startX];
                IX += INCX;
                IY += INCY;
            }
        }
    }
    
    private char IMPRVE(final int N, final int LP, final double[] XSET, final int startXSET, final double[] YSET, final int startYSET, final double[] XBASE, final double FXBASE, final double[] DIST, final int startDIST, final double DELTA, final double[] NFP, final int startNFP, final double[] XARCH, final int startXARCH, final double[] YARCH, final int startYARCH, final char HTYPE, final int NH, final int[] HESSTR, final double[] W, final int startW) {
        if (this.NP < LP) {
            final char ACTION = this.IMPRVC(N, LP, XSET, startXSET, YSET, startYSET, XBASE, FXBASE, DIST, startDIST, DELTA, NFP, startNFP, HTYPE, NH, HESSTR, W, startW);
            if (ACTION != '-' || this.NF > this.MAXNF) {
                return ACTION;
            }
        }
        char ACTION = this.IMPRVF(N, LP, XSET, startXSET, YSET, startYSET, XBASE, FXBASE, DIST, startDIST, DELTA, NFP, startNFP, XARCH, startXARCH, YARCH, startYARCH, HTYPE, NH, HESSTR, W, startW);
        if (ACTION != '-' || this.NF > this.MAXNF) {
            return ACTION;
        }
        ACTION = this.IMPRVL(N, LP, XSET, startXSET, YSET, startYSET, XBASE, FXBASE, DIST, startDIST, DELTA, NFP, startNFP, XARCH, startXARCH, YARCH, startYARCH, HTYPE, NH, HESSTR, W, startW);
        return ACTION;
    }
    
    private char IMPRVC(final int N, final int LP, final double[] XSET, final int startXSET, final double[] YSET, final int startYSET, final double[] XBASE, final double FXBASE, final double[] DIST, final int startDIST, final double DELTA, final double[] NFP, final int startNFP, final char HTYPE, final int NH, final int[] HESSTR, final double[] W, final int startW) {
        final int INEW = 1 + this.NP * N;
        final Point FL = this.NFPDAT(N, this.NP + 1, LP);
        final int IPOL = FL.x;
        final int LPOL = FL.y;
        this.MAXABS(N, DELTA, NFP, IPOL - 1 + startNFP, LPOL, XSET, INEW - 1 + startXSET, HTYPE, NH, HESSTR, W, startW);
        ++this.NP;
        DIST[this.NP - 1 + startDIST] = this.DNRM2(N, XSET, INEW - 1 + startXSET, 1);
        this.DAXPY(N, 1.0, XBASE, 0, 1, XSET, INEW - 1 + startXSET, 1);
        ++this.NF;
        if (this.NF > this.MAXNF) {
            return '-';
        }
        YSET[this.NP - 1 + startYSET] = this.UFN(N, XSET, INEW - 1 + startXSET);
        return 'A';
    }
    
    private char IMPRVF(final int N, final int LP, final double[] XSET, final int startXSET, final double[] YSET, final int startYSET, final double[] XBASE, final double FXBASE, final double[] DIST, final int startDIST, final double DELTA, final double[] NFP, final int startNFP, final double[] XARCH, final int startXARCH, final double[] YARCH, final int startYARCH, final char HTYPE, final int NH, final int[] HESSTR, final double[] W, final int startW) {
        final int HIST = 10;
        final int NP1 = N + 1;
        final double ONET = 1.2;
        final char ACTION = '-';
        final double MAXDST = 1.2 * DELTA;
        double DMAX = 0.0;
        int IP = 0;
        for (int J = 1; J <= this.NP; ++J) {
            if (DIST[J - 1 + startDIST] > DMAX) {
                DMAX = DIST[J - 1 + startDIST];
                IP = J;
            }
        }
        if (DMAX <= MAXDST) {
            return ACTION;
        }
        final Point FL = this.NFPDAT(N, IP, LP);
        final int ifP = FL.x;
        final int LFP = FL.y;
        double FPMAX = 0.0;
        int INEW = 0;
        final int JP = 1 + Math.max(this.NARCH - 10, 0) * N;
        int IA = 0;
        for (int J = JP; J <= this.NARCH; ++J) {
            final int JX = 1 + (J - 1) * N;
            System.arraycopy(XARCH, JX - 1 + startXARCH, W, NP1 - 1 + startW, N);
            this.DAXPY(N, -1.0, XBASE, 0, 1, W, NP1 - 1 + startW, 1);
            final double DJ = this.DNRM2(N, W, NP1 - 1 + startW, 1);
            if (DJ <= DELTA) {
                final double POLVAL = Math.abs(this.VALP(N, W, NP1 - 1 + startW, NFP, ifP - 1 + startNFP, LFP, HTYPE, NH, HESSTR));
                if (POLVAL > FPMAX) {
                    IA = JX;
                    INEW = J;
                    FPMAX = POLVAL;
                }
            }
        }
        if (FPMAX >= 1.2) {
            this.REPLCE(N, IP, XSET, startXSET, YSET, startYSET, XARCH, IA - 1 + startXARCH, YARCH[INEW - 1 + startYARCH], false, XBASE, FXBASE, DIST, startDIST, XARCH, startXARCH, YARCH, startYARCH);
            return 'O';
        }
        final double VALUE = this.MAXABS(N, DELTA, NFP, ifP - 1 + startNFP, LFP, W, startW, HTYPE, NH, HESSTR, W, NP1 - 1 + startW);
        this.DAXPY(N, 1.0, XBASE, 0, 1, W, startW, 1);
        this.REPLCE(N, IP, XSET, startXSET, YSET, startYSET, W, startW, VALUE, true, XBASE, FXBASE, DIST, startDIST, XARCH, startXARCH, YARCH, startYARCH);
        return 'F';
    }
    
    private char IMPRVL(final int N, final int LP, final double[] XSET, final int startXSET, final double[] YSET, final int startYSET, final double[] XBASE, final double FXBASE, final double[] DIST, final int startDIST, final double DELTA, final double[] NFP, final int startNFP, final double[] XARCH, final int startXARCH, final double[] YARCH, final int startYARCH, final char HTYPE, final int NH, final int[] HESSTR, final double[] W, final int startW) {
        double VALUE = 0.0;
        final double ZERO = 0.0;
        final double ONE = 1.0;
        final double REPTHR = 2.0;
        final int NP1 = N + 1;
        final int NNP1 = N + NP1;
        double FPMAX = 0.0;
        int INEW = 1;
        for (int J = 1; J <= this.NP; ++J) {
            final Point FL = this.NFPDAT(N, J, LP);
            final int IPOL = FL.x;
            final int LPOL = FL.y;
            VALUE = this.MAXABS(N, DELTA, NFP, IPOL - 1 + startNFP, LPOL, W, NP1 - 1 + startW, HTYPE, NH, HESSTR, W, NNP1 - 1 + startW);
            if (VALUE > FPMAX) {
                FPMAX = VALUE;
                INEW = J;
                System.arraycopy(W, NP1 - 1 + startW, W, startW, N);
            }
        }
        this.DAXPY(N, 1.0, XBASE, 0, 1, W, startW, 1);
        char ACTION;
        if (FPMAX > 2.0) {
            this.REPLCE(N, INEW, XSET, startXSET, YSET, startYSET, W, startW, VALUE, true, XBASE, FXBASE, DIST, startDIST, XARCH, startXARCH, YARCH, startYARCH);
            ACTION = 'C';
        }
        else {
            ACTION = '-';
        }
        return ACTION;
    }
    
    private char TRYRPL(final int N, final int LP, final double[] XSET, final int startXSET, final double[] YSET, final int startYSET, final double[] XNEW, final int startXNEW, final double FXNEW, final double[] XBASE, final double FXBASE, final double[] DIST, final int startDIST, final double DELTA, final double[] NFP, final int startNFP, final double[] XARCH, final int startXARCH, final double[] YARCH, final int startYARCH, final char HTYPE, final int NH, final int[] HESSTR, final double[] W, final int startW) {
        final double ONET = 1.2;
        final double THRNEW = 0.05;
        final double THRFAR = 2.0;
        final double THRCLO = 2.0;
        char ACTION = '-';
        this.GETNFP(N, LP, XSET, startXSET, YSET, startYSET, 0, XBASE, FXBASE, DIST, startDIST, DELTA, -1.0, NFP, startNFP, XARCH, startXARCH, YARCH, startYARCH, HTYPE, NH, HESSTR, W, startW);
        System.arraycopy(XNEW, startXNEW, W, startW, N);
        this.DAXPY(N, -1.0, XBASE, 0, 1, W, startW, 1);
        if (this.NP < LP) {
            final Point FL = this.NFPDAT(N, this.NP + 1, LP);
            final int IPOL = FL.x;
            final int LPOL = FL.y;
            final double PVAL = Math.abs(this.VALP(N, W, startW, NFP, IPOL - 1 + startNFP, LPOL, HTYPE, NH, HESSTR));
            if (PVAL >= 0.05) {
                ACTION = 'A';
                final int I = this.NP * N + 1;
                ++this.NP;
                System.arraycopy(XNEW, startXNEW, XSET, I - 1 + startXSET, N);
                YSET[this.NP - 1 + startYSET] = FXNEW;
                DIST[this.NP - 1 + startDIST] = this.DNRM2(N, W, startW, 1);
            }
        }
        else {
            double DUP = 1.0E20;
            boolean doitagain;
            do {
                doitagain = false;
                double DMAX = 1.2 * DELTA;
                int IMAX = 0;
                for (int I = 1; I <= this.NP; ++I) {
                    final double DI = DIST[I - 1 + startDIST];
                    if (DI > DMAX && DI < DUP) {
                        DMAX = DI;
                        IMAX = I;
                    }
                }
                if (IMAX > 0) {
                    final Point FL2 = this.NFPDAT(N, IMAX, LP);
                    final int IPOL = FL2.x;
                    final int LPOL = FL2.y;
                    final double PVAL = Math.abs(this.VALP(N, W, startW, NFP, IPOL - 1 + startNFP, LPOL, HTYPE, NH, HESSTR));
                    if (PVAL >= 2.0 * Math.pow(DELTA / DMAX, 2.0)) {
                        this.REPLCE(N, IMAX, XSET, startXSET, YSET, startYSET, XNEW, startXNEW, FXNEW, false, XBASE, FXBASE, DIST, startDIST, XARCH, startXARCH, YARCH, startYARCH);
                        ACTION = 'F';
                    }
                    else {
                        DUP = DMAX;
                        doitagain = true;
                    }
                }
                else {
                    double PMAX = 0.0;
                    IMAX = 0;
                    for (int I = 1; I <= this.NP; ++I) {
                        final Point FL2 = this.NFPDAT(N, I, LP);
                        final int IPOL = FL2.x;
                        final int LPOL = FL2.y;
                        final double PVAL = Math.abs(this.VALP(N, W, startW, NFP, IPOL - 1 + startNFP, LPOL, HTYPE, NH, HESSTR));
                        if (PVAL > PMAX) {
                            IMAX = I;
                            PMAX = PVAL;
                        }
                    }
                    if (PMAX < 2.0) {
                        continue;
                    }
                    this.REPLCE(N, IMAX, XSET, startXSET, YSET, startYSET, XNEW, startXNEW, FXNEW, false, XBASE, FXBASE, DIST, startDIST, XARCH, startXARCH, YARCH, startYARCH);
                    ACTION = 'C';
                }
            } while (doitagain);
        }
        return ACTION;
    }
    
    private void ARCHIV(final int N, final double[] XX, final int startXX, final double FXX, final double[] XARCH, final int startXARCH, final double[] YARCH, final int startYARCH) {
        int JX;
        if (this.NARCH < this.MXARCH) {
            JX = 1 + this.NARCH * N;
        }
        else {
            JX = 1;
        }
        this.NARCH = Math.min(this.MXARCH, this.NARCH + 1);
        System.arraycopy(XX, startXX, XARCH, JX - 1 + startXARCH, N);
        YARCH[this.NARCH - 1 + startYARCH] = FXX;
    }
    
    private double VALP(final int N, final double[] X, final int startX, final double[] POL, final int startPOL, final int LPOL, final char HTYPE, final int NH, final int[] HESSTR) {
        final double HALF = 0.5;
        double VALP = this.DDOT(N, X, startX, 1, POL, startPOL, 1);
        if (LPOL > N) {
            int NXT = N + 1;
            for (int J = 1; J <= N; ++J) {
                final double XJ = X[J - 1 + startX];
                VALP += 0.5 * POL[NXT - 1 + startPOL] * XJ * XJ;
                ++NXT;
            }
            if (HTYPE == 'D') {
                for (int J = 2; J < N; ++J) {
                    int II = J;
                    int JJ = 1;
                    for (int I = J; I < N; ++I) {
                        VALP += X[II - 1 + startX] * X[JJ - 1 + startX] * POL[NXT - 1 + startPOL];
                        ++NXT;
                        ++II;
                        ++JJ;
                    }
                }
            }
            else if (HTYPE == 'B') {
                for (int J = 2; J < NH; ++J) {
                    int II = J;
                    int JJ = 1;
                    for (int I = J; I < N; ++I) {
                        VALP += X[II - 1 + startX] * X[JJ - 1 + startX] * POL[NXT - 1 + startPOL];
                        ++NXT;
                        ++II;
                        ++JJ;
                    }
                }
            }
            else {
                for (int I = 1; I <= NH + NH; I += 2) {
                    VALP += X[HESSTR[I - 1] - 1 + startX] * X[HESSTR[I] - 1 + startX] * POL[NXT - 1 + startPOL];
                    ++NXT;
                }
            }
        }
        return VALP;
    }
    
    private double DDOT(final int N, final double[] DX, final int startDX, final int INCX, final double[] DY, final int startDY, final int INCY) {
        double DDOT = 0.0;
        if (N <= 0) {
            return 0.0;
        }
        if (INCX == 1 && INCY == 1) {
            final int M = N % 5;
            if (M != 0) {
                for (int I = 1; I <= M; ++I) {
                    DDOT += DX[I - 1 + startDX] * DY[I - 1 + startDY];
                }
                if (N < 5) {
                    return DDOT;
                }
            }
            int I;
            for (int MP1 = I = M + 1; I <= N; I += 5) {
                DDOT = DDOT + DX[I - 1 + startDX] * DY[I - 1 + startDY] + DX[I + startDX] * DY[I + startDY] + DX[I + 1 + startDX] * DY[I + 1 + startDY] + DX[I + 2 + startDX] * DY[I + 2 + startDY] + DX[I + 3 + startDX] * DY[I + 3 + startDY];
            }
        }
        else {
            int IX = 1;
            int IY = 1;
            if (INCX < 0) {
                IX = (-N + 1) * INCX + 1;
            }
            if (INCY < 0) {
                IY = (-N + 1) * INCY + 1;
            }
            for (int I = 1; I <= N; ++I) {
                DDOT += DX[IX - 1 + startDX] * DY[IY - 1 + startDY];
                IX += INCX;
                IY += INCY;
            }
        }
        return DDOT;
    }
    
    private void REPLCE(final int N, final int IP, final double[] XSET, final int startXSET, final double[] YSET, final int startYSET, final double[] XNEW, final int startXNEW, final double FXNEW, final boolean CALCF, final double[] XBASE, final double FXBASE, final double[] DIST, final int startDIST, final double[] XARCH, final int startXARCH, final double[] YARCH, final int startYARCH) {
        final int IX = 1 + (IP - 1) * N;
        this.ARCHIV(N, XSET, IX - 1 + startXSET, YSET[IP - 1 + startYSET], XARCH, startXARCH, YARCH, startYARCH);
        System.arraycopy(XNEW, startXNEW, XSET, IX - 1 + startXSET, N);
        double VALUE = 0.0;
        for (int I = 1; I <= N; ++I) {
            VALUE += Math.pow(XNEW[I - 1 + startXNEW] - XBASE[I - 1], 2.0);
        }
        DIST[IP - 1 + startDIST] = Math.sqrt(VALUE);
        if (CALCF) {
            ++this.NF;
            if (this.NF > this.MAXNF) {
                return;
            }
            YSET[IP - 1 + startYSET] = this.UFN(N, XSET, IX - 1 + startXSET);
        }
        else {
            YSET[IP - 1 + startYSET] = FXNEW;
        }
    }
    
    private Point NFPDAT(final int N, final int I, final int LP) {
        int LENGTH;
        int FIRST;
        if (I <= N) {
            LENGTH = N;
            FIRST = 1 + (I - 1) * N;
        }
        else {
            LENGTH = LP;
            FIRST = 1 + N * N + (I - N - 1) * LENGTH;
        }
        return new Point(FIRST, LENGTH);
    }
    
    private double MAXABS(final int N, final double DELTA, final double[] POL, final int startPOL, final int LPOL, final double[] XSOL, final int startXSOL, final char HTYPE, final int NH, final int[] HESSTR, final double[] W, final int startW) {
        final int ITER = 100;
        final double MONE = -1.0;
        final int ITMAX = 10;
        final int NSQ = N * N;
        final int N2 = 1 + NSQ;
        final int N3 = N2 + N;
        final int N4 = N3 + N;
        double LAMBDA = 0.0;
        this.MKHESS(N, POL, startPOL, LPOL, W, startW, HTYPE, NH, HESSTR);
        DGQToutput dgqt = this.DGQT(N, W, startW, N, POL, startPOL, DELTA, 0.1, 0.1, 10, LAMBDA, XSOL, startXSOL, ITER, W, N2 - 1 + startW, W, N3 - 1 + startW, W, N4 - 1 + startW);
        LAMBDA = dgqt.PAR;
        final double MINVAL = dgqt.F;
        this.DSCAL(N, -1.0, POL, startPOL, 1);
        this.DSCAL(NSQ, -1.0, W, startW, 1);
        final int N5 = N4 + N;
        dgqt = this.DGQT(N, W, startW, N, POL, startPOL, DELTA, 0.1, 0.1, 10, LAMBDA, W, N5 - 1 + startW, ITER, W, N2 - 1 + startW, W, N3 - 1 + startW, W, N4 - 1 + startW);
        LAMBDA = dgqt.PAR;
        double MAXVAL = dgqt.F;
        MAXVAL = -MAXVAL;
        this.DSCAL(N, -1.0, POL, startPOL, 1);
        double VALUE;
        if (MAXVAL > -MINVAL) {
            System.arraycopy(W, N5 - 1 + startW, XSOL, startXSOL, N);
            VALUE = MAXVAL;
        }
        else {
            VALUE = MINVAL;
        }
        return VALUE;
    }
    
    private void DSCAL(final int N, final double DA, final double[] DX, final int startDX, final int INCX) {
        if (N <= 0 || INCX <= 0) {
            return;
        }
        if (INCX == 1) {
            final int M = N % 5;
            if (M != 0) {
                for (int I = 1; I <= M; ++I) {
                    DX[I - 1 + startDX] *= DA;
                }
                if (N < 5) {
                    return;
                }
            }
            int I;
            for (int MP1 = I = M + 1; I <= N; I += 5) {
                DX[I - 1 + startDX] *= DA;
                DX[I + 1 - 1 + startDX] *= DA;
                DX[I + 2 - 1 + startDX] *= DA;
                DX[I + 3 - 1 + startDX] *= DA;
                DX[I + 4 - 1 + startDX] *= DA;
            }
        }
        else {
            for (int NINCX = N * INCX, I = 1; I <= NINCX; I += INCX) {
                DX[I - 1 + startDX] *= DA;
            }
        }
    }
    
    private double DASUM(final int N, final double[] DX, final int startDX, final int INCX) {
        double sum = 0.0;
        for (int i = 0; i < N; i += INCX) {
            sum += Math.abs(DX[i + startDX]);
        }
        return sum;
    }
    
    private double DESTSV(final int N, final double[] R, final int startR, final int LDR, final double[] Z, final int startZ) {
        final double P01 = 0.01;
        for (int I = 1; I <= N; ++I) {
            Z[I - 1 + startZ] = 0.0;
        }
        double E = Math.abs(R[startR]);
        if (E == 0.0) {
            final double SVMIN = 0.0;
            Z[startZ] = 1.0;
            return SVMIN;
        }
        for (int I = 1; I <= N; ++I) {
            E = this.SIGN(E, -Z[I - 1 + startZ]);
            if (Math.abs(E - Z[I - 1 + startZ]) > Math.abs(R[(I - 1) * LDR + I - 1 + startR])) {
                final double TEMP = Math.min(0.01, Math.abs(R[(I - 1) * LDR + I - 1 + startR]) / Math.abs(E - Z[I - 1 + startZ]));
                this.DSCAL(N, TEMP, Z, startZ, 1);
                E *= TEMP;
            }
            double W;
            double WM;
            if (R[(I - 1) * LDR + I - 1 + startR] == 0.0) {
                W = 1.0;
                WM = 1.0;
            }
            else {
                W = (E - Z[I - 1 + startZ]) / R[(I - 1) * LDR + I - 1 + startR];
                WM = -(E + Z[I - 1 + startZ]) / R[(I - 1) * LDR + I - 1 + startR];
            }
            double S = Math.abs(E - Z[I - 1 + startZ]);
            double SM = Math.abs(E + Z[I - 1 + startZ]);
            for (int J = I + 1; J <= N; ++J) {
                SM += Math.abs(Z[J - 1 + startZ] + WM * R[(J - 1) * LDR + I - 1 + startR]);
            }
            if (I < N) {
                this.DAXPY(N - I, W, R, I * LDR + I - 1 + startR, LDR, Z, I + startZ, 1);
                S += this.DASUM(N - I, Z, I + startZ, 1);
            }
            if (S < SM) {
                final double TEMP = WM - W;
                W = WM;
                if (I < N) {
                    this.DAXPY(N - I, TEMP, R, I * LDR + I - 1 + startR, LDR, Z, I + startZ, 1);
                }
            }
            Z[I - 1 + startZ] = W;
        }
        double YNORM = this.DNRM2(N, Z, startZ, 1);
        for (int J = N; J <= 1; --J) {
            if (Math.abs(Z[J - 1 + startZ]) > Math.abs(R[(J - 1) * LDR + J - 1 + startR])) {
                final double TEMP = Math.min(0.01, Math.abs(R[(J - 1) * LDR + J - 1 + startR]) / Math.abs(Z[J - 1 + startZ]));
                this.DSCAL(N, TEMP, Z, startZ, 1);
                YNORM *= TEMP;
            }
            if (R[(J - 1) * LDR + J - 1 + startR] == 0.0) {
                Z[J - 1 + startZ] = 1.0;
            }
            else {
                Z[J - 1 + startZ] /= R[(J - 1) * LDR + J - 1 + startR];
            }
            final double TEMP = -Z[J - 1 + startZ];
            this.DAXPY(J - 1, TEMP, R, (J - 1) * LDR + startR, 1, Z, startZ, 1);
        }
        final double ZNORM = 1.0 / this.DNRM2(N, Z, startZ, 1);
        final double SVMIN = YNORM * ZNORM;
        this.DSCAL(N, ZNORM, Z, startZ, 1);
        return SVMIN;
    }
    
    private double SIGN(final double x, final double y) {
        if (y >= 0.0) {
            return Math.abs(x);
        }
        return -Math.abs(x);
    }
    
    private class DGQToutput
    {
        double PAR;
        double F;
        int INFO;
    }
}
