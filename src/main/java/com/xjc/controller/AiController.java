package com.xjc.controller;

import cn.hutool.core.text.UnicodeUtil;
import com.alibaba.fastjson.JSON;
import com.xjc.pojo.Answer;
import com.xjc.pojo.Choices;
import com.xjc.properties.OpenAi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@CrossOrigin
@ConfigurationPropertiesScan(basePackages = "com.xjc.properties")
public class AiController {

    @Autowired
    private OpenAi openAi;


    @Value("${openai.secretKey}")
    private String secretKey;

    @GetMapping(value = "/request",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> request(@RequestParam String question) {
        return submit( question);
    }

    private Flux<String> submit( @RequestParam String question) {

        openAi.setPrompt(question);

        WebClient webClient = WebClient.create();

        WebClient.ResponseSpec authorization = webClient.post()
                .uri("https://api.openai.com/v1/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + secretKey)
                .bodyValue(JSON.toJSONString(openAi))
                .retrieve();

        Flux<String> stringFlux = authorization.bodyToFlux(String.class);

        return stringFlux.mapNotNull(string -> {

            if (string.contains("DONE")) {
                return "Answer Done!";
            }

            Answer answer = JSON.parseObject(string, Answer.class);

            StringBuilder stringBuilder = new StringBuilder();

            List<Choices> choices = answer.getChoices();

            choices.forEach(choice -> {
                stringBuilder.append(UnicodeUtil.toString(choice.getText()));
            });

            StringBuilder s = new StringBuilder(stringBuilder.toString());

            //后端的换行符改为前端的换行符
            if (s.toString().contains("\n")) {
                s = new StringBuilder("<br/>");
            }

            //后端的空格符改为前端的空格符
            return s.toString().replace(" ", "&ensp;");

        }).cache();
    }

}
