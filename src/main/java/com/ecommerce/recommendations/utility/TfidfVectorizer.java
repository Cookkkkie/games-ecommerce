package com.ecommerce.recommendations.utility;

import java.util.*;
import java.util.stream.Collectors;

public class TfidfVectorizer {

    /** token → column index */
    private final Map<String, Integer> vocab = new HashMap<>();
    /** game index → sparse term-tf map */
    private final List<Map<Integer, Double>> tf = new ArrayList<>();
    /** idf vector (size = |vocab|) */
    private double[] idf;
    /** cached L2 norms of TF-IDF rows */
    private double[] norms;

    public TfidfVectorizer(List<String> documents) {
        build(documents);
    }

    private void build(List<String> docs) {
        int N = docs.size();
        List<Map<Integer, Double>> termCounts = new ArrayList<>();
        int row = 0;

        // pass 1 ─ build vocab & term counts
        for (String doc : docs) {
            Map<Integer, Double> counts = new HashMap<>();
            for (String raw : doc.split("\\s+")) {
                if (raw.isBlank()) continue;
                String tok = raw.trim();
                int col = vocab.computeIfAbsent(tok, t -> vocab.size());
                counts.merge(col, 1.0, Double::sum);
            }
            termCounts.add(counts);
            row++;
        }

        // document frequency
        double[] df = new double[vocab.size()];
        for (Map<Integer, Double> counts : termCounts) {
            counts.keySet().forEach(col -> df[col]++);
        }

        // idf
        idf = new double[vocab.size()];
        for (int i = 0; i < idf.length; i++) {
            idf[i] = Math.log1p(N / (df[i] + 1.0));   // log(1 + N/(df+1))
        }

        // tf-idf + norms
        norms = new double[N];
        for (int i = 0; i < N; i++) {
            Map<Integer, Double> rowCounts = termCounts.get(i);
            double sumSq = 0.0;
            int tokens = rowCounts.values().stream().mapToInt(Double::intValue).sum();

            Map<Integer, Double> tfRow = new HashMap<>();
            for (Map.Entry<Integer, Double> e : rowCounts.entrySet()) {
                double tfVal = e.getValue() / tokens;       // term freq
                double tfidf = tfVal * idf[e.getKey()];
                tfRow.put(e.getKey(), tfidf);
                sumSq += tfidf * tfidf;
            }
            tf.add(tfRow);
            norms[i] = Math.sqrt(sumSq);
        }
    }

    /** cosine similarity between rows i & j */
    public double similarity(int i, int j) {
        Map<Integer, Double> a = tf.get(i);
        Map<Integer, Double> b = tf.get(j);
        // iterate over smaller map
        if (a.size() > b.size()) { Map<Integer, Double> tmp = a; a = b; b = tmp; }

        double dot = 0.0;
        for (Map.Entry<Integer, Double> e : a.entrySet()) {
            dot += e.getValue() * b.getOrDefault(e.getKey(), 0.0);
        }
        return dot == 0 ? 0 : dot / (norms[i] * norms[j]);
    }

    /** top-k most similar rows to row idx (excluding itself) */
    public List<IdxScore> topK(int idx, int k) {
        PriorityQueue<IdxScore> pq = new PriorityQueue<>(Comparator.comparingDouble(s -> s.score));
        for (int j = 0; j < tf.size(); j++) {
            if (j == idx) continue;
            double sim = similarity(idx, j);
            if (pq.size() < k) {
                pq.offer(new IdxScore(j, sim));
            } else if (sim > pq.peek().score) {
                pq.poll(); pq.offer(new IdxScore(j, sim));
            }
        }
        return pq.stream()
                .sorted((a,b) -> Double.compare(b.score, a.score))
                .collect(Collectors.toList());
    }

    public record IdxScore(int index, double score) {}
}
