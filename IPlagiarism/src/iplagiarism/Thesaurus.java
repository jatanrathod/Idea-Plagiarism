package iplagiarism;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Thesaurus {

    ArrayList<String> wordList;
    HashMap<String, ArrayList<String>> synonyms;

    public Thesaurus(String[] words) {
        this.synonyms = new HashMap<>();
        this.wordList = new ArrayList<>();
        for (String word : words) {
            wordList.add(word);
        }
    }

    public Thesaurus(String word) {
        this.synonyms = new HashMap<>();
        this.wordList = new ArrayList<>();

        wordList.add(word);
    }

    public HashMap<String, ArrayList<String>> getSynonyms() throws IOException, InterruptedException {
        if (synonyms.isEmpty()) {
            for (String word : wordList) {
                try {
                    Document doc = Jsoup.connect("http://www.thesaurus.com/browse/" + word)
                            .userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0")
                            .timeout(3000)
                            .get();
                    Elements block = doc.getElementsByClass("relevancy-block");
                    Elements list = block.select(".relevancy-list");
                    Elements text = list.select(".text");
                    ArrayList<String> synonymList = new ArrayList<>();
                    for (int j = 0; j < text.size(); j++) {
                        synonymList.add(text.get(j).text());
                    }
                    synonyms.put(word, synonymList);
                } catch (IOException e) {
                }
            }
        }
        return synonyms;
    }
}
