package com.ndev.privchat.privchat.service.nickname;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class NicknameService {

    @Value("classpath:adjectives.csv")
    private Resource adjectivesResource;

    @Value("classpath:nouns.csv")
    private Resource nounsResource;

    private final Random random = new Random();

    public String generateNickname() throws IOException {
        List<String> adjectives = loadWordsFromCsv(adjectivesResource);
        List<String> nouns = loadWordsFromCsv(nounsResource);

        String firstWord = getRandomWord(adjectives);
        String secondWord = getRandomWord(nouns);

        String numbers = String.format("%03d", random.nextInt(1000));

        return firstWord + secondWord + numbers;
    }

    private List<String> loadWordsFromCsv(Resource resource) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("adjective") || line.startsWith("noun")) {
                    continue;
                }
                words.add(line.trim().toLowerCase());
            }
        }
        return words;
    }

    private String getRandomWord(List<String> wordList) {
        int randomIndex = random.nextInt(wordList.size());
        return wordList.get(randomIndex);
    }
}
