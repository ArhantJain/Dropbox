#include <bits/stdc++.h>
using namespace std;


int main() {

    int n,x;
    vector<int> log;

    cin >> n;
    for (int i=0; i<n; i++) {
        cin >> x;
        log.push_back(x);
    }

    // t[i][j][k], where i represents the day, j represents the previous breakout day, 
    // and k represents the current number of breakouts.
    int t[n+1][n+1][n+1];

    // Initializing the matrix with a big value say: 1001 which can't be possible in any case, bec n <=100
    for(int i=0; i<n; i++){
        for(int j=0; j<=i; j++){
            for(int k=0; k<=n; k++)
                t[i][j][k] = 1001;
        }
    }
    // John started his log on the day of a breakout. Therefore, the first entry will always be zero. 
    // If not, we have to make it zero.
    if(log[0]==0)
        t[0][0][1]=0;
    else
        t[0][0][1]=1;

    // Iterate over the days and possible breakouts to update the table
    for (int day=1; day < n; day++) {
        for (int prevBreakout=0; prevBreakout <= day; prevBreakout++) {
            for (int currentBreakoutCount=1; currentBreakoutCount <= day+1; currentBreakoutCount++) {
                // If the current day is not the last day
                if (prevBreakout < day) {
                    t[day][prevBreakout][currentBreakoutCount] = t[day-1][prevBreakout][currentBreakoutCount];
                }   
                else {
                // If the current day is the last day, iterate over previous days and find the minimum
                    for (int prevDay=0; prevDay < day; prevDay++) {
                        t[day][prevBreakout][currentBreakoutCount] = min(t[day][prevBreakout][currentBreakoutCount], t[day-1][prevDay][currentBreakoutCount-1]);
                    }
                }
            
                // Check if the counter value on the current day is inconsistent with the breakout count
                if (log[day] != day-prevBreakout) {
                        t[day][prevBreakout][currentBreakoutCount]++;
                }
            }
        }
    }

    // Finding the minimum inconsistency for each possible number of breakouts
    for (int currentBreakoutCount=1; currentBreakoutCount <= n; currentBreakoutCount++) {
        int minimumInconsistency = 1001;
        for (int prevBreakout=0; prevBreakout < n; prevBreakout++) {
            minimumInconsistency = min(minimumInconsistency, t[n-1][prevBreakout][currentBreakoutCount]);
        }
        cout << minimumInconsistency << endl;
    }

    return 0;
}
