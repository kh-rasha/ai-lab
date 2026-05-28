package se.rasha.ai_lab;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AiResponseDto {

    @NotBlank
    private String sentiment;

    @Min(0)
    @Max(100)
    private int score;

    public AiResponseDto() {
    }

    public AiResponseDto(String sentiment, int score) {
        this.sentiment = sentiment;
        this.score = score;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "AiResponseDto{" +
                "sentiment='" + sentiment + '\'' +
                ", score=" + score +
                '}';
    }
}
