package agents;

import java.util.ArrayList;
import java.time.LocalDateTime;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.telegramAdapter.TelegramAdapterAPI;
import lombok.*;
import com.mindsmiths.gpt3.GPT3AdapterAPI;
import com.mindsmiths.ruleEngine.util.Log;
import java.util.List;
import java.util.Arrays;
import com.mindsmiths.sdk.utils.Utils;
import models.Personality;

@Getter
@Setter
public class Hobotnica extends Agent {

    public Hobotnica() {
    }
    private Personality personality = Personality.friendlyAI;
    private boolean pinged;
    private List<String> memory = new ArrayList<>();
    private int MAX_MEMORY = 6;
    private LocalDateTime lastInteractionTime;

    public void changePersonality(){
        resetMemory();
        List<Personality> choices = new ArrayList<Personality>(
                Arrays.asList(Personality.values())
        );
        choices.remove(personality);
        personality = Utils.randomChoice(choices);
    }

    public void addInstruction(String text){
        memory.add(text + "\n");
        trimMemory();
    }

    private void trimMemory() {
        if (memory.size() > MAX_MEMORY + 1)
            memory = memory.subList(memory.size() - 1 - MAX_MEMORY, memory.size());
    }

    public void resetMemory() {
        memory.clear();
    }
    public void addMessageToMemory(String sender, String text) {
        memory.add(String.format("%s: %s\n", sender, text));
        trimMemory();
    }

    public void sendMessage(String text) {
        String chatId = getConnection("telegram");
        TelegramAdapterAPI.sendMessage(chatId, text);
    }

    public void askGPT3() {
        simpleGPT3Request(
            personality.getInstruction() + String.join("\n", memory) + personality.getAiName() + ":", personality.getTemp(),
            personality.getResponseLen(), List.of(personality.getAiName() + ":", personality.getHumanName() + ":")
        );
    }
    public void simpleGPT3Request(String prompt, Double temp, Integer responseLen, List<String> stop) {
        Log.info("Prompt for GPT-3:\n" + prompt);
        GPT3AdapterAPI.complete(
            prompt,
            "text-davinci-001",
            responseLen,
            temp,
            1.0,
            1,
            null,
            false,
            stop,
            0.6,
            0.0,
            1,
            null
            );
    }
}