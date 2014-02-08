package blink;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GenerateQI {

	static class BlinkBuffer {
		ArrayList<BlinkEntry> bs;
		QI[] qis;
		int count;
		
		public void reset(ArrayList<BlinkEntry> bs) {
			this.bs = bs;
			qis = new QI[bs.size()];
			count = 0;
		}
		
		public synchronized int getCount() {
			if (count == qis.length) {
				System.out.print("!");
				return -1;
			}
			System.out.print(".");
			return count++;
		}
	}
	
	static class QICalculator extends Thread {
		BlinkBuffer blinkBuffer;
		QICalculator(BlinkBuffer blinkBuffer) {
			this.blinkBuffer = blinkBuffer;
		}
		@Override
		public void run() {
			while (true) {
				int blinkIdx = blinkBuffer.getCount();
				if (blinkIdx == -1) break;
				
				BlinkEntry be = blinkBuffer.bs.get(blinkIdx);
            	GBlink b = new GBlink(new MapWord(be.get_mapCode()));
            	b.setColor((int) be.get_colors());
                QI qi = b.optimizedQuantumInvariant(3, 8);
                blinkBuffer.qis[blinkIdx] = qi;
			}
		}
	}
	
    public static void main(String[] args) throws FileNotFoundException, IOException, SQLException, ClassNotFoundException {
    	// Args: Limit / numThreads / numedges / qiIDPrefix
        int numThreads = 16;
        if (args.length >= 2) {
        	numThreads = Integer.parseInt(args[1]);
        }
        BlinkBuffer blinkBuffer = new BlinkBuffer();
        QICalculator[] threads = new QICalculator[numThreads];
    	
    	int numedges = 0;
    	if (args.length >= 3) {
    		numedges = Integer.parseInt(args[2]);
    	}
    
    	long qiIDPrefix = 0;
    	if (args.length >= 4) {
        	qiIDPrefix = Integer.parseInt(args[3]);
        }
    
        BlinkDB db = (BlinkDB) App.getRepositorio();
        long t0 = System.currentTimeMillis();

        QIRepository R = new QIRepository();

        // fill in QIRepository
        ArrayList<QI> qis = App.getRepositorio().getQIs();
        for (QI q: qis)
            R.add(q);
            
        HashMap<BlinkEntry, QI> _map = new HashMap<BlinkEntry, QI>();
		int limit = Integer.parseInt(args[0]);
        long blinkIDs[] = db.getBlinkIDsWithoutQI(limit, numedges);
        System.out.println("Found "+blinkIDs.length+" blinks without QI");
        int delta = 3;
        int count = 1;
        int acum = 0;
        for (int k = 0; k < blinkIDs.length; k += delta) {

            long t = System.currentTimeMillis();
            long[] curIDs = Arrays.copyOfRange(blinkIDs, k, Math.min(k+delta, blinkIDs.length));
            System.out.print("Calc for ");
            for (long x: curIDs) {
            	System.out.print(" "+x);
            }
            System.out.println("");
            ArrayList<BlinkEntry> bs = db.getBlinksByIDsArray(curIDs);
            
            t = System.currentTimeMillis();
            
            blinkBuffer.reset(bs);
            for (int i = 0; i < numThreads; ++i) {
            	threads[i] = new QICalculator(blinkBuffer);
            	threads[i].start();
            }
            try {
	            for (int i = 0; i < numThreads; ++i) {
    				threads[i].join();
    			}
            } catch (InterruptedException e) {
            	System.out.println("Problem!");
            }
			
			System.out.println(String.format(" %.2f sec.", (System.currentTimeMillis() - t) / 1000.0));
            for (int i = 0; i < bs.size(); ++i) {
            	BlinkEntry be = bs.get(i);
            
                QI qi = blinkBuffer.qis[i];
                
                // add to repository
                QI qiRep = R.add(qi);
                if (qiRep == null)
                    qiRep = qi;

                //
                _map.put(be, qiRep);
            }

            //
            ArrayList<QI> list = R.getList(); // get list of not persistent QIs
            //t = System.currentTimeMillis();
            
            db.insertQIs(list, qiIDPrefix);
            acum = acum+list.size();
            System.out.println(String.format("Inserted %6d new QIs total QIs %6d in %.2f sec.", list.size(), acum, (System.currentTimeMillis() - t0) / 1000.0));

            // updating biEntry
            for (BlinkEntry be : bs) {
                QI qi = _map.get(be);
                be.set_qi(qi.get_id());
            }

            // update qis
            t = System.currentTimeMillis();
            db.updateBlinksQI(bs);
            // System.out.println(String.format("Updated QIs %d blinks in %.2f sec.", bs.size(), (System.currentTimeMillis() - t) / 1000.0));

            // update index
            System.out.println("Calculated "+(k+curIDs.length)+" of "+blinkIDs.length+" blinks QI");
        }

        System.out.println(String.format("Total Time to calculate QIs %.2f sec.",(System.currentTimeMillis() - t0) / 1000.0));
        System.exit(0);
    }

}

class QIRepository {
    HashMap<Long,ArrayList<QI>> _map = new HashMap<Long,ArrayList<QI>>();
    public QIRepository() {}

    /**
     * Returns null if the qi is new to the repository
     * otherwise returns the already stored QI
     */
    public synchronized QI add(QI qi) {
        long hashCode = qi.getHashCode();
        ArrayList<QI> list = _map.get(hashCode);
        if (list == null) {
            list = new ArrayList<QI>();
            _map.put(hashCode,list);
            list.add(qi);
            return null;
        }
        else {
            for (QI aux: list) {
                if (aux.isEqual(qi))
                    return aux;
            }
            list.add(qi);
            return null;
        }
    }

    /**
     * Contains such quantum invariant?
     */
    public boolean contains(QI qi) {
        long hashCode = qi.getHashCode();
        ArrayList<QI> list = _map.get(hashCode);
        if (list == null) {
            return false;
        }
        else {
            for (QI aux: list) {
                if (aux.isEqual(qi))
                    return true;
            }
            return false;
        }
    }

    /**
     * Get list of not-persistent qi
     */
    public ArrayList<QI> getList() {
        ArrayList<QI> result = new ArrayList<QI>();
        for (ArrayList<QI> list: _map.values()) {
            for (QI qi: list) {
                if (qi.get_id() == QI.NOT_PERSISTENT)
                    result.add(qi);
            }
        }
        return result;
    }
}

