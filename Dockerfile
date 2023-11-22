FROM java:17
MAINTAINER cmumnmn
ADD demo/chat-bot-1.0.jar chat-bot.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","chat-bot.jar"]