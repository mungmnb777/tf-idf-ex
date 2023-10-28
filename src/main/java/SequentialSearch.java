import model.DocumentData;
import search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class SequentialSearch {
    public static final String BOOKS_DIRECTORY = "./src/main/resources/books";
    public static final String SEARCH_QUERY_1 = "이름을 가질 수 있다.";

    public static void main(String[] args) throws FileNotFoundException {
        File documentDirectory = new File(BOOKS_DIRECTORY);

        List<String> documents = Arrays.stream(Objects.requireNonNull(documentDirectory.list()))
                .map(documentName -> BOOKS_DIRECTORY + "/" + documentName)
                .toList();

        List<String> terms = TFIDF.getWordsFromLine(SEARCH_QUERY_1);

        findMostRelevantDocuments(documents, terms);
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> terms) throws FileNotFoundException {
        Map<String, DocumentData> documentsResults = new HashMap<>();

        for (String document : documents) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = TFIDF.getWordsFromLines(lines);
            DocumentData documentData = TFIDF.createDocumentData(words, terms);
            documentsResults.put(document, documentData);
        }

        Map<Double, List<String>> documentsByScore = TFIDF.getDocumentsSortedByScore(terms, documentsResults);
        printResults(documentsByScore);
    }

    private static void printResults(Map<Double, List<String>> documentsByScore) {
        for (Map.Entry<Double, List<String>> docScorePair : documentsByScore.entrySet()) {
            Double score = docScorePair.getKey();
            for (String document : docScorePair.getValue()) {
                System.out.println(String.format("Book : %s - score : %f", document.split("/")[5], score));
            }
        }
    }
}
