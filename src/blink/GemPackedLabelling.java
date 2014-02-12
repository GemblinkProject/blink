package blink;

import java.io.PrintStream;
import java.util.HashSet;

/**
 * This labelling admits that all odd vertice
 * with label k is adjacent to vertice k+1 by
 * color 0.
 */
public class GemPackedLabelling implements Comparable {
	static char[] symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	static int[] values = new int[128];
    private int _n;
    private int[] _code;
    private int _handleNumber;

    static {
    	for (int i = 0; i < 128; ++i) {
    		values[i] = -1;
    	}
    	for (int i = 0; i < symbols.length; ++i) {
    		values[symbols[i]] = i;
    	}
    }
    
    public GemPackedLabelling(int[] code, int handleNumber) {
        _code = code;
        _n = 2 * (code.length/3);
        _handleNumber = handleNumber;
    }

    public int[] getCode() {
        return (int[])_code.clone();
    }

    public GemPackedLabelling(int[] code) {
        this(code,0);
    }
    public GemPackedLabelling(String st) {
        this(st,0);
    }

    public GemPackedLabelling(String st, int handleNumber) {
        int k = 0;
        for (int i = 0; i < st.length(); ++i) {
        	if (values[(int)st.charAt(i)] != -1) ++k;
        }
        k /= 3;
        int capacity = symbols.length, length = 1;
        while (k > capacity*length) {
        	capacity *= symbols.length;
        	length++;
        }
        k /= length;
        _code = new int[3*k];
        _n = 2*k;
        
        int idx = 0;

        for (int i=0;i<3*k;i++) {
        	int c = 0;
        	for (int j = 0; j < length; ++j) {
        		int v = values[(int)st.charAt(idx++)]; 
        		if (v == -1) --j;
        		else c = v + c*symbols.length;
        	}
        	_code[i] = c+1;
        }

        _handleNumber = handleNumber;

    }

    public String getLettersString(String sep) {
        StringBuffer s = new StringBuffer();
        int capacity = symbols.length, length = 1;
        while (_n > capacity) {
        	capacity *= symbols.length;
        	length++;
        }
        char[] word = new char[length];
        
        for (int i = 0; i < _code.length; ++i) {
            if (i % (_n/2) == 0 && i > 0)
                s.append(sep);

            int x = _code[i] - 1;
            for (int j = 0; j < length; ++j) {
            	word[length-j-1] = symbols[x%symbols.length];
            	x /= symbols.length;
            }
            
            s.append(word);
        }
        return s.toString();
    }

    public int getHandleNumber() {
        return _handleNumber;
    }

    public String getIntegersString(char sep) {
        StringBuffer s = new StringBuffer();
        int k = 0;
        for (int i: _code) {
            if (k % (_n/2) == 0 && k > 0)
                s.append(sep);
            s.append(String.format("%2d ",i));
            k++;
        }
        return s.toString();
    }

    public int getNumberOfVertcices() {
        return _n;
    }

    public int hashCode() {
        boolean parity = false;
        int hc = 0;
        for (int i=0;i<_code.length;i+=2) {
            if (!parity)
                hc += _code[i];
            else
                hc -= _code[i];
            parity = !parity;
        }
        return hc;
    }

    public boolean equals(Object o) {
        return compareTo(o) == 0;
    }

    public boolean equalsNotCheckingHandle(Object o) {
        return compareToNotCheckingHandles(o) == 0;
    }

    public int compareToNotCheckingHandles(Object o) {
        GemPackedLabelling g = (GemPackedLabelling) o;
        int r = _n - g._n;
        if (r != 0)
            return r;
        int i = 0;
        while (r == 0 && i<_code.length) {
            r = _code[i] - g._code[i];
            i++;
        }
        return r;
    }

    public int compareTo(Object o) {
        GemPackedLabelling g = (GemPackedLabelling) o;
        int r = _n - g._n;
        if (r != 0)
            return r;
        r = this.getHandleNumber() - g.getHandleNumber();
        int i = 0;
        while (r == 0 && i<_code.length) {
            r = _code[i] - g._code[i];
            i++;
        }
        return r;
    }


    public long getGemHashCode() {
        return calculateGemHashCode(_code);
    }

    public static long calculateGemHashCode(int code[]) {
        int q = code.length / 8;
        int r = code.length % 8;
        long result = 0;
        for (int i = 0; i < 8; i++) {
            int xi = 0;
            for (int j = 0; j < q; j++)
                xi = xi + code[q * i + j];
            xi = xi % 256;
            result = result + (xi << 8 * i);
        }
        int xr = 0;
        for (int j = 0; j < r; j++)
            xr = xr + code[q * 8 + j];
        result += xr;
        return result;
    }


    public int getNeighbour(int lbl, int c) {
        if (c == 0) { // color zero
            if (lbl % 2 == 0) return lbl-1;
            else return lbl+1;
        }
        else {
            if (lbl % 2 == 1) {
                int index = _n/2 * (c-1) + lbl/2;
                return _code[index] * 2;
            }
            else {
                int offset = _n/2 * (c-1);
                for (int i=0;i<_n/2;i++) {
                    if (_code[offset + i] * 2 == lbl)
                        return i*2+1;
                }
            }
        }
        throw new RuntimeException();
    }

    // -------------------- Num Blobs -----------------
    private int _numBlobs;
    public int getNumBlobs() {
        return _numBlobs;
    }
    public void setNumBlobs(int nb) {
        _numBlobs = nb;
    }
    // -------------------- Num Blobs -----------------


    public void generatePIGALE(PrintStream s) {
        HashSet<String> m = new HashSet<String>();
        s.println("PIG:0 "+this.getLettersString("_"));
        s.println();
        for (int i=0;i<_n;i+=2) {
            String st = String.format("%d %d",i+1,this.getNeighbour(i+1,1));
            if (!m.contains(st)) {
                s.println(st);
                m.add(st);
            }
            st = String.format("%d %d",i+1,this.getNeighbour(i+1,2));
            if (!m.contains(st)) {
                s.println(st);
                m.add(st);
            }
            st = String.format("%d %d",i+1,this.getNeighbour(i+1,3));
            if (!m.contains(st)) {
                s.println(st);
                m.add(st);
            }
        }
        s.println("0 0");
    }
}
