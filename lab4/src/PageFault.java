/* It is in this file, specifically the replacePage function that will
   be called by MemoryManagement when there is a page fault.  The 
   users of this program should rewrite PageFault to implement the 
   page replacement algorithm.
*/

// This PageFault file is an example of the FIFO Page Replacement
// Algorithm as described in the Memory Management section.

import java.util.*;

public class PageFault {

    /**
     * The page replacement algorithm for the memory management sumulator.
     * This method gets called whenever a page needs to be replaced.
     * <p>
     * This is an implementation of the Least Recently Used (LRU) algorithm.
     * <pre>
     *   Page page = ( Page ) mem.elementAt( oldestPage )
     * </pre>
     * This line brings the contents of the Page at oldestPage (a
     * specified integer) from the mem vector into the page object.
     * Next recall the contents of the target page, replacePageNum.
     * Set the physical memory address of the page to be added equal
     * to the page to be removed.
     *
     * @param mem            is the vector which contains the contents of the pages
     *                       in memory being simulated.  mem should be searched to find the
     *                       proper page to remove, and modified to reflect any changes.
     * @param virtPageNum    is the number of virtual pages in the
     *                       simulator (set in Kernel.java).
     * @param replacePageNum is the requested page which caused the
     *                       page fault.
     * @return               the page which got swapped out
     */
    public static int replacePage(Vector<Page> mem, int virtPageNum, int replacePageNum) {

        Page lruPage = null;
        int lruTouchTime = 0;
        
        for (int i = 0; i < virtPageNum; i++) {
            Page page = mem.elementAt(i);
            
            if (page.physical != -1) {
                if (page.lastTouchTime >= lruTouchTime) {
                    lruPage = page;
                    lruTouchTime = page.lastTouchTime;
                }
            }
        }

        mem.elementAt(replacePageNum).physical = lruPage.physical;
        return lruPage.id;
    }
}
