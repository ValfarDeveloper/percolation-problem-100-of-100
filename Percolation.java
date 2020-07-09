import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.Scanner;

public class Percolation {
    private final boolean[] openSites;
    private final WeightedQuickUnionUF sites;
    private final WeightedQuickUnionUF sitesWithoutBottomVN;
    private final int topVirtualSite;
    private final int bottomVirtualSite;
    private final int n;
    private int numberOpenSites;

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

    private void makeUnionWithAdjacentSite(int siteIndex, int adjacentSite, WeightedQuickUnionUF dataType) {
        /** Check if the site is possible and if is open then connect to it */
        if (adjacentSite != -1 && openSites[adjacentSite]) {
            if (dataType == sites || (dataType == sitesWithoutBottomVN && adjacentSite != bottomVirtualSite)) {
                dataType.union(siteIndex, adjacentSite);
            }
        }
    }

    private void connectWithAdjacentSites(int siteIndex, WeightedQuickUnionUF dataType) {
        /** If the site is on the top row, the top adjacent site is the top virtual site */
        int topSite = siteIndex > n - 1 ? siteIndex - n : topVirtualSite;
        /** If the site is on the last column, it doesn't have a right adjacent site = -1 */
        int rightSite = siteIndex % n == n-1 ? -1 : siteIndex + 1;
        /** If the site is on the bottom row, the bottom adjacent site is the bottom virtual site*/
        int bottomSite = siteIndex < (n*n - n) ? siteIndex + n : bottomVirtualSite;
        /** If the site is on the first column, it doesn't have a left adjacent site = -1 */
        int leftSite = siteIndex % n == 0 ? -1 : siteIndex - 1;

        this.makeUnionWithAdjacentSite(siteIndex, topSite, dataType);
        this.makeUnionWithAdjacentSite(siteIndex, rightSite, dataType);
        this.makeUnionWithAdjacentSite(siteIndex, bottomSite, dataType);
        this.makeUnionWithAdjacentSite(siteIndex, leftSite, dataType);
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
             * We open the corresponding site for check backwash in isFull operation
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

    /**
     * Return a graphic representation of the system
     */
    public String toString(){
        String grid = "";

        for(int i = 0 ; i < n*n ; i ++){
            if(i % n == 0){
                grid += "\n";
            }

            grid += openSites[i] ? " □ " : " ■ ";
        }

        return grid;
    }

    // test client (optional)
    public static void main(String[] args){
        Scanner keyboard = new Scanner(System.in);
        int operation = 0;

        System.out.println("WELCOME TO THE PERCOLATION ALGORITHM");
        System.out.println("Enter the n such that n*n represents the sites grid:");

        int n = keyboard.nextInt();
        Percolation percolation = new Percolation(n);


        do {
            System.out.println("-----------------------------------------------------"
                            + "\n What operation you want to run?: "
                            + "\n 1. open"
                            + "\n 2. isOpen"
                            + "\n 3. isFull"
                            + "\n 4. numberOfOpenSites"
                            + "\n 5. percolates?"
                            + "\n introduce any other value to Exit \n");

            operation = keyboard.nextInt();

            switch(operation) {
                case 1: 
                    {
                        System.out.println("\nIntroduce the row and column for open the site. ");
                        System.out.println("Row: ");
                        int row = keyboard.nextInt();
                        System.out.println("Column: ");
                        int col = keyboard.nextInt();

                        percolation.open(row, col);
                        System.out.println("==> The site was opened. The new grid is: " + percolation.toString());
                    }
                    break;
                case 2:
                    {
                        System.out.println("\nIntroduce the row and column for check if the site is open. ");
                        System.out.println("Row: ");
                        int row = keyboard.nextInt();
                        System.out.println("Column: ");
                        int col = keyboard.nextInt();

                        boolean isOpen = percolation.isOpen(row, col);

                        System.out.println("==> The site is " + (isOpen ? "open." : "closed."));
                    }
                    break;
                case 3:
                    {
                        System.out.println("\nIntroduce the row and column for check if the site a full site. ");
                        System.out.println("Row: ");
                        int row = keyboard.nextInt();
                        System.out.println("Column: ");
                        int col = keyboard.nextInt();

                        boolean isFullSite = percolation.isFull(row, col);

                        System.out.println("==> The site " + (isFullSite ? "is full site." : "is not full site."));
                    }
                    break;
                case 4:
                    {
                        int openSites = percolation.numberOfOpenSites();

                        System.out.println("==> The number of open sites is: " + openSites);
                    }
                    break;
                case 5:
                    {
                        boolean percolates = percolation.percolates();

                        System.out.println("==> The system " + (percolates ? "percolate." : "not percolate.") + percolation.toString());
                    }
                    break;
            }
        }while(operation >= 1 && operation <= 5);
    }
}