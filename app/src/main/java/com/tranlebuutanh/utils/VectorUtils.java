package com.tranlebuutanh.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VectorUtils {

    // Remove Vietnamese diacritics → base ASCII so "quan ly" matches "quản lý"
    public static String removeDiacritics(String text) {
        if (text == null) return "";
        return text
            .replace("à","a").replace("á","a").replace("ả","a").replace("ã","a").replace("ạ","a")
            .replace("ă","a").replace("ắ","a").replace("ặ","a").replace("ằ","a").replace("ẳ","a").replace("ẵ","a")
            .replace("â","a").replace("ấ","a").replace("ầ","a").replace("ẩ","a").replace("ẫ","a").replace("ậ","a")
            .replace("è","e").replace("é","e").replace("ẻ","e").replace("ẽ","e").replace("ẹ","e")
            .replace("ê","e").replace("ế","e").replace("ề","e").replace("ể","e").replace("ễ","e").replace("ệ","e")
            .replace("ì","i").replace("í","i").replace("ỉ","i").replace("ĩ","i").replace("ị","i")
            .replace("ò","o").replace("ó","o").replace("ỏ","o").replace("õ","o").replace("ọ","o")
            .replace("ô","o").replace("ố","o").replace("ồ","o").replace("ổ","o").replace("ỗ","o").replace("ộ","o")
            .replace("ơ","o").replace("ớ","o").replace("ờ","o").replace("ở","o").replace("ỡ","o").replace("ợ","o")
            .replace("ù","u").replace("ú","u").replace("ủ","u").replace("ũ","u").replace("ụ","u")
            .replace("ư","u").replace("ứ","u").replace("ừ","u").replace("ử","u").replace("ữ","u").replace("ự","u")
            .replace("ỳ","y").replace("ý","y").replace("ỷ","y").replace("ỹ","y").replace("ỵ","y")
            .replace("đ","d");
    }

    // Tokenize: lowercase + remove diacritics + remove punctuation + split
    public static String[] tokenize(String text) {
        if (text == null || text.isEmpty()) return new String[0];
        String normalized = removeDiacritics(text.toLowerCase())
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return normalized.split(" ");
    }

    // Build vocabulary from all documents
    public static Map<String, Integer> buildVocabulary(String[] documents) {
        Map<String, Integer> vocab = new HashMap<>();
        int idx = 0;
        for (String doc : documents) {
            for (String token : tokenize(doc)) {
                if (!token.isEmpty() && !vocab.containsKey(token)) {
                    vocab.put(token, idx++);
                }
            }
        }
        return vocab;
    }

    // TF vector of a document given vocabulary
    public static double[] vectorize(String doc, Map<String, Integer> vocab) {
        double[] vec = new double[vocab.size()];
        String[] tokens = tokenize(doc);
        for (String token : tokens) {
            Integer i = vocab.get(token);
            if (i != null) vec[i] += 1.0;
        }
        return vec;
    }

    // Cosine similarity between two vectors
    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot   += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Euclidean distance (smaller = more similar)
    public static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return Math.sqrt(sum);
    }

    // Jaccard similarity on token sets
    public static double jaccardSimilarity(String docA, String docB) {
        String[] tokA = tokenize(docA);
        String[] tokB = tokenize(docB);
        Map<String, Boolean> setA = new HashMap<>();
        for (String t : tokA) if (!t.isEmpty()) setA.put(t, true);
        Map<String, Boolean> setB = new HashMap<>();
        for (String t : tokB) if (!t.isEmpty()) setB.put(t, true);

        int intersection = 0;
        for (String k : setA.keySet()) if (setB.containsKey(k)) intersection++;
        int union = setA.size() + setB.size() - intersection;
        return union == 0 ? 0 : (double) intersection / union;
    }

    // Find best matching index using cosine similarity (returns index of best match)
    public static int findBestMatch(String query, String[] documents) {
        Map<String, Integer> vocab = buildVocabulary(documents);
        double[] queryVec = vectorize(query, vocab);

        int bestIdx = 0;
        double bestScore = -1;
        for (int i = 0; i < documents.length; i++) {
            double[] docVec = vectorize(documents[i], vocab);
            double score = cosineSimilarity(queryVec, docVec);
            if (score > bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    // Return all similarity scores
    public static double[] allScores(String query, String[] documents) {
        Map<String, Integer> vocab = buildVocabulary(documents);
        double[] queryVec = vectorize(query, vocab);
        double[] scores = new double[documents.length];
        for (int i = 0; i < documents.length; i++) {
            double[] docVec = vectorize(documents[i], vocab);
            scores[i] = cosineSimilarity(queryVec, docVec);
        }
        return scores;
    }
}
