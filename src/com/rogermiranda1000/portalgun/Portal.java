package com.rogermiranda1000.portalgun;

public class Portal {
    public double[][] loc = new double[2][3];
    public String[] world = {"", ""};
    public char[] dir = {' ', ' '};
    int task = 0;
    public boolean down[] = {false, false};

    public Portal() {}
    public Portal(String[] l1, String[] l2) {
        for(int a = 0; a<2; a++) {
            String[] l = l2;
            if(a==0) l = l1;

            this.world[a] = l[0];
            for(int b = 0; b<3; b++) this.loc[a][b] = Double.parseDouble(l[b+1]);
            this.dir[a] = l[4].charAt(0);
            this.down[a] = Boolean.valueOf(l[5]);
        }
    }

    public String Save() {
        String msg = "";
        for (int a = 0; a<2; a++) {
            msg+=world[a]+",";
            for(int b = 0; b<3; b++) msg+=loc[a][b]+",";
            msg+=dir[a]+","+down[a];
            if(a==0) msg+=">";
        }
        return msg;
    }

    public int contains(double loc[], String mundo) {
        for(int a = 0; a<2; a++) {
            if((this.loc[a][0] == loc[0] && this.loc[a][1] == loc[1] && this.loc[a][2] == loc[2] && this.world[a].equalsIgnoreCase(mundo)) ||
            (down[a] && correction(this.loc[a], dir[a])[0] == loc[0] && correction(this.loc[a], dir[a])[1] == loc[1] && correction(this.loc[a], dir[a])[2] == loc[2]
                    && this.world[a].equalsIgnoreCase(mundo))) return a;
        }

        return -1;
    }

    private double[] correction(double[] l, char d) {
        double[] n = l.clone();
        if (d == 'N') n[0] -= 1.0D;
        else if (d == 'S') n[0] += 1.0D;
        else if (d == 'E') n[2] -= 1.0D;
        else if (d == 'W') n[2] += 1.0D;
        return n;
    }
}
