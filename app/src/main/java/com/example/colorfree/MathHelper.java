package com.example.colorfree;

import java.util.Random;

public class MathHelper {
    public static class Problem {
        public String question;
        public int answer;

        public Problem(String q, int a) {
            this.question = q;
            this.answer = a;
        }
    }

    public static Problem generateProblem() {
        Random r = new Random();
        int a = r.nextInt(90) + 10; // 10-99
        int b = r.nextInt(90) + 10; // 10-99
        
        // simple addition for now, can be expanded
        return new Problem(a + " + " + b + " = ?", a + b);
    }
}
