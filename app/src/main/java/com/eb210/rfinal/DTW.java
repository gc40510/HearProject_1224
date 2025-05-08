package com.eb210.rfinal;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTW {
    DTW(Context context){
        this.context = context;
    }
    Context context;
    List<List<String>> parper_check_dict() throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream in = assetManager.open("extra_questions.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        List<String> first_sound = new ArrayList<String>();
        List<String> final_sound = new ArrayList<String>();
        line = br.readLine();
        for(int i=0;i<5;i++) {
            line = br.readLine().replace('\n', '\0');
            for(String str:line.split(" ")) {
                final_sound.add(str);
            }
        }
        line = br.readLine().replace('\n', '\0');
        for(String str:line.split(" ")) {
            first_sound.add(str);
        }
        List<List<String>> ans = new ArrayList<List<String>>();
        ans.add(first_sound);
        ans.add(final_sound);
        return ans;

    }
    boolean check_is_equal(String a, String b) {
        List<String> first_sound = null;
        List<String> final_sound = null;
        try {
            List<List<String>> dict = parper_check_dict();
            first_sound = dict.get(0);
            final_sound = dict.get(1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(first_sound.indexOf(a)!=-1) {
            if(first_sound.indexOf(b)!=-1) {
                return true;
            }else {
                return false;
            }
        }else {
            if(final_sound.indexOf(b)!=-1) {
                return true;
            }else {
                return false;
            }
        }
    }
    int find_min(int a, int b, int c) {
        int min = a;
        if(min > b) {
            min = b;
        }
        if(min > c) {
            min = c;
        }
        return min;
    }
    int[][] dtw_algo(String[] target, String[] refrence) {
        int n = target.length;
        int m = refrence.length;
        int[][] d = new int[n][m];
        int[][] a = new int[n][m];
        for(int i=0;i<n;i++) {
            for(int j=0;j<m;j++) {
                if(target[i] == refrence[j]) {
                    d[i][j] = 0;
                }else {
                    if(check_is_equal(target[i], refrence[j])) {
                        d[i][j] = 1;
                    }else {
                        d[i][j] = 2;
                    }

                }
            }
        }
        for(int i=0;i<n;i++) {
            for(int j=0;j<m;j++) {
                if(i==0 && j==0) {
                    a[0][0] = d[0][0];
                }else if(i==0) {
                    a[0][j] = a[0][j-1] + d[i][j];
                }else if(j==0) {
                    a[i][0] = a[i-1][0] + d[i][j];
                }else {
                    a[i][j] = find_min((a[i-1][j] + d[i][j]), (a[i-1][j-1] + d[i][j]), (a[i][j-1] + d[i][j]));
                }
            }
        }
        return a;
    }
    Map<String, List<String>> dtw(String[] target, String[] refrence){
        int[][] a = dtw_algo(target, refrence);
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for(int i=0;i<target.length;i++) {
            map.put(target[i], new ArrayList<String>());
        }

        int next_x = target.length-1;
        int next_y = refrence.length-1;
        int last_x = 0;
        int last_y = 0;
        int min_x;
        int min_y;
        Map<String, Integer> state_count = new HashMap<String, Integer>();
        state_count.put("insertion", 0);
        state_count.put("deletion", 0);
        state_count.put("normal", 0);

        String state = "normal";
        int substition = 0;
        while(next_x!=0 || next_y!=0) {
            last_x = next_x;
            last_y = next_y;
            state = "normal";
            if(next_x!=0) {
                min_x = next_x-1;
            }else {
                min_x = 0;
                state = "insertion";
            }

            if(next_y!=0) {
                min_y = next_y-1;
            }else {
                min_y = 0;
                state = "deletion";
            }

            if(a[min_x][min_y] > a[min_x][next_y]) {
                min_y = next_y;
                state = "deletion";
            }else if(a[min_x][min_y] > a[next_x][min_y]) {
                min_x = next_x;
                state = "insertion";
            }

            next_x = min_x;
            next_y = min_y;
            state_count.put(state, state_count.get(state)+1);
            if(state == "normal") {
                if(target[last_x] != refrence[last_y]) {
                    substition += 1;
                }
//				System.out.println(target[last_x]+"---->"+refrence[last_y]);
                map.get(target[last_x]).add(refrence[last_y]);

            }
//			System.out.println(state);

        }
        if(state == "normal") {
            if(target[0] != refrence[0]) {
                substition += 1;
            }
//			System.out.println(target[0]+"---->"+refrence[0]);

        }
        map.get(target[0]).add(refrence[0]);
//		System.out.println(map.size());

        for (String key : map.keySet()) {
            Log.v("read txt ", key + " -> " + map.get(key));
        }
//        System.out.println();

        return map;
    }
}
