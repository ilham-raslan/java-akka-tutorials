package com.ilham.github;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class FirstSimpleBehavior extends AbstractBehavior<String> {

    private FirstSimpleBehavior(ActorContext<String> context) {
        super(context);
    }

    public static Behavior<String> create() {
        return Behaviors.setup(FirstSimpleBehavior::new);
    }

    public Receive<String> createReceive() {
        return this.newReceiveBuilder()
                .onMessageEquals("say hello", () -> {
                    System.out.println("Hello");
                    return this;
                })
                .onMessageEquals("who are you", () -> {
                    System.out.printf("My path is %s%n", this.getContext().getSelf().path());
                    return this;
                })
                .onMessageEquals("create a child", () -> {
                    ActorRef<String> secondActor = this.getContext().spawn(FirstSimpleBehavior.create(), "secondActor");
                    secondActor.tell("who are you");
                    return this;
                })
                .onAnyMessage(message -> {
                    System.out.printf("I received the message: %s%n", message);
                    return this;
                })
                .build();
    }
}
