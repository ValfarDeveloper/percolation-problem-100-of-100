import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final boolean[] openSites;
    private final WeightedQuickUnionUF sites;
    private final WeightedQuickUnionUF sitesWithoutBottomVN;
    private final int topVirtualSite;
    private final int bottomVirtualSite;
    private final int n;
    private int numberOpenSites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int sideNumber) {
        if (sideNumber <= 0) {
            throw new IllegalArgumentException("n must match n >= 1");
        }

        int gridSize = sideNumber*sideNumber;
        /**
         * We add the extra nodes for top and bottom
         * Specifically those nodes will be n*n and n*n + 1
         * */
        int numberSites = gridSize + 2;

        /**
         * We initialice the array with the double number sites because:
         * from 0 to numbersites-1 will gridSizebe the sites connected to virtual top and virtual bottom nodes
         * from numbersites to numbersites*2 - 1 will be the sites only with the virtual top node, this is for avoid backwash
         */
        sites = new WeightedQuickUnionUF(numberSites);
        sitesWithoutBottomVN = new WeightedQuickUnionUF(numberSites);
        openSites = new boolean[numberSites];
        n = sideNumber;
        numberOpenSites = 0;
        /** The last two nodes will be for the virtual sites */
        topVirtualSite = gridSize;
        bottomVirtualSite = gridSize + 1;
        /** We mark as open the virtual nodes */
        openSites[topVirtualSite] = true;
        openSites[bottomVirtualSite] = true;
    }

    private int getSiteIndex(int row, int col) {
        return (row - 1)*n + (col - 1);
    }

    private boolean makeUnionWithAdjacentSite(int siteIndex, int adjacentSite, WeightedQuickUnionUF dataType) {
        /** Check if the site is possible and if is open then connect to it */
        if (adjacentSite != -1 && openSites[adjacentSite]) {
            if (dataType == sites || (dataType == sitesWithoutBottomVN && adjacentSite != bottomVirtualSite)) {
                dataType.union(siteIndex, adjacentSite);
            }

            return true;
        }
        
        return false;
    }

    private void connectWithAdjacentSites(int siteIndex, WeightedQuickUnionUF dataType) {
        boolean isOpen = false;

        /** If the site is on the top row, the top adjacent site is the top virtual site */
        int topSite = siteIndex > n - 1 ? siteIndex - n : topVirtualSite;
        /** If the site is on the last column, it doesn't have a right adjacent site = -1 */
        int rightSite = siteIndex % n == n-1 ? -1 : siteIndex + 1;
        /** If the site is on the bottom row, the bottom adjacent site is the bottom virtual site*/
        int bottomSite = siteIndex < (n*n - n) ? siteIndex + n : bottomVirtualSite;
        /** If the site is on the first column, it doesn't have a left adjacent site = -1 */
        int leftSite = siteIndex % n == 0 ? -1 : siteIndex - 1;

        isOpen = isOpen | this.makeUnionWithAdjacentSite(siteIndex, topSite, dataType);
        isOpen = isOpen | this.makeUnionWithAdjacentSite(siteIndex, rightSite, dataType);
        isOpen = isOpen | this.makeUnionWithAdjacentSite(siteIndex, bottomSite, dataType);
        isOpen = isOpen | this.makeUnionWithAdjacentSite(siteIndex, leftSite, dataType);
    }

    private void validateRowAndCol(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException("The input for row and col must be between 1 and n inclusive.");
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        this.validateRowAndCol(row, col);
        if (!this.isOpen(row, col)) {
            int siteIndex = this.getSiteIndex(row, col);
            
            this.connectWithAdjacentSites(siteIndex, sites);
            /**
             * We open the corresponding site for check backwash
             */
            this.connectWithAdjacentSites(siteIndex, sitesWithoutBottomVN);

            /** We mark the site as open */
            openSites[siteIndex] = true;

            numberOpenSites++;
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        this.validateRowAndCol(row, col);
        int siteIndex = this.getSiteIndex(row, col);

        return openSites[siteIndex];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        this.validateRowAndCol(row, col);
        int siteIndex = this.getSiteIndex(row, col);

        return sitesWithoutBottomVN.find(siteIndex) == sitesWithoutBottomVN.find(topVirtualSite);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numberOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return sites.find(topVirtualSite) == sites.find(bottomVirtualSite);
    }
}