package com.kenzie.app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.Scanner;

/** STEPS **/
//        TODO: Step One - GET the questions
//        Make GET request for https://jservice.kenzie.academy/api/clues to return a list of questions.

//        TODO: Step Two - Present a single question to the user
//        "Category:  "
//        "Question:  "

//        The GET call will return the questions in the same order each time.
//        In order to improve gameplay, you will need to employ logic to randomize the questions that are selected.

//        TODO: Step Three - Allow the user to respond to the question
//        "Your Answer:  "

//        Create some way for a user to input an answer to whatever they Receive.
//        Response should be a STRING, even if the answer contains numbers.

//        TODO: Step Four - Determine if the user's answer was correct
//        & TODO: Step Five - Keep track of the user's score
//        Capitalization should not matter
//        Correct answers = +1 point, continue
//        Incorrect answers = no change, display indicator and correct answer, continue

//        TODO: Step Six - End of program
//        After the 10 questions have been asked and answered, display the total score and then exit the program.

class Main {
    private static final String url = "https://jservice.kenzie.academy/api/clues";

    public static String makeGETRequest(String url) {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = httpResponse.statusCode();
            if (statusCode == 200) {
                return httpResponse.body();
            } else {
                return String.format("GET request failed: %d status code received %s", statusCode, httpResponse.body());
            }
        } catch (IOException | InterruptedException e) {
            return e.getMessage();
        }
    }

    public static String finalResults(int points) {
        String results = "";
        if (points >= 0 && points <= 6) {
            results = "Better luck next time! Your final score was: " + points;
        } else if (points == 7) {
            results = "Not Bad! Your final score was: " + points;
        } else if (points == 8) {
            results = "Nice! Your final score was: " + points;
        } else if (points == 9) {
            results = "Amazing! Your final score was: " + points;
        } else if (points == 10) {
            results = "Perfect! Your final score was: " + points;
        }
        return results;
    }

    public class EmptyInputException extends IllegalArgumentException {
        public EmptyInputException(String input) {
            super("No answer was given!");
        }
    }

    public static void main(String[] args){

        try {
            int totalPoints = 0;
            Random random = new Random();
            Scanner scanner = new Scanner(System.in);

            ObjectMapper mapper = new ObjectMapper();
            QuestionsListDTO questionList = mapper.readValue(makeGETRequest(Main.url), QuestionsListDTO.class);

            System.out.println("Welcome to a quick trivia game! You will be given ten (10) \"Jeopardy!\"-styled clues " +
                    "and it is up to you to answer what it refers to. Type in your answer to the given clue below.");
            System.out.println();

            for (int i = 1; i <= 10; i++) {

                int randomQuestionNumber = random.nextInt(100);
                String displayCategory = questionList.getClues().get(randomQuestionNumber).getCategory().getTitle();
                String displayQuestion = questionList.getClues().get(randomQuestionNumber).getQuestion();
                String displayAnswer = questionList.getClues().get(randomQuestionNumber).getAnswer();

                // Initial Display
                System.out.println("Score: " + totalPoints);
                System.out.println("Category: " + displayCategory);
                System.out.println("Question " + i + ": " + displayQuestion);
                System.out.print("Your answer: ");

                // User Inputs answer
                String input = scanner.nextLine();

                // Result
                if (input.equalsIgnoreCase(displayAnswer) && !input.equals("")) {
                    System.out.println();
                    System.out.println("Correct! Next question...");
                    System.out.println();
                    totalPoints++;

                } else {
                    System.out.println();
                    System.out.print("Incorrect answer! The correct answer is: ");
                    System.out.println(displayAnswer + '.');
                    System.out.println("Next question...");
                    System.out.println();
                }


            }

            System.out.println(Main.finalResults(totalPoints));


        } catch (Exception e) {
            System.out.println("Unexpected Exception:");
            e.getMessage();
            e.printStackTrace();
        }
    }

}

